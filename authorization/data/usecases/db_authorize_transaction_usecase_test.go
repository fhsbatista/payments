package usecases

import (
	"authorization/domain"
	"testing"

	"github.com/stretchr/testify/mock"
)

type MockSaveTransactionsRepository struct {
	mock.Mock
}

func (m *MockSaveTransactionsRepository) Save(input domain.TransactionInput) error {
	args := m.Called(input)
	return args.Error(0)
}

func Test_ShouldCallRepositoryCorrectly(t *testing.T) {
	input := domain.TransactionInput{}
	mockSaveRepository := new(MockSaveTransactionsRepository)
	mockSaveRepository.On("Save", input).Return(nil)
	usecase := DbAuthorizeTransactionUsecase{mockSaveRepository}
	
	usecase.Call(input)

	mockSaveRepository.AssertCalled(t, "Save", input)
	mockSaveRepository.AssertExpectations(t)
}