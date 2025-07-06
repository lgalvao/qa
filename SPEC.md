# Sistema de Perguntas e Respostas

## Resumo
Aplicativo web para perguntas e respostas, sem autenticação, moderação ou votação. Foco em CRUD de perguntas, resolução de provas e geração de estatísticas, tudo em português brasileiro.

## Status da Implementação
✅ Backend Básico (Perguntas, Categorias, Respostas)
✅ Gerenciamento de Provas (criação, resposta e resultado)
✅ Estatísticas
🖥️ Frontend (não implementado)

## Funcionalidades Principais
- [x] Criar perguntas, incluindo alternativas de respostas (apenas uma correta)
- [x] Listar perguntas, com filtro por categoria e nível de dificuldade
- [x] Responder perguntas
- [x] Criar uma prova (seleção aleatória de perguntas por categoria e nível)
- [x] Armazenar respostas dadas na prova, incluindo erros e acertos
- [x] Gerar estatísticas de acertos e erros por prova e por categoria
- [x] Gerenciar categorias
- [ ] Listar provas
- [ ] Listar respostas de uma pergunta

## Modelos de Dados

### Pergunta
- id (Long)
- titulo (String)
- descricao (String)
- alternativas (List<Alternativa>)
- categoria (Categoria)
- nivelDificuldade (String)
- dataCriacao (LocalDateTime)

### Alternativa
- id (Long)
- texto (String)
- correta (boolean)
- pergunta (Pergunta)

### Resposta
- id (Long)
- pergunta (Pergunta)
- texto (String)
- correta (boolean)
- data (LocalDateTime)

### Prova
- id (Long)
- perguntas (List<Pergunta>)
- respostasDadas (List<RespostaDada>)
- categoria (Categoria)
- nivelDificuldade (String)
- acertos (Integer)
- erros (Integer)
- data (LocalDateTime)

### Categoria
- id (Long)
- nome (String)

### RespostaDada
- id (Long)
- pergunta (Pergunta)
- alternativaEscolhida (Alternativa)
- correta (boolean)

## Endpoints Implementados

### Perguntas
- `GET /perguntas` - Lista todas as perguntas
- `GET /perguntas/{id}` - Obtém uma pergunta por ID
- `POST /perguntas` - Cria uma nova pergunta
- `DELETE /perguntas/{id}` - Remove uma pergunta

### Categorias
- `GET /categorias` - Lista todas as categorias
- `POST /categorias` - Cria uma nova categoria

### Respostas
- `POST /perguntas/{id}/respostas` - Responde uma pergunta

### Provas
- `POST /api/provas` - Cria uma prova
- `POST /api/provas/respostas` - Responde uma prova
- `GET /api/provas/respostas/{provaId}/resultado` - Obtém o resultado de uma prova

### Estatísticas
- `GET /api/estatisticas` - Estatísticas gerais e por categoria

## Próximos Passos

### Backend
- [ ] Listar provas existentes (não prioritário para o protótipo)
- [ ] Listar respostas de uma pergunta (não prioritário)
- [ ] Melhorias: validação de entrada, tratamento de erros padronizado, documentação da API

### Testes
- Cobertura propositalmente mínima para protótipo. Testes básicos de endpoint e fluxo principal.

### Frontend (Futuro)
- Listagem de perguntas
- Formulário de criação de perguntas
- Interface para responder perguntas
- Visualização de provas e resultados

## Tecnologias Utilizadas
- Java 21+
- Spring Boot 3.x
- Spring Data JPA
- PostgreSQL
- JUnit 5
- MockMvc para testes de integração
- Lombok
- Gradle (com wrapper)

## Configuração
1. Banco de dados: PostgreSQL rodando em `localhost:5432/qa_db`
2. Usuário/senha padrão: postgres/postgres
3. Schema inicial: `src/main/resources/schema.sql`

## Executando os Testes
```bash
./gradlew test
```

## Executando a Aplicação
```bash
./gradlew bootRun
```
- Apenas um usuário (você)
- Sem internacionalização (apenas pt-BR)
