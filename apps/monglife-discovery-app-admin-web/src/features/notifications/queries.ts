import { useMutation, useQuery } from '@tanstack/react-query';
import { notificationsApi } from './api';
import type { SendResult } from './types';

export const notificationKeys = {
  devices: (params: object) => ['notifications', 'devices', params] as const,
};

export const useNotifiableDevices = (params: Parameters<typeof notificationsApi.devices>[0]) =>
  useQuery({ queryKey: notificationKeys.devices(params), queryFn: () => notificationsApi.devices(params) });

/** 실제 API 가 계정 단위라 distinct accountId 마다 1건씩 보낸다 */
export const useSendNotification = () =>
  useMutation({
    mutationFn: async ({ accountIds, title, body }: { accountIds: number[]; title: string; body: string }) => {
      const settled = await Promise.allSettled(accountIds.map((accountId) => notificationsApi.send({ accountId, title, body })));
      return settled.map<SendResult>((r, i) => ({
        accountId: accountIds[i],
        ok: r.status === 'fulfilled',
        error: r.status === 'rejected' ? (r.reason instanceof Error ? r.reason.message : String(r.reason)) : undefined,
      }));
    },
  });
