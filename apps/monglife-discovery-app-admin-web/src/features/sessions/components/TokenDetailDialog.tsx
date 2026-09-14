import { useState } from 'react';
import { Link } from 'react-router-dom';
import type { ActiveSession, Token } from '../types';
import { Button, Dialog } from '@/shared/ui';
import { CopyButton } from '@/shared/components/CopyButton';
import { ConfirmDialog } from '@/shared/components/ConfirmDialog';
import { formatDateTime, formatDuration } from '@/shared/lib/format';

interface TokenDetailDialogProps {
  /** ActiveSession 을 주면 이메일·기기명이 함께 표시된다 */
  token: Token | ActiveSession | null;
  onClose: () => void;
  onRevoke: (refreshToken: string) => Promise<unknown>;
}

export function TokenDetailDialog({ token, onClose, onRevoke }: TokenDetailDialogProps) {
  const [confirm, setConfirm] = useState(false);
  const [busy, setBusy] = useState(false);

  return (
    <>
      <Dialog
        open={!!token}
        onClose={onClose}
        title="토큰 상세"
        description={token ? (('email' in token && token.email) || `계정 #${token.accountId}`) : undefined}
        size="lg"
        footer={<Button variant="danger" onClick={() => setConfirm(true)}>로그아웃</Button>}
      >
        {token && (
          <dl className="space-y-3 text-sm">
            <Row label="계정">
              <Link className="text-primary hover:underline" to={`/accounts/${token.accountId}`} onClick={onClose}>
                {('email' in token && token.email) || `#${token.accountId}`}
              </Link>
            </Row>
            {'deviceName' in token && token.deviceName && <Row label="기기">{token.deviceName}</Row>}
            <Row label="기기 ID"><span className="font-mono text-xs">{token.deviceId}</span></Row>
            <Row label="앱"><span className="text-muted-foreground">{token.appPackageName}</span></Row>
            <Row label="버전">{token.buildVersion}</Row>
            <Row label="발급">{formatDateTime(token.createdAt)}</Row>
            <Row label="남은 시간">{formatDuration(token.expiration)}</Row>
            <TokenBlock label="Access token" value={token.accessToken} />
            <TokenBlock label="Refresh token" value={token.refreshToken} />
          </dl>
        )}
      </Dialog>
      <ConfirmDialog
        open={confirm}
        danger
        title="로그아웃 처리할까요?"
        description={token ? `${('email' in token && token.email) || `#${token.accountId}`} · ${('deviceName' in token && token.deviceName) || token.deviceId} — 토큰이 만료되어 다시 로그인해야 합니다.` : undefined}
        confirmLabel="로그아웃"
        loading={busy}
        onClose={() => setConfirm(false)}
        onConfirm={async () => {
          if (!token) return;
          setBusy(true);
          try {
            await onRevoke(token.refreshToken);
            setConfirm(false);
            onClose();
          } finally {
            setBusy(false);
          }
        }}
      />
    </>
  );
}

function Row({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <div className="flex items-center justify-between gap-4">
      <dt className="shrink-0 text-muted-foreground">{label}</dt>
      <dd className="min-w-0 text-right">{children}</dd>
    </div>
  );
}

function TokenBlock({ label, value }: { label: string; value: string }) {
  return (
    <div>
      <dt className="mb-1.5 text-muted-foreground">{label}</dt>
      <dd className="flex items-start gap-1">
        <code className="block min-w-0 flex-1 rounded-md bg-surface-muted p-2 font-mono text-xs break-all">{value}</code>
        <CopyButton value={value} />
      </dd>
    </div>
  );
}
