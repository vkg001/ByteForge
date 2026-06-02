import { CSSProperties } from "react";

export const wrapperStyle: CSSProperties = {
    minHeight: "calc(100vh - 54px)",
    display: "flex",
    justifyContent: "center",
    background: "#f7f8fa",
    paddingTop: "40px",
    fontFamily: "-apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif",
};

export const cardStyle: CSSProperties = {
    width: "100%",
    maxWidth: 400,
    background: "#ffffff",
    padding: "40px 32px",
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    boxSizing: "border-box",
};

export const logoContainerStyle: CSSProperties = {
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    marginBottom: "32px",
    gap: "8px",
};

export const logoIconStyle: CSSProperties = {
    width: "48px",
    height: "48px",
    backgroundColor: "#ffa116",
    color: "#fff",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    fontWeight: "bold",
    borderRadius: "8px",
    fontSize: "20px",
};

export const formStyle: CSSProperties = {
    width: "100%",
    display: "flex",
    flexDirection: "column",
    gap: "16px",
};

export const inputStyle: CSSProperties = {
    width: "100%",
    padding: "10px 12px",
    borderRadius: "4px",
    border: "1px solid #d9d9d9",
    fontSize: "14px",
    outline: "none",
    boxSizing: "border-box",
    color: "#262626",
};

export const captchaBoxStyle: CSSProperties = {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    padding: "8px 12px",
    border: "1px solid #d9d9d9",
    borderRadius: "4px",
    backgroundColor: "#fafafa",
};

export const captchaLeftStyle: CSSProperties = {
    display: "flex",
    alignItems: "center",
    gap: "8px",
    fontSize: "14px",
    color: "#262626",
};

export const checkIconStyle: CSSProperties = {
    width: "24px",
    height: "24px",
    backgroundColor: "#28a745",
    color: "white",
    borderRadius: "50%",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    fontSize: "14px",
};

export const captchaRightStyle: CSSProperties = {
    textAlign: "right",
    fontSize: "10px",
    color: "#8c8c8c",
};

export const buttonStyle: CSSProperties = {
    width: "100%",
    padding: "10px",
    borderRadius: "4px",
    border: "none",
    background: "#546776", // Slate/Gray-Blue from screenshot
    color: "#fff",
    fontSize: "15px",
    cursor: "pointer",
    marginTop: "4px",
};

export const agreementTextStyle: CSSProperties = {
    fontSize: "12px",
    color: "#8c8c8c",
    textAlign: "center",
    marginTop: "8px",
    marginBottom: "16px",
};

export const linkStyle: CSSProperties = {
    color: "#007aff",
    textDecoration: "none",
    cursor: "pointer",
};

export const actionRowStyle: CSSProperties = {
    display: "flex",
    justifyContent: "space-between",
    width: "100%",
    fontSize: "13px",
    color: "#5c5c5c",
};

export const actionLinkStyle: CSSProperties = {
    cursor: "pointer",
};

export const dividerStyle: CSSProperties = {
    display: "flex",
    alignItems: "center",
    color: "#bfbfbf",
    fontSize: "13px",
    margin: "32px 0 24px 0",
    width: "100%",
};

export const lineStyle: CSSProperties = {
    flex: 1,
    borderBottom: "1px solid #f0f0f0",
};

export const dividerTextStyle: CSSProperties = {
    padding: "0 10px",
};

export const socialGroupStyle: CSSProperties = {
    display: "flex",
    justifyContent: "center",
    gap: "16px",
};

export const socialIconStyle: CSSProperties = {
    width: "36px",
    height: "36px",
    borderRadius: "50%",
    backgroundColor: "#d9d9d9", // Light gray circle
    color: "#ffffff",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    cursor: "pointer",
    fontSize: "14px",
    fontWeight: "bold",
};