package domain

import (
	"math/big"
	"time"
)

type TransactionInput struct {
	Id int64
	PayeerId int64
	PayeeId int64
	Amount big.Float
	Time time.Time
	Status string
}

func (t *TransactionInput) ToAuthorization(status Status) Authorization {
	return Authorization{
		Id: t.Id,
		PayerId: t.PayeeId,
		PayeeId: t.PayeeId,
		Amount: t.Amount,
		Time: t.Time,
		Status: status,
	}
}