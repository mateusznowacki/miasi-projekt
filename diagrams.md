# Diagramy Architektoniczne

Ten plik przechowuje struktury i opisy kluczowych diagramów projektowych i architektonicznych systemu Medflow.

## Diagram: Architektura heksagonalna - moduł «Kontekst Medyczny (Wizyta)»

Struktura architektury heksagonalnej dedykowana dla modułu medycznego. Przepływ sterowania biegnie od lewej (Strona wejściowa), przez rdzeń biznesowy, aż do prawej (Strona wyjściowa).

### 1. Strona Wejściowa (Primary / Driving Side)
Są to elementy, które inicjują interakcję z systemem (użytkownicy, inne systemy, zdarzenia).

* **Aktorzy / Zewnętrzne źródła:**
  * Klient UI *(komunikacja przez REST / HTTP)*
  * Inne moduły *(zdarzenia dziedziny z szyny zdarzeń)*

* **Adaptery wejściowe:**
  * `«adapter wejściowy»` Kontroler REST
  * `«adapter wejściowy»` Słuchacz zdarzeń

* **Porty (interfejsy wejściowe):**
  * `«port»` RezerwacjaWizyty / AnulowanieWizyty
  * `«port»` Odbiór zdarzeń

---

### 2. Rdzeń (The Core)
Centralna część systemu zawierająca logikę biznesową (heksagony). Nic z rdzenia nie "wie" o istnieniu bazy danych czy REST.

* **Zewnętrzny heksagon (Warstwa aplikacji):**
  * usługi aplikacyjne
  * porty (wejściowe i wyjściowe)

* **Wewnętrzny heksagon (Warstwa dziedziny):**
  * agregaty: **Wizyta** oraz **Harmonogram**
  * encje, obiekty wartości oraz zdarzenia dziedziny

---

### 3. Strona Wyjściowa (Secondary / Driven Side)
Są to elementy, z którymi system komunikuje się, aby wykonać swoje zadania (zapis do bazy, zapytania do innych modułów).

* **Porty wyjściowe (interfejsy wyjściowe w dziedzinie):**
  * `«port wyjściowy»` Tożsamość
  * `«port wyjściowy»` Repozytorium
  * `«port wyjściowy»` Brama Placówki
  * `«port wyjściowy»` Brama Personelu

* **Adaptery wyjściowe (infrastruktura):**
  * `«adapter / ACL»` Tożsamość (tłumaczenie IAM -> DanePacjenta)
  * `«adapter wyjściowy»` Trwałość (implementacja repozytorium)
  * `«adapter»` Brama Placówki
  * `«adapter»` Brama Personelu

* **Zewnętrzne systemy i infrastruktura:**
  * Moduł IAM *(UOH + JO)*
  * Baza danych *(odizolowany schemat Medyczny)*
  * Moduł Placówki
  * Moduł Personelu

---

## Diagram: Klasy i relacje (Domain-Driven Design) - Moduł Medyczny

Poniższe zestawienie przedstawia główne elementy modelu dziedziny na podstawie wzorców DDD. Elementy pogrupowano według ich stereotypów.

### 🔷 Encje (`«encja»`)
Główne obiekty biznesowe posiadające własną tożsamość, atrybuty i metody.

* **Wizyta**
  * **Atrybuty:** `- WizytaId`, `- PacjentId`, `- LekarzId`, `- sloty: SlotId [1..*]`
  * **Metody:** `+ zarezerwuj()`, `+ anuluj()`, `+ zakoncz()`
* **Harmonogram**
  * **Atrybuty:** `- HarmonogramId`, `- LekarzId`
  * **Metody:** `+ dodajTerminy()`, `+ zarezerwujSloty()`, `+ zwolnijSloty()`
* **Slot**
  * **Atrybuty:** `- SlotId`
  * *(Powiązany relacją kompozycji z Harmonogramem: `1..*`)*

### 🟨 Usługi aplikacji (`«usługa aplikacji»`)
Komponenty koordynujące zadania (korzystają z encji - relacja `«use»`).

* **PrzegladanieWizyt** *(używa: Wizyta)*
* **AnulowanieWizyty** *(używa: Wizyta)*
* **RezerwacjaWizyty** *(używa: Wizyta, Harmonogram)*
* **PrzegladanieSlotow** *(używa: Harmonogram)*
* **ZarzadzanieKalendarzem** *(używa: Harmonogram)*

### 🟩 Obiekty wartości (`«obiekt wartości»`)
Obiekty bez własnej tożsamości, opisujące cechy i stany encji.

* **TypKonsultacji** *(powiązany z encją Wizyta)*
* **StatusWizyty** *(powiązany z encją Wizyta)*
* **OkresCzasu** *(powiązany z encją Slot)*
* **StanSlotu** *(powiązany z encją Slot)*

### 🟧 Zdarzenia dziedziny (`«zdarzenie dziedziny»`)
Fakty zaistniałe w systemie (generowane przez encje - relacja `«create»`).

* **Generowane przez encję Wizyta:**
  * `WizytaZarezerwowana`
  * `WizytaAnulowana`
  * `WizytaZakonczona`
* **Generowane przez encję Harmonogram:**
  * `SlotZwolniony`

---

## Diagram: Klasy i relacje - Moduł Dokumentacji Medycznej (Dziedzina Główna)

### 🔷 Encje (`«encja»`)
* **RekordMedyczny**
  * **Atrybuty:** `- RekordId`, `- WizytaId`, `- PacjentId`, `- LekarzId`, `- notatki`
  * **Metody:** `+ utworz()`, `+ aktualizuj()`

### 🟨 Usługi aplikacji (`«usługa aplikacji»`)
Wszystkie używają encji `RekordMedyczny`.
* **WypelnienieRekorduMedycznego**
* **AktualizacjaRekordu**
* **PrzegladanieDokumentacji**

### 🟩 Obiekty wartości (`«obiekt wartości»`)
Elementy opisujące składowe encji `RekordMedyczny`:
* **Diagnoza**
* **Objaw**
* **Recepta**
* **Zalecenie**

### 🟧 Zdarzenia dziedziny (`«zdarzenie dziedziny»`)
Zdarzenia generowane przez `RekordMedyczny`:
* `RekordMedycznyUtworzony`
* `RekordMedycznyZaktualizowany`

---

## Diagram: Klasy i relacje - Dziedziny Pomocnicze i Generyczne

### 1. «Kontekst Placówka» (Dziedzina Pomocnicza)
* **«encja» Placowka**
  * **Atrybuty:** `- PlacowkaId`
  * **Metody:** `+ dodajOddzial()`, `+ dodajGabinet()`
  * **Powiązane obiekty wartości:** `Adres` (1), `NazwaPlacowki` (1)
* **«encja» Oddzial** *(relacja `1..*` z Placowką)*
  * **Atrybuty:** `- OddzialId`, `- nazwa`
* **«encja» Gabinet** *(relacja `1..*` z Oddziałem)*
  * **Atrybuty:** `- GabinetId`
  * **Powiązane obiekty wartości:** `Lokalizacja` (budynek, piętro, numer)

### 2. «Kontekst Personel» (Dziedzina Pomocnicza)
* **«encja» Lekarz**
  * **Atrybuty:** `- LekarzId`, `- PlacowkaId`, `- OddzialId`, `- GabinetId`
  * **Powiązane obiekty wartości:** `Specjalizacja` (1), `ImieNazwisko` (1)
* **«encja» PracownikAdministracyjny**
  * **Atrybuty:** `- PracownikId`, `- PlacowkaId`
  * **Powiązane obiekty wartości:** `Stanowisko`, `ImieNazwisko`

### 3. «Kontekst Tożsamość i Dostęp (IAM)» (Dziedzina Generyczna)
* **«encja — korzeń agregatu» Konto**
  * **Atrybuty:** `- KontoId`, `- hasloHash` *(atrybut techniczny)*
  * **Metody:** `+ zaloguj()`, `+ wyloguj()`, `+ zarejestruj()`, `+ aktualizujDane()`, `+ zarzadzajZgodami()`
  * **Powiązane obiekty wartości:**
    * `ImieNazwisko` (1) *(z relacją do `Email`)*
    * `Rola` (1)
    * `Adres` (0..1)
    * `PESEL` (0..1)
    * `Telefon` (0..1)
    * `Zgoda` (*)
    * `Token` (0..1)
