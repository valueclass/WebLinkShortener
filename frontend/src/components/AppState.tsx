import { createContext, useEffect, useState, ReactNode, useContext } from "react";
import { useNavigate } from "react-router";
import { UnauthorizedException, User, WhoAmI } from "../api/Api";
import { FetchException } from "../utils/Fetch";

export interface AppState {
    user?: User,
    onLogin(user: User): void;
    onLogout(): void;
}

const AppStateContext = createContext<AppState>({
    onLogin(_) {},
    onLogout() {}
});

interface ProviderProps {
    children?: ReactNode
}

export function AppStateProvider({ children } : ProviderProps) {
    const [appState, setAppState] = useState<AppState>({
        onLogin(u: User) {
            console.info("logged in", u);
            setAppState(prev => ({ ...prev, user: u }));
        },
        onLogout() {
            console.info("logged out", appState.user);
            setAppState(prev => ({ ...prev, user: undefined }));
        }
    });

    useEffect(() => {
        const sub = WhoAmI()
            .subscribe({
                next: user => {
                    setAppState(prev => ({ user: user, ...prev }));
                },
                error: err => {
                    if (err instanceof UnauthorizedException) {
                        console.log('Unauthorized');
                    } else {
                        if (err instanceof FetchException) {
                            console.log('fetch error', err);
                        } else {
                            console.log('error', err);
                        }
                    }
                },
                complete: () => {
                }
            });
        return () => sub.unsubscribe();
    }, [])

    return (<AppStateContext.Provider value={ appState } children={ children } />);
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
    });

    return state;
}
