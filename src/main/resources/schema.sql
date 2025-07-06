CREATE TABLE IF NOT EXISTS categoria (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS pergunta (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(255),
    descricao VARCHAR(1024),
    categoria_id BIGINT REFERENCES categoria(id),
    nivel_dificuldade VARCHAR(64),
    data_criacao TIMESTAMP
);

CREATE TABLE IF NOT EXISTS alternativa (
    id BIGSERIAL PRIMARY KEY,
    texto VARCHAR(255),
    correta BOOLEAN,
    pergunta_id BIGINT REFERENCES pergunta(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS resposta (
    id BIGSERIAL PRIMARY KEY,
    pergunta_id BIGINT REFERENCES pergunta(id) ON DELETE CASCADE,
    texto VARCHAR(255),
    correta BOOLEAN,
    data TIMESTAMP
);

CREATE TABLE IF NOT EXISTS prova (
    id BIGSERIAL PRIMARY KEY,
    categoria_id BIGINT REFERENCES categoria(id),
    nivel_dificuldade VARCHAR(64),
    acertos INTEGER,
    erros INTEGER,
    data TIMESTAMP
);

CREATE TABLE IF NOT EXISTS resposta_dada (
    id BIGSERIAL PRIMARY KEY,
    pergunta_id BIGINT,
    alternativa_escolhida BIGINT,
    correta BOOLEAN
);

CREATE TABLE IF NOT EXISTS prova_perguntas (
    prova_id BIGINT REFERENCES prova(id) ON DELETE CASCADE,
    pergunta_id BIGINT REFERENCES pergunta(id) ON DELETE CASCADE
);
