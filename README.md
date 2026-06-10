# Library API

API REST para gerenciamento de livros, desenvolvida com Spring Boot. Inclui frontend simples em HTML/CSS/JS.

# Como abrir o backend do projeto?
1. abrir terminal na pasta backend
2. usar comando 'java -version'
3. depois comando 'mvnw spring-boot:run' ou './mvnw spring-boot:run'
4. e acessa 'http://localhost:8080/swagger-ui/index.html'

# Como acessar o Frontend do projeto?
1. verificar se tem node com comando 'node --version'
2. executar pelo terminal da pasta o .bat (Windows) ou .sh (Linux)


## Tecnologias

- **Backend:** Java 21, Spring Boot 4.0.6, Spring Data JPA, Flyway, H2 (in-memory), Swagger/OpenAPI
- **Frontend:** HTML, CSS e JavaScript puro (sem frameworks)
- **Servidor frontend:** Node.js (built-in `http` module)

---

## Estrutura do projeto

```
api/
├── start.sh              # Sobe backend + frontend juntos (macOS/Linux)
├── start.bat             # Sobe backend + frontend juntos (Windows)
├── backend/              # Spring Boot
│   └── src/main/java/com/project/library/
└── frontend/
    ├── index.html        # Interface web
    └── server.js         # Servidor HTTP na porta 3000
```

---

## Pré-requisitos

| Ferramenta | Versão mínima |
|------------|--------------|
| Java (JDK) | 21           |
| Node.js    | 18+          |

### Instalando o Java 21

**macOS (Homebrew):**
```bash
brew install openjdk@21
export PATH="/opt/homebrew/opt/openjdk@21/bin:$PATH"
```
Para tornar permanente, adicione a linha `export PATH=...` ao `~/.zshrc` e rode `source ~/.zshrc`.

**Linux (apt — Ubuntu/Debian):**
```bash
sudo apt update
sudo apt install openjdk-21-jdk -y
```

**Linux (dnf — Fedora/RHEL):**
```bash
sudo dnf install java-21-openjdk-devel -y
```

**Windows:**

Baixe o instalador `.msi` em https://adoptium.net, execute e marque a opção **"Add to PATH"** durante a instalação.

Verificar instalação (todos os sistemas):
```bash
java -version
# java version "21.x.x" ...
```

---

### Instalando o Node.js

**macOS (Homebrew):**
```bash
brew install node
```

**Linux (apt — Ubuntu/Debian):**
```bash
sudo apt update
sudo apt install nodejs npm -y
```

**Linux (dnf — Fedora/RHEL):**
```bash
sudo dnf install nodejs -y
```

**Windows:**

Baixe o instalador `.msi` em https://nodejs.org (versão LTS), execute e siga os passos (PATH já é configurado automaticamente).

Verificar instalação (todos os sistemas):
```bash
node --version
# v22.x.x
```

---

## Como rodar

### macOS / Linux

```bash
# Na raiz do projeto
chmod +x start.sh
./start.sh
```

> Se aparecer "Permission denied" no `mvnw`, o script já executa `chmod +x mvnw` automaticamente.

### Windows

```bat
start.bat
```

> Caso o PowerShell bloqueie scripts, abra o terminal como **Administrador** e execute:
> ```
> Set-ExecutionPolicy RemoteSigned
> ```

### Rodando separadamente (qualquer OS)

**Backend:**
```bash
# macOS/Linux
cd backend
./mvnw spring-boot:run

# Windows
cd backend
mvnw.cmd spring-boot:run
```

**Frontend:**
```bash
cd frontend
node server.js
```

---

## Endereços após subir

| Serviço       | URL                              |
|---------------|----------------------------------|
| Frontend      | http://localhost:3000            |
| API REST      | http://localhost:8080/books      |
| Swagger UI    | http://localhost:8080/swagger-ui |
| H2 Console    | http://localhost:8080/h2-console |

> **H2 Console** — credenciais: JDBC URL `jdbc:h2:mem:testdb`, usuário `sa`, senha em branco.

---

## Endpoints da API

Base URL: `http://localhost:8080`

### Listar todos os livros

```
GET /books
```

Query params (paginação):

| Parâmetro | Tipo    | Padrão | Descrição                        |
|-----------|---------|--------|----------------------------------|
| `page`    | integer | 0      | Número da página (começa em 0)   |
| `size`    | integer | 10     | Itens por página                 |
| `sort`    | string  | —      | Ex: `title,asc` ou `author,desc` |

**Resposta 200:**
```json
{
  "content": [
    {
      "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
      "title": "Dom Casmurro",
      "author": "Machado de Assis",
      "publishedDate": "1899-01-01"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "number": 0,
  "first": true,
  "last": true
}
```

---

### Buscar livro por ID

```
GET /books/{id}
```

| Parâmetro | Tipo   | Descrição         |
|-----------|--------|-------------------|
| `id`      | UUID   | ID do livro       |

**Resposta 200:**
```json
{
  "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "title": "Dom Casmurro",
  "author": "Machado de Assis",
  "publishedDate": "1899-01-01"
}
```

**Resposta 404:**
```json
{
  "message": "Entity not found",
  "timestamp": "2025-01-01T12:00:00"
}
```

---

### Cadastrar livro

```
POST /books
Content-Type: application/json
```

**Body:**
```json
{
  "title": "Dom Casmurro",
  "author": "Machado de Assis",
  "publishedDate": "1899-01-01"
}
```

| Campo           | Tipo       | Obrigatório | Validação              |
|-----------------|------------|-------------|------------------------|
| `title`         | string     | Sim         | Entre 2 e 50 caracteres |
| `author`        | string     | Não         | Máx. 100 caracteres    |
| `publishedDate` | string     | Não         | Formato `YYYY-MM-DD`   |

**Resposta 201:** retorna o livro criado com `id` gerado.

**Resposta 400:**
```json
{
  "message": "Validation failed",
  "errors": [
    { "field": "title", "message": "Title must not be empty" }
  ]
}
```

---

### Atualizar livro

```
PUT /books/{id}
Content-Type: application/json
```

Body com os mesmos campos do `POST`. Substitui todos os campos do livro.

**Resposta 200:** retorna o livro atualizado.  
**Resposta 404:** livro não encontrado.

---

### Excluir livro

```
DELETE /books/{id}
```

**Resposta 204:** sem corpo.  
**Resposta 404:** livro não encontrado.

---

## Exemplos com curl

```bash
# Cadastrar
curl -X POST http://localhost:8080/books \
  -H "Content-Type: application/json" \
  -d '{"title":"Dom Casmurro","author":"Machado de Assis","publishedDate":"1899-01-01"}'

# Listar (página 0, 5 itens)
curl "http://localhost:8080/books?page=0&size=5&sort=title,asc"

# Buscar por ID
curl http://localhost:8080/books/3fa85f64-5717-4562-b3fc-2c963f66afa6

# Atualizar
curl -X PUT http://localhost:8080/books/3fa85f64-5717-4562-b3fc-2c963f66afa6 \
  -H "Content-Type: application/json" \
  -d '{"title":"Memórias Póstumas","author":"Machado de Assis","publishedDate":"1881-01-01"}'

# Excluir
curl -X DELETE http://localhost:8080/books/3fa85f64-5717-4562-b3fc-2c963f66afa6
```
