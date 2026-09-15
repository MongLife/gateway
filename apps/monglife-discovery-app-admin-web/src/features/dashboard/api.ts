import { api } from '@/shared/api/client';
import type { LoginStat, SignupStat, UserStats } from './types';

export const statsApi = {
  users: () => api.get<UserStats>('/admin/stats/users'),
  logins: (days: number) => api.get<LoginStat[]>('/admin/stats/logins', { days }),
  signups: (days: number) => api.get<SignupStat[]>('/admin/stats/signups', { days }),
};
