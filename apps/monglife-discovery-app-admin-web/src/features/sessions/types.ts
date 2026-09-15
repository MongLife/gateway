/** domains/monglife-discovery-domain-account TokenEntity (Redis @RedisHash monglife_token) */
export interface Token {
  refreshToken: string;
  accessToken: string;
  deviceId: string;
  accountId: number;
  appPackageName: string;
  buildVersion: string;
  createdAt: string;
  /** TTL (초) */
  expiration: number;
}

/** 로그인 현황 = 토큰 + 계정 요약 */
export interface ActiveSession extends Token {
  email?: string;
  name?: string;
  deviceName?: string;
}
