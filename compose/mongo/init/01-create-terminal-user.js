db = db.getSiblingDB("terminal_service");

db.createUser({
    user: "terminal_service",
    pwd: "terminal_service",
    roles: [
        {
            role: "readWrite",
            db: "terminal_service"
        }
    ]
});

db.createCollection("terminais");

db.terminais.insertMany([
    {
        terminalId: "T1",
        nome: "Terminal Atlântico",
        ativo: true,
        tiposCargaAceitos: [
            "ELETRONICOS",
            "MAQUINAS",
            "TEXTIL"
        ],
        capacidade: {
            maximaContainers: 5000,
            ocupacaoAtual: 3120
        },
        zonas: [
            {
                codigo: "A1",
                tipo: "CARGA_GERAL",
                disponivel: true
            },
            {
                codigo: "R1",
                tipo: "REFRIGERADA",
                disponivel: false,
                motivoIndisponibilidade: "MANUTENCAO"
            }
        ],
        restricoes: {
            aceitaCargaPerigosa: false,
            aceitaCargaRefrigerada: true,
            alturaMaximaMetros: 4.5,
            pesoMaximoToneladas: 28
        },
        equipamentos: [
            "GUINDASTE",
            "EMPILHADEIRA",
            "SCANNER"
        ]
    },
    {
        terminalId: "T2",
        nome: "Terminal Pacífico",
        ativo: true,
        tiposCargaAceitos: [
            "ALIMENTOS",
            "REFRIGERADOS",
            "MEDICAMENTOS"
        ],
        capacidade: {
            maximaContainers: 3000,
            ocupacaoAtual: 2800
        },
        zonas: [
            {
                codigo: "F1",
                tipo: "REFRIGERADA",
                disponivel: true
            }
        ],
        restricoes: {
            aceitaCargaPerigosa: false,
            aceitaCargaRefrigerada: true,
            alturaMaximaMetros: 3.8,
            pesoMaximoToneladas: 22
        },
        equipamentos: [
            "CAMARA_FRIA",
            "EMPILHADEIRA",
            "SCANNER"
        ]
    },
    {
        terminalId: "T3",
        nome: "Terminal Químico",
        ativo: false,
        tiposCargaAceitos: [
            "QUIMICOS",
            "CARGA_PERIGOSA"
        ],
        capacidade: {
            maximaContainers: 1500,
            ocupacaoAtual: 900
        },
        zonas: [
            {
                codigo: "Q1",
                tipo: "CARGA_PERIGOSA",
                disponivel: false,
                motivoIndisponibilidade: "TERMINAL_INATIVO"
            }
        ],
        restricoes: {
            aceitaCargaPerigosa: true,
            aceitaCargaRefrigerada: false,
            alturaMaximaMetros: 4.0,
            pesoMaximoToneladas: 30
        },
        equipamentos: [
            "SENSOR_QUIMICO",
            "SCANNER",
            "GUINDASTE_ESPECIAL"
        ]
    }
]);