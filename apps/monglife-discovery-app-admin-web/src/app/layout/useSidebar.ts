import { useCallback, useEffect, useState } from 'react';

const KEY = 'monglife.admin.sidebar';

/** 사이드바 접힘 상태. 새로고침해도 유지 */
export function useSidebar() {
  const [collapsed, setCollapsed] = useState(() => {
    try {
      return localStorage.getItem(KEY) === 'collapsed';
    } catch {
      return false;
    }
  });
  useEffect(() => {
    try {
      localStorage.setItem(KEY, collapsed ? 'collapsed' : 'expanded');
    } catch {
      /* noop */
    }
  }, [collapsed]);
  const toggle = useCallback(() => setCollapsed((c) => !c), []);
  return { collapsed, toggle };
}
