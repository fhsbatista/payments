package com.payments;

import com.payments.main.validation.EmailValidation;
import com.payments.main.validation.RequiredFieldValidation;
import com.payments.main.validation.Validation;
import com.payments.main.validation.ValidationComposite;
import com.payments.users.data.repositories.CreateUserRepository;
import com.payments.users.data.repositories.GetUserByEmailRepository;
import com.payments.users.data.usecases.DbCreateUser;
import com.payments.users.domain.usecases.CreateUser;
import com.payments.users.infra.db.mysql.UserMysqlRepository;
import com.payments.users.presentation.UsersController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}
