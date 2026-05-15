package br.com.infnet.containerService.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name= "containers", schema = "container_service")
@Getter@Setter
public class PortContainer {
    @Id
    private String id;
    private String shipId;
    private String terminalId;
    private String originCountry;
    private String destinationCountry;
    private String cargoType;
    @Enumerated(EnumType.STRING)
    private StatusContainer status;
    private LocalDateTime arrivalDate;

    protected PortContainer() {
    }
    public PortContainer(
            String containerId,
            String shipId,
            String terminalId,
            String originCountry,
            String destinationCountry,
            String cargoType,
            StatusContainer status,
            LocalDateTime arrivalDate
    ) {
        this.id = containerId;
        this.shipId = shipId;
        this.terminalId = terminalId;
        this.originCountry = originCountry;
        this.destinationCountry = destinationCountry;
        this.cargoType = cargoType;
        this.status = status;
        this.arrivalDate = arrivalDate;
    }


}
