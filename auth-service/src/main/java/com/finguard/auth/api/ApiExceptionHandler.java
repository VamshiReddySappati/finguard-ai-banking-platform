package com.finguard.auth.api;

import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(BadCredentialsException.class)
    ResponseEntity<?> badCredentials(BadCredentialsException ex){return error(HttpStatus.UNAUTHORIZED,"Invalid email or password");}
    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<?> status(ResponseStatusException ex){return error(HttpStatus.valueOf(ex.getStatusCode().value()),ex.getReason());}
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<?> validation(MethodArgumentNotValidException ex){
        String message=ex.getBindingResult().getFieldErrors().stream().findFirst().map(e->e.getField()+" "+e.getDefaultMessage()).orElse("Invalid request");
        return error(HttpStatus.BAD_REQUEST,message);
    }
    private ResponseEntity<?> error(HttpStatus status,String message){return ResponseEntity.status(status).body(Map.of("timestamp", Instant.now(),"status",status.value(),"error",status.getReasonPhrase(),"message",message==null?status.getReasonPhrase():message));}
}
