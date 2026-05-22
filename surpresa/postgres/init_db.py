import os
import random
import uuid
from datetime import datetime, timedelta
from decimal import Decimal

import psycopg2


DB_HOST = os.getenv("DB_HOST", "localhost")
DB_PORT = os.getenv("DB_PORT", "5433")
DB_NAME = os.getenv("DB_NAME", "portflow_legado")
DB_USER = os.getenv("DB_USER", "portflow")
DB_PASSWORD = os.getenv("DB_PASSWORD", "portflow")


TIPOS_EVENTO = [
    "DIVERGENCIA_PESO",
    "CAMINHAO_BLOQUEADO",
    "CARGA_SENSIVEL_COM_RISCO",
    "NOTA_FISCAL_REUTILIZADA",
    "MOTORISTA_COM_MULTIPLAS_OCORRENCIAS",
]

TIPOS_CARGA = [
    "ELETRONICOS",
    "MEDICAMENTOS",
    "QUIMICOS",
    "ALIMENTOS",
    "AUTOPECAS",
    "TEXTIL",
    "GRAOS",
]

MOTORISTAS = [
    ("12345678901", "Carlos Mendes"),
    ("23456789012", "Rafael Oliveira"),
    ("34567890123", "Bruno Almeida"),
    ("45678901234", "Marcelo Rocha"),
    ("56789012345", "Eduardo Nascimento"),
    ("67890123456", "João Pereira"),
    ("78901234567", "André Martins"),
    ("89012345678", "Felipe Barros"),
    ("90123456789", "Lucas Ferreira"),
    ("01234567890", "Paulo Azevedo"),
]

TRANSPORTADORAS = [
    ("11222333000144", "Transportadora Atlantica"),
    ("22333444000155", "Logistica Sul Express"),
    ("33444555000166", "Carga Forte Transportes"),
    ("44555666000177", "Mar Azul Logistica"),
    ("55666777000188", "Rota Portuaria LTDA"),
    ("66777888000199", "Continental Cargo"),
]

GATES = [
    "GATE-01",
    "GATE-02",
    "GATE-03",
    "GATE-04",
]


def conectar():
    return psycopg2.connect(
        host=DB_HOST,
        port=DB_PORT,
        dbname=DB_NAME,
        user=DB_USER,
        password=DB_PASSWORD,
    )


def criar_tabela(conn):
    sql = """
        CREATE TABLE IF NOT EXISTS gate_evento_legado (
            id UUID PRIMARY KEY,

            tipo_evento VARCHAR(80) NOT NULL,

            placa_caminhao VARCHAR(20) NOT NULL,
            cpf_motorista VARCHAR(14) NOT NULL,
            nome_motorista VARCHAR(120) NOT NULL,

            cnpj_transportadora VARCHAR(18) NOT NULL,
            nome_transportadora VARCHAR(120) NOT NULL,

            numero_nota_fiscal VARCHAR(40) NOT NULL,
            tipo_carga VARCHAR(60) NOT NULL,

            peso_declarado NUMERIC(10, 2),
            peso_aferido NUMERIC(10, 2),
            diferenca_peso NUMERIC(10, 2),

            status_gate VARCHAR(60) NOT NULL,
            status_vistoria VARCHAR(60) NOT NULL,

            risco VARCHAR(30) NOT NULL,
            motivo TEXT,

            gate VARCHAR(30) NOT NULL,

            data_evento TIMESTAMP NOT NULL,
            origem VARCHAR(80) NOT NULL
        );
    """

    with conn.cursor() as cursor:
        cursor.execute(sql)

    conn.commit()


def criar_indices(conn):
    sql = """
        CREATE INDEX IF NOT EXISTS idx_gate_evento_legado_tipo_evento
        ON gate_evento_legado(tipo_evento);

        CREATE INDEX IF NOT EXISTS idx_gate_evento_legado_placa
        ON gate_evento_legado(placa_caminhao);

        CREATE INDEX IF NOT EXISTS idx_gate_evento_legado_cpf_motorista
        ON gate_evento_legado(cpf_motorista);

        CREATE INDEX IF NOT EXISTS idx_gate_evento_legado_transportadora
        ON gate_evento_legado(cnpj_transportadora);

        CREATE INDEX IF NOT EXISTS idx_gate_evento_legado_nota_fiscal
        ON gate_evento_legado(numero_nota_fiscal);

        CREATE INDEX IF NOT EXISTS idx_gate_evento_legado_data_evento
        ON gate_evento_legado(data_evento);
    """

    with conn.cursor() as cursor:
        cursor.execute(sql)

    conn.commit()


def limpar_tabela(conn):
    with conn.cursor() as cursor:
        cursor.execute("TRUNCATE TABLE gate_evento_legado;")

    conn.commit()


def gerar_placa():
    letras = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

    return (
        random.choice(letras)
        + random.choice(letras)
        + random.choice(letras)
        + "-"
        + str(random.randint(1000, 9999))
    )


def gerar_nota_fiscal():
    return f"NF-{random.randint(100000, 999999)}"


def gerar_data_evento():
    agora = datetime.now()
    dias_atras = random.randint(0, 30)
    horas_atras = random.randint(0, 23)
    minutos_atras = random.randint(0, 59)

    return agora - timedelta(
        days=dias_atras,
        hours=horas_atras,
        minutes=minutos_atras,
    )


def gerar_peso_declarado():
    return Decimal(str(round(random.uniform(18000, 30000), 2)))


def gerar_evento():
    tipo_evento = random.choice(TIPOS_EVENTO)

    cpf_motorista, nome_motorista = random.choice(MOTORISTAS)
    cnpj_transportadora, nome_transportadora = random.choice(TRANSPORTADORAS)

    tipo_carga = random.choice(TIPOS_CARGA)
    peso_declarado = gerar_peso_declarado()

    if tipo_evento == "DIVERGENCIA_PESO":
        peso_aferido = peso_declarado + Decimal(random.randint(1500, 6000))
        status_gate = "DIVERGENCIA_PESO"
        status_vistoria = "APROVADA_COM_OBSERVACAO"
        risco = "MEDIO"
        motivo = "Peso aferido diferente do peso declarado no agendamento."

    elif tipo_evento == "CAMINHAO_BLOQUEADO":
        peso_aferido = peso_declarado + Decimal(random.randint(-500, 500))
        status_gate = "BLOQUEADO"
        status_vistoria = "REPROVADA"
        risco = "ALTO"
        motivo = "Caminhão bloqueado durante vistoria operacional."

    elif tipo_evento == "CARGA_SENSIVEL_COM_RISCO":
        tipo_carga = random.choice(["ELETRONICOS", "MEDICAMENTOS", "QUIMICOS"])
        peso_aferido = peso_declarado + Decimal(random.randint(1000, 4500))
        status_gate = "LIBERADO_COM_ALERTA"
        status_vistoria = "APROVADA_COM_OBSERVACAO"
        risco = "ALTO"
        motivo = "Carga sensível associada a divergência operacional."

    elif tipo_evento == "NOTA_FISCAL_REUTILIZADA":
        peso_aferido = peso_declarado + Decimal(random.randint(-300, 300))
        status_gate = "LIBERADO_COM_ALERTA"
        status_vistoria = "APROVADA_COM_OBSERVACAO"
        risco = "ALTO"
        motivo = "Nota fiscal reutilizada em mais de um agendamento."

    elif tipo_evento == "MOTORISTA_COM_MULTIPLAS_OCORRENCIAS":
        peso_aferido = peso_declarado + Decimal(random.randint(500, 2500))
        status_gate = "LIBERADO_COM_ALERTA"
        status_vistoria = "APROVADA_COM_OBSERVACAO"
        risco = "MEDIO"
        motivo = "Motorista associado a múltiplas ocorrências no gate."

    else:
        peso_aferido = peso_declarado
        status_gate = "LIBERADO"
        status_vistoria = "APROVADA"
        risco = "BAIXO"
        motivo = "Evento operacional comum."

    diferenca_peso = peso_aferido - peso_declarado

    return {
        "id": str(uuid.uuid4()),
        "tipo_evento": tipo_evento,
        "placa_caminhao": gerar_placa(),
        "cpf_motorista": cpf_motorista,
        "nome_motorista": nome_motorista,
        "cnpj_transportadora": cnpj_transportadora,
        "nome_transportadora": nome_transportadora,
        "numero_nota_fiscal": gerar_nota_fiscal(),
        "tipo_carga": tipo_carga,
        "peso_declarado": peso_declarado,
        "peso_aferido": peso_aferido,
        "diferenca_peso": diferenca_peso,
        "status_gate": status_gate,
        "status_vistoria": status_vistoria,
        "risco": risco,
        "motivo": motivo,
        "gate": random.choice(GATES),
        "data_evento": gerar_data_evento(),
        "origem": "carga-inicial-python",
    }


def inserir_evento(conn, evento):
    sql = """
        INSERT INTO gate_evento_legado (
            id,
            tipo_evento,
            placa_caminhao,
            cpf_motorista,
            nome_motorista,
            cnpj_transportadora,
            nome_transportadora,
            numero_nota_fiscal,
            tipo_carga,
            peso_declarado,
            peso_aferido,
            diferenca_peso,
            status_gate,
            status_vistoria,
            risco,
            motivo,
            gate,
            data_evento,
            origem
        ) VALUES (
            %(id)s,
            %(tipo_evento)s,
            %(placa_caminhao)s,
            %(cpf_motorista)s,
            %(nome_motorista)s,
            %(cnpj_transportadora)s,
            %(nome_transportadora)s,
            %(numero_nota_fiscal)s,
            %(tipo_carga)s,
            %(peso_declarado)s,
            %(peso_aferido)s,
            %(diferenca_peso)s,
            %(status_gate)s,
            %(status_vistoria)s,
            %(risco)s,
            %(motivo)s,
            %(gate)s,
            %(data_evento)s,
            %(origem)s
        );
    """

    with conn.cursor() as cursor:
        cursor.execute(sql, evento)


def popular_eventos(conn, quantidade=1000):
    for indice in range(quantidade):
        evento = gerar_evento()
        inserir_evento(conn, evento)

        if (indice + 1) % 100 == 0:
            print(f"{indice + 1} eventos inseridos...")

    conn.commit()


def contar_eventos(conn):
    with conn.cursor() as cursor:
        cursor.execute("SELECT COUNT(*) FROM gate_evento_legado;")
        return cursor.fetchone()[0]


def exibir_resumo(conn):
    with conn.cursor() as cursor:
        cursor.execute(
            """
            SELECT tipo_evento, COUNT(*) AS total
            FROM gate_evento_legado
            GROUP BY tipo_evento
            ORDER BY total DESC;
            """
        )

        linhas = cursor.fetchall()

    print()
    print("Resumo dos eventos gerados:")
    print("-" * 50)

    for tipo_evento, total in linhas:
        print(f"{tipo_evento}: {total}")

    print("-" * 50)
    print(f"Total geral: {contar_eventos(conn)}")


def main():
    print("Inicializando banco legado do PortFlow...")
    print(f"Conectando em {DB_HOST}:{DB_PORT}/{DB_NAME}")

    conn = conectar()

    try:
        criar_tabela(conn)
        criar_indices(conn)

        print("Tabela criada com sucesso.")
        print("Limpando dados antigos...")
        limpar_tabela(conn)

        print("Populando tabela com 1000 eventos legados...")
        popular_eventos(conn, quantidade=1000)

        exibir_resumo(conn)

        print()
        print("Banco legado inicializado com sucesso.")

    except Exception as erro:
        conn.rollback()
        print("Erro ao inicializar banco legado.")
        print(erro)

    finally:
        conn.close()


if __name__ == "__main__":
    main()