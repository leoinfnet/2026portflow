package br.com.infnet.containerService.service;

import br.com.infnet.containerService.domain.PortContainer;
import br.com.infnet.containerService.domain.StatusContainer;
import br.com.infnet.containerService.dto.ContainerArrivalRequest;
import br.com.infnet.containerService.dto.ValidacaoTerminalResponse;
import br.com.infnet.containerService.exception.TerminalValidationException;
import br.com.infnet.containerService.kafka.KafkaService;
import br.com.infnet.containerService.kafka.KafkaServiceImpl;
import br.com.infnet.containerService.repository.PortContainerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContainerService {
    private final PortContainerRepository repository;
    private final TerminalService service;
    private final KafkaService kafkaService;
    private final Logger log = LoggerFactory.getLogger(ContainerService.class);
    public PortContainer registerArrival(ContainerArrivalRequest request){
        PortContainer container = new PortContainer(
                request.containerId(),
                request.shipId(),
                request.terminalId(),
                request.originCountry(),
                request.destinationCountry(),
                request.cargoType(),
                StatusContainer.DOCUMENTACAO_PENDENTE,
                LocalDateTime.now()
        );
        ValidacaoTerminalResponse validacao = service.validarTerminal(request.terminalId(),
                request.cargoType());
        if(!validacao.terminalValido()){
            log.error("erro ao validar terminal {}", request);
            throw new TerminalValidationException(validacao.mensagem());
        }
        PortContainer saved = repository.save(container);
        kafkaService.sendDocumentacaoPendente(saved.getId());
        return  saved;
    }

    public List<PortContainer> findAll(){
        return repository.findAll();
    }
    public PortContainer findById(String id){
        return repository.findById(id)
                .orElseThrow( () -> new EntityNotFoundException("Container não localizado"));
    }
    public StatusContainer findStatusById(String containerId){
        return findById(containerId).getStatus();
    }
    public PortContainer updateStatus(String containerId, StatusContainer newStatus){
        PortContainer byId = findById(containerId);
        byId.setStatus(newStatus);
        return repository.save(byId);
    }

}
