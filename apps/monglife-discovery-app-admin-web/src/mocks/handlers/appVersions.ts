import { http } from 'msw';
import { db } from '../data/db';
import type { CreateAppVersion } from '@/features/app-versions/types';
import { fail, latency, ok } from './util';

const BASE = import.meta.env.VITE_API_BASE_URL ?? '/api';

export const appVersionHandlers = [
  http.get(`${BASE}/admin/app-versions`, async () => {
    await latency();
    return ok(db.appVersions);
  }),

  http.post(`${BASE}/admin/app-versions`, async ({ request }) => {
    await latency();
    const body = (await request.json()) as CreateAppVersion;
    if (db.appVersions.some((v) => v.appPackageName === body.appPackageName && v.buildVersion === body.buildVersion))
      return fail(409, 'ALREADY_EXISTS_APP_VERSION', '이미 등록된 버전입니다.');
    const ts = new Date().toISOString();
    const created = { appVersionId: Math.max(0, ...db.appVersions.map((v) => v.appVersionId)) + 1, ...body, createdAt: ts, updatedAt: ts };
    db.appVersions.push(created);
    return ok(created);
  }),

  http.patch(`${BASE}/admin/app-versions/:id`, async ({ params, request }) => {
    await latency();
    const v = db.appVersions.find((x) => x.appVersionId === Number(params.id));
    if (!v) return fail(404, 'NOT_EXISTS_APP_VERSION', '버전이 없습니다.');
    const { mustUpdate } = (await request.json()) as { mustUpdate: boolean };
    v.mustUpdate = mustUpdate;
    v.updatedAt = new Date().toISOString();
    return ok(v);
  }),

  http.delete(`${BASE}/admin/app-versions/:id`, async ({ params }) => {
    await latency();
    const i = db.appVersions.findIndex((x) => x.appVersionId === Number(params.id));
    if (i < 0) return fail(404, 'NOT_EXISTS_APP_VERSION', '버전이 없습니다.');
    db.appVersions.splice(i, 1);
    return ok(null);
  }),
];
