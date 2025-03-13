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

type MockEventPublisher struct {
	mock.Mock
}

func (m *MockEventPublisher) Publish(authorization domain.Authorization) error {
	args := m.Called(authorization)
	return args.Error(0)
}

func makeInput() domain.TransactionInput {
	return domain.TransactionInput{}
}

func makeSut(
	t *testing.T,
	saveError error,
	httpError error,
	eventPublishError error,
) (
	DbAuthorizeTransactionUsecase,
	*MockSaveTransactionsRepository,
	*MockSetFailureTransactionRepository,
	*MockSetSuccessTransactionRepository,
	*MockHttpClient,
	*MockEventPublisher,
) {
	t.Helper()

	mockSaveRepository := new(MockSaveTransactionsRepository)
	mockSetFailureTransactionRepository := new(MockSetFailureTransactionRepository)
	mockSetSuccessTransactionRepository := new(MockSetSuccessTransactionRepository)
	httpClient := new(MockHttpClient)
	eventPublisher := new(MockEventPublisher)

	mockSaveRepository.On("Save", mock.Anything).Return(saveError)
	mockSetFailureTransactionRepository.On("SetFailure", mock.Anything).Return(nil)
	mockSetSuccessTransactionRepository.On("SetSuccess", mock.Anything).Return(nil)
	httpClient.On("Get", mock.Anything).Return(httpError)
	eventPublisher.On("Publish", mock.Anything).Return(eventPublishError)

	sut := DbAuthorizeTransactionUsecase{
		mockSaveRepository,
		mockSetFailureTransactionRepository,
		mockSetSuccessTransactionRepository,
		httpClient,
		eventPublisher,
	}

	return sut,
		mockSaveRepository,
		mockSetFailureTransactionRepository,
		mockSetSuccessTransactionRepository,
		httpClient,
		eventPublisher
}

func Test_ShouldCallRepositoryCorrectly(t *testing.T) {
	sut, saveRepository, _, _, _, _ := makeSut(t, nil, nil, nil)
	input := makeInput()

	sut.Call(input)

	saveRepository.AssertCalled(t, "Save", input)
	saveRepository.AssertExpectations(t)
}

func Test_ShouldReturnErrorIfRepositoryFails(t *testing.T) {
	error := errors.New("Could not save transaction")
	sut, _, _, _, _, _ := makeSut(t, error, nil, nil)

	err := sut.Call(makeInput())

	assert.Equal(t, error, err, "error should be the expected")
}

func Test_ShouldCallHttpClientCorrectly(t *testing.T) {
	sut, _, _, _, httpClient, _ := makeSut(t, nil, nil, nil)

	sut.Call(makeInput())

	httpClient.AssertCalled(t, "Get", APIURL)
	httpClient.AssertExpectations(t)
}

func Test_ShouldCallSetFailureTransactionOnHttpFailure(t *testing.T) {
	error := errors.New("Authorization failed")
	sut, _, setFailureRepository, _, _, _ := makeSut(t, nil, error, nil)
	input := makeInput()

	sut.Call(makeInput())

	setFailureRepository.AssertCalled(t, "SetFailure", input.Id)
	setFailureRepository.AssertExpectations(t)
}

func Test_ShouldCallEventPublisherWithDeclinedOnHttpSuccess(t *testing.T) {
	error := errors.New("Authorization failed")
	sut, _, _, _, _, eventPublisher := makeSut(t, nil, error, nil)
	input := makeInput()

	sut.Call(makeInput())

	expectedAuthorization := domain.Authorization{
		Id: input.Id,
		PayerId: input.PayeeId,
		PayeeId: input.PayeeId,
		Amount: input.Amount,
		Time: input.Time,
		Status: domain.Declined,
	}
	eventPublisher.AssertCalled(t, "Publish", expectedAuthorization)
	eventPublisher.AssertExpectations(t)
}

func Test_ShouldCallSetSuccessTransactionOnHttpSuccess(t *testing.T) {
	sut, _, setFailureRepository, setSuccessRepository, _, _ := makeSut(t, nil, nil, nil)
	input := makeInput()

	sut.Call(makeInput())

	setFailureRepository.AssertNotCalled(t, "SetFailure", mock.Anything)
	setSuccessRepository.AssertCalled(t, "SetSuccess", input.Id)
	setSuccessRepository.AssertExpectations(t)
}

func Test_ShouldCallEventPublisherWithAuthorizedOnHttpSuccess(t *testing.T) {
	sut, _, _, _, _, eventPublisher := makeSut(t, nil, nil, nil)
	input := makeInput()

	sut.Call(makeInput())

	expectedAuthorization := domain.Authorization{
		Id: input.Id,
		PayerId: input.PayeeId,
		PayeeId: input.PayeeId,
		Amount: input.Amount,
		Time: input.Time,
		Status: domain.Authorized,
	}
	eventPublisher.AssertCalled(t, "Publish", expectedAuthorization)
	eventPublisher.AssertExpectations(t)
}
