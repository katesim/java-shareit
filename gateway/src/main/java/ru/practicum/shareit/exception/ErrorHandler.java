package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleValidationException(ValidationException exception) {
        log.error(exception.getMessage());
        return exception.getMessage();
    }

    @ExceptionHandler({ConstraintViolationException.class, MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleAnnotationValidationException(final RuntimeException runtimeException) {
        return runtimeException.getMessage();
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleForbiddenException(ForbiddenException exception) {
        log.error(exception.getMessage());
        return exception.getMessage();
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFoundException(NotFoundException exception) {
        log.error(exception.getMessage());
        return exception.getMessage();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleException(Throwable exception) {
        log.error(exception.getMessage());
        return exception.getMessage();
    }
}
