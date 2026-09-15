import { http } from 'msw';
import { db } from '../data/db';
import type { ActiveSession } from '@/features/sessions/types';
import { latency, ok, paged, sorted } from './util';

const BASE = import.meta.env.VITE_API_BASE_URL ?? '/api';

const live = () => db.tokens.filter((t) => t.expiration > 0);

export const sessionHandlers = [
  http.get(`${BASE}/admin/sessions`, async ({ request }) => {
    await latency();
    const url = new URL(request.url);
    const deviceName = url.searchParams.get('deviceName');
    const app = url.searchParams.get('appPackageName');
    const ver = url.searchParams.get('buildVersion');
    const items: ActiveSession[] = live()
      .map((t) => {
        const a = db.accounts.find((x) => x.accountId === t.accountId);
        const d = db.devices.find((x) => x.deviceId === t.deviceId);
        return { ...t, email: a?.email, name: a?.name, deviceName: d?.deviceName };
      })
      .filter((s) => (!deviceName || s.deviceName === deviceName) && (!app || s.appPackageName === app) && (!ver || s.buildVersion === ver));
    return paged(sorted(items, url, { createdAt: (s) => s.createdAt }, 'createdAt,desc'), url);
  }),

  http.get(`${BASE}/admin/sessions/tokens`, async ({ request }) => {
    await latency();
    const url = new URL(request.url);
    const accountId = url.searchParams.get('accountId');
    const items = live()
      .filter((t) => !accountId || t.accountId === Number(accountId))
      .sort((a, b) => b.createdAt.localeCompare(a.createdAt));
    return paged(items, url);
  }),

  http.delete(`${BASE}/admin/sessions/accounts/:accountId`, async ({ params }) => {
    await latency();
    for (let i = db.tokens.length - 1; i >= 0; i--) {
      if (db.tokens[i].accountId === Number(params.accountId)) db.tokens.splice(i, 1);
    }
    return ok(null);
  }),

  http.delete(`${BASE}/admin/sessions/:refreshToken`, async ({ params }) => {
    await latency();
    const i = db.tokens.findIndex((t) => t.refreshToken === params.refreshToken);
    if (i >= 0) db.tokens.splice(i, 1);
    return ok(null);
  }),
];
