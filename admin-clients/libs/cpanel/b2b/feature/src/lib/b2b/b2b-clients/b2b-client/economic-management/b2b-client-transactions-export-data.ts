import { FieldDataGroup } from '@admin-clients/shared/data-access/models';

export const b2bClientTransactionsExportData: FieldDataGroup[] = [
    {
        fieldKey: 'B2B_CLIENTS.ECONOMIC_MANAGEMENT.EXPORT_TITLE',
        field: 'b2b_client_transactions_data',
        isDefault: true,
        fields: [
            {
                field: 'date',
                fieldKey: 'B2B_CLIENTS.ECONOMIC_MANAGEMENT.HEAD.TRANSACTION_DATE',
                isDefault: true
            },
            {
                field: 'time',
                fieldKey: 'B2B_CLIENTS.ECONOMIC_MANAGEMENT.HEAD.TRANSACTION_TIME',
                isDefault: true
            },
            {
                field: 'transaction_type',
                fieldKey: 'B2B_CLIENTS.ECONOMIC_MANAGEMENT.HEAD.TRANSACTION_TYPE',
                isDefault: true
            },
            {
                field: 'deposit_type',
                fieldKey: 'B2B_CLIENTS.ECONOMIC_MANAGEMENT.HEAD.DEPOSIT_TYPE',
                isDefault: true
            },
            {
                field: 'channel',
                fieldKey: 'B2B_CLIENTS.ECONOMIC_MANAGEMENT.HEAD.TRANSACTION_CHANNEL',
                isDefault: true
            },
            {
                field: 'user',
                fieldKey: 'B2B_CLIENTS.ECONOMIC_MANAGEMENT.HEAD.TRANSACTION_USER',
                isDefault: true
            },
            {
                field: 'order_code',
                fieldKey: 'B2B_CLIENTS.ECONOMIC_MANAGEMENT.HEAD.TRANSACTION_ORDER_CODE',
                isDefault: true
            },
            {
                field: 'notes',
                fieldKey: 'B2B_CLIENTS.ECONOMIC_MANAGEMENT.HEAD.TRANSACTION_NOTES',
                isDefault: true
            },
            {
                field: 'previous_balance',
                fieldKey: 'B2B_CLIENTS.ECONOMIC_MANAGEMENT.HEAD.PREVIOUS_BALANCE',
                isDefault: true
            },
            {
                field: 'amount',
                fieldKey: 'B2B_CLIENTS.ECONOMIC_MANAGEMENT.HEAD.TRANSACTION_AMOUNT',
                isDefault: true
            },
            {
                field: 'balance',
                fieldKey: 'B2B_CLIENTS.ECONOMIC_MANAGEMENT.HEAD.BALANCE',
                isDefault: true
            },
            {
                field: 'credit',
                fieldKey: 'B2B_CLIENTS.ECONOMIC_MANAGEMENT.HEAD.CREDIT_LIMIT',
                isDefault: true
            },
            {
                field: 'debt',
                fieldKey: 'B2B_CLIENTS.ECONOMIC_MANAGEMENT.HEAD.DEBT',
                isDefault: true
            }
        ]
    }
];
