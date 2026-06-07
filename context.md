# Project Context (Medflow)

Ten plik służy do przechowywania wspólnych danych, ustaleń, słownika pojęć i architektury systemu Medflow.

## Wizja dziedziny
System Medflow to spójna platforma HIS (Hospital Information System) wspierająca personel medyczny w procesie diagnozy poprzez automatyzację, przy jednoczesnym zachowaniu pełnego rygoru ochrony tożsamości.

---

## 1. Konteksty Ograniczone (Bounded Contexts)
Każda poddziedzina w architekturze została wyizolowana we własnym kontekście, wytyczając czytelną, pojęciową granicę otaczającą jej model:

1. **Kontekst Medyczny:** Zarządza cyklem życia wizyty i historią leczenia.
2. **Kontekst Lokalizacji:** Definiuje topologię placówki (budynki, oddziały, gabinety).
3. **Kontekst Personelu:** Przechowuje dane lekarzy i ich specjalizacje.
4. **Kontekst Tożsamości (IAM):** Odpowiada za uwierzytelnianie i sesje.
5. **Kontekst Dokumentów:** Realizuje techniczne zadania związane z plikami i analizą OCR.

---

## 2. Wymagania Funkcjonalne

### 2.1 Zarządzanie Kontem i Tożsamością
- **Logowanie i sesje:** Logowanie odbywa się przy użyciu adresu e-mail oraz hasła. System zapewnia mechanizm odświeżania tokenów sesji.
- **Rejestracja:** Publiczna rejestracja pacjenta wymusza podanie numeru PESEL oraz akceptację odpowiednich zgód.

### 2.2 Organizacja Pracy i Wizyt
- **Topologia:** Administratorzy mają możliwość definiowania hierarchii w placówce: *Placówka -> Oddział -> Gabinet*.
- **Planowanie:** Lekarze posiadają przypisane gabinety. Wizyty opierają się na systemie konfigurowalnych "slotów" z możliwością ich automatycznego zwalniania w przypadku anulowania.

### 2.3 Cyfrowa Dokumentacja
- **Rekord medyczny:** Dodanie jakiegokolwiek wpisu w rekordzie medycznym automatycznie i asynchronicznie zamyka powiązaną z nim wizytę (zmiana statusu na "Zakończona").
- **Przetwarzanie plików:** Upload pliku przez personel systemowo uruchamia proces ekstrakcji metadanych (OCR).

---

## 3. Przypadki Użycia (Usługi Dziedziny)

- **Rezerwacja Wizyty:** Pacjent wyszukuje lekarza w placówce, następnie wskazuje jeden lub kilka dostępnych slotów i dokonuje ostatecznej rezerwacji terminu.
- **Wypełnienie Rekordu Medycznego:** Lekarz dodaje diagnozę oraz receptę do wizyty pacjenta. Warunkiem końcowym tej akcji jest asynchroniczne zamknięcie statusu wizyty.
- **Obsługa Dokumentów Zewnętrznych:** Wysłanie pliku (np. skanu) przez lekarza lub pacjenta wywołuje usługę OCR, która automatycznie uzupełnia metadane bazując na treści przesłanego dokumentu.

---

## 4. Język Wszechobecny (Ubiquitous Language)
Kluczowym elementem modelowania Medflow jest spójny słownik pojęć. Hermetyzuje on wiedzę domenową i staje się jedynym obowiązującym językiem zarówno w komunikacji zespołu, jak i w dokumentacji oraz samym kodzie źródłowym (w obrębie danego Kontekstu Ograniczonego).

---

## 5. Modelowanie Taktyczne (Backend)
Projekt realizowany jest zgodnie ze ścisłymi zasadami **Domain-Driven Design (DDD)**. 
Główny nacisk kładziemy na tzw. *happy paths* (podstawowe poprawne ścieżki), precyzyjne mapowanie między warstwami i integrację kontekstów.

### 5.1 Styl architektury i zasady przewodnie
Backend został zbudowany jako **Modułowy Monolit**. Jest to jeden artefakt wykonywalny, w którym każdy kontekst ograniczony (Medyczny, Lokalizacji, Personelu, Tożsamości, Dokumentów) stanowi osobny, logicznie wydzielony moduł.

Wewnątrz każdego modułu stosowana jest **Architektura Portów i Adapterów (Heksagonalna)**. Opiera się na koncepcji czystej architektury (Clean Architecture), gdzie rdzeniem jest izlolowana dziedzina biznesowa.

#### Warstwy w architekturze heksagonalnej:
- **Warstwa Dziedziny (Domain Layer):** Najgłębsza część heksagonu. Czysty kod biznesowy bez zależności technologicznych. Zawiera agregaty (np. `Wizyta`, `Harmonogram`, `RekordMedyczny`), encje, obiekty wartości, zdarzenia dziedziny oraz interfejsy portów wyjściowych (np. interfejsy repozytoriów).
- **Warstwa Aplikacji (Application Layer):** Otacza dziedzinę. Posiada usługi aplikacyjne orkiestrujące przypadki użycia (np. pobranie z repozytorium -> akcja na agregacie -> zapis -> event). Znajdują się tu porty wejściowe dla adapterów napędzających (np. `RezerwacjaWizyty`).
- **Adaptery Wejściowe (Strona Napędzająca - Primary):** Inicjują akcje. Należą do nich kontrolery REST oraz wewnętrzni słuchacze zdarzeń, korzystające z portów wejściowych.
- **Adaptery Wyjściowe (Strona Napędzana - Secondary):** Technologie i mechanizmy wywoływane przez system za pomocą portów wyjściowych. Obejmują implementacje repozytoriów (bazy danych) i integracje międzymodułowe / zewnętrzne API.

#### Realizacja Zasad SOLID w projekcie:
- **SRP (Jedna odpowiedzialność):** Usługi aplikacyjne są niezwykle ziarniste. Każda usługa obsługuje wyłącznie jeden przypadek użycia (np. klasa `AnulowanieWizyty` odpowiada tylko za to).
- **OCP (Otwarte-zamknięte):** Poprzez wzorzec strategii (np. `TypKonsultacji`) i porty wyjściowe dodajemy nową logikę/adaptery bez modyfikacji rdzenia.
- **LSP (Podstawienie Liskov):** Adaptery wyjściowe można swobodnie wymieniać pod warunkiem implementowania danego portu, co nie psuje logiki dziedziny.
- **ISP (Segregacja interfejsów):** Definiowane są wąskie, wyspecjalizowane porty. Każdy korzeń agregatu posiada własny port (np. `RepozytoriumHarmonogramow`, `RepozytoriumWizyt`).
- **DIP (Odwrócenie zależności):** Implementacje utrwalania (adaptery) zależą od definicji portów wewnątrz dziedziny. To Rdzeń biznesowy decyduje czego potrzebuje.

### 5.2 Mapowanie, integracja kontekstów i translacja (ACL)
- **Hermetyzacja dziedziny:** Wszystkie żądania wejściowe (np. JSON z kontrolerów) mapowane są na Obiekty Poleceń (Command Objects), zanim trafią do usług aplikacyjnych. Model trwałości (Encje Bazy Danych) również jest oddzielony od modelu dziedziny — adapter zapisu mapuje dane, co chroni czystość agregatów.
- **Relacja Klient-Dostawca:** Kontekst Medyczny (klient) korzysta z danych Kontekstów Lokalizacji i Personelu (dostawcy). Definiuje u siebie porty wyjściowe (*Brama Lokalizacji*, *Brama Personelu*), by rozwiązywać tożsamość zasobów (np. `LekarzId`) poprzez adaptery po stronie infrastruktury.
- **Warstwa Zapobiegająca Uszkodzeniu (Anti-Corruption Layer - ACL):** Relacja Kontekstu Medycznego z systemem IAM (Tożsamość). Po stronie Medycznego istnieje dedykowany adapter wyjściowy, który pobiera model z IAM (usługa otwartego hosta) i tłumaczy go z technicznego obiektu `Konto` na obiekt dziedzinowy `DanePacjenta`. Chroni to Język Wszechobecny medycyny przed pojęciami systemu IAM.

### 5.3 Model integracji i wdrożenia
- **Deployment:** Aplikacja wdrażana jako **Modułowy Monolit** w pojedynczym procesie (jeden plik wykonywalny, np. jeden kontener). Ułatwia to utrzymanie przy jednoczesnym uniknięciu długu technicznego.
- **Separacja Danych:** Każdy moduł loguje się do bazy osobno i operuje **wyłącznie we własnym schemacie relacyjnym**. Kategoryczny zakaz złączeń bazodanowych (JOIN) pomiędzy różnymi kontekstami.
- **Przechowywanie Plików:** Pliki medyczne przechowywane w zewnętrznym magazynie obiektowym (Object Storage), a nie w bazie relacyjnej.
- **Zewnętrzne Procesy:** Usługa OCR działa jako zewnętrzny, osobny proces, z którym komunikujemy się asynchronicznie (po zdarzeniu `DokumentPrzeslany`).
- **Dystrybucja Zdarzeń:** Zdarzenia dziedziny wędrują wewnątrz aplikacji przez wbudowaną, pamięciową szynę zdarzeń. Podział struktury kodu odzwierciedla wzorzec DDD (katalogi `domain`, `application`, `infrastructure`).
