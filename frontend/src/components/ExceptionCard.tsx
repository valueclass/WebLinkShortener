import { Button, Card } from "@blueprintjs/core";
import { Exception } from "../utils/Exceptions";

interface ExceptionCardProps {
    message: string
    exception?: Exception;
    onClose?(): void;
}

export function ExceptionCard({ message, exception, onClose }: ExceptionCardProps) {
    const close = () => {
        if (onClose) onClose();
    }

    if (exception) {
        return (
            <Card className="bg-red-500 my-5 text-slate-100 text-sm font-semibold">
                <span>{message} {exception.getDisplayMessage()}</span>
                <br />
                <Button className="bp4-intent-danger mt-2" text="Close" onClick={_ => close()} />
            </Card>
        );
    } else {
        return (<></>);
    }
}
