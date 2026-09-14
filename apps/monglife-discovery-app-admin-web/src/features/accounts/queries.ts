import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { accountsApi } from './api';
import type { AccountPatch } from './types';

export const accountKeys = {
  all: ['accounts'] as const,
  list: (params: object) => [...accountKeys.all, 'list', params] as const,
  detail: (id: number) => [...accountKeys.all, 'detail', id] as const,
  devices: (id: number) => [...accountKeys.detail(id), 'devices'] as const,
  loginHistories: (id: number) => [...accountKeys.detail(id), 'login-histories'] as const,
};

export const useAccounts = (params: Parameters<typeof accountsApi.list>[0]) =>
  useQuery({ queryKey: accountKeys.list(params), queryFn: () => accountsApi.list(params) });

export const useAccount = (id: number) =>
  useQuery({ queryKey: accountKeys.detail(id), queryFn: () => accountsApi.get(id), enabled: Number.isFinite(id) });

export const useAccountDevices = (id: number) =>
  useQuery({ queryKey: accountKeys.devices(id), queryFn: () => accountsApi.devices(id) });

export const useAccountLoginHistories = (id: number) =>
  useQuery({ queryKey: accountKeys.loginHistories(id), queryFn: () => accountsApi.loginHistories(id) });

export function usePatchAccount(id: number) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (body: AccountPatch) => accountsApi.patch(id, body),
    onSuccess: () => qc.invalidateQueries({ queryKey: accountKeys.all }),
  });
}
