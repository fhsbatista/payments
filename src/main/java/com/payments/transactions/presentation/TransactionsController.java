package com.payments.transactions.presentation;

import com.payments.main.validation.Validation;
import com.payments.main.validation.ValidationException;
import com.payments.transactions.domain.CustomExceptions;
import com.payments.transactions.domain.entities.Transaction;
import com.payments.transactions.domain.usecases.CreateTransaction;
import com.payments.transactions.domain.usecases.CreateTransactionInput;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
public class TransactionsController {
    private final Validation validation;
    private final CreateTransaction usecase;

    public TransactionsController(Validation validation, CreateTransaction usecase) {
        this.validation = validation;
        this.usecase = usecase;
    }

    @PostMapping("/transactions")
    ResponseEntity<?> handle(@RequestBody CreateTransactionInput input) {
        try {
            validation.validate(input);
            final Transaction transaction = usecase.call(input);

            final UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance();
            final URI uri = uriBuilder
                    .path("/transactions/{id}")
                    .buildAndExpand(transaction.id())
                    .toUri();

            return ResponseEntity
                    .created(uri)
                    .body(TransactionPresenter.fromTransaction(transaction));
        } catch (CustomExceptions e) {
            return ResponseEntity.badRequest().body(ErrorPresenter.fromException(e));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(ErrorPresenter.fromValidationException(e));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(ErrorPresenter.fromException(e));
        }
    }
}
