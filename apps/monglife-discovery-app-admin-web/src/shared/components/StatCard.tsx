import type { ReactNode } from 'react';
import { Card } from '@/shared/ui';

interface StatCardProps {
  label: string;
  value: ReactNode;
  hint?: string;
  icon?: ReactNode;
}

export function StatCard({ label, value, hint, icon }: StatCardProps) {
  return (
    <Card className="flex items-center gap-4 p-5">
      {icon && <div className="flex size-10 items-center justify-center rounded-md bg-primary-soft text-primary">{icon}</div>}
      <div className="min-w-0">
        <p className="text-xs text-muted-foreground">{label}</p>
        <p className="mt-0.5 text-2xl font-semibold tabular-nums">{value}</p>
        {hint && <p className="mt-0.5 text-xs text-muted-foreground">{hint}</p>}
      </div>
    </Card>
  );
}
