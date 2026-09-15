import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { appVersionsApi } from './api';
import type { CreateAppVersion } from './types';

export const appVersionKeys = { all: ['app-versions'] as const };

export const useAppVersions = () =>
  useQuery({ queryKey: appVersionKeys.all, queryFn: appVersionsApi.list });

export function useCreateAppVersion() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (body: CreateAppVersion) => appVersionsApi.create(body),
    onSuccess: () => qc.invalidateQueries({ queryKey: appVersionKeys.all }),
  });
}

export function useSetMustUpdate() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ appVersionId, mustUpdate }: { appVersionId: number; mustUpdate: boolean }) =>
      appVersionsApi.setMustUpdate(appVersionId, mustUpdate),
    onSuccess: () => qc.invalidateQueries({ queryKey: appVersionKeys.all }),
  });
}

export function useRemoveAppVersion() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (appVersionId: number) => appVersionsApi.remove(appVersionId),
    onSuccess: () => qc.invalidateQueries({ queryKey: appVersionKeys.all }),
  });
}
