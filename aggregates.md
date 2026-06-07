# Agregaty Systemu Medflow

Ten plik zawiera szczegółowy opis agregatów i encji zdefiniowanych w ramach poszczególnych Kontekstów Ograniczonych systemu Medflow.

## 1. Kontekst Medyczny (Dziedzina Główna)

### Agregat: Wizyta
- **Korzeń agregatu:** encja `Wizyta`.
- **Tożsamość:** obiekt wartości `WizytaId`.
- **Skład i atrybuty:** 
  - referencyjne obiekty wartości `PacjentId` oraz `LekarzId` (odwołania rozłączne do innych agregatów),
  - obiekty wartości `TypKonsultacji` i `StatusWizyty`,
  - lista zarezerwowanych slotów w postaci obiektów tożsamości `SlotId` [1..*].
- **Niezmienniki (Invariants):** zarezerwowane sloty muszą dotyczyć tego samego lekarza i zachowywać ciągłość czasową. Przejścia statusu `StatusWizyty` są nieodwracalne: ze stanu „Zarezerwowana” można przejść do stanu „Anulowana” lub „Zakończona”, ale stany końcowe są już ostateczne.
- **Zachowania:** `zarezerwuj()`, `anuluj()`, `zakoncz()`.

### Agregat: Harmonogram
- **Korzeń agregatu:** encja `Harmonogram`. Reprezentuje grafik pracy przypisany danemu lekarzowi.
- **Tożsamość:** obiekt wartości `HarmonogramId`.
- **Skład i atrybuty:** 
  - referencyjny obiekt wartości `LekarzId`.
  - stan agregatu tworzą encje potomne `Slot` (`SlotId`), które przechowują obiekty wartości `OkresCzasu` (data rozpoczęcia i zakończenia) oraz `StanSlotu` (enumeracja „Wolny” / „Zajęty”).
- **Niezmienniki (Invariants):** ochrona przed overbookingiem (podwójną rezerwacją) — ten sam Slot nie może przejść w stan „Zajęty”, jeżeli już w nim jest. Rezerwacja sprawdza całą kolekcję slotów.
- **Zachowania:** `zarezerwujSloty()`, `zwolnijSloty()`.

### Agregat: Rekord Medyczny
- **Korzeń agregatu:** encja `RekordMedyczny`.
- **Tożsamość:** obiekt wartości `RekordId`.
- **Skład i atrybuty:** 
  - referencyjny obiekt wartości `WizytaId`,
  - obiekty wartości opisujące stan zdrowia: `Diagnoza`, `Objaw`, `Recepta` (lek wraz z wytycznymi dawkowania),
  - notatki.
- **Niezmienniki (Invariants):** rekord musi odwoływać się do istniejącej, nieodwołanej Wizyty. Po utworzeniu jego treść medyczna jest już niemodyfikowalna (stanowi historię).
- **Zachowania:** operacja wytwórcza `utworz()`.

---

## 2. Kontekst Lokalizacji (Dziedzina Pomocnicza)
Odpowiada za reprezentację infrastruktury medycznej.

### Agregat: Placówka
- **Korzeń agregatu:** encja `Placowka`.
- **Tożsamość:** obiekt wartości `PlacowkaId`.
- **Skład:** obiekty wartości `Adres` oraz `NazwaPlacowki`. Placówka komponuje listę encji potomnych `Oddzial` (`OddzialId`), a te z kolei encje `Gabinet` (`GabinetId`).

---

## 3. Kontekst Personelu (Dziedzina Pomocnicza)
Zarządza zasobami lekarskimi.

### Agregat: Lekarz
- **Korzeń agregatu:** encja `Lekarz`.
- **Tożsamość:** obiekt wartości `LekarzId`.
- **Skład:** obiekty wartości `ImieNazwisko` i `Specjalizacja`. Lekarz wskazuje też na zasoby przestrzenne przez identyfikatory referencyjne `PlacowkaId` i `GabinetId`.

---

## 4. Kontekst Tożsamości — IAM (Dziedzina Generyczna)
Zarządza uwierzytelnianiem użytkowników.

### Agregat: Konto
- **Korzeń agregatu:** encja `Konto`.
- **Tożsamość:** obiekt wartości `KontoId`.
- **Skład i atrybuty:** obiekty wartości `Email`, `Rola`, `PESEL`, `Zgoda`, `Token`. Dodatkowo encja przechowuje czysto techniczny, zhaszowany atrybut hasła, wyłączony z operacji dziedzinowych.
- **Zachowania:** `zaloguj()`, `zarejestruj()`, `resetHasla()`.

---

## 5. Kontekst Dokumentów (Dziedzina Generyczna)
Realizuje techniczną obsługę plików badań oraz proces OCR.

### Agregat: Dokument
- **Korzeń agregatu:** encja `Dokument`.
- **Tożsamość:** obiekt wartości `DokumentId`.
- **Skład i atrybuty:** obiekty wartości `ReferencjaPliku` (wskazanie pliku w magazynie), `TypPliku`, `Metadane` oraz `StatusOCR` (enumeracja stanu przetwarzania).
- **Zachowania:** `przeslij()`, `uzupelnijMetadane()`.
