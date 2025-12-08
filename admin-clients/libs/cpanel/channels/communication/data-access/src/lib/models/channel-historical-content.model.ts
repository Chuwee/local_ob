import { IdName } from '@admin-clients/shared/data-access/models';

export interface ChannelHistoricalContent {
    creation_date: string;
    subject?: string;
    value: string;
    language?: string;
    author?: IdName;
}
