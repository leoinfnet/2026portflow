import json
import os
import threading
import queue
import time
import uuid
from datetime import datetime

from confluent_kafka import Consumer, Producer


BOOTSTRAP_SERVERS = os.getenv("KAFKA_BOOTSTRAP_SERVERS", "portflow-kafka:29092")

TOPICO_ENTRADA = os.getenv(
    "TOPICO_ENTRADA",
    "portflow.container.documentacao_pendente"
)

TOPICO_DOCUMENTACAO_LIBERADA = os.getenv(
    "TOPICO_DOCUMENTACAO_LIBERADA",
    "portflow.container.documentacao_liberada"
)

TOPICO_DOCUMENTACAO_RECUSADA = os.getenv(
    "TOPICO_DOCUMENTACAO_RECUSADA",
    "portflow.container.documentacao_recusada"
)

GRUPO_CONSUMER = os.getenv(
    "GRUPO_CONSUMER",
    "sistema-legado-documentacao-v1"
)

ORIGEM_SISTEMA = "sistema-legado-documentacao"

STATUS_DOCUMENTACAO_PENDENTE = "DOCUMENTACAO_PENDENTE"
STATUS_DOCUMENTACAO_LIBERADA = "DOCUMENTACAO_LIBERADA"
STATUS_DOCUMENTACAO_RECUSADA = "DOCUMENTACAO_RECUSADA"

eventos_recebidos = queue.Queue()


def limpar_tela():
    os.system("cls" if os.name == "nt" else "clear")


def linha():
    print("=" * 72)


def cabecalho():
    limpar_tela()
    linha()
    print("        SISTEMA FEDERAL DE DOCUMENTACAO PORTUARIA - SFDOC 1987")
    print("        MODULO: ANALISE DE DOCUMENTACAO DE CONTAINERS")
    linha()
    print(f"        KAFKA.............: {BOOTSTRAP_SERVERS}")
    print(f"        TOPICO IN.........: {TOPICO_ENTRADA}")
    print(f"        TOPICO LIBERADA...: {TOPICO_DOCUMENTACAO_LIBERADA}")
    print(f"        TOPICO RECUSADA...: {TOPICO_DOCUMENTACAO_RECUSADA}")
    print(f"        GRUPO.............: {GRUPO_CONSUMER}")
    linha()


def tela_aguardando():
    cabecalho()
    print()
    print("        STATUS DO SISTEMA")
    print()
    print("        >>> AGUARDANDO DOCUMENTACOES PENDENTES <<<")
    print()
    print("        Este sistema simula uma aplicacao legada de uma")
    print("        reparticao publica responsavel pela analise documental.")
    print()
    print("        Quando um evento chegar no topico documentacao_pendente,")
    print("        o operador devera liberar ou recusar a documentacao.")
    print()
    linha()
    print("        Pressione CTRL+C para encerrar.")
    linha()


def obter_codigo_container(evento):
    return evento.get("codigoContainer", evento.get("containerId", "-"))


def obter_data_evento(evento):
    return evento.get("dataHora", evento.get("dataEvento", "-"))


def tela_container(evento):
    cabecalho()
    print()
    print("        NOVA DOCUMENTACAO RECEBIDA PARA ANALISE")
    linha()
    print(f"        EVENT ID.........: {evento.get('eventId', '-')}")
    print(f"        CONTAINER ID.....: {evento.get('containerId', '-')}")
    print(f"        CODIGO CONTAINER.: {obter_codigo_container(evento)}")
    print(f"        TERMINAL.........: {evento.get('terminalId', '-')}")
    print(f"        TIPO DE CARGA....: {evento.get('cargoType', '-')}")
    print(f"        STATUS ANTERIOR..: {evento.get('statusAnterior', '-')}")
    print(f"        STATUS ATUAL.....: {evento.get('statusAtual', '-')}")
    print(f"        ORIGEM...........: {evento.get('origem', '-')}")
    print(f"        DATA DO EVENTO...: {obter_data_evento(evento)}")
    print(f"        CORRELATION ID...: {evento.get('correlationId', '-')}")
    linha()
    print()
    print("        MENU DE OPERACOES")
    print()
    print("        1 - LIBERAR DOCUMENTACAO")
    print("        2 - RECUSAR DOCUMENTACAO")
    print("        3 - IGNORAR POR ENQUANTO")
    print()
    linha()


def tela_evento_ignorado(evento):
    status_atual = evento.get("statusAtual", "-")
    origem = evento.get("origem", "-")
    container_id = evento.get("containerId", "-")

    print()
    print("        EVENTO RECEBIDO, MAS IGNORADO PELO SFDOC")
    print(f"        CONTAINER.: {container_id}")
    print(f"        STATUS....: {status_atual}")
    print(f"        ORIGEM....: {origem}")
    time.sleep(2)


def criar_consumer():
    return Consumer({
        "bootstrap.servers": BOOTSTRAP_SERVERS,
        "group.id": GRUPO_CONSUMER,
        "auto.offset.reset": "earliest",
        "enable.auto.commit": True
    })


def criar_producer():
    return Producer({
        "bootstrap.servers": BOOTSTRAP_SERVERS
    })


def consumir_eventos():
    consumer = criar_consumer()
    consumer.subscribe([TOPICO_ENTRADA])

    print("Consumer iniciado.", flush=True)
    print(f"Bootstrap servers: {BOOTSTRAP_SERVERS}", flush=True)
    print(f"Tópico de entrada: {TOPICO_ENTRADA}", flush=True)
    print(f"Grupo: {GRUPO_CONSUMER}", flush=True)

    while True:
        msg = consumer.poll(1.0)

        if msg is None:
            continue

        if msg.error():
            eventos_recebidos.put({
                "tipo": "erro",
                "mensagem": str(msg.error())
            })
            continue

        try:
            evento = json.loads(msg.value().decode("utf-8"))

            print("=" * 72, flush=True)
            print("EVENTO RECEBIDO DO KAFKA", flush=True)
            print(f"Tópico....: {msg.topic()}", flush=True)
            print(f"Partição..: {msg.partition()}", flush=True)
            print(f"Offset....: {msg.offset()}", flush=True)
            print(evento, flush=True)
            print("=" * 72, flush=True)

            eventos_recebidos.put({
                "tipo": "evento",
                "payload": evento
            })

        except Exception as erro:
            eventos_recebidos.put({
                "tipo": "erro",
                "mensagem": str(erro)
            })


def montar_evento_documentacao(evento_original, status_atual, descricao, motivo=None):
    container_id = evento_original.get("containerId")
    codigo_container = evento_original.get("codigoContainer", container_id)

    evento_saida = {
        "eventId": str(uuid.uuid4()),
        "containerId": container_id,
        "codigoContainer": codigo_container,
        "terminalId": evento_original.get("terminalId"),
        "cargoType": evento_original.get("cargoType"),
        "statusAnterior": STATUS_DOCUMENTACAO_PENDENTE,
        "statusAtual": status_atual,
        "descricao": descricao,
        "origem": ORIGEM_SISTEMA,
        "dataHora": datetime.now().isoformat(),
        "correlationId": evento_original.get("correlationId", str(uuid.uuid4()))
    }

    if motivo is not None:
        evento_saida["motivo"] = motivo

    return evento_saida


def publicar_evento(topico, evento_saida):
    producer = criar_producer()

    producer.produce(
        topico,
        key=str(evento_saida["containerId"]),
        value=json.dumps(evento_saida).encode("utf-8")
    )

    producer.flush()


def publicar_documentacao_liberada(evento_original):
    evento_saida = montar_evento_documentacao(
        evento_original=evento_original,
        status_atual=STATUS_DOCUMENTACAO_LIBERADA,
        descricao="Documentação liberada manualmente pela repartição pública",
        motivo=None
    )

    publicar_evento(TOPICO_DOCUMENTACAO_LIBERADA, evento_saida)

    return evento_saida


def publicar_documentacao_recusada(evento_original, motivo):
    evento_saida = montar_evento_documentacao(
        evento_original=evento_original,
        status_atual=STATUS_DOCUMENTACAO_RECUSADA,
        descricao="Documentação recusada manualmente pela repartição pública",
        motivo=motivo
    )

    publicar_evento(TOPICO_DOCUMENTACAO_RECUSADA, evento_saida)

    return evento_saida


def exibir_resultado(evento_saida, topico_publicado):
    print()
    linha()

    if evento_saida.get("statusAtual") == STATUS_DOCUMENTACAO_LIBERADA:
        print("        DOCUMENTACAO LIBERADA COM SUCESSO")
    else:
        print("        DOCUMENTACAO RECUSADA COM SUCESSO")

    linha()
    print(f"        CONTAINER........: {evento_saida.get('codigoContainer')}")
    print(f"        NOVO STATUS......: {evento_saida.get('statusAtual')}")
    print(f"        EVENTO PUBLICADO.: {topico_publicado}")

    if evento_saida.get("motivo"):
        print(f"        MOTIVO...........: {evento_saida.get('motivo')}")

    print(f"        CORRELATION ID...: {evento_saida.get('correlationId')}")
    linha()
    input("        PRESSIONE ENTER PARA VOLTAR AO MODO DE ESPERA...")


def processar_container(evento):
    while True:
        tela_container(evento)
        opcao = input("        DIGITE A OPCAO DESEJADA: ").strip()

        if opcao == "1":
            cabecalho()
            print()
            print("        ANALISANDO DOCUMENTACAO...")
            time.sleep(1)

            print("        VALIDANDO ASSINATURAS E CARIMBOS...")
            time.sleep(1)

            print("        REGISTRANDO LIBERACAO NO SISTEMA LEGADO...")
            time.sleep(1)

            evento_saida = publicar_documentacao_liberada(evento)

            exibir_resultado(
                evento_saida=evento_saida,
                topico_publicado=TOPICO_DOCUMENTACAO_LIBERADA
            )
            return

        if opcao == "2":
            cabecalho()
            print()
            print("        RECUSA DE DOCUMENTACAO")
            linha()
            print("        Informe o motivo da recusa.")
            print("        Exemplo: Documento sem assinatura do fiscal.")
            linha()
            print()

            motivo = input("        MOTIVO DA RECUSA: ").strip()

            while not motivo:
                print()
                print("        O motivo da recusa e obrigatorio.")
                motivo = input("        MOTIVO DA RECUSA: ").strip()

            cabecalho()
            print()
            print("        ANALISANDO DOCUMENTACAO...")
            time.sleep(1)

            print("        REGISTRANDO RECUSA NO SISTEMA LEGADO...")
            time.sleep(1)

            print("        ENVIANDO EVENTO AO BARRAMENTO CENTRAL...")
            time.sleep(1)

            evento_saida = publicar_documentacao_recusada(evento, motivo)

            exibir_resultado(
                evento_saida=evento_saida,
                topico_publicado=TOPICO_DOCUMENTACAO_RECUSADA
            )
            return

        if opcao == "3":
            print()
            print("        OPERACAO CANCELADA PELO USUARIO.")
            time.sleep(1)
            return

        print()
        print("        OPCAO INVALIDA.")
        time.sleep(1)


def deve_processar_evento(evento):
    status_atual = evento.get("statusAtual")

    return status_atual == STATUS_DOCUMENTACAO_PENDENTE


def main():
    thread_consumer = threading.Thread(
        target=consumir_eventos,
        daemon=True
    )
    thread_consumer.start()

    tela_aguardando()

    try:
        while True:
            try:
                item = eventos_recebidos.get(timeout=1)
            except queue.Empty:
                continue

            if item["tipo"] == "erro":
                cabecalho()
                print()
                print("        ERRO AO CONSUMIR EVENTO")
                print()
                print(f"        {item['mensagem']}")
                print()
                input("        PRESSIONE ENTER PARA CONTINUAR...")
                tela_aguardando()
                continue

            evento = item["payload"]

            status_atual = evento.get("statusAtual", "-")
            origem = evento.get("origem", "-")

            print(
                f"EVENTO ANALISADO PELO SFDOC | "
                f"STATUS: {status_atual} | "
                f"ORIGEM: {origem}",
                flush=True
            )

            if not deve_processar_evento(evento):
                tela_evento_ignorado(evento)
                tela_aguardando()
                continue

            processar_container(evento)
            tela_aguardando()

    except KeyboardInterrupt:
        cabecalho()
        print()
        print("        SISTEMA ENCERRADO PELO OPERADOR.")
        print()


if __name__ == "__main__":
    main()