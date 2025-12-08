import {
    CustomerFriendRelation, CustomerGender, CustomerListItem, CustomerTitle, CustomerType
} from '@admin-clients/cpanel-viewers-customers-data-access';
import { FormControl, FormGroup } from '@angular/forms';
import { Moment } from 'moment-timezone';

export enum AddCustomerType {
    existing = 'existing',
    new = 'new'
}
export interface AddWizardFriendFamilyForm {
    relation: FormGroup<AddFriendAndFamilyRelationForm>;
    addFriendFamilyForm: FormGroup<AddFriendFamilyForm>;
    searchCtrl: FormControl<string>;
}

export interface NewFriendFamilyForm {
    location: FormGroup<{
        address: FormControl<string>;
        city: FormControl<string>;
        country: FormControl<string>;
        postalCode: FormControl<string>;
        provinceCode: FormControl<string>;
    }>;
    birthday: FormControl<Moment>;
    email: FormControl<string>;
    gender: FormControl<CustomerGender>;
    identification: FormControl<string>;
    memberId: FormControl<string>;
    name: FormControl<string>;
    title: FormControl<CustomerTitle>;
    phone: FormControl<string>;
    phone_2: FormControl<string>;
    address_2: FormControl<string>;
    surname: FormControl<string>;
    type: FormControl<CustomerType>;
    language: FormControl<string>;
}

export interface VmCustomersToBeFriend extends CustomerListItem {
    selected?: boolean;
    disabled?: boolean;
}

export interface AddFriendFamilyForm {
    newFriendFamilyForm: FormGroup<NewFriendFamilyForm>;
    existingCustomerCtrl: FormControl<VmCustomersToBeFriend[]>;
}

export interface AddFriendAndFamilyRelationForm {
    addType: FormControl<AddCustomerType>;
    type: FormControl<CustomerFriendRelation>;
}

export interface VmCustomerToBeFriendConfirmation {
    name: string;
    surname: string;
    email: string;
    member_id: string;
    identification: string;
    relation: CustomerFriendRelation;
}

