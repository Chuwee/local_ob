import { PageableFilter } from '@admin-clients/shared/data-access/models';

export interface ElementsInfoFilterRequest extends PageableFilter {
    type?: string;
    status?: string;
    hasContents?: boolean;
}
