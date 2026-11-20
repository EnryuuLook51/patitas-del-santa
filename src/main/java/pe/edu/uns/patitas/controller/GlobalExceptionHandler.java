package pe.edu.uns.patitas.controller;

import pe.edu.uns.patitas.exception.RecursoNoEncontradoException;
import pe.edu.uns.patitas.exception.ReglaNegocioException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String,Object>> manejarNoEncontrado(RecursoNoEncontradoException ex) {
        return respuesta(HttpStatus.NOT_FOUND, ex.getMessage());
    }
    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<Map<String,Object>> manejarRegla(ReglaNegocioException ex) {
        return respuesta(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,Object>> manejarValidacion(MethodArgumentNotValidException ex) {
        Map<String,Object> detalles = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e -> detalles.put(e.getField(), e.getDefaultMessage()));
        Map<String,Object> body = new LinkedHashMap<>();
        body.put("fecha", LocalDateTime.now());
        body.put("estado", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Datos inválidos");
        body.put("detalles", detalles);
        return ResponseEntity.badRequest().body(body);
    }
    private ResponseEntity<Map<String,Object>> respuesta(HttpStatus estado, String mensaje) {
        Map<String,Object> body = new LinkedHashMap<>();
        body.put("fecha", LocalDateTime.now());
        body.put("estado", estado.value());
        body.put("error", estado.getReasonPhrase());
        body.put("mensaje", mensaje);
        return ResponseEntity.status(estado).body(body);
    }
}
