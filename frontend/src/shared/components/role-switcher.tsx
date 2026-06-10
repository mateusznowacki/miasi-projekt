import { useNavigate } from "@tanstack/react-router";
import { FlaskConical } from "lucide-react";
import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { getDemoUserForRole } from "@/shared/api/mock-db";
import { setAuth } from "@/shared/auth/auth-store";
import { useAuth } from "@/shared/auth/use-auth";
import { ROLE_LABELS, type Role } from "@/shared/types/role";

const ROLES: Role[] = ["patient", "doctor", "admin_staff", "admin"];

export function RoleSwitcher() {
  const auth = useAuth();
  const navigate = useNavigate();

  function switchTo(role: Role) {
    setAuth(getDemoUserForRole(role));
    navigate({ to: "/dashboard" });
  }

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button variant="outline" size="sm" className="gap-2">
          <FlaskConical className="size-4" />
          <span className="hidden md:inline">Demo: {auth ? ROLE_LABELS[auth.role] : "—"}</span>
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end" className="w-56">
        <DropdownMenuLabel>Przełącz rolę (demo)</DropdownMenuLabel>
        <DropdownMenuSeparator />
        {ROLES.map((role) => (
          <DropdownMenuItem
            key={role}
            onSelect={() => switchTo(role)}
            disabled={auth?.role === role}
          >
            {ROLE_LABELS[role]}
          </DropdownMenuItem>
        ))}
      </DropdownMenuContent>
    </DropdownMenu>
  );
}
