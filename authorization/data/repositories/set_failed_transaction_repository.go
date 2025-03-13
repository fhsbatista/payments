package repositories

import "authorization/domain"

type SetFailureTransactionRepository interface {
	SetFailure(id int64) (*domain.Authorization, error)
}