package br.com.infnet.containerService.service;

import br.com.infnet.containerService.dto.ValidacaoTerminalResponse;

public interface TerminalService {
    ValidacaoTerminalResponse validarTerminal(String terminalId, String cargoType);
}
