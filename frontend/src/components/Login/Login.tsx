// Login.tsx
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useMutation } from "@tanstack/react-query";
import { login, LoginPayload, LoginResponse } from "../../api/Auth";
import { ApiError } from "../../api/client";
import { useAuthStore } from "../../store/useAuthStore";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faGoogle, faGithub, faApple } from "@fortawesome/free-brands-svg-icons";

// Import standard CSS
import "./Login.css"; 

const Login = () => {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const setToken = useAuthStore((state: any) => state.setToken);
    const navigate = useNavigate();

    const mutation = useMutation<LoginResponse, ApiError, LoginPayload>({
        mutationFn: login,
        onSuccess: (data: LoginResponse) => {
            setToken(data.token);
            navigate("/");
        },
    });

    const handleSubmit = (event: React.SubmitEvent<HTMLFormElement>) => {
        event.preventDefault();
        mutation.mutate({ email, password });
    };

    const errorMessage = (() => {
        if (!mutation.isError) return null;
        if (mutation.error.status === 401) return "Wrong password. Please try again.";
        if (mutation.error.status === 404) return "Email not registered.";
        return "Unable to sign in. Please check your details and retry.";
    })();

    return (
        <div className="login-wrapper">
            <div className="login-card">

                <div className="logo-container">
                    <div className="logo-icon">B</div>
                    <span className="logo-text">ByteForge</span>
                </div>

                <form onSubmit={handleSubmit} className="login-form">
                    <input
                        className="login-input"
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        placeholder="Username or E-mail"
                        required
                    />
                    <input
                        className="login-input"
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        placeholder="Password"
                        required
                    />

                    {/* Mock Cloudflare CAPTCHA Box */}
                    <div className="captcha-box">
                        <div className="captcha-left">
                            <div className="check-icon">✓</div>
                            Success!
                        </div>
                        <div className="captcha-right">
                            <strong>CLOUDFLARE</strong><br />
                            Privacy • Terms
                        </div>
                    </div>

                    {errorMessage && <div className="error-message">{errorMessage}</div>}

                    <button type="submit" className="submit-button" disabled={mutation.isPending}>
                        {mutation.isPending ? "Signing In..." : "Sign In"}
                    </button>
                </form>

                <div className="agreement-text">
                    By continuing, you agree to <span className="agreement-link">Terms</span> & <span className="agreement-link">Privacy Policy</span>.
                </div>

                <div className="action-row">
                    <span className="action-link">Forgot Password?</span>
                    <span className="action-link">Sign Up</span>
                </div>

                <div className="divider">
                    <div className="divider-line"></div>
                    <span className="divider-text">or you can sign in with</span>
                    <div className="divider-line"></div>
                </div>

                <div className="social-group">
                    <div className="social-icon">
                        <FontAwesomeIcon icon={faGoogle} style={{ fontSize: "18px" }} />
                    </div>
                    <div className="social-icon">
                        <FontAwesomeIcon icon={faGithub} style={{ fontSize: "18px" }} />
                    </div>
                    <div className="social-icon">
                        <FontAwesomeIcon icon={faApple} style={{ fontSize: "20px" }} />
                    </div>
                </div>

            </div>
        </div>
    );
};

export default Login;