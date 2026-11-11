# SmartBank

Plateforme bancaire de **démonstration** (simulation pédagogique, aucune transaction réelle) —
architecture microservices Spring Boot / Spring Cloud / Angular.

**Équipe :** Soufiane Ammari · Ahmed Abderrahman El Baghdadi · Mohamed Amine Amrania · Youssef Boughanem · Adam Amaziane

---

## 1. Architecture

```
                 Angular (4200)
                      │  JWT (Keycloak, PKCE)
                      ▼
              API Gateway (8080) ──── Keycloak (8180)
                      │ lb://   ◄──── Eureka (8761)
   ┌──────────┬───────┼─────────┬────────────┬──────────────┐
customer   account  transaction transfer     loan       notification
 (8081)    (8082)    (8083)      (8084)     (8085)        (8086)
   │          ▲ Feign  ▲            │ Feign     │ Feign        ▲
   │          └────────┼────────────┘───────────┘              │
   └──────────────► Kafka (9092) ─────────────────────────────┘
                 Config Server (8888) · PostgreSQL (1 base / service)
```

| Composant | Techno | Où |
|---|---|---|
| Frontend | Angular 20 + keycloak-angular | `frontend/` |
| API Gateway | Spring Cloud Gateway (WebFlux) + Resilience4J | `infrastructure/api-gateway` |
| Service Discovery | Eureka | `infrastructure/discovery-server` |
| Configuration | Spring Cloud Config (profil native) | `infrastructure/config-server` |
| Code partagé | événements Kafka, sécurité JWT, erreurs, Swagger | `common/smartbank-common` |
| Sécurité | Keycloak 26 (OAuth2 / OIDC), realm importé auto | `docker/keycloak` |
| Messaging | Kafka 3.9 (KRaft, sans Zookeeper) | `docker-compose.yml` |
| Persistance | PostgreSQL 16, une base par service | `docker/postgres` |
| Emails | Mailpit (faux SMTP) | http://localhost:8025 |

## 2. Démarrage

**Prérequis :** JDK 17, Node 20+, Docker Desktop. (Maven n'est pas nécessaire : utiliser `mvnw`.)

### Mode développement (recommandé) : infra dans Docker, services dans l'IDE

```bash
docker compose up -d                 # postgres, kafka, keycloak, mailpit
./mvnw clean install -DskipTests     # Windows : mvnw.cmd clean install -DskipTests
```

Puis lancer dans l'IDE, **dans cet ordre** :
1. `ConfigServerApplication` → 2. `DiscoveryServerApplication` → 3. `ApiGatewayApplication`
4. les services dont vous avez besoin (`CustomerServiceApplication`, ...)

Frontend :
```bash
cd frontend
npm install
npm start                            # http://localhost:4200
```

### Mode démo : tout dans Docker

```bash
./mvnw clean package -DskipTests
docker compose --profile apps up -d --build
```

> ⚠️ Tout l'environnement consomme ~6-8 Go de RAM. En dev, ne lancez que les services nécessaires.

### URLs utiles

| URL | Quoi |
|---|---|
| http://localhost:4200 | Frontend Angular |
| http://localhost:8080 | API Gateway (toutes les API sous `/api/**`) |
| http://localhost:8761 | Tableau de bord Eureka |
| http://localhost:8180 | Console Keycloak (`admin` / `admin`) |
| http://localhost:8081/swagger-ui.html | Swagger d'un service (port 8081 → 8086) |
| http://localhost:8025 | Mailpit (emails envoyés) |
| http://localhost:8888/customer-service/default | Config servie à un service |

### Comptes de test (realm `smartbank`)

| Utilisateur | Mot de passe | Rôles |
|---|---|---|
| `client1` | `client1` | CLIENT |
| `agent1` | `agent1` | AGENT |
| `admin1` | `admin1` | ADMIN, AGENT |

Obtenir un token pour tester avec curl / Postman / Swagger (« Authorize ») :
```bash
curl -s -X POST http://localhost:8180/realms/smartbank/protocol/openid-connect/token \
  -d grant_type=password -d client_id=smartbank-frontend -d username=agent1 -d password=agent1
```

## 3. Répartition

| Membre | Périmètre |
|---|---|
| **Soufiane Ammari** | Socle : Config, Eureka, Gateway, Keycloak, Docker Compose, Kafka, CI — intégration / lead technique |
| **Ahmed Abderrahman El Baghdadi** | `customer-service` + `account-service` |
| **Mohamed Amine Amrania** | `transfer-service` + `transaction-service` (saga de virement, Kafka) |
| **Youssef Boughanem** | `loan-service` + `notification-service` + relevés Spring Batch (dans `transaction-service`) |
| **Adam Amaziane** | Frontend Angular (layout, auth, guards, tableaux de bord) — chacun aide sur les écrans de ses services |

## 4. Ce qui existe déjà (exemples de référence à copier)

| Besoin | Exemple |
|---|---|
| CRUD complet (entité, DTO, validation, service, contrôleur, rôles) | `customer-service` |
| Publier un événement Kafka | `CustomerService.create()` + `KafkaTopicsConfig` |
| Consommer un événement Kafka | `notification-service/.../BankingEventsListener` |
| Appel Feign + Retry + Circuit Breaker + fallback | `account-service/.../client/CustomerLookupService` |
| Tests unitaires (Mockito) | `CustomerServiceTest` |
| Tests web + sécurité (MockMvc + JWT simulé) | `CustomerControllerTest` |
| Service HTTP Angular + guard par rôle | `frontend/src/app/core/` |

**Fourni automatiquement à chaque service par `smartbank-common` (rien à configurer) :**
- sécurité JWT Keycloak : rôles Keycloak → `hasRole('CLIENT' | 'AGENT' | 'ADMIN')` dans `@PreAuthorize` ;
- propagation du token sur les appels Feign ;
- erreurs au format ProblemDetail : lancer `ResourceNotFoundException` (404) ou `BusinessException` (422/409) ;
- Swagger avec bouton « Authorize ».

## 5. Conventions

- **Packages** : `com.smartbank.<service>.{domain, service, web, web.dto, client, config, listener}`.
- **Montants** : toujours `BigDecimal` (jamais `double`), devise `MAD`.
- **API REST** : préfixe `/api/<ressource>` (c'est ce que route la gateway), DTO en `record`, jamais d'entité JPA exposée.
- **Config** : pas de config métier dans le `application.yml` du service → la mettre dans
  `infrastructure/config-server/src/main/resources/config/<service>.yml` (redémarrer le config-server).
- **Événements Kafka** : définis **uniquement** dans `common/.../events` (+ nom du topic dans `Topics`).
  Changer un événement = prévenir l'équipe.
- **Git** : une branche par fonctionnalité (`feature/transfer-saga`), Pull Request relue par 1 personne avant merge sur `main`.

## 6. Contrats prévus (à affiner ensemble)

| Service | Endpoints principaux |
|---|---|
| customer | `POST/GET /api/customers`, `GET /api/customers/{id}`, `GET /api/customers/me` ✅ |
| account | `POST /api/accounts`, `GET /api/accounts/me`, `GET /api/accounts/{iban}`, interne : `POST /api/accounts/{iban}/debit` et `/credit` (avec `operationId` pour l'idempotence) |
| transaction | `GET /api/transactions?iban=&from=&to=`, `GET /api/statements/{iban}/{yyyy-MM}`, `POST /api/statements/run` (ADMIN) |
| transfer | `POST /api/transfers` (en-tête `Idempotency-Key`), `GET /api/transfers/me` |
| loan | `POST /api/loans/simulate`, `POST /api/loans` (demande), `GET /api/loans/me`, `PUT /api/loans/{id}/decision` (AGENT) |
| notification | `GET /api/notifications/me` |

| Topic Kafka | Producteur | Consommateurs |
|---|---|---|
| `smartbank.customer.created` | customer | notification |
| `smartbank.account.created` | account | notification |
| `smartbank.transfer.completed` | transfer | transaction, notification |
| `smartbank.transfer.failed` | transfer | notification |
| `smartbank.loan.decided` | loan | notification |
| `smartbank.statement.generated` | transaction (batch) | notification |

**Contrôles de cohérence d'un virement** (transfer-service) : montant > 0 et ≤ plafond, comptes source ≠ cible,
comptes existants et actifs, source appartenant au client connecté, solde suffisant, plafond journalier,
idempotence (même `Idempotency-Key` ⇒ même résultat, pas de double débit), verrouillage optimiste (`@Version`) sur le solde.
