import { Link } from "@tanstack/react-router";
import { ChevronRight, Stethoscope, UserCog } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { cn } from "@/lib/utils";
import type { StaffDto } from "@/client";
import { mapStaffRole } from "@/shared/types/map-staff-role";

export function StaffCard({ member }: { member: StaffDto }) {
  const isDoctor = mapStaffRole(member.role) === "doctor";
  const Icon = isDoctor ? Stethoscope : UserCog;
  const subtitle = isDoctor ? member.specialization : member.position;

  return (
    <Link
      to="/staff/$id"
      params={{ id: member.id ?? "" }}
      className="group flex items-center gap-4 rounded-xl border bg-card p-4 transition-colors hover:border-primary/40 hover:bg-accent/40"
    >
      <div className="flex size-11 shrink-0 items-center justify-center rounded-full bg-primary/10 text-primary">
        <Icon className="size-5" />
      </div>
      <div className="min-w-0 flex-1">
        <div className="flex items-center gap-2">
          <p className="truncate font-medium">
            {isDoctor ? "dr " : ""}
            {member.firstName} {member.lastName}
          </p>
          {member.active === false && (
            <Badge className="border-transparent bg-muted text-muted-foreground">Nieaktywny</Badge>
          )}
        </div>
        <p className={cn("text-sm text-muted-foreground", !subtitle && "italic")}>
          {isDoctor ? "Lekarz" : "Pracownik administracyjny"}
          {subtitle ? ` · ${subtitle}` : ""}
        </p>
      </div>
      <ChevronRight className="size-5 shrink-0 text-muted-foreground transition-transform group-hover:translate-x-0.5" />
    </Link>
  );
}
