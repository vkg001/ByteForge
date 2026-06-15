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
    codeOutput: string;
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

/** Parse the raw ~CASE_BEGIN~...~CASE_END~ output string into structured cases */
interface ParsedCase {
    output: string;
    userLogs: string;
    timeMs: number | null;
}

function parseCaseOutput(raw: string): ParsedCase[] {
    const blocks = raw.split('~CASE_BEGIN~').filter(b => b.trim());
    return blocks.map(block => {
        // strip ~CASE_END~
        const body = block.replace('~CASE_END~', '');

        const logsMatch = body.match(/~USER_LOGS~([\s\S]*?)~FUNC_OUT~/);
        const outputMatch = body.match(/~FUNC_OUT~\n?([\s\S]*?)~TIME\|/);
        const timeMatch = body.match(/~TIME\|([\d.]+)~/);

        return {
            output: outputMatch?.[1]?.trim() ?? '',
            userLogs: logsMatch?.[1]?.trim() ?? '',
            timeMs: timeMatch ? parseFloat(timeMatch[1]) : null,
        };
    });
}

function formatDate(iso: string): string {
    const d = new Date(iso);
    return d.toLocaleString(undefined, {
        month: 'short', day: 'numeric', year: 'numeric',
        hour: '2-digit', minute: '2-digit',
    });
}

// ── Sub-component: expanded detail for one submission ─────────────────────

const SubmissionDetail = ({ sub }: { sub: Submission }) => {
    const cases = parseCaseOutput(sub.codeOutput);
    const [activeCase, setActiveCase] = useState(0);
    const current = cases[activeCase];
    const meta = statusMeta(sub.submissionStatus);

    return (
        <div className={styles.subDetail}>
            {/* Verdict summary row */}
            <div
                className={styles.subDetailVerdict}
                data-color={meta.color}
            >
                <span className={styles.subDetailVerdictLabel}>{meta.label}</span>
                {sub.failedOnTestCase && (
                    <span className={styles.subDetailVerdictHint}>
                        Failed on {sub.failedOnTestCase.hidden ? 'a hidden test case' : 'test case'}
                    </span>
                )}
            </div>

            {/* Code block */}
            <div>
                <p className={styles.subDetailSectionLabel}>Code</p>
                <pre className={styles.subCode}>{sub.submissionCode}</pre>
            </div>

            {/* Failed test case (if WA and not hidden) */}
            {sub.failedOnTestCase && !sub.failedOnTestCase.hidden && (
                <div>
                    <p className={styles.subDetailSectionLabel}>Failed Test Case</p>
                    <div className={styles.caseDetail}>
                        <div className={styles.ioGroup}>
                            <p className={styles.ioLabel}>Input</p>
                            <pre className={styles.ioBlock}>{sub.failedOnTestCase.input}</pre>
                        </div>
                        <div className={styles.ioGroup}>
                            <p className={styles.ioLabel}>Expected Output</p>
                            <pre className={styles.ioBlock}>{sub.failedOnTestCase.output}</pre>
                        </div>
                    </div>
                </div>
            )}

            {/* Per-case output breakdown */}
            {cases.length > 0 && (
                <div>
                    <p className={styles.subDetailSectionLabel}>
                        Test Case Output
                        <span className={styles.subDetailCaseCount}>{cases.length} cases</span>
                    </p>
                    <div className={styles.caseTabs} style={{ marginBottom: '0.75rem' }}>
                        {cases.map((_, idx) => (
                            <button
                                key={idx}
                                className={`${styles.caseTab} ${activeCase === idx ? styles.caseTabActive : ''}`}
                                onClick={() => setActiveCase(idx)}
                            >
                                Case {idx + 1}
                            </button>
                        ))}
                    </div>

                    {current && (
                        <div className={styles.caseDetail}>
                            <div className={styles.ioGroup}>
                                <p className={styles.ioLabel}>Output</p>
                                <pre className={styles.ioBlock}>
                                    {current.output || <span className={styles.emptyOutput}>(empty)</span>}
                                </pre>
                            </div>
                            {current.userLogs && (
                                <div className={styles.ioGroup}>
                                    <p className={styles.ioLabel}>Stdout</p>
                                    <pre className={styles.ioBlock}>{current.userLogs}</pre>
                                </div>
                            )}
                            {current.timeMs !== null && (
                                <p className={styles.execTime}>
                                    Runtime: {current.timeMs.toFixed(3)} ms
                                </p>
                            )}
                        </div>
                    )}
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

    const PAGE_SIZE = 10; // used to infer if more pages exist

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

    // Initial load
    useEffect(() => {
        setSubmissions([]);
        setPage(0);
        setExpandedId(null);
        fetchPage(0, true);
    }, [problemId]);

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
                            {/* Status */}
                            <span
                                className={styles.subStatus}
                                data-color={meta.color}
                            >
                                {meta.label}
                            </span>

                            {/* Language */}
                            <span className={styles.subLang}>{langLabel}</span>

                            {/* Date */}
                            <span className={styles.subDate}>{formatDate(sub.submissionDateTime)}</span>

                            {/* Chevron */}
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