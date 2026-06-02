import { create } from "zustand";

interface AuthState {
    token: string | null;
    setToken: (token: string) => void;
    clearToken: () => void;
}

const initialToken = typeof window !== "undefined" ? sessionStorage.getItem("auth_token") : null;

export const useAuthStore = create<AuthState>((set: any) => ({
    token: initialToken,
    setToken: (token: string) => {
        sessionStorage.setItem("auth_token", token);
        set({ token });
    },
    clearToken: () => {
        sessionStorage.removeItem("auth_token");
        set({ token: null });
    },
}));