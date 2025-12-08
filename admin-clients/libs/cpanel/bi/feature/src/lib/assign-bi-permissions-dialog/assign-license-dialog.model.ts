import { EntityUserPermissions } from '@admin-clients/cpanel/organizations/entity-users/data-access';

export interface AssignLicenseDialogData {
    permission: EntityUserPermissions;
    title: string;
    onlyInternalUsers?: boolean;
    alreadyAssigned?: number[];
}
