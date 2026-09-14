/** 사용자 오류 신고. 백엔드에 아직 없는 도메인 — 목 계약 */
export type ErrorReportStatus = 'OPEN' | 'ANSWERED';

export interface ErrorReportReply {
  replyId: number;
  content: string;
  /** 답변 이메일 발송 대상 */
  sentTo: string;
  createdAt: string;
}

export interface ErrorReport {
  reportId: number;
  accountId: number;
  email: string;
  name: string;
  deviceId: string;
  deviceName: string;
  appPackageName: string;
  buildVersion: string;
  title: string;
  content: string;
  status: ErrorReportStatus;
  createdAt: string;
  replies: ErrorReportReply[];
}

export interface ErrorReportSummary extends Omit<ErrorReport, 'replies' | 'content'> {
  replyCount: number;
}
