package com.payments.users.presentation;

import com.payments.users.domain.CustomExceptions;
import com.payments.users.domain.entities.User;
import com.payments.users.domain.usecases.CreateUser;
import com.payments.users.domain.usecases.CreateUserInput;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
public class UsersController {
    private final CreateUser usecase;

    public UsersController(CreateUser usecase) {
        this.usecase = usecase;
    }

    public ResponseEntity<?> handle(CreateUserInput input) {
        try {
            final User user = usecase.call(input);
            final UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance();
            final URI uri = uriBuilder.path("/users/{id}").buildAndExpand(user.id()).toUri();

            return ResponseEntity.created(uri).body(UserPresenter.fromUser(user));
        } catch (CustomExceptions e) {
            return ResponseEntity.badRequest().body(ErrorPresenter.fromException(e));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ErrorPresenter.fromException(e));
        }
    }
}