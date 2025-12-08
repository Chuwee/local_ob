import { EventPrice } from '@admin-clients/cpanel/promoters/events/data-access';

export type PriceZoneFilterModel = {
    [key: string]: EventPrice[];
};
