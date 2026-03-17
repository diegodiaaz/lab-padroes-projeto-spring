# Lab Padrões de Projeto com Spring Boot

Projeto desenvolvido durante o bootcamp da **Digital Innovation One** com o objetivo de aplicar Design Patterns clássicos do GoF em um cenário real com Spring Boot.

O projeto original cobria Singleton, Strategy e Facade. Extendi com a implementação de **Proxy** e **Observer** por iniciativa própria, aprofundando os estudos além do conteúdo do curso.

---

## O que foi praticado

- Aplicação de **5 Design Patterns** (GoF) em um projeto Spring Boot real
- Integração com **API externa** (ViaCEP) via Spring Cloud OpenFeign
- Implementação de **cache em memória** para otimizar chamadas HTTP
- Arquitetura **orientada a eventos** com o sistema nativo do Spring (`ApplicationEventPublisher`)
- Separação de responsabilidades seguindo os princípios **SOLID**
- Persistência com **Spring Data JPA** e banco H2

---

## Design Patterns aplicados

### Singleton
Gerenciado pelo Spring IoC Container via `@Service` e `@Component` — instância única sem implementação manual.

### Strategy
A interface `ClienteService` desacopla o contrato da implementação. O controller não conhece detalhes concretos — apenas a abstração.

### Facade
O `ClienteRestController` simplifica o acesso a múltiplas integrações (banco H2 + ViaCEP) em uma API REST coesa.

### Proxy *(implementação própria)*
`ClienteServiceProxy` intercepta as operações de escrita e aplica um **cache de CEPs em memória**, evitando chamadas redundantes à API externa.

| Situação | Comportamento |
|---|---|
| CEP já no cache | Reutiliza — zero chamada externa |
| CEP no banco H2 | Carrega do banco e popula o cache |
| CEP novo | Consulta ViaCEP, persiste e cacheia |

### Observer *(implementação própria)*
Após cada operação, o Proxy publica um `ClienteEvent`. O `EmailNotificationObserver` reage de forma completamente desacoplada — novos observers podem ser adicionados sem tocar no código existente.

---

## Estrutura de packages

```
one.digitalinnovation.gof
├── controller
│   └── ClienteRestController.java       # Facade
├── model
│   ├── Cliente.java
│   ├── ClienteRepository.java
│   ├── Endereco.java
│   └── EnderecoRepository.java
├── service
│   ├── ClienteService.java              # Strategy (interface)
│   └── ViaCepService.java               # Feign Client
├── proxy
│   └── ClienteServiceProxy.java         # Proxy
└── observer
    ├── ClienteEvent.java
    └── EmailNotificationObserver.java
```

---

## Stack

![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat&logo=springboot&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring_Data_JPA-6DB33F?style=flat&logo=spring&logoColor=white)
![Java](https://img.shields.io/badge/Java_21-ED8B00?style=flat&logo=openjdk&logoColor=white)
![OpenFeign](https://img.shields.io/badge/OpenFeign-6DB33F?style=flat&logo=spring&logoColor=white)
![Swagger](https://img.shields.io/badge/OpenAPI-85EA2D?style=flat&logo=swagger&logoColor=black)
![H2](https://img.shields.io/badge/H2_Database-004088?style=flat)

---

## Como rodar

```bash
mvn spring-boot:run
```

Acesse: `http://localhost:8080/clientes`

## Endpoints

| Método | Rota | Descrição |
|---|---|---|
| GET | `/clientes` | Lista todos os clientes |
| GET | `/clientes/{id}` | Busca por ID |
| POST | `/clientes` | Cria cliente (consulta ViaCEP) |
| PUT | `/clientes/{id}` | Atualiza cliente |
| DELETE | `/clientes/{id}` | Remove cliente |

---

Projeto base por [@falvojr](https://github.com/falvojr) — Digital Innovation One.