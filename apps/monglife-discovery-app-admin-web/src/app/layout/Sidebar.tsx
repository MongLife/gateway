import { NavLink } from 'react-router-dom';
import { PanelLeftClose, PanelLeftOpen } from 'lucide-react';
import { NAV } from '@/app/nav';
import { cn } from '@/shared/lib/cn';
import { Logo } from '@/shared/components/Logo';

interface SidebarProps {
  collapsed: boolean;
  onToggle: () => void;
}

export function Sidebar({ collapsed, onToggle }: SidebarProps) {
  return (
    <aside
      className={cn(
        'flex shrink-0 flex-col bg-sidebar text-sidebar-foreground transition-[width] duration-200',
        collapsed ? 'w-16' : 'w-60',
      )}
    >
      <div className={cn('flex h-14 items-center', collapsed ? 'flex-col justify-center gap-1 px-0' : 'gap-2 px-4')}>
        {!collapsed && (
          <>
            <Logo className="size-8" />
            <span className="min-w-0 flex-1 truncate text-sm font-semibold text-white">MongLife Admin</span>
          </>
        )}
        <button
          type="button"
          onClick={onToggle}
          aria-label={collapsed ? '사이드바 펼치기' : '사이드바 접기'}
          title={collapsed ? '사이드바 펼치기' : '사이드바 접기'}
          className="rounded-md p-1.5 hover:bg-sidebar-active hover:text-white"
        >
          {collapsed ? <PanelLeftOpen className="size-4" /> : <PanelLeftClose className="size-4" />}
        </button>
      </div>

      <nav className={cn('flex-1 space-y-5 overflow-y-auto py-3', collapsed ? 'px-2' : 'px-3')}>
        {NAV.map((group) => (
          <div key={group.title}>
            {collapsed ? (
              <div className="mx-2 mb-2 border-t border-white/10" />
            ) : (
              <p className="mb-1.5 px-2 text-[11px] font-medium tracking-wide uppercase opacity-60">{group.title}</p>
            )}
            <ul className="space-y-0.5">
              {group.items.map((item) => (
                <li key={item.to}>
                  <NavLink
                    to={item.to}
                    end={item.end}
                    title={collapsed ? item.label : undefined}
                    className={({ isActive }) =>
                      cn(
                        'flex items-center gap-2.5 rounded-md text-sm transition-colors',
                        collapsed ? 'justify-center px-0 py-2.5' : 'px-2.5 py-2',
                        isActive ? 'bg-sidebar-active text-white' : 'hover:bg-sidebar-active/60 hover:text-white',
                      )
                    }
                  >
                    <item.icon className="size-4 shrink-0" />
                    {!collapsed && <span className="truncate">{item.label}</span>}
                  </NavLink>
                </li>
              ))}
            </ul>
          </div>
        ))}
      </nav>
      {!collapsed && <div className="px-5 py-3 text-[11px] opacity-50">discovery admin · v0.1.0</div>}
    </aside>
  );
}
