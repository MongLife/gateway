import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { errorReportsApi } from './api';

export const errorReportKeys = {
  all: ['error-reports'] as const,
  list: (params: object) => [...errorReportKeys.all, 'list', params] as const,
  detail: (id: number) => [...errorReportKeys.all, 'detail', id] as const,
};

export const useErrorReports = (params: Parameters<typeof errorReportsApi.list>[0]) =>
  useQuery({ queryKey: errorReportKeys.list(params), queryFn: () => errorReportsApi.list(params) });

export const useErrorReport = (id: number) =>
  useQuery({ queryKey: errorReportKeys.detail(id), queryFn: () => errorReportsApi.get(id), enabled: Number.isFinite(id) });

export function useReplyErrorReport(id: number) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (content: string) => errorReportsApi.reply(id, content),
    onSuccess: () => qc.invalidateQueries({ queryKey: errorReportKeys.all }),
  });
}
