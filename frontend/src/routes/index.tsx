import { MedflowHomePage } from "@/features/medflow/pages/medflow-home-page";
import { createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/")({
  component: MedflowHomePage,
});
