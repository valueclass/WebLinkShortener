export type Nullable<T> = T | null;

export function isString(s: any): s is String {
    return typeof s === 'string' || s instanceof String;
}
