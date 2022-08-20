import { Observable, of } from "rxjs";
import { Exception } from "../utils/Exceptions";
import { Fetch } from "../utils/Fetch";
import { responseToJson } from "../utils/Json";
import { isString } from "../utils/Types";
import { then } from "../utils/Operators";

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
}

interface ErrorResponse {
    status: number;
    message: string;
}

export interface User {
    id: string,
    username: string
}

function ErrorResponseToException(res: ErrorResponse): ApiException {
    switch (res.status) {
        case UnauthorizedException.STATUS_CODE:
            return new UnauthorizedException(res.message);
        default:
            return new ApiException(res.message, res.status);
    }
}

function ApiFetch(request: Request | RequestInfo, init?: RequestInit): Observable<Response> {
    const req = request instanceof Request ? request : new Request(`/api/v1/${request[0] === '/' ? request.substring(1) : request}`, init);

    return Fetch(req)
        .pipe(
            source => new Observable(subscriber => {
                console.trace('ApiFetch subscriber', subscriber);

                source.subscribe({
                    next: res => {
                        if (res.ok) {
                            subscriber.next(res);
                            subscriber.complete();
                        } else {
                            of(res)
                                .pipe(responseToJson())
                                .subscribe({
                                    next: data => {
                                        const response = data as ErrorResponse;
                                        subscriber.error(ErrorResponseToException(response));
                                        subscriber.complete();
                                    },
                                    error: err => subscriber.error(err),
                                    complete: () => {}
                                });
                        }
                    },
                    error: err => subscriber.error(err),
                    complete: () => {}
                });
            })
        );
}

function ApiPost(url: string, init: RequestInit = {}): Observable<Response> {
    return ApiFetch(url, {
        method: 'POST',
        ...init
    });
}

function ApiGet(url: string, init: RequestInit = {}): Observable<Response> {
    return ApiFetch(url, {
        method: 'GET',
        ...init
    });
}

export function UserLogin(username: string, password: string): Observable<User> {
    const payload = {
        username: username,
        password: password
    }

    return ApiPost('/users/login', {
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(payload)
    }).pipe(responseToJson());
}

export function UserLogout(): Observable<any> {
    return ApiPost('/users/logout').pipe(then());
}

export function WhoAmI(): Observable<User> {
    return ApiGet('/users/whoami').pipe(responseToJson());
}
