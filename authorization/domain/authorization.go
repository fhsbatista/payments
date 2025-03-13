package domain

import (
	"math/big"
	"time"
)

type Status string

const (
	Pending    Status = "pending"
	Authorized Status = "authorized"
	Declined   Status = "declined"
)

type Authorization struct {
	Id      int64
	PayerId int64
	PayeeId int64
	Amount  big.Float
	Time    time.Time
	Status  Status
}
