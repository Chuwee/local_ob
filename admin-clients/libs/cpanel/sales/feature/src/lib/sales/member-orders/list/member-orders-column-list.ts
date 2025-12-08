import { MemberOrderFields } from '@admin-clients/cpanel-sales-data-access';
import { FieldDataGroup } from '@admin-clients/shared/data-access/models';

export const memberOrderColumnList: FieldDataGroup[] = [
    {
        field: 'personalDataGroup',
        fieldKey: 'BUYERS.PERSONAL_DATA',
        isDefault: false,
        fields: [
            {
                field: MemberOrderFields.code,
                fieldKey: 'FORMS.LABELS.CODE',
                isDefault: true,
                disabled: true
            },
            {
                field: MemberOrderFields.clubName,
                fieldKey: 'FORMS.LABELS.CLUB',
                isDefault: true
            },
            {
                field: MemberOrderFields.type,
                fieldKey: 'FORMS.LABELS.TYPE',
                isDefault: true
            },
            {
                field: MemberOrderFields.purchaseDate,
                fieldKey: 'FORMS.LABELS.DATE',
                isDefault: true
            },
            {
                field: MemberOrderFields.client,
                fieldKey: 'FORMS.LABELS.CLIENT',
                isDefault: true
            },
            {
                field: MemberOrderFields.basePrice,
                fieldKey: 'FORMS.LABELS.BASE_PRICE',
                isDefault: true
            },
            {
                field: MemberOrderFields.promotions,
                fieldKey: 'FORMS.LABELS.PROMOTIONS',
                isDefault: true
            },
            {
                field: MemberOrderFields.charges,
                fieldKey: 'FORMS.LABELS.CHARGES',
                isDefault: true
            },
            {
                field: MemberOrderFields.finalPrice,
                fieldKey: 'FORMS.LABELS.FINAL_PRICE',
                isDefault: true,
                disabled: true
            }
        ]
    }
];
