import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { EntityUserPermissions } from './entity-user-permissions.enum';
import { EntityUserRoles } from './entity-user-roles.enum';
import { EntityUserStatus } from './entity-user-status.model';

export class GetEntityUsersRequest implements PageableFilter {
    limit: number;
    offset: number;
    sort?: string; // "(date|num_items):(asc|desc)"
    q?: string;  // Wildcard filter
    code?: string;
    entityId?: number;
    operatorId?: number;
    status?: EntityUserStatus[];
    roles?: EntityUserRoles[];
    permissions?: EntityUserPermissions[];

    constructor() {
        this.limit = 20;
        this.offset = 0;
        this.q = '';
        this.code = null;
        this.entityId = null;
        this.operatorId = null;
        this.status = [];
    }
}
