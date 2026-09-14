import type { ErrorReport } from '@/features/error-reports/types';
import { accounts, devices, rand } from './db';

const DAY = 86_400_000;
const now = Date.now();
const iso = (ms: number) => new Date(ms).toISOString();
const pick = <T,>(arr: T[]) => arr[Math.floor(rand() * arr.length)];

const TITLES = [
  '앱이 실행 직후 꺼져요',
  '로그인 후 화면이 하얗게 나옵니다',
  '푸시 알림이 오지 않아요',
  '캐릭터 화면에서 멈춤',
  '결제 후 아이템이 반영되지 않음',
  '다크 모드에서 글자가 안 보여요',
  '업데이트 후 로그인 불가',
];
const BODIES = [
  '앱을 켜면 로고가 잠깐 나오고 바로 종료됩니다. 재설치해도 같아요.',
  '카카오 로그인 성공 후 흰 화면만 나오고 아무것도 안 눌립니다. 와이파이/LTE 둘 다 동일합니다.',
  '알림 설정은 켜져 있는데 며칠째 알림이 안 옵니다.',
  '캐릭터 탭 들어가면 로딩만 계속 돌아요. 다른 탭은 정상입니다.',
  '결제는 완료됐다고 나오는데 아이템이 안 들어왔습니다. 주문번호 첨부합니다.',
  '다크 모드로 바꾸면 설정 화면 글자가 배경색이랑 같아서 안 보여요.',
  '오늘 업데이트하고 나서 로그인 버튼 눌러도 반응이 없습니다.',
];

export const errorReports: ErrorReport[] = Array.from({ length: 23 }, (_, i): ErrorReport => {
  const id = i + 1;
  const linked = devices.filter((d) => d.accountId);
  const d = pick(linked);
  const a = accounts.find((x) => x.accountId === d.accountId)!;
  const created = now - Math.floor(rand() * 20) * DAY - Math.floor(rand() * DAY);
  const ti = Math.floor(rand() * TITLES.length);
  const answered = rand() < 0.4;
  return {
    reportId: id,
    accountId: a.accountId,
    email: a.email,
    name: a.name,
    deviceId: d.deviceId,
    deviceName: d.deviceName,
    appPackageName: 'com.monglife.app',
    buildVersion: pick(['1.2.0', '1.2.1', '1.3.0']),
    title: TITLES[ti],
    content: BODIES[ti],
    status: answered ? 'ANSWERED' : 'OPEN',
    createdAt: iso(created),
    reply: answered
      ? {
          content: '안녕하세요, MongLife 입니다.\n\n불편을 드려 죄송합니다. 해당 증상은 다음 버전(1.3.1)에서 수정될 예정입니다. 업데이트 후에도 같은 문제가 있으면 다시 알려주세요.\n\n감사합니다.',
          sentTo: a.email,
          createdAt: iso(created + Math.floor(rand() * 2 * DAY) + 3_600_000),
        }
      : null,
  };
}).sort((a, b) => b.createdAt.localeCompare(a.createdAt));
