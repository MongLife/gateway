import { http } from 'msw';
import { db } from '../data/db';
import type { AccountPatch } from '@/features/accounts/types';
import { fail, includes, latency, ok, paged, q, sorted } from './util';
import { withAccount } from './devices';

const BASE = import.meta.env.VITE_API_BASE_URL ?? '/api';

export const accountHandlers = [
  http.get(`${BASE}/admin/accounts`, async ({ request }) => {
    await latency();
    const url = new URL(request.url);
    const needle = q(url);
    const platform = url.searchParams.get('platform');
    const role = url.searchParams.get('role');
    const status = url.searchParams.get('status'); // ACTIVE | DELETED
    const items = db.accounts.filter(
      (a) =>
        (!needle || includes(a.email, needle) || includes(a.name, needle) || includes(a.socialAccountId, needle)) &&
        (!platform || a.platform === platform) &&
        (!role || a.role === role) &&
        (!status || (status === 'DELETED') === a.isDeleted),
    );
    return paged(sorted(items, url, { accountId: (a) => a.accountId, createdAt: (a) => a.createdAt }, 'accountId,desc'), url);
  }),

  http.get(`${BASE}/admin/accounts/:id`, async ({ params }) => {
    await latency();
    const a = db.accounts.find((x) => x.accountId === Number(params.id));
    return a ? ok(a) : fail(404, 'NOT_EXISTS_ACCOUNT', '계정이 없습니다.');
  }),

  http.get(`${BASE}/admin/accounts/:id/devices`, async ({ params }) => {
    await latency();
    return ok(db.devices.filter((d) => d.accountId === Number(params.id)).map(withAccount));
  }),

  http.get(`${BASE}/admin/accounts/:id/login-histories`, async ({ params }) => {
    await latency();
    return ok(
      db.loginHistories
        .filter((h) => h.accountId === Number(params.id))
        .sort((a, b) => b.loginAt.localeCompare(a.loginAt)),
    );
  }),

  http.patch(`${BASE}/admin/accounts/:id`, async ({ params, request }) => {
    await latency();
    const a = db.accounts.find((x) => x.accountId === Number(params.id));
    if (!a) return fail(404, 'NOT_EXISTS_ACCOUNT', '계정이 없습니다.');
    const body = (await request.json()) as AccountPatch;
    if (body.name !== undefined) a.name = body.name;
    if (body.role !== undefined) a.role = body.role;
    if (body.isDeleted !== undefined) a.isDeleted = body.isDeleted;
    a.updatedAt = new Date().toISOString();
    return ok(a);
  }),
];
