import { Spinner } from "@blueprintjs/core";
import { ReactElement } from "react";

interface LoadingOverlayProps {
    show: boolean;
    children?: ReactElement;
}

export function LoadingOverlay({ show, children }: LoadingOverlayProps) {
    if (show) {
        return (
            <div className="relative p-2">
                {children || null}
                <Spinner className="absolute inset-0" />
                <div className="absolute bg-stone-400 opacity-50 w-full h-full inset-0"></div>
            </div>
        );
    } else {
        return (
            <div className="relative p-2">
                {children || null}
            </div>
        );
    }
}
