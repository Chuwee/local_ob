export interface OperatorTax {
    id: number;
    name: string;
    description?: string;
    value: number;
    default: boolean;
}

export type PostOperatorTaxRequest = Omit<OperatorTax, 'id' | 'default'>;

export type PutOperatorTaxRequest = Omit<OperatorTax, 'value'>;
