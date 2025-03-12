package repositories

type SetFailureTransactionRepository interface {
	SetFailure(id int64) error
}