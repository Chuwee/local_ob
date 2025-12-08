export enum ProducerInvoiceProviderStatus {
    notRequested = 'NOT_REQUESTED',
    requested = 'REQUESTED',
    completed = 'COMPLETED'
}

export enum ProducerInvoiceProviderOptions {
    onebox = 'ONEBOX',
    tbai = 'TBAI',
    fever = 'FEVER'
}

export interface GetProducerInvoiceProviderResponse {
    provider: ProducerInvoiceProviderOptions;
    status: ProducerInvoiceProviderStatus;
}

export type PostProducerInvoiceProviderResponse = GetProducerInvoiceProviderResponse;
