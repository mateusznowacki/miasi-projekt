import { Link } from "@tanstack/react-router";
import {
  CalendarDays,
  CalendarPlus,
  ClipboardList,
  Stethoscope,
  User,
  Users,
} from "lucide-react";
import type { LucideIcon } from "lucide-react";
import { Card } from "@/components/ui/card";
import type { Role } from "@/shared/types/role";

interface QuickLink {
  label: string;
  description: string;
  to: string;
  icon: LucideIcon;
  roles: Role[];
}

const LINKS: QuickLink[] = [
  {
    label: "Umów wizytę",
    description: "Zarezerwuj nowy termin u lekarza",
    to: "/appointments/new",
    icon: CalendarPlus,
    roles: ["patient", "admin_staff"],
  },
  {
    label: "Moje wizyty",
    description: "Nadchodzące i przeszłe wizyty",
    to: "/appointments",
    icon: ClipboardList,
    roles: ["patient", "doctor", "admin_staff"],
  },
  {
    label: "Harmonogram",
    description: "Zarządzaj terminami i dostępnością",
    to: "/schedule",
    icon: CalendarDays,
    roles: ["doctor", "admin_staff"],
  },
  {
    label: "Pacjenci",
    description: "Wyszukaj i przeglądaj kartoteki",
    to: "/patients",
    icon: Users,
    roles: ["doctor", "admin_staff"],
  },
  {
    label: "Personel",
    description: "Lekarze i pracownicy administracyjni",
    to: "/staff",
    icon: Stethoscope,
    roles: ["admin_staff", "admin"],
  },
  {
    label: "Mój profil",
    description: "Twoje dane osobowe",
    to: "/profile",
    icon: User,
    roles: ["patient", "doctor", "admin_staff"],
  },
];

export function QuickLinks({ role }: { role: Role }) {
  const links = LINKS.filter((link) => link.roles.includes(role));

  return (
    <div className="grid grid-cols-1 gap-3 sm:grid-cols-2 lg:grid-cols-3">
      {links.map((link) => (
        <Card key={link.to} className="p-0">
          <Link
            to={link.to}
            className="flex h-full items-start gap-3 rounded-xl p-4 transition-colors hover:bg-accent/40"
          >
            <div className="flex size-10 shrink-0 items-center justify-center rounded-lg bg-primary/10 text-primary">
              <link.icon className="size-5" />
            </div>
            <div>
              <p className="font-medium">{link.label}</p>
              <p className="text-sm text-muted-foreground">{link.description}</p>
            </div>
          </Link>
        </Card>
      ))}
    </div>
  );
}
