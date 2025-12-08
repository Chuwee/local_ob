export enum AttributeType {
    numeric = 'NUMERIC',
    alphanumeric = 'ALPHANUMERIC',
    defined = 'DEFINED'
}

export enum AttributeSelectionType {
    single = 'SINGLE',
    multiple = 'MULTIPLE'
}

export enum AttributeScope {
    event = 'EVENT',
    session = 'SESSION',
    group = 'GROUP',
    profile = 'PROFILE'
}

export interface Attribute {
    id: number;
    entity_id: number;
    name: string;
    texts: {
        name: { [language: string]: string };
        values: [{
            id: number;
            name: string;
            value: { [language: string]: string };
        }];
    };
    code?: string;
    scope: AttributeScope;
    type: AttributeType;
    selection_type?: AttributeSelectionType;
    min?: number;
    max?: number;
}

export interface AttributeWithValues extends Attribute {
    value?: string;
    selected?: number[];
}
export interface PutAttribute {
    id: number;
    value?: string | number;
    selected?: number[];
}
