import { api } from '@/shared/api/client';
import type { AppVersion, CreateAppVersion } from './types';

export const appVersionsApi = {
  list: () => api.get<AppVersion[]>('/admin/app-versions'),
  create: (body: CreateAppVersion) => api.post<AppVersion>('/admin/app-versions', body),
  setMustUpdate: (appVersionId: number, mustUpdate: boolean) =>
    api.patch<AppVersion>(`/admin/app-versions/${appVersionId}`, { mustUpdate }),
  remove: (appVersionId: number) => api.delete<void>(`/admin/app-versions/${appVersionId}`),
};
