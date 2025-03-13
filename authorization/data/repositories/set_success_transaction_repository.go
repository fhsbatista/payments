package repositories

import "authorization/domain"

type SetSuccessTransactionRepository interface {
	SetSuccess(id int64) (*domain.Authorization, error)
}