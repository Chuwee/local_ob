export type EntityCustomerType = {
    id: number;
    code: string;
    name: string;
    assignation_type: 'MANUAL' | 'AUTOMATIC';
    triggers: {
        trigger: CustomerTypeTrigger;
        handler: string;
        selected: boolean;
    }[];
};
export type EntityCustomerTypeReq = {
    code: string;
    name: string;
    assignation_type: 'MANUAL' | 'AUTOMATIC';
    triggers: CustomerTypeTrigger[];
};

export const customerTypeTrigger = ['REGISTRATION', 'LOGIN', 'PURCHASE'] as const;
export type CustomerTypeTrigger = typeof customerTypeTrigger[number];
