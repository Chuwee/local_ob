import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'obfuscateString',
    pure: true,
    standalone: true
})
export class ObfuscateStringPipe implements PipeTransform {
    constructor() { }

    transform(value: string, obfuscatePattern = ObfuscatePattern.first, replaceChar = '*', maxLength = 9, obfuscateNum = 4): string {
        if (value === null) { return ''; }
        maxLength = maxLength > value.length ? value.length : maxLength;
        const nonObfuscateNum = maxLength - obfuscateNum;
        if (nonObfuscateNum > 0) {
            return obfuscateRules.get(obfuscatePattern)(value, obfuscateNum, nonObfuscateNum, replaceChar);
        }
        return replaceChar.repeat(obfuscateNum);
    }
}

export enum ObfuscatePattern {
    middle = 'MIDDLE',
    between = 'BETWEEN',
    last = 'LAST',
    first = 'FIRST'
}

const obfuscateRules: Map<ObfuscatePattern, (...[any]: any) => string> = new Map([
    [ObfuscatePattern.first, (value: string, obfuscateNum: number, nonObfuscateNum: number, replaceChar: string): string =>
        replaceChar.repeat(obfuscateNum) + value.slice((value.length - nonObfuscateNum), value.length)],
    [ObfuscatePattern.middle, (value: string, obfuscateNum: number, nonObfuscateNum: number, replaceChar: string): string =>
        value.slice(0, nonObfuscateNum / 2) + replaceChar.repeat(obfuscateNum) + value.slice(value.length - nonObfuscateNum / 2, value.length)],
    [ObfuscatePattern.between, (value: string, obfuscateNum: number, nonObfuscateNum: number, replaceChar: string): string =>
        replaceChar.repeat(obfuscateNum / 2) + value.slice(value.length / 2 - nonObfuscateNum / 2, value.length / 2 + nonObfuscateNum / 2) + replaceChar.repeat(obfuscateNum / 2)],
    [ObfuscatePattern.last, (value: string, obfuscateNum: number, nonObfuscateNum: number, replaceChar: string): string =>
        value.slice(0, nonObfuscateNum) + replaceChar.repeat(obfuscateNum)]
]
);
