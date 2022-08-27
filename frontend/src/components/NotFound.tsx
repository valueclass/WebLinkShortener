import { Card, H1, Intent } from "@blueprintjs/core";
import { LinkButton } from "./LinkButton";

export function NotFound() {
    return (
        <Card className="max-w-lg mx-auto">
            <H1>Not Found!</H1>
            <div className="w-full mb-4">
                <span className="text-base w-full">The resource was not found (typo?)</span>
            </div>
            <LinkButton icon="home" text="Home" to="/" minimal={true} className="w-full" intent={Intent.PRIMARY} />
        </Card>
    );
}
