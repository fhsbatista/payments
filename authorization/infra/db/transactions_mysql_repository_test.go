package infra

import (
	"authorization/domain"
	"math/big"
	"testing"
	"time"

	_ "github.com/go-sql-driver/mysql"
	"github.com/stretchr/testify/assert"
)

func createAuthorization() *domain.Authorization {
	input := domain.TransactionInput{
		TransactionId: 1,
		PayerId: 2,
		PayeeId: 3,
		Amount: *big.NewFloat(2.0),
		Time: time.Now(),
		Status: string(domain.Pending),
	}

	repo := TransactionsMysqlRepository{}

	authorization, _ := repo.Save(input)

	return authorization
}

func TestSave(t *testing.T) {
	t.Run("Should return authorization", func (t *testing.T) {
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
	})
}

func TestFind(t *testing.T) {
	t.Run("Should return authorization", func (t *testing.T) {
		authorization := createAuthorization()
		sut := TransactionsMysqlRepository{}
	
		result, _ := sut.Find(authorization.Id)
	
		assert.NotNil(t, result)
	})
}
