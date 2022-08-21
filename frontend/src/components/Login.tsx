import { Button, Card, FormGroup, H2, InputGroup } from "@blueprintjs/core";
import { useEffect, useState, FormEvent } from "react";
import { useNavigate } from "react-router";
import { Subscription } from "rxjs";
import { UserLogin } from "../api/Api";
import { Exception } from "../utils/Exceptions";
import { useAppState } from "./AppState";
import { ExceptionCard } from "./ExceptionCard";

export function Login() {
    const state = useAppState();
    const [username, setUsername] = useState<string>('');
    const [password, setPassword] = useState<string>('');
    const [subscription, setSubscription] = useState<Subscription>();
    const [exception, setException] = useState<Exception>();
    const navigate = useNavigate();

    useEffect(() => {
        if (state.user)
            navigate('/');
    }, [navigate, state]);

    useEffect(() => () => {
        if (subscription)
            subscription.unsubscribe();
    }, [subscription]);

    const handler = (event: FormEvent) => {
        event.preventDefault();
        setSubscription(undefined);
        setException(undefined);

        const sub = UserLogin(username, password)
            .subscribe({
                next(user) {
                    state.onLogin(user);
                    setSubscription(undefined);
                    navigate('/');
                },
                error(ex) {
                    setSubscription(undefined);
                    setException(ex);
                },
                complete() { }
            });

        setSubscription(sub);
    }

    return (
        <div className="">
            <Card className="max-w-lg mx-auto">
                <H2>Log in</H2>
                <ExceptionCard message="Failed to Log In:" exception={exception} onClose={() => setException(undefined)} />
                <form onSubmit={handler}>
                    <FormGroup label="Username" labelFor="username">
                        <InputGroup id="username" type="text" placeholder="Username" onInput={e => setUsername(e.currentTarget.value)} disabled={!!subscription} />
                    </FormGroup>
                    <FormGroup label="Password" labelFor="password">
                        <InputGroup id="password" type="password" placeholder="Password" onInput={e => setPassword(e.currentTarget.value)} disabled={!!subscription} />
                    </FormGroup>
                    <Button type="submit" text="Log in" />
                </form>
            </Card>
        </div>
    )
}
