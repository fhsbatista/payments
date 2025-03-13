package repositories

type SetSuccessTransactionRepository interface {
	SetSuccess(id int64) error
}