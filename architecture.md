# Szczegóły Architektury i Agregatów DDD

## 1. Kontekst Wizyta (Dziedzina Główna)
Odpowiada za planowanie i realizację wizyt oraz za kalendarz dostępności lekarzy. Wyróżniono w nim dwa agregaty wyznaczające granice spójności.

### Agregat: Wizyta
- **Korzeń agregatu:** encja Wizyta.
- **Tożsamość:** obiekt wartości `WizytaId`.
- **Skład i atrybuty:** referencyjne obiekty wartości `PacjentId` oraz `LekarzId` (odwołania rozłączne do innych kontekstów), obiekty wartości `TypKonsultacji` i `StatusWizyty`, a także lista zarezerwowanych slotów w postaci obiektów tożsamości `SlotId` [1..*].
- **Niezmienniki:**
  - Zarezerwowane sloty muszą dotyczyć tego samego lekarza i zachowywać ciągłość czasową.
  - Przejścia `StatusWizyty` są nieodwracalne: ze stanu „Zarezerwowana” można przejść do stanu „Anulowana” lub „Zakończona”, ale stany końcowe są już ostateczne.
- **Zachowania:** `zarezerwuj()`, `anuluj()`, `zakoncz()`.

### Agregat: Harmonogram
- **Korzeń agregatu:** encja Harmonogram. Reprezentuje kalendarz dostępności przypisany danemu lekarzowi.
- **Tożsamość:** obiekt wartości `HarmonogramId`.
- **Skład i atrybuty:** referencyjny `LekarzId`. Stan agregatu tworzą encje potomne `Slot` (`SlotId`), które przechowują obiekty wartości `OkresCzasu` (data rozpoczęcia i zakończenia) oraz `StanSlotu` (enumeracja „Wolny” / „Zajęty”).
- **Niezmienniki:**
  - Ochrona przed overbookingiem (podwójną rezerwacją), ten sam Slot nie może przejść w stan „Zajęty”, jeżeli już w nim jest.
  - Usunąć lub zmienić można tylko termin jeszcze niezarezerwowany.
- **Zachowania:** `dodajTerminy()`, `zarezerwujSloty()`, `zwolnijSloty()`.

---

## 2. Kontekst Dokumentacja Medyczna (Dziedzina Główna)
Zarządza rekordami medycznymi pacjentów powiązanymi z odbytymi wizytami.

### Agregat: Rekord Medyczny
- **Korzeń agregatu:** encja RekordMedyczny.
- **Tożsamość:** obiekt wartości `RekordId`.
- **Skład i atrybuty:** referencyjne `WizytaId`, `PacjentId` oraz `LekarzId` (umożliwiają pobieranie rekordów po wizycie, pacjencie lub lekarzu) oraz obiekty wartości opisujące stan zdrowia: `Diagnoza`, `Objaw`, `Recepta`, `Zalecenie`, a także notatki.
- **Niezmienniki:**
  - Rekord musi odwoływać się do istniejącej, nieodwołanej Wizyty.
  - Aktualizacje są dozwolone, ale zachowują historię zmian, wcześniejsze wersje pozostają dostępne.
- **Zachowania:** `utworz()`, `aktualizuj()`.

---

## 3. Kontekst Placówka (Dziedzina Pomocnicza)
Odpowiada za strukturę infrastruktury medycznej.
- **Korzeń agregatu:** encja Placowka (`PlacowkaId`).
- **Skład:** obiekty wartości `Adres` oraz `NazwaPlacowki`. Placówka komponuje listę encji potomnych `Oddzial` (`OddzialId`), a te z kolei encje `Gabinet` (`GabinetId`) opisane obiektem wartości `Lokalizacja` (budynek, piętro, numer). Odwzorowuje to topologię „placówka → oddział → gabinet” z wymagań.
- **Zachowania:** `dodajOddzial()`, `dodajGabinet()`.

---

## 4. Kontekst Personel (Dziedzina Pomocnicza)
Zarządza pracownikami placówek, zarówno lekarzami, jak i personelem administracyjnym.
- **Agregat Lekarz:** korzeń Lekarz (`LekarzId`); obiekty wartości `ImieNazwisko` i `Specjalizacja` oraz identyfikatory referencyjne `PlacowkaId`, `OddzialId` i `GabinetId`.
- **Agregat Pracownik Administracyjny:** korzeń PracownikAdministracyjny (`PracownikId`); obiekty wartości `ImieNazwisko` i `Stanowisko` oraz referencyjny `PlacowkaId`.

---

## 5. Kontekst Tożsamość i Dostęp - IAM (Dziedzina Generyczna)
Zarządza uwierzytelnianiem, kontami użytkowników oraz zgodami na przetwarzanie danych.
- **Korzeń agregatu:** encja Konto (`KontoId`).
- **Skład:** obiekty wartości `Email`, `Rola`, `PESEL`, `Telefon`, `Adres`, `ImieNazwisko`, `Zgoda` oraz `Token` (sesja). Dodatkowo encja przechowuje czysto techniczny, zhaszowany atrybut hasła, wyłączony z operacji dziedzinowych.
- **Niezmienniki:** rejestracja pacjenta wymaga podania imienia, nazwiska, adresu e-mail, hasła, numeru PESEL i telefonu oraz akceptacji wymaganych zgód.
- **Zachowania:** `zaloguj()`, `wyloguj()`, `zarejestruj()`, `aktualizujDane()`, `zarzadzajZgodami()`.

> **Uwaga projektowa:** strategia nie wydziela osobnego kontekstu „Pacjent”, dlatego dane pacjenta (kartotekę: dane osobowe, kontaktowe i zgody) ulokowaliśmy w Kontekście Tożsamości i Dostępu - to tu zachodzi rejestracja oraz zarządzanie zgodami, a `PacjentId` jest identyfikatorem konta w roli pacjenta. „Pełny profil pacjenta” z wymagań jest więc modelem odczytowym złożonym z danych tego kontekstu (dane osobowe, zgody) oraz Kontekstu Dokumentacja Medyczna (dane medyczne).

---

## Usługi aplikacji i zdarzenia dziedziny
Usługa aplikacji nie zawiera reguł biznesowych - wykonuje tylko akcje niebiznesowe: zarządza cyklem życia transakcji, wstrzykuje zależności i przekazuje sterowanie do dziedziny. Komunikacja wykraczająca poza granicę jednego agregatu (a tym bardziej kontekstu) odbywa się asynchronicznie, przez publikowanie zdarzeń dziedziny, co zapewnia spójność ostateczną całego systemu.

### Proces 1: Rezerwacja wizyty
1. Z adaptera REST do usługi aplikacyjnej trafia żądanie rezerwacji (od pacjenta lub pracownika administracyjnego) z identyfikatorami: `LekarzId`, listą wybranych slotów oraz `TypKonsultacji`.
2. Usługa pobiera przez port wyjściowy agregat Harmonogram danego lekarza.
3. Wywoływana jest metoda `zarezerwujSloty()` na Harmonogramie. Sprawdza ona dostępność slotów i zmienia obiekty wartości `StanSlotu` na „Zajęty”.
4. Usługa tworzy nowy agregat Wizyta.
5. Konstruktor Wizyty ustala początkowy `StatusWizyty` i wiąże ze sobą odpowiednie `SlotId`.
6. Usługa zapisuje zmieniony Harmonogram oraz nową Wizytę przez adaptery trwałości.
7. Wizyta emituje zdarzenie dziedziny `WizytaZarezerwowana`.

### Proces 2: Anulowanie wizyty i zwolnienie zasobów
Ten przypadek dobrze pokazuje działanie spójności ostatecznej opartej na zdarzeniach (między dwoma agregatami tego samego kontekstu):
1. System żąda anulowania wcześniej zarezerwowanej wizyty (`WizytaId`).
2. Usługa pobiera agregat Wizyta i wywołuje na nim `anuluj()`.
3. Wizyta zostaje zapisana, a do systemu trafia zdarzenie `WizytaAnulowana` przenoszące w ładunku listę przypisanych slotów.
4. Słuchacz zdarzeń (reaktywny adapter wejściowy) przechwytuje zdarzenie i wywołuje usługę zwalniania terminów.
5. Na załadowanym Harmonogramie wywoływana jest metoda `zwolnijSloty()` z listą zwracanych terminów, co przywraca je do kalendarza dostępności.
6. Harmonogram publikuje zdarzenie dziedziny `SlotZwolniony`.

### Proces 3: Utworzenie rekordu i zamknięcie wizyty (integracja między kontekstami)
1. Lekarz przez adapter wejściowy Kontekstu Dokumentacja Medyczna dodaje diagnozę, objawy, zalecenia oraz ewentualną receptę dla podanego `WizytaId`.
2. Usługa tworzy nowy agregat RekordMedyczny i zapisuje go w repozytorium.
3. Zgłaszane jest zdarzenie `RekordMedycznyUtworzony`.
4. Słuchacz w Kontekście Wizyta odbiera to zdarzenie i reaguje zamknięciem powiązanej Wizyty.
5. Operacja `zakoncz()` ustawia `StatusWizyty` na „Zakończona” (stan końcowy), po czym Wizyta emituje zdarzenie `WizytaZakonczona`.

---

## Mapowanie, integracja kontekstów i translacja (ACL)
Wszystkie żądania wejściowe (np. pliki JSON przyjmowane w REST) są mapowane przez kontrolery na Obiekty Poleceń (Command Objects), zanim trafią do usług aplikacyjnych. Model dziedziny nie pokrywa się też z modelem trwałości: adapter zapisu wyciąga dane z agregatów i przepisuje je do dedykowanych Encji Persystencji na poziomie bazy. Chroni to enkapsulację dziedziny.

Wymiana informacji między modułami odbywa się zgodnie z mapą kontekstów:
- Konteksty główne Wizyta i Dokumentacja Medyczna współpracują ze sobą wyłącznie przez zdarzenia dziedziny (`RekordMedycznyUtworzony` → zamknięcie wizyty) i odwołania po identyfikatorach (`WizytaId`) - bez wspólnego modelu, w duchu spójności ostatecznej.
- Kontekst Wizyta jest klientem pomocniczych kontekstów Placówka oraz Personel w relacji Klient–Dostawca. W jego rdzeniu zdefiniowano porty wyjściowe (Brama Placówki i Brama Personelu), dzięki którym w adapterze poza heksagonem można rozwiązać dane zasobów na podstawie np. `LekarzId`.
- Relacja z generycznym Kontekstem Tożsamość i Dostęp opiera się na wzorcu Warstwy Zapobiegającej Uszkodzeniu (ACL). Dedykowany adapter wyjściowy pobiera model wystawiony przez usługę otwartego hosta (UOH + JO) i tłumaczy złożone Konto na ograniczony, dopasowany do dziedziny medycznej obiekt `DanePacjenta`. Dzięki temu język wszechobecny kontekstów medycznych pozostaje czysty i nieobciążony pojęciami z systemu IAM.
