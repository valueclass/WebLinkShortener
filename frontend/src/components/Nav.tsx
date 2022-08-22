import { Alignment, Icon, Navbar } from "@blueprintjs/core";
import { useLocation } from "react-router";
import { useAppState } from "./AppState";
import { LinkButton } from "./LinkButton";

interface ButtonsProps {
    auth: boolean,
    pathname: string
}

function Buttons({ auth, pathname }: ButtonsProps) {
    if (auth) {
        return (
            <>
                <LinkButton className="bp4-minimal" to="/account" disabled={pathname === '/account'} icon={<Icon icon="person" style={{ margin: '0' }} />} text={<span className="hidden sm:block sm:ml-[7px]">Account</span>} />
                <LinkButton className="bp4-minimal" to="/" disabled={pathname === '/'} icon={<Icon icon="home" style={{ margin: '0' }} />} text={<span className="hidden sm:block sm:ml-[7px]">Home</span>} />
                <LinkButton className="bp4-minimal" to="/logout" disabled={pathname === '/logout'} icon={<Icon icon="log-out" style={{ margin: '0' }} />} text={<span className="hidden sm:block sm:ml-[7px]">Log out</span>} />
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
