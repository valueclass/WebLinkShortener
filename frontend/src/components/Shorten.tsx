import { Button, Card, Checkbox, FormGroup, H2, InputGroup } from "@blueprintjs/core";
import { FormEvent, useEffect, useState } from "react";
import { useNavigate } from "react-router";
import { Subscription } from "rxjs";
import { ShortenLink } from "../api/Api";
import { Exception } from "../utils/Exceptions";
import { useLoginGuard } from "./AppState";
import { ExceptionCard } from "./ExceptionCard";
import { LoadingOverlay } from "./LoadingOverlay";

class ValidationException extends Exception {
    constructor(message: string) {
        super(message);
    }

    getDisplayMessage(): string {
        return this.message;
    }

    get type(): string {
        return ValidationException.name;
    }
}

function IsDestinationUrlValid(dest: string): boolean {
    try {
        const url = new URL(dest);
        console.log(url);
        return url.protocol === "https:" || url.protocol === "http:";
    } catch (_) {
        return false;
    }
}

const nameRegex = /^[a-zA-Z0-9_-]{1,128}$/;

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

        if (!destination || destination === "") {
            setException(new ValidationException("Empty destination"));
            return;
        }

        if (!IsDestinationUrlValid(destination)) {
            setException(new ValidationException("Destination is not a valid URL"));
            return;
        }

        if (!IsDestinationUrlValid(destination)) {
            setException(new ValidationException("Destination is not a valid URL"));
            return;
        }

        if (name.length > 0) {
            if (name.length > 128) {
                setException(new ValidationException("Name cannot be longer than 128 characters"));
                return;
            }

            if (!name.match(nameRegex)) {
                setException(new ValidationException("Name is not valid"));
                return;
            }
        }

        const sub = ShortenLink(destination, name, private_)
            .subscribe({
                next(value) {
                    setSubscription(undefined);
                    navigate(`/edit/${value.id}`);
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
                        <InputGroup id="input-destination" placeholder="https://example.com/" onChange={e => setDestination(e.currentTarget.value)} />
                    </FormGroup>
                    <FormGroup label="Name" labelFor="input-name">
                        <InputGroup id="input-name" onChange={e => setName(e.currentTarget.value)} />
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
