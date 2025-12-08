export interface PutSeasonTicketRenewalsResponse {
    items: {
        id: string;
        result: boolean;
        reason?: PutSeasonTicketRenewalResponseReason;
    }[];
}

export enum PutSeasonTicketRenewalResponseReason {
    userHasNotRenewals = 'USER_HAS_NOT_RENEWALS',
    renewalProductNotFound = 'RENEWAL_PRODUCT_NOT_FOUND',
    renewalAlreadyRenewed = 'RENEWAL_ALREADY_RENEWED',
    invalidSeat = 'INVALID_SEAT'
}
