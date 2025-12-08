/* Texts to show on role description ("modules accesible for this role") */
export enum RoleModules {
    all = 'ALL',
    salesReportsTransactions = 'SALES_REPORTS_TRANSACTIONS',
    mgmtEvents = 'MGMT_EVENTS',
    salesTransactionsNotTotalOpr = 'SALES_TRANSACTIONS_NOT_TOTAL_OPR',
    viewers = 'VIEWERS',
    chnSalesRqst = 'CHN_SALES_RQST',
    salesChn = 'SALES_CHN',
    salesReportsTransactionsTickets = 'SALES_REPORTS_TRANSACTIONS_TICKETS',
    viewersDistList = 'VIEWERS_DIST_LIST',
    viewersClients = 'VIEWERS_CLIENTS',
    collectives = 'COLLECTIVES',
    collectivesDir = 'COLLECTIVES_DIR',
    venues = 'VENUES',
    venuesDir = 'VENUES_DIR',
    myData = 'MY_DATA',
    salesReportsTickets = 'SALES_REPORTS_TICKETS',
    salesTransactions = 'SALES_TRANSACTIONS',
    reports = 'REPORTS',
    mgmtProdDlvryPoints = 'MGMT_PROD_DLVRY_POINTS'
}

/* eslint-disable @typescript-eslint/naming-convention */
export const ModulesAvailable = {
    ROLE_OPR_MGR: [
        RoleModules.all
    ],
    ROLE_OPR_ANS: [
        RoleModules.salesReportsTransactions
    ],
    ROLE_OPR_CALL: [
        RoleModules.mgmtEvents,
        RoleModules.salesTransactionsNotTotalOpr,
        RoleModules.viewers,
        RoleModules.chnSalesRqst
    ],
    ROLE_ENT_MGR: [
        RoleModules.all
    ],
    ROLE_ENT_ANS: [
        RoleModules.salesReportsTransactionsTickets
    ],
    ROLE_CRM_MGR: [
        RoleModules.viewers
    ],
    ROLE_CRM_DLIST: [
        RoleModules.viewersDistList
    ],
    ROLE_COL_MGR: [
        RoleModules.collectives
    ],
    ROLE_REC_MGR: [
        RoleModules.venues
    ],
    ROLE_REC_EDI: [
        RoleModules.myData
    ],
    ROLE_EVN_MGR: [
        RoleModules.mgmtEvents,
        RoleModules.mgmtProdDlvryPoints,
        RoleModules.salesReportsTickets,
        RoleModules.collectivesDir,
        RoleModules.venuesDir
    ],
    ROLE_EVN_VAL: [
        RoleModules.myData
    ],
    ROLE_CNL_MGR: [
        RoleModules.salesReportsTransactions,
        RoleModules.salesChn
    ],
    ROLE_CNL_SAC: [
        RoleModules.salesTransactions,
        RoleModules.viewersClients
    ],
    ROLE_CNL_INT: [
        RoleModules.myData
    ],
    ROLE_CNL_BRIDGE: [
        RoleModules.myData
    ],
    ROLE_PRD_ANS: [
        RoleModules.reports
    ]
};
