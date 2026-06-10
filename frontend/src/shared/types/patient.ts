export interface PatientPersonalData {
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  pesel: string;
  address: string;
}

export interface PatientMedicalData {
  bloodType: string;
  allergies: string;
  chronicDiseases: string;
  medications: string;
}

export interface Patient {
  id: string;
  personalData: PatientPersonalData;
  medicalData: PatientMedicalData;
}
