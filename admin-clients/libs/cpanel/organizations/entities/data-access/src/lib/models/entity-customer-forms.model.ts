import { FormsField, FormsRules } from '@admin-clients/cpanel/common/utils';

export interface CustomerForms {
    profile: FormsField[][];
    signIn: FormsField[][];
}

export interface CustomerFormsField extends FormsField {
    visibleSignIn?: boolean;
    visibleProfile?: boolean;
    rules?: FormsRules[];
}

export type VisibleKey = 'visibleProfile' | 'visibleSignIn';
