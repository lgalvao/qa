# Regras do Projeto Q&A

## 1. Convenções de Código
- Todo o código deve estar em português (classes, métodos, variáveis, mensagens)
- Usar nomenclatura em português para tabelas e colunas do banco de dados
- Seguir o padrão camelCase para nomes de variáveis e métodos
- Usar classes de domínio enxutas com Lombok (`@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`)
- Manter construtores protegidos para entidades JPA

## 2. Estrutura do Projeto
```
src/
├── main/
│   ├── java/lg/qa/
│   │   ├── config/       # Configurações do Spring
│   │   ├── controller/   # Controladores REST
│   │   ├── dto/          # Objetos de Transferência de Dados
│   │   ├── exception/    # Tratamento de exceções
│   │   ├── model/        # Entidades JPA
│   │   ├── repository/   # Repositórios Spring Data
│   │   └── service/      # Lógica de negócios
│   └── resources/
│       └── application.yml
└── test/                 # Testes unitários e de integração
```

## 3. Banco de Dados
- Usar PostgreSQL como banco de dados principal
- Configuração do Hibernate com `ddl-auto: update` em desenvolvimento
- Nomes de tabelas em minúsculas com underscore (snake_case)
- Definir explicitamente os relacionamentos JPA
- Usar `@CreationTimestamp` para campos de data de criação
- Evitar o uso de chaves estrangeiras compostas quando possível

## 4. API REST
- Seguir as convenções RESTful
- Usar substantivos no plural para recursos (ex: `/perguntas`, `/respostas`)
- Retornar códigos HTTP apropriados:
  - 200 para sucesso na consulta
  - 201 para criação
  - 204 para exclusão
  - 400 para requisições inválidas
  - 404 para recursos não encontrados
  - 500 para erros internos
- Usar DTOs para entrada/saída de dados
- Documentar endpoints com JavaDoc

## 5. Tratamento de Exceções
- Criar exceptions específicas para cada tipo de erro
- Implementar `@ControllerAdvice` para tratamento global de exceções
- Retornar mensagens de erro padronizadas
- Logar erros inesperados

## 6. Testes
- Escrever testes unitários para serviços e utilitários
- Implementar testes de integração para controladores
- Usar `@DataJpaTest` para testar repositórios
- Manear dados de teste de forma isolada
- Usar `@Sql` para carregar dados de teste quando necessário

## 7. Boas Práticas
- Manter as classes pequenas e com responsabilidade única
- Evitar lógica de negócios em controladores
- Usar injeção de dependência via construtor
- Documentar código complexo
- Manter o `application.yml` organizado por perfis

## 8. Versionamento
- Commits atômicos e descritivos
- Usar convenção de mensagens:
  - feat: Nova funcionalidade
  - fix: Correção de bug
  - docs: Atualização de documentação
  - style: Formatação de código
  - refactor: Refatoração de código
  - test: Adição ou correção de testes

## 9. Dependências
- Manter as versões das dependências atualizadas
- Remover dependências não utilizadas
- Usar versões estáveis e LTS quando disponível

## 10. Performance
- Usar `@Transactional` apenas quando necessário
- Evitar N+1 queries em relacionamentos JPA
- Considerar uso de DTOs com projeções para consultas complexas
- Implementar paginação em endpoints que retornam listas

## 11. Segurança
- Validar todas as entradas de usuário
- Não expor entidades JPA diretamente na API
- Usar HTTPS em produção
- Implementar autenticação/autorização quando necessário

## 12. Logging
- Usar SLF4J para logs
- Níveis de log apropriados:
  - ERROR: Erros inesperados
  - WARN: Condições inesperadas, mas recuperáveis
  - INFO: Eventos significativos do sistema
  - DEBUG: Informações detalhadas para depuração
  - TRACE: Informações mais detalhadas

## 13. Documentação
- Manter o README.md atualizado
- Documentar endpoints da API (OpenAPI/Swagger)
- Incluir exemplos de requisições/respostas
- Documentar pré-requisitos e configurações necessárias
