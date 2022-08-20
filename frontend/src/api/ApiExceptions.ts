import { Exception } from "../utils/Exceptions";
import { isString } from "../utils/Types";

export class ApiException extends Exception {
    protected _status: number;

    public constructor(status: number);
    public constructor(mesage: string, status: number);
    public constructor(status: number, cause: Exception);
    public constructor(message: string, status: number, cause: Exception);
    public constructor(a: number | string, b?: number | Exception, c?: Exception) {
        const message = isString(a) ? (a as string) : null;
        const cause = b instanceof Exception ? b : (c || null);
        const status = isString(a) ? (b as number) : a;

        if (message) {
            if (cause) super(message, cause);
            else super(message);
        } else {
            super();
        }

        this._status = status;
    }

    get status() {
        return this._status;
    }

    get type(): string {
        return ApiException.name;
    }

    getDisplayMessage() {
        return "Unknown API Exception";
    }
}

export class UnauthorizedException extends ApiException {
    public static readonly STATUS_CODE = 401;

    public constructor(message: string, cause: Exception);
    public constructor(message: string);
    public constructor();
    public constructor(message?: string, cause?: Exception) {
        if (message) {
            if (cause) super(message, UnauthorizedException.STATUS_CODE, cause);
            else super(message, UnauthorizedException.STATUS_CODE);
        } else {
            if (cause) super(UnauthorizedException.STATUS_CODE, cause);
            else super(UnauthorizedException.STATUS_CODE);
        }
    }

    get type(): string {
        return UnauthorizedException.name
    }

    getDisplayMessage(): string {
        return this.message;
    }
}

export class ServerException extends ApiException {
    public static readonly STATUS_CODE = 500;

    public constructor(message: string, cause: Exception);
    public constructor(message: string);
    public constructor();
    public constructor(message?: string, cause?: Exception) {
        if (message) {
            if (cause) super(message, ServerException.STATUS_CODE, cause);
            else super(message, ServerException.STATUS_CODE);
        } else {
            if (cause) super(ServerException.STATUS_CODE, cause);
            else super(ServerException.STATUS_CODE);
        }
    }

    get type(): string {
        return ServerException.name;
    }

    getDisplayMessage(): string {
        return "Internal Server Exception";
    }
}