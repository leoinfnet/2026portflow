package br.com.infnet.containerService.repository;

import br.com.infnet.containerService.domain.PortContainer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortContainerRepository extends JpaRepository<PortContainer,String> {
}
