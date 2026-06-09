package br.com.infnet.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "usuarios", schema = "auth_service")
public class Usuario {

    @Id
    private UUID id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "senha_hash", nullable = false)
    private String senhaHash;

    @Column(name = "terminal_id", length = 50)
    private String terminalId;

    @Column(nullable = false)
    private boolean ativo;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "usuario_roles",
            schema = "auth_service",
            joinColumns = @JoinColumn(name = "usuario_id")
    )
    @Column(name = "role")
    private Set<String> roles = new HashSet<>();

    protected Usuario() {
    }

    public Usuario(
            UUID id,
            String nome,
            String email,
            String senhaHash,
            String terminalId,
            boolean ativo,
            LocalDateTime criadoEm,
            Set<String> roles
    ) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senhaHash = senhaHash;
        this.terminalId = terminalId;
        this.ativo = ativo;
        this.criadoEm = criadoEm;
        this.roles = roles;
    }

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public Set<String> getRoles() {
        return roles;
    }
}