import { useState } from "react";
import { Link } from "@tanstack/react-router";
import { Search, UserPlus, Users } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { EmptyState } from "@/shared/components/empty-state";
import { ListSkeleton } from "@/shared/components/list-skeleton";
import { PageHeader } from "@/shared/components/page-header";
import { useAuth } from "@/shared/auth/use-auth";
import type { StaffRole } from "@/shared/types/staff-member";
import { useStaffList } from "../api/use-staff-list";
import { StaffCard } from "../components/staff-card";

export function StaffListPage() {
  const auth = useAuth();
  const [name, setName] = useState("");
  const [role, setRole] = useState<StaffRole | "all">("all");

  const { data, isPending, isError, error } = useStaffList({
    name,
    role: role === "all" ? undefined : role,
  });

  return (
    <div>
      <PageHeader
        title="Personel"
        description="Lekarze i pracownicy administracyjni."
        actions={
          auth?.role === "admin" ? (
            <Button asChild>
              <Link to="/staff/new">
                <UserPlus className="size-4" />
                Dodaj pracownika
              </Link>
            </Button>
          ) : undefined
        }
      />

      <div className="mb-4 flex flex-col gap-3 sm:flex-row">
        <div className="relative flex-1">
          <Search className="absolute left-3 top-1/2 size-4 -translate-y-1/2 text-muted-foreground" />
          <Input
            placeholder="Szukaj po nazwisku"
            className="pl-9"
            value={name}
            onChange={(e) => setName(e.target.value)}
          />
        </div>
        <Select value={role} onValueChange={(value) => setRole(value as StaffRole | "all")}>
          <SelectTrigger className="w-full sm:w-56">
            <SelectValue />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="all">Wszyscy</SelectItem>
            <SelectItem value="doctor">Lekarze</SelectItem>
            <SelectItem value="admin_staff">Pracownicy administracyjni</SelectItem>
          </SelectContent>
        </Select>
      </div>

      {isPending ? (
        <ListSkeleton count={4} />
      ) : isError ? (
        <p className="text-sm text-destructive">{error.message}</p>
      ) : data.length === 0 ? (
        <EmptyState
          icon={Users}
          title="Brak pracowników"
          description="Nie znaleziono pracowników dla podanych kryteriów."
        />
      ) : (
        <div className="flex flex-col gap-3">
          {data.map((member) => (
            <StaffCard key={member.id} member={member} />
          ))}
        </div>
      )}
    </div>
  );
}
