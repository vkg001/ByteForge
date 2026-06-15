import { useState, useEffect, useRef } from "react";

/* ─── TYPEWRITER DATA ────────────────────────────────────── */
const CODE_LINES = [
    { text: "# 001 · Two Sum", cls: "c-cm" },
    { text: "# Given nums and target, return indices of", cls: "c-cm" },
    { text: "# two numbers that add up to target.", cls: "c-cm" },
    { text: "" },
    {
        text: "def two_sum(nums, target):",
        parts: [
            { text: "def ", cls: "c-kw" },
            { text: "two_sum", cls: "c-fn" },
            { text: "(nums, target):", cls: "c-var" },
        ],
    },
    {
        text: "    seen = {}",
        parts: [
            { text: "    seen", cls: "c-var" },
            { text: " = ", cls: "c-op" },
            { text: "{}", cls: "c-def" },
        ],
    },
    {
        text: "    for i, num in enumerate(nums):",
        parts: [
            { text: "    ", cls: "" },
            { text: "for ", cls: "c-kw" },
            { text: "i, num ", cls: "c-var" },
            { text: "in ", cls: "c-kw" },
            { text: "enumerate", cls: "c-fn" },
            { text: "(nums):", cls: "c-var" },
        ],
    },
    {
        text: "        complement = target - num",
        parts: [
            { text: "        complement", cls: "c-var" },
            { text: " = ", cls: "c-op" },
            { text: "target", cls: "c-var" },
            { text: " - ", cls: "c-op" },
            { text: "num", cls: "c-var" },
        ],
    },
    {
        text: "        if complement in seen:",
        parts: [
            { text: "        ", cls: "" },
            { text: "if ", cls: "c-kw" },
            { text: "complement ", cls: "c-var" },
            { text: "in ", cls: "c-kw" },
            { text: "seen:", cls: "c-var" },
        ],
    },
    {
        text: "            return [seen[complement], i]",
        parts: [
            { text: "            ", cls: "" },
            { text: "return ", cls: "c-kw" },
            { text: "[seen[complement], i]", cls: "c-var" },
        ],
    },
    {
        text: "        seen[num] = i",
        parts: [
            { text: "        seen[num]", cls: "c-var" },
            { text: " = ", cls: "c-op" },
            { text: "i", cls: "c-var" },
        ],
    },
    { text: "" },
    { text: "# ✓ Runtime: O(n)  |  Space: O(n)", cls: "c-cm" },
];

/* ─── ANIMATED CODE WINDOW ───────────────────────────────── */
function CodeWindow() {
    const [renderedLines, setRenderedLines] = useState([]);
    const [cursorVisible, setCursorVisible] = useState(true);
    const lineRef = useRef(0);
    const charRef = useRef(0);
    const timerRef = useRef(null);

    useEffect(() => {
        function buildPartialLine(line, upTo) {
            if (!line.parts) {
                return <span className={line.cls || "c-var"}>{line.text.slice(0, upTo)}</span>;
            }
            const parts = [];
            let acc = 0;
            for (let i = 0; i < line.parts.length; i++) {
                const p = line.parts[i];
                const end = acc + p.text.length;
                if (acc >= upTo) break;
                parts.push(
                    <span key={i} className={p.cls || ""}>
                        {p.text.slice(0, upTo - acc)}
                    </span>
                );
                acc = end;
            }
            return <>{parts}</>;
        }

        function tick() {
            const li = lineRef.current;
            const ci = charRef.current;

            if (li >= CODE_LINES.length) {
                setCursorVisible(false);
                timerRef.current = setTimeout(() => {
                    setRenderedLines([]);
                    lineRef.current = 0;
                    charRef.current = 0;
                    setCursorVisible(true);
                    timerRef.current = setTimeout(tick, 100);
                }, 3200);
                return;
            }

            const line = CODE_LINES[li];
            const full = line.text;

            if (ci === 0) {
                setRenderedLines((prev) => [
                    ...prev,
                    { key: li, content: buildPartialLine(line, 0) },
                ]);
            }

            const next = ci + 1;
            if (next > full.length) {
                lineRef.current = li + 1;
                charRef.current = 0;
                timerRef.current = setTimeout(tick, full === "" ? 40 : 80);
            } else {
                setRenderedLines((prev) =>
                    prev.map((r, idx) =>
                        idx === prev.length - 1
                            ? { ...r, content: buildPartialLine(line, next) }
                            : r
                    )
                );
                charRef.current = next;
                const delay = full === "" ? 40 : Math.random() * 35 + 18;
                timerRef.current = setTimeout(tick, delay);
            }
        }

        timerRef.current = setTimeout(tick, 600);
        return () => clearTimeout(timerRef.current);
    }, []);

    return (
        <div style={styles.codeWindow}>
            <div style={styles.codeTitleBar}>
                <span style={{ ...styles.codeDot, background: "#ff5f57" }} />
                <span style={{ ...styles.codeDot, background: "#febc2e" }} />
                <span style={{ ...styles.codeDot, background: "#28c840" }} />
                <span style={styles.codeFilename}>two_sum.py</span>
            </div>
            <div style={styles.codeBody}>
                <div style={styles.lineNums}>
                    {Array.from({ length: Math.max(renderedLines.length, 1) }, (_, i) => (
                        <div key={i} style={styles.lineNum}>{i + 1}</div>
                    ))}
                </div>
                <div style={styles.codeContent}>
                    {renderedLines.map((r) => (
                        <div key={r.key} style={{ whiteSpace: "pre", fontFamily: 'var(--font-mono)', fontSize: 12.5, lineHeight: 1.65 }}>
                            {r.content}
                        </div>
                    ))}
                    {cursorVisible && (
                        <span
                            style={{
                                display: "inline-block",
                                width: 2,
                                height: 14,
                                background: 'var(--amber)',
                                verticalAlign: "text-bottom",
                                animation: "bf-blink 1s step-end infinite",
                            }}
                        />
                    )}
                </div>
            </div>
        </div>
    );
}

/* ─── FEATURES DATA ──────────────────────────────────────── */
const FEATURES = [
    { icon: "⌨", title: "Browser-native editor", desc: "Syntax highlighting, autocomplete, and vim keybindings. Feels like your local setup, runs anywhere." },
    { icon: "⚙", title: "Smart problem filters", desc: "Filter by difficulty, topic, company, or your own solving history. Find the exact problem you need." },
    { icon: "📊", title: "Submission heatmap", desc: "365 days of activity at a glance. Streaks, totals, and momentum — all visible on your profile." },
    { icon: "⚡", title: "Instant test feedback", desc: "Run against hidden and custom test cases in milliseconds. No waiting, no guessing what failed." },
    { icon: "🏆", title: "Difficulty tiers", desc: "Easy, Medium, Hard — each calibrated to real interview difficulty, not arbitrary labels." },
    { icon: "💬", title: "Discussion threads", desc: "Every problem has a discussion board. Post your solution, read others, or ask for a nudge." },
];

/* ─── PROBLEMS DATA ─────────────────────────────────────── */
const PROBLEMS = [
    { idx: "001", name: "Two Sum", diff: "Easy", acc: "49.2%", tag: "Hash Map" },
    { idx: "053", name: "Maximum Subarray", diff: "Medium", acc: "50.1%", tag: "DP" },
    { idx: "124", name: "Binary Tree Max Path", diff: "Hard", acc: "38.6%", tag: "Tree / DFS" },
    { idx: "206", name: "Reverse Linked List", diff: "Easy", acc: "73.4%", tag: "Linked List" },
    { idx: "300", name: "Longest Increasing Subseq.", diff: "Medium", acc: "52.7%", tag: "DP / Binary Search" },
];

const DIFF_STYLES = {
    Easy: { color: "var(--diff-easy)", background: "var(--diff-easy-bg)" },
    Medium: { color: "var(--diff-med)", background: "var(--diff-med-bg)" },
    Hard: { color: "var(--diff-hard)", background: "var(--diff-hard-bg)" },
};

/* ─── FEATURE CARD with hover ────────────────────────────── */
function FeatureCard({ icon, title, desc }) {
    const [hovered, setHovered] = useState(false);
    return (
        <div
            onMouseEnter={() => setHovered(true)}
            onMouseLeave={() => setHovered(false)}
            style={{
                ...styles.featureCard,
                background: hovered ? 'var(--card-hover)' : 'var(--bg)',
            }}
        >
            <div style={styles.featureIcon}>{icon}</div>
            <h3 style={styles.featureTitle}>{title}</h3>
            <p style={styles.featureDesc}>{desc}</p>
        </div>
    );
}

/* ─── PROBLEM ROW with hover ─────────────────────────────── */
function ProblemRow({ idx, name, diff, acc, tag }) {
    const [hovered, setHovered] = useState(false);
    return (
        <div
            onMouseEnter={() => setHovered(true)}
            onMouseLeave={() => setHovered(false)}
            style={{
                ...styles.problemRow,
                background: hovered ? 'var(--card-hover)' : "transparent",
            }}
        >
            <div style={styles.problemName}>
                <span style={styles.problemIdx}>{idx}</span>
                {name}
            </div>
            <div>
                <span style={{ ...styles.diffBadge, ...DIFF_STYLES[diff] }}>{diff}</span>
            </div>
            <div style={styles.problemAcc}>{acc}</div>
            <div>
                <span style={styles.problemTag}>{tag}</span>
            </div>
        </div>
    );
}

/* ─── NAV BUTTON helpers ─────────────────────────────────── */
function BtnGhost({ children, style }) {
    const [h, setH] = useState(false);
    return (
        <button
            onMouseEnter={() => setH(true)}
            onMouseLeave={() => setH(false)}
            style={{
                ...styles.btnGhost,
                color: h ? 'var(--text)' : 'var(--text-dim)',
                borderColor: h ? 'var(--text-mid)' : 'var(--border2)',
                ...style,
            }}
        >
            {children}
        </button>
    );
}
function BtnPrimary({ children, style }) {
    const [h, setH] = useState(false);
    return (
        <button
            onMouseEnter={() => setH(true)}
            onMouseLeave={() => setH(false)}
            style={{
                ...styles.btnPrimary,
                background: h ? 'var(--amber-hover)' : 'var(--amber)',
                ...style,
            }}
        >
            {children}
        </button>
    );
}

/* ─── MAIN PAGE ──────────────────────────────────────────── */
export default function HomePage() {
    return (
        <div className="bf-root">
            <section style={styles.hero}>
                <div>
                    <div style={styles.heroEyebrow}>
                        <span style={styles.eyebrowDot} />
                        Now in public beta
                    </div>
                    <h1 style={styles.heroH1}>
                        Write code.<br />
                        Break things.<br />
                        <span style={styles.heroAccent}>{"{ get better }"}</span>
                    </h1>
                    <p style={styles.heroSub}>
                        ByteForge gives you 500+ curated problems, a built-in editor, and a
                        submission heatmap — everything you need to go from rusty to ready.
                    </p>
                    <div style={styles.heroCtas}>
                        <CtaPrimary>Start solving — it's free</CtaPrimary>
                        <CtaSecondary>
                            <span style={{ fontSize: 13 }}>▶</span>
                            See how it works
                        </CtaSecondary>
                    </div>
                </div>
                <CodeWindow />
            </section>

            <div style={styles.statsBar}>
                <div style={styles.statsInner}>
                    {[
                        { num: "500", sup: "+", label: "Curated problems" },
                        { num: "12", sup: "k", label: "Active users" },
                        { num: "94", sup: "%", label: "Interview success rate" },
                        { num: "8", sup: "", label: "Languages supported" },
                    ].map((s, i) => (
                        <div
                            key={i}
                            style={{
                                ...styles.stat,
                                paddingLeft: i === 0 ? 0 : undefined,
                                borderRight: i < 3 ? `1px solid var(--border)` : "none",
                            }}
                        >
                            <div style={styles.statNum}>
                                {s.num}
                                {s.sup && <span style={styles.statSup}>{s.sup}</span>}
                            </div>
                            <div style={styles.statLabel}>{s.label}</div>
                        </div>
                    ))}
                </div>
            </div>

            <section style={styles.features}>
                <div style={styles.sectionEyebrow}>Why ByteForge</div>
                <h2 style={styles.sectionTitle}>Everything in one place</h2>
                <p style={styles.sectionSub}>
                    No tab-switching, no subscriptions for the basics. Just you and the problem.
                </p>
                <div style={styles.featuresGrid}>
                    {FEATURES.map((f) => (
                        <FeatureCard key={f.title} {...f} />
                    ))}
                </div>
            </section>

            <div style={styles.problemsSection}>
                <div style={styles.problemsInner}>
                    <div style={styles.sectionEyebrow}>Problem set preview</div>
                    <h2 style={styles.sectionTitle}>500+ problems. Endless variations.</h2>
                    <p style={styles.sectionSub}>
                        From linked lists to dynamic programming — organised by topic so you
                        can train deliberately.
                    </p>

                    <div style={styles.problemTable}>
                        <div style={styles.problemHeader}>
                            <span>Problem</span>
                            <span>Difficulty</span>
                            <span>Acceptance</span>
                            <span>Topic</span>
                        </div>
                        {PROBLEMS.map((p) => (
                            <ProblemRow key={p.idx} {...p} />
                        ))}
                        <div
                            style={{
                                ...styles.problemRow,
                                background: 'var(--card-hover)',
                                cursor: "default",
                                borderBottom: "none",
                            }}
                        >
                            <div
                                style={{
                                    gridColumn: "1 / -1",
                                    fontSize: 12.5,
                                    fontStyle: "italic",
                                    color: 'var(--text-ghost)',
                                    display: "flex",
                                    alignItems: "center",
                                    gap: 6,
                                }}
                            >
                                <span>🔒</span>
                                495 more problems — sign up to unlock
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div style={{ borderTop: `1px solid var(--border)` }}>
                <div style={styles.ctaStrip}>
                    <div>
                        <h2 style={styles.ctaTitle}>Ready to start forging?</h2>
                        <p style={styles.ctaSub}>Free forever for the essentials. No credit card.</p>
                    </div>
                    <div style={styles.ctaActions}>
                        <BtnGhost style={{ fontSize: 14, padding: "0.6rem 1.25rem" }}>Sign in</BtnGhost>
                        <BtnPrimary style={{ fontSize: 14, padding: "0.6rem 1.4rem" }}>Create account</BtnPrimary>
                    </div>
                </div>
            </div>

            <footer style={styles.footer}>
                <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
                    <div
                        style={{
                            ...styles.brandMark,
                            width: 20,
                            height: 20,
                            fontSize: 9,
                            borderRadius: 4,
                        }}
                    >
                        {"{}"}
                    </div>
                    <span style={{ fontSize: 12, color: 'var(--text-dim)' }}>ByteForge © 2026</span>
                </div>
                <div style={{ display: "flex", gap: "1.25rem" }}>
                    {["Privacy", "Terms", "GitHub"].map((l) => (
                        <FooterLink key={l}>{l}</FooterLink>
                    ))}
                </div>
            </footer>
        </div>
    );
}

function CtaPrimary({ children }) {
    const [h, setH] = useState(false);
    const [active, setActive] = useState(false);
    return (
        <button
            onMouseEnter={() => setH(true)}
            onMouseLeave={() => setH(false)}
            onMouseDown={() => setActive(true)}
            onMouseUp={() => setActive(false)}
            style={{
                padding: "0.65rem 1.5rem",
                fontSize: 14,
                fontWeight: 600,
                color: '#fff',
                background: h ? 'var(--amber-hover)' : 'var(--amber)',
                border: "none",
                borderRadius: 8,
                cursor: "pointer",
                fontFamily: 'var(--font-sans)',
                transform: active ? "scale(0.98)" : "scale(1)",
                transition: "background 0.15s, transform 0.1s",
            }}
        >
            {children}
        </button>
    );
}

function CtaSecondary({ children }) {
    const [h, setH] = useState(false);
    return (
        <button
            onMouseEnter={() => setH(true)}
            onMouseLeave={() => setH(false)}
            style={{
                display: "flex",
                alignItems: "center",
                gap: 6,
                padding: "0.65rem 1.25rem",
                fontSize: 14,
                fontWeight: 500,
                color: h ? 'var(--text)' : 'var(--text-dim)',
                background: "transparent",
                border: `1px solid ${h ? 'var(--text-mid)' : 'var(--border2)'}`,
                borderRadius: 8,
                cursor: "pointer",
                fontFamily: 'var(--font-sans)',
                transition: "color 0.15s, border-color 0.15s",
            }}
        >
            {children}
        </button>
    );
}

function FooterLink({ children }) {
    const [h, setH] = useState(false);
    return (
        <a
            href="#"
            onMouseEnter={() => setH(true)}
            onMouseLeave={() => setH(false)}
            style={{
                fontSize: 12,
                color: h ? 'var(--text-low)' : 'var(--text-dim)',
                textDecoration: "none",
                transition: "color 0.15s",
            }}
        >
            {children}
        </a>
    );
}

/* ─── STYLES OBJECT ──────────────────────────────────────── */
const styles = {
    brandMark: {
        width: 28,
        height: 28,
        background: 'var(--amber)',
        borderRadius: 6,
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        fontFamily: 'var(--font-mono)',
        fontSize: 11,
        fontWeight: 700,
        color: '#111',
        letterSpacing: -1,
    },
    btnGhost: {
        padding: "0.35rem 0.85rem",
        fontSize: 13,
        fontWeight: 500,
        background: "transparent",
        border: "1px solid",
        borderRadius: 7,
        cursor: "pointer",
        fontFamily: 'var(--font-sans)',
        transition: "color 0.15s, border-color 0.15s",
    },
    btnPrimary: {
        padding: "0.35rem 0.9rem",
        fontSize: 13,
        fontWeight: 600,
        color: '#fff',
        border: "none",
        borderRadius: 7,
        cursor: "pointer",
        fontFamily: 'var(--font-sans)',
        transition: "background 0.15s",
    },
    hero: {
        display: "grid",
        gridTemplateColumns: "1fr 1fr",
        gap: "3rem",
        alignItems: "center",
        maxWidth: 1100,
        margin: "0 auto",
        padding: "5rem 2rem 4rem",
    },
    heroEyebrow: {
        display: "inline-flex",
        alignItems: "center",
        gap: 6,
        padding: "4px 10px",
        background: "rgba(245, 159, 0, 0.1)",
        border: "1px solid rgba(245, 159, 0, 0.25)",
        borderRadius: 999,
        fontSize: 11.5,
        fontWeight: 600,
        color: 'var(--amber)',
        letterSpacing: "0.06em",
        textTransform: "uppercase",
        marginBottom: "1.25rem",
    },
    eyebrowDot: {
        width: 5,
        height: 5,
        borderRadius: "50%",
        background: 'var(--amber)',
        display: "inline-block",
        animation: "bf-pulse 2s ease infinite",
    },
    heroH1: {
        fontSize: 48,
        fontWeight: 800,
        lineHeight: 1.1,
        letterSpacing: "-0.04em",
        color: 'var(--text-hi)',
        margin: "0 0 1.25rem",
    },
    heroAccent: {
        fontFamily: 'var(--font-mono)',
        color: 'var(--amber)',
        fontSize: 42,
        fontWeight: 700,
    },
    heroSub: {
        fontSize: 15.5,
        color: 'var(--text-low)',
        lineHeight: 1.7,
        margin: "0 0 2rem",
        maxWidth: 420,
    },
    heroCtas: { display: "flex", alignItems: "center", gap: "0.75rem" },
    codeWindow: {
        background: 'var(--bg-deep)',
        border: `1px solid var(--border2)`,
        borderRadius: 12,
        overflow: "hidden",
        fontFamily: 'var(--font-mono)',
    },
    codeTitleBar: {
        display: "flex",
        alignItems: "center",
        gap: 6,
        padding: "10px 14px",
        background: 'var(--card)',
        borderBottom: `1px solid var(--border2)`,
    },
    codeDot: {
        width: 10,
        height: 10,
        borderRadius: "50%",
        display: "inline-block",
    },
    codeFilename: {
        marginLeft: 6,
        fontSize: 11,
        color: 'var(--text-mid)',
        fontWeight: 500,
        fontFamily: 'var(--font-mono)',
    },
    codeBody: {
        padding: "1.25rem 1.25rem 1.25rem 0",
        display: "flex",
        gap: 0,
        minHeight: 260,
    },
    lineNums: {
        padding: "0 0.75rem",
        display: "flex",
        flexDirection: "column",
        textAlign: "right",
        userSelect: "none",
        minWidth: 36,
    },
    lineNum: {
        fontSize: 11.5,
        color: 'var(--text-ghost)',
        lineHeight: 1.65,
        fontFamily: 'var(--font-mono)',
    },
    codeContent: { flex: 1 },
    statsBar: {
        borderTop: `1px solid var(--border)`,
        borderBottom: `1px solid var(--border)`,
        background: 'var(--bg-deep)',
    },
    statsInner: {
        maxWidth: 1100,
        margin: "0 auto",
        padding: "1.5rem 2rem",
        display: "grid",
        gridTemplateColumns: "repeat(4, 1fr)",
    },
    stat: { padding: "0 2rem" },
    statNum: {
        fontSize: 26,
        fontWeight: 800,
        color: 'var(--text)',
        letterSpacing: "-0.03em",
        fontFamily: 'var(--font-mono)',
        lineHeight: 1,
    },
    statSup: { color: 'var(--amber)', fontSize: 20 },
    statLabel: { fontSize: 12, color: 'var(--text-dim)', marginTop: 4, fontWeight: 500 },
    features: {
        maxWidth: 1100,
        margin: "0 auto",
        padding: "4rem 2rem",
    },
    sectionEyebrow: {
        fontSize: 11.5,
        fontWeight: 600,
        color: 'var(--amber)',
        letterSpacing: "0.08em",
        textTransform: "uppercase",
        marginBottom: "0.75rem",
    },
    sectionTitle: {
        fontSize: 30,
        fontWeight: 800,
        color: 'var(--text)',
        letterSpacing: "-0.03em",
        margin: "0 0 0.5rem",
    },
    sectionSub: {
        fontSize: 14.5,
        color: 'var(--text-dim)',
        margin: "0 0 3rem",
        maxWidth: 480,
    },
    featuresGrid: {
        display: "grid",
        gridTemplateColumns: "repeat(3, 1fr)",
        gap: "1px",
        background: 'var(--border)',
        border: `1px solid var(--border)`,
        borderRadius: 12,
        overflow: "hidden",
    },
    featureCard: {
        padding: "1.75rem",
        transition: "background 0.15s",
    },
    featureIcon: {
        width: 36,
        height: 36,
        background: "rgba(245, 159, 0, 0.1)",
        border: "1px solid rgba(245, 159, 0, 0.2)",
        borderRadius: 8,
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        marginBottom: "1rem",
        fontSize: 16,
    },
    featureTitle: {
        fontSize: 14.5,
        fontWeight: 700,
        color: 'var(--text)',
        margin: "0 0 0.4rem",
        letterSpacing: "-0.01em",
    },
    featureDesc: { fontSize: 13, color: 'var(--text-dim)', lineHeight: 1.6, margin: 0 },
    problemsSection: {
        background: 'var(--bg-deep)',
        borderTop: `1px solid var(--border)`,
    },
    problemsInner: {
        maxWidth: 1100,
        margin: "0 auto",
        padding: "4rem 2rem",
    },
    problemTable: {
        width: "100%",
        border: `1px solid var(--border)`,
        borderRadius: 12,
        overflow: "hidden",
    },
    problemHeader: {
        display: "grid",
        gridTemplateColumns: "2fr 1fr 1fr 1fr",
        padding: "0.6rem 1.25rem",
        background: 'var(--card)',
        borderBottom: `1px solid var(--border)`,
        fontSize: 11,
        fontWeight: 600,
        color: 'var(--text-ghost)',
        textTransform: "uppercase",
        letterSpacing: "0.07em",
    },
    problemRow: {
        display: "grid",
        gridTemplateColumns: "2fr 1fr 1fr 1fr",
        padding: "0.85rem 1.25rem",
        borderBottom: `1px solid var(--border2)`,
        alignItems: "center",
        transition: "background 0.1s",
        cursor: "pointer",
    },
    problemName: {
        fontSize: 13.5,
        fontWeight: 500,
        color: 'var(--text-mid)',
        display: "flex",
        alignItems: "center",
        gap: 8,
    },
    problemIdx: {
        fontSize: 11.5,
        color: 'var(--text-ghost)',
        fontFamily: 'var(--font-mono)',
        minWidth: 28,
    },
    diffBadge: {
        fontSize: 11.5,
        fontWeight: 600,
        padding: "2px 8px",
        borderRadius: 4,
        display: "inline-block",
    },
    problemAcc: {
        fontSize: 13,
        color: 'var(--text-dim)',
        fontFamily: 'var(--font-mono)',
    },
    problemTag: {
        fontSize: 11,
        color: 'var(--text-ghost)',
        background: 'var(--card)',
        border: `1px solid var(--border2)`,
        padding: "2px 7px",
        borderRadius: 4,
        display: "inline-block",
    },
    ctaStrip: {
        maxWidth: 1100,
        margin: "0 auto",
        padding: "4rem 2rem",
        display: "flex",
        alignItems: "center",
        justifyContent: "space-between",
        gap: "2rem",
    },
    ctaTitle: {
        fontSize: 28,
        fontWeight: 800,
        color: 'var(--text)',
        letterSpacing: "-0.03em",
        margin: "0 0 0.4rem",
    },
    ctaSub: { fontSize: 14, color: 'var(--text-dim)', margin: 0 },
    ctaActions: { display: "flex", gap: "0.75rem", flexShrink: 0 },
    footer: {
        borderTop: `1px solid var(--border)`,
        padding: "1.5rem 2rem",
        display: "flex",
        alignItems: "center",
        justifyContent: "space-between",
        maxWidth: 1100,
        margin: "0 auto",
    },
};