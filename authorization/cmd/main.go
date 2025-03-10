package main

import (
	"database/sql"
	"log"

	"github.com/golang-migrate/migrate/v4"
	"github.com/golang-migrate/migrate/v4/database/mysql"
	_ "github.com/golang-migrate/migrate/v4/source/file"
	_ "github.com/go-sql-driver/mysql"
)

func main() {
	executeMigrations()
}

func executeMigrations() {
	uri := "root:root@tcp(127.0.0.1:3306)/test"
	db, err := sql.Open("mysql", uri)
	if err != nil {
		log.Fatal("Could not open connection", err)
	}

	driver, err := mysql.WithInstance(db, &mysql.Config{})
	if err != nil {
		log.Fatal("Could not connect to mysql", err)
	}

	m, err := migrate.NewWithDatabaseInstance(
		"file://migrations",
		"mysql",
		driver,
	)
	if err != nil {
		log.Fatal("Erro ao criar instancia da migracao", err)
	}

	if err := m.Up(); err != nil && err != migrate.ErrNoChange {
		log.Fatal("Failure while applying migrations:", err)
	}

	log.Println("Migrations finished!")
}