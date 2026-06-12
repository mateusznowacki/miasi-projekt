# MiASI Projekt

## Dokumentacja API

Dokumentacja interfejsu programistycznego (API) znajduje się pod poniższym adresem (Swagger UI):
[http://localhost:8080/swagger-ui/index.html#/](http://localhost:8080/swagger-ui/index.html#/)

Specyfikacja OpenAPI (JSON) jest dostępna pod adresem:
[http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

## Jak uruchomić aplikację (Spring Boot)

Aby uruchomić aplikację backendową, otwórz terminal w głównym katalogu projektu i użyj narzędzia Maven Wrapper:

Na systemach Linux / macOS:
```bash
./mvnw spring-boot:run
```

Na systemie Windows:
```cmd
mvnw.cmd spring-boot:run
```

### Dane testowe (profil `dev`)

Uruchom backend z profilem `dev`, aby automatycznie zasilić bazę danymi demo (tylko przy pierwszym starcie, gdy baza jest pusta):

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Wymaga działającej bazy PostgreSQL (`docker compose up -d`).

| Rola | Email | Hasło |
|------|-------|-------|
| Administrator | `admin@medflow.pl` | `haslo123` |
| Recepcja | `rejestracja@medflow.pl` | `password` |
| Lekarz (kardiologia) | `lekarz@medflow.pl` | `password` |
| Lekarz (interna) | `maria.nowak@medflow.pl` | `password` |
| Pacjent | `pacjent@medflow.pl` | `haslo123` |
| Pacjent | `anna.wisniewska@medflow.pl` | `haslo123` |

Seeder tworzy też terminy w harmonogramach lekarzy oraz wizyty (nadchodzące i jedną zakończoną z wpisem w historii medycznej).
