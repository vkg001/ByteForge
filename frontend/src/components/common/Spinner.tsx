interface SpinnerProps {
    size?: 'sm' | 'md' | 'lg';
    className?: string;
}

export const Spinner = ({ size = 'md', className = '' }: SpinnerProps) => {
    const sizes = {
        sm: "h-4 w-4",
        md: "h-8 w-8",
        lg: "h-12 w-12"
    };

    return (
        <div
            className={`animate-spin rounded-full border-t-2 border-b-2 border-brand-primary ${sizes[size]} ${className}`}
        />
    );
};