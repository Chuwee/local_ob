export const seasonTicketTransferPolicy = ['FRIENDS_AND_FAMILY', 'ALL'] as const;
export type SeasonTicketTransferPolicy = typeof seasonTicketTransferPolicy[number];

export interface SeasonTicketTransferSeats {
    enable_transfer_delay?: boolean;
    transfer_ticket_min_delay_time?: number;
    transfer_ticket_max_delay_time?: number;
    enable_recovery_delay?: boolean;
    recovery_ticket_max_delay_time?: number;
    enable_max_ticket_transfers?: boolean;
    transfer_policy?: SeasonTicketTransferPolicy;
    max_ticket_transfers?: number;
    enable_bulk: boolean;
    bulk_customer_types?: {
        id: number;
        code: string;
        name: string;
    }[];
    excluded_sessions?: number[];
}

export interface PutSeasonTicketTransferSeats {
    enable_transfer_delay?: boolean;
    transfer_ticket_min_delay_time?: number;
    transfer_ticket_max_delay_time?: number;
    enable_recovery_delay?: boolean;
    recovery_ticket_max_delay_time?: number;
    enable_max_ticket_transfers?: boolean;
    transfer_policy?: SeasonTicketTransferPolicy;
    max_ticket_transfers?: number;
    enable_bulk?: boolean;
    bulk_customer_types?: number[];
    excluded_sessions?: number[];
}
