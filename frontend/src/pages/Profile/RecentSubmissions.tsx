import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import styles from './RecentSubmissions.module.css';
import { submissionService } from "../../services/submission.service";
import { RecentSubmission } from '../../types'; // Ensure this path is correct

// Utility to convert ISO timestamp to relative time format
const getRelativeTime = (isoString: string): string => {
    const date = new Date(isoString);
    const now = new Date();
    const diffInSeconds = Math.floor((now.getTime() - date.getTime()) / 1000);

    if (diffInSeconds < 60) return `${diffInSeconds} secs ago`;
    const diffInMinutes = Math.floor(diffInSeconds / 60);
    if (diffInMinutes < 60) return `${diffInMinutes} mins ago`;
    const diffInHours = Math.floor(diffInMinutes / 60);
    if (diffInHours < 24) return `${diffInHours} hours ago`;
    const diffInDays = Math.floor(diffInHours / 24);
    if (diffInDays === 1) return 'Yesterday';
    return `${diffInDays} days ago`;
};

// Utility to map Language ID to display string. Update these IDs based on your Judge0 configuration.
const getLanguageName = (languageId: number): string => {
    const languageMap: Record<number, string> = {
        71: 'Python',
        54: 'C++',
        62: 'Java',
        93: 'JavaScript',
        // Add remaining mappings
    };
    return languageMap[languageId] || 'Unknown';
};

// Utility to map backend SubmissionStatus enums to UI text
const getDisplayStatus = (status: string): string => {
    const statusMap: Record<string, string> = {
        'ACC': 'Accepted',
        'WA': 'Wrong Answer',
        'TLE': 'Time Limit Exceeded',
        'RTE': 'Runtime Error',
        'CE': 'Compilation Error'
    };
    return statusMap[status] || status;
};

export const RecentSubmissions = ({ userId }: { userId?: string }) => {
    const [submissions, setSubmissions] = useState<RecentSubmission[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchSubmissions = async () => {
            setLoading(true);
            setError(null);
            try {
                // Fetching the top 15 recent submissions
                const data = await submissionService.getRecentSubmissions(15);
                setSubmissions(data);
            } catch (err) {
                console.error("Failed to fetch recent submissions:", err);
                setError("Failed to load submissions.");
            } finally {
                setLoading(false);
            }
        };

        fetchSubmissions();
    }, [userId]); // Re-fetch if userId changes, though currently the backend fetches for the logged-in user

    if (loading) {
        return <div className={styles.emptyState}>Loading submissions...</div>;
    }

    if (error) {
        return <div className={styles.emptyState}>{error}</div>;
    }

    if (submissions.length === 0) {
        return <div className={styles.emptyState}>No recent submissions found.</div>;
    }

    return (
        <div className={styles.list}>
            {submissions.map((sub) => {
                const displayStatus = getDisplayStatus(sub.status);
                const isAccepted = sub.status === 'ACC';

                return (
                    // WARNING: sub.problemId and sub.id MUST be added to your backend DTO for this Link to work.
                    <Link to={`/problems/${sub.problemId}/submissions/${sub.id}`} key={sub.id} className={styles.item}>
                        <div>
                            <div className={styles.problemName}>{sub.problemTitle}</div>
                            <div className={styles.timestamp}>{getRelativeTime(sub.submittedAt)}</div>
                        </div>
                        <div>
                            <span className={`${styles.status} ${isAccepted ? styles.accepted : styles.rejected}`}>
                                {isAccepted ? '✓' : '✕'} {displayStatus}
                            </span>
                        </div>
                        <div className={styles.language}>{getLanguageName(sub.languageId)}</div>
                    </Link>
                );
            })}
        </div>
    );
};