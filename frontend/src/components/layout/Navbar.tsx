// src/components/layout/Navbar.tsx
import { useEffect, useRef, useState } from 'react';
import { Link, NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context';
import { useTheme } from '../../context/ThemeContext';
import styles from './Navbar.module.css';

// ── Inline SVG icons ──────────────────────────────────────────────────────
const IconSun = () => (
    <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <circle cx="12" cy="12" r="5" /><line x1="12" y1="1" x2="12" y2="3" /><line x1="12" y1="21" x2="12" y2="23" />
        <line x1="4.22" y1="4.22" x2="5.64" y2="5.64" /><line x1="18.36" y1="18.36" x2="19.78" y2="19.78" />
        <line x1="1" y1="12" x2="3" y2="12" /><line x1="21" y1="12" x2="23" y2="12" />
        <line x1="4.22" y1="19.78" x2="5.64" y2="18.36" /><line x1="18.36" y1="5.64" x2="19.78" y2="4.22" />
    </svg>
);
const IconMoon = () => (
    <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z" />
    </svg>
);
const IconMenu = () => (
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <line x1="3" y1="6" x2="21" y2="6" /><line x1="3" y1="12" x2="21" y2="12" /><line x1="3" y1="18" x2="21" y2="18" />
    </svg>
);
const IconX = () => (
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <line x1="18" y1="6" x2="6" y2="18" /><line x1="6" y1="6" x2="18" y2="18" />
    </svg>
);
const IconUser = () => (
    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" /><circle cx="12" cy="7" r="4" />
    </svg>
);
const IconLogOut = () => (
    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" /><polyline points="16 17 21 12 16 7" /><line x1="21" y1="12" x2="9" y2="12" />
    </svg>
);
const IconCode = () => (
    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <polyline points="16 18 22 12 16 6" /><polyline points="8 6 2 12 8 18" />
    </svg>
);
const IconList = () => (
    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <line x1="8" y1="6" x2="21" y2="6" /><line x1="8" y1="12" x2="21" y2="12" /><line x1="8" y1="18" x2="21" y2="18" />
        <line x1="3" y1="6" x2="3.01" y2="6" /><line x1="3" y1="12" x2="3.01" y2="12" /><line x1="3" y1="18" x2="3.01" y2="18" />
    </svg>
);
const IconCompass = () => (
    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <circle cx="12" cy="12" r="10" />
        <polygon points="16.24 7.76 14.12 14.12 7.76 16.24 9.88 9.88 16.24 7.76" />
    </svg>
);

// ── Nav link definitions ───────────────────────────────────────────────────
const NAV_LINKS = [
    { to: '/home', label: 'Explore', Icon: IconCompass },
    { to: '/problems', label: 'Problems', Icon: IconList },
    { to: '/contest', label: 'Contest', Icon: IconCode },
];

// ── Component ─────────────────────────────────────────────────────────────
export const Navbar = () => {
    const { user, loading, logout } = useAuth();
    const { theme, toggleTheme } = useTheme();
    const navigate = useNavigate();

    const [scrolled, setScrolled] = useState(false);
    const [dropdownOpen, setDropdownOpen] = useState(false);
    const [mobileOpen, setMobileOpen] = useState(false);

    const dropdownRef = useRef<HTMLDivElement>(null);

    // Scroll shadow
    useEffect(() => {
        const onScroll = () => setScrolled(window.scrollY > 4);
        window.addEventListener('scroll', onScroll, { passive: true });
        return () => window.removeEventListener('scroll', onScroll);
    }, []);

    // Close dropdown on outside click
    useEffect(() => {
        const handler = (e: MouseEvent) => {
            if (dropdownRef.current && !dropdownRef.current.contains(e.target as Node)) {
                setDropdownOpen(false);
            }
        };
        document.addEventListener('mousedown', handler);
        return () => document.removeEventListener('mousedown', handler);
    }, []);

    // Lock body scroll when mobile drawer open
    useEffect(() => {
        document.body.style.overflow = mobileOpen ? 'hidden' : '';
        return () => { document.body.style.overflow = ''; };
    }, [mobileOpen]);

    const handleLogout = () => {
        setDropdownOpen(false);
        setMobileOpen(false);
        logout();
    };

    return (
        <>
            <nav className={`${styles.nav} ${scrolled ? styles.scrolled : ''}`}>

                {/* ── Left ── */}
                <div className={styles.left}>
                    {/* Hamburger (mobile only) */}
                    <button
                        className={styles.hamburger}
                        onClick={() => setMobileOpen(true)}
                        aria-label="Open menu"
                    >
                        <IconMenu />
                    </button>

                    {/* Brand */}
                    <Link to="/" className={styles.brand}>
                        <div className={styles.brandMark}>{`{}`}</div>
                        <span className={styles.brandName}>ByteForge</span>
                    </Link>

                    {/* Desktop links */}
                    <div className={styles.navLinks}>
                        {NAV_LINKS.map(({ to, label }) => (
                            <NavLink
                                key={to}
                                to={to}
                                className={({ isActive }) =>
                                    `${styles.navLink} ${isActive ? styles.active : ''}`
                                }
                            >
                                {label}
                            </NavLink>
                        ))}
                    </div>
                </div>

                {/* ── Right ── */}
                <div className={styles.right}>
                    {/* Theme toggle */}
                    <button
                        className={styles.themeToggle}
                        onClick={toggleTheme}
                        aria-label="Toggle theme"
                    >
                        {theme === 'dark' ? <IconSun /> : <IconMoon />}
                    </button>

                    {/* Auth state */}
                    {!loading && (
                        user ? (
                            /* Avatar + dropdown */
                            <div className={styles.avatarWrap} ref={dropdownRef}>
                                <button
                                    className={styles.avatar}
                                    onClick={() => setDropdownOpen(o => !o)}
                                    aria-label="Account menu"
                                    aria-expanded={dropdownOpen}
                                >
                                    {user.name.charAt(0).toUpperCase()}
                                </button>

                                <div className={`${styles.dropdown} ${dropdownOpen ? styles.open : ''}`}>
                                    <div className={styles.dropdownHeader}>
                                        <div className={styles.dropdownName}>{user.name}</div>
                                        {user.email && (
                                            <div className={styles.dropdownEmail}>{user.email}</div>
                                        )}
                                    </div>
                                    <div className={styles.dropdownBody}>
                                        <Link
                                            to="/profile"
                                            className={styles.dropdownItem}
                                            onClick={() => setDropdownOpen(false)}
                                        >
                                            <IconUser /> Profile
                                        </Link>
                                        <div className={styles.dropdownDivider} />
                                        <button
                                            className={`${styles.dropdownItem} ${styles.danger}`}
                                            onClick={handleLogout}
                                        >
                                            <IconLogOut /> Sign out
                                        </button>
                                    </div>
                                </div>
                            </div>
                        ) : (
                            /* Sign in / Sign up */
                            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                                <button className={styles.btnSignIn} onClick={() => navigate('/login')}>
                                    Sign in
                                </button>
                                <button className={styles.btnSignUp} onClick={() => navigate('/signup')}>
                                    Sign up
                                </button>
                            </div>
                        )
                    )}
                </div>
            </nav>

            {/* ── Mobile drawer ── */}
            <div className={`${styles.mobileDrawer} ${mobileOpen ? styles.open : ''}`}>
                <div
                    className={styles.mobileOverlay}
                    onClick={() => setMobileOpen(false)}
                    aria-hidden="true"
                />
                <div className={styles.mobilePanel}>
                    {/* Panel header mirrors the navbar brand */}
                    <div className={styles.mobilePanelHeader}>
                        <Link to="/" className={styles.brand} onClick={() => setMobileOpen(false)}>
                            <div className={styles.brandMark}>{`{}`}</div>
                            <span className={styles.brandName}>ByteForge</span>
                        </Link>
                        <button
                            onClick={() => setMobileOpen(false)}
                            aria-label="Close menu"
                            style={{
                                marginLeft: 'auto',
                                background: 'none',
                                border: 'none',
                                color: 'var(--text-muted)',
                                cursor: 'pointer',
                                display: 'flex',
                                alignItems: 'center',
                                padding: '4px',
                            }}
                        >
                            <IconX />
                        </button>
                    </div>

                    {/* Links */}
                    <div className={styles.mobilePanelLinks}>
                        {NAV_LINKS.map(({ to, label, Icon }) => (
                            <NavLink
                                key={to}
                                to={to}
                                className={({ isActive }) =>
                                    `${styles.mobileNavLink} ${isActive ? styles.active : ''}`
                                }
                                onClick={() => setMobileOpen(false)}
                            >
                                <Icon /> {label}
                            </NavLink>
                        ))}
                    </div>

                    {/* Footer: user info or auth buttons */}
                    <div className={styles.mobilePanelFooter}>
                        {!loading && (
                            user ? (
                                <div style={{ display: 'flex', flexDirection: 'column', gap: '0.25rem' }}>
                                    <div style={{
                                        display: 'flex', alignItems: 'center', gap: '0.6rem',
                                        padding: '0.5rem 0.75rem',
                                        borderRadius: '8px',
                                        background: 'var(--bg-input)',
                                        marginBottom: '0.35rem',
                                    }}>
                                        <div className={styles.avatar} style={{ width: 28, height: 28, fontSize: 11, borderRadius: 6 }}>
                                            {user.name.charAt(0).toUpperCase()}
                                        </div>
                                        <div>
                                            <div style={{ fontSize: 13, fontWeight: 600, color: 'var(--text-primary)', lineHeight: 1.3 }}>
                                                {user.name}
                                            </div>
                                            {user.email && (
                                                <div style={{ fontSize: 11, color: 'var(--text-muted)' }}>{user.email}</div>
                                            )}
                                        </div>
                                    </div>
                                    <NavLink
                                        to="/profile"
                                        className={styles.mobileNavLink}
                                        onClick={() => setMobileOpen(false)}
                                    >
                                        <IconUser /> Profile
                                    </NavLink>
                                    <button
                                        className={`${styles.mobileNavLink} ${styles.danger}`}
                                        onClick={handleLogout}
                                        style={{ background: 'none', border: 'none', cursor: 'pointer', fontFamily: 'inherit', color: '#ef4444', width: '100%', textAlign: 'left' }}
                                    >
                                        <IconLogOut /> Sign out
                                    </button>
                                </div>
                            ) : (
                                <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
                                    <button
                                        className={styles.btnSignIn}
                                        style={{ width: '100%', justifyContent: 'center' }}
                                        onClick={() => { navigate('/login'); setMobileOpen(false); }}
                                    >
                                        Sign in
                                    </button>
                                    <button
                                        className={styles.btnSignUp}
                                        style={{ width: '100%', justifyContent: 'center' }}
                                        onClick={() => { navigate('/signup'); setMobileOpen(false); }}
                                    >
                                        Sign up
                                    </button>
                                </div>
                            )
                        )}
                    </div>
                </div>
            </div>
        </>
    );
};