export interface AutomaticSaleConfig {
    sort?: boolean;
    use_seat_mappings?: boolean;
    use_ob_ids_for_seat_mappings?: boolean;
    default_purchase_language?: boolean;
    skip_add_attendant?: boolean;
    allow_break_adjacent_seats?: boolean;
    use_locators?: boolean;
    force_multi_ticket?: boolean;
    channel_id?: number;
    preview_token?: string;
    add_extra_attendee_information?: boolean;
}

export interface AutomaticSaleConfigCsv extends AutomaticSaleConfig {
    automatic_type?: 'SECTOR' | 'PRICE_ZONE';
}
