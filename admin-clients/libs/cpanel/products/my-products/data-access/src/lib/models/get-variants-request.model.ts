import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { ProductVariantStatus } from './product-variant-status.model';

export interface GetVariantsRequest extends PageableFilter {
    q?: string;
    status?: ProductVariantStatus;
    ids?: number[];
    stock?: number;
}
