import { api } from '@/shared/api/client';
import type { PageParams } from '@/shared/api/types';
import type { Device } from './types';

export const devicesApi = {
  list: (params: PageParams & { unmappedOnly?: boolean; fcm?: 'REGISTERED' | 'NONE'; deviceName?: string; sort?: string }) =>
    api.getPage<Device>('/admin/devices', params),
  connect: (deviceId: string, accountId: number) =>
    api.put<Device>(`/admin/devices/${encodeURIComponent(deviceId)}/account`, { accountId }),
  disconnect: (deviceId: string) => api.delete<Device>(`/admin/devices/${encodeURIComponent(deviceId)}/account`),
  remove: (deviceId: string) => api.delete<void>(`/admin/devices/${encodeURIComponent(deviceId)}`),
};
