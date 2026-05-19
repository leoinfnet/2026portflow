package br.com.infnet.terminalService.repository;

import br.com.infnet.terminalService.domain.Terminal;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TerminalRepository extends MongoRepository<Terminal,String> {
    Optional<Terminal> findByTerminalId(String s);
}
