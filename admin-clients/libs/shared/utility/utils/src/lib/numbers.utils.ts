/**
 * Given a value, this funcition will return an number if possible otherwise it will throw an error
 * @param value the value we want to use as a number
 * @returns a validated number
 */
export function convertAndValidateNumber(value: number | string): number {
    if (typeof value === 'string' && !isNaN(Number(value) - parseFloat(value))) {
        return Number(value);
    }
    if (typeof value !== 'number') {
        throw new Error(`${value} is not a number`);
    }
    return value;
}
