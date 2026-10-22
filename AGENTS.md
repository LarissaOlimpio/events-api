# AGENTS.md

## Visão geral
API REST para gerenciamento de eventos, cupons, endereços e usuários. A aplicação segue uma organização por domínio, com camadas de controller, serviço e persistência; Flyway controla a evolução do banco.

## Stack e versões
- Java: 21 declarado no projeto; o plugin Maven configura compilação com source/target 17.
- Spring Boot: 4.0.5; Spring MVC, Spring Data JPA, Spring Security e Bean Validation.
- Banco: PostgreSQL, driver JDBC da linha declarada pelo Spring Boot.
- Migrações: Flyway, com suporte PostgreSQL.
- MapStruct: 1.6.3. Build: Maven com wrapper incluído.

## Estrutura
- `src/main/java/com/manager/events_api/domain/event/`: eventos, filtros, DTOs, mapeamento e regras de eventos.
- `src/main/java/com/manager/events_api/domain/coupon/`: cupons associados a eventos.
- `src/main/java/com/manager/events_api/domain/user/`: usuários, papéis, DTOs e persistência.
- `src/main/java/com/manager/events_api/domain/address/`: entidade, DTOs e mapeamento de endereços.
- `src/main/java/com/manager/events_api/infra/security/`: autenticação e regras de autorização.
- `src/main/java/com/manager/events_api/infra/exceptions/`: exceções e tratamento global.
- `src/main/resources/db/migration/`: scripts SQL versionados pelo Flyway.
- `src/test/java/`: testes unitários de serviços e teste de contexto.

## Comandos
No Windows, use o wrapper incluído:
- Build: `mvnw.cmd clean package`
- Executar: `mvnw.cmd spring-boot:run`
- Testar: `mvnw.cmd test`

A aplicação depende de PostgreSQL configurado localmente. `src/main/resources/application.properties` existe localmente e não é versionado; arquivos `.env`, quando presentes no ambiente, também não devem ser versionados. Nunca copie seus valores para documentação ou logs.

## Convenções observadas
- Pacotes e classes organizados por domínio; controllers expõem endpoints REST sob `/api/...`.
- DTOs de entrada e resposta separam o contrato HTTP das entidades; records são usados em vários DTOs.
- Regras de negócio ficam nos serviços; repositórios Spring Data JPA cuidam da persistência.
- MapStruct usa `componentModel = SPRING` para mapear DTOs e entidades.
- Validação declarativa usa Jakarta Validation; controllers aplicam `@Valid` aos corpos validados.
- Exceções de negócio usam `BusinessException`; `RestExceptionHandler` converte essa exceção em resposta HTTP.
- Migrações seguem o padrão Flyway `V<n>__descricao.sql`.

## Regras para a IA
- Preserve a organização por domínio e mantenha regras de negócio nos serviços.
- Para mudanças de esquema, crie uma nova migração Flyway; não reescreva migrações já aplicadas.
- Não leia, exponha ou registre valores de `application.properties`, `.env` ou outras credenciais.
- Mantenha DTOs como fronteira da API e revise autorização ao adicionar ou alterar endpoints.
- Não altere arquivos fora do escopo solicitado.

## Decisões e estado atual
- Autenticação JWT stateless implementada: login em `POST /api/auth/login`; segredo vem de `api.security.token.secret` e validade é configurada em horas, com padrão de 2 horas.
- `POST /api/user` é público e cria usuários como `VIEWER`; rotas de usuário exigem `ADMIN`, leituras de eventos/cupons exigem autenticação e escritas exigem `ADMIN` ou `ORGANIZER`.
- O filtro JWT é inserido na cadeia Spring Security e não é registrado em duplicidade pelo servlet; não há autorização por proprietário de evento/cupom nesta etapa.
- Testes `JwtAuthenticationIntegrationTest` e `RoleAuthorizationTest` passaram; não adicionar regras de propriedade sem nova decisão.
