package repositories

import "authorization/domain"

type SaveTransactionRepository interface {
	Save(input domain.TransactionInput) error
}