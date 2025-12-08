export interface VoucherCodesToImport {
    balance: number;
    expiration?: string;
    email?: string;
    usage_limit?: number;
}

export interface VoucherCodesWithPinToImport {
    balance: number;
    expiration?: string;
    pin: string;
    email?: string;
    usage_limit?: number;
}
