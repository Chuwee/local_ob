export enum EmailContentTextsType {
    disclaimer = 'DISCLAIMER'
}

export interface SaleRequestEmailContentTexts {
    language: string;
    type: EmailContentTextsType;
    value: string;
}
