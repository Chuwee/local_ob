export interface NotificationRecipients {
    filter: {
        event_ids: number[];
        session_ids: number[];
        channel_ids: number[];
        purchase_date: {
            from: string;
            to: string;
            override: boolean;
        };
        exclude_commercial_mailing_not_allowed: boolean;
    };
}
