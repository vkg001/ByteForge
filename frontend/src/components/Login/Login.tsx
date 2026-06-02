import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useMutation } from "@tanstack/react-query";
import { login, LoginPayload, LoginResponse } from "../../api/Auth";
import { ApiError } from "../../api/client";
import { useAuthStore } from "../../store/useAuthStore";
import * as loginStyles from "./LoginCss";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faGoogle, faGithub, faApple } from "@fortawesome/free-brands-svg-icons";

const Login = () => {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const setToken = useAuthStore((state: any) => state.setToken);
    const navigate = useNavigate();

    const mutation = useMutation<LoginResponse, ApiError, LoginPayload>({
        mutationFn: login,
        onSuccess: (data) => {
            setToken(data.token);
            navigate("/");
        },
    });

    const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
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
        <div style={loginStyles.wrapperStyle}>
            <div style={loginStyles.cardStyle}>

                <div style={loginStyles.logoContainerStyle}>
                    <div style={loginStyles.logoIconStyle}>B</div>
                    <span style={{ fontSize: "20px", fontWeight: "600" }}>ByteForge</span>
                </div>

                <form onSubmit={handleSubmit} style={loginStyles.formStyle}>
                    <input
                        style={loginStyles.inputStyle}
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        placeholder="Username or E-mail"
                        required
                    />
                    <input
                        style={loginStyles.inputStyle}
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        placeholder="Password"
                        required
                    />

                    {/* Mock Cloudflare CAPTCHA Box */}
                    <div style={loginStyles.captchaBoxStyle}>
                        <div style={loginStyles.captchaLeftStyle}>
                            <div style={loginStyles.checkIconStyle}>✓</div>
                            Success!
                        </div>
                        <div style={loginStyles.captchaRightStyle}>
                            <strong>CLOUDFLARE</strong><br />
                            Privacy • Terms
                        </div>
                    </div>

                    {errorMessage && <div style={{ color: "#dc2626", fontSize: 13, textAlign: "center" }}>{errorMessage}</div>}

                    <button type="submit" style={loginStyles.buttonStyle} disabled={mutation.isPending}>
                        {mutation.isPending ? "Signing In..." : "Sign In"}
                    </button>
                </form>

                <div style={loginStyles.agreementTextStyle}>
                    By continuing, you agree to <span style={loginStyles.linkStyle}>Terms</span> & <span style={loginStyles.linkStyle}>Privacy Policy</span>.
                </div>

                <div style={loginStyles.actionRowStyle}>
                    <span style={loginStyles.actionLinkStyle}>Forgot Password?</span>
                    <span style={loginStyles.actionLinkStyle}>Sign Up</span>
                </div>

                <div style={loginStyles.dividerStyle}>
                    <div style={loginStyles.lineStyle}></div>
                    <span style={loginStyles.dividerTextStyle}>or you can sign in with</span>
                    <div style={loginStyles.lineStyle}></div>
                </div>

                <div style={loginStyles.socialGroupStyle}>
                    <div style={loginStyles.socialIconStyle}>
                        <FontAwesomeIcon icon={faGoogle} style={{ fontSize: "18px", color: "#ffffff" }} />
                    </div>
                    <div style={loginStyles.socialIconStyle}>
                        <FontAwesomeIcon icon={faGithub} style={{ fontSize: "18px", color: "#ffffff" }} />
                    </div>
                    <div style={loginStyles.socialIconStyle}>
                        <FontAwesomeIcon icon={faApple} style={{ fontSize: "20px", color: "#ffffff" }} />
                    </div>
                </div>

            </div>
        </div>
    );
};

export default Login;