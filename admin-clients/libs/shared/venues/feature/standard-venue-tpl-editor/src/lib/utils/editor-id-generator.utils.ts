export class IdGenerator {
    private static _nextId = -1;

    static getTempId(): number {
        return this._nextId--;
    }

    static getAlphanumericTempId(): string {
        return 'a' + this._nextId--;
    }

    static isTempId(id: number): boolean {
        return id < 0;
    }

    static isAlphanumericTempId(id: string): boolean {
        return id?.length > 2 && id.charAt(0) === 'a' && id.charAt(1) === '-' && !isNaN(Number(id.slice(2)));
    }
}
