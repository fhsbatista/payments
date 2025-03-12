package usecases

import "authorization/domain"
import "authorization/data/repositories"

type DbAuthorizeTransactionUsecase struct {
	saveTransactionRepository repositories.SaveTransactionRepository
}

func NewDbAuthorizeTransactionUsecase(saveTransactionRepository repositories.SaveTransactionRepository) *DbAuthorizeTransactionUsecase {
	return &DbAuthorizeTransactionUsecase{
		saveTransactionRepository: saveTransactionRepository,
	}
}

func (u *DbAuthorizeTransactionUsecase) Call(input domain.TransactionInput) error {
	err := u.saveTransactionRepository.Save(input)

	if err != nil {
		return err
	}

	return nil
}
