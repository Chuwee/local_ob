export interface B2bClientBalance extends B2BCurrencyBalance {
    currencies_balance?: B2BCurrencyBalance[]; // Migrated Clients to MultiCurrency
}

interface B2BCurrencyBalance {
    balance: number;
    credit_limit: number;
    total_available: number;
    debt: number;
    currency_code: string;
}
