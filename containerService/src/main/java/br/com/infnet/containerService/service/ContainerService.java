package br.com.infnet.containerService.service;

import br.com.infnet.containerService.domain.PortContainer;
import br.com.infnet.containerService.domain.StatusContainer;
import br.com.infnet.containerService.dto.ContainerArrivalRequest;
import br.com.infnet.containerService.repository.PortContainerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContainerService {
    private final PortContainerRepository repository;

    public PortContainer registerArrival(ContainerArrivalRequest request){
        PortContainer container = new PortContainer(
                request.containerId(),
                request.shipId(),
                request.terminalId(),
                request.originCountry(),
                request.destinationCountry(),
                request.cargoType(),
                StatusContainer.CHEGOU,
                LocalDateTime.now()
        );

        return repository.save(container);

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
