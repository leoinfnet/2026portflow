CREATE SCHEMA IF NOT EXISTS auth_service;

CREATE TABLE IF NOT EXISTS auth_service.usuarios (
                                                     id UUID PRIMARY KEY,
                                                     nome VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    terminal_id VARCHAR(50),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS auth_service.usuario_roles (
                                                          usuario_id UUID NOT NULL,
                                                          role VARCHAR(80) NOT NULL,

    CONSTRAINT pk_usuario_roles PRIMARY KEY (usuario_id, role),

    CONSTRAINT fk_usuario_roles_usuario
    FOREIGN KEY (usuario_id)
    REFERENCES auth_service.usuarios(id)
    ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS auth_service.refresh_tokens (
                                                           id UUID PRIMARY KEY,
                                                           usuario_id UUID NOT NULL,
                                                           token_hash VARCHAR(255) NOT NULL,
    expira_em TIMESTAMP NOT NULL,
    revogado BOOLEAN NOT NULL DEFAULT FALSE,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ultimo_uso_em TIMESTAMP,

    CONSTRAINT fk_refresh_token_usuario
    FOREIGN KEY (usuario_id)
    REFERENCES auth_service.usuarios(id)
    ON DELETE CASCADE
    );

INSERT INTO auth_service.usuarios (
    id,
    nome,
    email,
    senha_hash,
    terminal_id,
    ativo
) VALUES
      (
          '11111111-1111-1111-1111-111111111111',
          'Ana Operadora',
          'ana@portflow.com',
          '123456',
          'T1',
          true
      ),
      (
          '22222222-2222-2222-2222-222222222222',
          'Carlos Admin',
          'admin@portflow.com',
          '123456',
          null,
          true
      )
    ON CONFLICT (email) DO NOTHING;

INSERT INTO auth_service.usuario_roles (
    usuario_id,
    role
) VALUES
      (
          '11111111-1111-1111-1111-111111111111',
          'OPERADOR_PORTUARIO'
      ),
      (
          '22222222-2222-2222-2222-222222222222',
          'ADMIN'
      )
    ON CONFLICT DO NOTHING;