export interface PutSeasonTicketChangeSeats {
    enable?: boolean;
    start_date?: string;
    end_date?: string;
    enable_max_value?: boolean;
    max_value?: number;
    changed_seat_quota?: {
        enable?: boolean;
        id?: number;
    };
    fixed_surcharge?: number;
    changed_seat_status?: string;
    changed_seat_block_reason_id?: number;
    limit_change_seat_quotas?: {
        enable?: boolean;
        quota_ids?: number[];
    };
}
