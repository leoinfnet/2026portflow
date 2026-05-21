package br.com.infnet.containerService;

import br.com.infnet.containerService.client.TerminalClient;
import br.com.infnet.containerService.dto.ValidacaoTerminalResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestaClient {
    @Autowired
    private TerminalClient client;
    @Test
    void deveTestaroClient(){
        ValidacaoTerminalResponse validacaoTerminalResponse = client.validarTerminal("T1", "ELETRONICOS");
        System.out.println(validacaoTerminalResponse);

    }
}
