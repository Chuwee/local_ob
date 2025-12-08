export interface SeasonTicketChangeSeatPrice {
    relation_id: number;
    season_ticket_id: number;
    source_price_type_id: number;
    source_price_type_name: string;
    target_price_type_id: number;
    target_price_type_name: string;
    rate_id: number;
    rate_name: string;
    value: number;
}
