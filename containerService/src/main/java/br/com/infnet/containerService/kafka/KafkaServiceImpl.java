package br.com.infnet.containerService.kafka;

import br.com.infnet.containerService.events.ContainerStatusEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Profile("prod")
public class KafkaServiceImpl implements KafkaService {
    private final KafkaTemplate<String, ContainerStatusEvent> kafkaTemplate;

    private void sendEvent(ContainerStatusEvent event){
        kafkaTemplate.send("portflow.container.documentacao_pendente"
                ,event.containerId(),
                event);
    }
    public void sendDocumentacaoPendente(String containerId){
        ContainerStatusEvent containerStatusEvent = ContainerStatusEvent.documentacaoPendente(containerId);
        sendEvent(containerStatusEvent);
    }

}
