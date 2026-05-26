package br.com.infnet.terminalService.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Component
public class TerminalMetrics {
    private final Counter validacoesTotal;
    private final Counter validacoesAprovadas;
    private final Counter validacoesRecusadas;
    private final Timer validacaoDuration;

    public TerminalMetrics(MeterRegistry meterRegistry) {

    this.validacoesTotal = Counter.builder("portflow_terminal_validacoes_total")
            .description("Total de validacoes de terminal realizadas")
            .tag("service","terminal-service")
            .register(meterRegistry);

       this.validacoesAprovadas= Counter.builder("portflow_terminal_validacoes_aprovadas_total")
                .description("Total de validacoes de terminal aprovadas")
                .tag("service","terminal-service")
                .register(meterRegistry);

        this.validacoesRecusadas = Counter.builder("portflow_terminal_validacoes_recusadas_total")
                .description("Total de validações de terminal recusadas")
                .tag("service", "terminal-service")
                .register(meterRegistry);

        this.validacaoDuration = Timer.builder("portflow_terminal_validacao_duration")
                .description("Tempo gasto para validar um terminal")
                .tag("service", "terminal-service")
                .publishPercentileHistogram()
                .register(meterRegistry);
    }
    public void incrementarValidacoesTotal() {
        validacoesTotal.increment();
    }

    public void incrementarValidacoesAprovadas() {
        validacoesAprovadas.increment();
    }

    public void incrementarValidacoesRecusadas() {
        validacoesRecusadas.increment();
    }

    public <T> T medirTempoValidacao(java.util.function.Supplier<T> operacao) {
        return validacaoDuration.record(operacao);
    }
}
