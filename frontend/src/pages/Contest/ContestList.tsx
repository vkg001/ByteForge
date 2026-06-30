// src/pages/Contest/ContestList.tsx
import { useState, useEffect, useRef, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { contestService } from '../../services';
import { Contest, ContestStatus } from '../../types';
import styles from './ContestList.module.css';

const IconClock = () => (
    <svg width="13" height="13" viewBox="0 0 24 24" fill="none"
        stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <circle cx="12" cy="12" r="10" /><path d="M12 6v6l4 2" />
    </svg>
);

const IconChevronLeft = () => (
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none"
        stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <path d="m15 18-6-6 6-6" />
    </svg>
);

const IconChevronRight = () => (
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none"
        stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <path d="m9 18 6-6-6-6" />
    </svg>
);

const getStatus = (startTime: string, endTime: string): ContestStatus => {
    const now = Date.now();
    const start = new Date(startTime).getTime();
    const end = new Date(endTime).getTime();
    if (now < start) return 'upcoming';
    if (now <= end) return 'live';
    return 'ended';
};

const statusBadgeClass = (status: ContestStatus) => {
    if (status === 'live') return styles.statusLive;
    if (status === 'upcoming') return styles.statusUpcoming;
    return styles.statusEnded;
};

const statusLabel = (status: ContestStatus) => {
    if (status === 'live') return 'Live';
    if (status === 'upcoming') return 'Upcoming';
    return 'Ended';
};

const formatDateTime = (iso: string) =>
    new Date(iso).toLocaleString(undefined, {
        weekday: 'short',
        month: 'short',
        day: 'numeric',
        hour: 'numeric',
        minute: '2-digit',
    });

const formatDuration = (startTime: string, endTime: string) => {
    const ms = new Date(endTime).getTime() - new Date(startTime).getTime();
    const totalMinutes = Math.max(0, Math.round(ms / 60000));
    const hours = Math.floor(totalMinutes / 60);
    const minutes = totalMinutes % 60;
    if (hours === 0) return `${minutes}m`;
    if (minutes === 0) return `${hours}h`;
    return `${hours}h ${minutes}m`;
};

/** Ticks once a second while `targetIso` is in the future; null once it passes. */
const useCountdown = (targetIso: string | null) => {
    const [remaining, setRemaining] = useState(() =>
        targetIso ? new Date(targetIso).getTime() - Date.now() : 0
    );

    useEffect(() => {
        if (!targetIso) return;
        const tick = () => setRemaining(new Date(targetIso).getTime() - Date.now());
        tick();
        const interval = setInterval(tick, 1000);
        return () => clearInterval(interval);
    }, [targetIso]);

    if (!targetIso || remaining <= 0) return null;

    const totalSeconds = Math.floor(remaining / 1000);
    return {
        days: Math.floor(totalSeconds / 86400),
        hours: Math.floor((totalSeconds % 86400) / 3600),
        minutes: Math.floor((totalSeconds % 3600) / 60),
        seconds: totalSeconds % 60,
    };
};

export const ContestList = () => {
    const navigate = useNavigate();
    const [page, setPage] = useState(0);
    const [contests, setContests] = useState<Contest[]>([]);
    const [loading, setLoading] = useState(true);
    const [isLastPage, setIsLastPage] = useState(false);

    // Tracks which page's data is currently in `contests`, so that bouncing
    // back from an over-paginated request doesn't trigger a redundant fetch.
    const lastLoadedPage = useRef<number | null>(null);

    useEffect(() => {
        if (page === lastLoadedPage.current) {
            setLoading(false);
            return;
        }

        let cancelled = false;

        const fetchContests = async () => {
            setLoading(true);
            try {
                const res = await contestService.getContests(page);
                const data: Contest[] = Array.isArray(res) ? res : res?.data || [];
                if (cancelled) return;

                if (data.length === 0) {
                    // Empty array = no more pages. Step back to the last
                    // valid page without re-fetching it.
                    setIsLastPage(true);
                    if (page > 0) {
                        setPage(p => p - 1);
                        return;
                    }
                } else {
                    setContests(data);
                    setIsLastPage(false);
                    lastLoadedPage.current = page;
                }
            } catch (err) {
                console.error('Failed to fetch contests', err);
            } finally {
                if (!cancelled) setLoading(false);
            }
        };

        fetchContests();
        return () => { cancelled = true; };
    }, [page]);

    // API sorts descending by startTime, so the first item on page 0 is the
    // soonest upcoming (or currently live) contest — feature it as a hero card.
    const featured = useMemo(() => {
        if (page !== 0 || contests.length === 0) return null;
        const status = getStatus(contests[0].startTime, contests[0].endTime);
        return status === 'ended' ? null : contests[0];
    }, [contests, page]);

    const rest = featured ? contests.slice(1) : contests;

    const countdown = useCountdown(
        featured && getStatus(featured.startTime, featured.endTime) === 'upcoming'
            ? featured.startTime
            : null
    );

    const handlePrev = () => {
        if (page > 0) {
            // Any page we can go back to always has a "next" page (the one
            // we're leaving), so clear any stale last-page flag.
            setIsLastPage(false);
            setPage(p => p - 1);
        }
    };

    const handleNext = () => {
        if (!isLastPage) setPage(p => p + 1);
    };

    return (
        <div className={styles.page}>
            <div className={styles.header}>
                <h1 className={styles.title}>Contests</h1>
            </div>

            {loading && contests.length === 0 ? (
                <div className={styles.stateWrap}>
                    <div className={styles.spinnerRing} />
                </div>
            ) : contests.length === 0 ? (
                <div className={styles.stateWrap}>
                    <div className={styles.emptyState}>
                        <div className={styles.emptyIcon}>🏆</div>
                        <div className={styles.emptyTitle}>No contests found</div>
                        <div className={styles.emptyHint}>Check back later for upcoming contests</div>
                    </div>
                </div>
            ) : (
                <>
                    {featured && (
                        <div
                            className={styles.featuredCard}
                            onClick={() => navigate(`/contest/${featured.id}`)}
                        >
                            <div className={styles.featuredTop}>
                                <span className={`${styles.statusBadge} ${statusBadgeClass(getStatus(featured.startTime, featured.endTime))}`}>
                                    {getStatus(featured.startTime, featured.endTime) === 'live' && <span className={styles.liveDot} />}
                                    {statusLabel(getStatus(featured.startTime, featured.endTime))}
                                </span>
                                {countdown && (
                                    <div className={styles.countdown}>
                                        {countdown.days > 0 && <span>{countdown.days}d</span>}
                                        <span>{String(countdown.hours).padStart(2, '0')}h</span>
                                        <span>{String(countdown.minutes).padStart(2, '0')}m</span>
                                        <span>{String(countdown.seconds).padStart(2, '0')}s</span>
                                    </div>
                                )}
                            </div>
                            <h2 className={styles.featuredTitle}>{featured.title}</h2>
                            <p className={styles.featuredDescription}>{featured.description}</p>
                            <div className={styles.featuredMeta}>
                                <span className={styles.metaItem}><IconClock /> {formatDateTime(featured.startTime)}</span>
                                <span className={styles.metaDivider}>·</span>
                                <span className={styles.metaItem}>{formatDuration(featured.startTime, featured.endTime)}</span>
                            </div>
                        </div>
                    )}

                    {rest.length > 0 && (
                        <div className={styles.listCard}>
                            {rest.map(contest => {
                                const status = getStatus(contest.startTime, contest.endTime);
                                return (
                                    <div
                                        key={contest.id}
                                        className={styles.row}
                                        onClick={() => navigate(`/contest/${contest.id}`)}
                                    >
                                        <div className={styles.rowMain}>
                                            <span className={styles.rowTitle}>{contest.title}</span>
                                            <span className={styles.rowDescription}>{contest.description}</span>
                                        </div>
                                        <div className={styles.rowMeta}>
                                            <span className={styles.rowDate}>{formatDateTime(contest.startTime)}</span>
                                            <span className={styles.rowDuration}>{formatDuration(contest.startTime, contest.endTime)}</span>
                                            <span className={`${styles.statusBadge} ${statusBadgeClass(status)}`}>
                                                {status === 'live' && <span className={styles.liveDot} />}
                                                {statusLabel(status)}
                                            </span>
                                        </div>
                                    </div>
                                );
                            })}
                        </div>
                    )}

                    <div className={styles.pagination}>
                        <button
                            className={styles.pageBtn}
                            onClick={handlePrev}
                            disabled={page === 0 || loading}
                        >
                            <IconChevronLeft /> Prev
                        </button>
                        <span className={styles.pageNumber}>Page {page + 1}</span>
                        <button
                            className={styles.pageBtn}
                            onClick={handleNext}
                            disabled={isLastPage || loading}
                        >
                            Next <IconChevronRight />
                        </button>
                    </div>
                </>
            )}
        </div>
    );
};