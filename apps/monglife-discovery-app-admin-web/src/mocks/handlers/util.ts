import { HttpResponse } from 'msw';
import type { PageResponseDto, ResponseDto } from '@/shared/api/types';

export const ok = <T>(result: T, message = 'OK') => {
  const body: ResponseDto<T> = { code: 'DISCOVERY_ADMIN_OK', message, result };
  return HttpResponse.json(body as ResponseDto<unknown>);
};

export const fail = (status: number, code: string, message: string) => {
  const body: ResponseDto<null> = { code, message, result: null };
  return HttpResponse.json(body as ResponseDto<unknown>, { status });
};

export function paged<T>(items: T[], url: URL) {
  const page = Math.max(0, Number(url.searchParams.get('page') ?? 0));
  const size = Math.min(100, Math.max(1, Number(url.searchParams.get('size') ?? 20)));
  const slice = items.slice(page * size, page * size + size);
  const totalPage = Math.ceil(items.length / size);
  const body: PageResponseDto<T[]> = { code: 'DISCOVERY_ADMIN_OK', message: 'OK', result: slice, page, size, totalPage, isLastPage: page + 1 >= totalPage };
  return HttpResponse.json(body as PageResponseDto<unknown[]>, { headers: { 'X-Total-Count': String(items.length) } });
}

export const q = (url: URL) => (url.searchParams.get('query') ?? '').trim().toLowerCase();
export const includes = (v: unknown, needle: string) => String(v ?? '').toLowerCase().includes(needle);

/** 실제 API 처럼 약간 지연 */
export const latency = () => new Promise((r) => setTimeout(r, 150 + Math.random() * 250));

/** `sort=field,dir` 를 적용. allowed 에 없는 필드는 무시 */
export function sorted<T>(items: T[], url: URL, allowed: Record<string, (row: T) => string | number | boolean | null | undefined>, fallback?: string) {
  const raw = url.searchParams.get('sort') ?? fallback;
  if (!raw) return items;
  const [key, dir = 'asc'] = raw.split(',');
  const get = allowed[key];
  if (!get) return items;
  const sign = dir === 'desc' ? -1 : 1;
  return [...items].sort((a, b) => {
    const va = get(a) ?? '';
    const vb = get(b) ?? '';
    return (va < vb ? -1 : va > vb ? 1 : 0) * sign;
  });
}

export const cmpVersion = (a: string, b: string) => {
  const pa = a.split('.').map(Number);
  const pb = b.split('.').map(Number);
  for (let i = 0; i < Math.max(pa.length, pb.length); i++) {
    const d = (pa[i] ?? 0) - (pb[i] ?? 0);
    if (d !== 0) return d;
  }
  return 0;
};
