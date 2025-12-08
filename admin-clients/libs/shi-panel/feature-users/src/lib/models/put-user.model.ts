import { UserStatus, UserRoles } from '@admin-clients/shi-panel/utility-models';

export interface PutUser {
    name?: string;
    surname?: string;
    username?: string;
    status?: UserStatus;
    role?: UserRoles;
}
