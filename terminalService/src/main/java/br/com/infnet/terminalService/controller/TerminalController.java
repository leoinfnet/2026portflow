package br.com.infnet.terminalService.controller;

import br.com.infnet.terminalService.dto.TerminalResponse;
import br.com.infnet.terminalService.dto.ValidacaoTerminalResponse;
import br.com.infnet.terminalService.service.TerminalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/terminais")
public class TerminalController {
    private final TerminalService terminalService;

    public TerminalController(TerminalService terminalService) {
        this.terminalService = terminalService;
    }



    @GetMapping
    public ResponseEntity<Iterable<TerminalResponse>> findAll() {
        Iterable<TerminalResponse> response = terminalService.findAll()
                .stream()
                .map(TerminalResponse::fromDomain)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{terminalId}")
    public ResponseEntity<TerminalResponse> findByTerminalId(
            @PathVariable("terminalId") String terminalId
    ) {
        return ResponseEntity.ok(
                TerminalResponse.fromDomain(
                        terminalService.findByTerminalId(terminalId)
                )
        );
    }

    @GetMapping("/{terminalId}/validacao")
    public ResponseEntity<ValidacaoTerminalResponse> validarTerminal(
            @PathVariable("terminalId") String terminalId,
            @RequestParam("tipoCarga")  String tipoCarga
    ) {
        return ResponseEntity.ok(
                terminalService.validarTerminal(terminalId, tipoCarga)
        );
    }
}
