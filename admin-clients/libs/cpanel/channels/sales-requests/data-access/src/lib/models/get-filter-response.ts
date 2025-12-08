import { ScrolledMetadata, FilterOption } from '@admin-clients/shared/data-access/models';

export interface GetFilterResponse {
    data: FilterOption[];
    metadata: ScrolledMetadata;
}
