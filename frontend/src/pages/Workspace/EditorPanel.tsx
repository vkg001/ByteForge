// src/pages/Workspace/EditorPanel.tsx
import Editor from '@monaco-editor/react';
import styles from './Workspace.module.css';

interface EditorPanelProps {
    sourceCode: string;
    onChange: (value: string | undefined) => void;
    languageId: number;
    setLanguageId: (id: number) => void;
}

const LANGUAGES = [
    { id: 54, label: 'C++',      monaco: 'cpp' },
    { id: 62, label: 'Java',     monaco: 'java' },
    { id: 71, label: 'Python 3', monaco: 'python' },
];

const getMonacoLang = (id: number) =>
    LANGUAGES.find(l => l.id === id)?.monaco ?? 'cpp';

export const EditorPanel = ({ sourceCode, onChange, languageId, setLanguageId }: EditorPanelProps) => {
    return (
        <div className={styles.panel}>
            {/* ── Toolbar ── */}
            <div className={styles.tabBar}>
                <div className={styles.tabBarEnd}>
                    <select
                        className={styles.langSelect}
                        value={languageId}
                        onChange={e => setLanguageId(Number(e.target.value))}
                    >
                        {LANGUAGES.map(l => (
                            <option key={l.id} value={l.id}>{l.label}</option>
                        ))}
                    </select>

                    <div className={styles.editorActions}>
                        <button className={`${styles.btnSm} ${styles.btnSecondary}`}>
                            Format
                        </button>
                        <button className={`${styles.btnSm} ${styles.btnGhost}`}>
                            Reset
                        </button>
                    </div>
                </div>
            </div>

            {/* ── Monaco ── */}
            <div className={styles.editorMount}>
                <Editor
                    height="100%"
                    language={getMonacoLang(languageId)}
                    theme="vs-dark"
                    value={sourceCode}
                    onChange={onChange}
                    options={{
                        minimap: { enabled: false },
                        fontSize: 13.5,
                        fontFamily: "'Fira Code', 'JetBrains Mono', monospace",
                        fontLigatures: true,
                        scrollBeyondLastLine: false,
                        smoothScrolling: true,
                        padding: { top: 16, bottom: 16 },
                        lineNumbersMinChars: 3,
                        renderLineHighlight: 'gutter',
                    }}
                />
            </div>
        </div>
    );
};
