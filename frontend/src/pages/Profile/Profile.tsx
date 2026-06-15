// src/pages/Profile/Profile.tsx
import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { userService } from '../../services';
import { User, UserStats, CalendarEntry } from '../../types';
import { ActivityCalendar } from './ActivityCalendar';
import { DonutChart } from './DonutChart';
import styles from './Profile.module.css';

// ── Inline SVG icons (no extra dep) ────────────────────────────────────────
const IconPin = () => (
    <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z" /><circle cx="12" cy="10" r="3" />
    </svg>
);
const IconBuilding = () => (
    <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <rect x="2" y="7" width="20" height="14" rx="2" /><path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16" />
    </svg>
);
const IconFlame = () => (
    <svg width="12" height="12" viewBox="0 0 24 24" fill="currentColor">
        <path d="M12 2c0 0-5 5.5-5 10a5 5 0 0 0 10 0c0-2.5-1.5-4.5-2.5-5.5C14 8 13 10 12 10c-1 0-1.5-1-1.5-2C10.5 6 12 2 12 2z" opacity="0.5" />
        <path d="M12 22a7 7 0 0 0 7-7c0-4-3.5-7-5-8.5C14 8 13 10 12 10c-.8 0-1.5-.5-1.5-1.5 0-1 .5-2 .5-2C9 8 5 11.5 5 15a7 7 0 0 0 7 7z" />
    </svg>
);

export const Profile = () => {
    const { userId } = useParams<{ userId: string }>();
    const [profileUser, setProfileUser] = useState<User | null>(null);
    const [stats, setStats] = useState<UserStats | null>(null);
    const [calendar, setCalendar] = useState<CalendarEntry[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetch = async () => {
            setLoading(true);
            try {
                const [userData, statsData, calendarData] = await Promise.all([
                    userId ? userService.getUserById(userId) : userService.getCurrentUser(),
                    userService.getUserStats(userId),
                    userService.getUserCalendar(userId),
                ]);
                setProfileUser(userData);
                setStats(statsData);
                setCalendar(calendarData);
            } catch (err) {
                console.error('Failed to fetch profile data', err);
            } finally {
                setLoading(false);
            }
        };
        fetch();
    }, [userId]);

    if (loading || !profileUser || !stats) {
        return (
            <div className={styles.loadingPage}>
                <div className={styles.spinnerRing} />
            </div>
        );
    }

    const { easy, medium, hard } = stats.problemStats;
    const totalSolved = easy + medium + hard;

    const easyPct = Math.max((easy / (totalSolved || 1)) * 100, easy > 0 ? 4 : 0);
    const medPct = Math.max((medium / (totalSolved || 1)) * 100, medium > 0 ? 4 : 0);
    const hardPct = Math.max((hard / (totalSolved || 1)) * 100, hard > 0 ? 4 : 0);

    return (
        <div className={styles.page}>

            {/* ── Left Column ── */}
            <div className={styles.leftCol}>

                {/* Profile card */}
                <div className={styles.card}>
                    <div className={styles.avatarWrap}>
                        <div className={styles.avatar}>
                            {profileUser.name.charAt(0).toUpperCase()}
                        </div>
                        <h1 className={styles.userName}>{profileUser.name}</h1>
                        <div className={styles.rankBadge}>
                            {stats.reputation > 0 ? `Rank #${stats.reputation}` : 'Unranked'}
                        </div>
                    </div>

                    <div>
                        <div className={styles.metaRow}>
                            <span className={styles.metaLabel}><IconPin /> Location</span>
                            <span className={styles.metaValue}>{profileUser.location || '—'}</span>
                        </div>
                        <div className={styles.metaRow}>
                            <span className={styles.metaLabel}><IconBuilding /> Company</span>
                            <span className={styles.metaValue}>{profileUser.company || '—'}</span>
                        </div>
                    </div>
                </div>

                {/* Streak card */}
                <div className={styles.card}>
                    <p className={styles.cardHeader}>Streak</p>
                    <div className={styles.streakGrid}>
                        <div className={styles.streakItem}>
                            <span className={styles.streakLabel}>
                                <span style={{ color: '#f97316' }}><IconFlame /></span>
                                Current
                            </span>
                            <span className={styles.streakValue}>
                                {stats.currentStreak}
                                <span className={styles.streakUnit}>d</span>
                            </span>
                        </div>
                        <div className={styles.streakDivider} />
                        <div className={styles.streakItem}>
                            <span className={styles.streakLabel}>
                                <span style={{ color: 'var(--accent)' }}><IconFlame /></span>
                                Best
                            </span>
                            <span className={styles.streakValue}>
                                {stats.maxStreak}
                                <span className={styles.streakUnit}>d</span>
                            </span>
                        </div>
                    </div>
                </div>
            </div>

            {/* ── Right Column ── */}
            <div className={styles.rightCol}>

                {/* Solved problems */}
                <div className={styles.card}>
                    <p className={styles.cardHeader}>Solved Problems</p>
                    <div className={styles.solvedWrap}>
                        <DonutChart easy={easy} medium={medium} hard={hard} total={totalSolved} />

                        <div className={styles.difficultyList}>
                            {/* Easy */}
                            <div className={styles.difficultyRow}>
                                <div className={styles.difficultyMeta}>
                                    <span className={`${styles.diffLabel} ${styles.easy}`}>Easy</span>
                                    <span className={styles.diffCount}>{easy}</span>
                                </div>
                                <div className={styles.barTrack}>
                                    <div className={`${styles.barFill} ${styles.easy}`} style={{ width: `${easyPct}%` }} />
                                </div>
                            </div>

                            {/* Medium */}
                            <div className={styles.difficultyRow}>
                                <div className={styles.difficultyMeta}>
                                    <span className={`${styles.diffLabel} ${styles.medium}`}>Medium</span>
                                    <span className={styles.diffCount}>{medium}</span>
                                </div>
                                <div className={styles.barTrack}>
                                    <div className={`${styles.barFill} ${styles.medium}`} style={{ width: `${medPct}%` }} />
                                </div>
                            </div>

                            {/* Hard */}
                            <div className={styles.difficultyRow}>
                                <div className={styles.difficultyMeta}>
                                    <span className={`${styles.diffLabel} ${styles.hard}`}>Hard</span>
                                    <span className={styles.diffCount}>{hard}</span>
                                </div>
                                <div className={styles.barTrack}>
                                    <div className={`${styles.barFill} ${styles.hard}`} style={{ width: `${hardPct}%` }} />
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                {/* Activity heatmap */}
                <div className={styles.card}>
                    <p className={styles.cardHeader}>Submission Activity</p>
                    <ActivityCalendar data={calendar} />
                </div>

            </div>
        </div>
    );
};