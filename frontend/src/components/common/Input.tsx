import { InputHTMLAttributes, forwardRef } from 'react';

interface InputProps extends InputHTMLAttributes<HTMLInputElement> {
    label?: string;
    error?: string;
}

export const Input = forwardRef<HTMLInputElement, InputProps>(
    ({ label, error, className = '', ...props }, ref) => {
        return (
            <div className="flex flex-col gap-1 w-full">
                {label && (
                    <label className="text-sm font-medium text-dark-text">
                        {label}
                    </label>
                )}
                <input
                    ref={ref}
                    className={`w-full bg-dark-layer border ${error ? 'border-brand-error' : 'border-dark-border'} text-dark-text rounded-md px-3 py-2 focus:outline-none focus:border-brand-primary transition-colors disabled:opacity-50 disabled:cursor-not-allowed ${className}`}
                    {...props}
                />
                {error && <span className="text-xs text-brand-error">{error}</span>}
            </div>
        );
    }
);

Input.displayName = 'Input';