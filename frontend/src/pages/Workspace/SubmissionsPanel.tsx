// src/pages/Workspace/SubmissionsPanel.tsx
import { useState, useEffect, useCallback } from 'react';
import { submissionService } from '../../services';
import styles from './Workspace.module.css';

// ── Types ─────────────────────────────────────────────────────────────────

interface FailedTestCase {
    input: string;
    output: string;
    hidden: boolean;
    hiddenAfterFailure: boolean;
}

interface Submission {
    id: number;
    languageId: number;
    submissionCode: string;
    failedOnTestCase: FailedTestCase | null;
    submissionStatus: string;
    codeOutput: any; // typed as 'any' to handle the empty arrays/strings your API currently sends
    userLogs: string | null;
    submissionDateTime: string;
}

interface SubmissionsResponse {
    allSubmissions: Submission[];
    status: string;
}

// ── Helpers ───────────────────────────────────────────────────────────────

const LANG_LABEL: Record<number, string> = {
    54: 'C++',
    62: 'Java',
    71: 'Python 3',
};

const STATUS_META: Record<string, { label: string; color: 'green' | 'red' | 'muted' }> = {
    ACC: { label: 'Accepted', color: 'green' },
    WA: { label: 'Wrong Answer', color: 'red' },
    TLE: { label: 'Time Limit Exceeded', color: 'red' },
    MLE: { label: 'Memory Limit Exceeded', color: 'red' },
    RE: { label: 'Runtime Error', color: 'red' },
    CE: { label: 'Compile Error', color: 'red' },
};

const statusMeta = (s: string) =>
    STATUS_META[s] ?? { label: s, color: 'muted' as const };

function formatDate(iso: string): string {
    const d = new Date(iso);
    return d.toLocaleString(undefined, {
        month: 'short', day: 'numeric', year: 'numeric',
        hour: '2-digit', minute: '2-digit',
    });
}

// ── Sub-component: expanded detail for one submission ─────────────────────

const SubmissionDetail = ({ sub }: { sub: Submission }) => {
    const meta = statusMeta(sub.submissionStatus);
    
    // Strict guard against the backend bug you mentioned. 
    // If it's ACC, we ignore any phantom failed test cases.
    const isActuallyFailed = sub.submissionStatus !== 'ACC' && sub.failedOnTestCase;
    const showFailedCase = isActuallyFailed && !sub.failedOnTestCase!.hidden;

    return (
        <div className={styles.subDetail}>
            {/* Verdict summary row */}
            <div
                className={styles.subDetailVerdict}
                data-color={meta.color}
            >
                <span className={styles.subDetailVerdictLabel}>{meta.label}</span>
                {isActuallyFailed && (
                    <span className={styles.subDetailVerdictHint}>
                        Failed on {sub.failedOnTestCase!.hidden ? 'a hidden test case' : 'test case'}
                    </span>
                )}
            </div>

            {/* Code block */}
            <div>
                <p className={styles.subDetailSectionLabel}>Code</p>
                <pre className={styles.subCode}>{sub.submissionCode}</pre>
            </div>

            {/* Failed test case (if applicable and not hidden) */}
            {showFailedCase && (
                <div>
                    <p className={styles.subDetailSectionLabel}>Failed Test Case</p>
                    <div className={styles.caseDetail}>
                        <div className={styles.ioGroup}>
                            <p className={styles.ioLabel}>Input</p>
                            <pre className={styles.ioBlock}>{sub.failedOnTestCase!.input}</pre>
                        </div>
                        <div className={styles.ioGroup}>
                            <p className={styles.ioLabel}>Expected Output</p>
                            <pre className={styles.ioBlock}>{sub.failedOnTestCase!.output}</pre>
                        </div>
                        
                        {/* Render Stdout if logs exist and aren't just empty space */}
                        {sub.userLogs && sub.userLogs.trim() !== '' && (
                            <div className={styles.ioGroup}>
                                <p className={styles.ioLabel}>Stdout</p>
                                <pre className={styles.ioBlock}>{sub.userLogs}</pre>
                            </div>
                        )}
                    </div>
                </div>
            )}
        </div>
    );
};

// ── Main component ────────────────────────────────────────────────────────

interface SubmissionsPanelProps {
    problemId: string;
}

export const SubmissionsPanel = ({ problemId }: SubmissionsPanelProps) => {
    const [submissions, setSubmissions] = useState<Submission[]>([]);
    const [loading, setLoading] = useState(true);
    const [page, setPage] = useState(0);
    const [hasMore, setHasMore] = useState(true);
    const [expandedId, setExpandedId] = useState<number | null>(null);

    const PAGE_SIZE = 10;

    const fetchPage = useCallback(async (pageNum: number, replace: boolean) => {
        setLoading(true);
        try {
            const res: SubmissionsResponse = await submissionService.getSubmissions(problemId, pageNum);
            const incoming = res.allSubmissions ?? [];
            setSubmissions(prev => replace ? incoming : [...prev, ...incoming]);
            setHasMore(incoming.length >= PAGE_SIZE);
        } catch (err) {
            console.error('Failed to load submissions', err);
        } finally {
            setLoading(false);
        }
    }, [problemId]);

    useEffect(() => {
        setSubmissions([]);
        setPage(0);
        setExpandedId(null);
        fetchPage(0, true);
    }, [problemId, fetchPage]);

    const handleLoadMore = () => {
        const next = page + 1;
        setPage(next);
        fetchPage(next, false);
    };

    const toggle = (id: number) =>
        setExpandedId(prev => prev === id ? null : id);

    // ── Empty / loading states ────────────────────────────────────────────

    if (loading && submissions.length === 0) {
        return (
            <div className={styles.subLoadingWrap}>
                <span className={styles.executingSpinner} />
            </div>
        );
    }

    if (!loading && submissions.length === 0) {
        return (
            <div className={styles.submissionsEmpty}>
                <div className={styles.submissionsEmptyIcon}>⌕</div>
                <p>No submissions yet</p>
                <p style={{ fontSize: '12px' }}>Submit your code to see history here.</p>
            </div>
        );
    }

    // ── List ──────────────────────────────────────────────────────────────

    return (
        <div className={styles.subList}>
            {submissions.map(sub => {
                const meta = statusMeta(sub.submissionStatus);
                const isOpen = expandedId === sub.id;
                const langLabel = LANG_LABEL[sub.languageId] ?? `Lang ${sub.languageId}`;

                return (
                    <div key={sub.id} className={styles.subRow}>
                        {/* ── Summary row (always visible) ── */}
                        <button
                            className={styles.subSummary}
                            onClick={() => toggle(sub.id)}
                            aria-expanded={isOpen}
                        >
                            <span
                                className={styles.subStatus}
                                data-color={meta.color}
                            >
                                {meta.label}
                            </span>
                            <span className={styles.subLang}>{langLabel}</span>
                            <span className={styles.subDate}>{formatDate(sub.submissionDateTime)}</span>
                            <span className={`${styles.subChevron} ${isOpen ? styles.subChevronOpen : ''}`}>
                                ›
                            </span>
                        </button>

                        {/* ── Expanded detail ── */}
                        {isOpen && <SubmissionDetail sub={sub} />}
                    </div>
                );
            })}

            {/* Load more */}
            {hasMore && (
                <div className={styles.subLoadMore}>
                    <button
                        className={`${styles.btnSm} ${styles.btnSecondary}`}
                        onClick={handleLoadMore}
                        disabled={loading}
                    >
                        {loading ? 'Loading…' : 'Load more'}
                    </button>
                </div>
            )}
        </div>
    );
};