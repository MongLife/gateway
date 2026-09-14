export interface UserStats {
  todayJoined: number;
  weekJoined: number;
  totalAccounts: number;
  /** 최근 30일 로그인 기록이 없는 (미탈퇴) 계정 수 */
  inactiveAccounts: number;
  activeSessions: number;
}

export interface LoginStat {
  /** YYYY-MM-DD */
  date: string;
  loginCount: number;
  uniqueAccounts: number;
}

export interface SignupStat {
  /** YYYY-MM-DD */
  date: string;
  count: number;
}
