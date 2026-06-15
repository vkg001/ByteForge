// src/pages/Workspace/Workspace.tsx
import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { Group, Panel, Separator } from 'react-resizable-panels';
import { problemService } from '../../services';
import { ProblemDetail } from '../../types';
import { DescriptionPanel } from './DescriptionPanel';
import { EditorPanel } from './EditorPanel';
import { TerminalPanel } from './TerminalPanel';
import styles from './Workspace.module.css';

export const Workspace = () => {
    const { problemId } = useParams<{ problemId: string }>();
    const [problem, setProblem]               = useState<ProblemDetail | null>(null);
    const [loading, setLoading]               = useState(true);
    const [sourceCode, setSourceCode]         = useState('');
    const [languageId, setLanguageId]         = useState(54);      // default: C++
    const [isTerminalOpen, setIsTerminalOpen] = useState(true);

    useEffect(() => {
        if (!problemId) return;
        const fetchProblem = async () => {
            setLoading(true);
            try {
                const data = await problemService.getProblemById(problemId);
                setProblem(data);
                const boilerplate = data.boilerPlateCodes?.find(
                    (b: any) => b.languageCode === languageId
                );
                if (boilerplate) setSourceCode(boilerplate.userCode);
            } catch (err) {
                console.error('Failed to load problem', err);
            } finally {
                setLoading(false);
            }
        };
        fetchProblem();
    }, [problemId, languageId]);

    if (loading || !problem) {
        return (
            <div className={styles.loadingPage}>
                <div className={styles.spinnerRing} />
            </div>
        );
    }

    return (
        <div className={styles.workspace}>
            <Group orientation="horizontal">
                {/* ── Left: Description ── */}
                <Panel defaultSize={45} minSize={28}>
                    <DescriptionPanel problem={problem} />
                </Panel>

                <Separator className={styles.separatorH} />

                {/* ── Right: Editor + Terminal ── */}
                <Panel defaultSize={55} minSize={30}>
                    <Group orientation="vertical">
                        <Panel defaultSize={isTerminalOpen ? 68 : 100} minSize={20}>
                            <EditorPanel
                                sourceCode={sourceCode}
                                onChange={val => setSourceCode(val ?? '')}
                                languageId={languageId}
                                setLanguageId={setLanguageId}
                            />
                        </Panel>

                        {isTerminalOpen && (
                            <>
                                <Separator className={styles.separatorV} />
                                <Panel defaultSize={32} minSize={12}>
                                    <TerminalPanel
                                        problem={problem}
                                        sourceCode={sourceCode}
                                        languageId={languageId}
                                        onClose={() => setIsTerminalOpen(false)}
                                    />
                                </Panel>
                            </>
                        )}
                    </Group>
                </Panel>
            </Group>
        </div>
    );
};