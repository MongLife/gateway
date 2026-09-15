import { ChevronLeft, ChevronRight } from 'lucide-react';
import { Button } from './Button';

interface PaginationProps {
  page: number;
  size: number;
  total: number;
  onPageChange: (page: number) => void;
}

export function Pagination({ page, size, total, onPageChange }: PaginationProps) {
  const last = Math.max(0, Math.ceil(total / size) - 1);
  return (
    <div className="flex items-center justify-between gap-3 border-t px-4 py-2.5 text-xs text-muted-foreground">
      <span>
        총 {total.toLocaleString('ko-KR')}건 · {page + 1} / {last + 1} 페이지
      </span>
      <div className="flex gap-1">
        <Button variant="secondary" size="sm" disabled={page <= 0} onClick={() => onPageChange(page - 1)}>
          <ChevronLeft className="size-4" />
        </Button>
        <Button variant="secondary" size="sm" disabled={page >= last} onClick={() => onPageChange(page + 1)}>
          <ChevronRight className="size-4" />
        </Button>
      </div>
    </div>
  );
}
