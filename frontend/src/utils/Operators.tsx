import { Observable, OperatorFunction } from "rxjs";

export function then(): OperatorFunction<any, void> {
    return source => {
        return new Observable(subscriber => {
            source.subscribe({
                next() { },
                error(err) {
                    subscriber.error(err);
                },
                complete() {
                    subscriber.complete();
                }
            });
        });
    }
}
