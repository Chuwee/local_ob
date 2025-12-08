/**
 * Safely handles circular references
 */
export const stringify = (obj: unknown, indent = 0): string => {
    let cache = [];

    const replacer: (this: unknown, key: string, value: unknown) => unknown = (key, value) => {
        if (typeof value === 'function') { return 'function'; }
        if (typeof value === 'symbol') { return 'symbol'; }
        if (typeof value === 'undefined') { return null; }

        // Duplicate reference found, discard key
        return typeof value === 'object' && value !== null ?
            cache.includes(value) ? '[[Circular Reference]]' : cache.push(value) && value : value;
    };

    const result = JSON.stringify(obj, replacer, indent);
    cache = null;
    return result;
};
