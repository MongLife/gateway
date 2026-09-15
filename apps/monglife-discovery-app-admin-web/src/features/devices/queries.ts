import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { accountKeys } from '@/features/accounts/queries';
import { sessionKeys } from '@/features/sessions/queries';
import { devicesApi } from './api';

export const deviceKeys = {
  all: ['devices'] as const,
  list: (params: object) => [...deviceKeys.all, 'list', params] as const,
};

export const useDevices = (params: Parameters<typeof devicesApi.list>[0]) =>
  useQuery({ queryKey: deviceKeys.list(params), queryFn: () => devicesApi.list(params) });

export function useConnectDevice() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ deviceId, accountId }: { deviceId: string; accountId: number }) =>
      devicesApi.connect(deviceId, accountId),
    onSuccess: () => {
      void qc.invalidateQueries({ queryKey: deviceKeys.all });
      void qc.invalidateQueries({ queryKey: accountKeys.all });
    },
  });
}

export function useDisconnectDevice() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (deviceId: string) => devicesApi.disconnect(deviceId),
    onSuccess: () => {
      void qc.invalidateQueries({ queryKey: deviceKeys.all });
      void qc.invalidateQueries({ queryKey: accountKeys.all });
    },
  });
}

export function useRemoveDevice() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (deviceId: string) => devicesApi.remove(deviceId),
    onSuccess: () => {
      void qc.invalidateQueries({ queryKey: deviceKeys.all });
      void qc.invalidateQueries({ queryKey: accountKeys.all });
      void qc.invalidateQueries({ queryKey: sessionKeys.all });
    },
  });
}
