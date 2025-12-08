export enum SeatStatus {
    unknown = 'UNKNOWN', // 0
    free = 'FREE', // 1
    sold = 'SOLD', // 2
    promotorLocked = 'PROMOTOR_LOCKED', // 3
    systemLocked = 'SYSTEM_LOCKED', // 4
    booked = 'BOOKED', // 5
    kill = 'KILL', // 6
    emitted = 'EMITTED', // 7
    validated = 'VALIDATED', // 8 not yet available in api
    inRefund = 'INREFUND', // 9 not yet available in api
    cancelled = 'CANCELLED', // 10 not yet available in api
    presoldLocked = 'PRESOLD_LOCKED', // 11
    soldLocked = 'SOLD_LOCKED', // 12 not yet available in api
    gift = 'GIFT', // 13
    seasonLocked = 'SEASON_LOCKED', // 14
    externalLocked = 'EXTERNAL_LOCKED', // 15
    externalDelete = 'EXTERNAL_DELETE' // 16 not yet available in api
}

export const occupiedStatus = [SeatStatus.sold, SeatStatus.booked, SeatStatus.emitted, SeatStatus.gift];
