export interface PutSeasonTicketRenewals {
    items: PutSeasonTicketRenewalsItem[];
}

export interface PutSeasonTicketRenewalsItem {
    user_id: string;
    id: string;
    seat_id?: number;
    rate_id?: number;
    renewal_substatus?: string | null;
    auto_renewal?: boolean;
}
