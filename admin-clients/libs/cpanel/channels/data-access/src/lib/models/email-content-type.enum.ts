export enum BoxOfficeEmailContentType {
    ticketAndReceipt = 'TICKET_AND_RECEIPT',
    onlyTicket = 'ONLY_TICKET',
    onlyReceipt = 'ONLY_RECEIPT',
    none = 'NONE'
}

export enum WebEmailContentType {
    unifiedTicketAndReceipt = 'UNIFIED_TICKET_AND_RECEIPT',
    receiptAndPassbook = 'RECEIPT_AND_PASSBOOK'
}

export enum NewReceiptEmailContentType {
    none = BoxOfficeEmailContentType.none,
    onlyReceipt = BoxOfficeEmailContentType.onlyReceipt,
    unifiedTicketAndReceipt = WebEmailContentType.unifiedTicketAndReceipt
}

// eslint-disable-next-line @typescript-eslint/naming-convention
export const EmailContentType = { ...BoxOfficeEmailContentType, ...WebEmailContentType };
export type EmailContentType = BoxOfficeEmailContentType | WebEmailContentType;
