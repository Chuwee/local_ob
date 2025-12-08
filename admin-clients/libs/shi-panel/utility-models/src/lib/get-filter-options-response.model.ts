import { ListResponse } from '@OneboxTM/utils-state';

export interface GetFilterOptionsResponse extends ListResponse<FilterOption> {
}

export interface FilterOption {
    id: string;
    name: string;
    code: string;
}
