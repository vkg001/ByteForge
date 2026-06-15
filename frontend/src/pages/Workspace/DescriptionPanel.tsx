// src/pages/Workspace/DescriptionPanel.tsx
import { useState } from 'react';
import ReactMarkdown from 'react-markdown';
import { ProblemDetail } from '../../types';
import { SubmissionsPanel } from './SubmissionsPanel';
import styles from './Workspace.module.css';

interface DescriptionPanelProps {
    problem: ProblemDetail;
}

const diffBadgeClass = (d: string) => {
    if (d === 'EASY') return styles.diffEasy;
    if (d === 'MEDIUM') return styles.diffMedium;
    if (d === 'HARD') return styles.diffHard;
    return '';
};

const diffLabel = (d: string) =>
    d.charAt(0) + d.slice(1).toLowerCase();

export const DescriptionPanel = ({ problem }: DescriptionPanelProps) => {
    const [activeTab, setActiveTab] = useState<'description' | 'submissions'>('description');

    return (
        <div className={styles.panel}>
            {/* ── Tab bar ── */}
            <div className={styles.tabBar}>
                <button
                    className={`${styles.tab} ${activeTab === 'description' ? styles.tabActive : ''}`}
                    onClick={() => setActiveTab('description')}
                >
                    Description
                </button>
                <button
                    className={`${styles.tab} ${activeTab === 'submissions' ? styles.tabActive : ''}`}
                    onClick={() => setActiveTab('submissions')}
                >
                    Submissions
                </button>
            </div>

            {/* ── Body ── */}
            <div className={`${styles.panelBody} ${activeTab === 'description' ? styles.descBody : styles.subBody}`}>
                {activeTab === 'description' ? (
                    <>
                        {/* Title + meta */}
                        <div className={styles.problemMeta}>
                            <h1 className={styles.problemTitle}>
                                {problem.id}. {problem.problemTitle}
                            </h1>
                            <div className={styles.metaRow}>
                                <span className={`${styles.diffBadge} ${diffBadgeClass(problem.problemDifficulty)}`}>
                                    {diffLabel(problem.problemDifficulty)}
                                </span>
                                {problem.topics?.map(topic => (
                                    <span key={topic} className={styles.topicTag}>{topic}</span>
                                ))}
                            </div>
                        </div>

                        {/* Statement — rendered as markdown */}
                        <div className={styles.mdProse}>
                            <ReactMarkdown>{problem.problemStatement}</ReactMarkdown>
                        </div>

                        {/* Examples */}
                        {problem.examples && problem.examples.length > 0 && (
                            <div className={styles.examplesWrap}>
                                {problem.examples.map((example, idx) => (
                                    <div key={idx}>
                                        <p className={styles.exampleLabel}>Example {idx + 1}:</p>
                                        <div className={styles.exampleBlock}>
                                            <div className={styles.exampleLine}>
                                                <span className={styles.exampleKey}>Input: </span>
                                                {example.input}
                                            </div>
                                            <div className={styles.exampleLine}>
                                                <span className={styles.exampleKey}>Output: </span>
                                                {example.output}
                                            </div>
                                            {example.explanation && (
                                                <div className={styles.exampleExplanation}>
                                                    <span className={styles.exampleKey}>Explanation: </span>
                                                    {example.explanation}
                                                </div>
                                            )}
                                        </div>
                                    </div>
                                ))}
                            </div>
                        )}

                        {/* Constraints — each item is markdown */}
                        {problem.constraints && problem.constraints.length > 0 && (
                            <div>
                                <p className={styles.sectionLabel}>Constraints:</p>
                                <ul className={styles.constraintsList}>
                                    {problem.constraints.map((c, idx) => (
                                        <li key={idx} className={styles.constraintItem}>
                                            <ReactMarkdown
                                                components={{
                                                    p: ({ children }) => <>{children}</>,
                                                }}
                                            >
                                                {c}
                                            </ReactMarkdown>
                                        </li>
                                    ))}
                                </ul>
                            </div>
                        )}
                    </>
                ) : (
                    <SubmissionsPanel problemId={String(problem.id)} />
                )}
            </div>
        </div>
    );
};