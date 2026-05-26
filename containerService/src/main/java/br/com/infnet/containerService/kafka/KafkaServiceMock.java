package br.com.infnet.containerService.kafka;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile({"dev","default"})
@Service
public class KafkaServiceMock implements KafkaService {
    @Override
    public void sendDocumentacaoPendente(String containerId) {
        System.out.println("Enviando mensagem para o Kafka");
    }
}
