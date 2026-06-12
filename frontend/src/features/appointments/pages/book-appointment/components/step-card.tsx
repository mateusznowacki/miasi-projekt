import type { ReactNode } from "react";
import { Card, CardContent } from "@/components/ui/card";

export function StepCard({
  step,
  title,
  children,
}: {
  step: number;
  title: string;
  children: ReactNode;
}) {
  return (
    <Card>
      <CardContent className="space-y-4 pt-6">
        <div className="flex items-center gap-3">
          <span className="flex size-7 items-center justify-center rounded-full bg-primary text-sm font-semibold text-primary-foreground">
            {step}
          </span>
          <h2 className="font-semibold">{title}</h2>
        </div>
        {children}
      </CardContent>
    </Card>
  );
}
