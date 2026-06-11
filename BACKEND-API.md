# Medflow — Backend API (handoff dla backendu)

Krótki opis kontraktu API, którego oczekuje **obecny frontend** (prototype na mockach).  
Frontend trzyma token w `localStorage` i wysyła go jako `Authorization: Bearer <token>` na chronionych endpointach.

---

## Role użytkowników

| Rola w API (`role`) | Skrót | Opis |
|---------------------|-------|------|
| `patient` | P | Pacjent |
| `doctor` | L | Lekarz |
| `admin_staff` | PA | Pracownik administracyjny |
| `admin` | ADMIN | Administrator systemu |

`role` musi wracać w odpowiedzi logowania — frontend na jej podstawie buduje nawigację i guardy tras.

---

## Uwagi ogólne

- **Daty/czasy:** ISO 8601 stringi (np. `2026-06-15T10:30:00.000Z`).
- **Statusy wizyt:** `Zarezerwowana` \| `Zakończona` \| `Anulowana`
- **Statusy slotów:** `Wolny` \| `Zajęty`
- **Błędy:** sensowny komunikat w body (frontend wyświetla `error.message` w toastach).
- **Paginacja:** na razie nie jest używana — frontend oczekuje pełnych list.

---

## 1. Auth

### `POST /auth/login`
**Dostęp:** publiczny

**Body:**
```json
{ "email": "string", "password": "string" }
```

**Response:**
```json
{
  "accessToken": "string",
  "userId": "string",
  "email": "string",
  "firstName": "string",
  "lastName": "string",
  "role": "patient | doctor | admin_staff | admin"
}
```

---

## 2. Rejestracja pacjenta

### `POST /patients/register`
**Dostęp:** publiczny

**Body:**
```json
{
  "firstName": "string",
  "lastName": "string",
  "email": "string",
  "password": "string",
  "pesel": "string",
  "phone": "string"
}
```

**Response:**
```json
{ "patientId": "string" }
```

---

## 3. Pacjenci

### `GET /patients?name=&pesel=`
**Dostęp:** L, PA  
Wyszukiwanie po imieniu/nazwisku i/lub PESEL (substring, case-insensitive).

**Response:** `Patient[]`

```json
{
  "id": "string",
  "personalData": {
    "firstName": "string",
    "lastName": "string",
    "email": "string",
    "phone": "string",
    "pesel": "string",
    "address": "string"
  },
  "medicalData": {
    "bloodType": "string",
    "allergies": "string",
    "chronicDiseases": "string",
    "medications": "string"
  }
}
```

### `GET /patients/:id`
**Dostęp:** P (własny profil), L, PA  
**Response:** `Patient` (jak wyżej)

### `PUT /patients/:id/personal`
**Dostęp:** P (własny), PA

**Body:**
```json
{
  "firstName": "string",
  "lastName": "string",
  "phone": "string",
  "email": "string",
  "address": "string"
}
```

**Response:** zaktualizowany `Patient`

### `PUT /patients/:id/medical`
**Dostęp:** L, PA

**Body:**
```json
{
  "bloodType": "string",
  "allergies": "string",
  "chronicDiseases": "string",
  "medications": "string"
}
```

**Response:** zaktualizowany `Patient`

---

## 4. Lekarze (do rezerwacji wizyt)

### `GET /doctors?specialization=&name=`
**Dostęp:** P, PA (i ogólnie zalogowani przy rezerwacji)  
Zwraca **tylko aktywnych** lekarzy.

**Response:** lista obiektów w stylu `StaffMember` z `role: "doctor"`:

```json
{
  "id": "string",
  "role": "doctor",
  "firstName": "string",
  "lastName": "string",
  "email": "string",
  "active": true,
  "specialization": "string",
  "pwz": "string",
  "department": "string"
}
```

---

## 5. Harmonogram (sloty)

### `GET /schedule?doctorId=&from=&to=`
**Dostęp:** L (własny), PA  
Sloty lekarza w zakresie dat (`from` / `to` — ISO).

**Response:** `Slot[]`

```json
{
  "id": "string",
  "doctorId": "string",
  "startTime": "ISO",
  "endTime": "ISO",
  "status": "Wolny | Zajęty",
  "room": "string"
}
```

### `GET /schedule/slots?doctorId=&date=`
**Dostęp:** P, PA (rezerwacja wizyty)  
Wolne sloty (`status = Wolny`) w wybranym dniu.

**Response:** `Slot[]`

### `POST /schedule/slots`
**Dostęp:** L, PA

**Body:**
```json
{
  "doctorId": "string",
  "slots": [
    { "startTime": "ISO", "endTime": "ISO", "room": "string" }
  ]
}
```

**Response:** utworzone `Slot[]`

### `PUT /schedule/slots/:slotId`
**Dostęp:** L, PA  
Tylko gdy slot ma status `Wolny`.

**Body:**
```json
{ "startTime": "ISO", "endTime": "ISO", "room": "string" }
```

**Response:** `Slot`

### `DELETE /schedule/slots/:slotId`
**Dostęp:** L, PA  
Tylko gdy slot ma status `Wolny`.

**Response:** `{ "id": "string" }`

---

## 6. Wizyty

### `GET /appointments?userId=&filter=upcoming|past`
**Dostęp:** P, L, PA

Filtrowanie po roli użytkownika z tokena:
- **P** → wizyty gdzie `patientId = userId`
- **L** → wizyty gdzie `doctorId = userId`
- **PA** → wszystkie wizyty (rejestracja)

`upcoming` = przyszłe + status `Zarezerwowana`  
`past` = przeszłe **lub** status ≠ `Zarezerwowana`

**Response:** `Appointment[]`

```json
{
  "id": "string",
  "date": "ISO",
  "doctorId": "string",
  "doctorName": "string",
  "patientId": "string",
  "patientName": "string",
  "status": "Zarezerwowana | Zakończona | Anulowana",
  "type": "string",
  "room": "string"
}
```

> Frontend używa też listy wizyt po pacjencie na profilu — można to obsłużyć przez `GET /appointments?patientId=:id` albo osobny endpoint; obecnie mock filtruje po `patientId` lokalnie.

### `GET /appointments/:id`
**Dostęp:** P, L, PA  
**Response:** `Appointment`

### `POST /appointments`
**Dostęp:** P, PA

**Body:**
```json
{
  "doctorId": "string",
  "slotIds": ["string"],
  "patientId": "string",
  "type": "string"
}
```

**Response:**
```json
{ "appointmentId": "string" }
```

**Efekty uboczne (backend):**
- oznacz slot(y) jako `Zajęty`
- utwórz wizytę ze statusem `Zarezerwowana`

### `DELETE /appointments/:id`
**Dostęp:** P, PA (gdy status = `Zarezerwowana`)

Anulowanie wizyty.

**Efekty uboczne (backend):**
- status wizyty → `Anulowana`
- zwolnij powiązany slot w harmonogramie (front **nie** woła osobno endpointu slotu)

---

## 7. Rekordy medyczne

### `GET /medical-records?appointmentId=:id`
**Dostęp:** P, L, PA  
**Response:** `MedicalRecord | null`

```json
{
  "id": "string",
  "appointmentId": "string",
  "patientId": "string",
  "diagnoses": "string",
  "symptoms": "string",
  "prescriptions": "string",
  "notes": "string",
  "createdAt": "ISO"
}
```

### `POST /medical-records`
**Dostęp:** L

**Body:**
```json
{
  "appointmentId": "string",
  "diagnoses": "string",
  "symptoms": "string",
  "prescriptions": "string",
  "notes": "string"
}
```

**Response:** `MedicalRecord`

**Efekty uboczne (backend):**
- utworzenie rekordu **automatycznie zamyka wizytę** (status → `Zakończona`)

---

## 8. Personel

### `GET /staff?role=doctor|admin_staff&name=&specialization=`
**Dostęp:** PA, ADMIN

**Response:** `StaffMember[]`

```json
{
  "id": "string",
  "role": "doctor | admin_staff",
  "firstName": "string",
  "lastName": "string",
  "email": "string",
  "active": true,
  "specialization": "string?",
  "pwz": "string?",
  "department": "string?",
  "position": "string?"
}
```

### `GET /staff/:id`
**Dostęp:** PA, ADMIN  
**Response:** `StaffMember`

### `POST /staff`
**Dostęp:** ADMIN

**Body (lekarz):**
```json
{
  "role": "doctor",
  "firstName": "string",
  "lastName": "string",
  "email": "string",
  "specialization": "string",
  "pwz": "string",
  "department": "string"
}
```

**Body (pracownik admin):**
```json
{
  "role": "admin_staff",
  "firstName": "string",
  "lastName": "string",
  "email": "string",
  "position": "string"
}
```

**Response:**
```json
{ "staffId": "string" }
```

### `PUT /staff/:id`
**Dostęp:** ADMIN  
**Body:** pola do aktualizacji (jak przy tworzeniu, częściowe OK)  
**Response:** `StaffMember`

### `DELETE /staff/:id`
**Dostęp:** ADMIN  
Dezaktywacja konta (`active: false`), nie twarde usuwanie.

**Response:** `StaffMember`

---

## Podsumowanie endpointów (checklista)

| Metoda | Endpoint | Kto |
|--------|----------|-----|
| POST | `/auth/login` | publiczny |
| POST | `/patients/register` | publiczny |
| GET | `/patients` | L, PA |
| GET | `/patients/:id` | P, L, PA |
| PUT | `/patients/:id/personal` | P, PA |
| PUT | `/patients/:id/medical` | L, PA |
| GET | `/doctors` | zalogowani |
| GET | `/schedule` | L, PA |
| GET | `/schedule/slots` | P, PA |
| POST | `/schedule/slots` | L, PA |
| PUT | `/schedule/slots/:slotId` | L, PA |
| DELETE | `/schedule/slots/:slotId` | L, PA |
| GET | `/appointments` | P, L, PA |
| GET | `/appointments/:id` | P, L, PA |
| POST | `/appointments` | P, PA |
| DELETE | `/appointments/:id` | P, PA |
| GET | `/medical-records` | P, L, PA |
| POST | `/medical-records` | L |
| GET | `/staff` | PA, ADMIN |
| GET | `/staff/:id` | PA, ADMIN |
| POST | `/staff` | ADMIN |
| PUT | `/staff/:id` | ADMIN |
| DELETE | `/staff/:id` | ADMIN |

**Razem: 22 endpointy** (bez osobnego `/auth/me` — frontend na razie go nie używa).

---

## Reguły biznesowe (ważne dla backendu)

1. **Anulowanie wizyty** → zwolnij slot; front nie woła `DELETE /schedule/slots`.
2. **Utworzenie rekordu medycznego** → zamknij wizytę (`Zakończona`).
3. **Slot `Zajęty`** → brak edycji i usuwania (walidacja po stronie API; front ukrywa akcje).
4. **`GET /appointments`** — filtrowanie `userId` zależy od roli (patrz sekcja 6).
5. **Chronione endpointy** — wymagają `Authorization: Bearer <token>`.

---

## Integracja z frontendem (później)

Mocki są w `src/shared/api/mock-db.ts`, typy w `src/shared/types/`.  
Po gotowości API wystarczy podmienić funkcje w `features/*/api/` na prawdziwe `fetch` + te same kształty JSON.
