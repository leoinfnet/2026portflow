package br.com.infnet.containerService.handler;

import br.com.infnet.containerService.dto.ErrorResponse;
import br.com.infnet.containerService.exception.TerminalValidationException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(TerminalValidationException.class)
    public ResponseEntity<ErrorResponse> handleTerminalException(
          TerminalValidationException ex,
          HttpServletRequest req
    ){
        ErrorResponse terminalInvalido =
                new ErrorResponse("TERMINAL INVALIDO", ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT)
                .body(terminalInvalido);
    }
}
