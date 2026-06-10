import { useNavigate } from "@tanstack/react-router";
import { LogOut, User as UserIcon } from "lucide-react";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { clearAuth } from "@/shared/auth/auth-store";
import { useAuth } from "@/shared/auth/use-auth";
import { ROLE_LABELS } from "@/shared/types/role";

export function UserMenu() {
  const auth = useAuth();
  const navigate = useNavigate();

  if (!auth) return null;

  const initials = `${auth.firstName.charAt(0)}${auth.lastName.charAt(0)}`.toUpperCase();

  function handleLogout() {
    clearAuth();
    navigate({ to: "/login" });
  }

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button variant="ghost" className="h-9 gap-2 px-2">
          <Avatar className="size-7">
            <AvatarFallback className="bg-primary/10 text-xs font-medium text-primary">
              {initials}
            </AvatarFallback>
          </Avatar>
          <span className="hidden text-sm font-medium sm:inline">
            {auth.firstName} {auth.lastName}
          </span>
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end" className="w-56">
        <DropdownMenuLabel className="flex flex-col gap-0.5">
          <span>
            {auth.firstName} {auth.lastName}
          </span>
          <span className="text-xs font-normal text-muted-foreground">
            {ROLE_LABELS[auth.role]}
          </span>
        </DropdownMenuLabel>
        <DropdownMenuSeparator />
        {auth.role !== "admin" && (
          <DropdownMenuItem onSelect={() => navigate({ to: "/profile" })}>
            <UserIcon className="mr-2 size-4" />
            Mój profil
          </DropdownMenuItem>
        )}
        <DropdownMenuItem onSelect={handleLogout}>
          <LogOut className="mr-2 size-4" />
          Wyloguj się
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  );
}
