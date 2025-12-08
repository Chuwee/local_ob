import { EdRow, EdSeat } from './venue-tpl-editor-venue-map-items.model';

export interface BlockChange {
    x: number;
    y: number;
    seatWrappers: [SeatWrapper, SeatWrapper];
    row: EdRow;
}

export interface Aisle {
    blockChanges: BlockChange[];
    points: string;
}

export interface SeatWrapper {
    element: Element;
    seat?: EdSeat;
    radius: number;
    x: number;
    y: number;
}

export interface SeatWrapperWithDistance {
    seatWrapper: SeatWrapper;
    distance: number;
}

export interface RowInfo {
    row: EdRow;
    seatWrappers: SeatWrapper[];
    points: string;
}
