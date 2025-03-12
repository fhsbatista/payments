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

type MockHttpClient struct {
	mock.Mock
}

func (m *MockHttpClient) Get(url string) error {
	args := m.Called(url)
	return args.Error(0)
}

func makeSaveTransactionRepository() *MockSaveTransactionsRepository {
	repo := new(MockSaveTransactionsRepository)
	repo.On("Save", mock.Anything).Return(nil) 
	return repo
}

func makeSetFailureTransactionRepository() *MockSetFailureTransactionRepository {
	repo := new(MockSetFailureTransactionRepository)
	repo.On("SetFailure", mock.Anything).Return(nil) 
	return repo
}

func Test_ShouldCallRepositoryCorrectly(t *testing.T) {
	input := domain.TransactionInput{}
	mockSaveRepository := makeSaveTransactionRepository()
	mockSetFailureTransactionRepository := makeSetFailureTransactionRepository()
	httpClient := new(MockHttpClient)
	httpClient.On("Get", mock.Anything).Return(nil)

	usecase := DbAuthorizeTransactionUsecase{
		mockSaveRepository,
		mockSetFailureTransactionRepository,
		httpClient,
	}

	usecase.Call(input)

	mockSaveRepository.AssertCalled(t, "Save", input)
	mockSaveRepository.AssertExpectations(t)
}

func Test_ShouldReturnErrorIfRepositoryFails(t *testing.T) {
	input := domain.TransactionInput{}
	mockSaveRepository := new(MockSaveTransactionsRepository)
	expectedError := errors.New("Could not save transaction")
	mockSaveRepository.On("Save", input).Return(expectedError)
	mockSetFailureTransactionRepository := makeSetFailureTransactionRepository()
	httpClient := new(MockHttpClient)
	httpClient.On("Get", mock.Anything).Return(nil)

	usecase := DbAuthorizeTransactionUsecase{
		mockSaveRepository,
		mockSetFailureTransactionRepository,
		httpClient,
	}

	err := usecase.Call(input)

	assert.Equal(t, expectedError, err, "error should be the expected")
}

func Test_ShouldCallHttpClientCorrectly(t *testing.T) {
	input := domain.TransactionInput{}
	mockSaveRepository := new(MockSaveTransactionsRepository)
	mockSaveRepository.On("Save", input).Return(nil)
	mockSetFailureTransactionRepository := makeSetFailureTransactionRepository()
	httpClient := new(MockHttpClient)
	httpClient.On("Get", mock.Anything).Return(nil)
	usecase := DbAuthorizeTransactionUsecase{
		mockSaveRepository,
		mockSetFailureTransactionRepository,
		httpClient,
	}

	usecase.Call(input)

	httpClient.AssertCalled(t, "Get", APIURL)
	httpClient.AssertExpectations(t)
}

func Test_ShouldCallSetFailureTransactionOnHttpFailure(t *testing.T) {
	input := domain.TransactionInput{Id: 1}
	mockSaveRepository := new(MockSaveTransactionsRepository)
	mockSaveRepository.On("Save", input).Return(nil)
	mockSetFailureTransactionRepository := makeSetFailureTransactionRepository()
	httpClient := new(MockHttpClient)
	error := errors.New("Authorization failed")
	httpClient.On("Get", mock.Anything).Return(error)
	usecase := DbAuthorizeTransactionUsecase{
		mockSaveRepository,
		mockSetFailureTransactionRepository,
		httpClient,
	}

	usecase.Call(input)

	mockSetFailureTransactionRepository.AssertCalled(t, "SetFailure", input.Id)
	httpClient.AssertExpectations(t)
}
