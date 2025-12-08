export interface ProductAttribute {
    attribute_id: number;
    name: string;
    position: number;
}

export interface ProductAttributeValue {
    value_id: number;
    attribute_id: number;
    name: string;
    position: number;
}

export interface ProductAttributeChannelContents {
    key: string;
    value: string;
    language: string;
}

export interface ProductAttributeValueListChannelContents {
    valueId: number;
    key: string;
    value: string;
    language: string;
}
