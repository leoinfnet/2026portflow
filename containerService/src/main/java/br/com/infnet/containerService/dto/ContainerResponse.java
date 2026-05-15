package br.com.infnet.containerService.dto;

import br.com.infnet.containerService.domain.PortContainer;
import br.com.infnet.containerService.domain.StatusContainer;

import java.time.LocalDateTime;

public record ContainerResponse(
        String containerId,
        String shipId,
        String terminalId,
        String originCountry,
        String destinationCountry,
        String cargoType,
        StatusContainer status,
        LocalDateTime arrivalDate
) {
    public static ContainerResponse fromDomain(PortContainer container){
        return new ContainerResponse(
                container.getId(),
                container.getShipId(),
                container.getTerminalId(),
                container.getOriginCountry(),
                container.getDestinationCountry(),
                container.getCargoType(),
                container.getStatus(),
                container.getArrivalDate()
        );
    }
}
