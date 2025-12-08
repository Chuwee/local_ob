import { ProductsFields } from '@admin-clients/cpanel-sales-data-access';
import { FieldDataGroup } from '@admin-clients/shared/data-access/models';

export const productsColumnList: FieldDataGroup[] = [
    {
        field: 'products',
        fieldKey: 'PRODUCTS',
        isDefault: false,
        fields: [
            {
                field: ProductsFields.code,
                fieldKey: 'FORMS.LABELS.CODE',
                isDefault: true,
                disabled: true
            },
            {
                field: ProductsFields.product,
                fieldKey: 'ORDER.PRODUCTS_DATA.NAME',
                isDefault: true
            },
            {
                field: ProductsFields.variant,
                fieldKey: 'ORDER.PRODUCTS_DATA.VARIANT',
                isDefault: true
            },
            {
                field: ProductsFields.purchaseDate,
                fieldKey: 'TICKET.PURCHASE_DATE',
                isDefault: true
            },
            {
                field: ProductsFields.barcode,
                fieldKey: 'TICKET.BARCODE',
                isDefault: true
            },
            {
                field: ProductsFields.channel,
                fieldKey: 'TICKET.CHANNEL_NAME',
                isDefault: true
            },
            {
                field: ProductsFields.client,
                fieldKey: 'TICKET.CLIENT',
                isDefault: true
            },
            {
                field: ProductsFields.prints,
                fieldKey: 'TICKET.PRINTS',
                isDefault: true
            },
            {
                field: ProductsFields.validation,
                fieldKey: 'TICKET.VALIDATION',
                isDefault: true
            },
            {
                field: ProductsFields.state,
                fieldKey: 'TICKET.STATE',
                isDefault: true
            },
            {
                field: ProductsFields.price,
                fieldKey: 'TICKET.FINAL_PRICE',
                isDefault: true,
                disabled: true
            }
        ]
    }
];
