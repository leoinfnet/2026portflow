package br.com.infnet.containerService.controller;

import br.com.infnet.containerService.domain.PortContainer;
import br.com.infnet.containerService.dto.ContainerArrivalRequest;
import br.com.infnet.containerService.dto.ContainerResponse;
import br.com.infnet.containerService.service.ContainerService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class ContainerController {
    private final ContainerService service;
    private final Logger log =  LoggerFactory.getLogger(ContainerController.class);
    @PostMapping("/arrival")
    public ResponseEntity<ContainerResponse> registerArrival(@RequestBody
                                ContainerArrivalRequest request){
        log.info("Iniciando registro de chegada de container {}", request );
        PortContainer portContainer = service.registerArrival(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ContainerResponse.fromDomain(portContainer));
    }
    @GetMapping
    public ResponseEntity<List<ContainerResponse>> findAll(){
        List<ContainerResponse> list = service.findAll()
                .stream()
                .map(ContainerResponse::fromDomain)
                .toList();

        return ResponseEntity.ok(list);
    }
    @GetMapping("{containerId}")
    public ResponseEntity<ContainerResponse> findById(
            @PathVariable("containerId") String containerId){
        PortContainer byId = service.findById(containerId);
        return ResponseEntity.ok(ContainerResponse.fromDomain(byId));

    }

}
