export interface AvetMatch {
    id: number;
    name: string;
    match_date: string;
    start_sales_date: string;
    end_sales_date: string;
    match_date_confirmed: boolean;
    smart_booking_related: boolean;
}

export interface EventAdditionalConfig {
    avet_match_list: AvetMatch[];
}
