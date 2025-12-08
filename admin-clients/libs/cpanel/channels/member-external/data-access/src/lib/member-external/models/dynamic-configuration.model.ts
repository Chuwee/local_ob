export interface DynamicField {
    id: string;
    type: FieldType;
    container: FieldContainerType;
    target?: string;
    source?: string;
    value?: any;
}

export interface DynamicConfiguration {
    operation_name: OperationName;
    implementation: string;
    type: Type;
    fields: DynamicField[];
    order_type: OrderType;
}

export type DynamicConfigurations = DynamicConfiguration[];

export type FieldContainerType = 'LIST' | 'SINGLE' | 'MAP';
export type OrderType = 'BUY_SEAT' | 'CHANGE_SEAT' | 'RENEWAL';
export type FieldType = 'STRING' | 'INTEGER';
export type Type = 'VALIDATION' | 'INFERER' | 'PRICE_CALCULATOR';
export type OperationName = 'CHANGE_SEAT_VALIDATOR' | 'BUY_SEAT_VALIDATOR' | 'RENEWAL_VALIDATOR' |
    'NEW_SEAT_PRICE' | 'PREVIOUS_SEAT_PRICE' | 'SUBSCRIPTION_MODE_INFERER' | 'ROLE_INFERER';
