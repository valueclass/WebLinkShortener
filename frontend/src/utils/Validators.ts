import { Exception } from "./Exceptions";
import { Nullable } from "./Types";

export class LinkValidationException extends Exception {

    getDisplayMessage(): string {
        return this.message;
    }

    get type(): string {
        return LinkValidationException.name;
    }
}

export class LinkValidator {
    private static readonly NAME_REGEX = /^[a-zA-Z0-9_-]{1,128}$/;
    private static readonly PROTOCOLS = ["https:", "http:"];

    private name?: Nullable<string>;
    private destination?: Nullable<string>;

    constructor(name?: Nullable<string>, destination?: Nullable<string>) {
        this.name = name;
        this.destination = destination;
    }

    private validUrl(url: string, protocols: string[]): boolean {
        try {
            const real = new URL(url);
            console.log('Validator: real url', real);
            return protocols.some(proto => real.protocol === proto);
        } catch (_) {
            return false;
        }
    }

    private validateName(): Nullable<LinkValidationException> {
        if (!this.name || this.name === '')
            return null;

        if (this.name.length > 128)
            return new LinkValidationException("Name cannot be longer than 128 characters");

        if (!this.name.match(LinkValidator.NAME_REGEX))
            return new LinkValidationException("Name is not valid");

        return null;
    }

    private validateDestination(): Nullable<LinkValidationException> {
        if (!this.destination || this.destination === '')
            return new LinkValidationException("Destination is empty");

        if (!this.validUrl(this.destination, LinkValidator.PROTOCOLS))
            return new LinkValidationException("Destination is not a valid URL");

        return null;
    }

    public validate(validateName: boolean, validateDestination: boolean): Nullable<LinkValidationException> {
        if (validateName) {
            const nameValidationResult = this.validateName();
            if (nameValidationResult)
                return nameValidationResult;
        }

        if (validateDestination) {
            const destinationValidationResult = this.validateDestination();
            if (destinationValidationResult)
                return destinationValidationResult;
        }

        return null;
    }
}
