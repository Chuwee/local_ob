export interface GetTicketPdfResponse {
    tickets: TicketPdf[];
}

export interface TicketPdf {
    download_link: string;
}
