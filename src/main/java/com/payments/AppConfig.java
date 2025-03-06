package com.payments;

import com.payments.main.validation.EmailValidation;
import com.payments.main.validation.RequiredFieldValidation;
import com.payments.main.validation.Validation;
import com.payments.main.validation.ValidationComposite;
import com.payments.transactions.data.repositories.CreateTransactionRepository;
import com.payments.transactions.data.repositories.GetUserBalanceRepository;
import com.payments.transactions.data.usecases.DbCreateTransaction;
import com.payments.transactions.data.usecases.HttpAuthorizer;
import com.payments.transactions.domain.usecases.Authorizer;
import com.payments.transactions.domain.usecases.CreateTransaction;
import com.payments.transactions.infra.db.mysql.TransactionMysqlRepository;
import com.payments.transactions.infra.mock.TransactionMockRepository;
import com.payments.transactions.presentation.TransactionsController;
import com.payments.users.data.repositories.CreateUserRepository;
import com.payments.users.data.repositories.GetUserByEmailRepository;
import com.payments.users.data.usecases.DbCreateUser;
import com.payments.users.domain.usecases.CreateUser;
import com.payments.users.infra.db.mysql.UserMysqlRepository;
import com.payments.users.presentation.UsersController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
public class AppConfig {
    @Bean
    public UsersController usersController() {
        final List<Validation> validations = List.of(
                new RequiredFieldValidation("name"),
                new RequiredFieldValidation("cpf"),
                new RequiredFieldValidation("email"),
                new RequiredFieldValidation("password"),
                new EmailValidation("email")
        );
        final ValidationComposite validationComposite = new ValidationComposite(validations);

        return new UsersController(validationComposite, createUser());
    }

    @Bean
    public TransactionsController transactionsController() {
        final List<Validation> validations = List.of(
                new RequiredFieldValidation("payeeId"),
                new RequiredFieldValidation("payerId"),
                new RequiredFieldValidation("amount")
        );
        final ValidationComposite validationComposite = new ValidationComposite(validations);

        return new TransactionsController(validationComposite, createTransaction());
    }

    @Bean
    public CreateUser createUser() {
        return new DbCreateUser(
                createUserRepository(),
                getUserByEmailRepository()
        );
    }

    @Bean
    public CreateUserRepository createUserRepository() {
        return new UserMysqlRepository();
    }

    @Bean
    public GetUserByEmailRepository getUserByEmailRepository() {
        return new UserMysqlRepository();
    }

    @Bean
    public CreateTransaction createTransaction() {
        return new DbCreateTransaction(
                createTransactionRepository(),
                getUserBalanceRepository(),
                authorizer()
        );
    }

    public CreateTransactionRepository createTransactionRepository() {
        return new TransactionMysqlRepository();
    }

    public GetUserBalanceRepository getUserBalanceRepository() {
        return new TransactionMockRepository();
    }

    public Authorizer authorizer() {
        return new HttpAuthorizer(restTemplate());
    }

    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
