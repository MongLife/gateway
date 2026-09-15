import { api } from '@/shared/api/client';
import type { PageParams } from '@/shared/api/types';
import type { ErrorReport, ErrorReportReply, ErrorReportStatus, ErrorReportSummary } from './types';

export const errorReportsApi = {
  list: (params: PageParams & { status?: ErrorReportStatus; deviceName?: string; appPackageName?: string; buildVersion?: string; sort?: string }) =>
    api.getPage<ErrorReportSummary>('/admin/error-reports', params),
  get: (reportId: number) => api.get<ErrorReport>(`/admin/error-reports/${reportId}`),
  /** 답변은 사용자 이메일로 발송된다 */
  reply: (reportId: number, content: string) =>
    api.post<ErrorReportReply>(`/admin/error-reports/${reportId}/replies`, { content }),
};
