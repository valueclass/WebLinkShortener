import { Button, Card, Checkbox, FormGroup, InputGroup, Intent, NonIdealState, Position, Spinner, Tag, Toaster } from "@blueprintjs/core";
import { FormEvent, useEffect, useState } from "react";
import { BoolQueryParam, FetchLinks, Link } from "../api/Api";
import { Exception } from "../utils/Exceptions";
import { useLoginGuard } from "./AppState";
import { LinkButton } from "./LinkButton";

const includePrivateByDefault = true;
const includePublicByDefault = true;
const includeDisabledByDefault = false;
const defaultMaxResults = 30;

const DashboardToaster = Toaster.create({
    position: Position.TOP_RIGHT
});

enum State {
    LOADING,
    ERROR,
    NO_RESULTS,
    NO_SEARCH_RESULTS,
    DONE
}

function CreateButton() {
    return (<LinkButton to="/shorten" text="Create" icon="plus" minimal={true} outlined={true} intent={Intent.PRIMARY} />);
}

interface LinkInfoProps {
    link: Link
}

function LinkInfo({ link }: LinkInfoProps) {
    const copy = () => {
        if (!('clipboard' in navigator))
            return;

        navigator.clipboard.writeText(`${process.env.REACT_APP_BASE_URL}/link/${link.name}`)
            .then(
                _ => DashboardToaster.show({ message: 'Copied link to clipboard', intent: Intent.PRIMARY }),
                _ => DashboardToaster.show({ message: 'Failed to copy', intent: Intent.DANGER })
            );
    }

    return (
        <div className="w-full border-stone-300 border rounded-sm grid grid-cols-8 p-1 sm:p-2 mt-2">
            <span className="font-semibold col-span-2">Name</span>
            <span className="col-start-3 col-span-6">
                {link.name}
            </span>
            <span className="font-semibold col-span-2">Destination</span>
            <span className="col-start-3 col-span-6 truncate">
                <a href={link.destination} className="text-blue-500">{link.destination}</a>
            </span>

            <div className="col-span-8 mt-1">
                {link.private ? <Tag>Private</Tag> : <></>}
                {link.disabled ? <Tag>Disabled</Tag> : <></>}
            </div>

            <div className="col-span-8 mt-2 flex gap-y-1 flex-col sm:flex-row sm:gap-x-1">
                <LinkButton icon="edit" text="Edit" className="w-full sm:w-auto" to={`/manage/${link.id}`} />
                <Button icon="clipboard" text="Copy" className="w-full sm:w-auto" onClick={() => copy()} />
            </div>
        </div>
    );
}

interface ContentProps {
    state: State,
    exception?: Exception,
    links: Link[]
}

function Content({ state, exception, links }: ContentProps) {
    switch (state) {
        case State.LOADING:
            return (<NonIdealState icon={<Spinner />} title="Loading" />);
        case State.ERROR:
            return (<NonIdealState icon="cross" title="Error" action={<Button text="Try again" icon="repeat" />} description={(
                <div>
                    <span className="font-semibold">Error: {exception?.getDisplayMessage() || "Unknown error"}</span>
                    <br />
                    <span>Try fetching links again</span>
                </div>
            )} />);
        case State.NO_RESULTS:
            return (<NonIdealState icon="blank" title="No results" description="Create a new short link" action={<CreateButton />} />);
        case State.NO_SEARCH_RESULTS:
            return (<NonIdealState icon="search" title="No search results" action={<CreateButton />} description={
                <div>
                    <span>Search did not match any links</span>
                    <br />
                    <span>Try searching for something else or create new link</span>
                </div>
            } />);
        case State.DONE:
            return (<div>{links.map((link, index) => (<LinkInfo key={index} link={link} />))}</div>);
    }
}

function FilterLinks(links: Link[], public_: boolean, private_: boolean, disabled: boolean, query: string, maxResults: number): Link[] {
    const filtered = links.filter(link => {
        if (link.private) {
            if (!private_)
                return false;
        } else {
            if (!public_)
                return false;
        }

        if (link.disabled && !disabled)
            return false;

        if (query && query !== '')
            return link.name.includes(query) || link.destination.includes(query);

        return true;
    });

    return filtered.slice(0, Math.min(filtered.length, maxResults));
}

export function Dashboard() {
    const { user } = useLoginGuard();
    const [state, setState] = useState<State>(State.LOADING);
    const [exception, setException] = useState<Exception>();
    const [private_, setPrivate] = useState<boolean>(includePrivateByDefault);
    const [disabled, setDisabled] = useState<boolean>(includeDisabledByDefault);
    const [public_, setPublic] = useState<boolean>(includePublicByDefault);
    const [maxResults, setMaxResults] = useState<number>(defaultMaxResults);
    const [query, setQuery] = useState<string>('');
    const [links, setLinks] = useState<Link[]>([]);
    const [results, setResults] = useState<Link[]>([]);

    useEffect(() => {
        if (!user)
            return () => { };

        const sub = FetchLinks(BoolQueryParam.INCLUDE, BoolQueryParam.INCLUDE, user.id)
            .subscribe({
                next(links) {
                    setLinks(links);

                    if (links.length > 0) {
                        setResults(FilterLinks(links, includePublicByDefault, includePrivateByDefault, includeDisabledByDefault, '', defaultMaxResults));
                        setState(State.DONE);
                    } else {
                        setState(State.NO_RESULTS);
                    }
                },
                error(ex) {
                    setException(ex);
                    setState(State.ERROR);
                },
                complete() { },
            });

        return () => sub.unsubscribe();
    }, [user]);

    const search = (event: FormEvent) => {
        event.preventDefault();

        const results = FilterLinks(links, public_, private_, disabled, query, maxResults);
        setResults(results);
        setState(results.length > 0 ? State.DONE : State.NO_SEARCH_RESULTS);
    }

    return (
        <div>
            <Card className="mb-4">
                <div className="flex flex-col sm:flex-row flex-wrap sm:flex-nowrap">
                    <form onSubmit={search} className="w-full sm:w-auto">
                        <div className="flex flex-col sm:flex-row">
                            <div className="flex flex-row">
                                <FormGroup label="Public" style={{ marginBottom: '0' }} inline={true} labelFor="checkbox-public">
                                    <Checkbox style={{ margin: '4px 0px 0px 0px' }} id="checkbox-public" checked={public_} onChange={e => setPublic(e.currentTarget.checked)} />
                                </FormGroup>
                                <FormGroup label="Private" style={{ marginBottom: '0' }} inline={true} labelFor="checkbox-private">
                                    <Checkbox style={{ margin: '4px 0px 0px 0px' }} id="checkbox-private" checked={private_} onChange={e => setPrivate(e.currentTarget.checked)} />
                                </FormGroup>
                                <FormGroup label="Disabled" style={{ marginBottom: '0' }} inline={true} labelFor="checkbox-disabled">
                                    <Checkbox style={{ margin: '4px 0px 0px 0px' }} id="checkbox-disabled" checked={disabled} onChange={e => setDisabled(e.currentTarget.checked)} />
                                </FormGroup>
                            </div>
                            <FormGroup label="Results" style={{ marginBottom: '0' }} className="mt-1 sm:mt-0" inline={true} labelFor="input-max-results">
                                <InputGroup type="number" value={maxResults.toString()} id="input-max-results" className="mx-2 max-w-[80px]" onChange={e => setMaxResults(e.currentTarget.valueAsNumber)} />
                            </FormGroup>
                            <InputGroup type="text" placeholder="Search..." className="mt-2 sm:mt-0" onChange={e => setQuery(e.currentTarget.value)} />
                            <Button icon="search" className="mt-2 sm:mt-0 sm:ml-1" type="submit" />
                        </div>
                    </form>
                </div>
            </Card >
            <Content state={state} exception={exception} links={results} />
        </div >
    );
}
