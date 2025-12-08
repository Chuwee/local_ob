import { UserRoles } from './user-roles.model';
// TODO look to unify with cpanel-client

export const roleEvent: UserRoles[] = [
    UserRoles.OPR_MGR, UserRoles.OPR_CALL,
    UserRoles.EVN_MGR
];

export const roleTransaction: UserRoles[] = [
    UserRoles.OPR_CALL, UserRoles.OPR_MGR,
    UserRoles.ENT_MGR, UserRoles.ENT_ANS,
    UserRoles.CNL_MGR, UserRoles.CNL_SAC
];

export const roleTickets: UserRoles[] = [
    UserRoles.OPR_MGR, UserRoles.ENT_MGR,
    UserRoles.ENT_ANS, UserRoles.CNL_MGR,
    UserRoles.CNL_SAC, UserRoles.REC_MGR,
    UserRoles.EVN_MGR
];

export const roleSales: UserRoles[] = [
    // Roles Transactions
    UserRoles.OPR_CALL,
    // Roles Common
    UserRoles.OPR_MGR,
    UserRoles.ENT_MGR, UserRoles.ENT_ANS,
    UserRoles.CNL_MGR, UserRoles.CNL_SAC
    // Roles Tickets & Products
    // Sin permisos peticiones aggregations
    // UserRoles.REC_MGR, UserRoles.EVN_MGR
];

export const roleHome: UserRoles[] = [
    // Roles Transactions
    UserRoles.OPR_CALL,
    // Roles Common
    UserRoles.OPR_MGR,
    UserRoles.ENT_MGR, UserRoles.ENT_ANS,
    UserRoles.CNL_MGR, UserRoles.CNL_SAC,
    // Roles Tickets & Products
    UserRoles.REC_MGR, UserRoles.EVN_MGR,
    // BI
    UserRoles.SYS_MGR
];
