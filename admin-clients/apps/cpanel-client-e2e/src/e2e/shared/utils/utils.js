
export const getNestedValue = (obj, key) => {
    return key.split('.').reduce(function (result, key) {
        return result[key]
    }, obj);
}