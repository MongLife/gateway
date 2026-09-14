import { http } from 'msw';
import { errorReports } from '../data/errorReports';
import type { ErrorReportSummary } from '@/features/error-reports/types';
import { fail, includes, latency, ok, paged, q, sorted } from './util';

const BASE = import.meta.env.VITE_API_BASE_URL ?? '/api';

export const errorReportHandlers = [
  http.get(`${BASE}/admin/error-reports`, async ({ request }) => {
    await latency();
    const url = new URL(request.url);
    const needle = q(url);
    const status = url.searchParams.get('status');
    const deviceName = url.searchParams.get('deviceName');
    const app = url.searchParams.get('appPackageName');
    const ver = url.searchParams.get('buildVersion');
    const items: ErrorReportSummary[] = errorReports
      .filter(
        (r) =>
          (!status || r.status === status) &&
          (!deviceName || r.deviceName === deviceName) &&
          (!app || r.appPackageName === app) &&
          (!ver || r.buildVersion === ver) &&
          (!needle || includes(r.title, needle) || includes(r.email, needle) || includes(r.name, needle)),
      )
      .map((r) => {
        const { replies, ...rest } = r;
        delete (rest as Partial<typeof r>).content;
        return { ...rest, replyCount: replies.length };
      });
    return paged(sorted(items, url, { reportId: (r) => r.reportId, createdAt: (r) => r.createdAt }, 'createdAt,desc'), url);
  }),

  http.get(`${BASE}/admin/error-reports/:id`, async ({ params }) => {
    await latency();
    const r = errorReports.find((x) => x.reportId === Number(params.id));
    return r ? ok(r) : fail(404, 'NOT_EXISTS_ERROR_REPORT', '신고가 없습니다.');
  }),

  http.post(`${BASE}/admin/error-reports/:id/replies`, async ({ params, request }) => {
    await latency();
    const r = errorReports.find((x) => x.reportId === Number(params.id));
    if (!r) return fail(404, 'NOT_EXISTS_ERROR_REPORT', '신고가 없습니다.');
    const { content } = (await request.json()) as { content: string };
    const reply = { replyId: Date.now(), content, sentTo: r.email, createdAt: new Date().toISOString() };
    r.replies.push(reply);
    r.status = 'ANSWERED';
    console.info('[mock] error-report reply mailed', { to: r.email, content });
    return ok(reply, '답변을 발송했습니다.');
  }),
];
