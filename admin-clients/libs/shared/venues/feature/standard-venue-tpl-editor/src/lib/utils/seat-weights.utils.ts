const minValue = 96;
const maxValue = 255;

export const maxWeightValue = 100;

export function getWeightsColors(): string[] {
    const colors: string[] = [];
    for (let i = minValue; i <= maxValue; i++) {
        colors.push(getColor([minValue, i, maxValue]));
    }
    for (let i = 1; i <= maxValue - minValue; i++) {
        colors.push(getColor([minValue, maxValue, maxValue - i]));
    }
    for (let i = minValue + 1; i <= maxValue; i++) {
        colors.push(getColor([i, maxValue, minValue]));
    }
    for (let i = 1; i <= maxValue - minValue; i++) {
        colors.push(getColor([maxValue, maxValue - i, minValue]));
    }
    return colors;
}

export function getWeightsKeyColors(): string[] {
    return [
        getColor([minValue, minValue, maxValue]),
        getColor([minValue, maxValue, maxValue]),
        getColor([minValue, maxValue, minValue]),
        getColor([maxValue, maxValue, minValue]),
        getColor([maxValue, minValue, minValue])
    ];
}

export function weightToColor(weightColors: string[], weight: number, minValue: number, maxValue: number): string {
    return weightColors[Math.round((weight - minValue) / (maxValue - minValue) * (weightColors.length - 1))];
}

export function colorToWeight(weightColors: string[], color: string, minValue = 0, maxValue: number = maxWeightValue): number {
    const index = weightColors.indexOf(color);
    if (index === -1) {
        console.warn('No available color in the weights colors collection: ' + color);
        return 0;
    } else {
        return Math.round(((index * (maxValue - minValue)) / (weightColors.length - 1)) + minValue);
    }
}

function getColor(colorComps: number[]): string {
    return '#' + colorComps.map(colorComp => colorComp.toString(16).padStart(2, '0')).join('');
}
