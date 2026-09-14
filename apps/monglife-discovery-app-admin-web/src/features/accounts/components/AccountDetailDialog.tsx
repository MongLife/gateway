import { Link } from 'react-router-dom';
import { ExternalLink } from 'lucide-react';
import { useAccount } from '../queries';
import { PlatformBadge } from './PlatformBadge';
import { Badge, Button, Dialog } from '@/shared/ui';
import { formatDateTime } from '@/shared/lib/format';

interface AccountDetailDialogProps {
  accountId: number | null;
  onClose: () => void;
}

/** 계정 요약 모달. 편집은 계정 상세 페이지에서 한다 */
export function AccountDetailDialog({ accountId, onClose }: AccountDetailDialogProps) {
  const { data: a, isLoading } = useAccount(accountId ?? NaN);

  return (
    <Dialog
      open={accountId !== null}
      onClose={onClose}
      title="계정 상세"
      description={a?.email}
      footer={
        <>
          {a && (
            <Link
              to={`/accounts/${a.accountId}`}
              onClick={onClose}
              className="mr-auto inline-flex items-center gap-1.5 self-center text-sm text-primary hover:underline"
            >
              <ExternalLink className="size-4" /> 계정 페이지로
            </Link>
          )}
          <Button variant="secondary" onClick={onClose}>닫기</Button>
        </>
      }
    >
      {isLoading ? (
        <div className="space-y-2">{Array.from({ length: 5 }).map((_, i) => <div key={i} className="h-5 animate-pulse rounded bg-surface-muted" />)}</div>
      ) : !a ? (
        <p className="text-sm text-muted-foreground">계정을 찾을 수 없습니다.</p>
      ) : (
        <dl className="space-y-3 text-sm">
          <Row label="ID">{a.accountId}</Row>
          <Row label="이름">{a.name}</Row>
          <Row label="이메일">{a.email}</Row>
          <Row label="플랫폼"><PlatformBadge platform={a.platform} /></Row>
          <Row label="소셜 ID">{a.socialAccountId ?? '-'}</Row>
          <Row label="권한"><Badge tone={a.role === 'ADMIN' ? 'primary' : 'neutral'}>{a.role}</Badge></Row>
          <Row label="상태">{a.isDeleted ? <Badge tone="danger">탈퇴</Badge> : <Badge tone="success">정상</Badge>}</Row>
          <Row label="가입일">{formatDateTime(a.createdAt)}</Row>
          <Row label="수정일">{formatDateTime(a.updatedAt)}</Row>
        </dl>
      )}
    </Dialog>
  );
}

function Row({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <div className="flex items-center justify-between gap-4">
      <dt className="shrink-0 text-muted-foreground">{label}</dt>
      <dd className="min-w-0 truncate text-right">{children}</dd>
    </div>
  );
}
