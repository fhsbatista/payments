package infra

import (
	"authorization/domain"
	"database/sql"
	"log"
)

type TransactionsMysqlRepository struct{}

func (repo *TransactionsMysqlRepository) Save(input domain.TransactionInput) (*domain.Authorization, error) {
	uri := "root:root@tcp(127.0.0.1:3306)/test"
	db, err := sql.Open("mysql", uri)
	if err != nil {
		log.Fatal("Could not connect to database")
	}
	defer db.Close()

	query := "INSERT INTO AUTHORIZATIONS " +
		"(payer_id, payee_id, amount, time, status) " +
		"VALUES (?, ?, ?, ?, ?)"

	amount, _ := input.Amount.Float64()

	result, err := db.Exec(
		query,
		input.PayerId,
		input.PayeeId,
		amount,
		input.Time,
		input.Status,
	)

	if err != nil {
		return nil, err
	}

	insertedId, err := result.LastInsertId()
	if err != nil {
		return nil, err
	}

	return &domain.Authorization{
		Id: insertedId,
		PayerId: input.PayerId,
		PayeeId: input.PayeeId,
		Amount: input.Amount,
		Time: input.Time,
		Status: domain.Status(input.Status),
	}, nil
}