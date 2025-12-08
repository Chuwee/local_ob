interface Point {
    x: number;
    y: number;
}

interface Line {
    p1: Point;
    p2: Point;
}

export function getHypotenuse(side1: number, side2?: number): number {
    return Math.sqrt(Math.pow(side1, 2) + Math.pow(side2 ?? side1, 2));
}

export function getDistanceBetween(p1: Point, p2: Point): number {
    return Math.sqrt(Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y - p1.y, 2));
}

export function getMiddlePoint(p1: Point, p2: Point): { x: number; y: number } {
    return { x: (p1.x + p2.x) / 2, y: (p1.y + p2.y) / 2 };
}

export function getAngleInRadians(p1: Point, p2: Point): number {
    return Math.atan2(p2.y - p1.y, p2.x - p1.x);
}

export function getAngleInDegrees(p1: Point, p2: Point): number {
    return getAngleInRadians(p1, p2) * 180 / Math.PI;
}

export function getAngleInDegreesRounded(p1: Point, p2: Point): number {
    return Math.round(roundDecimals(getAngleInDegrees(p1, p2), 2));
}

export function roundDecimals(value: number, digits = 1): number {
    const factor = Math.pow(10, digits);
    return Math.round(value * factor) / factor;
}

export function linesCollide(l1: Line, l2: Line): boolean {
    const denominator = (l2.p2.y - l2.p1.y) * (l1.p2.x - l1.p1.x) - (l2.p2.x - l2.p1.x) * (l1.p2.y - l1.p1.y);
    if (denominator !== 0) {
        const t1 = ((l2.p2.x - l2.p1.x) * (l1.p1.y - l2.p1.y) - (l2.p2.y - l2.p1.y) * (l1.p1.x - l2.p1.x)) / denominator;
        const t2 = ((l1.p2.x - l1.p1.x) * (l1.p1.y - l2.p1.y) - (l1.p2.y - l1.p1.y) * (l1.p1.x - l2.p1.x)) / denominator;
        return !(t1 < 0 || t1 > 1 || t2 < 0 || t2 > 1);
    } else {
        return false; // Lines are parallel, no intersection
    }
}
