export enum SeatNotLinkableReason {
    alreadyLinked = 'SEAT_ALREADY_LINKED',
    sold = 'SEAT_SOLD_IN_INDIVIDUAL_SESSION',
    notCompatible = 'SEAT_NOT_COMPATIBLE',
    blocked = 'SEAT_BLOCKED_IN_INDIVIDUAL_SESSION',
    notAvailable = 'SEAT_NOT_AVAILABLE_IN_INDIVIDUAL_SESSION',
    killed = 'SEAT_KILLED_IN_INDIVIDUAL_SESSION'
}
