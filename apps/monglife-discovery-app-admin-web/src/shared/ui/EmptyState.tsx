import { Inbox } from 'lucide-react';

export function EmptyState({ message = '데이터가 없습니다.' }: { message?: string }) {
  return (
    <div className="flex flex-col items-center justify-center gap-2 py-12 text-muted-foreground">
      <Inbox className="size-6" />
      <p className="text-sm">{message}</p>
    </div>
  );
}
