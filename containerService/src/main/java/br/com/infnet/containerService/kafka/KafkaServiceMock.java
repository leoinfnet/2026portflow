package br.com.infnet.containerService.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile({"dev","default"})
@Service
public class KafkaServiceMock implements KafkaService {
    private final Logger logger = LoggerFactory.getLogger(KafkaServiceMock.class);
    @Override
    public void sendDocumentacaoPendente(String containerId) {
        logger.info("Enviando mensagem para o Kafka {}", containerId);
    }
}
