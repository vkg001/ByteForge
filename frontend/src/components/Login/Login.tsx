import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useMutation } from "@tanstack/react-query";
import { login, LoginPayload, LoginResponse } from "../../api/Auth";
import { ApiError } from "../../api/client";
import { useAuthStore } from "../../store/useAuthStore";
import * as loginStyles from "./LoginCss";

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
                <div style={loginStyles.headerStyle}>
                    <h1 style={{ margin: 0, fontSize: 32 }}>Sign in</h1>
                    <p style={loginStyles.subTextStyle}>Use your ByteForge account to continue</p>
                </div>

                <form onSubmit={handleSubmit}>
                    <label style={loginStyles.labelStyle}>
                        Email
                        <input
                            style={loginStyles.inputStyle}
                            type="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            placeholder="name@example.com"
                            required
                        />
                    </label>

                    <label style={loginStyles.labelStyle}>
                        Password
                        <input
                            style={loginStyles.inputStyle}
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            placeholder="Password"
                            required
                        />
                    </label>

                    {errorMessage && <div style={loginStyles.errorStyle}>{errorMessage}</div>}

                    <button type="submit" style={loginStyles.buttonStyle} disabled={mutation.isPending}>
                        {mutation.isPending ? "Signing in..." : "Sign in"}
                    </button>
                </form>
            </div>
        </div>
    );
};

export default Login;