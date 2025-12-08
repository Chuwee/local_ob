export interface PostRelocationSeats {
    seats: RelocationSeat[];
}

export interface RelocationSeat {
    source_id: number;
    destination_id: number;
}