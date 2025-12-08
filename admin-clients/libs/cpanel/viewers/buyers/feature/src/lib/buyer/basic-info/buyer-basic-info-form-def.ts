import { Validators } from '@angular/forms';
import { BuyerGender, BuyerType } from '@admin-clients/cpanel-viewers-buyers-data-access';

// g: group, k: field name/key, val: field validators
export const buyerBasicInfoFormStruct = {
    personalData: {
        g: true,
        k: 'personalData',
        type: { k: 'type', val: null },
        idCardId: { k: 'idCardId', val: null },
        idCardType: { k: 'idCardType', val: null },
        name: { k: 'name', val: null },
        surname: { k: 'surname', val: null },
        gender: { k: 'gender', val: null },
        language: { k: 'language', val: null },
        birthDate: { k: 'birthDate', val: null }
    },
    location: {
        g: true,
        k: 'location',
        country: { k: 'country', val: null },
        countrySubdivision: { k: 'countrySubdivision', val: null },
        city: { k: 'city', val: null },
        zipCode: { k: 'zipCode', val: null },
        address: { k: 'address', val: null }
    },
    contact: {
        g: true,
        k: 'contact',
        email: { k: 'email', val: [Validators.required] },
        fix: { k: 'fix', val: null },
        mobile: { k: 'mobile', val: null }
    },
    notes: { k: 'notes', val: null }
};

export interface BuyerBasicInfoFormValue {
    personalData: {
        type: BuyerType;
        idCardId: string;
        idCardType: string;
        name: string;
        surname: string;
        gender: BuyerGender;
        language: string;
        birthDate: string;
    };
    location: {
        country: string;
        countrySubdivision: string;
        city: string;
        zipCode: string;
        address: string;
    };
    contact: {
        email: string;
        fix: string;
        mobile: string;
    };
    notes: string;
}

