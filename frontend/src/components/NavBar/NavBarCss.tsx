import { CSSProperties } from "react";

export const navStyle: CSSProperties = {
    display: "flex",
    alignItems: "center",
    justifyContent: "space-between",
    backgroundColor: "#ffffff",
    padding: "0 max(20px, calc((100vw - 1200px) / 2))", // Centers content on wide screens
    height: "54px",
    boxShadow: "0 1px 2px rgba(0,0,0,0.05)",
    fontFamily: "-apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif",
    borderBottom: "1px solid #f0f0f0",
};

export const leftSectionStyle: CSSProperties = {
    display: "flex",
    alignItems: "center",
    gap: "32px",
};

export const logoPlaceholderStyle: CSSProperties = {
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    width: "24px",
    height: "24px",
    backgroundColor: "#ffa116",
    color: "#fff",
    borderRadius: "4px",
    fontWeight: "bold",
    fontSize: "12px",
    textDecoration: "none",
};

export const linkGroupStyle: CSSProperties = {
    display: "flex",
    alignItems: "center",
    gap: "24px",
};

export const linkStyle: CSSProperties = {
    color: "#5c5c5c",
    textDecoration: "none",
    fontSize: "14px",
    cursor: "pointer",
};

export const dropdownStyle: CSSProperties = {
    display: "flex",
    alignItems: "center",
    gap: "4px",
};

export const rightSectionStyle: CSSProperties = {
    display: "flex",
    alignItems: "center",
    gap: "16px",
};

export const searchBarStyle: CSSProperties = {
    display: "flex",
    alignItems: "center",
    backgroundColor: "#f2f3f4",
    padding: "6px 12px",
    borderRadius: "16px",
    color: "#8c8c8c",
    fontSize: "14px",
    width: "180px",
};

export const premiumButtonStyle: CSSProperties = {
    color: "#ffa116",
    backgroundColor: "#fff3e0",
    border: "none",
    padding: "6px 12px",
    borderRadius: "16px",
    fontSize: "14px",
    cursor: "pointer",
};