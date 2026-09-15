/** domains/monglife-discovery-domain-account AccountEntity */
export type Role = 'ADMIN' | 'NORMAL';
/** 소셜 로그인 플랫폼. 백엔드 AccountEntity 에는 아직 없는 필드 */
export type Platform = 'google' | 'apple' | 'kakao';

export interface Account {
  accountId: number;
  socialAccountId?: string | null;
  platform?: Platform | null;
  email: string;
  name: string;
  role: Role;
  isDeleted: boolean;
  createdAt: string;
  updatedAt: string;
}

/** LoginHistoryEntity */
export interface LoginHistory {
  accountLogId: number;
  accountId: number;
  deviceId: string;
  appPackageName: string;
  deviceName: string;
  buildVersion: string;
  loginAt: string;
  loginCount: number;
}

export interface AccountPatch {
  name?: string;
  role?: Role;
  isDeleted?: boolean;
}
