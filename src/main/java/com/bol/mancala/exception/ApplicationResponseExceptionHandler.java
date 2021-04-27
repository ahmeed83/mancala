package com.bol.mancala.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

/**
 * Application Response Exception Handler.
 *
 * This class will take care of mapping the exception to a specific response so that
 * we can send it back to the client.
 */
@ControllerAdvice
public class ApplicationResponseExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Exception handler.
     *
     * @param ex exception.
     * @return the error json response.
     */
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ApplicationErrorResponse> customCategory(final ApplicationException ex) {
        return new ResponseEntity<>(ApplicationErrorResponse.builder()
                                            .timestamp(LocalDateTime.now())
                                            .errorMessage(ex.getMessage())
                                            .httpStatus(ex.getHttpStatus().value())
                                            .build(), ex.getHttpStatus());
    }
}
