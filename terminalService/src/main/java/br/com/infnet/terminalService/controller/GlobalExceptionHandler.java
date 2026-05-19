package br.com.infnet.terminalService.controller;

import br.com.infnet.terminalService.execption.TerminalNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(TerminalNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTerminalNotFound(
            TerminalNotFoundException exception
    ) {
        ErrorResponse response = new ErrorResponse(
                "TERMINAL_NAO_ENCONTRADO",
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    public record ErrorResponse(
            String codigo,
            String mensagem
    ) {
    }

}
