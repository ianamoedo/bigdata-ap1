
package com.Ap1.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ValidationErrorInterceptor {

    @ExceptionHandler({MethodArgumentNotValidException.class, IllegalArgumentException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationMessageError handleExceptions(Exception ex) {
        ValidationMessageError response = new ValidationMessageError();

        if (ex instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException validationException = (MethodArgumentNotValidException) ex;
            for (FieldError item : validationException.getFieldErrors()) {
                ValidationError error = new ValidationError();
                error.setField(item.getField());
                error.setMessage(item.getDefaultMessage());
                response.getErrors().add(error);
            }
        } else if (ex instanceof IllegalArgumentException) {
            ValidationError error = new ValidationError();
            error.setMessage(ex.getMessage());
            response.getErrors().add(error);
        }

        return response;
    }
}
