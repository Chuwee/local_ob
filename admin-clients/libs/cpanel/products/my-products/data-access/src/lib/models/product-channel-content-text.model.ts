export type ProductChannelTextContent = {
    type: ProductChannelContentTextType;
    language: string;
    language_id?: string;
    value: string;
};

export enum ProductChannelContentTextType {
    productName = 'PRODUCT_NAME',
    description = 'DESCRIPTION',
    notes = 'NOTES'
}
