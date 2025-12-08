import { IdName } from '@admin-clients/shared/data-access/models';

export interface PostBookingQuestion extends IdName {
    label: {
        default_value: string;
        translations: Record<string, string>;
    };
}
