package br.com.infnet.containerService.dto;

public record ContainerArrivalRequest(
        String containerId,
        String shipId,
        String terminalId,
        String originCountry,
        String destinationCountry,
        String cargoType) {
}
