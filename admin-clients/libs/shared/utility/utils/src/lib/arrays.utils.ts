
export function unionWith<T extends { id?: unknown }>(
    array1: T[], array2: T[], comparator = (e1: T, e2: T) => e1.id === e2.id
): T[] {
    return [...array1, ...array2].reduce((acc, elem) => {
        const obj = acc.find(resultElem => comparator(resultElem, elem));
        return obj ? acc : acc.concat(elem);
    }, []);
}

export function differenceWith<T extends { id?: unknown }>(
    array1: T[], array2: T[], comparator = (e1: T, e2: T) => e1.id === e2.id
): T[] {
    return array1.filter(e1 => !array2.find(e2 => comparator(e1, e2)));
}
