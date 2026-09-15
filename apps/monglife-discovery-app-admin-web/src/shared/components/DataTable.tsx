import type { ReactNode } from 'react';
import { ArrowDown, ArrowUp, ArrowUpDown } from 'lucide-react';
import { EmptyState, Select, Table, TBody, TD, TH, THead, TR } from '@/shared/ui';
import { cn } from '@/shared/lib/cn';
import type { SortState } from '@/shared/api/types';

export interface Column<T> {
  key: string;
  header: ReactNode;
  cell: (row: T) => ReactNode;
  className?: string;
  /** 주면 헤더 클릭으로 정렬. 값은 서버(or 클라이언트) 정렬 키 */
  sortKey?: string;
  /** 모바일 카드에서 라벨 없이 그릴 때 정렬 위치. 헤더가 문자열이 아니거나 비어 있으면 라벨 없음 (기본 end) */
  mobileAlign?: 'start' | 'end';
}

interface DataTableProps<T> {
  columns: Column<T>[];
  rows: T[];
  rowKey: (row: T) => string | number;
  loading?: boolean;
  emptyMessage?: string;
  onRowClick?: (row: T) => void;
  sort?: SortState | null;
  onSortChange?: (sort: SortState) => void;
}

export function DataTable<T>({ columns, rows, rowKey, loading, emptyMessage, onRowClick, sort, onSortChange }: DataTableProps<T>) {
  const toggleSort = (key: string) => {
    if (!onSortChange) return;
    onSortChange({ key, dir: sort?.key === key && sort.dir === 'desc' ? 'asc' : 'desc' });
  };

  const head = (
    <THead>
      <tr>
        {columns.map((c) => (
          <TH key={c.key} className={c.className}>
            {c.sortKey && onSortChange ? (
              <button
                type="button"
                onClick={() => toggleSort(c.sortKey!)}
                className={cn('inline-flex items-center gap-1 hover:text-foreground', sort?.key === c.sortKey && 'text-primary')}
              >
                {c.header}
                {sort?.key === c.sortKey ? (
                  sort.dir === 'desc' ? <ArrowDown className="size-3.5" /> : <ArrowUp className="size-3.5" />
                ) : (
                  <ArrowUpDown className="size-3.5 opacity-40" />
                )}
              </button>
            ) : (
              c.header
            )}
          </TH>
        ))}
      </tr>
    </THead>
  );

  const sortable = columns.filter((c) => c.sortKey);
  // 모바일: 헤더 대신 정렬 셀렉트
  const mobileSort = onSortChange && sortable.length > 0 && (
    <div className="flex justify-end border-b px-4 py-2 sm:hidden">
      <Select
        aria-label="정렬"
        className="h-8 w-auto text-xs"
        value={sort ? `${sort.key}:${sort.dir}` : ''}
        onChange={(e) => {
          const [key, dir] = e.target.value.split(':');
          onSortChange({ key, dir: dir as SortState['dir'] });
        }}
      >
        {sortable.flatMap((c) => [
          <option key={`${c.sortKey}:desc`} value={`${c.sortKey}:desc`}>{c.header} ↓</option>,
          <option key={`${c.sortKey}:asc`} value={`${c.sortKey}:asc`}>{c.header} ↑</option>,
        ])}
      </Select>
    </div>
  );

  if (loading) {
    return (
      <div className="space-y-2 p-4">
        {Array.from({ length: 5 }).map((_, i) => (
          <div key={i} className="h-8 animate-pulse rounded bg-surface-muted" />
        ))}
      </div>
    );
  }
  if (rows.length === 0) {
    return (
      <>
        <div className="hidden sm:block"><Table>{head}</Table></div>
        <EmptyState message={emptyMessage} />
      </>
    );
  }
  return (
    <>
      {/* 모바일: 행마다 카드로 세로 나열. 좌우 스크롤 없음 */}
      <div className="sm:hidden">
        {mobileSort}
        <ul className="divide-y">
          {rows.map((row) => (
            <li
              key={rowKey(row)}
              className={cn('space-y-1.5 px-4 py-3 text-sm', onRowClick && 'cursor-pointer active:bg-surface-muted/60')}
              onClick={onRowClick ? () => onRowClick(row) : undefined}
            >
              {columns.map((c) => {
                const value = c.cell(row);
                if (value === null || value === undefined || value === '') return null;
                const hasHeader = typeof c.header === 'string' && c.header !== '';
                return (
                  <div key={c.key} className={cn('flex items-start gap-3', !hasHeader && (c.mobileAlign === 'start' ? 'justify-start' : 'justify-end'))}>
                    {hasHeader && <span className="w-20 shrink-0 text-xs text-muted-foreground">{c.header}</span>}
                    <span className={cn('min-w-0 flex-1 break-all', hasHeader ? 'text-right' : 'flex-none')}>{value}</span>
                  </div>
                );
              })}
            </li>
          ))}
        </ul>
      </div>
      <div className="hidden sm:block">
        <Table>
          {head}
          <TBody>
            {rows.map((row) => (
              <TR key={rowKey(row)} className={cn(onRowClick && 'cursor-pointer')} onClick={onRowClick ? () => onRowClick(row) : undefined}>
                {columns.map((c) => (
                  <TD key={c.key} className={c.className}>
                    {c.cell(row)}
                  </TD>
                ))}
              </TR>
            ))}
          </TBody>
        </Table>
      </div>
    </>
  );
}
