/**
 * 토큰 저장소. 관리자 인증 방식이 정해지기 전이라 sessionStorage 로 둔다.
 * 저장 위치를 바꿀 때 이 파일만 고친다.
 */
const ACCESS_KEY = 'monglife.admin.accessToken';
const REFRESH_KEY = 'monglife.admin.refreshToken';

function safeGet(key: string) {
  try {
    return sessionStorage.getItem(key);
  } catch {
    return null;
  }
}

export const tokenStorage = {
  getAccessToken: () => safeGet(ACCESS_KEY),
  getRefreshToken: () => safeGet(REFRESH_KEY),
  set(accessToken: string, refreshToken: string) {
    try {
      sessionStorage.setItem(ACCESS_KEY, accessToken);
      sessionStorage.setItem(REFRESH_KEY, refreshToken);
    } catch {
      /* private mode 등 */
    }
  },
  clear() {
    try {
      sessionStorage.removeItem(ACCESS_KEY);
      sessionStorage.removeItem(REFRESH_KEY);
    } catch {
      /* noop */
    }
  },
};
