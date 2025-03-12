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