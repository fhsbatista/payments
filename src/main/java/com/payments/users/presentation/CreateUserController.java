package com.payments.users.presentation;

import com.payments.users.domain.CustomExceptions;
import com.payments.users.domain.entities.User;
import com.payments.users.domain.usecases.CreateUser;
import com.payments.users.domain.usecases.CreateUserInput;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CreateUserController {
    private final CreateUser usecase;

    public CreateUserController(CreateUser usecase) {
        this.usecase = usecase;
    }

    public ResponseEntity<User> handle(CreateUserInput input) throws CustomExceptions {
        usecase.call(input);
        return ResponseEntity.notFound().build();
    }
}
