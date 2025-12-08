import { ReleaseDataSessionStatus } from '@admin-clients/shared/common/data-access';

export interface ReleasedListFilter {
    session_id?: string;
    release_status?: ReleaseDataSessionStatus[];
}
