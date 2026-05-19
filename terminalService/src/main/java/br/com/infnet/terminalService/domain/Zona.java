package br.com.infnet.terminalService.domain;

public class Zona {
    private String codigo;

    private String tipo;

    private boolean disponivel;

    private String motivoIndisponibilidade;

    public String getCodigo() {
        return codigo;
    }

    public String getTipo() {
        return tipo;
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    public String getMotivoIndisponibilidade() {
        return motivoIndisponibilidade;
    }
}
