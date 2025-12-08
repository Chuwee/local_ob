import moment from 'moment-timezone';

function hasNestedFields(object: unknown, key: string): boolean {
    return object && (object[key] instanceof Object) && !(object[key] instanceof Array);
}

export function getObjectWithChangedValues<T>(
    sourceObject: T,
    valuesConfig: Map<string, { oldValue: unknown; newValue: unknown }>
): T {
    const resultObject = Object.keys(sourceObject).reduce((accumulatedObject: T, key) => {
        if (hasNestedFields(accumulatedObject, key)) {
            accumulatedObject = {
                ...accumulatedObject,
                [key]: getObjectWithChangedValues(accumulatedObject[key], valuesConfig)
            };
        } else if (valuesConfig.has(key)) {
            accumulatedObject = {
                ...accumulatedObject,
                [key]: accumulatedObject[key] !== valuesConfig.get(key).oldValue ?
                    accumulatedObject[key] :
                    valuesConfig.get(key).newValue
            };

        }
        return accumulatedObject;
    }, sourceObject);

    return resultObject;
}

function isFieldToBeAdded(valuesToIgnore: unknown[], value: unknown): boolean {
    return !valuesToIgnore.includes(value);
}

export function getObjectWithoutIgnoredValues<T>(
    sourceObject: T,
    valuesToIgnore: unknown[] = []
): T {
    const resultObject = Object.keys(sourceObject).reduce<Partial<T>>((accumulatedObject, key) => {
        if (hasNestedFields(sourceObject, key)) {
            const nestedObject = getObjectWithoutIgnoredValues<T[keyof T]>(
                sourceObject[key],
                valuesToIgnore
            );
            if (Object.keys(nestedObject).length > 0) {
                accumulatedObject = {
                    ...accumulatedObject,
                    [key]: nestedObject
                };
            }
        } else if (isFieldToBeAdded(valuesToIgnore, sourceObject[key])) {
            accumulatedObject = {
                ...accumulatedObject,
                [key]: sourceObject[key]
            };
        }
        return accumulatedObject;
    }, {});

    return resultObject as T;
}

export function getFormattedDate(date: moment.Moment | string, format = 'YYYY-MM-DD'): string {
    return moment(date).format(format);
}
