import { Select } from '@/shared/ui';
import { cn } from '@/shared/lib/cn';

interface Option {
  value: string;
  label: string;
}

interface FilterSelectProps {
  label: string;
  value: string;
  onChange: (value: string) => void;
  options: readonly (Option | string)[];
  allLabel?: string;
  className?: string;
}

/** "전체" 옵션이 붙은 필터 셀렉트. value '' 가 전체 */
export function FilterSelect({ label, value, onChange, options, allLabel = '전체', className }: FilterSelectProps) {
  return (
    <Select aria-label={label} className={cn('w-36', className)} value={value} onChange={(e) => onChange(e.target.value)}>
      <option value="">
        {label}: {allLabel}
      </option>
      {options.map((o) => {
        const opt = typeof o === 'string' ? { value: o, label: o } : o;
        return (
          <option key={opt.value} value={opt.value}>
            {opt.label}
          </option>
        );
      })}
    </Select>
  );
}
