package br.com.infnet.terminalService.service;

import br.com.infnet.terminalService.domain.Capacidade;
import br.com.infnet.terminalService.domain.Terminal;
import br.com.infnet.terminalService.dto.ValidacaoTerminalResponse;
import br.com.infnet.terminalService.execption.TerminalNotFoundException;
import br.com.infnet.terminalService.metrics.TerminalMetrics;
import br.com.infnet.terminalService.repository.TerminalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Term;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Service
public class TerminalService {


    private final TerminalMetrics terminalMetrics;


    private final TerminalRepository terminalRepository;

    public TerminalService(TerminalRepository terminalRepository,
                           TerminalMetrics terminalMetrics) {
        this.terminalRepository = terminalRepository;
        this.terminalMetrics = terminalMetrics;
    }

    public List<Terminal> findAll() {
        return terminalRepository.findAll();
    }

    public Terminal findByTerminalId(String terminalId) {
        return terminalRepository.findByTerminalId(terminalId)
                .orElseThrow(() -> new TerminalNotFoundException(terminalId));
    }



    public ValidacaoTerminalResponse validarTerminal(String terminalId, String tipoCarga) {
        return terminalMetrics.medirTempoValidacao(() -> executarValidacaoTerminal(terminalId, tipoCarga));
    }

    private ValidacaoTerminalResponse executarValidacaoTerminal(String terminalId, String tipoCarga) {
        terminalMetrics.incrementarValidacoesTotal();

        return terminalRepository.findByTerminalId(terminalId)
                .map(terminal -> validarTerminalExistente(terminal, tipoCarga))
                .orElseGet(() -> criarRespostaTerminalNaoEncontrado(terminalId));
    }
    private ValidacaoTerminalResponse criarRespostaTerminalNaoEncontrado(String terminalId) {
        terminalMetrics.incrementarValidacoesRecusadas();

        return new ValidacaoTerminalResponse(
                terminalId,
                false,
                false,
                false,
                false,
                false,
                "Terminal não encontrado"
        );
    }
//    public ValidacaoTerminalResponse validarTerminal(String terminalId, String tipoCarga) {
//        return terminalRepository.findByTerminalId(terminalId)
//                .map(terminal -> validarTerminalExistente(terminal, tipoCarga))
//                .orElseGet(() -> new ValidacaoTerminalResponse(
//                        terminalId,
//                        false,
//                        false,
//                        false,
//                        false,
//                        false,
//                        "Terminal não encontrado"
//                ));
//    }

    private ValidacaoTerminalResponse validarTerminalExistente(
            Terminal terminal,
            String tipoCarga
    ) {
        boolean ativo = terminal.isAtivo();
        boolean tipoCargaAceito = terminal.aceitaTipoCarga(tipoCarga);
        boolean capacidadeDisponivel = possuiCapacidadeDisponivel(terminal);

        boolean terminalValido = ativo && tipoCargaAceito && capacidadeDisponivel;
        registrarResultadoValidacao(terminalValido);
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

    private void registrarResultadoValidacao(boolean terminalValido) {
        terminalMetrics.incrementarValidacoesTotal();
        if (terminalValido) {
            terminalMetrics.incrementarValidacoesAprovadas();
        } else {
            terminalMetrics.incrementarValidacoesRecusadas();
        }
    }

}
