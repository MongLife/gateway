import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import type { PageParams } from '@/shared/api/types';
import { sessionsApi } from './api';

export const sessionKeys = {
  all: ['sessions'] as const,
  list: (params: object) => [...sessionKeys.all, 'list', params] as const,
  tokens: (params: object) => [...sessionKeys.all, 'tokens', params] as const,
};

export const useActiveSessions = (params: Parameters<typeof sessionsApi.list>[0]) =>
  useQuery({ queryKey: sessionKeys.list(params), queryFn: () => sessionsApi.list(params), refetchInterval: 30_000 });

export const useTokens = (params: PageParams & { accountId?: number }) =>
  useQuery({ queryKey: sessionKeys.tokens(params), queryFn: () => sessionsApi.tokens(params) });

export function useRevokeToken() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (refreshToken: string) => sessionsApi.revoke(refreshToken),
    onSuccess: () => qc.invalidateQueries({ queryKey: sessionKeys.all }),
  });
}

export function useRevokeAccountTokens() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (accountId: number) => sessionsApi.revokeByAccount(accountId),
    onSuccess: () => qc.invalidateQueries({ queryKey: sessionKeys.all }),
  });
}
