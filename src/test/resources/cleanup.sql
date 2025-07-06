-- Desabilita as restrições de chave estrangeira temporariamente
SET session_replication_role = 'replica';

-- Limpa as tabelas
TRUNCATE TABLE resposta_dada CASCADE;
TRUNCATE TABLE alternativa CASCADE;
TRUNCATE TABLE pergunta CASCADE;
TRUNCATE TABLE prova_perguntas CASCADE;
TRUNCATE TABLE prova CASCADE;
TRUNCATE TABLE categoria CASCADE;

-- Reabilita as restrições de chave estrangeira
SET session_replication_role = 'origin';
