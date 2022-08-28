import { Button, Card, Checkbox, FormGroup, H2, InputGroup } from "@blueprintjs/core";
import { FormEvent, useEffect, useState } from "react";
import { useNavigate } from "react-router";
import { Subscription } from "rxjs";
import { ShortenLink } from "../api/Api";
import { Exception } from "../utils/Exceptions";
import { LinkValidator } from "../utils/Validators";
import { useLoginGuard } from "./AppState";
import { ExceptionCard } from "./ExceptionCard";
import { LoadingOverlay } from "./LoadingOverlay";

export function Shorten() {
    useLoginGuard();

    const navigate = useNavigate();

    const [exception, setException] = useState<Exception>();
    const [destination, setDestination] = useState<string>('');
    const [name, setName] = useState<string>('');
    const [private_, setPrivate] = useState<boolean>(false);
    const [subscription, setSubscription] = useState<Subscription>();

    useEffect(() => () => {
        if (subscription)
            subscription.unsubscribe();
    });

    const handler = (event: FormEvent) => {
        event.preventDefault();

        const ex = new LinkValidator(name, destination).validate(true, true);
        if (ex) {
            setException(ex);
            return;
        }

        const sub = ShortenLink(destination, name, private_)
            .subscribe({
                next(value) {
                    setSubscription(undefined);
                    navigate(`/manage/${value.id}`);
                },
                error(err) {
                    setException(err);
                },
                complete() { },
            });

        setSubscription(sub);
    }

    return (
        <Card className="max-w-5xl mx-auto">
            <H2>Shorten</H2>
            <ExceptionCard message="Failed to shorten a link: " exception={exception} onClose={() => setException(undefined)} />
            <LoadingOverlay show={false}>
                <form onSubmit={handler}>
                    <FormGroup label="Destination" labelFor="input-destination">
                        <InputGroup id="input-destination" placeholder="https://example.com/" value={destination} onChange={e => setDestination(e.currentTarget.value.trim())} />
                    </FormGroup>
                    <FormGroup label="Name" labelFor="input-name">
                        <InputGroup id="input-name" value={name} onChange={e => setName(e.currentTarget.value.trim())} />
                    </FormGroup>
                    <FormGroup label="Private" inline={true} labelFor="checkbox-private">
                        <Checkbox style={{ margin: '4px 0px 0px 0px' }} id="checkbox-private" onChange={e => setPrivate(e.currentTarget.checked)} />
                    </FormGroup>
                    <Button type="submit" text="Save" icon="tick" />
                </form>
            </LoadingOverlay>
        </Card>
    );
}
