export interface TrendPoint {
  /** YYYY-MM-DD */
  date: string;
  value: number;
  tooltip?: string;
}

/**
 * 의존성 없는 간단한 SVG 막대 차트. 디자인 시스템 싱크 후 차트 라이브러리로 교체해도 된다.
 */
export function TrendChart({ data, loading, label }: { data: TrendPoint[]; loading?: boolean; label: string }) {
  if (loading) return <div className="h-48 animate-pulse rounded bg-surface-muted" />;
  if (data.length === 0) return <p className="py-12 text-center text-sm text-muted-foreground">데이터가 없습니다.</p>;

  const W = 800;
  const H = 180;
  const pad = { top: 8, right: 8, bottom: 24, left: 36 };
  const innerW = W - pad.left - pad.right;
  const innerH = H - pad.top - pad.bottom;
  const max = Math.max(1, ...data.map((d) => d.value));
  const step = innerW / data.length;
  const barW = Math.max(4, step * 0.6);
  const ticks = [0, 0.5, 1].map((r) => Math.round(max * r));

  return (
    <div className="w-full overflow-x-auto">
      <svg viewBox={`0 0 ${W} ${H}`} className="h-48 w-full min-w-[480px]" role="img" aria-label={label}>
        {ticks.map((t) => {
          const y = pad.top + innerH - (t / max) * innerH;
          return (
            <g key={t}>
              <line x1={pad.left} x2={W - pad.right} y1={y} y2={y} stroke="var(--color-border)" strokeDasharray="2 3" />
              <text x={pad.left - 6} y={y + 3} textAnchor="end" fontSize="10" fill="var(--color-muted-foreground)">
                {t}
              </text>
            </g>
          );
        })}
        {data.map((d, i) => {
          const h = (d.value / max) * innerH;
          const x = pad.left + i * step + (step - barW) / 2;
          const y = pad.top + innerH - h;
          const showLabel = data.length <= 14 || i % Math.ceil(data.length / 10) === 0;
          return (
            <g key={d.date}>
              <rect x={x} y={y} width={barW} height={h} rx="3" fill="var(--color-primary)" opacity="0.85">
                <title>{d.tooltip ?? `${d.date} · ${d.value}`}</title>
              </rect>
              {showLabel && (
                <text x={x + barW / 2} y={H - 8} textAnchor="middle" fontSize="10" fill="var(--color-muted-foreground)">
                  {d.date.slice(5)}
                </text>
              )}
            </g>
          );
        })}
      </svg>
    </div>
  );
}
