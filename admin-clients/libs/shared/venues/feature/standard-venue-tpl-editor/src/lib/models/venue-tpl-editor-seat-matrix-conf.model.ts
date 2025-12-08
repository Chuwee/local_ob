export const venueTplEditorSeatMatrixLimits = {
    rows: 100,
    seats: 150,
    size: 100,
    seatsDistance: 100,
    rowsDistance: 100
};

export interface VenueTplEditorSeatMatrixConf {
    commitConfiguration: boolean;
    continueRows: boolean;
    matrix: {
        sector: number;
        rows: number;
        seats: number;
        seatsSize: number;
        seatsDistance: number;
        rowsDistance: number;
    };
    rows: {
        range: SeatMatrixConfRangeType;
        numeration: SeatMatrixConfNumerationType;
        direction: SeatMatrixConfRowDirection;
        numericStartsWith: number;
        alphabeticStartsWith: string;
        prefix: string;
        show: boolean;
        position: SeatMatrixConfRowLabelPosition;
    };
    seats: {
        numTracks: 1 | 2;
        seatsRange: SeatMatrixConfRangeType;
        track1: {
            numeration: SeatMatrixConfNumerationType;
            direction: SeatMatrixConfSeatDirection;
            numericStartsWith: number;
            alphabeticStartsWith: string;
            seats: number;
        };
        track2: {
            numeration: SeatMatrixConfNumerationType;
            direction: SeatMatrixConfSeatDirection;
            numericStartsWith: number;
            alphabeticStartsWith: string;
            seats: number;
        };
    };
    rowContinuation: {
        sector: number;
        seats: number;
        fromRow: number;
        toRow: number;
        rowsDirection: SeatMatrixConfRowDirection;
        seatsDirection: SeatMatrixConfSeatDirection;
        seatsSize: number;
        seatsDistance: number;
        rowsDistance: number;
    };
}

export enum SeatMatrixConfRangeType {
    numeric = 'numeric',
    alphabetic = 'alphabetic'
}

export enum SeatMatrixConfNumerationType {
    correlative = 'correlative',
    even = 'even', // par
    odd = 'odd' // impar
}

export enum SeatMatrixConfRowDirection {
    up = 'up',
    down = 'down'
}

export enum SeatMatrixConfSeatDirection {
    left = 'left',
    right = 'right'
}

export enum SeatMatrixConfRowLabelPosition {
    left = 'left',
    right = 'right',
    both = 'both'
}

export const venueTplEditorSeatMatrixConfInitValue: VenueTplEditorSeatMatrixConf = {
    commitConfiguration: false,
    continueRows: false,
    matrix: {
        sector: null,
        rows: 5,
        seats: 10,
        seatsSize: 10,
        seatsDistance: 2,
        rowsDistance: 6
    },
    rows: {
        range: SeatMatrixConfRangeType.numeric,
        numeration: SeatMatrixConfNumerationType.correlative,
        direction: SeatMatrixConfRowDirection.up,
        numericStartsWith: 1,
        alphabeticStartsWith: 'A',
        prefix: '',
        show: true,
        position: SeatMatrixConfRowLabelPosition.left
    },
    seats: {
        numTracks: 1,
        seatsRange: SeatMatrixConfRangeType.numeric,
        track1: {
            numeration: SeatMatrixConfNumerationType.correlative,
            direction: SeatMatrixConfSeatDirection.right,
            numericStartsWith: 1,
            alphabeticStartsWith: 'A',
            seats: 10
        },
        track2: {
            numeration: SeatMatrixConfNumerationType.even,
            direction: SeatMatrixConfSeatDirection.right,
            numericStartsWith: 2,
            alphabeticStartsWith: 'A',
            seats: 5
        }
    },
    rowContinuation: {
        sector: null,
        seats: 5,
        fromRow: null,
        toRow: null,
        rowsDirection: SeatMatrixConfRowDirection.up,
        seatsDirection: SeatMatrixConfSeatDirection.right,
        seatsSize: 10,
        seatsDistance: 2,
        rowsDistance: 6
    }
};
