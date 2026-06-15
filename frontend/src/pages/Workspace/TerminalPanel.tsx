// src/pages/Workspace/TerminalPanel.tsx
import { useState } from 'react';
import { submissionService } from '../../services';
import { ProblemDetail } from '../../types';
import styles from './Workspace.module.css';

interface TerminalPanelProps {
    problem: ProblemDetail;
    sourceCode: string;
    languageId: number;
    onClose: () => void;
}

// ── Shape of API responses ────────────────────────────────────────────────

interface RunCaseResult {
    codeOutput: string;
    executionTimeMs: number;
    input: string;
    status: string;       // "ACC" just means executed, NOT a comparison
    userOutput: string;
}

interface RunResponse {
    compileError: string | null;
    globalStatus: string;
    results: RunCaseResult[];
}

interface SubmitResponse {
    totalTestCases: number;
    totalPassed: number;
    status: string;           // "ACC" | "WA" | "TLE" | "RE" | ...
    hiddenTestCase: boolean | null;
    input: string | null;
    expectedOutput: string | null;
    codeOutput: string;
    error: string | null;
    userLogs: string;
}

type ResultState =
    | { kind: 'run';    data: RunResponse }
    | { kind: 'submit'; data: SubmitResponse }
    | { kind: 'error';  message: string };

// ── Helpers ───────────────────────────────────────────────────────────────

const STATUS_LABEL: Record<string, string> = {
    ACC: 'Accepted',
    WA:  'Wrong Answer',
    TLE: 'Time Limit Exceeded',
    MLE: 'Memory Limit Exceeded',
    RE:  'Runtime Error',
    CE:  'Compile Error',
};

const statusLabel = (s: string) => STATUS_LABEL[s] ?? s;

// ── Component ─────────────────────────────────────────────────────────────

export const TerminalPanel = ({ problem, sourceCode, languageId, onClose }: TerminalPanelProps) => {
    const [activeTab, setActiveTab]   = useState<'testcases' | 'result'>('testcases');
    const [activeCase, setActiveCase] = useState(0);
    const [loading, setLoading]       = useState(false);
    const [result, setResult]         = useState<ResultState | null>(null);

    // Active run-result case index (separate from testcase tab index)
    const [activeRunCase, setActiveRunCase] = useState(0);

    const customTestCases = problem.examples?.map(ex => ex.input) ?? [];

    const handleRun = async () => {
        setLoading(true);
        setActiveTab('result');
        setResult(null);
        setActiveRunCase(0);
        try {
            const res: RunResponse = await submissionService.runCode({
                problemId: Number(problem.id),
                languageId,
                sourceCode,
                customTestCases,
            });
            setResult({ kind: 'run', data: res });
        } catch (err: any) {
            setResult({ kind: 'error', message: err.response?.data?.message || 'Execution failed.' });
        } finally {
            setLoading(false);
        }
    };

    const handleSubmit = async () => {
        setLoading(true);
        setActiveTab('result');
        setResult(null);
        try {
            const res: SubmitResponse = await submissionService.submitCode({
                problemId: Number(problem.id),
                languageId,
                sourceCode,
            });
            setResult({ kind: 'submit', data: res });
        } catch (err: any) {
            setResult({ kind: 'error', message: err.response?.data?.message || 'Submission failed.' });
        } finally {
            setLoading(false);
        }
    };

    // ── Result renderers ──────────────────────────────────────────────────

    const renderRunResult = (data: RunResponse) => {
        if (data.compileError) {
            return (
                <div className={styles.resultSection}>
                    <div className={styles.verdictBanner} data-verdict="CE">
                        <span className={styles.verdictLabel}>Compile Error</span>
                    </div>
                    <pre className={styles.resultError}>{data.compileError}</pre>
                </div>
            );
        }

        const cases = data.results;
        const current = cases[activeRunCase];

        return (
            <div className={styles.resultSection}>
                {/* Case selector tabs */}
                <div className={styles.caseTabs}>
                    {cases.map((_, idx) => (
                        <button
                            key={idx}
                            className={`${styles.caseTab} ${activeRunCase === idx ? styles.caseTabActive : ''}`}
                            onClick={() => setActiveRunCase(idx)}
                        >
                            Case {idx + 1}
                        </button>
                    ))}
                </div>

                {/* Current case detail */}
                {current && (
                    <div className={styles.caseDetail}>
                        <div className={styles.ioGroup}>
                            <p className={styles.ioLabel}>Input</p>
                            <pre className={styles.ioBlock}>{current.input}</pre>
                        </div>
                        <div className={styles.ioGroup}>
                            <p className={styles.ioLabel}>Your Output</p>
                            <pre className={styles.ioBlock}>
                                {current.codeOutput || <span className={styles.emptyOutput}>(empty)</span>}
                            </pre>
                        </div>
                        {current.userOutput && (
                            <div className={styles.ioGroup}>
                                <p className={styles.ioLabel}>Stdout</p>
                                <pre className={styles.ioBlock}>{current.userOutput}</pre>
                            </div>
                        )}
                        <p className={styles.execTime}>
                            Runtime: {current.executionTimeMs.toFixed(3)} ms
                        </p>
                    </div>
                )}
            </div>
        );
    };

    const renderSubmitResult = (data: SubmitResponse) => {
        const accepted = data.status === 'ACC';

        return (
            <div className={styles.resultSection}>
                {/* Verdict banner */}
                <div
                    className={styles.verdictBanner}
                    data-verdict={accepted ? 'ACC' : 'FAIL'}
                >
                    <span className={styles.verdictLabel}>{statusLabel(data.status)}</span>
                    <span className={styles.verdictCount}>
                        {data.totalPassed} / {data.totalTestCases} testcases passed
                    </span>
                </div>

                {/* Failing case details (only on WA / RE / TLE) */}
                {!accepted && data.input && (
                    <div className={styles.caseDetail}>
                        <div className={styles.ioGroup}>
                            <p className={styles.ioLabel}>Input</p>
                            <pre className={styles.ioBlock}>{data.input}</pre>
                        </div>
                        <div className={styles.ioGroup}>
                            <p className={styles.ioLabel}>Expected Output</p>
                            <pre className={styles.ioBlock}>{data.expectedOutput}</pre>
                        </div>
                        <div className={styles.ioGroup}>
                            <p className={styles.ioLabel}>Your Output</p>
                            <pre className={styles.ioBlock}>
                                {data.codeOutput || <span className={styles.emptyOutput}>(empty)</span>}
                            </pre>
                        </div>
                        {data.userLogs && (
                            <div className={styles.ioGroup}>
                                <p className={styles.ioLabel}>Stdout</p>
                                <pre className={styles.ioBlock}>{data.userLogs}</pre>
                            </div>
                        )}
                        {data.error && (
                            <pre className={styles.resultError}>{data.error}</pre>
                        )}
                    </div>
                )}
            </div>
        );
    };

    const renderResult = () => {
        if (loading) {
            return (
                <div className={styles.executingState}>
                    <span className={styles.executingSpinner} />
                    Executing code…
                </div>
            );
        }
        if (!result) {
            return (
                <div className={styles.emptyResult}>
                    Run or submit your code to see results.
                </div>
            );
        }
        if (result.kind === 'error') {
            return <pre className={styles.resultError}>{result.message}</pre>;
        }
        if (result.kind === 'run') {
            return renderRunResult(result.data);
        }
        return renderSubmitResult(result.data);
    };

    // ── Render ────────────────────────────────────────────────────────────

    return (
        <div className={styles.panel}>
            {/* ── Tab bar ── */}
            <div className={styles.tabBar}>
                <button
                    className={`${styles.tab} ${activeTab === 'testcases' ? styles.tabActive : ''}`}
                    onClick={() => setActiveTab('testcases')}
                >
                    Testcases
                </button>
                <button
                    className={`${styles.tab} ${activeTab === 'result' ? styles.tabActive : ''}`}
                    onClick={() => setActiveTab('result')}
                >
                    Test Result
                    {loading && <span className={styles.tabSpinner} />}
                </button>
                <div style={{ marginLeft: 'auto' }}>
                    <button className={styles.terminalClose} onClick={onClose} aria-label="Close terminal">
                        ✕
                    </button>
                </div>
            </div>

            {/* ── Body ── */}
            <div className={styles.panelBody} style={{ padding: '1rem' }}>
                {activeTab === 'testcases' ? (
                    <>
                        <div className={styles.caseTabs}>
                            {problem.examples?.map((_, idx) => (
                                <button
                                    key={idx}
                                    className={`${styles.caseTab} ${activeCase === idx ? styles.caseTabActive : ''}`}
                                    onClick={() => setActiveCase(idx)}
                                >
                                    Case {idx + 1}
                                </button>
                            ))}
                        </div>
                        {problem.examples?.[activeCase] && (
                            <div className={styles.ioGroup}>
                                <p className={styles.ioLabel}>Input</p>
                                <pre className={styles.ioBlock}>{problem.examples[activeCase].input}</pre>
                            </div>
                        )}
                    </>
                ) : (
                    <div className={styles.resultWrap}>
                        {renderResult()}
                    </div>
                )}
            </div>

            {/* ── Footer ── */}
            <div className={styles.terminalFooter}>
                <span className={styles.consoleLabel}>Console</span>
                <div className={styles.footerActions}>
                    <button
                        className={`${styles.btnSm} ${styles.btnRun}`}
                        onClick={handleRun}
                        disabled={loading}
                    >
                        Run
                    </button>
                    <button
                        className={`${styles.btnSm} ${styles.btnSubmit}`}
                        onClick={handleSubmit}
                        disabled={loading}
                    >
                        Submit
                    </button>
                </div>
            </div>
        </div>
    );
};