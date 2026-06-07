# Projekt MiASI - MedFlow (System Medyczny)

Ten projekt jest aplikacją typu backend napisaną w języku Java przy użyciu frameworka Spring Boot (z wykorzystaniem podejścia Domain-Driven Design). Do przechowywania danych projekt korzysta z bazy danych PostgreSQL.

## Wymagania wstępne

Aby uruchomić projekt na swoim komputerze, potrzebujesz:
1. **Zainstalowanego środowiska Docker** (do uruchomienia bazy danych).
2. **Javy (JDK) w wersji 21**.
3. **Mavena** (do budowania projektu). 
   *Uwaga: Projekt zawiera wbudowany skrypt `mvnw` (Maven Wrapper), dzięki któremu instalacja Mavena w systemie nie jest absolutnie wymagana, ale warto wiedzieć, jak go zainstalować.*

---

## 1. Instalacja Javy i Mavena

Poniżej znajdują się instrukcje, jak zainstalować wymagane narzędzia za pomocą wiersza poleceń w zależności od systemu operacyjnego. Dzięki temu można uruchomić projekt bezpośrednio z konsoli (bez użycia np. IntelliJ).

### 🐧 Linux (Ubuntu / Debian)

Najprostszą opcją jest użycie wbudowanego menedżera pakietów `apt`:
```bash
sudo apt update
sudo apt install openjdk-21-jdk maven -y
```

**Zalecana alternatywa (SDKMAN!):** Jeśli wolisz mieć łatwiejszy system zarządzania wersjami Javy, użyj SDKMAN:
```bash
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install java 21.0.2-open
sdk install maven
```

### 🍎 macOS

Na systemach macOS najwygodniej użyć menedżera pakietów **Homebrew**. Jeśli go nie masz, zainstaluj ze strony [brew.sh](https://brew.sh/).
Następnie wykonaj w terminalu:
```bash
brew install openjdk@21 maven
```
*Wskazówka: Może być konieczne zaktualizowanie zmiennej systemowej PATH. Brew wypisze odpowiednią komendę w terminalu po instalacji (zwykle coś w stylu: `sudo ln -sfn /usr/local/opt/openjdk@21/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-21.jdk`).*

---

## 2. Uruchamianie bazy danych (PostgreSQL)

Projekt wykorzystuje bazę PostgreSQL, która jest skonfigurowana w pliku `docker-compose.yml`.

1. Otwórz terminal w **głównym katalogu** projektu (tam, gdzie znajduje się plik `docker-compose.yml`).
2. Uruchom kontener z bazą poleceniem:
   ```bash
   docker compose up -d
   ```
   *(Parametr `-d` uruchamia kontener w tle. Aby zatrzymać bazę, użyj `docker compose down`).*

---

## 3. Uruchamianie aplikacji

Aby skompilować i włączyć aplikację z poziomu terminala, wykonaj poniższe kroki.

1. Przejdź do katalogu `backend`:
   ```bash
   cd backend
   ```
2. Uruchom aplikację za pomocą Mavena. Masz dwie opcje:

   **Opcja A (korzystając z wbudowanego skryptu Maven Wrapper – polecana):**
   ```bash
   ./mvnw spring-boot:run
   ```

   **Opcja B (korzystając z globalnie zainstalowanego Mavena):**
   ```bash
   mvn spring-boot:run
   ```

To wszystko! Aplikacja Spring Boot skompiluje się, pobierze zależności i uruchomi. Będzie połączona z Twoją lokalną bazą PostgreSQL (Docker).
