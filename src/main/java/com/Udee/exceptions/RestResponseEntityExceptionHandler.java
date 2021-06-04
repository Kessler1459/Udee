package com.Udee.exceptions;

import com.Udee.exceptions.notFound.BrandNotFoundException;
import com.Udee.exceptions.notFound.ResourceNotFoundException;
import com.Udee.models.dto.MessageDTO;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {BrandNotFoundException.class})
    public ResponseEntity<Object> handleNotFound(RuntimeException ex) {
        return new ResponseEntity<>(new MessageDTO(ex.getLocalizedMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolations(ConstraintViolationException ex) {
        List<String> errors = new ArrayList<>();
        for (ConstraintViolation e : ex.getConstraintViolations()) {
            errors.add(e.getRootBeanClass().getName() + " " + e.getPropertyPath() + ": " + e.getMessage());
        }
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, errors, ex.getLocalizedMessage());
        return new ResponseEntity<>(apiError, apiError.getHttpStatus());
    }

    //todo talvez poner estos errores en un objeto para la response no suelto
    @ExceptionHandler(value = PageException.class)
    public ResponseEntity<Object> handlePaginationException(PageException ex) {
        return new ResponseEntity<>(new MessageDTO(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<Object> handlePaginationException(ResourceNotFoundException ex) {
        return new ResponseEntity<>(new MessageDTO(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = HttpClientErrorException.class)
    public ResponseEntity<Object> handleClientErrorException(HttpClientErrorException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new MessageDTO(ex.getMessage()));
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Missing parameter " + ex.getParameterName() + " of type " + ex.getParameterType()));
    }

    @ExceptionHandler(value = DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolationException (DataIntegrityViolationException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new MessageDTO(ex.getLocalizedMessage()));
    }

    @ExceptionHandler(value = WrongCredentialsException.class)
    public ResponseEntity<MessageDTO> handleBadCredentials(WrongCredentialsException ex){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageDTO(ex.getMessage()));
    }

}
