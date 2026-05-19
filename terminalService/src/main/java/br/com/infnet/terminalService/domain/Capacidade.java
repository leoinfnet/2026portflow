package br.com.infnet.terminalService.domain;

public class Capacidade {
    private Integer maximaContainers;

    private Integer ocupacaoAtual;

    public Integer getMaximaContainers() {
        return maximaContainers;
    }

    public Integer getOcupacaoAtual() {
        return ocupacaoAtual;
    }

    public boolean possuiCapacidadeDisponivel() {
        if (maximaContainers == null || ocupacaoAtual == null) {
            return false;
        }

        return ocupacaoAtual < maximaContainers;
    }
}
