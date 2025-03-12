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

type MockHttpClient struct {
	mock.Mock
}

func (m *MockHttpClient) Get(url string) error {
	args := m.Called(url)
	return args.Error(0)
}

func Test_ShouldCallRepositoryCorrectly(t *testing.T) {
	input := domain.TransactionInput{}
	mockSaveRepository := new(MockSaveTransactionsRepository)
	mockSaveRepository.On("Save", input).Return(nil)
	httpClient := new(MockHttpClient)
	httpClient.On("Get", mock.Anything).Return(nil)

	usecase := DbAuthorizeTransactionUsecase{
		mockSaveRepository,
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
	httpClient := new(MockHttpClient)
	httpClient.On("Get", mock.Anything).Return(nil)

	usecase := DbAuthorizeTransactionUsecase{
		mockSaveRepository,
		httpClient,
	}

	err := usecase.Call(input)

	assert.Equal(t, expectedError, err, "error should be the expected")
}

func Test_ShouldCallHttpClientCorrectly(t *testing.T) {
	input := domain.TransactionInput{}
	mockSaveRepository := new(MockSaveTransactionsRepository)
	mockSaveRepository.On("Save", input).Return(nil)
	httpClient := new(MockHttpClient)
	httpClient.On("Get", mock.Anything).Return(nil)
	usecase := DbAuthorizeTransactionUsecase{
		mockSaveRepository,
		httpClient,
	}

	usecase.Call(input)

	httpClient.AssertCalled(t, "Get", APIURL)
	httpClient.AssertExpectations(t)
}
