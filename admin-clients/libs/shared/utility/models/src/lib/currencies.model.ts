export interface Currency {
    id?: number;
    code: string;
    description?: string;
}

export interface OperatorCurrency {
    default_currency: string;
    selected: Currency[];
}
