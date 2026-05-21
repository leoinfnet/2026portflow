package br.com.infnet.containerService.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record ContainerStatusEvent(
        String eventId,
        String containerId,
        String statusAnterior,
        String statusAtual,
        String descricao,
        String origem,
        LocalDateTime dataHora,
        String correlationId
) {
    public static ContainerStatusEvent documentacaoPendente(String containerId){
        String correlationId = UUID.randomUUID().toString();
        return new ContainerStatusEvent(
                UUID.randomUUID().toString(),
                containerId,
                "CHEGOU",
                "DOCUMENTACAO_PENDENTE",
                "Container aguardando criação da documentação pela repartição pública",
                "container-service",
                LocalDateTime.now(),
                correlationId
        );
    }
}
