export interface SeasonTicketReleaseSeats {
    release_seat_enabled?: boolean;
    enable_release_delay?: boolean;
    release_min_delay_time?: number;
    release_max_delay_time?: number;
    enable_recover_delay?: boolean;
    recover_max_delay_time?: number;
    customer_percentage: number;
    enable_max_releases: boolean;
    max_releases: number;
    earnings_limit?: {
        enabled: boolean;
        percentage?: number;
    }
    excluded_sessions?: number[];
}
