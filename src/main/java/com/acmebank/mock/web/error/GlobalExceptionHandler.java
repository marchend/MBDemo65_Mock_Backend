package com.acmebank.mock.web.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler that translates domain exceptions into RFC 7807
 * {@code application/problem+json} responses.
 *
 * <p>No stack traces are ever included in the response body.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles {@link AccountNotFoundException} with a 404 problem+json body.
     *
     * @param ex the exception carrying the missing account identifier
     * @return a 404 {@link ResponseEntity} with {@code application/problem+json} content type
     */
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleAccountNotFound(AccountNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
        problem.setTitle("Account Not Found");

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }
}
