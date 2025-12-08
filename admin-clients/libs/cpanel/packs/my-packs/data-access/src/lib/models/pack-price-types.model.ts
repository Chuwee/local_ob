export interface PackPriceTypes {
    selection_type: PackPriceTypesScope;
    price_types: {
        id: number;
        name: string;
    }[];
}

export enum PackPriceTypesScope {
    all = 'ALL',
    restricted = 'RESTRICTED'
}

export interface PutPackPriceTypes {
    selection_type: PackPriceTypesScope;
    price_type_ids: number[];
}