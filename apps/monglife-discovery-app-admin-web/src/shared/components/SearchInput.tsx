import { useEffect, useState } from 'react';
import { Search } from 'lucide-react';
import { Input } from '@/shared/ui';
import { cn } from '@/shared/lib/cn';

interface SearchInputProps {
  value: string;
  onChange: (value: string) => void;
  placeholder?: string;
  className?: string;
}

/** 300ms 디바운스 검색 입력 */
export function SearchInput({ value, onChange, placeholder = '검색', className }: SearchInputProps) {
  const [local, setLocal] = useState(value);
  useEffect(() => setLocal(value), [value]);
  useEffect(() => {
    const t = setTimeout(() => local !== value && onChange(local), 300);
    return () => clearTimeout(t);
  }, [local, value, onChange]);
  return (
    <div className={cn('relative w-64', className)}>
      <Search className="pointer-events-none absolute top-1/2 left-2.5 size-4 -translate-y-1/2 text-muted-foreground" />
      <Input className="pl-8" value={local} onChange={(e) => setLocal(e.target.value)} placeholder={placeholder} />
    </div>
  );
}
