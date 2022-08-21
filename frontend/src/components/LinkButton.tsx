import { Button, ButtonProps } from "@blueprintjs/core";
import { useNavigate } from "react-router";

interface LinkButtonProps {
    to: string,
}

export function LinkButton(props: LinkButtonProps & ButtonProps) {
    const navigate = useNavigate();

    return (
        <Button {...props} onClick={() => navigate(props.to)} />
    );
}
