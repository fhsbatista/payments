package usecases

import (
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
}

func NewDbAuthorizeTransactionUsecase(
	saveTransactionRepository repositories.SaveTransactionRepository,
	setFailureTransactionRepository repositories.SetFailureTransactionRepository,
	setSuccessTransactionRepository repositories.SetSuccessTransactionRepository,
	httpClient http.HttpClient,
) *DbAuthorizeTransactionUsecase {
	return &DbAuthorizeTransactionUsecase{
		saveTransactionRepository:       saveTransactionRepository,
		setFailureTransactionRepository: setFailureTransactionRepository,
		setSuccessTransactionRepository: setSuccessTransactionRepository,
		httpClient:                      httpClient,
	}
}

func (u *DbAuthorizeTransactionUsecase) Call(input domain.TransactionInput) error {
	err := u.saveTransactionRepository.Save(input)

	if err != nil {
		return err
	}

	err = u.httpClient.Get(APIURL)

	if err != nil {
		u.setFailureTransactionRepository.SetFailure(input.Id)
		return nil
	}

	u.setSuccessTransactionRepository.SetSuccess(input.Id)

	return nil
}
