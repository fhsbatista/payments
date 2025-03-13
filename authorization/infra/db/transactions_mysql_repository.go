package infra

import (
	"authorization/domain"
	"database/sql"
	"log"
	"math/big"
	"time"
)

type TransactionsMysqlRepository struct{}

func (repo *TransactionsMysqlRepository) Find(id int64) (*domain.Authorization, error) {
	uri := "root:root@tcp(127.0.0.1:3306)/test"
	db, err := sql.Open("mysql", uri)
	if err != nil {
		log.Fatal("Could not connect to database")
	}
	defer db.Close()

	var a domain.Authorization
	var amountBytes []byte
	var timeBytes []byte
	var status string
	err = db.QueryRow(
		"SELECT * FROM AUTHORIZATIONS WHERE id = ? LIMIT 1", 
		id,
	).Scan(&a.Id, &a.PayerId, &a.PayeeId, &amountBytes, &timeBytes, &status)

	if err != nil {
		return nil, err
	}

	amount, _, err := big.ParseFloat(string(amountBytes), 10, 53, big.ToNearestEven)
	if err != nil {
		return nil, err
	}
	
	parsedTime, err := time.Parse("2006-01-02 15:04:05", string(timeBytes))
	if err != nil {
		return nil, err
	}
	
	a.Amount = *amount
	a.Time = parsedTime.In(time.Local)
	a.Status = domain.Status(status)
	
	return &a, nil
}

func (repo *TransactionsMysqlRepository) SetFailure(id int64) (*domain.Authorization, error) {
	uri := "root:root@tcp(127.0.0.1:3306)/test"
	db, err := sql.Open("mysql", uri)
	if err != nil {
		log.Fatal("Could not connect to database")
	}
	defer db.Close()

	query := "UPDATE AUTHORIZATIONS " +
	"SET status = ? " +
	"WHERE id = ?"

	_, err = db.Exec(
		query,
		string(domain.Declined),
		id,
	)

	if err != nil {
		return nil, err
	}

	return nil, nil
}


func (repo *TransactionsMysqlRepository) SetSuccess(id int64) (*domain.Authorization, error) {
	uri := "root:root@tcp(127.0.0.1:3306)/test"
	db, err := sql.Open("mysql", uri)
	if err != nil {
		log.Fatal("Could not connect to database")
	}
	defer db.Close()

	query := "UPDATE AUTHORIZATIONS " +
	"SET status = ? " +
	"WHERE id = ?"

	_, err = db.Exec(
		query,
		string(domain.Authorized),
		id,
	)

	if err != nil {
		return nil, err
	}

	return nil, nil
}

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