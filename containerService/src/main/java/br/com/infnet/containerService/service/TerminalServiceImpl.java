package br.com.infnet.containerService.service;

import br.com.infnet.containerService.client.TerminalClient;
import br.com.infnet.containerService.dto.ValidacaoTerminalResponse;
import br.com.infnet.containerService.exception.TerminalValidationException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Profile({"prod"})

public class TerminalServiceImpl implements TerminalService {
    private final TerminalClient client;

    @CircuitBreaker(name = "terminalValidation", fallbackMethod = "fallBackMethod")
    @Retry(name = "terminalValidation")
    public ValidacaoTerminalResponse validarTerminal(String terminalId, String cargoType){
        return client.validarTerminal(terminalId,cargoType);
    }
    public ValidacaoTerminalResponse fallBackMethod(String terminalId,
                                                    String cargoType,
                                                    Throwable tw){
        System.out.println("Fall Back Executado!");
        throw new TerminalValidationException("Não foi possivel validar o terminal par a a carga " + cargoType);

    }

}
