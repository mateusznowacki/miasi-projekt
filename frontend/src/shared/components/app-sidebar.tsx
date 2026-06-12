import { Link } from "@tanstack/react-router";
import {
  Activity,
  CalendarDays,
  CalendarPlus,
  ClipboardList,
  LayoutDashboard,
  Stethoscope,
  User,
  Users,
} from "lucide-react";
import type { LucideIcon } from "lucide-react";
import {
  Sidebar,
  SidebarContent,
  SidebarGroup,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
} from "@/components/ui/sidebar";
import { useAuth } from "@/shared/auth/use-auth";
import type { Role } from "@/shared/types/role";

interface NavItem {
  label: string;
  to: string;
  icon: LucideIcon;
  roles: Role[];
}

const NAV_ITEMS: NavItem[] = [
  {
    label: "Pulpit",
    to: "/dashboard",
    icon: LayoutDashboard,
    roles: ["patient", "doctor", "admin_staff", "admin"],
  },
  {
    label: "Wizyty",
    to: "/appointments",
    icon: ClipboardList,
    roles: ["patient", "doctor", "admin_staff"],
  },
  {
    label: "Umów wizytę",
    to: "/appointments/new",
    icon: CalendarPlus,
    roles: ["patient", "admin_staff"],
  },
  {
    label: "Harmonogram",
    to: "/schedule",
    icon: CalendarDays,
    roles: ["doctor", "admin_staff"],
  },
  {
    label: "Pacjenci",
    to: "/patients",
    icon: Users,
    roles: ["doctor", "admin_staff"],
  },
  {
    label: "Personel",
    to: "/staff",
    icon: Stethoscope,
    roles: ["admin_staff", "admin"],
  },
  {
    label: "Mój profil",
    to: "/profile",
    icon: User,
    roles: ["patient", "doctor", "admin_staff"],
  },
];

export function AppSidebar() {
  const auth = useAuth();
  const role = auth?.role;
  const items = role ? NAV_ITEMS.filter((item) => item.roles.includes(role)) : [];

  return (
    <Sidebar>
      <SidebarHeader>
        <div className="flex items-center gap-2 px-2 py-1.5">
          <div className="flex size-9 items-center justify-center rounded-lg bg-primary text-primary-foreground">
            <Activity className="size-5" />
          </div>
          <div className="leading-tight">
            <Link to="/" className="text-base font-semibold tracking-tight hover:underline focus:underline">
              Medflow
            </Link>
            <p className="text-xs text-muted-foreground">Opieka medyczna</p>
          </div>
        </div>
      </SidebarHeader>
      <SidebarContent>
        <SidebarGroup>
          <SidebarGroupLabel>Nawigacja</SidebarGroupLabel>
          <SidebarGroupContent>
            <SidebarMenu>
              {items.map((item) => (
                <SidebarMenuItem key={item.to}>
                  <SidebarMenuButton asChild tooltip={item.label}>
                    <Link to={item.to} activeProps={{ "data-active": "true" }}>
                      <item.icon />
                      <span>{item.label}</span>
                    </Link>
                  </SidebarMenuButton>
                </SidebarMenuItem>
              ))}
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>
      </SidebarContent>
    </Sidebar>
  );
}
