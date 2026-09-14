import { useState } from 'react';
import { Link } from 'react-router-dom';
import { useActiveSessions, useRevokeToken } from '../queries';
import type { ActiveSession } from '../types';
import { TokenDetailDialog } from '../components/TokenDetailDialog';
import { useFilterOptions } from '@/features/meta/queries';
import { DataTable, type Column } from '@/shared/components/DataTable';
import { ConfirmDialog } from '@/shared/components/ConfirmDialog';
import { FilterSelect } from '@/shared/components/FilterSelect';
import { Button, Card, PageHeader, Pagination } from '@/shared/ui';
import { formatDateTime, formatDuration } from '@/shared/lib/format';
import { toSortParam, type SortState } from '@/shared/api/types';

const SIZE = 15;

export function ActiveSessionsPage() {
  const [page, setPage] = useState(0);
  const [deviceName, setDeviceName] = useState('');
  const [app, setApp] = useState('');
  const [ver, setVer] = useState('');
  const [sort, setSort] = useState<SortState>({ key: 'createdAt', dir: 'desc' });
  const [target, setTarget] = useState<ActiveSession | null>(null);
  const [detail, setDetail] = useState<ActiveSession | null>(null);
  const { data, isLoading, isFetching, refetch } = useActiveSessions({
    page,
    size: SIZE,
    deviceName: deviceName || undefined,
    appPackageName: app || undefined,
    buildVersion: ver || undefined,
    sort: toSortParam(sort),
  });
  const options = useFilterOptions();
  const revoke = useRevokeToken();
  const reset = <T,>(set: (v: T) => void) => (v: T) => { set(v); setPage(0); };

  const columns: Column<ActiveSession>[] = [
    { key: 'account', header: '계정', cell: (s) => <Link className="text-primary hover:underline" to={`/accounts/${s.accountId}`} onClick={(e) => e.stopPropagation()}>{s.email ?? `#${s.accountId}`}</Link> },
    { key: 'name', header: '이름', cell: (s) => s.name ?? '-' },
    { key: 'device', header: '기기명', cell: (s) => <span title={s.deviceId}>{s.deviceName ?? s.deviceId}</span> },
    { key: 'app', header: '앱', cell: (s) => <span className="text-muted-foreground">{s.appPackageName}</span> },
    { key: 'ver', header: '버전', cell: (s) => s.buildVersion },
    { key: 'created', header: '로그인', sortKey: 'createdAt', cell: (s) => formatDateTime(s.createdAt) },
    { key: 'ttl', header: '만료까지', cell: (s) => formatDuration(s.expiration) },
    {
      key: 'act',
      header: '',
      className: 'text-right',
      cell: (s) => (
        <Button
          variant="danger"
          size="sm"
          onClick={(e) => {
            e.stopPropagation();
            setTarget(s);
          }}
        >
          로그아웃
        </Button>
      ),
    },
  ];

  return (
    <>
      <PageHeader
        title="로그인 현황"
        description="유효한 리프레시 토큰 기준. 30초마다 갱신. 행을 누르면 토큰 상세"
        actions={
          <div className="flex w-full flex-col gap-2 sm:w-auto sm:flex-row sm:items-center">
            <FilterSelect label="기기명" value={deviceName} onChange={reset(setDeviceName)} options={options.data?.deviceNames ?? []} className="w-full sm:w-40" />
            <FilterSelect label="앱" value={app} onChange={reset(setApp)} options={options.data?.appPackageNames ?? []} className="w-full sm:w-64" />
            <FilterSelect label="버전" value={ver} onChange={reset(setVer)} options={options.data?.buildVersions ?? []} className="w-full sm:w-32" />
            <Button variant="secondary" size="sm" className="w-full sm:w-auto" loading={isFetching} onClick={() => void refetch()}>새로고침</Button>
          </div>
        }
      />
      <Card>
        <DataTable columns={columns} rows={data?.items ?? []} rowKey={(s) => s.refreshToken} loading={isLoading} sort={sort} onSortChange={reset(setSort)} onRowClick={setDetail} emptyMessage="로그인한 사용자가 없습니다." />
        {data && <Pagination page={data.page} size={data.size} total={data.total} onPageChange={setPage} />}
      </Card>
      <TokenDetailDialog token={detail} onClose={() => setDetail(null)} onRevoke={(rt) => revoke.mutateAsync(rt)} />
      <ConfirmDialog
        open={!!target}
        danger
        title="로그아웃 처리할까요?"
        description={target ? `${target.email ?? `#${target.accountId}`} · ${target.deviceName ?? target.deviceId} — 토큰이 만료되어 다시 로그인해야 합니다.` : undefined}
        confirmLabel="로그아웃"
        loading={revoke.isPending}
        onClose={() => setTarget(null)}
        onConfirm={() => target && revoke.mutate(target.refreshToken, { onSuccess: () => setTarget(null) })}
      />
    </>
  );
}
