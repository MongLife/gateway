import { tokenStorage } from '@/shared/auth/tokenStorage';
import type { Page, PageResponseDto, ResponseDto } from './types';

const BASE_URL = (import.meta.env.VITE_API_BASE_URL ?? '/api').replace(/\/$/, '');

export class ApiError extends Error {
  constructor(
    public status: number,
    public code: string,
    message: string,
  ) {
    super(message);
    this.name = 'ApiError';
  }
}

export type Query = { [key: string]: string | number | boolean | undefined | null };

interface RequestOptions {
  query?: Query;
  body?: unknown;
  signal?: AbortSignal;
}

function buildUrl(path: string, query?: Query) {
  const url = new URL(BASE_URL + path, window.location.origin);
  if (query) {
    for (const [k, v] of Object.entries(query)) {
      if (v !== undefined && v !== null && v !== '') url.searchParams.set(k, String(v));
    }
  }
  return url.toString();
}

async function request<T>(method: string, path: string, opts: RequestOptions = {}): Promise<T> {
  const headers: Record<string, string> = { Accept: 'application/json' };
  const accessToken = tokenStorage.getAccessToken();
  if (accessToken) headers.Authorization = `Bearer ${accessToken}`;
  if (opts.body !== undefined) headers['Content-Type'] = 'application/json';

  const res = await fetch(buildUrl(path, opts.query), {
    method,
    headers,
    body: opts.body === undefined ? undefined : JSON.stringify(opts.body),
    signal: opts.signal,
  });

  if (res.status === 401) {
    // TODO: /public/auth/reissue 로 재발급 시도. 관리자 인증 방식이 정해지면 구현.
    tokenStorage.clear();
    if (window.location.pathname !== '/login') window.location.assign('/login');
    throw new ApiError(401, 'UNAUTHORIZED', '세션이 만료되었습니다.');
  }

  const text = await res.text();
  const json = text ? (JSON.parse(text) as ResponseDto<T>) : undefined;

  if (!res.ok) {
    throw new ApiError(res.status, json?.code ?? String(res.status), json?.message ?? res.statusText);
  }
  return json?.result as T;
}

/** PageResponseDto 를 Page<T> 로 변환. total 은 X-Total-Count 헤더가 있으면 그 값을 쓴다. */
async function requestPage<T>(path: string, query?: Query): Promise<Page<T>> {
  const headers: Record<string, string> = { Accept: 'application/json' };
  const accessToken = tokenStorage.getAccessToken();
  if (accessToken) headers.Authorization = `Bearer ${accessToken}`;

  const res = await fetch(buildUrl(path, query), { headers });
  if (res.status === 401) {
    tokenStorage.clear();
    if (window.location.pathname !== '/login') window.location.assign('/login');
    throw new ApiError(401, 'UNAUTHORIZED', '세션이 만료되었습니다.');
  }
  const json = (await res.json()) as PageResponseDto<T[]> & { total?: number };
  if (!res.ok) throw new ApiError(res.status, json.code, json.message);
  const total = Number(res.headers.get('X-Total-Count') ?? json.total ?? json.result.length);
  return { items: json.result, page: json.page, size: json.size, total };
}

export const api = {
  get: <T>(path: string, query?: Query) => request<T>('GET', path, { query }),
  getPage: <T>(path: string, query?: Query) => requestPage<T>(path, query),
  post: <T>(path: string, body?: unknown) => request<T>('POST', path, { body }),
  put: <T>(path: string, body?: unknown) => request<T>('PUT', path, { body }),
  patch: <T>(path: string, body?: unknown) => request<T>('PATCH', path, { body }),
  delete: <T>(path: string, body?: unknown) => request<T>('DELETE', path, { body }),
};
