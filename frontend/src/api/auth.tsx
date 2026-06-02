import { apiFetch } from "./client";

export interface LoginPayload {
    email: string;
    password: string;
}

export interface LoginResponse {
    token: string;
}

export function login(payload: LoginPayload) {
    return apiFetch<LoginResponse>("auth/login", {
        method: "POST",
        body: JSON.stringify(payload),
    });
}