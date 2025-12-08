import { ReimbursementFormsSchema } from './reimbursement-forms-schema.model';

export const reimbursementFormsSchemaValues: ReimbursementFormsSchema = {
    redsys_bank_transfer: [],
    eci_bank_transfer: [
        {
            name: 'oC.name',
            label: 'ORDER.REIMBURSEMENT.RETRY_NAME',
            type: 'TEXT',
            required: true,
            fieldSize: '50%',
            value: null
        },
        {
            name: 'oC.lastName',
            label: 'ORDER.REIMBURSEMENT.RETRY_LAST_NAME',
            type: 'TEXT',
            required: true,
            fieldSize: '50%',
            value: null
        },
        {
            name: 'oC.streetType',
            label: 'ORDER.REIMBURSEMENT.RETRY_TYPE_STREET',
            type: 'TEXT',
            required: true,
            fieldSize: '50%',
            value: null
        },
        {
            name: 'oC.streetName',
            label: 'ORDER.REIMBURSEMENT.RETRY_NAME_STREET',
            type: 'TEXT',
            required: true,
            fieldSize: '50%',
            value: null
        },
        {
            name: 'oC.streetNumber',
            label: 'ORDER.REIMBURSEMENT.RETRY_NUM_STREET',
            type: 'TEXT',
            required: true,
            fieldSize: '50%',
            value: null
        },
        {
            name: 'oC.flatStairsDoor',
            label: 'ORDER.REIMBURSEMENT.RETRY_NUM_FLAT',
            type: 'TEXT',
            required: true,
            fieldSize: '50%',
            value: null
        },
        {
            name: 'oC.postalCode',
            label: 'ORDER.REIMBURSEMENT.RETRY_POSTAL_CODE',
            type: 'TEXT',
            required: true,
            fieldSize: '50%',
            value: null
        },
        {
            name: 'oC.personalPhone',
            label: 'ORDER.REIMBURSEMENT.RETRY_PHONE',
            type: 'TEXT',
            required: true,
            fieldSize: '50%',
            value: null
        },
        {
            name: 'oC.bankAccount',
            label: 'ORDER.REIMBURSEMENT.RETRY_BANK_ACCOUNT',
            type: 'TEXT',
            required: true,
            fieldSize: '100%',
            pattern: '^.{24,25}$',
            value: null
        }
    ]
};

