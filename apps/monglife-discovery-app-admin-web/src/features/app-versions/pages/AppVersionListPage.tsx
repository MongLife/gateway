import { useMemo, useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Plus } from 'lucide-react';
import { useAppVersions, useCreateAppVersion, useRemoveAppVersion, useSetMustUpdate } from '../queries';
import { AppVersionDetailDialog } from '../components/AppVersionDetailDialog';
import type { AppVersion } from '../types';
import { DataTable, type Column } from '@/shared/components/DataTable';
import { Badge, Button, Card, CardHeader, CardTitle, Dialog, Field, Input, PageHeader, Switch } from '@/shared/ui';
import { formatDateTime } from '@/shared/lib/format';
import { FilterSelect } from '@/shared/components/FilterSelect';
import type { SortState } from '@/shared/api/types';

const schema = z.object({
  appPackageName: z.string().min(1, '패키지명을 입력하세요.'),
  buildVersion: z.string().regex(/^\d+(\.\d+)*$/, '예: 1.2.3'),
});
type FormValues = z.infer<typeof schema>;

export function AppVersionListPage() {
  const { data, isLoading } = useAppVersions();
  const create = useCreateAppVersion();
  const setMustUpdate = useSetMustUpdate();
  const remove = useRemoveAppVersion();
  const [open, setOpen] = useState(false);
  const [detail, setDetail] = useState<AppVersion | null>(null);
  const [pkg, setPkg] = useState('');
  const [ver, setVer] = useState('');
  const [must, setMust] = useState('');
  const [sort, setSort] = useState<SortState | null>(null);

  const packages = useMemo(() => Array.from(new Set((data ?? []).map((v) => v.appPackageName))).sort(), [data]);
  const versions = useMemo(
    () => Array.from(new Set((data ?? []).map((v) => v.buildVersion))).sort((a, b) => compareVersion(b, a)),
    [data],
  );
  // 목록이 페이징 없이 통째로 오므로 필터·정렬은 클라이언트에서 한다
  const rows = useMemo(() => {
    const filtered = (data ?? []).filter(
      (v) => (!pkg || v.appPackageName === pkg) && (!ver || v.buildVersion === ver) && (!must || (must === 'ON') === v.mustUpdate),
    );
    if (!sort) return filtered.sort((a, b) => a.appPackageName.localeCompare(b.appPackageName) || compareVersion(b.buildVersion, a.buildVersion));
    const sign = sort.dir === 'desc' ? -1 : 1;
    const key = sort.key as 'createdAt' | 'updatedAt';
    return filtered.sort((a, b) => ((a[key] ?? '') < (b[key] ?? '') ? -1 : (a[key] ?? '') > (b[key] ?? '') ? 1 : 0) * sign);
  }, [data, pkg, ver, must, sort]);

  const form = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: { appPackageName: packages[0] ?? '', buildVersion: '' },
  });

  const openCreate = () => {
    // defaultValues 는 마운트 시점(목록 로드 전)에 잡히므로 열 때 패키지를 채운다
    if (!form.getValues('appPackageName') && packages[0]) form.setValue('appPackageName', packages[0]);
    setOpen(true);
  };

  const columns: Column<AppVersion>[] = [
    { key: 'pkg', header: '앱', cell: (v) => v.appPackageName },
    { key: 'ver', header: '버전', cell: (v) => <span className="font-mono">{v.buildVersion}</span> },
    { key: 'flag', header: '강제 업데이트', cell: (v) => (v.mustUpdate ? <Badge tone="danger">ON</Badge> : <Badge>OFF</Badge>) },
    { key: 'created', header: '등록일', sortKey: 'createdAt', cell: (v) => formatDateTime(v.createdAt), className: 'text-muted-foreground' },
    { key: 'updated', header: '수정일', sortKey: 'updatedAt', cell: (v) => formatDateTime(v.updatedAt), className: 'text-muted-foreground' },
    {
      key: 'toggle',
      header: '',
      className: 'text-right',
      cell: (v) => (
        <span onClick={(e) => e.stopPropagation()}>
          <Switch checked={v.mustUpdate} disabled={setMustUpdate.isPending} onCheckedChange={(mustUpdate) => setMustUpdate.mutate({ appVersionId: v.appVersionId, mustUpdate })} label="강제 업데이트" />
        </span>
      ),
    },
  ];

  const onSubmit = form.handleSubmit((values) =>
    create.mutate({ ...values, mustUpdate: false }, {
      onSuccess: () => {
        setOpen(false);
        form.reset({ appPackageName: values.appPackageName, buildVersion: '' });
      },
    }),
  );

  return (
    <>
      <PageHeader
        title="앱 버전"
        description="버전별 강제 업데이트 플래그. ON 이면 해당 버전 이하 클라이언트는 NeedUpdateApp(406) 을 받습니다. 행을 누르면 상세"
        actions={<Button className="w-full sm:w-auto" onClick={openCreate}><Plus className="size-4" /> 버전 등록</Button>}
      />
      <Card>
        <CardHeader className="flex-col items-stretch sm:flex-row sm:items-center">
          <CardTitle>등록된 버전 {rows.length}개</CardTitle>
          <div className="flex flex-col gap-2 sm:flex-row">
            <FilterSelect label="앱" value={pkg} onChange={setPkg} options={packages} className="w-full sm:w-64" />
            <FilterSelect label="버전" value={ver} onChange={setVer} options={versions} className="w-full sm:w-32" />
            <FilterSelect label="강제 업데이트" value={must} onChange={setMust} options={['ON', 'OFF']} className="w-full sm:w-40" />
          </div>
        </CardHeader>
        <DataTable columns={columns} rows={rows} rowKey={(v) => v.appVersionId} loading={isLoading} sort={sort} onSortChange={setSort} onRowClick={setDetail} />
      </Card>

      <AppVersionDetailDialog
        version={detail ? (data?.find((v) => v.appVersionId === detail.appVersionId) ?? detail) : null}
        onClose={() => setDetail(null)}
        onToggle={(appVersionId, mustUpdate) => setMustUpdate.mutateAsync({ appVersionId, mustUpdate })}
        onDelete={(id) => remove.mutateAsync(id)}
      />

      <Dialog
        open={open}
        onClose={() => setOpen(false)}
        title="새 버전 등록"
        footer={
          <>
            <Button variant="secondary" onClick={() => setOpen(false)}>취소</Button>
            <Button type="submit" form="app-version-form" loading={create.isPending}>등록</Button>
          </>
        }
      >
        <form id="app-version-form" onSubmit={onSubmit} className="space-y-4">
          <Field label="앱 (패키지명)" error={form.formState.errors.appPackageName?.message}>
            <Input list="pkg-list" placeholder="com.monglife.app" {...form.register('appPackageName')} />
            <datalist id="pkg-list">{packages.map((p) => <option key={p} value={p} />)}</datalist>
          </Field>
          <Field label="버전" error={form.formState.errors.buildVersion?.message}>
            <Input placeholder="1.2.3" {...form.register('buildVersion')} />
          </Field>
          {create.error && <p className="text-xs text-danger">{create.error.message}</p>}
        </form>
      </Dialog>
    </>
  );
}

function compareVersion(a: string, b: string) {
  const pa = a.split('.').map(Number);
  const pb = b.split('.').map(Number);
  for (let i = 0; i < Math.max(pa.length, pb.length); i++) {
    const d = (pa[i] ?? 0) - (pb[i] ?? 0);
    if (d !== 0) return d;
  }
  return 0;
}
