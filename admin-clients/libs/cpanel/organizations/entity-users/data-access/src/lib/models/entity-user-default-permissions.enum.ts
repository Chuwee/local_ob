/* Texts to show on role description ("available permissions for this role") */
export enum DefaultPermissionsDescriptions {
    accessEditUserChangePasswd = 'ACCESS_EDIT_USER_CHANGE_PASSWD',
    editOwnOperator = 'EDIT_OWN_OPERATOR',
    editOwnEntity = 'EDIT_OWN_ENTITY',
    mgrOprUsersEntUsers = 'MGR_OPR_USERS_ENT_USERS',
    mgrEntUsers = 'MGR_ENT_USERS',
    readActionsHistory = 'READ_ACTIONS_HISTORY',
    mgrOprEntNewEnt = 'MGR_OPR_ENT_NEW_ENT',
    mgrEvnConfTicketsGroups = 'MGR_EVN_CONF_TICKETS_GROUPS',
    mgrEvnChnVenues = 'MGR_EVN_CHN_VENUES',
    mgrEvnNotConfTicketsGroups = 'MGR_EVN_NOT_CONF_TICKETS_GROUPS',
    generateAllReports = 'GENERATE_ALL_REPORTS',
    readExportCancelTransactions = 'READ_EXPORT_CANCEL_TRANSACTIONS',
    readExportTransactions = 'READ_EXPORT_TRANSACTIONS',
    readExportNotCancelTransactions = 'READ_EXPORT_NOT_CANCEL_TRANSACTIONS',
    readExportNotTotalTransactions = 'READ_EXPORT_NOT_TOTAL_TRANSACTIONS',
    readExportNotRefundTransacTickets = 'READ_EXPORT_NOT_REFUND_TRANSAC_TICKETS',
    readNotTotalRefundExportTransactions = 'READ_NOT_TOTAL_REFUND_EXPORT_TRANSACTIONS',
    doRefunds = 'DO_REFUNDS',
    readReprintTickets = 'READ_REPRINT_TICKETS',
    mgrViewersSubsDistLists = 'MGR_VIEWERS_SUBS_DIST_LISTS',
    mgrCollectives = 'MGR_COLLECTIVES',
    mgrVenuesConfs = 'MGR_VENUES_CONFS',
    mgrVenuesCapacity = 'MGR_VENUES_CAPACITY',
    mgrChnPointOfSale = 'MGR_CHN_POINT_OF_SALE',
    mgrSalesRequestChn = 'MGR_SALES_REQUEST_CHN',
    generateWhitelist = 'GENERATE_WHITELIST',
    generateEntReports = 'GENERATE_ENT_REPORTS',
    generateVenueReports = 'GENERATE_VENUE_REPORTS',
    generatePromoterReports = 'GENERATE_PROMOTER_REPORTS',
    generateProducerReports = 'GENERATE_PRODUCER_REPORTS',
    accessProducerReports = 'ACCESS_PRODUCER_REPORTS',
    generateChnReports = 'GENERATE_CHN_REPORTS',
    registerModExportDistLists = 'REGISTER_MOD_EXPORT_DIST_LISTS',
    notVenueEditor = 'NOT_VENUE_EDITOR',
    enterVenueEditor = 'ENTER_VENUE_EDITOR',
    readDirCol = 'READ_DIR_COL',
    readDirVenues = 'READ_DIR_VENUES',
    ticketValidation = 'TICKET_VALIDATION',
    useBoxOffice = 'USE_BOX_OFFICE',
    useWebBoxOffice = 'USE_WEB_BOX_OFFICE',
    readViewersEditClients = 'READ_VIEWERS_EDIT_CLIENTS',
    connectWebExternal = 'CONNECT_WEB_EXTERNAL',
    connectChnBridge = 'CONNECT_CHN_BRIDGE',
    mgrProdDlvryPointsSales = 'MGR_PROD_DLVRY_POINTS_SALES'
}

/* eslint-disable @typescript-eslint/naming-convention */
export const RolesDefaultPermissions = {
    ROLE_OPR_MGR: [
        DefaultPermissionsDescriptions.accessEditUserChangePasswd,
        DefaultPermissionsDescriptions.editOwnOperator,
        DefaultPermissionsDescriptions.mgrOprUsersEntUsers,
        DefaultPermissionsDescriptions.readActionsHistory,
        DefaultPermissionsDescriptions.mgrOprEntNewEnt,
        DefaultPermissionsDescriptions.mgrEvnConfTicketsGroups,
        DefaultPermissionsDescriptions.generateAllReports,
        DefaultPermissionsDescriptions.readExportCancelTransactions,
        DefaultPermissionsDescriptions.doRefunds,
        DefaultPermissionsDescriptions.readReprintTickets,
        DefaultPermissionsDescriptions.mgrViewersSubsDistLists,
        DefaultPermissionsDescriptions.mgrCollectives,
        DefaultPermissionsDescriptions.mgrVenuesConfs,
        DefaultPermissionsDescriptions.mgrChnPointOfSale
    ],
    ROLE_OPR_ANS: [
        DefaultPermissionsDescriptions.accessEditUserChangePasswd,
        DefaultPermissionsDescriptions.generateAllReports,
        DefaultPermissionsDescriptions.readExportNotCancelTransactions
    ],
    ROLE_OPR_CALL: [
        DefaultPermissionsDescriptions.accessEditUserChangePasswd,
        DefaultPermissionsDescriptions.mgrEvnNotConfTicketsGroups,
        DefaultPermissionsDescriptions.readExportNotTotalTransactions,
        DefaultPermissionsDescriptions.doRefunds,
        DefaultPermissionsDescriptions.readReprintTickets,
        DefaultPermissionsDescriptions.mgrViewersSubsDistLists,
        DefaultPermissionsDescriptions.mgrSalesRequestChn
    ],
    ROLE_ENT_MGR: [
        DefaultPermissionsDescriptions.accessEditUserChangePasswd,
        DefaultPermissionsDescriptions.editOwnEntity,
        DefaultPermissionsDescriptions.mgrEntUsers,
        DefaultPermissionsDescriptions.mgrViewersSubsDistLists,
        DefaultPermissionsDescriptions.mgrEvnChnVenues,
        DefaultPermissionsDescriptions.generateWhitelist
    ],
    ROLE_ENT_ANS: [
        DefaultPermissionsDescriptions.accessEditUserChangePasswd,
        DefaultPermissionsDescriptions.generateEntReports,
        DefaultPermissionsDescriptions.readExportNotRefundTransacTickets
    ],
    ROLE_CRM_MGR: [
        DefaultPermissionsDescriptions.accessEditUserChangePasswd,
        DefaultPermissionsDescriptions.mgrViewersSubsDistLists
    ],
    ROLE_CRM_DLIST: [
        DefaultPermissionsDescriptions.accessEditUserChangePasswd,
        DefaultPermissionsDescriptions.registerModExportDistLists
    ],
    ROLE_COL_MGR: [
        DefaultPermissionsDescriptions.accessEditUserChangePasswd,
        DefaultPermissionsDescriptions.mgrCollectives
    ],
    ROLE_REC_MGR: [
        DefaultPermissionsDescriptions.accessEditUserChangePasswd,
        DefaultPermissionsDescriptions.mgrVenuesCapacity,
        DefaultPermissionsDescriptions.notVenueEditor,
        DefaultPermissionsDescriptions.generateVenueReports
    ],
    ROLE_REC_EDI: [
        DefaultPermissionsDescriptions.accessEditUserChangePasswd,
        DefaultPermissionsDescriptions.enterVenueEditor
    ],
    ROLE_EVN_MGR: [
        DefaultPermissionsDescriptions.accessEditUserChangePasswd,
        DefaultPermissionsDescriptions.mgrEvnConfTicketsGroups,
        DefaultPermissionsDescriptions.mgrProdDlvryPointsSales,
        DefaultPermissionsDescriptions.readReprintTickets,
        DefaultPermissionsDescriptions.generatePromoterReports,
        DefaultPermissionsDescriptions.readDirCol,
        DefaultPermissionsDescriptions.readDirVenues
    ],
    ROLE_EVN_VAL: [
        DefaultPermissionsDescriptions.accessEditUserChangePasswd,
        DefaultPermissionsDescriptions.ticketValidation
    ],
    ROLE_CNL_MGR: [
        DefaultPermissionsDescriptions.accessEditUserChangePasswd,
        DefaultPermissionsDescriptions.mgrChnPointOfSale,
        DefaultPermissionsDescriptions.generateChnReports,
        DefaultPermissionsDescriptions.readExportTransactions,
        DefaultPermissionsDescriptions.doRefunds
    ],
    ROLE_CNL_TAQ: [
        DefaultPermissionsDescriptions.useBoxOffice,
        DefaultPermissionsDescriptions.useWebBoxOffice
    ],
    ROLE_CNL_SAC: [
        DefaultPermissionsDescriptions.accessEditUserChangePasswd,
        DefaultPermissionsDescriptions.readNotTotalRefundExportTransactions,
        DefaultPermissionsDescriptions.readViewersEditClients
    ],
    ROLE_CNL_INT: [
        DefaultPermissionsDescriptions.connectWebExternal
    ],
    ROLE_CNL_BRIDGE: [
        DefaultPermissionsDescriptions.connectChnBridge
    ],
    ROLE_PRD_ANS: [
        DefaultPermissionsDescriptions.accessEditUserChangePasswd,
        DefaultPermissionsDescriptions.accessProducerReports,
        DefaultPermissionsDescriptions.generateProducerReports
    ]
};
