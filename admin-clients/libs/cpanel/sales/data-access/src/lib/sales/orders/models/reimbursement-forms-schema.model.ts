export interface ReimbursementFormsSchema {
    [key: string]: {
        name: string;
        label: string;
        type: string;
        required: boolean;
        fieldSize: string;
        pattern?: string;
        value: any;
    }[];
}
