package com.Udee.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {BrandNotFoundException.class})
    public ResponseEntity<Object> handleNotFound(RuntimeException ex){
        return new ResponseEntity<>(ex.getLocalizedMessage(),HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolations(ConstraintViolationException ex){
        List<String> errors=new ArrayList<>();
        for (ConstraintViolation e: ex.getConstraintViolations()){
            errors.add(e.getRootBeanClass().getName()+" "+e.getPropertyPath()+": "+e.getMessage());
        }
        ApiError apiError=new ApiError(HttpStatus.BAD_REQUEST,errors,ex.getLocalizedMessage());
        return new ResponseEntity<>(apiError,apiError.getHttpStatus());
    }
    //todo talvez poner estos errores en un objeto para la response no suelto
    @ExceptionHandler(value = {PageException.class})
    public ResponseEntity<Object> handlePaginationException(PageException ex){
        return new ResponseEntity<>(ex.getMessage(),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<Object> handlePaginationException(ResourceNotFoundException ex){
        return new ResponseEntity<>(ex.getMessage(),HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = HttpClientErrorException.class)
    public ResponseEntity<Object> handleClientErrorException(HttpClientErrorException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
}
