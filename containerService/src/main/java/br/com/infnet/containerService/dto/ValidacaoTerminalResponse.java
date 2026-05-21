package br.com.infnet.containerService.dto;

public record ValidacaoTerminalResponse(
        String terminalId,
        boolean existe,
        boolean ativo,
        boolean tipoCargaAceito,
        boolean capacidadeDisponivel,
        boolean terminalValido,
        String mensagem

) {
}
