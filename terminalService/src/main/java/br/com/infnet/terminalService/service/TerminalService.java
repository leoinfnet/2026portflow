package br.com.infnet.terminalService.service;

import br.com.infnet.terminalService.domain.Capacidade;
import br.com.infnet.terminalService.domain.Terminal;
import br.com.infnet.terminalService.dto.ValidacaoTerminalResponse;
import br.com.infnet.terminalService.execption.TerminalNotFoundException;
import br.com.infnet.terminalService.repository.TerminalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TerminalService {
    private final TerminalRepository terminalRepository;

    public TerminalService(TerminalRepository terminalRepository) {
        this.terminalRepository = terminalRepository;
    }

    public List<Terminal> findAll() {
        return terminalRepository.findAll();
    }

    public Terminal findByTerminalId(String terminalId) {
        return terminalRepository.findByTerminalId(terminalId)
                .orElseThrow(() -> new TerminalNotFoundException(terminalId));
    }

    public ValidacaoTerminalResponse validarTerminal(String terminalId, String tipoCarga) {
        return terminalRepository.findByTerminalId(terminalId)
                .map(terminal -> validarTerminalExistente(terminal, tipoCarga))
                .orElseGet(() -> new ValidacaoTerminalResponse(
                        terminalId,
                        false,
                        false,
                        false,
                        false,
                        false,
                        "Terminal não encontrado"
                ));
    }

    private ValidacaoTerminalResponse validarTerminalExistente(
            Terminal terminal,
            String tipoCarga
    ) {
        boolean ativo = terminal.isAtivo();
        boolean tipoCargaAceito = terminal.aceitaTipoCarga(tipoCarga);
        boolean capacidadeDisponivel = possuiCapacidadeDisponivel(terminal);

        boolean terminalValido = ativo && tipoCargaAceito && capacidadeDisponivel;

        String mensagem = montarMensagem(
                terminal.getTerminalId(),
                tipoCarga,
                ativo,
                tipoCargaAceito,
                capacidadeDisponivel,
                terminalValido
        );

        return new ValidacaoTerminalResponse(
                terminal.getTerminalId(),
                true,
                ativo,
                tipoCargaAceito,
                capacidadeDisponivel,
                terminalValido,
                mensagem
        );
    }

    private boolean possuiCapacidadeDisponivel(Terminal terminal) {
        Capacidade capacidade = terminal.getCapacidade();

        if (capacidade == null) {
            return false;
        }

        return capacidade.possuiCapacidadeDisponivel();
    }

    private String montarMensagem(
            String terminalId,
            String tipoCarga,
            boolean ativo,
            boolean tipoCargaAceito,
            boolean capacidadeDisponivel,
            boolean terminalValido
    ) {
        if (terminalValido) {
            return "Terminal " + terminalId + " disponível para receber carga do tipo " + tipoCarga;
        }

        if (!ativo) {
            return "Terminal " + terminalId + " está inativo";
        }

        if (!tipoCargaAceito) {
            return "Terminal " + terminalId + " não aceita carga do tipo " + tipoCarga;
        }

        if (!capacidadeDisponivel) {
            return "Terminal " + terminalId + " não possui capacidade disponível";
        }

        return "Terminal " + terminalId + " inválido para a operação";
    }

}
