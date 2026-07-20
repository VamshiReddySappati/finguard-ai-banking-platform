package com.finguard.transaction.api;
import org.springframework.http.*;import org.springframework.web.bind.MethodArgumentNotValidException;import org.springframework.web.bind.annotation.*;import org.springframework.web.server.ResponseStatusException;import org.springframework.web.client.RestClientResponseException;import java.time.Instant;import java.util.Map;
@RestControllerAdvice public class ApiExceptionHandler {
 @ExceptionHandler(ResponseStatusException.class) ResponseEntity<?> status(ResponseStatusException ex){return error(HttpStatus.valueOf(ex.getStatusCode().value()),ex.getReason());}
 @ExceptionHandler(RestClientResponseException.class) ResponseEntity<?> downstream(RestClientResponseException ex){return error(HttpStatus.BAD_GATEWAY,"Account service rejected the request: "+ex.getResponseBodyAsString());}
 @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<?> validation(MethodArgumentNotValidException ex){String m=ex.getBindingResult().getFieldErrors().stream().findFirst().map(e->e.getField()+" "+e.getDefaultMessage()).orElse("Invalid request");return error(HttpStatus.BAD_REQUEST,m);}
 private ResponseEntity<?> error(HttpStatus s,String m){return ResponseEntity.status(s).body(Map.of("timestamp",Instant.now(),"status",s.value(),"error",s.getReasonPhrase(),"message",m==null?s.getReasonPhrase():m));}
}
