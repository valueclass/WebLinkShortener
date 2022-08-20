import { isString, Nullable } from "./Types";

export class Exception {
    protected _message: string;
    protected _cause: Exception | null;
    private _type;

    public constructor(message: string, cause: Nullable<Exception>);
    public constructor(cause: Exception);
    public constructor(message: string);
    public constructor();

    public constructor(first?: string | Exception, second?: Nullable<Exception>) {
        this._message = first && isString(first) ? (first as string) : '';
        this._cause = second || null;

        this._type = this.type;
    }

    get message() {
        return this._message;
    }

    get cause() {
        return this._cause;
    }

    get type(): string {
        return Exception.name;
    }

    getDisplayMessage() {
        return "Exception";
    }
}

export class ErrorException<E extends Error> extends Exception {
    protected _error: E;

    public constructor(error: E) {
        super(error.message);
        this._error = error;
    }

    get type(): string {
        return ErrorException.name;
    }

    get error() {
        return this._error;
    }

    getDisplayMessage(): string {
        return "Internal Exception";
    }
}
