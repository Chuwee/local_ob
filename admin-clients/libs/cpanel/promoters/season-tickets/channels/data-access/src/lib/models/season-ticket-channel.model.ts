import { ChannelType } from '@admin-clients/cpanel/channels/data-access';
import { SeasonTicketSettingsLanguagesModel } from '@admin-clients/cpanel/promoters/season-tickets/data-access';

export interface SeasonTicketChannel {
    channel: {
        id: number;
        name: string;
        type?: ChannelType;
        is_v4?: boolean;
        entity: {
            id: number;
            name: string;
            logo: string;
        };
    };
    status: {
        request: string;
        sale: string;
        release: string;
    };
    season_ticket: {
        id: number;
        status: string;
    };
    settings: {
        secondary_market_enabled: boolean;
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
        booking: {
            enabled: boolean;
            start_date: string;
            end_date: string;
        };
        secondary_market_sale?: {
            enabled?: boolean;
            start_date?: string;
            end_date?: string;
        };
        languages: SeasonTicketSettingsLanguagesModel;
    };
    use_all_quotas: boolean;
    quotas: {
        id: number;
        description: string;
        template_name: string;
        selected: boolean;
    }[];
}
