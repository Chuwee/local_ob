import { ListResponse } from '@OneboxTM/utils-state';
import { IdName } from '@admin-clients/shared/data-access/models';

export type PostPriceTypeRestriction = {
    required_price_type_ids: number[];
    required_tickets_number: number;
    locked_tickets_number: number;
};

export type GetPriceTypeRestricion = {
    required_price_types: IdName[];
    required_tickets_number: number;
    locked_tickets_number: number;
};

export type RestrictedPriceZones = ListResponse<IdName>;
