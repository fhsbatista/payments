package domain

import (
	"math/big"
	"time"
)

type TransactionInput struct {
	TransactionId int64
	PayerId       int64
	PayeeId       int64
	Amount        big.Float
	Time          time.Time
	Status        string
}

func (t *TransactionInput) ToAuthorization(id int64, status Status) Authorization {
	return Authorization{
		Id:            id,
		TransactionId: t.TransactionId,
		PayerId:       t.PayeeId,
		PayeeId:       t.PayeeId,
		Amount:        t.Amount,
		Time:          t.Time,
		Status:        status,
	}
}
