package com.payments;

import com.payments.users.data.repositories.CreateUserRepository;
import com.payments.users.data.repositories.GetUserByEmailRepository;
import com.payments.users.data.usecases.DbCreateUser;
import com.payments.users.domain.usecases.CreateUser;
import com.payments.users.infra.db.mysql.UserMysqlRepository;
import com.payments.users.presentation.UsersController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public UsersController usersController() {
        return new UsersController(createUser());
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
