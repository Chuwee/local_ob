export interface UpdateSeasonTicketChannelsRequest {
    settings: {
        use_season_ticket_dates: boolean;
        release: {
            enabled: boolean;
            date: string;
        };
        sale: {
            enabled: boolean;
            start_date: string;
            end_date: string;
        };
    };
    use_all_quotas: boolean;
    quotas: number[];
}
