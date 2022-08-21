import { Alignment, Navbar } from "@blueprintjs/core";
import { useLocation } from "react-router";
import { useAppState } from "./AppState";
import { LinkButton } from "./LinkButton";

interface ButtonsProps {
    auth: boolean,
    pathname: string
}

function Buttons({ auth, pathname }: ButtonsProps) {
    if (auth) {
        console.log('pathname is', pathname);

        return (
            <>
                <LinkButton className="bp4-minimal" icon="user" text="Account" to="/account" disabled={pathname === '/account'} />
                <LinkButton className="bp4-minimal" icon="home" text="Home" to="/" disabled={pathname === '/'} />
                <LinkButton className="bp4-minimal" icon="log-out" text="Log out" to="/logout" disabled={pathname === '/logout'} />
            </>
        );
    } else {
        return <></>
    }
}

export function Nav() {
    const state = useAppState();
    const location = useLocation();

    return (
        <Navbar>
            <Navbar.Group align={Alignment.LEFT}>
                <Navbar.Heading>WebLinkShortener</Navbar.Heading>
                <Navbar.Divider />
            </Navbar.Group>
            <Navbar.Group align={Alignment.RIGHT}>
                <Buttons auth={!!state.user} pathname={location.pathname} />
            </Navbar.Group>
        </Navbar>
    );
}
