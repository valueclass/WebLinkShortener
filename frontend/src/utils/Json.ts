import { mergeMap, OperatorFunction } from "rxjs";
import { ErrorException, Exception } from "./Exceptions";

export class JsonException extends Exception {

    get type(): string {
        return JsonException.name;
    }
}

function mapJsonErrors(err: any) {
    if (err instanceof Error)
        throw new JsonException('Failed to convert response to Json', new ErrorException(err));

    throw new JsonException('Failed to convert response to Json');
}

export function responseToJson<T>(): OperatorFunction<Response, T> {
    return (source) => {
        return source.pipe(
            mergeMap(res => res.json().catch(err => mapJsonErrors(err)))
        );
    }
}