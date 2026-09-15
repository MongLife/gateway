import { cn } from '@/shared/lib/cn';

/**
 * public/logo.png 는 432px 캔버스에 실제 그림이 가운데 186px 뿐이라(여백 ~28%),
 * 박스에 꽉 차도록 1.9배 확대해서 잘라 보여준다. 원본 파일은 건드리지 않는다.
 */
export function Logo({ className }: { className?: string }) {
  return (
    <span className={cn('inline-block shrink-0 overflow-hidden', className)} aria-hidden>
      <img src="/logo.png" alt="" className="size-full scale-[1.9] [image-rendering:pixelated]" draggable={false} />
    </span>
  );
}
