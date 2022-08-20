import { catchError, Observable } from "rxjs";
import { fromFetch } from "rxjs/fetch";
import { Exception, ErrorException } from "./Exceptions";

export class FetchException extends Exception {

    get type(): string {
        return FetchException.name;
    }

    getDisplayMessage(): string {
        return "Network error";
    }
}

export function Fetch(request: Request | RequestInfo, init?: RequestInit): Observable<Response> {
    const req = request instanceof Request ? request : new Request(request, init);

    return fromFetch(req)
        .pipe(
            catchError((err, _) => {
                if (err instanceof Error)
                    throw new FetchException('Failed to fetch data', new ErrorException(err));

                throw new FetchException('Failed to fetch data', err instanceof Exception ? err : null);
            })
        );
}