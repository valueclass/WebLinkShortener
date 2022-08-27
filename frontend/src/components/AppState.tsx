import { Alert } from "@blueprintjs/core";
import { createContext, useEffect, useState, ReactNode, useContext } from "react";
import { useNavigate } from "react-router";
import { Subscription } from "rxjs";
import { User, WhoAmI } from "../api/Api";
import { UnauthorizedException } from "../api/ApiExceptions";
import { Exception } from "../utils/Exceptions";
import { Nullable, Runnable } from "../utils/Types";

export interface AppState {
    user?: Nullable<User>,
    onLogin(user: User): void;
    onLogout(): void;
}

const AppStateContext = createContext<AppState>({
    user: null,
    onLogin(_) { },
    onLogout() { }
});

interface ExceptionAlertProps {
    children?: ReactNode;
    exception?: Exception;
    onClose: Runnable;
}

export function ExceptionAlert({ children, exception, onClose }: ExceptionAlertProps) {
    if (exception) {
        return (
            <>
                <Alert isOpen={true} onClose={() => onClose()} confirmButtonText="Retry">
                    <span>Failed to load: {exception.getDisplayMessage()}</span>
                </Alert>
                {children}
            </>
        );
    } else {
        return (<>{children}</>);
    }
}

interface ProviderProps {
    children?: ReactNode
}

export function AppStateProvider({ children }: ProviderProps) {
    const [appState, setAppState] = useState<AppState>({
        user: null,
        onLogin(u: User) {
            setAppState(prev => ({ ...prev, user: u }));
        },
        onLogout() {
            setAppState(prev => ({ ...prev, user: null }));
        }
    });

    const [exception, setException] = useState<Exception>();
    const [subscription, setSubscription] = useState<Subscription>();

    const fetchUser = () => {
        return WhoAmI()
            .subscribe({
                next: user => {
                    setAppState(prev => ({ user: user, ...prev }));
                },
                error(ex) {
                    if (ex instanceof UnauthorizedException) {
                        setAppState(prev => ({ user: null, ...prev }),);
                    } else {
                        setException(ex);
                    }
                    setSubscription(undefined);
                },
                complete: () => {
                    setSubscription(undefined);
                }
            });
    }

    const reload = () => {
        setException(undefined);
        setSubscription(fetchUser());
    }

    useEffect(() => {
        const sub = fetchUser();
        return () => sub.unsubscribe();
    }, []);

    useEffect(() => () => {
        if (subscription)
            subscription.unsubscribe();
    }, [subscription])

    return (<AppStateContext.Provider value={appState} children={<ExceptionAlert children={children} exception={exception} onClose={reload} />} />);
}

export function useAppState(): AppState {
    return useContext(AppStateContext);
}

export function useLoginGuard(): AppState {
    const state = useAppState();
    const navigate = useNavigate();

    useEffect(() => {
        if (!state.user)
            navigate('/login');
    }, [state]);

    return state;
}
