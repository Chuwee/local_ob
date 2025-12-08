export interface PriceTypesDialogData {
    name: string;
    code: string;
    id: number;
    templateId: number;
    mode: PriceTypesDataMode;
    isNameReadOnly: boolean;
}

export enum PriceTypesDataMode {
    edition = 'edition',
    creation = 'creation'
}
