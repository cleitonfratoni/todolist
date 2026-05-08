# Todo List API

REST API para gerenciamento de tarefas com autenticação por usuário.

## Tecnologias

- **Java 17**
- **Spring Boot 4.0.5** (Web MVC, Data JPA, DevTools)
- **PostgreSQL 15**
- **Lombok**
- **BCrypt** — hash de senhas
- **Docker / Docker Compose**

## Pré-requisitos

- Java 17+
- Maven 3.8+
- Docker e Docker Compose

## Como executar

**1. Suba o banco de dados:**

```bash
docker compose up -d
```

**2. Configure as variáveis de ambiente** (copie o arquivo de exemplo):

```bash
cp .env.example .env
```

| Variável            | Padrão     | Descrição              |
|---------------------|------------|------------------------|
| `POSTGRES_USER`     | `admin`    | Usuário do banco       |
| `POSTGRES_PASSWORD` | `admin`    | Senha do banco         |
| `POSTGRES_DB`       | `todolist` | Nome do banco de dados |

**3. Rode a aplicação:**

```bash
./mvnw spring-boot:run
```

A API estará disponível em `http://localhost:8080`.

---

## Endpoints

### Usuários

| Método | Rota             | Descrição          | Auth |
|--------|------------------|--------------------|------|
| POST   | `/users/create`  | Cria um novo usuário | Não  |

**Body — criar usuário:**
```json
{
  "username": "joao",
  "password": "senha123"
}
```

---

### Tarefas

Todas as rotas de tarefas exigem **HTTP Basic Auth** (username + password do usuário cadastrado).

| Método | Rota              | Descrição                      |
|--------|-------------------|--------------------------------|
| POST   | `/tasks/create`   | Cria uma nova tarefa           |
| GET    | `/tasks/`         | Lista as tarefas do usuário    |
| POST   | `/tasks/{id}`     | Atualiza uma tarefa existente  |

**Body — criar/atualizar tarefa:**
```json
{
  "title": "Estudar Spring Boot",
  "description": "Revisar filtros e JPA",
  "startAt": "2026-05-10T09:00:00",
  "endAt": "2026-05-10T12:00:00",
  "priority": "HIGH"
}
```

---

## Estrutura do Projeto

```
src/main/java/com/fratoni/todolist/
├── errors/        # Tratamento global de exceções
├── filter/        # Filtro de autenticação Basic Auth
├── task/          # Domínio de tarefas (Model, Repository, Controller)
├── user/          # Domínio de usuários (Model, Repository, Controller)
└── utils/         # Utilitários (cópia de propriedades não nulas)
```
