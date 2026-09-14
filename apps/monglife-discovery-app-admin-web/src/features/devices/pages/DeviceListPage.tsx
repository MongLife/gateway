import { useState } from 'react';
import { useDevices, useDisconnectDevice, useRemoveDevice } from '../queries';
import type { Device } from '../types';
import { DeviceDetailDialog } from '../components/DeviceDetailDialog';
import { AccountDetailDialog } from '@/features/accounts/components/AccountDetailDialog';
import { useFilterOptions } from '@/features/meta/queries';
import { DataTable, type Column } from '@/shared/components/DataTable';
import { SearchInput } from '@/shared/components/SearchInput';
import { FilterSelect } from '@/shared/components/FilterSelect';
import { Badge, Card, PageHeader, Pagination } from '@/shared/ui';
import { formatDateTime } from '@/shared/lib/format';
import { toSortParam, type SortState } from '@/shared/api/types';

const SIZE = 15;

export function DeviceListPage() {
  const [page, setPage] = useState(0);
  const [query, setQuery] = useState('');
  const [fcm, setFcm] = useState('');
  const [deviceName, setDeviceName] = useState('');
  const [sort, setSort] = useState<SortState>({ key: 'createdAt', dir: 'desc' });
  const [detail, setDetail] = useState<Device | null>(null);
  const [accountId, setAccountId] = useState<number | null>(null);
  const { data, isLoading } = useDevices({
    page,
    size: SIZE,
    query,
    fcm: (fcm || undefined) as 'REGISTERED' | 'NONE' | undefined,
    deviceName: deviceName || undefined,
    sort: toSortParam(sort),
  });
  const options = useFilterOptions();
  const remove = useRemoveDevice();
  const disconnect = useDisconnectDevice();
  const reset = <T,>(set: (v: T) => void) => (v: T) => { set(v); setPage(0); };

  const columns: Column<Device>[] = [
    { key: 'id', header: '기기 ID', cell: (d) => <span className="font-mono text-xs text-primary">{d.deviceId}</span> },
    { key: 'name', header: '기기명', cell: (d) => d.deviceName },
    { key: 'fcm', header: 'FCM 토큰', cell: (d) => (d.fcmToken ? <Badge tone="success">등록</Badge> : <Badge>없음</Badge>) },
    {
      key: 'account',
      header: '연결 계정',
      cell: (d) =>
        d.accountId ? (
          <button
            className="text-primary hover:underline"
            onClick={(e) => {
              e.stopPropagation();
              setAccountId(d.accountId!);
            }}
          >
            {d.accountEmail ?? `#${d.accountId}`}
          </button>
        ) : (
          <span className="text-muted-foreground">미연결</span>
        ),
    },
    { key: 'created', header: '등록일', sortKey: 'createdAt', cell: (d) => formatDateTime(d.createdAt), className: 'text-muted-foreground' },
  ];

  return (
    <>
      <PageHeader
        title="기기"
        description="등록된 기기 목록. 행을 누르면 상세"
        actions={
          <div className="flex w-full flex-col gap-2 sm:w-auto sm:flex-row sm:items-center">
            <FilterSelect label="FCM" value={fcm} onChange={reset(setFcm)} options={[{ value: 'REGISTERED', label: '등록' }, { value: 'NONE', label: '없음' }]} className="w-full sm:w-32" />
            <FilterSelect label="기기명" value={deviceName} onChange={reset(setDeviceName)} options={options.data?.deviceNames ?? []} className="w-full sm:w-40" />
            <SearchInput value={query} onChange={reset(setQuery)} placeholder="기기 ID / 기기명 / 계정 이메일" className="w-full sm:w-64" />
          </div>
        }
      />
      <Card>
        <DataTable columns={columns} rows={data?.items ?? []} rowKey={(d) => d.deviceId} loading={isLoading} sort={sort} onSortChange={reset(setSort)} onRowClick={setDetail} />
        {data && <Pagination page={data.page} size={data.size} total={data.total} onPageChange={setPage} />}
      </Card>
      <DeviceDetailDialog device={detail} onClose={() => setDetail(null)} onDelete={(id) => remove.mutateAsync(id)} onDisconnect={(id) => disconnect.mutateAsync(id)} />
      <AccountDetailDialog accountId={accountId} onClose={() => setAccountId(null)} />
    </>
  );
}
