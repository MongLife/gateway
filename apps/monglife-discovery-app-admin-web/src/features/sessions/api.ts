import { api } from '@/shared/api/client';
import type { PageParams } from '@/shared/api/types';
import type { ActiveSession, Token } from './types';

export const sessionsApi = {
  list: (params: PageParams & { deviceName?: string; appPackageName?: string; buildVersion?: string; sort?: string }) =>
    api.getPage<ActiveSession>('/admin/sessions', params),
  tokens: (params: PageParams & { accountId?: number }) => api.getPage<Token>('/admin/sessions/tokens', params),
  revoke: (refreshToken: string) => api.delete<void>(`/admin/sessions/${encodeURIComponent(refreshToken)}`),
  revokeByAccount: (accountId: number) => api.delete<void>(`/admin/sessions/accounts/${accountId}`),
};
