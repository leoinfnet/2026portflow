package br.com.infnet.containerService.client;

import br.com.infnet.containerService.dto.ValidacaoTerminalResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "${api.endpoints.terminal}")
public interface TerminalClient {
    @GetMapping("/terminais/{terminalId}/validacao")
    ValidacaoTerminalResponse validarTerminal(@PathVariable("terminalId") String terminal,
                                              @RequestParam("tipoCarga") String tipoCarga);
}
