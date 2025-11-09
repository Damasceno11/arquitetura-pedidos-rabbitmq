# 🚀 Arquitetura Enterprise: Sistema de Pedidos com RabbitMQ

![Status](https://img.shields.io/badge/status-concluído-brightgreen)
![Java](https://img.shields.io/badge/Java-17-blue)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.7-green)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-blueviolet)

Este projeto demonstra a construção de um sistema de microsserviços resiliente, focado em mensageria assíncrona com RabbitMQ, boas práticas de Clean Architecture e padrões enterprise.

O objetivo é simular o fluxo de um pedido (write) e a notificação assíncrona de e-mail (read/process), garantindo que nenhuma mensagem seja perdida, mesmo em caso de falhas temporárias.

---

## 🏛️ Diagrama da Arquitetura (Mermaid)

O fluxo de dados segue uma arquitetura desacoplada baseada em eventos, utilizando RabbitMQ como Message Broker.

```mermaid
graph TD
    subgraph "Fluxo de Comando (Write)"
        A[Usuário] -- "1. POST /pedidos" --> B(ms-pedido API);
        B -- "2. Salva Pedido" --> C[(PostgreSQL)];
        B -- "3. Publica Evento" --> D{pedidos.exchange};
    end

    subgraph "Fluxo de Evento (Process)"
        D -- "4. Roteia" --> E[Q: pedidos.v1.pedido-criado.ms-email];
        E -- "5. Consome" --> F(ms-email Consumer);

        subgraph "Caminho Feliz (ID Ímpar)"
            F -- "6a. Processa" --> G([Mailtrap]);
        end

        subgraph "Caminho Infeliz (ID Par - Resiliência)"
            F -- "6b. Falha (3x Retries)" --> H{pedidos.exchange.dlx};
            H -- "7. Roteia Falha" --> I[Q: ...ms-email.dlq];
        end
    end

    style B fill:#f9d,stroke:#333,stroke-width:2px
    style F fill:#d9f,stroke:#333,stroke-width:2px
    style I fill:#ffb,stroke:#333,stroke-width:2px
````

1.  **Produtor (`ms-pedido`):** Recebe a requisição HTTP (POST), valida, salva no Postgres (em uma transação) e publica um evento `PedidoCriadoEvent` na Exchange.
2.  **Exchange (RabbitMQ):** Roteia a mensagem para a fila correta baseado na "routing key".
3.  **Consumidor (`ms-email`):** Ouve a fila principal. Ao receber a mensagem, tenta processá-la (enviar o e-mail).
4.  **Padrão Retry:** Se o processamento falhar (simulado para IDs pares), o Spring AMQP tenta reprocessar a mensagem 3 vezes com um backoff exponencial.
5.  **Padrão DLQ (Dead Letter Queue):** Após 3 falhas, a mensagem é movida automaticamente para uma "fila morta" (DLQ) para análise manual, garantindo que **nenhum pedido seja perdido**.

-----

## 🛠️ Stack Tecnológica (Padrão Enterprise 2025)

| Categoria | Tecnologia | Justificativa |
| :--- | :--- | :--- |
| **Backend** | Java 17 + Spring Boot 3.5.7 | Ecossistema robusto para microsserviços. |
| **Mensageria** | Spring AMQP (RabbitMQ) | Alto throughput e resiliência (Retries, DLQ). |
| **Banco de Dados** | PostgreSQL 16 | Banco de dados relacional robusto e escalável. |
| **Migração (DDL)** | Flyway | Versionamento de schema de banco de dados (Infra-como-Código). |
| **Persistência** | Spring Data JPA / Hibernate | Alta produtividade para acesso a dados. |
| **Documentação** | SpringDoc (OpenAPI 3) | Geração automática de documentação (Swagger UI). |
| **Mapeamento** | MapStruct + Lombok | Redução de boilerplate e alta performance em DTO $\leftrightarrow$ Entity. |
| **Validação** | Jakarta Validation | Validação de DTOs na borda da API (`@Valid`). |
| **Testes** | JUnit 5 + `@DataJpaTest` (H2) | Testes de integração da camada de persistência. |
| **Infraestrutura** | Docker Compose | Orquestração do ambiente de desenvolvimento (Postgres + Rabbit). |

-----

## ✨ Padrões e Decisões de Arquitetura

Este projeto não é apenas sobre "fazer funcionar", mas sobre "fazer da forma correta".

* **Clean Architecture:** Separação rigorosa de responsabilidades em `domain` (entidades), `application` (serviços, DTOs) e `infrastructure` (config, controllers, mensageria).
* **DTOs (Data Transfer Objects):** Nenhuma entidade JPA (`Pedido`) é exposta na API. Usamos DTOs (com `java.lang.Record`) para definir "contratos" de API imutáveis.
* **Produtor vs. Consumidor (Propriedade da Fila):**
    * O **`ms-pedido` (Produtor)** *não sabe* para qual fila ele envia. Ele apenas conhece a **Exchange** (o "tópico").
    * O **`ms-email` (Consumidor)** é o *dono* da **Queue** (a "fila"). Ele a declara, define seus argumentos (como a política de DLQ) e faz o *binding* com a Exchange.
* **Estratégia de ID (JPA):** Uso de `GenerationType.SEQUENCE` com `allocationSize=50` para performance em escritas (batch inserts) no Postgres.
* **Otimização de Query (Flyway V2):** Criação proativa de um **índice** na coluna `cliente_id` para otimizar futuras consultas de "meus pedidos", evitando *Full Table Scans*.
* **Tratamento de Erros:** Uso de `@RestControllerAdvice` (`GlobalExceptionHandler`) para capturar erros de validação (`400`) e erros internos (`500`), retornando um JSON limpo e padronizado.

-----

## 🚀 Como Executar o Projeto

**Pré-requisitos:**

* Java 17 (JDK)
* Maven 3.x
* Docker e Docker Compose

#### 1\. Subir a Infraestrutura

Na raiz do projeto, suba o Postgres e o RabbitMQ:

```bash
docker-compose up -d
```

* **RabbitMQ Admin:** `http://localhost:15672` (login: `guest` / `guest`)
* **Postgres (via DBeaver/etc.):** `localhost:5432` (login: `postgres` / `postgres`)

#### 2\. Configurar o `ms-email`

O `ms-email` usa o [Mailtrap.io](https://mailtrap.io) (Sandbox) para simular o envio de e-mails.

1.  Crie uma conta gratuita no Mailtrap.

2.  Vá em **Email Testing** $\rightarrow$ **My Sandbox**.

3.  Copie suas credenciais (Username/Password).

4.  Cole-as no arquivo `ms-email/src/main/resources/application.yml`:

    ```yaml
    spring:
      mail:
        host: sandbox.smtp.mailtrap.io
        port: 2525
        username: "SEU_USERNAME_DO_MAILTRAP"
        password: "SUA_SENHA_DO_MAILTRAP"
    ```

#### 3\. Iniciar os Microsserviços

Inicie os serviços (em terminais separados ou pela IDE). **É importante iniciar o Consumidor primeiro.**

```bash
# Terminal 1: Iniciar o Consumidor (para criar as filas)
cd ms-email
mvn spring-boot:run

# Terminal 2: Iniciar o Produtor
cd ms-pedido
mvn spring-boot:run
```

-----

## 🧪 Como Testar (3 Cenários)

Acesse a documentação da API (Swagger) no seu navegador:
**`http://localhost:8080/swagger-ui.html`**

#### Teste A: Caminho Feliz (Pedido Ímpar)

1.  **Ação:** Envie um `POST /pedidos` com um ID ímpar (ex: `ID: 103`).
2.  **Resultado:**
    * Swagger retorna `Code 201 Created`.
    * O log do `ms-email` mostra `Email enviado com sucesso...`.
    * O e-mail **aparece** na sua Inbox do Mailtrap.

#### Teste B: Caminho Infeliz (Pedido Par - Retry/DLQ)

1.  **Ação:** Envie um `POST /pedidos` com um ID par (ex: `ID: 102`).
2.  **Resultado:**
    * Swagger retorna `Code 201 Created`.
    * O log do `ms-email` mostra **"Falha simulada\!"** 3 vezes (com intervalos de 3s e 6s).
    * O e-mail **não** aparece no Mailtrap.
    * O **RabbitMQ Admin** (aba Queues) mostra **1** mensagem na fila `...ms-email.dlq`.

#### Teste C: Validação (400 Bad Request)

1.  **Ação:** Envie um `POST /pedidos` com `"clienteId": null`.
2.  **Resultado:**
    * Swagger retorna `Code 400 Bad Request`.
    * O Response Body é: `{"clienteId": "ID do cliente não pode ser nulo"}`.

-----

## 🐛 Lições Aprendidas (Troubleshooting)

Durante o desenvolvimento, encontramos e solucionamos diversos problemas clássicos de arquitetura de microsserviços.

### 1\. Incompatibilidade de Versão (SpringDoc vs. Spring Boot)

* **Problema:** O Swagger UI (`/swagger-ui.html`) retornava `500 Internal Server Error` e o log mostrava `java.lang.NoSuchMethodError: 'void org.springframework.web.method.ControllerAdviceBean.<init>(java.lang.Object)'`.

* **Análise:** A versão do `springdoc-openapi` (Swagger) era incompatível com a versão do `spring-boot-starter-parent` (Spring Framework).

* **Solução:** Consultar a documentação oficial e alinhar as versões. A combinação correta encontrada foi:

    * `spring-boot-starter-parent`: **`3.5.7`**
    * `springdoc-openapi-starter-webmvc-ui`: **`2.8.6`** (ou a mais recente compatível)

  <!-- end list -->

  ```xml
  <parent>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-parent</artifactId>
      <version>3.5.7</version>
  </parent>

  <dependency>
      <groupId>org.springdoc</groupId>
      <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
      <version>2.8.6</version> </dependency>
  ```

### 2\. Conflito de Fila no RabbitMQ (`PRECONDITION_FAILED`)

* **Problema:** O `ms-email` (Consumidor) falhava ao iniciar com o erro `PRECONDITION_FAILED - inequivalent arg 'x-dead-letter-exchange'`.

* **Análise:** O `ms-pedido` (Produtor) estava declarando a fila *sem* os argumentos de DLQ, e o `ms-email` tentava redeclará-la *com* os argumentos. O RabbitMQ proíbe a alteração de argumentos de uma fila existente.

* **Solução:** Remover a declaração de `Queue` e `Binding` do `RabbitMQConfig.java` do **Produtor (`ms-pedido`)**. O Produtor só deve conhecer a `Exchange`. O Consumidor é o dono da `Queue`.

  ```java
  // Solução no ms-pedido/infrastructure/config/RabbitMQConfig.java

  // DELETADO 
  // @Bean public Queue queue() { ... }

  // DELETADO
  // @Bean public Binding binding() { ... }

  // MANTIDO
  @Bean public TopicExchange topicExchange() { ... }
  ```

### 3\. Mensagens Desaparecendo (Binding Faltando)

* **Problema:** Os testes A e B falhavam. O e-mail não chegava no Mailtrap e a mensagem também não ia para a DLQ. As mensagens estavam "sumindo".

* **Análise:** O `RabbitMQConfig.java` do **Consumidor (`ms-email`)** estava declarando a fila (`Queue`) e a `DLQ`, mas faltava o `Binding` principal para ligar a `Queue` principal à `Exchange`.

* **Solução:** Adicionar o `Binding` da fila principal no `ms-email`.

  ```java
  // Solução no ms-email/infrastructure/config/RabbitMQConfig.java

  @Bean
  public Binding binding() { // <-- ESTE BEAN ESTAVA FALTANDO
      return BindingBuilder
              .bind(queue())
              .to(topicExchange())
              .with(routingKey);
  }
  ```

### 4\. Falha em Testes de Integração (H2 vs. Postgres)

* **Problema:** O `@DataJpaTest` falhava ao iniciar, com `SQLGrammarException` no `V1__...sql`.

* **Análise:** O script do Flyway usava a sintaxe do Postgres (`START 1 INCREMENT 50`), que é incompatível com o banco em memória H2 (usado nos testes).

* **Solução:** Alterar o script de migração para uma sintaxe SQL mais universal, compatível com ambos.

  ```sql
  /* V1__create_tables_pedido.sql */

  /* ANTIGO: CREATE SEQUENCE pedido_seq START 1 INCREMENT 50; */

  /* SOLUÇÃO: */
  CREATE SEQUENCE pedido_seq START WITH 1 INCREMENT BY 50;
  CREATE SEQUENCE pedido_item_seq START WITH 1 INCREMENT BY 50;
  ```

### 5\. Falha em Testes de JPA (`Table "PEDIDOS" not found`)

* **Problema:** O `@DataJpaTest` falhava ao tentar salvar, mesmo após corrigir o H2.

* **Análise:** O Flyway criou a tabela `tb_pedidos`, mas a entidade `Pedido.java` não tinha a anotação `@Table`, fazendo o Hibernate procurar por uma tabela padrão (`pedidos`).

* **Solução:** Adicionar anotações `@Table` explícitas nas entidades.

  ```java
  // Solução em Pedido.java
  @Entity
  @Table(name = "tb_pedidos") // <-- ESTA LINHA ESTAVA FALTANDO
  public class Pedido { ... }
  ```

-----

## 👨‍💻 Autor

| [<img src="https://avatars.githubusercontent.com/u/48593845?v=4" width="100">](https://github.com/Damasceno11) |
| :---: |
| **Pedro Paulo Damasceno Muniz** |
| [GitHub](https://github.com/Damasceno11) • [LinkedIn](https://www.linkedin.com/in/pedro-damasceno-23b330150/) |

```
```
