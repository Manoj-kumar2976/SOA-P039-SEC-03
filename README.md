# Vanguard Mutual - Automated Claim Verification & Settlement Engine

Spring Boot microservices project: Eureka discovery + API Gateway + JWT auth +
Policy/Claim/Settlement services, matching the Continuous Evaluation Project rubric.

## Services & Ports

| Service | Port | Purpose |
|---|---|---|
| eureka-server | 8761 | Service registry |
| api-gateway | 8080 | Single entry point, JWT validation, routing |
| auth-service | 8081 | Login, issues JWT |
| policy-service | 8082 | Policy CRUD + coverage verification |
| claim-service | 8083 | Claim intake, calls Policy then Settlement |
| settlement-service | 8084 | Settlement records, payout approval |

## Prerequisites
- Java 17
- Maven 3.8+
- An IDE (IntelliJ / STS / VS Code with Java extensions), or just command line

## Run Order (important — start in this sequence)

Open 6 terminals, one per service, and run in each folder:

```bash
mvn spring-boot:run
```

**Order:**
1. `eureka-server` — wait until it's up (http://localhost:8761)
2. `auth-service`
3. `policy-service`
4. `claim-service`
5. `settlement-service`
6. `api-gateway` — start last, after the others have registered with Eureka

Check http://localhost:8761 — you should see AUTH-SERVICE, POLICY-SERVICE,
CLAIM-SERVICE, SETTLEMENT-SERVICE, API-GATEWAY all listed as UP.

## Demo Flow (use Postman / curl)

### 1. Login (get JWT)
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"adjuster","password":"pass123"}'
```
Copy the `token` from the response. Demo users:
- `policyholder` / `pass123`
- `adjuster` / `pass123`
- `admin` / `pass123`

### 2. View seeded policies (via Gateway, JWT required)
```bash
curl http://localhost:8080/api/policy \
  -H "Authorization: Bearer <TOKEN>"
```
You'll see 3 seeded policies (id 1: ACTIVE, 50000 coverage; id 2: ACTIVE, 20000;
id 3: EXPIRED, 10000).

### 3. Submit a claim (triggers Claim -> Policy -> Settlement)
```bash
curl -X POST http://localhost:8080/api/claims \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"policyId":1,"claimAmount":15000}'
```
Response status will show `SETTLEMENT_INITIATED` if the policy is active and
the claim amount is within coverage. Try policyId 3 (EXPIRED) to see it get
`REJECTED` instead.

### 4. View the settlement created
```bash
curl http://localhost:8080/api/settlement \
  -H "Authorization: Bearer <TOKEN>"
```

### 5. Approve/pay the settlement
```bash
curl -X PUT http://localhost:8080/api/settlement/1/approve \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"approvedAmount":15000}'
```

## What to point at during the review

- **JWT Authentication**: `auth-service` issues it (`JwtUtil.java`), `api-gateway`
  validates it on every request (`AuthFilter.java`) before routing.
- **API Gateway**: `api-gateway/src/main/resources/application.yml` — route
  definitions per service.
- **Eureka + Load Balancing**: every service has `@EnableDiscoveryClient` +
  registers to `http://localhost:8761/eureka/`; `claim-service` calls
  `http://policy-service/...` and `http://settlement-service/...` by service
  name via a `@LoadBalanced RestTemplate` — no hardcoded IPs.
- **Inter-service communication**: `ClaimProcessingService.java` in
  claim-service — this is the literal Claim -> Policy -> Settlement flow.
- **Database tables**: H2 in-memory, one schema per service (policy, claim,
  settlement), matching your sample table design — visible via
  `/h2-console` on each service while it's running.

## Notes / what you should be ready to justify
- H2 in-memory DB is used for demo simplicity — swap `application.yml`
  datasource for MySQL/Postgres in a real deployment.
- Auth uses an in-memory user map (`UserService.java`) instead of a DB — call
  this out as a placeholder you'd replace with a Users table + password hashing.
- Only the Gateway validates JWTs in this version (not each downstream
  service) — simpler for a class project; a stricter design would re-validate
  at each service too.
