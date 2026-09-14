import { http } from 'msw';
import { db } from '../data/db';
import { fail, includes, latency, ok, paged, q, sorted } from './util';

const BASE = import.meta.env.VITE_API_BASE_URL ?? '/api';

export function withAccount<T extends { accountId?: number | null }>(d: T) {
  const a = d.accountId ? db.accounts.find((x) => x.accountId === d.accountId) : undefined;
  return { ...d, accountEmail: a?.email ?? null, accountName: a?.name ?? null };
}

export const deviceHandlers = [
  http.get(`${BASE}/admin/devices`, async ({ request }) => {
    await latency();
    const url = new URL(request.url);
    const needle = q(url);
    const unmappedOnly = url.searchParams.get('unmappedOnly') === 'true';
    const fcm = url.searchParams.get('fcm'); // REGISTERED | NONE
    const deviceName = url.searchParams.get('deviceName');
    const items = db.devices
      .map(withAccount)
      .filter(
        (d) =>
          (!unmappedOnly || !d.accountId) &&
          (!fcm || (fcm === 'REGISTERED') === !!d.fcmToken) &&
          (!deviceName || d.deviceName === deviceName) &&
          (!needle || includes(d.deviceId, needle) || includes(d.deviceName, needle) || includes(d.accountEmail, needle)),
      );
    return paged(sorted(items, url, { createdAt: (d) => d.createdAt }, 'createdAt,desc'), url);
  }),

  http.put(`${BASE}/admin/devices/:deviceId/account`, async ({ params, request }) => {
    await latency();
    const d = db.devices.find((x) => x.deviceId === params.deviceId);
    if (!d) return fail(404, 'NOT_EXISTS_DEVICE', '기기가 없습니다.');
    const { accountId } = (await request.json()) as { accountId: number };
    if (!db.accounts.some((a) => a.accountId === accountId && !a.isDeleted))
      return fail(404, 'NOT_EXISTS_ACCOUNT', '계정이 없습니다.');
    d.accountId = accountId;
    return ok(withAccount(d));
  }),

  http.delete(`${BASE}/admin/devices/:deviceId/account`, async ({ params }) => {
    await latency();
    const d = db.devices.find((x) => x.deviceId === params.deviceId);
    if (!d) return fail(404, 'NOT_EXISTS_DEVICE', '기기가 없습니다.');
    d.accountId = null;
    return ok(withAccount(d));
  }),

  http.delete(`${BASE}/admin/devices/:deviceId`, async ({ params }) => {
    await latency();
    const i = db.devices.findIndex((x) => x.deviceId === params.deviceId);
    if (i < 0) return fail(404, 'NOT_EXISTS_DEVICE', '기기가 없습니다.');
    db.devices.splice(i, 1);
    for (let k = db.tokens.length - 1; k >= 0; k--) if (db.tokens[k].deviceId === params.deviceId) db.tokens.splice(k, 1);
    return ok(null);
  }),
];
