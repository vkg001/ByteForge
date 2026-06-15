// src/pages/ProblemList/ProblemList.tsx
import { useState, useEffect, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { problemService } from '../../services';
import { Problem } from '../../types';
import styles from './ProblemList.module.css';

const IconSearch = () => (
    <svg width="14" height="14" viewBox="0 0 24 24" fill="none"
        stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <circle cx="11" cy="11" r="8" /><path d="m21 21-4.35-4.35" />
    </svg>
);

type Difficulty = 'ALL' | 'EASY' | 'MEDIUM' | 'HARD';

const DIFF_FILTERS: { label: string; value: Difficulty }[] = [
    { label: 'All', value: 'ALL' },
    { label: 'Easy', value: 'EASY' },
    { label: 'Medium', value: 'MEDIUM' },
    { label: 'Hard', value: 'HARD' },
];

const diffBadgeClass = (d: string) => {
    if (d === 'EASY') return styles.diffEasy;
    if (d === 'MEDIUM') return styles.diffMedium;
    if (d === 'HARD') return styles.diffHard;
    return '';
};

const diffLabel = (d: string) =>
    d.charAt(0) + d.slice(1).toLowerCase();

const activePillClass = (value: Difficulty) => {
    if (value === 'EASY') return styles.activeEasy;
    if (value === 'MEDIUM') return styles.activeMedium;
    if (value === 'HARD') return styles.activeHard;
    return styles.active;
};

export const ProblemList = () => {
    const navigate = useNavigate();
    const [problems, setProblems] = useState<Problem[]>([]);
    const [loading, setLoading] = useState(true);
    const [searchQuery, setSearchQuery] = useState('');
    const [difficulty, setDifficulty] = useState<Difficulty>('ALL');

    useEffect(() => {
        const fetchProblems = async () => {
            setLoading(true);
            try {
                const res = await problemService.searchProblems(searchQuery, 0);
                setProblems(res.data || res || []);
            } catch (err) {
                console.error('Failed to fetch problems', err);
            } finally {
                setLoading(false);
            }
        };
        const timer = setTimeout(fetchProblems, 500);
        return () => clearTimeout(timer);
    }, [searchQuery]);

    const filtered = useMemo(() => {
        if (difficulty === 'ALL') return problems;
        return problems.filter(p => p.problemDifficulty === difficulty);
    }, [problems, difficulty]);

    return (
        <div className={styles.page}>

            {/* ── Header ── */}
            <div className={styles.header}>
                <div className={styles.titleWrap}>
                    <h1 className={styles.title}>Problems</h1>
                    {!loading && (
                        <span className={styles.problemCount}>{filtered.length} problems</span>
                    )}
                </div>
            </div>

            {/* ── Toolbar ── */}
            <div className={styles.toolbar}>
                {/* Search */}
                <div className={styles.searchWrap}>
                    <span className={styles.searchIcon}><IconSearch /></span>
                    <input
                        className={styles.searchInput}
                        placeholder="Search problems..."
                        value={searchQuery}
                        onChange={e => setSearchQuery(e.target.value)}
                    />
                </div>

                {/* Difficulty filter */}
                <div className={styles.filterGroup}>
                    {DIFF_FILTERS.map(f => (
                        <button
                            key={f.value}
                            className={`${styles.filterPill} ${difficulty === f.value ? activePillClass(f.value) : ''}`}
                            onClick={() => setDifficulty(f.value)}
                        >
                            {f.label}
                        </button>
                    ))}
                </div>
            </div>

            {/* ── Table ── */}
            <div className={styles.tableCard}>
                <table className={styles.table}>
                    <thead className={styles.thead}>
                        <tr>
                            <th className={`${styles.th} ${styles.thStatus}`} />
                            <th className={`${styles.th} ${styles.thNum}`}>#</th>
                            <th className={styles.th}>Title</th>
                            <th className={`${styles.th} ${styles.thDiff}`}>Difficulty</th>
                            <th className={`${styles.th} ${styles.thTopics}`}>Topics</th>
                        </tr>
                    </thead>
                    <tbody>
                        {loading ? (
                            <tr className={styles.stateRow}>
                                <td colSpan={5}>
                                    <div className={styles.spinnerRing} />
                                </td>
                            </tr>
                        ) : filtered.length === 0 ? (
                            <tr className={styles.stateRow}>
                                <td colSpan={5}>
                                    <div className={styles.emptyState}>
                                        <div className={styles.emptyIcon}>⌕</div>
                                        <div className={styles.emptyTitle}>No problems found</div>
                                        <div className={styles.emptyHint}>
                                            {searchQuery
                                                ? `Nothing matches "${searchQuery}" — try a different term`
                                                : 'No problems match the selected filter'}
                                        </div>
                                    </div>
                                </td>
                            </tr>
                        ) : (
                            filtered.map((problem, index) => (
                                <tr
                                    key={problem.id || index}
                                    className={styles.tr}
                                    onClick={() => navigate(`/problems/${problem.id}`)}
                                >
                                    <td className={styles.tdStatus}>
                                        <div className={styles.statusDot} />
                                    </td>
                                    <td className={styles.tdNum}>{index + 1}</td>
                                    <td className={styles.tdTitle}>
                                        <span className={styles.problemTitle}>
                                            {problem.problemTitle}
                                        </span>
                                    </td>
                                    <td className={styles.tdDiff}>
                                        <span className={`${styles.diffBadge} ${diffBadgeClass(problem.problemDifficulty)}`}>
                                            {diffLabel(problem.problemDifficulty)}
                                        </span>
                                    </td>
                                    <td className={styles.tdTopics}>
                                        <div className={styles.topicsWrap}>
                                            {problem.topics?.slice(0, 3).map(topic => (
                                                <span key={topic} className={styles.topicTag}>
                                                    {topic}
                                                </span>
                                            ))}
                                        </div>
                                    </td>
                                </tr>
                            ))
                        )}
                    </tbody>
                </table>
            </div>
        </div>
    );
};
