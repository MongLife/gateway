import { useState } from 'react';
import type { AppVersion } from '../types';
import { Badge, Button, Dialog, Switch } from '@/shared/ui';
import { ConfirmDialog } from '@/shared/components/ConfirmDialog';
import { formatDateTime } from '@/shared/lib/format';

interface AppVersionDetailDialogProps {
  version: AppVersion | null;
  onClose: () => void;
  onToggle: (appVersionId: number, mustUpdate: boolean) => Promise<unknown>;
  onDelete: (appVersionId: number) => Promise<unknown>;
}

export function AppVersionDetailDialog({ version, onClose, onToggle, onDelete }: AppVersionDetailDialogProps) {
  const [confirm, setConfirm] = useState(false);
  const [busy, setBusy] = useState(false);

  return (
    <>
      <Dialog
        open={!!version}
        onClose={onClose}
        title="앱 버전 상세"
        description={version ? `${version.appPackageName} · ${version.buildVersion}` : undefined}
        footer={<Button variant="danger" onClick={() => setConfirm(true)}>삭제</Button>}
      >
        {version && (
          <dl className="space-y-3 text-sm">
            <Row label="ID">{version.appVersionId}</Row>
            <Row label="앱">{version.appPackageName}</Row>
            <Row label="버전"><span className="font-mono">{version.buildVersion}</span></Row>
            <Row label="강제 업데이트">
              <span className="inline-flex items-center gap-2">
                {version.mustUpdate ? <Badge tone="danger">ON</Badge> : <Badge>OFF</Badge>}
                <Switch
                  checked={version.mustUpdate}
                  disabled={busy}
                  label="강제 업데이트"
                  onCheckedChange={async (v) => {
                    setBusy(true);
                    try {
                      await onToggle(version.appVersionId, v);
                    } finally {
                      setBusy(false);
                    }
                  }}
                />
              </span>
            </Row>
            <Row label="등록일">{formatDateTime(version.createdAt)}</Row>
            <Row label="수정일">{formatDateTime(version.updatedAt)}</Row>
          </dl>
        )}
      </Dialog>
      <ConfirmDialog
        open={confirm}
        danger
        title="버전을 삭제할까요?"
        description={version ? `${version.appPackageName} ${version.buildVersion} — 이 버전의 강제 업데이트 판정이 사라집니다.` : undefined}
        confirmLabel="삭제"
        loading={busy}
        onClose={() => setConfirm(false)}
        onConfirm={async () => {
          if (!version) return;
          setBusy(true);
          try {
            await onDelete(version.appVersionId);
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
