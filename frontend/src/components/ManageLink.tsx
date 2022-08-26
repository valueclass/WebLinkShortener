import { Button, Card, Checkbox, Divider, FormGroup, H2, InputGroup, Intent, Position, Tag, Toaster } from "@blueprintjs/core";
import { FormEvent, useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router";
import { Subscription } from "rxjs";
import { Link, FetchLink, DisableLink, EditLink, EnableLink, RemoveLink } from "../api/Api";
import { NotFoundException } from "../api/ApiExceptions";
import { Exception } from "../utils/Exceptions";
import { Runnable } from "../utils/Types";
import { LinkValidator } from "../utils/Validators";
import { useLoginGuard } from "./AppState";
import { ExceptionCard } from "./ExceptionCard";
import { LoadingOverlay } from "./LoadingOverlay";

const ManageLinkToaster = Toaster.create({
    position: Position.TOP_RIGHT
});

export function ManageLink() {
    useLoginGuard();

    const { id } = useParams();
    const [link, setLink] = useState<Link>();
    const [exception, setException] = useState<Exception>();
    const [exceptionCardMessage, setExceptionCardMessage] = useState<string>('');
    const [destination, setDestination] = useState<string>('');
    const [name, setName] = useState<string>('');
    const [private_, setPrivate] = useState<boolean>(false);
    const [editing, setEditing] = useState<boolean>(false);
    const [subscription, setSubscription] = useState<Subscription>();
    const navigate = useNavigate();

    useEffect(() => {
        if (!id) {
            navigate('/');
            return;
        }

        const sub = fetchData(id, () => { });
        return () => sub.unsubscribe();
    }, [id]);

    useEffect(() => () => {
        if (subscription)
            subscription.unsubscribe();
    }, [subscription]);

    const updateState = (link: Link) => {
        setLink(link);
        setDestination(link.destination);
        setName(link.source);
        setPrivate(link.private);
    }

    const handler = (event: FormEvent) => {
        event.preventDefault();

        if (!link) {
            navigate('/missingno');
            return;
        }

        const dst = destination !== link.destination ? destination : null;
        const src = name !== link.source ? name : null;
        const prv = private_ !== link.private ? private_ : null;

        const ex = new LinkValidator(src, dst).validate();
        if (ex) {
            setException(ex);
            setExceptionCardMessage('Link is invalid, cannot update: ');
            return;
        }

        const sub = EditLink(link.id, dst, src, prv)
            .subscribe({
                next(link) {
                    updateState(link);
                },
                error(ex) {
                    setExceptionCardMessage('Failed to update link: ');
                    setException(ex);
                    setSubscription(undefined);
                },
                complete() {
                    setEditing(false);
                    setSubscription(undefined);
                },
            });

        setSubscription(sub);
    }

    const fetchData = (id: string, onComplete: Runnable): Subscription => {
        return FetchLink(id)
            .subscribe({
                next(link) {
                    updateState(link);
                },
                error(ex) {
                    if (ex instanceof NotFoundException) {
                        navigate('/missingno');
                    }

                    setException(ex);
                    setExceptionCardMessage('Failed to edit a link: ');
                },
                complete() {
                    onComplete();
                },
            });
    }

    const disable = () => {
        if (!link) {
            navigate('/missingno');
            return;
        }

        const sub = DisableLink(link.id)
            .subscribe({
                next(_) { },
                error(ex) {
                    setExceptionCardMessage('Failed to disable a link: ');
                    setException(ex);
                    setSubscription(undefined);
                },
                complete() {
                    setSubscription(fetchData(link.id, () => setSubscription(undefined)));
                }
            });

        setSubscription(sub);
    }

    const enable = () => {
        if (!link) {
            navigate('/missingno');
            return;
        }

        const sub = EnableLink(link.id)
            .subscribe({
                next(_) { },
                error(ex) {
                    setExceptionCardMessage('Failed to enable a link: ');
                    setException(ex);
                    setSubscription(undefined);
                },
                complete() {
                    setSubscription(fetchData(link.id, () => setSubscription(undefined)));
                },
            });

        setSubscription(sub);
    }

    const remove = () => {
        if (!link) {
            navigate('/missingno');
            return;
        }

        const sub = RemoveLink(link.id)
            .subscribe({
                next(_) { },
                error(ex) {
                    setExceptionCardMessage('Failed to delete a link: ');
                    setException(ex);
                    setSubscription(undefined);
                },
                complete() {
                    navigate('/');
                    setSubscription(undefined);
                },
            });

        setSubscription(sub);
    }

    const copy = () => {
        if (!('clipboard' in navigator))
            return;

        navigator.clipboard.writeText(`${process.env.REACT_APP_BASE_URL}/link/${link?.source}`)
            .then(
                _ => ManageLinkToaster.show({ message: 'Copied link to clipboard', intent: Intent.PRIMARY }),
                _ => ManageLinkToaster.show({ message: 'Failed to copy', intent: Intent.DANGER })
            );
    }

    const dirty = () => (private_ !== link?.private || destination !== link.destination || name !== link.source);
    const working = () => (!!subscription);

    return (
        <div>
            <Card className="max-w-5xl mx-auto">
                <H2>Manage</H2>
                <ExceptionCard message={exceptionCardMessage} exception={exception} onClose={() => setException(undefined)} />
                <LoadingOverlay show={working()}>
                    <form onSubmit={handler}>
                        <FormGroup label="Destination" labelFor="input-destination">
                            {editing ?
                                (
                                    <InputGroup id="input-destination" placeholder="https://example.com/" value={destination} onChange={e => setDestination(e.currentTarget.value.trim())} />
                                ) :
                                <span style={{ fontSize: '14px', margin: '0px 10px 0px 10px', height: '30px', lineHeight: '30px' }}>{destination}</span>
                            }
                        </FormGroup>

                        <FormGroup label="Name" labelFor="input-name">
                            {editing ?
                                (
                                    <InputGroup id="input-name" value={name} onChange={e => setName(e.currentTarget.value.trim())} />
                                ) :
                                <span style={{ fontSize: '14px', margin: '0px 10px 0px 10px', height: '30px', lineHeight: '30px' }}>{name}</span>
                            }
                        </FormGroup>
                        <div className="flex flex-row gap-x-1 mb-[15px]">
                            {editing ?
                                (
                                    <FormGroup label="Private" inline={true} labelFor="checkbox-private" style={{ margin: '0px' }}>
                                        <Checkbox style={{ margin: '4px 0px 0px 0px' }} id="checkbox-private" checked={private_} onChange={e => setPrivate(e.currentTarget.checked)} />
                                    </FormGroup>
                                ) :
                                (link?.private ? <Tag>Private</Tag> : <></>)
                            }
                            {link?.disabled ? <Tag style={{ maxHeight: '20px', lineHeight: '30px', verticalAlign: 'middle' }} className="self-center">Disabled</Tag> : <></>}
                        </div>
                        <div className="flex gap-x-1 mt-2">
                            <Button text={editing ? "Cancel" : "Edit"} icon={editing ? "cross" : "edit"} intent={editing ? Intent.DANGER : Intent.NONE} onClick={_ => setEditing(!editing)} />
                            {editing ? <Button type="submit" text="Save" icon="tick" disabled={!dirty()} /> : <></>}
                            <Divider />
                            <Button text={link?.disabled ? "Enable" : "Disable"} icon={link?.disabled ? "blank" : "disable"} onClick={_ => link?.disabled ? enable() : disable()} />
                            <Divider />
                            <Button text="Copy" icon="clipboard" onClick={_ => copy()} />
                            <Divider />
                            <Button text="Delete" icon="delete" intent={Intent.DANGER} onClick={_ => remove()} />
                        </div>
                    </form>
                </LoadingOverlay>
            </Card>
        </div>
    );
}
