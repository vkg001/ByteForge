// src/pages/Profile/ActivityCalendar.tsx
import { useMemo } from 'react';
import { CalendarEntry } from '../../types';
import styles from './Profile.module.css';

interface ActivityCalendarProps {
    data: CalendarEntry[];
}

const MONTH_NAMES = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];

export const ActivityCalendar = ({ data }: ActivityCalendarProps) => {
    const activityMap = useMemo(() => {
        const map = new Map<string, number>();
        data.forEach(entry => map.set(entry.date, entry.count));
        return map;
    }, [data]);

    const { weeks, totalSubmissions, monthLabels } = useMemo(() => {
        const today = new Date();
        const allDays: { date: string; count: number }[] = [];

        for (let i = 364; i >= 0; i--) {
            const d = new Date(today);
            d.setDate(d.getDate() - i);
            const dateStr = d.toISOString().split('T')[0];
            allDays.push({ date: dateStr, count: activityMap.get(dateStr) || 0 });
        }

        // Pad start so first day aligns to its correct weekday (0 = Sun)
        const firstDayOfWeek = new Date(allDays[0].date).getDay();
        const padded: ({ date: string; count: number } | null)[] = [
            ...Array(firstDayOfWeek).fill(null),
            ...allDays,
        ];

        // Chunk into weeks of 7
        const weeks: ({ date: string; count: number } | null)[][] = [];
        for (let i = 0; i < padded.length; i += 7) {
            weeks.push(padded.slice(i, i + 7));
        }

        // Month label: find the first week where a new month starts
        const monthLabels: { label: string; col: number }[] = [];
        let lastMonth = -1;
        weeks.forEach((week, col) => {
            const firstReal = week.find(d => d !== null);
            if (firstReal) {
                const month = new Date(firstReal.date).getMonth();
                if (month !== lastMonth) {
                    if (col <= weeks.length - 2) {
                        monthLabels.push({ label: MONTH_NAMES[month], col });
                    }
                    lastMonth = month;
                }
            }
        });

        const totalSubmissions = data.reduce((s, e) => s + e.count, 0);
        return { weeks, totalSubmissions, monthLabels };
    }, [activityMap, data]);

    const getIntensity = (count: number) => {
        if (count === 0) return styles.intensity0;
        if (count <= 2) return styles.intensity1;
        if (count <= 4) return styles.intensity2;
        if (count <= 7) return styles.intensity3;
        return styles.intensity4;
    };

    const CELL = 13; // Sized up slightly to match LeetCode proportions
    const GAP = 4;   
    const STEP = CELL + GAP;

    return (
        <div style={{ display: 'flex', flexDirection: 'column' }}>
            
            {/* Top Stats Header */}
            <div style={{ marginBottom: '1.25rem', display: 'flex', alignItems: 'baseline' }}>
                <span style={{ fontSize: '20px', fontWeight: '600', color: 'var(--text-primary)', letterSpacing: '-0.02em' }}>
                    {totalSubmissions.toLocaleString()}
                </span>
                <span style={{ fontSize: '13.5px', color: 'var(--text-muted)', marginLeft: '8px' }}>
                    submissions in the past one year
                </span>
            </div>

            {/* Scrollable grid area */}
            <div style={{}}>
                <div style={{ display: 'inline-flex', flexDirection: 'column', minWidth: '100%' }}>
                    
                    {/* Grid */}
                    <div style={{ display: 'flex', gap: GAP, flexWrap: 'nowrap' }}>
                        {weeks.map((week, wi) => (
                            <div key={wi} style={{ display: 'flex', flexDirection: 'column', gap: GAP, scale: '0.9' }}>
                                {week.map((day, di) =>
                                    day === null ? (
                                        <div
                                            key={`pad-${wi}-${di}`}
                                            style={{ width: CELL, height: CELL, borderRadius: 3, background: 'transparent' }}
                                        />
                                    ) : (
                                        <div
                                            key={day.date}
                                            className={`${styles.calendarCell} ${getIntensity(day.count)}`}
                                            style={{ width: CELL, height: CELL }}
                                            title={`${day.count} submission${day.count !== 1 ? 's' : ''} — ${day.date}`}
                                        />
                                    )
                                )}
                            </div>
                        ))}
                    </div>

                    {/* Month Labels below grid */}
                    <div style={{ position: 'relative', height: 20, marginTop: 8 }}>
                        {monthLabels.map(({ label, col }) => (
                            <span
                                key={`${label}-${col}`}
                                style={{
                                    position: 'absolute',
                                    left: col * STEP,
                                    fontSize: 12,
                                    color: 'var(--text-muted)',
                                    whiteSpace: 'nowrap',
                                }}
                            >
                                {label}
                            </span>
                        ))}
                    </div>
                    
                </div>
            </div>
        </div>
    );
};