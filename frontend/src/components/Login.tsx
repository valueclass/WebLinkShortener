import { useEffect, useState, MouseEvent } from "react";
import { useNavigate } from "react-router";
import { Subscription } from "rxjs";
import { UserLogin } from "../api/Api";
import { useAppState } from "./AppState";

export function Login() {
    const state = useAppState();
    const [username, setUsername] = useState<string>('');
    const [password, setPassword] = useState<string>('');
    const [subscription, setSubscription] = useState<Subscription>();
    const navigate = useNavigate();

    useEffect(() => {
        if (state.user)
            navigate('/');
    }, [navigate, state]);

    useEffect(() => () => {
        if (subscription)
            subscription.unsubscribe();
    }, [subscription]);

    const handler = (event: MouseEvent) => {
        event.preventDefault();

        const sub = UserLogin(username, password)
            .subscribe({
                next(user) {
                    state.onLogin(user);
                    setSubscription(undefined);
                    navigate('/');
                },
                error(_) {},
                complete() {}
            });

        setSubscription(sub);
    }

    return (
        <div>
            <h1>Log in</h1>
            <div>
                <form>
                    <input type="text" placeholder="Username" onInput={ e => setUsername(e.currentTarget.value) } disabled={ !!subscription }/>
                    <input type="password" placeholder="Password" onInput={ e => setPassword(e.currentTarget.value) } disabled={ !!subscription }/>
                    <button onClick={ handler } disabled={ !!subscription }>Log in</button>
                </form>
            </div>
        </div>
    )
}
