# Casa do Código - Buratti

Este projeto é uma aplicação web Java baseada em Spring MVC, criada como estudo do livro Casa do Código. Ela expõe uma pequena loja online com páginas para listar produtos, cadastrar produtos e exibir confirmação de cadastro.

## O que a aplicação faz

- Exibe a página inicial da loja.
- Lista produtos cadastrados.
- Permite o cadastro de novos produtos com validação básica.
- Usa Spring MVC, JPA/Hibernate e MySQL para o fluxo de cadastro e persistência.

## Tecnologias principais

- Java
- Maven
- Spring MVC
- Hibernate / JPA
- MySQL
- JSP

## Como executar localmente com Docker Compose

1. Certifique-se de ter Docker e Docker Compose instalados.
2. Copie o arquivo de exemplo de variáveis de ambiente:

   ```bash
   cp .env.example .env
   ```

3. Ajuste as variáveis em `.env` se precisar alterar usuário, senha ou nome do banco.
4. Suba a stack com:

   ```bash
   docker compose up --build
   ```

5. A aplicação ficará disponível em `http://localhost:8080` e o MySQL em `localhost:3306`.

## Persistência de dados e logs

Para fazer com que tudo que for gravado no banco e os logs permaneçam mesmo após reinicializações, a stack usa volumes Docker:

- `mysql-data` — mantém os arquivos do banco MySQL em `/var/lib/mysql`.
- `app-logs` — armazena logs da aplicação em `/var/log/casadocodigo`.

### Pontos importantes para manter o estado persistente

- Não remova os volumes nomeados definidos em `docker-compose.yml`.
- Se quiser preservar dados entre reinstalações, mantenha o volume `mysql-data` e não execute `docker compose down -v` (este comando remove os volumes).
- Para visualizar logs do app:

  ```bash
  docker compose logs -f app
  ```

- Para revisar o banco:

  ```bash
  docker compose exec mysql mysql -u casadocodigo -pcasadocodigo casadocodigo
  ```

## Estrutura do projeto

- `src/main/java` — classes Java da aplicação.
- `src/main/webapp` — páginas JSP e recursos web.
- `pom.xml` — configuração do Maven e dependências.
- `docker-compose.yml` — orquestração da aplicação com MySQL.
- `Dockerfile` — imagem da aplicação web.
- `.env.example` — exemplo de configuração de ambiente.

## Observações

Este repositório foi usado como estudo de arquitetura web Java e pode servir como base para evoluções futuras, como containerização, upgrade de runtime Java ou modernização para versões mais recentes do ecossistema.
