package usecases

import (
	"authorization/domain"
	"errors"
	"testing"

	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/mock"
)

type MockSaveTransactionsRepository struct {
	mock.Mock
}

func (m *MockSaveTransactionsRepository) Save(input domain.TransactionInput) error {
	args := m.Called(input)
	return args.Error(0)
}

type MockSetFailureTransactionRepository struct {
	mock.Mock
}

func (m *MockSetFailureTransactionRepository) SetFailure(id int64) error {
	args := m.Called(id)
	return args.Error(0)
}

type MockSetSuccessTransactionRepository struct {
	mock.Mock
}

func (m *MockSetSuccessTransactionRepository) SetSuccess(id int64) error {
	args := m.Called(id)
	return args.Error(0)
}

type MockHttpClient struct {
	mock.Mock
}

func (m *MockHttpClient) Get(url string) error {
	args := m.Called(url)
	return args.Error(0)
}

func makeInput() domain.TransactionInput {
	return domain.TransactionInput{}
}

func makeSut(
	t *testing.T,
	saveError error,
	httpError error,
) (
	DbAuthorizeTransactionUsecase,
	*MockSaveTransactionsRepository,
	*MockSetFailureTransactionRepository,
	*MockSetSuccessTransactionRepository,
	*MockHttpClient,
) {
	t.Helper()

	mockSaveRepository := new(MockSaveTransactionsRepository)
	mockSetFailureTransactionRepository := new(MockSetFailureTransactionRepository)
	mockSetSuccessTransactionRepository := new(MockSetSuccessTransactionRepository)
	httpClient := new(MockHttpClient)

	mockSaveRepository.On("Save", mock.Anything).Return(saveError)
	mockSetFailureTransactionRepository.On("SetFailure", mock.Anything).Return(nil)
	mockSetSuccessTransactionRepository.On("SetSuccess", mock.Anything).Return(nil)
	httpClient.On("Get", mock.Anything).Return(httpError)

	sut := DbAuthorizeTransactionUsecase{
		mockSaveRepository,
		mockSetFailureTransactionRepository,
		mockSetSuccessTransactionRepository,
		httpClient,
	}

	return sut,
		mockSaveRepository,
		mockSetFailureTransactionRepository,
		mockSetSuccessTransactionRepository,
		httpClient
}

func Test_ShouldCallRepositoryCorrectly(t *testing.T) {
	sut, saveRepository, _, _, _ := makeSut(t, nil, nil)
	input := makeInput()

	sut.Call(input)

	saveRepository.AssertCalled(t, "Save", input)
	saveRepository.AssertExpectations(t)
}

func Test_ShouldReturnErrorIfRepositoryFails(t *testing.T) {
	error := errors.New("Could not save transaction")
	sut, _, _, _, _ := makeSut(t, error, nil)

	err := sut.Call(makeInput())

	assert.Equal(t, error, err, "error should be the expected")
}

func Test_ShouldCallHttpClientCorrectly(t *testing.T) {
	sut, _, _, _, httpClient := makeSut(t, nil, nil)

	sut.Call(makeInput())

	httpClient.AssertCalled(t, "Get", APIURL)
	httpClient.AssertExpectations(t)
}

func Test_ShouldCallSetFailureTransactionOnHttpFailure(t *testing.T) {
	error := errors.New("Authorization failed")
	sut, _, setFailureRepository, _, _ := makeSut(t, nil, error)
	input := makeInput()

	sut.Call(makeInput())

	setFailureRepository.AssertCalled(t, "SetFailure", input.Id)
	setFailureRepository.AssertExpectations(t)
}

func Test_ShouldCallSetSuccessTransactionOnHttpSuccess(t *testing.T) {
	sut, _, setFailureRepository, setSuccessRepository, _ := makeSut(t, nil, nil)
	input := makeInput()

	sut.Call(makeInput())

	setFailureRepository.AssertNotCalled(t, "SetFailure", mock.Anything)
	setSuccessRepository.AssertCalled(t, "SetSuccess", input.Id)
	setSuccessRepository.AssertExpectations(t)
}
