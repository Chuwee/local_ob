export interface GetTotalRecipientsRequest {
    limit: number;
    offset: number;
    entity_id: number;
    event_id: number[];
    session_id?: number[];
    channel_id?: number[];
    purchase_from?: string;
    purchase_to?: string;
    exclude_commercial_mailing_not_allowed?: boolean;
}
