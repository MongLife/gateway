import { api } from '@/shared/api/client';
import type { PageParams } from '@/shared/api/types';
import type { Device } from '@/features/devices/types';
import type { Account, AccountPatch, LoginHistory } from './types';

// TODO: common-api 에 /admin/accounts 가 생기면 경로만 확인. 지금은 MSW 목.
export const accountsApi = {
  list: (params: PageParams & { platform?: string; role?: string; status?: 'ACTIVE' | 'DELETED'; sort?: string }) =>
    api.getPage<Account>('/admin/accounts', params),
  get: (accountId: number) => api.get<Account>(`/admin/accounts/${accountId}`),
  devices: (accountId: number) => api.get<Device[]>(`/admin/accounts/${accountId}/devices`),
  loginHistories: (accountId: number) => api.get<LoginHistory[]>(`/admin/accounts/${accountId}/login-histories`),
  patch: (accountId: number, body: AccountPatch) => api.patch<Account>(`/admin/accounts/${accountId}`, body),
};
