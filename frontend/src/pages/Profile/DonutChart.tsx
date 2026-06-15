// src/pages/Profile/DonutChart.tsx
import styles from './Profile.module.css';

interface DonutChartProps {
    easy: number;
    medium: number;
    hard: number;
    total: number;
}

export const DonutChart = ({ easy, medium, hard, total }: DonutChartProps) => {
    const R = 46;
    const STROKE = 9;
    const CIRC = 2 * Math.PI * R;

    // Each segment as a fraction of total, with a min-visible width if > 0
    const segments = [
        { value: easy, color: '#22c55e', label: 'easy' },
        { value: medium, color: '#e2b714', label: 'medium' },
        { value: hard, color: '#ef4444', label: 'hard' },
    ];

    const safeTotal = total || 1;
    let offset = 0;

    const arcs = segments.map(seg => {
        const pct = seg.value / safeTotal;
        const dash = Math.max(pct * CIRC - 2, seg.value > 0 ? 2 : 0); // 2px gap between segments
        const arc = { ...seg, dashArray: `${dash} ${CIRC - dash}`, dashOffset: -offset };
        offset += pct * CIRC;
        return arc;
    });

    return (
        <div className={styles.donutWrap}>
            <svg
                className={styles.donutSvg}
                width="120"
                height="120"
                viewBox="0 0 120 120"
            >
                {/* Track */}
                <circle
                    cx="60" cy="60" r={R}
                    fill="none"
                    stroke="var(--bg-input)"
                    strokeWidth={STROKE}
                />
                {/* Segments */}
                {arcs.map(arc => (
                    <circle
                        key={arc.label}
                        cx="60" cy="60" r={R}
                        fill="none"
                        stroke={arc.color}
                        strokeWidth={STROKE}
                        strokeDasharray={arc.dashArray}
                        strokeDashoffset={arc.dashOffset}
                        strokeLinecap="butt"
                    />
                ))}
            </svg>
            <div className={styles.donutCenter}>
                <span className={styles.donutTotal}>{total}</span>
                <span className={styles.donutSubLabel}>solved</span>
            </div>
        </div>
    );
};