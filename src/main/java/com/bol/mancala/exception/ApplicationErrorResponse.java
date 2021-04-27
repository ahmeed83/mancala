package com.bol.mancala.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Application error response. This will be used as a json response for each exception.
 */
@Getter
@Builder
public class ApplicationErrorResponse {

    /**
     * Error time.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private final LocalDateTime timestamp;

    /**
     * Http status
     */
    private final int httpStatus;

    /**
     * Error message will be returned to the client.
     */
    private final String errorMessage;
}
