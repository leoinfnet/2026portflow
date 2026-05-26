package br.com.infnet.containerService.service;

import br.com.infnet.containerService.dto.ValidacaoTerminalResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile({"default","dev"})
public class TerminalServiceMock implements TerminalService{
    @Override
    public ValidacaoTerminalResponse validarTerminal(String terminalId, String cargoType) {
        return new ValidacaoTerminalResponse(
                "terminal-001",
                true,
                true,
                true,
                true,
                true,
                "Terminal válido para receber a carga"
        );
    }
}
