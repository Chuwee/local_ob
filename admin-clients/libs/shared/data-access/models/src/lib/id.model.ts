export interface Id {
    id: number;
}

export interface IdString {
    id: string;
}

export interface Code {
    code: string;
}

export type IdOrCode = Id | Code | IdString;

export function compareWithIdOrCode(o1: IdOrCode, o2: IdOrCode): boolean {
    if (o1 && o2) {
        if ('id' in o1 && 'id' in o2) {
            return o1.id === o2.id;
        }
        if ('code' in o1 && 'code' in o2) {
            return o1.code === o2.code;
        }
    }
    return false;
}
