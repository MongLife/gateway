import { http } from 'msw';
import { db } from '../data/db';
import type { LoginStat, SignupStat, UserStats } from '@/features/dashboard/types';
import { latency, ok } from './util';

const BASE = import.meta.env.VITE_API_BASE_URL ?? '/api';
const DAY = 86_400_000;
const ymd = (ms: number) => new Date(ms).toISOString().slice(0, 10);

export const statsHandlers = [
  http.get(`${BASE}/admin/stats/users`, async () => {
    await latency();
    const today = ymd(Date.now());
    const week = ymd(Date.now() - 7 * DAY);
    const month = ymd(Date.now() - 30 * DAY);
    const recentlyLoggedIn = new Set(db.loginHistories.filter((h) => h.loginAt >= month).map((h) => h.accountId));
    const stats: UserStats = {
      todayJoined: db.accounts.filter((a) => a.createdAt.slice(0, 10) === today).length,
      weekJoined: db.accounts.filter((a) => a.createdAt.slice(0, 10) >= week).length,
      totalAccounts: db.accounts.filter((a) => !a.isDeleted).length,
      inactiveAccounts: db.accounts.filter((a) => !a.isDeleted && !recentlyLoggedIn.has(a.accountId)).length,
      activeSessions: db.tokens.length,
    };
    return ok(stats);
  }),

  http.get(`${BASE}/admin/stats/logins`, async ({ request }) => {
    await latency();
    const days = Math.min(90, Math.max(1, Number(new URL(request.url).searchParams.get('days') ?? 14)));
    const result: LoginStat[] = [];
    for (let i = days - 1; i >= 0; i--) {
      const date = ymd(Date.now() - i * DAY);
      const rows = db.loginHistories.filter((h) => h.loginAt === date);
      result.push({
        date,
        loginCount: rows.reduce((s, h) => s + h.loginCount, 0),
        uniqueAccounts: new Set(rows.map((h) => h.accountId)).size,
      });
    }
    return ok(result);
  }),

  http.get(`${BASE}/admin/stats/signups`, async ({ request }) => {
    await latency();
    const days = Math.min(90, Math.max(1, Number(new URL(request.url).searchParams.get('days') ?? 14)));
    const result: SignupStat[] = [];
    for (let i = days - 1; i >= 0; i--) {
      const date = ymd(Date.now() - i * DAY);
      result.push({ date, count: db.accounts.filter((a) => a.createdAt.slice(0, 10) === date).length });
    }
    return ok(result);
  }),
];
