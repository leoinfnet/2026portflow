package br.com.infnet.containerService.kafka;

import br.com.infnet.containerService.events.ContainerStatusEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaTerminalListener {
//    @KafkaListener(topics = "portflow.container.documentacao_liberada")
//    public void receberDocumentacaoLiberada(ContainerStatusEvent event){
//        System.out.println("Evento recebido");
//        System.out.println(event);
//    }
//    @KafkaListener(topics = "portflow.container.documentacao_recusada")
//    public void receberDocumentacaoRecusada(ContainerStatusEvent event){
//        System.out.println("Evento recebido");
//        System.out.println(event);
//    }

}
