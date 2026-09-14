import { useQuery } from '@tanstack/react-query';
import { statsApi } from './api';

export const useUserStats = () => useQuery({ queryKey: ['stats', 'users'], queryFn: statsApi.users });
export const useLoginStats = (days: number) =>
  useQuery({ queryKey: ['stats', 'logins', days], queryFn: () => statsApi.logins(days) });
export const useSignupStats = (days: number) =>
  useQuery({ queryKey: ['stats', 'signups', days], queryFn: () => statsApi.signups(days) });
