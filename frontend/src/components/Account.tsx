import { Button, Card, Divider, FormGroup, H2, H4, InputGroup, Intent } from "@blueprintjs/core";
import { useEffect } from "react";
import { useState, FormEvent } from "react";
import { useNavigate } from "react-router";
import { Subscription } from "rxjs";
import { UpdatePassword } from "../api/Api";
import { Exception } from "../utils/Exceptions";
import { Consumer, Function } from "../utils/Types";
import { useLoginGuard } from "./AppState";
import { ExceptionCard } from "./ExceptionCard";
import { LoadingOverlay } from "./LoadingOverlay";

export function Account() {
    const state = useLoginGuard();

    const navigate = useNavigate();

    const [old, setOld] = useState<string>("");
    const [updated, setUpdated] = useState<string>("");
    const [retyped, setRetyped] = useState<string>("");
    const [mismatch, setMismatch] = useState<boolean>(false);
    const [subscription, setSubscription] = useState<Subscription>();
    const [exception, setException] = useState<Exception>();

    useEffect(() => () => {
        if (subscription)
            subscription.unsubscribe();
    }, [subscription])

    const handler = (event: FormEvent) => {
        event.preventDefault();

        if (updated !== retyped) {
            setMismatch(true);
            return;
        }

        setSubscription(undefined);

        const sub = UpdatePassword(old, updated)
            .subscribe({
                next(_) { },
                error(ex) {
                    setException(ex);
                    setSubscription(undefined);
                },
                complete() {
                    navigate('/logout');
                    setSubscription(undefined);
                }
            });

        setSubscription(sub);
    }

    const validator = (setter: Consumer<Function<string, string>>) => (event: FormEvent<HTMLInputElement>) => {
        const value = event.currentTarget.value;

        setter(prev => {
            if (prev === updated) setMismatch(value !== retyped);
            else if (prev === retyped) setMismatch(value !== updated);
            return value;
        });
    }

    const intent = () => mismatch ? Intent.DANGER : Intent.NONE;
    const mismatchText = () => mismatch ? "Passwords do not match" : "";
    const loading = () => !!subscription;

    return (
        <div>
            <Card>
                <H2>Account</H2>
                <span className="text-lg">Logged in as: {state.user?.username}</span>
                <Divider className="my-4" />
                <H4>Change password</H4>
                <div className="max-w-lg mx-auto">
                    <ExceptionCard message="Failed to change password: " exception={exception} onClose={() => setException(undefined)} />
                    <LoadingOverlay show={loading()}>
                        <form onSubmit={handler}>
                            <FormGroup label="Old password" labelFor="input-old-password">
                                <InputGroup id="input-old-password" type="password" placeholder="Old password" disabled={loading()} onInput={e => setOld(e.currentTarget.value)} />
                            </FormGroup>
                            <FormGroup label="New password" labelFor="input-new-password" helperText={mismatchText()} intent={intent()}>
                                <InputGroup id="input-new-password" type="password" placeholder="New password" intent={intent()} disabled={loading()} onInput={validator(setUpdated)} />
                            </FormGroup>
                            <FormGroup label="Retype password" labelFor="input-retyped-password" helperText={mismatchText()} intent={intent()}>
                                <InputGroup id="input-retyped-password" type="password" placeholder="Retype password" intent={intent()} disabled={loading()} onInput={validator(setRetyped)} />
                            </FormGroup>
                            <Button type="submit" text="Update" icon="small-tick" disabled={mismatch || loading()} className="w-full sm:w-auto" />
                        </form>
                    </LoadingOverlay>
                </div>
            </Card >
        </div >
    );
}
