import type { Device } from '@/features/devices/types';

/** 기존 NotificationRequestDto (POST /admin/notification/mongs) */
export interface NotificationRequest {
  accountId: number;
  title: string;
  body: string;
}

export interface NotifiableDevice extends Device {
  accountId: number;
  email?: string;
  name?: string;
}

export interface SendResult {
  accountId: number;
  ok: boolean;
  error?: string;
}
