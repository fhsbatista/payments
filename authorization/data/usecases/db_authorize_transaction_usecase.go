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
	httpClient                      http.HttpClient
}

func NewDbAuthorizeTransactionUsecase(
	saveTransactionRepository repositories.SaveTransactionRepository,
	setFailureTransactionRepository repositories.SetFailureTransactionRepository,
	httpClient http.HttpClient,
) *DbAuthorizeTransactionUsecase {
	return &DbAuthorizeTransactionUsecase{
		saveTransactionRepository:       saveTransactionRepository,
		setFailureTransactionRepository: setFailureTransactionRepository,
		httpClient:                      httpClient,
	}
}

func (u *DbAuthorizeTransactionUsecase) Call(input domain.TransactionInput) error {
	err := u.saveTransactionRepository.Save(input)

	if err != nil {
		return err
	}

	u.httpClient.Get(APIURL)

	u.setFailureTransactionRepository.SetFailure(input.Id)

	return nil
}
