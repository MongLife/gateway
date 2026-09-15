import { http } from 'msw';
import { db } from '../data/db';
import { fail, latency, ok } from './util';

const BASE = import.meta.env.VITE_API_BASE_URL ?? '/api';

const EXPIRES_IN = 300;
const RESEND_AFTER = 30;
/** 개발 편의: 항상 통과하는 코드 */
const MASTER_CODE = '000000';

const issued = new Map<string, { code: string; issuedAt: number }>();

export const authHandlers = [
  http.post(`${BASE}/public/admin/auth/email/code`, async ({ request }) => {
    await latency();
    const { email } = (await request.json()) as { email?: string };
    if (!email) return fail(400, 'BAD_REQUEST', '이메일이 없습니다.');
    const account = db.accounts.find((a) => a.email === email && !a.isDeleted);
    if (!account || account.role !== 'ADMIN') return fail(403, 'NOT_ADMIN_ACCOUNT', '관리자 권한이 있는 계정이 아닙니다.');

    const prev = issued.get(email);
    if (prev && Date.now() - prev.issuedAt < RESEND_AFTER * 1000)
      return fail(429, 'TOO_MANY_REQUESTS', `${RESEND_AFTER}초 후에 다시 요청할 수 있습니다.`);

    const code = String(Math.floor(Math.random() * 1_000_000)).padStart(6, '0');
    issued.set(email, { code, issuedAt: Date.now() });
    console.info(`[mock] email code for ${email}: ${code} (or ${MASTER_CODE})`);
    return ok({ expiresIn: EXPIRES_IN, resendAfter: RESEND_AFTER }, '인증 코드를 발송했습니다.');
  }),

  http.post(`${BASE}/public/admin/auth/email/verify`, async ({ request }) => {
    await latency();
    const { email, code } = (await request.json()) as { email?: string; code?: string };
    if (!email || !code) return fail(400, 'BAD_REQUEST', '이메일과 코드가 필요합니다.');
    const entry = issued.get(email);
    if (!entry) return fail(400, 'INVALID_CODE', '인증 코드를 먼저 요청하세요.');
    if (Date.now() - entry.issuedAt > EXPIRES_IN * 1000) {
      issued.delete(email);
      return fail(410, 'EXPIRED_CODE', '인증 코드가 만료되었습니다.');
    }
    if (code !== entry.code && code !== MASTER_CODE) return fail(400, 'INVALID_CODE', '인증 코드가 올바르지 않습니다.');

    issued.delete(email);
    const account = db.accounts.find((a) => a.email === email)!;
    return ok({
      accountId: account.accountId,
      accessToken: `mock-access-${Date.now()}`,
      refreshToken: `mock-refresh-${Date.now()}`,
    });
  }),

  http.post(`${BASE}/public/auth/logout`, async () => {
    await latency();
    return ok(null);
  }),
];
