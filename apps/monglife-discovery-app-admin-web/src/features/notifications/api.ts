import { api } from '@/shared/api/client';
import type { PageParams } from '@/shared/api/types';
import type { NotifiableDevice, NotificationRequest } from './types';

export const notificationsApi = {
  devices: (params: PageParams & { accountId?: number; deviceName?: string }) =>
    api.getPage<NotifiableDevice>('/admin/notification/devices', params),
  /** 실존 엔드포인트: NotificationController → Kafka */
  send: (body: NotificationRequest) => api.post<void>('/admin/notification/mongs', body),
};
