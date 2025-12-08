import { FieldDataGroup } from '@admin-clients/shared/data-access/models';
import { BuyerFields } from './buyer-fields.enum';

export const buyersColumnList: FieldDataGroup[] = [
    {
        field: 'personalDataGroup',
        fieldKey: 'BUYERS.PERSONAL_DATA',
        isDefault: false,
        fields: [
            {
                field: BuyerFields.email,
                fieldKey: 'BUYERS.EMAIL',
                isDefault: true
            },
            {
                field: BuyerFields.name,
                fieldKey: 'BUYERS.NAME',
                isDefault: true
            },
            {
                field: BuyerFields.surname,
                fieldKey: 'BUYERS.SURNAME',
                isDefault: true
            },
            {
                field: BuyerFields.gender,
                fieldKey: 'BUYERS.GENDER',
                isDefault: false
            },
            {
                field: BuyerFields.birthDate,
                fieldKey: 'BUYERS.BIRTHDATE',
                isDefault: false
            },
            {
                field: BuyerFields.fixPhone,
                fieldKey: 'FORMS.LABELS.PHONE',
                isDefault: false
            },
            {
                field: BuyerFields.mobilePhone,
                fieldKey: 'FORMS.LABELS.MOBILE_PHONE',
                isDefault: false
            },
            {
                field: BuyerFields.docType,
                fieldKey: 'BUYERS.CARD_TYPE',
                isDefault: false
            },
            {
                field: BuyerFields.doc,
                fieldKey: 'BUYERS.ID_CARD',
                isDefault: false
            },
            {
                field: BuyerFields.creationDate,
                fieldKey: 'BUYERS.CREATION_DATE',
                isDefault: false
            },
            {
                field: BuyerFields.modificationDate,
                fieldKey: 'BUYERS.MODIFICATION_DATE',
                isDefault: false
            }
        ]
    },
    {
        field: 'adressGroup',
        fieldKey: 'FORMS.LABELS.ADDRESS',
        isDefault: false,
        fields: [
            {
                field: BuyerFields.country,
                fieldKey: 'FORMS.LABELS.COUNTRY',
                isDefault: false
            },
            {
                field: BuyerFields.countrySubdivision,
                fieldKey: 'FORMS.LABELS.COUNTRY_SUBDIVISION',
                isDefault: false
            },
            {
                field: BuyerFields.city,
                fieldKey: 'FORMS.LABELS.CITY',
                isDefault: false
            },
            {
                field: BuyerFields.zipCode,
                fieldKey: 'FORMS.LABELS.ZIPCODE',
                isDefault: false
            },
            {
                field: BuyerFields.address,
                fieldKey: 'FORMS.LABELS.ADDRESS',
                isDefault: false
            }
        ]
    },
    {
        field: 'communicationDataGroup',
        fieldKey: 'BUYERS.COMMUNICATION_DATA',
        isDefault: false,
        fields: [
            {
                field: BuyerFields.subscriptionList,
                fieldKey: 'BUYERS.SUBSCRIPTION_LISTS',
                isDefault: false
            },
            {
                field: BuyerFields.collectives,
                fieldKey: 'BUYERS.COLLECTIVES',
                isDefault: false
            },
            {
                field: BuyerFields.channels,
                fieldKey: 'BUYERS.CHANNELS',
                isDefault: false
            },
            {
                field: BuyerFields.allowCommercialMailing,
                fieldKey: 'BUYERS.ALLOW_COMMERCIAL_MAILING',
                isDefault: false
            },
            {
                field: BuyerFields.type,
                fieldKey: 'BUYERS.TYPE',
                isDefault: false
            }
        ]
    },
    {
        field: 'businessDataGroup',
        fieldKey: 'BUYERS.BUSINESS_DATA',
        isDefault: false,
        fields: [
            {
                field: BuyerFields.purchaseCount,
                fieldKey: 'BUYERS.PURCHASE_COUNT',
                isDefault: true
            },
            {
                field: BuyerFields.ticketCount,
                fieldKey: 'ORDER.TICKETS',
                isDefault: true
            },
            {
                field: BuyerFields.productCount,
                fieldKey: 'ORDER.PRODUCTS',
                isDefault: true
            },
            {
                field: BuyerFields.purchaseAmount,
                fieldKey: 'BUYERS.PURCHASE_AMOUNT',
                isDefault: false
            },
            {
                field: BuyerFields.avgAmount,
                fieldKey: 'BUYERS.AVG_AMOUNT',
                isDefault: false
            },
            {
                field: BuyerFields.refundedItemsCount,
                fieldKey: 'BUYERS.REFUND_COUNT',
                isDefault: false
            },
            {
                field: BuyerFields.refundAmount,
                fieldKey: 'BUYERS.REFUND_AMOUNT',
                isDefault: false
            },
            {
                field: BuyerFields.firstPurchaseDate,
                fieldKey: 'BUYERS.FIRST_PURCHASE',
                isDefault: false
            },
            {
                field: BuyerFields.lastPurchaseDate,
                fieldKey: 'BUYERS.LAST_PURCHASE',
                isDefault: false
            },
            {
                field: BuyerFields.avgDaysBeforeDateBuyed,
                fieldKey: 'BUYERS.AVG_DAYS_BEFORE_BUYED',
                isDefault: false
            }
        ]
    }
];
