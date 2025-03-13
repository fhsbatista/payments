package usecases

import (
	"authorization/domain"
	"errors"
	"math/big"
	"testing"
	"time"

	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/mock"
)

type MockSaveTransactionsRepository struct {
	mock.Mock
}

func (m *MockSaveTransactionsRepository) Save(input domain.TransactionInput) (*domain.Authorization, error) {
	args := m.Called(input)
	auth, _ := args.Get(0).(domain.Authorization)
	return &auth, args.Error(1)
}

type MockSetFailureTransactionRepository struct {
	mock.Mock
}

func (m *MockSetFailureTransactionRepository) SetFailure(id int64) (*domain.Authorization, error) {
	args := m.Called(id)
	auth, _ := args.Get(0).(domain.Authorization)
	return &auth, args.Error(1)
}

type MockSetSuccessTransactionRepository struct {
	mock.Mock
}

func (m *MockSetSuccessTransactionRepository) SetSuccess(id int64) (*domain.Authorization, error) {
	args := m.Called(id)
	auth, _ := args.Get(0).(domain.Authorization)
	return &auth, args.Error(1)
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

func makeAuthorization() domain.Authorization {
	return domain.Authorization{
		Id:      1,
		PayerId: 2,
		PayeeId: 3,
		Amount:  *big.NewFloat(2.0),
		Time:    time.Now(),
		Status:  domain.Pending,
	}
}

func makeSut(
	t *testing.T,
	saveError error,
	httpError error,
	eventPublishError error,
) (
	DbAuthorizeTransactionUsecase,
	domain.Authorization,
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
	authorization := makeAuthorization()

	if saveError != nil {
		mockSaveRepository.On("Save", mock.Anything).Return(nil, saveError)
	} else {
		mockSaveRepository.On("Save", mock.Anything).Return(authorization, nil)
	}

	mockSetFailureTransactionRepository.On("SetFailure", mock.Anything).Return(nil, nil)
	mockSetSuccessTransactionRepository.On("SetSuccess", mock.Anything).Return(nil, nil)
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
		authorization,
		mockSaveRepository,
		mockSetFailureTransactionRepository,
		mockSetSuccessTransactionRepository,
		httpClient,
		eventPublisher
}

func Test_ShouldCallRepositoryCorrectly(t *testing.T) {
	sut, _, saveRepository, _, _, _, _ := makeSut(t, nil, nil, nil)
	input := makeInput()

	sut.Call(input)

	saveRepository.AssertCalled(t, "Save", input)
	saveRepository.AssertExpectations(t)
}

func Test_ShouldReturnErrorIfRepositoryFails(t *testing.T) {
	error := errors.New("Could not save transaction")
	sut, _, _, _, _, _, _ := makeSut(t, error, nil, nil)

	err := sut.Call(makeInput())

	assert.Equal(t, error, err, "error should be the expected")
}

func Test_ShouldCallHttpClientCorrectly(t *testing.T) {
	sut, _, _, _, _, httpClient, _ := makeSut(t, nil, nil, nil)

	sut.Call(makeInput())

	httpClient.AssertCalled(t, "Get", APIURL)
	httpClient.AssertExpectations(t)
}

func Test_ShouldCallSetFailureTransactionOnHttpFailure(t *testing.T) {
	error := errors.New("Authorization failed")
	sut, _, _, setFailureRepository, _, _, _ := makeSut(t, nil, error, nil)
	input := makeInput()

	sut.Call(makeInput())

	setFailureRepository.AssertCalled(t, "SetFailure", input.TransactionId)
	setFailureRepository.AssertExpectations(t)
}

func Test_ShouldCallEventPublisherWithDeclinedOnHttpSuccess(t *testing.T) {
	error := errors.New("Authorization failed")
	sut, authorization, _, _, _, _, eventPublisher := makeSut(t, nil, error, nil)
	input := makeInput()

	sut.Call(makeInput())

	eventPublisher.AssertCalled(t, "Publish", input.ToAuthorization(authorization.Id, domain.Declined))
	eventPublisher.AssertExpectations(t)
}

func Test_ShouldCallSetSuccessTransactionOnHttpSuccess(t *testing.T) {
	sut, _, _, setFailureRepository, setSuccessRepository, _, _ := makeSut(t, nil, nil, nil)
	input := makeInput()

	sut.Call(makeInput())

	setFailureRepository.AssertNotCalled(t, "SetFailure", mock.Anything)
	setSuccessRepository.AssertCalled(t, "SetSuccess", input.TransactionId)
	setSuccessRepository.AssertExpectations(t)
}

func Test_ShouldCallEventPublisherWithAuthorizedOnHttpSuccess(t *testing.T) {
	sut, authorization, _, _, _, _, eventPublisher := makeSut(t, nil, nil, nil)
	input := makeInput()

	sut.Call(makeInput())

	eventPublisher.AssertCalled(t, "Publish", input.ToAuthorization(authorization.Id, domain.Authorized))
	eventPublisher.AssertExpectations(t)
}
