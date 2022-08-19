import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { UserLogout } from "../api/Api";
import { useLoginGuard } from "./AppState";

export function Logout() {
    const state = useLoginGuard();
    const navigate = useNavigate();

    useEffect(() => {
        console.log('logging out');

        const sub = UserLogout()
            .subscribe({
                next(_) {
                    console.log('next called');
                },
                error(error) {
                    console.error('error', error);
                },
                complete() {
                    console.log('complete called');
                    state.onLogout();
                    navigate('/login');
                }
            });

        return () => sub.unsubscribe();
    }, [state, navigate]);

    return (<><h1>Logged out</h1></>)
}
