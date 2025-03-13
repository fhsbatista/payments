package infra

import (
	"authorization/domain"
	"math/big"
	"testing"
	"time"

	_ "github.com/go-sql-driver/mysql"
	"github.com/stretchr/testify/assert"
)

func Test_ShouldReturnAuthorization(t *testing.T) {
	input := domain.TransactionInput{
		TransactionId: 1,
		PayerId:       2,
		PayeeId:       3,
		Amount:        *big.NewFloat(3.4),
		Time:          time.Now(),
		Status:        string(domain.Authorized),
	}
	sut := TransactionsMysqlRepository{}

	result, _ := sut.Save(input)

	assert.NotNil(t, result)
	assert.NotNil(t, result.Id)
	assert.NotNil(t, input.TransactionId, result.TransactionId)
	assert.Equal(t, input.PayerId, result.PayerId)
	assert.Equal(t, input.PayeeId, result.PayeeId)
	assert.Equal(t, input.Amount, result.Amount)
	assert.Equal(t, input.Time, result.Time)
	assert.Equal(t, input.Status, string(result.Status))
}
