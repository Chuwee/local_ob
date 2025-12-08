export function areObjectsEquals<T extends object>(x: T, y: T): boolean {
    if (!x || !y || typeof x !== typeof y) {
        return false;
    }
    for (const key in Object.keys(x)) {
        if (!(key in y) || x[key] !== y[key]) {
            return false;
        }
    }
    return true;
}

export function getOrPush<T extends object>(array: T[], item: T): T {
    const found = array.filter(iter => areObjectsEquals(item, iter));
    if (found.length > 0) {
        return found[0];
    }
    array.push(item);
    return item;
}

export function deepEqual<T>(x: T, y: T): boolean {
    const ok = Object.keys, tx = typeof x, ty = typeof y;
    return x && y && tx === 'object' && tx === ty ? (
        ok(x).length === ok(y).length &&
        ok(x).every(key => deepEqual(x[key], y[key]))
    ) : (x === y || (Number.isNaN(x) && Number.isNaN(y)));
}

export function mergeObjects<T>(target: T, source: Partial<T>): T {
    Object.keys(source).forEach(key => {
        const newSourceField = source[key];
        if (newSourceField !== null && newSourceField !== undefined) {
            target[key] ??= null;
            if (!Array.isArray(newSourceField) && newSourceField instanceof Object) {
                target[key] = mergeObjects(target[key] ?? {}, newSourceField);
            } else if (Array.isArray(newSourceField)) {
                target[key] = cloneObjectArray(newSourceField);
            } else {
                target[key] = cloneObject(newSourceField);
            }
        } else {
            target[key] = target[key] ?? newSourceField;
        }
    });
    return target;
}

export function cloneObject<T>(source: T): T {
    if (source != null) {
        let result: unknown;
        if (Array.isArray(source)) {
            result = cloneObjectArray(source);
        } else if (source instanceof Map) {
            const newMap = new Map<unknown, unknown>();
            source.forEach((value, key) => newMap.set(key, cloneObject(value)));
            result = newMap;
        } else if (source instanceof Set) {
            const newSet = new Set<unknown>();
            source.forEach(value => newSet.add(value));
            result = newSet;
        } else if (source instanceof Object) {
            result = mergeObjects({} as T, source);
        }
        if (result) {
            return result as T;
        }
    }
    return source;
}

export function cloneObjectArray<T>(source: T[]): T[] {
    if (source) {
        return source.map(element => cloneObject(element));
    } else {
        return source;
    }
}

type ObjectPrefix<T extends string> = T extends '' ? '' : `.${T}`;

export type NestedObjectKeys<T> = (T extends object ?
    { [K in Exclude<keyof T, symbol>]: `${K}${ObjectPrefix<NestedObjectKeys<T[K]>>}` }[Exclude<keyof T, symbol>]
    : '') extends infer D ? Extract<D, string> : never;

export function distinctByField<T, U>(items: T[], fieldAccessor: (v: T) => U, mergeLastItems = false): T[] {
    const result: T[] = [];
    const addedItemsMap = new Map<U, T>();
    const mergeDataFunc = mergeLastItems ?
        ((item: T) => {
            Object.assign(addedItemsMap.get(fieldAccessor(item)), item);
        })
        : null;
    items.forEach(item => {
        if (!addedItemsMap.has(fieldAccessor(item))) {
            result.push(item);
            addedItemsMap.set(fieldAccessor(item), item);
        } else {
            mergeDataFunc?.(item);
        }
    });
    return result;
}
