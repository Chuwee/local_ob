export type CsvSeasonTicketRenewalGeneration = {
    renewal_id?: string;
    reference?: string;
};

export interface PostSeasonTicketRenewalsGeneration {
    channelId?: number;
    data?: CsvSeasonTicketRenewalGeneration[];
}
