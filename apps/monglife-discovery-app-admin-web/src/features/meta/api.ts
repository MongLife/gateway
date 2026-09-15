import { api } from '@/shared/api/client';

/** 필터 셀렉트용 distinct 값 */
export interface FilterOptions {
  deviceNames: string[];
  appPackageNames: string[];
  buildVersions: string[];
}

export const metaApi = {
  filters: () => api.get<FilterOptions>('/admin/meta/filters'),
};
