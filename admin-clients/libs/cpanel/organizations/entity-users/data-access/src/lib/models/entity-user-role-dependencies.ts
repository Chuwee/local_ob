import { EntityUserPermissions } from './entity-user-permissions.enum';
import { EntityUserRoles } from './entity-user-roles.enum';

/* When ROLE_ENT_MGR is checked or unchecked, all the following roles also change */
export const entityMgrRoleDependencies: EntityUserRoles[] = [
    EntityUserRoles.ENT_ANS,
    EntityUserRoles.CRM_MGR,
    EntityUserRoles.CRM_DLIST,
    EntityUserRoles.COL_MGR,
    EntityUserRoles.REC_MGR,
    EntityUserRoles.REC_EDI,
    EntityUserRoles.EVN_MGR,
    EntityUserRoles.EVN_VAL,
    EntityUserRoles.CNL_MGR,
    EntityUserRoles.CNL_TAQ,
    EntityUserRoles.CNL_SAC,
    EntityUserRoles.CNL_INT,
    EntityUserRoles.CNL_BRIDGE
];

/* When ROLE_OPR_MGR is checked or unchecked, all the following permissions also change */
export const operatorMgrPermissionsDependencies: EntityUserPermissions[] = [
    EntityUserPermissions.automaticSales
];

/* When ROLE_ENT_MGR is checked or unchecked, all the following permissions also change */
export const entityMgrPermissionsDependencies: EntityUserPermissions[] = [
    EntityUserPermissions.invite,
    EntityUserPermissions.refund,
    EntityUserPermissions.issue,
    EntityUserPermissions.secondMarket
];

/* When ROLE_CNL_TAQ is checked or unchecked, its permissions also change */
export const taqUserPermissionsDependencies: EntityUserPermissions[] = [
    EntityUserPermissions.invite,
    EntityUserPermissions.refund,
    EntityUserPermissions.issue
];

export const biUserPermissionsDependencies: EntityUserPermissions[] = [
    EntityUserPermissions.basic,
    EntityUserPermissions.advanced
];
