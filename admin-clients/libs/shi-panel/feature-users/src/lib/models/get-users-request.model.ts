import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { UserStatus } from '@admin-clients/shi-panel/utility-models';

export interface GetUsersRequest extends PageableFilter {
    name?: string;
    surname?: string;
    email?: string;
    role?: string;
    status?: UserStatus;
}
