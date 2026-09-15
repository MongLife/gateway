import { useState } from 'react';
import { Link } from 'react-router-dom';
import type { Device } from '../types';
import { Badge, Button, Dialog } from '@/shared/ui';
import { CopyButton } from '@/shared/components/CopyButton';
import { ConfirmDialog } from '@/shared/components/ConfirmDialog';
import { formatDateTime } from '@/shared/lib/format';

interface DeviceDetailDialogProps {
  device: Device | null;
  onClose: () => void;
  /** 주면 삭제 버튼이 노출된다 (기기 페이지에서만) */
  onDelete?: (deviceId: string) => Promise<unknown>;
  /** 주면 연결 해제 버튼이 노출된다 */
  onDisconnect?: (deviceId: string) => Promise<unknown>;
}

export function DeviceDetailDialog({ device, onClose, onDelete, onDisconnect }: DeviceDetailDialogProps) {
  const [confirm, setConfirm] = useState<'delete' | 'disconnect' | null>(null);
  const [busy, setBusy] = useState(false);

  const run = async (fn: (id: string) => Promise<unknown>) => {
    if (!device) return;
    setBusy(true);
    try {
      await fn(device.deviceId);
      setConfirm(null);
      onClose();
    } finally {
      setBusy(false);
    }
  };

  return (
    <>
      <Dialog
        open={!!device}
        onClose={onClose}
        title="기기 상세"
        description={device?.deviceName}
        size="lg"
        footer={
          (onDisconnect && device?.accountId) || onDelete ? (
            <>
              {onDisconnect && device?.accountId && (
                <Button variant="secondary" onClick={() => setConfirm('disconnect')}>연결 해제</Button>
              )}
              {onDelete && <Button variant="danger" onClick={() => setConfirm('delete')}>기기 삭제</Button>}
            </>
          ) : undefined
        }
      >
        {device && (
          <dl className="space-y-3 text-sm">
            <Row label="기기 ID">
              <span className="font-mono text-xs">{device.deviceId}</span>
            </Row>
            <Row label="기기명">{device.deviceName}</Row>
            <Row label="연결 계정">
              {device.accountId ? (
                <Link className="text-primary hover:underline" to={`/accounts/${device.accountId}`} onClick={onClose}>
                  {device.accountEmail ?? `#${device.accountId}`}
                </Link>
              ) : (
                <span className="text-muted-foreground">미연결</span>
              )}
            </Row>
            <Row label="등록일">{formatDateTime(device.createdAt)}</Row>
            <div>
              <dt className="mb-1.5 flex items-center gap-2 text-muted-foreground">
                FCM 토큰 {device.fcmToken ? <Badge tone="success">등록</Badge> : <Badge>없음</Badge>}
              </dt>
              <dd className="flex items-start gap-1">
                <code className="block max-h-24 min-w-0 flex-1 overflow-auto rounded-md bg-surface-muted p-2 font-mono text-xs break-all">
                  {device.fcmToken ?? '-'}
                </code>
                {device.fcmToken && <CopyButton value={device.fcmToken} />}
              </dd>
            </div>
          </dl>
        )}
      </Dialog>

      <ConfirmDialog
        open={confirm === 'delete'}
        danger
        title="기기를 삭제할까요?"
        description={`${device?.deviceId} — 이 기기의 토큰도 함께 사라집니다.`}
        confirmLabel="삭제"
        loading={busy}
        onClose={() => setConfirm(null)}
        onConfirm={() => onDelete && void run(onDelete)}
      />
      <ConfirmDialog
        open={confirm === 'disconnect'}
        title="계정 연결을 해제할까요?"
        description={`${device?.deviceId} ↔ 계정 #${device?.accountId}`}
        confirmLabel="해제"
        loading={busy}
        onClose={() => setConfirm(null)}
        onConfirm={() => onDisconnect && void run(onDisconnect)}
      />
    </>
  );
}

function Row({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <div className="flex items-center justify-between gap-4">
      <dt className="shrink-0 text-muted-foreground">{label}</dt>
      <dd className="flex min-w-0 items-center gap-1 break-all text-right">{children}</dd>
    </div>
  );
}
