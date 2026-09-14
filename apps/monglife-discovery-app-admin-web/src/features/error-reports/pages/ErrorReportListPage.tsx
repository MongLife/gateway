import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useErrorReports } from '../queries';
import type { ErrorReportStatus, ErrorReportSummary } from '../types';
import { DataTable, type Column } from '@/shared/components/DataTable';
import { SearchInput } from '@/shared/components/SearchInput';
import { Badge, Card, PageHeader, Pagination } from '@/shared/ui';
import { FilterSelect } from '@/shared/components/FilterSelect';
import { useFilterOptions } from '@/features/meta/queries';
import { toSortParam, type SortState } from '@/shared/api/types';
import { formatDateTime } from '@/shared/lib/format';

const SIZE = 15;

export function StatusBadge({ status }: { status: ErrorReportStatus }) {
  return status === 'ANSWERED' ? <Badge tone="success">답변 완료</Badge> : <Badge tone="warning">미답변</Badge>;
}

export function ErrorReportListPage() {
  const [page, setPage] = useState(0);
  const [query, setQuery] = useState('');
  const [status, setStatus] = useState('');
  const [deviceName, setDeviceName] = useState('');
  const [app, setApp] = useState('');
  const [ver, setVer] = useState('');
  const [sort, setSort] = useState<SortState>({ key: 'createdAt', dir: 'desc' });
  const navigate = useNavigate();
  const { data, isLoading } = useErrorReports({
    page,
    size: SIZE,
    query,
    status: (status || undefined) as ErrorReportStatus | undefined,
    deviceName: deviceName || undefined,
    appPackageName: app || undefined,
    buildVersion: ver || undefined,
    sort: toSortParam(sort),
  });
  const options = useFilterOptions();
  const reset = <T,>(set: (v: T) => void) => (v: T) => { set(v); setPage(0); };

  const columns: Column<ErrorReportSummary>[] = [
    { key: 'id', header: 'ID', sortKey: 'reportId', cell: (r) => r.reportId, className: 'w-16 tabular-nums' },
    { key: 'status', header: '상태', cell: (r) => <StatusBadge status={r.status} /> },
    { key: 'title', header: '제목', cell: (r) => <span className="font-medium">{r.title}</span>, className: 'max-w-md truncate' },
    { key: 'user', header: '신고자', cell: (r) => <span>{r.email}<span className="ml-1 text-muted-foreground">{r.name}</span></span> },
    { key: 'device', header: '기기', cell: (r) => r.deviceName },
    { key: 'app', header: '앱', cell: (r) => <span className="text-muted-foreground">{r.appPackageName}</span> },
    { key: 'ver', header: '버전', cell: (r) => r.buildVersion },
    { key: 'created', header: '신고일', sortKey: 'createdAt', cell: (r) => formatDateTime(r.createdAt), className: 'text-muted-foreground' },
  ];

  return (
    <>
      <PageHeader
        title="오류 신고"
        description="사용자가 앱에서 보낸 오류 신고. 행을 누르면 상세·답변"
        actions={
          <div className="flex w-full flex-col gap-2 sm:w-auto sm:flex-row sm:items-center">
            <FilterSelect label="상태" value={status} onChange={reset(setStatus)} options={[{ value: 'OPEN', label: '미답변' }, { value: 'ANSWERED', label: '답변 완료' }]} className="w-full sm:w-32" />
            <FilterSelect label="기기" value={deviceName} onChange={reset(setDeviceName)} options={options.data?.deviceNames ?? []} className="w-full sm:w-40" />
            <FilterSelect label="앱" value={app} onChange={reset(setApp)} options={options.data?.appPackageNames ?? []} className="w-full sm:w-64" />
            <FilterSelect label="버전" value={ver} onChange={reset(setVer)} options={options.data?.buildVersions ?? []} className="w-full sm:w-32" />
            <SearchInput value={query} onChange={reset(setQuery)} placeholder="제목 / 이메일 / 이름" className="w-full sm:w-64" />
          </div>
        }
      />
      <Card>
        <DataTable columns={columns} rows={data?.items ?? []} rowKey={(r) => r.reportId} loading={isLoading} sort={sort} onSortChange={reset(setSort)} onRowClick={(r) => navigate(`/error-reports/${r.reportId}`)} emptyMessage="오류 신고가 없습니다." />
        {data && <Pagination page={data.page} size={data.size} total={data.total} onPageChange={setPage} />}
      </Card>
    </>
  );
}
