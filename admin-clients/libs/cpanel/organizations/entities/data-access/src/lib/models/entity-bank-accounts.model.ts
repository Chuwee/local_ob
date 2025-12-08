export interface EntityBankAccount {
    id?: number;
    entityId?: number;
    iban: string;
    bic: string;
    cc: string;
    name: string;
}

export interface PutEntityBankAccount {
    iban: string;
    bic: string;
    cc: string;
    name: string;
}