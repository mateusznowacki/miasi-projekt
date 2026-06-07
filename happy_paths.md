# Główne Procesy Biznesowe (Happy Paths)

Ten plik opisuje podstawowe przepływy (usługi aplikacji i zdarzenia dziedziny) w systemie Medflow. 
Komunikacja wykraczająca poza jeden agregat odbywa się asynchronicznie poprzez publikowanie **zdarzeń dziedziny (Domain Events)**, zapewniając spójność ostateczną.

---

## Proces 1: Rezerwacja wizyty przez pacjenta
1. Z adaptera REST do usługi aplikacyjnej trafia żądanie rezerwacji z identyfikatorami: `LekarzId`, listą wybranych slotów oraz `TypKonsultacji`.
2. Usługa pobiera przez port wyjściowy agregat **Harmonogram** danego lekarza.
3. Wywoływana jest metoda dziedzinowa `zarezerwujSloty()` na Harmonogramie. Sprawdza ona dostępność slotów i zmienia obiekty wartości `StanSlotu` na „Zajęty”.
4. Usługa aplikacyjna tworzy nowy agregat **Wizyta**.
5. Konstruktor Wizyty ustala początkowy `StatusWizyty` i wiąże ze sobą odpowiednie `SlotId`.
6. Usługa zapisuje zmieniony Harmonogram oraz nową Wizytę (poprzez adaptery trwałości/repozytoria).
7. Wizyta emituje zdarzenie dziedziny: `WizytaZarezerwowana`.

---

## Proces 2: Anulowanie wizyty i zwolnienie zasobów
*Przypadek ilustrujący działanie spójności ostatecznej opartej na zdarzeniach.*
1. System żąda anulowania wcześniej zarezerwowanej wizyty (po `WizytaId`).
2. Usługa pobiera agregat **Wizyta** i wywołuje na nim metodę `anuluj()`.
3. Wizyta zostaje zapisana. Do systemu (szyny zdarzeń) trafia zdarzenie `WizytaAnulowana`, przenoszące w ładunku listę przypisanych do niej slotów.
4. Słuchacz zdarzeń (reaktywny adapter wejściowy) powiązany z subdomeną harmonogramów przechwytuje zdarzenie i wywołuje usługę zwalniania.
5. Na załadowanym agregacie **Harmonogram** wywoływana jest metoda `zwolnijSloty()` z listą zwracanych terminów.
6. Harmonogram publikuje zdarzenie dziedziny: `SlotZwolniony`.

---

## Proces 3: Wypełnienie rekordu i automatyczne zamknięcie wizyty
1. Lekarz (przez adapter wejściowy) dodaje diagnozę, objawy oraz ewentualną receptę dla podanego `WizytaId`.
2. Usługa tworzy nowy agregat **RekordMedyczny** i zapisuje go w repozytorium.
3. Zgłaszane jest zdarzenie: `RekordMedycznyUtworzony`.
4. Słuchacz zdarzeń odbiera informację o powstaniu rekordu medycznego i reaguje zamknięciem procesu — pobiera powiązaną **Wizytę**.
5. Operacja `zakoncz()` na Wizycie ustawia `StatusWizyty` na „Zakończona” (stan końcowy).
6. Wizyta emituje zdarzenie: `WizytaZakonczona`.

---

## Proces 4: Przesłanie dokumentu i ekstrakcja metadanych (OCR)
1. Lekarz lub pacjent przesyła plik badania (np. skan) przez adapter REST.
2. Usługa tworzy agregat **Dokument**, zapisuje `ReferencjaPliku` oraz `TypPliku`, a `StatusOCR` ustawia na „oczekujący”. Fizyczny plik trafia do zewnętrznego magazynu plików (object storage).
3. Dokument emituje zdarzenie: `DokumentPrzeslany`.
4. Adapter integracyjny, wyzwalany tym zdarzeniem, woła asynchronicznie zewnętrzny silnik OCR.
5. Po zakończeniu analizy przez silnik OCR, usługa aplikacyjna wywołuje `uzupelnijMetadane()` na Dokumencie, zapisuje wykryte `Metadane` i zmienia `StatusOCR` na „ukończony”.
6. Dokument publikuje zdarzenie: `MetadaneWyekstrahowane`.
