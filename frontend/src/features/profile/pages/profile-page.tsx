import { PageHeader } from "@/shared/components/page-header";
import { useAuth } from "@/shared/auth/use-auth";
import { ProfilePatientDetails } from "../components/profile-patient-details";
import { ProfileStaffDetails } from "../components/profile-staff-details";

export function ProfilePage() {
  const auth = useAuth();
  if (!auth) return null;

  return (
    <div className="space-y-6">
      <PageHeader title="Mój profil" description="Twoje dane w systemie Medflow." />
      {auth.role === "patient" ? (
        <ProfilePatientDetails patientId={auth.userId} />
      ) : (
        <ProfileStaffDetails staffId={auth.userId} />
      )}
    </div>
  );
}
