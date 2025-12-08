/* eslint-disable @typescript-eslint/naming-convention */
// TODO look to unify with cpanel-client
export enum UserRoles {
    /** Super Operator Manager */
    SYS_MGR = 'ROLE_SYS_MGR',
    /** Super Operator Analist */
    SYS_ANS = 'ROLE_SYS_ANS',
    /** Operator Manager */
    OPR_MGR = 'ROLE_OPR_MGR',
    /** Operator Analyst */
    OPR_ANS = 'ROLE_OPR_ANS',
    /** Operator Call */
    OPR_CALL = 'ROLE_OPR_CALL',
    /** Entity Manager */
    ENT_MGR = 'ROLE_ENT_MGR',
    /** Entity Analyst */
    ENT_ANS = 'ROLE_ENT_ANS',
    /** Channel Manager */
    CNL_MGR = 'ROLE_CNL_MGR',
    /** Channel Integration */
    CNL_INT = 'ROLE_CNL_INT',
    /** Client Service */
    CNL_SAC = 'ROLE_CNL_SAC',
    /** Channel Boxoffice */
    CNL_TAQ = 'ROLE_CNL_TAQ',
    /** Event Manager */
    EVN_MGR = 'ROLE_EVN_MGR',
    /** Event Access Control */
    EVN_VAL = 'ROLE_EVN_VAL',
    /** CRM Manager */
    CRM_MGR = 'ROLE_CRM_MGR',
    /** CRM Distribution Lists */
    CRM_DLIST = 'ROLE_CRM_DLIST',
    /** Venue Manager */
    REC_MGR = 'ROLE_REC_MGR',
    /** Venue Editor */
    REC_EDI = 'ROLE_REC_EDI',
    /** Collective Manager */
    COL_MGR = 'ROLE_COL_MGR',
    /** Producer User */
    PRD_ANS = 'ROLE_PRD_ANS'
}
