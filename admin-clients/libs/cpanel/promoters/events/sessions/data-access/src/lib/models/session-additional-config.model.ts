export interface SessionAdditionalConfig {
    avet_external_operative: boolean;
    avet_match: {
        name: string;
        match_date: string;
        start_sales_date: string;
        end_sales_date: string;
        match_date_confirmed: boolean;
    };
}
