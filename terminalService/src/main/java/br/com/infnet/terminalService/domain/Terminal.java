package br.com.infnet.terminalService.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "terminais")
public class Terminal {
    @Id
    private String id;
    private String terminalId;
    private String nome;
    private boolean ativo;
    private List<String> tiposCargaAceitos;
    private Capacidade capacidade;
    private List<Zona> zonas;
    private Restricoes restricoes;
    private List<String> equipamentos;

    public String getId() {
        return id;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public String getNome() {
        return nome;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public List<String> getTiposCargaAceitos() {
        return tiposCargaAceitos;
    }

    public Capacidade getCapacidade() {
        return capacidade;
    }

    public List<Zona> getZonas() {
        return zonas;
    }

    public Restricoes getRestricoes() {
        return restricoes;
    }

    public List<String> getEquipamentos() {
        return equipamentos;
    }

    public boolean aceitaTipoCarga(String tipoCarga) {
        if (tiposCargaAceitos == null || tipoCarga == null) {
            return false;
        }

        return tiposCargaAceitos.contains(tipoCarga.toUpperCase());
    }

}
