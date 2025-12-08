import { UserRoles } from '@admin-clients/shi-panel/utility-models';

export interface PostUser {
    username: string;
    name: string;
    surname: string;
    role: UserRoles;
}
