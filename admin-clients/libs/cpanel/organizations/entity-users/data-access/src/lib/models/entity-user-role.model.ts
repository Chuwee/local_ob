import { EntityUserPermissions } from './entity-user-permissions.enum';
import { EntityUserRoles } from './entity-user-roles.enum';

export interface EntityUserRole {
    code: EntityUserRoles;
    permissions?: EntityUserPermissions[];
    additional_properties?: {
        producer_ids?: number[];
    };
}
