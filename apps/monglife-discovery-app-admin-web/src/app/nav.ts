import {
  BarChart3,
  Bell,
  Bug,
  LogIn,
  Package,
  Smartphone,
  Users,
  type LucideIcon,
} from 'lucide-react';

export interface NavItem {
  to: string;
  label: string;
  icon: LucideIcon;
  end?: boolean;
}

export interface NavGroup {
  title: string;
  items: NavItem[];
}

/** 기능 정의 0 ~ 4 순서 */
export const NAV: NavGroup[] = [
  {
    title: '통계',
    items: [{ to: '/', label: '대시보드', icon: BarChart3, end: true }],
  },
  {
    title: '사용자 관리',
    items: [
      { to: '/accounts', label: '계정', icon: Users },
      { to: '/devices', label: '기기', icon: Smartphone },
    ],
  },
  {
    title: '로그인 관리',
    items: [{ to: '/sessions', label: '로그인 현황', icon: LogIn }],
  },
  {
    title: '앱 버전',
    items: [{ to: '/app-versions', label: '버전 관리', icon: Package }],
  },
  {
    title: '알림',
    items: [
      { to: '/notifications', label: '푸시 알림 전송', icon: Bell },
      { to: '/error-reports', label: '오류 신고', icon: Bug },
    ],
  },
];
