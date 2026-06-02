import { CSSProperties } from "react";

export const wrapperStyle: React.CSSProperties = {
    minHeight: "100vh",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    background: "#f5f7fa",
    padding: "24px",
};

export const cardStyle: React.CSSProperties = {
    width: "100%",
    maxWidth: 420,
    background: "#ffffff",
    borderRadius: 12,
    boxShadow: "0 10px 30px rgba(15, 23, 42, 0.08)",
    padding: "36px 32px",
};

export const headerStyle: React.CSSProperties = {
    marginBottom: 24,
};

export const subTextStyle: React.CSSProperties = {
    marginTop: 8,
    color: "#6b7280",
    lineHeight: 1.5,
};

export const labelStyle: React.CSSProperties = {
    display: "block",
    marginBottom: 16,
    fontSize: 14,
    color: "#111827",
};

export const inputStyle: React.CSSProperties = {
    width: "100%",
    marginTop: 8,
    padding: "12px 14px",
    borderRadius: 10,
    border: "1px solid #d1d5db",
    fontSize: 15,
    outline: "none",
};

export const buttonStyle: React.CSSProperties = {
    width: "100%",
    marginTop: 16,
    padding: "12px 16px",
    borderRadius: 10,
    border: "none",
    background: "#0a6ebd",
    color: "#fff",
    fontSize: 16,
    cursor: "pointer",
};

export const errorStyle: React.CSSProperties = {
    marginTop: 10,
    color: "#dc2626",
    fontSize: 14,
};