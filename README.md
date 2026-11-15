# Nico — API REST (Back-End)

**Projeto Integrador — 1ª Fase · Programação Web**
Prof. Dr. Francisco Douglas L. Abreu · FATEC

## Sobre o projeto

O **Nico** é um sistema de Comunicação Aumentativa e Alternativa (CAA) para pessoas não verbais, principalmente pessoas com TEA. Um **administrador** (responsável ou terapeuta) cadastra **usuários** e configura **categorias** e **botões** (imagem, legenda, cor e áudio) que o usuário aciona para se comunicar.

Esta 1ª fase entrega o **back-end**: uma API REST com CRUD, cadastro/login e rotas protegidas.

## Links

| Item | Endereço |
|---|---|
| API publicada | https://api-nico.onrender.com |
| Swagger (documentação dos endpoints) | https://api-nico.onrender.com/swagger-ui.html |
| Repositório | https://github.com/romanofilipe98/Back-end-nico |
| Script do banco | [`database/script.sql`](database/script.sql) |

## Tecnologias

- Java 17 e Spring Boot 3.2.12 (Maven)
- Spring Data JPA / Hibernate
- PostgreSQL
- Spring Security + JWT, senhas com BCrypt
- Swagger / OpenAPI (Springdoc)
- Cloudinary (armazenamento de imagens e áudios)
- Deploy: Docker + Render

## Arquitetura (MVC em camadas)

```
src/main/java/br/com/comunicadornico
├── controller/   # endpoints REST
├── service/      # regras de negócio
├── repository/   # acesso aos dados (Spring Data JPA)
├── entity/       # entidades (mapeamento das tabelas)
├── dto/          # objetos de entrada e saída
├── security/     # JWT, filtro e configuração do Spring Security
└── config/       # Swagger e Cloudinary
```

## Banco de dados

PostgreSQL. O script com todas as tabelas está em `database/script.sql`.

| Tabela | Descrição |
|---|---|
| `tb_administrador` | Administrador (e-mail e telefone únicos, senha com hash, PIN) |
| `tb_usuario` | Usuário do comunicador, vinculado a um administrador |
| `tb_categoria` | Categoria de botões |
| `tb_botao` | Botão (legenda, cor, foto, áudios) |
| `tb_botao_categoria` | Relação N:N entre botão e categoria |
| `tb_botao_personalizacao` | Foto/áudio personalizados de um botão para um usuário |
| `tb_categoria_personalizacao` | Foto personalizada de uma categoria para um usuário |
| `tb_registrodeatividade` | Registro de cada uso de botão |
| `tb_tiradesentenca` / `tb_tiradesentenca_sequencia` | Frase montada pelo usuário |
| `tb_backupmanager` | Controle de backup |
| `tb_sintetizadorvoz` | Reservada para o sintetizador de voz |

Relacionamentos principais: Administrador 1:N Usuário, Botão e Categoria; Usuário 1:N Registro de Atividade; Usuário 1:1 Tira de Sentença; Botão N:N Categoria.

## Como executar

**Pré-requisitos:** JDK 17, Maven (ou `mvnw`), PostgreSQL e uma conta no Cloudinary.

1. Crie o banco PostgreSQL e execute `database/script.sql`.
2. Defina as variáveis de ambiente:

| Variável | Exemplo |
|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/nico` |
| `DB_USER` | `postgres` |
| `DB_PASSWORD` | `sua_senha` |
| `CLOUDINARY_CLOUD_NAME` | `seu_cloud_name` |
| `CLOUDINARY_API_KEY` | `sua_api_key` |
| `CLOUDINARY_API_SECRET` | `seu_api_secret` |

> `DB_URL` precisa começar com `jdbc:`.

3. Execute:

```bash
./mvnw spring-boot:run
```

A API sobe em `http://localhost:8080` e o Swagger em `http://localhost:8080/swagger-ui.html`.

## Autenticação e segurança

- Cadastro de administrador: `POST /api/administradores` (senha salva com BCrypt).
- Login: `POST /api/login` devolve um token JWT (validade de 2 horas).
- Rotas protegidas exigem o cabeçalho `Authorization: Bearer <token>`.
- Rotas públicas: cadastro de administrador, login, Swagger.

```http
POST /api/login
{ "email": "admin@teste.com", "senha": "1234" }
```

```json
{ "token": "eyJhbGciOi...", "tipo": "Bearer", "idAdministrador": 6, "nome": "Administrador Teste" }
```

## Endpoints (CRUD)

Todas as rotas exigem token, exceto `POST /api/administradores` e `POST /api/login`. A documentação completa, com exemplos de requisição e resposta, está no Swagger.

| Recurso | Criar (POST) | Listar (GET) | Buscar por ID (GET) | Buscar por nome (GET) | Atualizar (PUT) | Excluir (DELETE) |
|---|---|---|---|---|---|---|
| Administradores | `/api/administradores` | `/api/administradores` | `/api/administradores/{id}` | `/api/administradores/buscaradmnome?nome=` | `/api/administradores/{id}` | `/api/administradores/{id}` |
| Usuários | `/api/usuarios` | `/api/usuarios` | `/api/usuarios/{id}` | `/api/usuarios/buscar?nome=` | `/api/usuarios/{id}` | `/api/usuarios/{id}` |
| Categorias | `/api/categorias` | `/api/categorias` | `/api/categorias/{id}` | `/api/categorias/buscar?nome=` | `/api/categorias/{id}` | `/api/categorias/{id}` |
| Botões | `/api/botoes` | `/api/botoes` | `/api/botoes/{id}` | `/api/botoes/buscar?legenda=` | `/api/botoes/{id}` | `/api/botoes/{id}` |

### Outros endpoints

| Método | Rota | Descrição |
|---|---|---|
| POST | `/api/login/pin` | Valida o PIN do administrador |
| GET | `/api/usuarios/meus-usuarios` | Usuários do administrador logado |
| GET | `/api/usuarios/{id}/botoes-resolvidos` | Botões do comunicador com foto e áudio resolvidos |
| GET | `/api/usuarios/{id}/categorias-resolvidas` | Categorias do comunicador |
| POST / DELETE | `/api/usuarios/{id}/botoes/{idBotao}/personalizacao` | Cria ou remove personalização do botão |
| POST / DELETE | `/api/usuarios/{id}/categorias/{idCategoria}/personalizacao` | Cria ou remove personalização da categoria |
| POST | `/api/botoes/{id}/midia` | Atualiza foto e áudios padrão do botão |
| POST | `/api/categorias/{id}/foto` | Atualiza a foto padrão da categoria |
| POST | `/api/usuarios/{id}/botoes/{idBotao}/acionar` | Registra o uso de um botão |
| GET | `/api/usuarios/{id}/registros` | Histórico de uso |
| GET / DELETE | `/api/usuarios/{id}/tira-de-sentenca` | Consulta ou limpa a frase |
| POST | `/api/usuarios/{id}/tira-de-sentenca/botoes/{idBotao}` | Adiciona botão à frase |
| DELETE | `/api/usuarios/{id}/tira-de-sentenca/botoes/ultimo` | Remove o último botão |

### Códigos HTTP

`200` OK · `201` criado · `204` sem conteúdo · `400` dados inválidos · `401` não autenticado · `403` sem permissão · `404` não encontrado · `409` conflito (ex.: e-mail já cadastrado).

## Deploy

O back-end é publicado no **Render** a partir do `Dockerfile` do repositório, com o banco PostgreSQL e as variáveis de ambiente configuradas no painel. Endereço: https://api-nico.onrender.com

## Equipe

| Integrante| 
|---|---|
| Filipe Romano Amaral | 
| José Luiz Ferraz de Mello |
| Matheus Dantas Grigorio| 
| Renan Rodrigues Pawlovski | 

O desenvolvimento usa Git e GitHub, com commits individuais e branches por funcionalidade.