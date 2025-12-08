import { IdName } from '@admin-clients/shared/data-access/models';

export type CustomerTypesHistoricItem = {
    id: string;
    actions: CustomerTypesHistoricItemAction[];
    date: string;
    type: 'MANUAL' | 'AUTOMATIC';
    event?: IdName;
    order_id?: string;
    trigger?: 'REFUND' | 'CANCELATION' | 'PURCHASE' | 'LOGIN' | 'REGISTRATION';
};

export type CustomerTypesHistoricItemAction = {
    customerType: {
        id: number;
        name: string;
        code: string;
    };
    type: 'ADD' | 'REMOVE';
};