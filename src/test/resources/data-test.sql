-- Limpa todas as tabelas em ordem segura (do mais dependente para o menos dependente)
TRUNCATE TABLE resposta_dada CASCADE;
TRUNCATE TABLE resposta CASCADE;
TRUNCATE TABLE alternativa CASCADE;
TRUNCATE TABLE prova_perguntas CASCADE;
TRUNCATE TABLE pergunta CASCADE;
TRUNCATE TABLE categoria CASCADE;
