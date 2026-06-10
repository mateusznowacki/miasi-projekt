# Architektura i Konteksty Medflow (DDD + Hexagonal)

## Dziedziny i Konteksty
W systemie Medflow wdrażamy Domain-Driven Design (DDD) w architekturze heksagonalnej. Każda poddziedzina została wyizolowana we własnym kontekście ograniczonym (Bounded Context), wytyczając czytelną, pojęciową granicę otaczającą jej model.

1. **Dziedzina Główna - Zarządzanie Opieką Medyczną** (Kontekst: Zarządzanie opieką medyczną)
   - Obejmuje krytyczne procesy biznesowe (wizyty, diagnozy i dokumentacje medyczną), które stanowią o unikalności systemu. To tutaj realizowana jest największa wartość biznesowa.
   - Odpowiada za harmonogramowanie wizyt oraz realizację procedur medycznych.

2. **Dziedziny Pomocnicze** (Modelują istotne, ale nie kluczowe fragmenty biznesu, specyficzne dla domeny medycznej)
   - **Pacjent** (Kontekst: Pacjent): Zajmuje się zarządzaniem pacjentami placówki. Przechowuje dane osobowe oraz dane medyczne pacjentów.
   - **Personel** (Kontekst: Personel): Zajmuje się zarządzaniem pracownikami placówki. Przechowuje dane pracowników placówki (lekarzy i personelu).

3. **Dziedzina Generyczna - Zarządzanie dostępem i tożsamością** (Kontekst: Tożsamość i Dostęp)
   - Obsługa kont, logowania i bezpieczeństwa. Odpowiada za logowanie, zgody, bezpieczeństwo.

---

## Wizja dziedziny i wymagania funkcjonalne
System Medflow wspiera placówki medyczne w kompleksowej koordynacji procesów leczenia pacjentów, od rejestracji wizyty, przez jej realizację, aż po prowadzenie dokumentacji medycznej. Główną wartością biznesową systemu jest skrócenie czasu oczekiwania pacjentów, zwiększenie efektywności wykorzystania zasobów medycznych oraz poprawa jakości i ciągłości opieki.

Wymagania funkcjonalne, stanowiące o celach systemu, kategoryzuje się następująco:
- System musi umożliwiać użytkownikom korzystanie z systemu w różnych rolach: gościa, pacjenta, lekarza, pracownika administracyjnego oraz administratora systemu.
- System musi ograniczać dostęp do zasobów i funkcji w zależności od roli zalogowanego użytkownika.

### 1. Wymagania dla kontekstu Tożsamość i Dostęp
- System musi umożliwiać użytkownikowi logowanie za pomocą adresu e-mail i hasła.
- System musi umożliwiać użytkownikowi wylogowanie się z systemu.

### 2. Wymagania dla kontekstu Pacjent
- Gość może zarejestrować się jako pacjent, podając imię, nazwisko, adres e-mail, hasło, numer PESEL oraz numer telefonu.
- System musi pozwalać pacjentowi oraz personelowi na pobranie pełnego profilu pacjenta zawierającego dane osobowe oraz medyczne.
- Pacjent musi mieć możliwość aktualizacji swoich danych osobowych.
- Lekarz lub personel musi mieć możliwość aktualizacji danych medycznych pacjenta.
- System musi pozwalać na pobranie historii medycznej dla wskazanego pacjenta.
- System musi udostępniać personelowi oraz lekarzom funkcję wyszukiwania i listowania pacjentów.

### 3. Wymagania dla kontekstu Personel
- Administrator systemu musi mieć możliwość utworzenia konta lekarza, powiązując je z oddziałem oraz definiując jego specjalizację.
- Administrator systemu musi mieć możliwość utworzenia konta pracownika administracyjnego definiując jego stanowisko.
- Administrator systemu musi mieć możliwość aktualizacji danych lekarza, w tym specjalizacji oraz przypisania do oddziału.
- Administrator systemu musi mieć możliwość aktualizacji danych pracownika administracyjnego, w tym stanowiska.
- Administrator systemu musi mieć możliwość usunięcia konta lekarza lub pracownika administracyjnego.
- System musi umożliwiać pobranie szczegółowego profilu lekarza lub pracownika administracyjnego.
- System musi udostępniać funkcję wyszukiwania oraz listowania personelu.

### 4. Wymagania dla kontekstu Zarządzanie Opieką Medyczną
- Lekarz lub pracownik administracyjny musi mieć możliwość masowego dodawania terminów do kalendarza dostępności lekarza, określając gabinet oraz czas rozpoczęcia i zakończenia wizyty.
- Lekarz lub pracownik administracyjny musi mieć możliwość usuwania terminów z kalendarza dostępności, które nie zostały jeszcze zarezerwowane.
- Lekarz lub pracownik administracyjny musi mieć możliwość aktualizacji istniejących terminów w kalendarzu dostępności, w tym zmiany gabinetu lub czasu.
- System musi udostępniać listę dostępnych terminów dla wybranego lekarza w konkretnym dniu.
- Pacjent lub pracownik administracyjny musi mieć możliwość zarezerwowania wizyty w dostępnym terminie.
- Pacjent lub pracownik administracyjny musi mieć możliwość anulowania wizyty, co automatycznie przywraca termin do kalendarza dostępności lekarza.
- System musi udostępniać listę wizyt pacjenta lub lekarza, z możliwością filtrowania na wizyty przyszłe i przeszłe.
- System musi pozwalać na pobranie rekordu medycznego na podstawie identyfikatora wizyty.

---

## Scenariusze Przypadków Użycia (Use Cases)

### Kontekst: Tożsamość i Dostęp

#### PU1 - Logowanie
**Aktorzy:** Użytkownik (Gość, Pacjent, Lekarz, Pracownik administracyjny, Administrator systemu)  
**Cel:** Użytkownik uzyskuje dostęp do systemu Medflow poprzez uwierzytelnienie swoich danych.  
**Warunki wstępne:** Użytkownik posiada aktywne konto w systemie. Użytkownik nie jest aktualnie zalogowany.  
**Warunki końcowe:** Użytkownik jest zalogowany i uzyskuje dostęp do funkcji odpowiadających jego roli w systemie.  
**Scenariusz:**
1. Użytkownik inicjuje wykonanie PU1, przechodząc do strony logowania systemu Medflow.
2. System wyświetla formularz logowania z polami na login (adres e-mail) i hasło.
3. Użytkownik wprowadza swoje dane uwierzytelniające i zatwierdza formularz.
4. System weryfikuje poprawność podanych danych.
5. System rozpoznaje rolę Użytkownika i przekierowuje go do odpowiedniego panelu.
6. Zamiast kroku 5 system wyświetla komunikat o błędnych danych, jeśli weryfikacja w kroku 4 nie powiodła się; Użytkownik może ponowić próbę.

#### PU2 - Wylogowanie
**Aktorzy:** Użytkownik (Pacjent, Lekarz, Pracownik administracyjny, Administrator systemu)  
**Cel:** Użytkownik bezpiecznie kończy sesję w systemie Medflow.  
**Warunki wstępne:** Użytkownik jest aktualnie zalogowany w systemie.  
**Warunki końcowe:** Sesja Użytkownika zostaje zakończona; dostęp do chronionych zasobów jest zablokowany.  
**Scenariusz:**
1. Użytkownik inicjuje wykonanie PU2, wybierając opcję „Wyloguj" w interfejsie systemu.
2. System wyświetla prośbę o potwierdzenie wylogowania.
3. Użytkownik potwierdza chęć wylogowania.
4. System unieważnia token sesji Użytkownika i czyści dane sesyjne.
5. System przekierowuje Użytkownika na stronę logowania.
6. Zamiast kroków 2–3 system może wylogować Użytkownika automatycznie po wygaśnięciu sesji, bez konieczności potwierdzenia.

### Kontekst: Zarządzanie Opieką Zdrowotną

#### PU3 - Zarządzanie kalendarzem dostępności
**Aktorzy:** Lekarz, Pracownik administracyjny  
**Cel:** Aktor definiuje lub modyfikuje harmonogram dostępności lekarza, umożliwiając pacjentom rezerwację wizyt w odpowiednich terminach.  
**Warunki wstępne:** Aktor jest zalogowany w systemie.  
**Warunki końcowe:** Kalendarz dostępności lekarza zostaje zaktualizowany i jest widoczny dla użytkowników systemu.  
**Scenariusz:**
1. Aktor inicjuje wykonanie PU3, przechodząc do modułu zarządzania kalendarzem.
2. System wyświetla aktualny harmonogram dostępności lekarza w widoku tygodniowym lub miesięcznym.
3. Aktor wybiera akcję: dodanie nowego przedziału czasowego, edycja istniejącego lub usunięcie terminu.
4. Aktor określa parametry terminu (data, godzina, długość wizyty, typ wizyty).
5. System waliduje wprowadzone dane pod kątem konfliktów z istniejącymi rezerwacjami.
6. System zapisuje zmiany i aktualizuje kalendarz dostępności.
7. Zamiast kroku 6 system wyświetla komunikat o konflikcie terminów, jeśli walidacja w kroku 5 wykryła nakładające się wpisy; Aktor wraca do kroku 3.

#### PU4 - Przeglądanie kalendarza dostępności
**Aktorzy:** Pacjent, Lekarz, Pracownik administracyjny  
**Cel:** Aktor przegląda dostępne terminy wizyt u wybranego lekarza.  
**Warunki wstępne:** Aktor jest zalogowany w systemie.  
**Warunki końcowe:** Aktor uzyskuje podgląd wolnych terminów i może wybrać odpowiedni termin do rezerwacji.  
**Scenariusz:**
1. Aktor inicjuje wykonanie PU4 bezpośrednio lub za pośrednictwem PU5.
2. System wyświetla formularz filtrowania: specjalizacja, nazwisko lekarza, zakres dat.
3. Aktor określa kryteria wyszukiwania.
4. System pobiera i wyświetla dostępne terminy spełniające podane kryteria.
5. Aktor przegląda listę wolnych terminów.
6. Zamiast kroku 4 system wyświetla komunikat o braku dostępnych terminów spełniających kryteria, jeśli żaden termin nie pasuje; Aktor może zmienić kryteria i wrócić do kroku 3.

#### PU5 - Rezerwacja wizyty
**Aktorzy:** Pacjent, Pracownik administracyjny  
**Cel:** Aktor rezerwuje termin wizyty u wybranego lekarza.  
**Warunki wstępne:** Aktor jest zalogowany w systemie. Lekarz posiada wolne terminy w kalendarzu dostępności.  
**Warunki końcowe:** Wizyta zostaje zarezerwowana i zapisana w systemie; termin jest niedostępny dla innych rezerwacji. Wykonano PU4 w celu wyświetlenia dostępnych terminów.  
**Scenariusz:**
1. Aktor inicjuje wykonanie PU5, wybierając opcję „Zarezerwuj wizytę".
2. Wykonanie PU4 w celu wyświetlenia dostępnych terminów u lekarzy.
3. Aktor wybiera konkretny termin.
4. System wyświetla formularz rezerwacji z danymi: lekarz, data, godzina, typ wizyty.
5. Aktor potwierdza dane i zatwierdza rezerwację.
6. System zapisuje rezerwację, blokuje wybrany termin i wysyła potwierdzenie Pacjentowi.
7. Zamiast kroku 6 system wyświetla komunikat o błędzie, jeśli wybrany termin został w międzyczasie zajęty przez innego Pacjenta; Aktor wraca do kroku 2.

#### PU6 - Przeglądanie umówionych wizyt
**Aktorzy:** Pacjent, Lekarz, Pracownik administracyjny  
**Cel:** Aktor przegląda listę zaplanowanych wizyt powiązanych z jego kontem lub wybranym pacjentem/lekarzem.  
**Warunki wstępne:** Aktor jest zalogowany w systemie. W systemie istnieje co najmniej jedna umówiona wizyta powiązana z Aktorem.  
**Warunki końcowe:** Aktor uzyskuje wgląd w szczegóły zaplanowanych wizyt. Możliwe jest zainicjowanie PU7 (Anulowanie wizyty) jako rozszerzenia.  
**Scenariusz:**
1. Aktor inicjuje wykonanie PU6, przechodząc do sekcji „Moje wizyty" lub „Lista wizyt".
2. System pobiera i wyświetla listę umówionych wizyt wraz z filtrowaniem (data, lekarz, status).
3. Aktor przeglądana szczegóły wybranej wizyty (data, godzina, lekarz, lokalizacja).
4. Wykonanie PU7, jeśli Aktor chce anulować wybraną wizytę.
5. Zamiast kroku 3 system wyświetla komunikat o braku zaplanowanych wizyt, jeśli lista jest pusta.

#### PU7 - Anulowanie wizyty
**Aktorzy:** Pacjent, Pracownik administracyjny  
**Cel:** Aktor anuluje wcześniej zarezerwowaną wizytę, zwalniając termin w kalendarzu lekarza.  
**Warunki wstępne:** Aktor jest zalogowany. W systemie istnieje rezerwacja powiązana z Aktorem.  
**Warunki końcowe:** Rezerwacja zostaje usunięta z systemu; termin wraca do puli dostępnych terminów. Lekarz zostaje powiadomiony o anulowaniu.  
**Scenariusz:**
1. Aktor inicjuje wykonanie PU7 w trakcie przeglądania listy wizyt (PU6), wybierając opcję „Anuluj wizytę".
2. System wyświetla szczegóły wizyty przeznaczonej do anulowania i prosi o potwierdzenie.
3. Aktor potwierdza chęć anulowania.
4. System usuwa rezerwację, zwalnia termin w kalendarzu lekarza i wysyła powiadomienie do Lekarza.
5. System wyświetla Aktorowi komunikat o pomyślnym anulowaniu wizyty.
6. Zamiast kroku 3–5 Aktor może zrezygnować z anulowania; system wraca do listy wizyt (PU6) bez wprowadzania zmian.

#### PU8 - Przeprowadzenie wizyty
**Aktorzy:** Lekarz  
**Cel:** Lekarz rejestruje przebieg wizyty pacjenta oraz uzupełnia dokumentację medyczną.  
**Warunki wstępne:** Lekarz jest zalogowany w systemie. Wizyta jest zaplanowana i jej termin nastąpił.  
**Warunki końcowe:** Wizyta zostaje oznaczona jako przeprowadzona. Wykonano PU9 (Aktualizacja rekordów medycznych) jako rozszerzenie. Dokumentacja medyczna pacjenta jest zaktualizowana.  
**Scenariusz:**
1. Lekarz inicjuje wykonanie PU8, otwierając listę wizyt na dany dzień i wybierając właściwą pozycję.
2. System wyświetla kartę pacjenta wraz z historią poprzednich wizyt.
3. Lekarz zapoznaje się z historią choroby i przeprowadza badanie pacjenta.
4. Lekarz wprowadza wyniki badania, rozpoznanie (ICD-10) oraz zalecenia.
5. Wykonanie PU9 w celu aktualizacji rekordów medycznych pacjenta na podstawie wprowadzonych danych.
6. System oznacza wizytę jako przeprowadzoną i zapisuje pełną dokumentację.
7. Zamiast kroku 6 system wyświetla komunikat o błędzie walidacji, jeśli wymagane pola nie zostały wypełnione; Lekarz uzupełnia brakujące dane i ponawia próbę zapisu.

### Kontekst: Pacjent

#### PU9 - Rejestracja
**Aktorzy:** Gość, Pracownik administracyjny  
**Cel:** Aktor zakłada nowe konto Pacjenta w systemie Medflow.  
**Warunki wstępne:** Osoba nie posiada aktywnego konta w systemie.  
**Warunki końcowe:** Konto Pacjenta zostaje utworzone i aktywowane; Pacjent może się zalogować i korzystać z funkcji systemu.  
**Scenariusz:**
1. Aktor inicjuje wykonanie PU9, wybierając opcję „Zarejestruj się" na stronie głównej.
2. System wyświetla formularz rejestracyjny.
3. Aktor wypełnia dane osobowe: imię, nazwisko, numer PESEL, adres e-mail, numer telefonu, hasło.
4. System waliduje poprawność danych oraz sprawdza unikalność adresu e-mail i numeru PESEL.
5. Aktor akceptuje regulamin i politykę prywatności, a następnie zatwierdza formularz.
6. System tworzy konto Pacjenta, generuje profil i wysyła e-mail z linkiem aktywacyjnym.
7. Pacjent aktywuje konto, klikając link w wiadomości e-mail.
8. Zamiast kroków 6–7 system wyświetla komunikat o błędzie duplikatu, jeśli walidacja w kroku 4 wykaże, że e-mail lub PESEL już istnieje w bazie; Aktor jest kierowany do strony logowania lub odzyskiwania hasła.

#### PU10 - Wyszukanie pacjenta
**Aktorzy:** Pracownik administracyjny, Lekarz  
**Cel:** Aktor odnajduje w systemie rekord konkretnego pacjenta.  
**Warunki wstępne:** Aktor jest zalogowany w systemie.  
**Warunki końcowe:** System zwraca listę rekordów pasujących do podanych kryteriów wyszukiwania. Wykonano PU10 i Aktor uzyskał dostęp do danych pacjenta lub wyświetlono brak wyników.  
**Scenariusz:**
1. Aktor inicjuje wykonanie PU10, przechodząc do modułu wyszukiwania pacjentów lub za pośrednictwem PU11.
2. System wyświetla formularz wyszukiwania z polami: imię, nazwisko, PESEL, numer karty pacjenta.
3. Aktor wprowadza co najmniej jedno kryterium wyszukiwania i zatwierdza formularz.
4. System przeszukuje bazę danych i zwraca listę pasujących rekordów.
5. Aktor wybiera właściwy rekord pacjenta z listy wyników.
6. Zamiast kroku 5 system wyświetla komunikat „brak wyników", jeśli żaden rekord nie spełnia podanych kryteriów; Aktor może zmodyfikować kryteria i powtórzyć wyszukiwanie.

#### PU11 - Przeglądanie profilu pacjenta
**Aktorzy:** Pacjent, Lekarz, Pracownik administracyjny  
**Cel:** Aktor uzyskuje wgląd w dane profilowe pacjenta. Możliwe jest zainicjowanie PU12 (Aktualizacja profilu pacjenta) jako rozszerzenia.  
**Warunki wstępne:** Aktor jest zalogowany w systemie. Pacjent (jeśli nie jest aktorem) musi istnieć w bazie; w takim przypadku wykonano PU10 (Wyszukanie pacjenta) jako «include».  
**Warunki końcowe:** Aktor zapoznał się z profilem pacjenta. Opcjonalnie zainicjowano PU12 w celu aktualizacji danych.  
**Scenariusz:**
1. Aktor inicjuje wykonanie PU11, przechodząc do sekcji „Mój profil" (Pacjent) lub wybierając pacjenta z wyników PU10 (Lekarz / Pracownik administracyjny).
2. Wykonanie PU10 w celu odnalezienia pacjenta, jeśli Aktorem nie jest sam Pacjent.
3. System wyświetla profil pacjenta: dane osobowe, historia wizyt, przypisani lekarze.
4. Aktor przegląda informacje zawarte w profilu.
5. Wykonanie PU12, jeśli Aktor chce zaktualizować dane profilu.
6. Zamiast kroku 3 system wyświetla komunikat o braku uprawnień, jeśli Aktor nie ma prawa do wglądu w dany profil.

#### PU12 - Aktualizacja profilu pacjenta
**Aktorzy:** Pacjent, Pracownik administracyjny  
**Cel:** Aktor aktualizuje dane zawarte w profilu pacjenta. PU12 jest rozszerzeniem («extend») PU11. Możliwe jest wykonanie PU13 (Aktualizacja rekordów medycznych) lub PU14 (Aktualizacja danych osobowych).  
**Warunki wstępne:** Aktor jest zalogowany. Profil pacjenta jest otwarty w ramach PU11.  
**Warunki końcowe:** Dane profilu pacjenta zostają zaktualizowane w systemie. Wykonano PU13 lub PU14 w zależności od zakresu zmian.  
**Scenariusz:**
1. Aktor inicjuje wykonanie PU12, wybierając opcję „Edytuj profil" w widoku PU11.
2. System wyświetla edytowalny formularz z aktualnymi danymi pacjenta.
3. Aktor wybiera zakres modyfikacji: dane medyczne lub dane osobowe.
4. Wykonanie PU13 w celu aktualizacji rekordów medycznych, jeśli Aktor modyfikuje dane zdrowotne.
5. Wykonanie PU14 w celu aktualizacji danych osobowych, jeśli Aktor modyfikuje dane kontaktowe lub adresowe.
6. System waliduje wprowadzone zmiany i zapisuje zaktualizowany profil.
7. Zamiast kroku 6 system wyświetla komunikat o błędzie walidacji, jeśli dane są niepoprawne; Aktor poprawia wskazane pola i ponawia zapis.

#### PU13 - Aktualizacja rekordów medycznych
**Aktorzy:** Lekarz, Pracownik administracyjny  
**Cel:** Aktor aktualizuje dokumentację medyczną pacjenta. PU13 jest wywoływane jako rozszerzenie («extend») PU8 (Przeprowadzenie wizyty) lub jako część PU12 (Aktualizacja profilu pacjenta).  
**Warunki wstępne:** Aktor jest zalogowany. Wizyta jest w toku (PU8) lub profil pacjenta jest otwarty (PU12).  
**Warunki końcowe:** Dokumentacja medyczna pacjenta zostaje zaktualizowana i zapisana w systemie.  
**Scenariusz:**
1. Aktor inicjuje wykonanie PU13 w trakcie PU8 lub PU12.
2. System wyświetla aktualną dokumentację medyczną pacjenta (rozpoznania, leki, wyniki badań, alergies).
3. Aktor wprowadza lub modyfikuje: rozpoznania (ICD-10), przyjmowane leki, wyniki badań, notatki kliniczne.
4. System waliduje poprawność kodów i danych medycznych.
5. System zapisuje zaktualizowane rekordy medyczne z datą i identyfikatorem Aktora.
6. Zamiast kroku 5 system wyświetla komunikat o błędzie, jeśli wprowadzono nieprawidłowe kody lub brakuje wymaganych pól; Aktor uzupełnia dane i ponawia próbę.

#### PU14 - Aktualizacja danych osobowych
**Aktorzy:** Pacjent, Pracownik administracyjny  
**Cel:** Aktor aktualizuje dane kontaktowe i adresowe pacjenta w systemie. PU14 jest częścią PU12 (Aktualizacja profilu pacjenta).  
**Warunki wstępne:** Aktor jest zalogowany. Edytowalny formularz profilu jest otwarty w ramach PU12.  
**Warunki końcowe:** Dane osobowe pacjenta (adres, telefon, e-mail) zostają zaktualizowane w systemie.  
**Scenariusz:**
1. Aktor inicjuje wykonanie PU14 w ramach PU12, przechodząc do sekcji „Dane osobowe".
2. System wyświetla formularz z aktualnymi danymi kontaktowymi i adresowymi pacjenta.
3. Aktor modyfikuje wybrane pola: adres zamieszkania, numer telefonu, adres e-mail.
4. System waliduje format i poprawność wprowadzonych danych (np. format telefonu, unikalność e-mail).
5. System zapisuje zaktualizowane dane i potwierdza zapis komunikatem o powodzeniu.
6. Zamiast kroku 5 system wyświetla komunikat o błędzie, jeśli e-mail jest już zajęty przez inne konto lub dane mają nieprawidłowy format; Aktor koryguje dane i ponawia zapis.

### Kontekst: Personel

#### PU15 - Przeglądanie i wyszukiwanie personelu
**Aktorzy:** Administrator systemu, Pracownik administracyjny  
**Cel:** Aktor przegląda listę pracowników systemu i wyszukuje konkretnych członków personelu. Możliwe jest zainicjowanie PU16 (Zarządzanie personelem) jako rozszerzenia («extend»).  
**Warunki wstępne:** Aktor jest zalogowany w systemie.  
**Warunki końcowe:** Aktor uzyskuje wgląd w listę i profile pracowników. Wykonano PU15 i Aktor otrzymał żądane informacje lub zainicjowano PU16.  
**Scenariusz:**
1. Aktor inicjuje wykonanie PU15, przechodząc do modułu „Personel".
2. System wyświetla listę wszystkich pracowników z możliwością filtrowania według roli, specjalizacji lub statusu.
3. Aktor wprowadza kryteria wyszukiwania (imię, nazwisko, specjalizacja, rola).
4. System wyświetla przefiltrowaną listę pracowników spełniających kryteria.
5. Aktor wybiera pracownika i przegląda jego szczegółowy profil.
6. Wykonanie PU16, jeśli Aktor chce zarządzać danymi wybranego pracownika.
7. Zamiast kroku 4 system wyświetla komunikat „brak wyników", jeśli żaden pracownik nie spełnia podanych kryteriów; Aktor może zmodyfikować kryteria i powtórzyć wyszukiwanie.

#### PU16 - Zarządzanie personelem
**Aktorzy:** Administrator systemu  
**Cel:** Administrator zarządza kontami i danymi pracowników systemu. PU16 jest rozszerzeniem («extend») PU15. Możliwe jest wykonanie PU17 (Zarządzanie lekarzami) lub PU18 (Zarządzanie pracownikami administracyjnymi).  
**Warunki wstępne:** Aktor jest zalogowany jako Administrator systemu.  
**Warunki końcowe:** Dane personelu zostają zaktualizowane w systemie. Wykonano PU17 lub PU18 w zależności od roli modyfikowanego pracownika.  
**Scenariusz:**
1. Administrator inicjuje wykonanie PU16 w ramach PU15, wybierając akcję „Zarządzaj" przy wybranym pracowniku lub „Dodaj pracownika".
2. System wyświetla formularz zarządzania pracownikiem z możliwością wyboru akcji: dodaj, edytuj, dezaktywuj.
3. Administrator wybiera typ pracownika: Lekarz lub Pracownik administracyjny.
4. Wykonanie PU17 w celu zarządzania danymi Lekarza, jeśli wybranym typem jest Lekarz.
5. Wykonanie PU18 w celu zarządzania danymi Pracownika administracyjnego, jeśli wybranym typem jest Pracownik administracyjny.
6. System zapisuje wprowadzone zmiany i aktualizuje uprawnienia konta w systemie.
7. Zamiast kroku 6 system wyświetla komunikat o błędzie walidacji, jeśli brakuje wymaganych pól lub dane są niepoprawne; Administrator uzupełnia dane i ponawia zapis.

#### PU17 - Zarządzanie lekarzami
**Aktorzy:** Administrator systemu  
**Cel:** Administrator dodaje, edytuje lub dezaktywuje konta lekarzy oraz zarządza ich danymi zawodowymi.  
**Warunki wstępne:** Aktor jest zalogowany jako Administrator systemu. Wykonanie PU17 następuje jako część PU16 (Zarządzanie personelem).  
**Warunki końcowe:** Konto i dane lekarza zostają utworzone, zaktualizowane lub zdezaktywowane. Lekarz uzyskuje lub traci dostęp do systemu zgodnie z wprowadzonymi zmianami.  
**Scenariusz:**
1. Administrator inicjuje wykonanie PU17 w ramach PU16, wybierając typ pracownika „Lekarz".
2. System wyświetla formularz danych lekarza: imię, nazwisko, specjalizacja, numer PWZ, harmonogram pracy.
3. Administrator wypełnia lub modyfikuje wymagane pola.
4. System waliduje numer PWZ i unikalność danych w bazie.
5. System zapisuje dane, tworzy lub aktualizuje konto lekarza i nadaje odpowiednie uprawnienia.
6. Zamiast kroku 5 Administrator może dezaktywować konto lekarza; system blokuje dostęp bez usuwania historycznych danych.
7. Zamiast kroku 5 system wyświetla komunikat o błędzie, jeśli numer PWZ jest nieprawidłowy lub już istnieje w bazie; Administrator koryguje dane i ponawia zapis.

#### PU18 - Zarządzanie pracownikami administracyjnymi
**Aktorzy:** Administrator systemu  
**Cel:** Administrator dodaje, edytuje lub dezaktywuje konta pracowników administracyjnych oraz zarządza ich uprawnieniami.  
**Warunki wstępne:** Aktor jest zalogowany jako Administrator systemu. Wykonanie PU18 następuje jako część PU16 (Zarządzanie personelem).  
**Warunki końcowe:** Konto i dane pracownika administracyjnego zostają utworzone, zaktualizowane lub zdezaktywowane. Pracownik uzyskuje lub traci dostęp do systemu zgodnie z przypisaną rolą.  
**Scenariusz:**
1. Administrator inicjuje wykonanie PU18 w ramach PU16, wybierając typ pracownika „Pracownik administracyjny".
2. System wyświetla formularz danych pracownika: imię, nazwisko, stanowisko, zakres uprawnień, dane kontaktowe.
3. Administrator wypełnia lub modyfikuje wymagane pola i przypisuje zakres uprawnień.
4. System waliduje poprawność i unikalność danych (e-mail).
5. System zapisuje dane, tworzy lub aktualizuje konto pracownika i nadaje odpowiednie uprawnienia dostępu do modułów systemu.
6. Zamiast kroku 5 Administrator może dezaktywować konto pracownika; system blokuje dostęp bez usuwania historii operacji.
7. Zamiast kroku 5 system wyświetla komunikat o błędzie, jeśli dane są niepoprawne lub e-mail już istnieje; Administrator koryguje dane i ponawia zapis.
