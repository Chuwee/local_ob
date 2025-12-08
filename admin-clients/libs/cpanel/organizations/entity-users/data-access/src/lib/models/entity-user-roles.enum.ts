/* Order of the values in this enum is used to show list roles in required order */
/* eslint-disable @typescript-eslint/naming-convention */
export enum EntityUserRoles {
    /** OPERADORA **/
    /* Administrador de la operadora */
    OPR_MGR = 'ROLE_OPR_MGR',
    /* Analista de la operadora */
    OPR_ANS = 'ROLE_OPR_ANS',
    /* Soporte promotor/canal */
    OPR_CALL = 'ROLE_OPR_CALL',

    /** COMUNES + ASEGURADORA **/
    /* Administrador de la entidad */
    ENT_MGR = 'ROLE_ENT_MGR',
    /* Analista de la entidad */
    ENT_ANS = 'ROLE_ENT_ANS',
    /* Administrador de espectadores */
    CRM_MGR = 'ROLE_CRM_MGR',
    /* Gestor de listas de distribución */
    CRM_DLIST = 'ROLE_CRM_DLIST',
    /* Gestor de colectivos */
    COL_MGR = 'ROLE_COL_MGR',

    /** RECINTO **/
    /* Gestor de recintos */
    REC_MGR = 'ROLE_REC_MGR',
    /* Editor de recintos */
    REC_EDI = 'ROLE_REC_EDI',

    /** EVENTO **/
    /* Gestor de eventos */
    EVN_MGR = 'ROLE_EVN_MGR',
    /* Control de accesos */
    EVN_VAL = 'ROLE_EVN_VAL',

    /** CANAL **/
    /* Gestor de canales */
    CNL_MGR = 'ROLE_CNL_MGR',
    /* Usuario de taquilla */
    CNL_TAQ = 'ROLE_CNL_TAQ',
    /* Atención al cliente */
    CNL_SAC = 'ROLE_CNL_SAC',
    /* Integración canales */
    CNL_INT = 'ROLE_CNL_INT',
    /* Integración "Channel bridge" */
    CNL_BRIDGE = 'ROLE_CNL_BRIDGE',

    /** BUSINESS INTELLIGENCE **/
    /* Producer User */
    PRD_ANS = 'ROLE_PRD_ANS',
    /* BI User */
    BI_USR = 'ROLE_BI_USR',

    /** FEVER */
    /* Reporting */
    FV_REPORTING = 'ROLE_FV_REPORTING'

}
