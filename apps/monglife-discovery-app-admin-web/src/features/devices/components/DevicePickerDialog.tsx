import { useState } from 'react';
import { useDevices } from '../queries';
import type { Device } from '../types';
import { Button, Dialog, EmptyState } from '@/shared/ui';
import { SearchInput } from '@/shared/components/SearchInput';
import { formatDateTime } from '@/shared/lib/format';
import { cn } from '@/shared/lib/cn';

interface DevicePickerDialogProps {
  open: boolean;
  onClose: () => void;
  onPick: (device: Device) => Promise<unknown>;
}

/** 미연결 기기를 검색해 하나 고른다 */
export function DevicePickerDialog({ open, onClose, onPick }: DevicePickerDialogProps) {
  const [query, setQuery] = useState('');
  const [selected, setSelected] = useState<Device | null>(null);
  const [busy, setBusy] = useState(false);
  const { data, isLoading } = useDevices({ page: 0, size: 10, query, unmappedOnly: true });

  const close = () => {
    setSelected(null);
    setQuery('');
    onClose();
  };

  return (
    <Dialog
      open={open}
      onClose={close}
      title="기기 연결"
      description="계정에 연결되지 않은 기기만 표시됩니다"
      size="lg"
      footer={
        <>
          <Button variant="secondary" onClick={close}>취소</Button>
          <Button
            disabled={!selected}
            loading={busy}
            onClick={async () => {
              if (!selected) return;
              setBusy(true);
              try {
                await onPick(selected);
                close();
              } finally {
                setBusy(false);
              }
            }}
          >
            연결
          </Button>
        </>
      }
    >
      <SearchInput value={query} onChange={setQuery} placeholder="기기 ID / 기기명" className="w-full" />
      <div className="mt-3 max-h-80 overflow-y-auto rounded-md border">
        {isLoading ? (
          <div className="space-y-2 p-3">{Array.from({ length: 4 }).map((_, i) => <div key={i} className="h-9 animate-pulse rounded bg-surface-muted" />)}</div>
        ) : !data || data.items.length === 0 ? (
          <EmptyState message="미연결 기기가 없습니다." />
        ) : (
          <ul className="divide-y">
            {data.items.map((d) => (
              <li key={d.deviceId}>
                <label className={cn('flex cursor-pointer items-center gap-3 px-3 py-2 text-sm hover:bg-surface-muted', selected?.deviceId === d.deviceId && 'bg-primary-soft')}>
                  <input type="radio" name="device" className="accent-primary" checked={selected?.deviceId === d.deviceId} onChange={() => setSelected(d)} />
                  <span className="min-w-0 flex-1">
                    <span className="block truncate font-mono text-xs">{d.deviceId}</span>
                    <span className="text-muted-foreground">{d.deviceName}</span>
                  </span>
                  <span className="hidden shrink-0 text-xs text-muted-foreground sm:block">{formatDateTime(d.createdAt)}</span>
                </label>
              </li>
            ))}
          </ul>
        )}
      </div>
      {data && data.total > data.items.length && (
        <p className="mt-2 text-xs text-muted-foreground">{data.total}건 중 {data.items.length}건 표시 — 검색어로 좁히세요.</p>
      )}
    </Dialog>
  );
}
