export type Nullable<T> = T | null;
export type Consumer<T> = (t: T) => void;
export type Function<T, R> = (t: T) => R;

export function isString(s: any): s is String {
    return typeof s === 'string' || s instanceof String;
}
