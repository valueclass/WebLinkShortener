import { Card, H2 } from "@blueprintjs/core";
import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { UserLogout } from "../api/Api";
import { useLoginGuard } from "./AppState";

interface CardContentProps {
    working: boolean
}

function CardContent({ working }: CardContentProps) {
    if (working) {
        return (
            <span>Working...</span>
        );
    } else {
        return (
            <div className="flex items-baseline">
                <span className="mr-1">Logged out.</span>
                <Link to="/login" className="text-blue-600">Log in</Link>
            </div>
        );
    }
}

export function Logout() {
    const state = useLoginGuard();
    const [working, setWorking] = useState<boolean>(false);
    const navigate = useNavigate();

    useEffect(() => {
        const doLogout = () => {
            state.onLogout();
            setWorking(false);
            navigate('/login');
        }

        setWorking(true);

        const sub = UserLogout()
            .subscribe({
                next(_) { },
                error(_) {
                    doLogout();
                },
                complete() {
                    doLogout();
                }
            });

        return () => sub.unsubscribe();
    }, [state, navigate]);

    return (
        <Card className="max-w-lg mx-auto">
            <H2>Log out</H2>
            <CardContent working={working} />
        </Card>
    );
}
