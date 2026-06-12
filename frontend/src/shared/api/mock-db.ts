import type { AuthUser } from "../types/auth-user";
import type { Role } from "../types/role";
import type { Appointment } from "../types/appointment";
import type { MedicalRecord } from "../types/medical-record";
import type { Patient } from "../types/patient";
import type { Slot } from "../types/slot";
import type { StaffMember } from "../types/staff-member";

const LATENCY = 250;

export function delay<T>(value: T, ms = LATENCY): Promise<T> {
  return new Promise((resolve) => setTimeout(() => resolve(clone(value)), ms));
}

function clone<T>(value: T): T {
  return structuredClone(value);
}

let idCounter = 1000;
function nextId(prefix: string): string {
  idCounter += 1;
  return `${prefix}${idCounter}`;
}

function isoAt(daysFromNow: number, hour: number, minute = 0): string {
  const d = new Date();
  d.setHours(0, 0, 0, 0);
  d.setDate(d.getDate() + daysFromNow);
  d.setHours(hour, minute, 0, 0);
  return d.toISOString();
}

interface Credential {
  email: string;
  password: string;
  user: AuthUser;
}

// ---- Seed data -------------------------------------------------------------

const patients: Patient[] = [
  {
    id: "p1",
    personalData: {
      firstName: "Jan",
      lastName: "Kowalski",
      email: "pacjent@medflow.pl",
      phone: "600 100 200",
      pesel: "90010112345",
      address: "ul. Lipowa 4/2, Warszawa",
    },
    medicalData: {
      bloodType: "A Rh+",
      allergies: "Penicylina",
      chronicDiseases: "Nadciśnienie tętnicze",
      medications: "Ramipril 5mg",
    },
  },
  {
    id: "p2",
    personalData: {
      firstName: "Maria",
      lastName: "Nowak",
      email: "maria.nowak@example.com",
      phone: "601 222 333",
      pesel: "85052254321",
      address: "ul. Kwiatowa 18, Kraków",
    },
    medicalData: {
      bloodType: "0 Rh-",
      allergies: "Brak",
      chronicDiseases: "Astma",
      medications: "Salbutamol wziewnie",
    },
  },
  {
    id: "p3",
    personalData: {
      firstName: "Tomasz",
      lastName: "Wiśniewski",
      email: "tomasz.w@example.com",
      phone: "602 444 555",
      pesel: "78112098765",
      address: "ul. Słoneczna 9, Gdańsk",
    },
    medicalData: {
      bloodType: "B Rh+",
      allergies: "Pyłki traw",
      chronicDiseases: "Brak",
      medications: "Brak",
    },
  },
];

const staff: StaffMember[] = [
  {
    id: "d1",
    role: "doctor",
    firstName: "Anna",
    lastName: "Kowalska",
    email: "lekarz@medflow.pl",
    active: true,
    specialization: "Internista",
    pwz: "1234567",
    department: "Interna",
  },
  {
    id: "d2",
    role: "doctor",
    firstName: "Piotr",
    lastName: "Nowak",
    email: "piotr.nowak@medflow.pl",
    active: true,
    specialization: "Kardiolog",
    pwz: "2345678",
    department: "Kardiologia",
  },
  {
    id: "d3",
    role: "doctor",
    firstName: "Ewa",
    lastName: "Lewandowska",
    email: "ewa.lewandowska@medflow.pl",
    active: true,
    specialization: "Dermatolog",
    pwz: "3456789",
    department: "Dermatologia",
  },
  {
    id: "s1",
    role: "admin_staff",
    firstName: "Katarzyna",
    lastName: "Zielińska",
    email: "rejestracja@medflow.pl",
    active: true,
    position: "Rejestratorka medyczna",
  },
  {
    id: "s2",
    role: "admin_staff",
    firstName: "Marek",
    lastName: "Wójcik",
    email: "marek.wojcik@medflow.pl",
    active: false,
    position: "Koordynator recepcji",
  },
];

const slots: Slot[] = [];
function seedSlots() {
  const doctorIds = ["d1", "d2", "d3"];
  const rooms: Record<string, string> = { d1: "Gabinet 12", d2: "Gabinet 4", d3: "Gabinet 7" };
  for (const doctorId of doctorIds) {
    for (let day = 1; day <= 10; day++) {
      for (const hour of [9, 10, 11, 13, 14]) {
        slots.push({
          id: nextId("slot"),
          doctorId,
          startTime: isoAt(day, hour),
          endTime: isoAt(day, hour, 30),
          status: "Wolny",
          room: rooms[doctorId],
        });
      }
    }
  }
}
seedSlots();

const appointments: Appointment[] = [
  {
    id: "a1",
    date: isoAt(3, 10, 30),
    doctorId: "d1",
    doctorName: "dr Anna Kowalska",
    patientId: "p1",
    patientName: "Jan Kowalski",
    status: "Zarezerwowana",
    type: "Konsultacja",
    room: "Gabinet 12",
  },
  {
    id: "a2",
    date: isoAt(7, 14, 0),
    doctorId: "d2",
    doctorName: "dr Piotr Nowak",
    patientId: "p1",
    patientName: "Jan Kowalski",
    status: "Zarezerwowana",
    type: "Kontrola",
    room: "Gabinet 4",
  },
  {
    id: "a3",
    date: isoAt(-14, 9, 0),
    doctorId: "d1",
    doctorName: "dr Anna Kowalska",
    patientId: "p1",
    patientName: "Jan Kowalski",
    status: "Zakończona",
    type: "Konsultacja",
    room: "Gabinet 12",
  },
  {
    id: "a4",
    date: isoAt(-30, 11, 0),
    doctorId: "d3",
    doctorName: "dr Ewa Lewandowska",
    patientId: "p2",
    patientName: "Maria Nowak",
    status: "Zakończona",
    type: "Badanie",
    room: "Gabinet 7",
  },
];

const medicalRecords: MedicalRecord[] = [
  {
    id: "mr1",
    appointmentId: "a3",
    patientId: "p1",
    diagnoses: "Nadciśnienie tętnicze samoistne (I10)",
    symptoms: "Bóle głowy, podwyższone ciśnienie tętnicze",
    prescriptions: "Ramipril 5mg 1x dziennie",
    notes: "Kontrola za 3 miesiące, zalecana dieta niskosodowa.",
    createdAt: isoAt(-14, 9, 30),
  },
];

const credentials: Credential[] = [
  {
    email: "pacjent@medflow.pl",
    password: "haslo123",
    user: {
      userId: "p1",
      email: "pacjent@medflow.pl",
      firstName: "Jan",
      lastName: "Kowalski",
      role: "patient",
      accessToken: "",
    },
  },
  {
    email: "lekarz@medflow.pl",
    password: "haslo123",
    user: {
      userId: "d1",
      email: "lekarz@medflow.pl",
      firstName: "Anna",
      lastName: "Kowalska",
      role: "doctor",
      accessToken: "",
    },
  },
  {
    email: "rejestracja@medflow.pl",
    password: "haslo123",
    user: {
      userId: "s1",
      email: "rejestracja@medflow.pl",
      firstName: "Katarzyna",
      lastName: "Zielińska",
      role: "admin_staff",
      accessToken: "",
    },
  },
  {
    email: "admin@medflow.pl",
    password: "haslo123",
    user: {
      userId: "admin1",
      email: "admin@medflow.pl",
      firstName: "Administrator",
      lastName: "Systemu",
      role: "admin",
      accessToken: "",
    },
  },
];

// ---- Helpers ---------------------------------------------------------------

function makeToken(userId: string): string {
  return `mock-token-${userId}-${Date.now()}`;
}

// ---- Auth ------------------------------------------------------------------

export const DEMO_ACCOUNTS = credentials.map((c) => ({
  email: c.email,
  password: c.password,
  role: c.user.role,
}));

export async function dbLogin(email: string, password: string): Promise<AuthUser> {
  const match = credentials.find(
    (c) => c.email.toLowerCase() === email.toLowerCase() && c.password === password,
  );
  if (!match) {
    await new Promise((r) => setTimeout(r, LATENCY));
    throw new Error("Nieprawidłowy email lub hasło");
  }
  return delay({ ...match.user, accessToken: makeToken(match.user.userId) });
}

export interface RegisterPatientInput {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  pesel: string;
  phone: string;
}

export async function dbRegisterPatient(input: RegisterPatientInput): Promise<{ patientId: string }> {
  const id = nextId("p");
  patients.push({
    id,
    personalData: {
      firstName: input.firstName,
      lastName: input.lastName,
      email: input.email,
      phone: input.phone,
      pesel: input.pesel,
      address: "",
    },
    medicalData: { bloodType: "", allergies: "", chronicDiseases: "", medications: "" },
  });
  credentials.push({
    email: input.email,
    password: input.password,
    user: {
      userId: id,
      email: input.email,
      firstName: input.firstName,
      lastName: input.lastName,
      role: "patient",
      accessToken: "",
    },
  });
  return delay({ patientId: id });
}

export function getDemoUserForRole(role: Role): AuthUser {
  const cred = credentials.find((c) => c.user.role === role);
  if (!cred) throw new Error(`Brak konta demo dla roli ${role}`);
  return { ...cred.user, accessToken: makeToken(cred.user.userId) };
}

// ---- Patients --------------------------------------------------------------

export async function dbListPatients(filters: { name?: string; pesel?: string }): Promise<Patient[]> {
  const name = filters.name?.trim().toLowerCase() ?? "";
  const pesel = filters.pesel?.trim() ?? "";
  const result = patients.filter((p) => {
    const fullName = `${p.personalData.firstName} ${p.personalData.lastName}`.toLowerCase();
    const matchesName = name === "" || fullName.includes(name);
    const matchesPesel = pesel === "" || p.personalData.pesel.includes(pesel);
    return matchesName && matchesPesel;
  });
  return delay(result);
}

export async function dbGetPatient(id: string): Promise<Patient> {
  const patient = patients.find((p) => p.id === id);
  if (!patient) throw new Error("Nie znaleziono pacjenta");
  return delay(patient);
}

export async function dbUpdatePatientPersonal(
  id: string,
  data: Partial<Patient["personalData"]>,
): Promise<Patient> {
  const patient = patients.find((p) => p.id === id);
  if (!patient) throw new Error("Nie znaleziono pacjenta");
  patient.personalData = { ...patient.personalData, ...data };
  return delay(patient);
}

export async function dbUpdatePatientMedical(
  id: string,
  data: Partial<Patient["medicalData"]>,
): Promise<Patient> {
  const patient = patients.find((p) => p.id === id);
  if (!patient) throw new Error("Nie znaleziono pacjenta");
  patient.medicalData = { ...patient.medicalData, ...data };
  return delay(patient);
}

// ---- Schedule / Slots ------------------------------------------------------

export async function dbGetSchedule(doctorId: string, from?: string, to?: string): Promise<Slot[]> {
  const fromTime = from ? new Date(from).getTime() : -Infinity;
  const toTime = to ? new Date(to).getTime() : Infinity;
  const result = slots
    .filter((s) => s.doctorId === doctorId)
    .filter((s) => {
      const t = new Date(s.startTime).getTime();
      return t >= fromTime && t <= toTime;
    })
    .sort((a, b) => a.startTime.localeCompare(b.startTime));
  return delay(result);
}

export interface NewSlotInput {
  startTime: string;
  endTime: string;
  room: string;
}

export async function dbAddSlots(doctorId: string, newSlots: NewSlotInput[]): Promise<Slot[]> {
  const created: Slot[] = newSlots.map((s) => ({
    id: nextId("slot"),
    doctorId,
    startTime: s.startTime,
    endTime: s.endTime,
    status: "Wolny",
    room: s.room,
  }));
  slots.push(...created);
  return delay(created);
}

export async function dbUpdateSlot(slotId: string, data: NewSlotInput): Promise<Slot> {
  const slot = slots.find((s) => s.id === slotId);
  if (!slot) throw new Error("Nie znaleziono terminu");
  if (slot.status === "Zajęty") throw new Error("Nie można edytować zajętego terminu");
  slot.startTime = data.startTime;
  slot.endTime = data.endTime;
  slot.room = data.room;
  return delay(slot);
}

export async function dbDeleteSlot(slotId: string): Promise<{ id: string }> {
  const index = slots.findIndex((s) => s.id === slotId);
  if (index === -1) throw new Error("Nie znaleziono terminu");
  if (slots[index].status === "Zajęty") throw new Error("Nie można usunąć zajętego terminu");
  slots.splice(index, 1);
  return delay({ id: slotId });
}

// ---- Appointments ----------------------------------------------------------

export async function dbGetAppointment(id: string): Promise<Appointment> {
  const appointment = appointments.find((a) => a.id === id);
  if (!appointment) throw new Error("Nie znaleziono wizyty");
  return delay(appointment);
}

export async function dbListAppointmentsByPatient(patientId: string): Promise<Appointment[]> {
  const result = appointments
    .filter((a) => a.patientId === patientId)
    .sort((a, b) => b.date.localeCompare(a.date));
  return delay(result);
}

export async function dbCancelAppointment(id: string): Promise<Appointment> {
  const appointment = appointments.find((a) => a.id === id);
  if (!appointment) throw new Error("Nie znaleziono wizyty");
  appointment.status = "Anulowana";
  // Free the matching slot in the schedule.
  const slot = slots.find(
    (s) => s.doctorId === appointment.doctorId && s.startTime === appointment.date,
  );
  if (slot) slot.status = "Wolny";
  return delay(appointment);
}

// ---- Medical records -------------------------------------------------------

export async function dbGetMedicalRecordByAppointment(
  appointmentId: string,
): Promise<MedicalRecord | null> {
  const record = medicalRecords.find((r) => r.appointmentId === appointmentId) ?? null;
  return delay(record);
}

export interface CreateMedicalRecordInput {
  appointmentId: string;
  diagnoses: string;
  symptoms: string;
  prescriptions: string;
  notes: string;
}

export async function dbCreateMedicalRecord(
  input: CreateMedicalRecordInput,
): Promise<MedicalRecord> {
  const appointment = appointments.find((a) => a.id === input.appointmentId);
  if (!appointment) throw new Error("Nie znaleziono wizyty");
  const record: MedicalRecord = {
    id: nextId("mr"),
    appointmentId: input.appointmentId,
    patientId: appointment.patientId,
    diagnoses: input.diagnoses,
    symptoms: input.symptoms,
    prescriptions: input.prescriptions,
    notes: input.notes,
    createdAt: new Date().toISOString(),
  };
  medicalRecords.push(record);
  // Creating a record closes the appointment.
  appointment.status = "Zakończona";
  return delay(record);
}

// ---- Staff -----------------------------------------------------------------

export async function dbListStaff(filters: {
  role?: "doctor" | "admin_staff";
  name?: string;
  specialization?: string;
}): Promise<StaffMember[]> {
  const name = filters.name?.trim().toLowerCase() ?? "";
  const specialization = filters.specialization?.trim().toLowerCase() ?? "";
  const result = staff.filter((s) => {
    const fullName = `${s.firstName} ${s.lastName}`.toLowerCase();
    const matchesRole = !filters.role || s.role === filters.role;
    const matchesName = name === "" || fullName.includes(name);
    const matchesSpec =
      specialization === "" || (s.specialization ?? "").toLowerCase().includes(specialization);
    return matchesRole && matchesName && matchesSpec;
  });
  return delay(result);
}

export async function dbGetStaff(id: string): Promise<StaffMember> {
  const member = staff.find((s) => s.id === id);
  if (!member) throw new Error("Nie znaleziono pracownika");
  return delay(member);
}

export type CreateStaffInput = Omit<StaffMember, "id" | "active">;

export async function dbCreateStaff(input: CreateStaffInput): Promise<{ staffId: string }> {
  const id = nextId(input.role === "doctor" ? "d" : "s");
  staff.push({ ...input, id, active: true });
  return delay({ staffId: id });
}

export async function dbUpdateStaff(
  id: string,
  data: Partial<Omit<StaffMember, "id">>,
): Promise<StaffMember> {
  const member = staff.find((s) => s.id === id);
  if (!member) throw new Error("Nie znaleziono pracownika");
  Object.assign(member, data);
  return delay(member);
}

export async function dbDeactivateStaff(id: string): Promise<StaffMember> {
  const member = staff.find((s) => s.id === id);
  if (!member) throw new Error("Nie znaleziono pracownika");
  member.active = false;
  return delay(member);
}
