import { ReactNode, useEffect, useRef } from 'react';

interface ModalProps {
    isOpen: boolean;
    onClose?: () => void;
    children: ReactNode;
    title?: string;
}

export const Modal = ({ isOpen, onClose, children, title }: ModalProps) => {
    if (!isOpen) return null;

    const titleId = title ? 'modal-title' : undefined;
    const dialogRef = useRef<HTMLDivElement | null>(null);

    useEffect(() => {
        if (!isOpen || !onClose) return;
        const handleKey = (e: KeyboardEvent) => {
            if (e.key === 'Escape') onClose();
        };
        document.addEventListener('keydown', handleKey);
        return () => document.removeEventListener('keydown', handleKey);
    }, [isOpen, onClose]);

    return (
        <div
            className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50 backdrop-blur-sm p-4"
            onClick={() => onClose?.()}
        >
            <div
                ref={dialogRef}
                role="dialog"
                aria-modal="true"
                aria-labelledby={titleId}
                tabIndex={-1}
                className="bg-dark-bg border border-dark-border rounded-lg shadow-xl w-full max-w-md flex flex-col overflow-hidden"
                onClick={(e) => e.stopPropagation()}
            >
                {(title || onClose) && (
                    <div className="flex items-center justify-between p-4 border-b border-dark-border">
                        {title && <h3 id={titleId} className="text-lg font-semibold text-dark-text">{title}</h3>}
                        {onClose && (
                            <button
                                onClick={onClose}
                                className="text-dark-muted hover:text-dark-text transition-colors"
                                aria-label="Close modal"
                            >
                                ✕
                            </button>
                        )}
                    </div>
                )}
                <div className="p-4">
                    {children}
                </div>
            </div>
        </div>
    );
};