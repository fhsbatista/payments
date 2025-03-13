package usecases

import (
	"authorization/data/events"
	"authorization/data/http"
	"authorization/data/repositories"
	"authorization/domain"
)

const APIURL = "https://util.devi.tools/api/v2/authorize"

type DbAuthorizeTransactionUsecase struct {
	saveTransactionRepository       repositories.SaveTransactionRepository
	setFailureTransactionRepository repositories.SetFailureTransactionRepository
	setSuccessTransactionRepository repositories.SetSuccessTransactionRepository
	httpClient                      http.HttpClient
	eventPublisher                  events.EventPublisher
}

func NewDbAuthorizeTransactionUsecase(
	saveTransactionRepository repositories.SaveTransactionRepository,
	setFailureTransactionRepository repositories.SetFailureTransactionRepository,
	setSuccessTransactionRepository repositories.SetSuccessTransactionRepository,
	httpClient http.HttpClient,
	eventPublisher events.EventPublisher,
) *DbAuthorizeTransactionUsecase {
	return &DbAuthorizeTransactionUsecase{
		saveTransactionRepository:       saveTransactionRepository,
		setFailureTransactionRepository: setFailureTransactionRepository,
		setSuccessTransactionRepository: setSuccessTransactionRepository,
		httpClient:                      httpClient,
		eventPublisher:                  eventPublisher,
	}
}

func (u *DbAuthorizeTransactionUsecase) Call(input domain.TransactionInput) error {
	authorization, err := u.saveTransactionRepository.Save(input)

	if err != nil {
		return err
	}

	err = u.httpClient.Get(APIURL)

	if err != nil {
		u.setFailureTransactionRepository.SetFailure(input.TransactionId)
		u.eventPublisher.Publish(input.ToAuthorization(authorization.Id, domain.Declined))
		return nil
	}

	u.setSuccessTransactionRepository.SetSuccess(input.TransactionId)
	u.eventPublisher.Publish(input.ToAuthorization(authorization.Id, domain.Authorized))

	return nil
}
