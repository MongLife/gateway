import { Badge } from '@/shared/ui';
import type { Platform } from '../types';

const tones = { google: 'primary', apple: 'neutral', kakao: 'warning' } as const;

export function PlatformBadge({ platform }: { platform?: Platform | null }) {
  if (!platform) return <span className="text-muted-foreground">-</span>;
  return <Badge tone={tones[platform]}>{platform}</Badge>;
}
