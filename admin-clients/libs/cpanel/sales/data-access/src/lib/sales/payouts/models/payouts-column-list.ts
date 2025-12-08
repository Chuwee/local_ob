import { FieldDataGroup } from '@admin-clients/shared/data-access/models';
import { PayoutsFields } from './payouts-fields.enum';

export const payoutsColumnList: FieldDataGroup[] = [
    {
        field: 'payouts',
        fieldKey: 'PAYOUTS',
        isDefault: false,
        fields: [
            {
                field: PayoutsFields.code,
                fieldKey: 'FORMS.LABELS.CODE',
                isDefault: true,
                disabled: true
            },
            {
                field: PayoutsFields.channel,
                fieldKey: 'FORMS.LABELS.CHANNEL',
                isDefault: true
            },
            {
                field: PayoutsFields.event,
                fieldKey: 'FORMS.LABELS.EVENT',
                isDefault: true
            },
            {
                field: PayoutsFields.session,
                fieldKey: 'FORMS.LABELS.SESSION',
                isDefault: true
            },
            {
                field: PayoutsFields.seat,
                fieldKey: 'FORMS.LABELS.SEAT',
                isDefault: true
            },
            {
                field: PayoutsFields.client,
                fieldKey: 'FORMS.LABELS.CLIENT',
                isDefault: true
            },
            {
                field: PayoutsFields.clientId,
                fieldKey: 'FORMS.LABELS.CLIENT_ID',
                isDefault: false
            },
            {
                field: PayoutsFields.payoutType,
                fieldKey: 'FORMS.LABELS.PAYOUT_TYPE',
                isDefault: true
            },
            {
                field: PayoutsFields.iban,
                fieldKey: 'FORMS.LABELS.IBAN',
                isDefault: false
            },
            {
                field: PayoutsFields.bacs,
                fieldKey: 'FORMS.LABELS.BACS',
                isDefault: false
            },
            {
                field: PayoutsFields.price,
                fieldKey: 'FORMS.LABELS.PRICE',
                isDefault: true
            },
            {
                field: PayoutsFields.purchaseDate,
                fieldKey: 'FORMS.LABELS.DATE',
                isDefault: true
            },
            {
                field: PayoutsFields.status,
                fieldKey: 'FORMS.LABELS.STATUS',
                isDefault: true,
                disabled: true
            }
        ]
    }
];
