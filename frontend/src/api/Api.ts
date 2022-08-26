import { Observable, of } from "rxjs";
import { Fetch } from "../utils/Fetch";
import { responseToJson } from "../utils/Json";
import { then } from "../utils/Operators";
import { Nullable } from "../utils/Types";
import { ApiException, NotFoundException, ServerException, UnauthorizedException } from "./ApiExceptions";

export function ErrorResponseToException(res: ErrorResponse): ApiException {
    switch (res.status) {
        case UnauthorizedException.STATUS_CODE:
            return new UnauthorizedException(res.message);
        case ServerException.STATUS_CODE:
            return new ServerException(res.message);
        case NotFoundException.STATUS_CODE:
            return new NotFoundException(res.message);
        default:
            return new ApiException(res.message, res.status);
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

export interface Link {
    id: string;
    destination: string;
    source: string;
    ownerId: string;
    created: string;
    modified: string;
    disabled: boolean;
    private: boolean;
}

export enum BoolQueryParam {
    TURE = "true",
    FALSE = "false",
    INCLUDE = "include"
}

function ApiFetch(request: Request | RequestInfo, init?: RequestInit): Observable<Response> {
    const req = request instanceof Request ? request : new Request(`/api/v1/${request[0] === '/' ? request.substring(1) : request}`, init);

    return Fetch(req)
        .pipe(
            source => new Observable(subscriber => {
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
                                    complete: () => { }
                                });
                        }
                    },
                    error: err => subscriber.error(err),
                    complete: () => { }
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

function ApiPostJson(url: string, payload: object, init: RequestInit = {}): Observable<Response> {
    return ApiPost(url, {
        ...init,
        body: JSON.stringify(payload),
        headers: {
            ...init.headers,
            'Content-Type': 'application/json'
        }
    });
}

function ApiGet(url: string, init: RequestInit = {}): Observable<Response> {
    return ApiFetch(url, {
        method: 'GET',
        ...init
    });
}

function ApiPatch(url: string, init: RequestInit = {}): Observable<Response> {
    return ApiFetch(url, {
        method: 'PATCH',
        ...init
    });
}


function ApiPatchJson(url: string, payload: object, init: RequestInit = {}): Observable<Response> {
    return ApiPatch(url, {
        ...init,
        body: JSON.stringify(payload),
        headers: {
            ...init.headers,
            'Content-Type': 'application/json'
        }
    });
}

function ApiDelete(url: string, init: RequestInit = {}): Observable<Response> {
    return ApiFetch(url, {
        ...init,
        method: 'DELETE'
    });
}

export function UserLogin(username: string, password: string): Observable<User> {
    const payload = {
        username: username,
        password: password
    }

    return ApiPostJson('/users/login', payload).pipe(responseToJson());
}

export function UserLogout(): Observable<any> {
    return ApiPost('/users/logout').pipe(then());
}

export function WhoAmI(): Observable<User> {
    return ApiGet('/users/whoami').pipe(responseToJson());
}

export function UpdatePassword(old: string, updated: string): Observable<void> {
    const payload = {
        old: old,
        updated: updated
    }

    return ApiPostJson('/users/password', payload).pipe(then());
}

export function FetchLinks(private_: BoolQueryParam, disabled: BoolQueryParam, userId: string): Observable<Link[]> {
    return ApiGet(`/links?private=${private_}&disabled=${disabled}&owner=${userId}`).pipe(responseToJson());
}

export function ShortenLink(destination: string, name: string, private_: boolean): Observable<Link> {
    const payload = {
        destination: destination,
        source: name,
        private: private_
    }

    return ApiPostJson('/links', payload).pipe(responseToJson());
}

export function FetchLink(id: string): Observable<Link> {
    return ApiGet(`/links/${id}`).pipe(responseToJson());
}

export function DisableLink(id: string): Observable<void> {
    return ApiPost(`/links/${id}/disable`).pipe(then());
}

export function EnableLink(id: string): Observable<void> {
    return ApiPost(`/links/${id}/enable`).pipe(then());
}

export function EditLink(id: string, destination: Nullable<string>, name: Nullable<string>, private_: Nullable<boolean>): Observable<Link> {
    const payload = {
        destination: destination,
        source: name,
        private: private_
    }

    return ApiPatchJson(`/links/${id}`, payload).pipe(responseToJson());
}

export function RemoveLink(id: string): Observable<void> {
    return ApiDelete(`/links/${id}`).pipe(then());
}
