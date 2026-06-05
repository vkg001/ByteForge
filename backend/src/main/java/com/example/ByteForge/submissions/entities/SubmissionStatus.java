package com.example.ByteForge.submissions.entities;

public enum SubmissionStatus {
    WA, // wrong answer
    TLE, // time limit exceeded
    MLE, // memory limit exceeded
    ACC,
    CE,
    RE, // Catches SIGSEGV, SIGXFSZ, SIGFPE, etc.
    ISE; // Internal server error

    public static SubmissionStatus fromJudge0Id(int id) {
        return switch (id) {
            case 3 -> ACC;
            case 4 -> WA;
            case 11 -> CE;
            case 12 -> MLE;
            case 13 -> TLE;
            case 7, 8, 9, 10, 14 -> RE; // Catches SIGSEGV, SIGXFSZ, SIGFPE, etc.
            default -> ISE; // Fallback for API failures or unmapped states
        };
    }
}
