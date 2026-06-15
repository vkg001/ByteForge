// src/pages/Auth/Signup.tsx
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { authService } from '../../services';
import { useTheme } from '../../context/ThemeContext';
import styles from './Auth.module.css';

export const Signup = () => {
    const navigate = useNavigate();
    const { theme, toggleTheme } = useTheme();
    const [step, setStep] = useState<1 | 2>(1);
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const [showPassword, setShowPassword] = useState(false);
    const [formData, setFormData] = useState({
        name: '', email: '', password: '', otp: '',
    });

    const handleInitSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        setError('');
        try {
            await authService.signupInit({ name: formData.name, email: formData.email, password: formData.password });
            setStep(2);
        } catch (err: any) {
            setError(err.response?.data?.message || 'Failed to send verification code.');
        } finally {
            setLoading(false);
        }
    };

    const handleCompleteSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        setError('');
        try {
            await authService.signupComplete({ email: formData.email, otp: formData.otp });
            navigate('/login');
        } catch (err: any) {
            setError(err.response?.data?.message || 'Invalid code. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className={styles.authPage}>
            {/* Theme toggle */}
            <button className={styles.themeToggle} onClick={toggleTheme} aria-label="Toggle theme">
                {theme === 'dark'
                    ? <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><circle cx="12" cy="12" r="5" /><line x1="12" y1="1" x2="12" y2="3" /><line x1="12" y1="21" x2="12" y2="23" /><line x1="4.22" y1="4.22" x2="5.64" y2="5.64" /><line x1="18.36" y1="18.36" x2="19.78" y2="19.78" /><line x1="1" y1="12" x2="3" y2="12" /><line x1="21" y1="12" x2="23" y2="12" /><line x1="4.22" y1="19.78" x2="5.64" y2="18.36" /><line x1="18.36" y1="5.64" x2="19.78" y2="4.22" /></svg>
                    : <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z" /></svg>
                }
            </button>

            <div className={styles.card}>
                {/* Progress bar — 50% on step 1, 100% on step 2 */}
                <div className={styles.cardProgressBar} style={{ width: step === 1 ? '50%' : '100%' }} />

                {/* Brand */}
                <div className={styles.brand}>
                    <div className={styles.brandMark}>{`{}`}</div>
                    <span className={styles.brandName}>ByteForge</span>
                </div>

                {/* Heading */}
                <h1 className={styles.heading}>
                    {step === 1 ? 'Create account' : 'Verify email'}
                </h1>
                <p className={styles.subheading}>
                    {step === 1
                        ? 'Join thousands of developers sharpening their skills.'
                        : 'Enter the 6-digit code we sent to your inbox.'}
                </p>

                {/* Step dots */}
                <div className={styles.stepRow}>
                    <div className={`${styles.stepDot} ${step === 1 ? styles.active : styles.done}`} />
                    <div className={`${styles.stepDot} ${step === 2 ? styles.active : ''}`} />
                    <span className={styles.stepLabel}>Step {step} of 2</span>
                </div>

                {error && (
                    <div className={`${styles.alert} ${styles.error}`}>
                        <svg className={styles.alertIcon} width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><circle cx="12" cy="12" r="10" /><line x1="12" y1="8" x2="12" y2="12" /><line x1="12" y1="16" x2="12.01" y2="16" /></svg>
                        {error}
                    </div>
                )}

                {step === 1 ? (
                    <form className={styles.form} onSubmit={handleInitSubmit}>
                        {/* Full Name */}
                        <div className={styles.field}>
                            <label className={styles.label}>Full name</label>
                            <div className={styles.inputWrapper}>
                                <svg className={styles.inputIcon} width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" /><circle cx="12" cy="7" r="4" /></svg>
                                <input
                                    className={styles.input}
                                    type="text"
                                    placeholder="Ada Lovelace"
                                    required
                                    value={formData.name}
                                    onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                                />
                            </div>
                        </div>

                        {/* Email */}
                        <div className={styles.field}>
                            <label className={styles.label}>Email</label>
                            <div className={styles.inputWrapper}>
                                <svg className={styles.inputIcon} width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z" /><polyline points="22,6 12,13 2,6" /></svg>
                                <input
                                    className={styles.input}
                                    type="email"
                                    placeholder="you@example.com"
                                    required
                                    value={formData.email}
                                    onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                                />
                            </div>
                        </div>

                        {/* Password */}
                        <div className={styles.field}>
                            <label className={styles.label}>Password</label>
                            <div className={styles.inputWrapper}>
                                <svg className={styles.inputIcon} width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><rect x="3" y="11" width="18" height="11" rx="2" ry="2" /><path d="M7 11V7a5 5 0 0 1 10 0v4" /></svg>
                                <input
                                    className={styles.input}
                                    type={showPassword ? 'text' : 'password'}
                                    placeholder="Min. 8 characters"
                                    required
                                    minLength={8}
                                    value={formData.password}
                                    onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                                />
                                <button
                                    type="button"
                                    className={styles.passwordToggle}
                                    onClick={() => setShowPassword((p) => !p)}
                                    aria-label={showPassword ? 'Hide password' : 'Show password'}
                                >
                                    {showPassword
                                        ? <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24" /><line x1="1" y1="1" x2="23" y2="23" /></svg>
                                        : <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" /><circle cx="12" cy="12" r="3" /></svg>
                                    }
                                </button>
                            </div>
                        </div>

                        <button type="submit" className={styles.btnPrimary} disabled={loading}>
                            {loading && <span className={styles.spinner} />}
                            {loading ? 'Sending code…' : 'Continue'}
                        </button>
                    </form>
                ) : (
                    <form className={styles.form} onSubmit={handleCompleteSubmit}>
                        <div className={styles.emailHint}>
                            Code sent to <span>{formData.email}</span>
                        </div>

                        <div className={styles.field}>
                            <label className={styles.label}>Verification code</label>
                            <div className={styles.inputWrapper}>
                                <input
                                    className={`${styles.input} ${styles.inputOtp}`}
                                    type="text"
                                    inputMode="numeric"
                                    placeholder="— — — — — —"
                                    maxLength={6}
                                    required
                                    value={formData.otp}
                                    onChange={(e) => setFormData({ ...formData, otp: e.target.value.replace(/\D/g, '') })}
                                />
                            </div>
                        </div>

                        <button type="submit" className={styles.btnPrimary} disabled={loading}>
                            {loading && <span className={styles.spinner} />}
                            {loading ? 'Verifying…' : 'Complete signup'}
                        </button>

                        <button
                            type="button"
                            className={styles.btnGhost}
                            onClick={() => { setStep(1); setError(''); }}
                            disabled={loading}
                        >
                            ← Back
                        </button>
                    </form>
                )}

                {step === 1 && (
                    <>
                        <div className={styles.divider} />
                        <p className={styles.footer}>
                            Already have an account?{' '}
                            <button className={styles.footerLink} onClick={() => navigate('/login')}>
                                Sign in
                            </button>
                        </p>
                    </>
                )}
            </div>
        </div>
    );
};