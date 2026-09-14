import { http } from 'msw';
import { db } from '../data/db';
import { cmpVersion, latency, ok } from './util';

const BASE = import.meta.env.VITE_API_BASE_URL ?? '/api';

export const metaHandlers = [
  http.get(`${BASE}/admin/meta/filters`, async () => {
    await latency();
    const uniq = (xs: string[]) => Array.from(new Set(xs)).sort();
    return ok({
      deviceNames: uniq(db.devices.map((d) => d.deviceName)),
      appPackageNames: uniq(db.appVersions.map((v) => v.appPackageName)),
      buildVersions: uniq(db.appVersions.map((v) => v.buildVersion)).sort(cmpVersion).reverse(),
    });
  }),
];
