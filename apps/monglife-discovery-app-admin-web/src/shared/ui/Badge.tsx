import type { HTMLAttributes } from 'react';
import { cn } from '@/shared/lib/cn';

type Tone = 'neutral' | 'primary' | 'success' | 'warning' | 'danger';

const tones: Record<Tone, string> = {
  neutral: 'bg-surface-muted text-muted-foreground',
  primary: 'bg-primary-soft text-primary',
  success: 'bg-success/15 text-success',
  warning: 'bg-warning/20 text-warning',
  danger: 'bg-danger-soft text-danger',
};

export function Badge({ tone = 'neutral', className, ...props }: HTMLAttributes<HTMLSpanElement> & { tone?: Tone }) {
  return (
    <span
      className={cn('inline-flex items-center rounded-full px-2 py-0.5 text-xs font-medium', tones[tone], className)}
      {...props}
    />
  );
}
