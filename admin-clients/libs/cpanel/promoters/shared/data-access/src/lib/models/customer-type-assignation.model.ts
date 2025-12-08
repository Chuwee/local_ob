export type CustomerTypeAssignation = {
    mode: CustomerTypeAssignationMode;
    customer_type: {
        id: number;
        name: string;
        code: string;
    };
};

export type PutCustomerTypeAssignation = {
    customer_type_id: number;
    mode: CustomerTypeAssignationMode;
};

export const customerTypeAssignationMode = ['ADD', 'REMOVE'] as const;
export type CustomerTypeAssignationMode = typeof customerTypeAssignationMode[number];