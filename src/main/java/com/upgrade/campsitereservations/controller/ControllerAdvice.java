package com.upgrade.campsitereservations.controller;

import com.upgrade.campsitereservations.exception.BadRequestException;
import com.upgrade.campsitereservations.exception.NoAvailabilityException;
import com.upgrade.campsitereservations.exception.NotFoundException;
import com.upgrade.campsitereservations.model.ApiError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.format.DateTimeParseException;

@org.springframework.web.bind.annotation.ControllerAdvice
@Slf4j
public class ControllerAdvice {

    private static final String INTERNAL_ERROR = "An unexpected error happened. Please try later";

    @ExceptionHandler({BadRequestException.class,
            DateTimeParseException.class,
            MissingServletRequestParameterException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ApiError handleBadRequest(Exception ex) {
        return ApiError.builder()
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler({NoAvailabilityException.class, NotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ApiError handleNotFound(Exception ex) {
        return ApiError.builder()
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ApiError handleException(Exception ex) {
        log.info("An Unknown exception happened", ex);
        return ApiError.builder()
                .message(INTERNAL_ERROR)
                .build();
    }

    @ExceptionHandler({DataIntegrityViolationException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ApiError handleConstraintViolationException(Exception ex) {
        log.info("Constraint violation error", ex.getMessage());
        return ApiError.builder()
                .message("A reservation date conflict occurred while trying to add the reservation.")
                .build();
    }
}
