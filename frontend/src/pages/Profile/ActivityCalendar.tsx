// src/pages/Profile/ActivityCalendar.tsx
import { useMemo } from 'react';
import { CalendarEntry } from '../../types';
import styles from './Profile.module.css';

interface ActivityCalendarProps {
    data: CalendarEntry[];
}

const DAY_LABELS = ['', 'Mon', '', 'Wed', '', 'Fri', ''];
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
        // record { monthIndex, weekCol } — one entry per month transition
        const monthLabels: { label: string; col: number }[] = [];
        let lastMonth = -1;
        weeks.forEach((week, col) => {
            const firstReal = week.find(d => d !== null);
            if (firstReal) {
                const month = new Date(firstReal.date).getMonth();
                if (month !== lastMonth) {
                    // Only add if there's enough room (≥2 cols from the end) to avoid overflow
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

    const CELL = 11;  // cell width px
    const GAP = 3;   // gap px
    const STEP = CELL + GAP;
    const DAY_LABEL_WIDTH = 24; // px reserved for Mon/Wed/Fri labels

    return (
        <div>
            {/* Outer wrapper: day-label column + calendar column */}
            <div style={{ display: 'flex', gap: 0 }}>

                {/* Day labels column */}
                <div style={{
                    width: DAY_LABEL_WIDTH,
                    flexShrink: 0,
                    paddingTop: 18, // offset for month label row height
                    display: 'flex',
                    flexDirection: 'column',
                    gap: GAP,
                }}>
                    {DAY_LABELS.map((label, i) => (
                        <div key={i} style={{
                            height: CELL,
                            fontSize: 9.5,
                            color: 'var(--text-muted)',
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'flex-end',
                            paddingRight: 4,
                            lineHeight: 1,
                        }}>
                            {label}
                        </div>
                    ))}
                </div>

                {/* Calendar: month labels + week columns */}
                <div style={{ flex: 1, minWidth: 0 }}>

                    {/* Month label row — positioned relative to week columns */}
                    <div style={{
                        position: 'relative',
                        height: 18,
                        marginBottom: 2,
                    }}>
                        {monthLabels.map(({ label, col }) => (
                            <span
                                key={`${label}-${col}`}
                                style={{
                                    position: 'absolute',
                                    left: col * STEP,
                                    fontSize: 10.5,
                                    color: 'var(--text-muted)',
                                    whiteSpace: 'nowrap',
                                    lineHeight: '18px',
                                }}
                            >
                                {label}
                            </span>
                        ))}
                    </div>

                    {/* Week columns rendered as flex row — no overflow */}
                    <div style={{
                        display: 'flex',
                        gap: GAP,
                        flexWrap: 'nowrap',
                        width: '100%',
                    }}>
                        {weeks.map((week, wi) => (
                            <div
                                key={wi}
                                style={{
                                    display: 'flex',
                                    flexDirection: 'column',
                                    gap: GAP,
                                    flex: '1 1 0',      /* each week column grows equally */
                                    minWidth: 0,
                                }}
                            >
                                {week.map((day, di) =>
                                    day === null ? (
                                        <div
                                            key={`pad-${wi}-${di}`}
                                            style={{
                                                aspectRatio: '1',
                                                borderRadius: 2,
                                                background: 'transparent',
                                            }}
                                        />
                                    ) : (
                                        <div
                                            key={day.date}
                                            className={`${styles.calendarCell} ${getIntensity(day.count)}`}
                                            style={{ aspectRatio: '1', width: '100%', height: 'auto' }}
                                            title={`${day.count} submission${day.count !== 1 ? 's' : ''} — ${day.date}`}
                                        />
                                    )
                                )}
                            </div>
                        ))}
                    </div>
                </div>
            </div>

            {/* Legend */}
            <div className={styles.calendarLegend}>
                <span className={styles.calendarSummary}>
                    {totalSubmissions.toLocaleString()} submission{totalSubmissions !== 1 ? 's' : ''} in the past year
                </span>
                <div className={styles.legendItems}>
                    <span>Less</span>
                    {[styles.intensity0, styles.intensity1, styles.intensity2, styles.intensity3, styles.intensity4].map((cls, i) => (
                        <div key={i} className={`${styles.legendCell} ${cls}`} />
                    ))}
                    <span>More</span>
                </div>
            </div>
        </div>
    );
};