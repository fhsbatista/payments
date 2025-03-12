package domain

type AuthorizeTransactionUsecase interface {
	Call(input TransactionInput) error
}