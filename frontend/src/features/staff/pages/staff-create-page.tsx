import { Link } from "@tanstack/react-router";
import { ArrowLeft } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { PageHeader } from "@/shared/components/page-header";
import { StaffForm } from "../components/staff-form";

export function StaffCreatePage() {
  return (
    <div className="space-y-6">
      <Button asChild variant="ghost" size="sm" className="-ml-2">
        <Link to="/staff">
          <ArrowLeft className="size-4" />
          Wróć do personelu
        </Link>
      </Button>
      <PageHeader title="Nowy pracownik" />
      <Card>
        <CardContent className="pt-6">
          <StaffForm />
        </CardContent>
      </Card>
    </div>
  );
}
