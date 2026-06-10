import { Link } from "@tanstack/react-router";
import { ChevronRight, IdCard, Phone } from "lucide-react";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import type { Patient } from "@/shared/types/patient";

export function PatientCard({ patient }: { patient: Patient }) {
  const { firstName, lastName, pesel, phone } = patient.personalData;
  const initials = `${firstName.charAt(0)}${lastName.charAt(0)}`.toUpperCase();

  return (
    <Link
      to="/patients/$id"
      params={{ id: patient.id }}
      className="group flex items-center gap-4 rounded-xl border bg-card p-4 transition-colors hover:border-primary/40 hover:bg-accent/40"
    >
      <Avatar className="size-11">
        <AvatarFallback className="bg-primary/10 font-medium text-primary">
          {initials}
        </AvatarFallback>
      </Avatar>
      <div className="min-w-0 flex-1">
        <p className="truncate font-medium">
          {firstName} {lastName}
        </p>
        <div className="mt-1 flex flex-wrap items-center gap-x-4 gap-y-1 text-sm text-muted-foreground">
          <span className="inline-flex items-center gap-1">
            <IdCard className="size-3.5" />
            {pesel}
          </span>
          <span className="inline-flex items-center gap-1">
            <Phone className="size-3.5" />
            {phone}
          </span>
        </div>
      </div>
      <ChevronRight className="size-5 shrink-0 text-muted-foreground transition-transform group-hover:translate-x-0.5" />
    </Link>
  );
}
