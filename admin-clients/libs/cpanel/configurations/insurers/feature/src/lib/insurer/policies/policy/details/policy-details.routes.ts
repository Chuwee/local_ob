import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { Routes } from '@angular/router';
import { PolicyGeneralDataComponent } from '../general-data/policy-general-data.component';
import { PolicyPricesComponent } from '../prices/policy-prices.component';
import { PolicyTermsAndConditionsComponent } from '../terms-conditions/policy-terms-conditions.component';

export const POLICY_DETAILS_ROUTES: Routes = [
    {
        path: '',
        redirectTo: 'general-data',
        pathMatch: 'full'
    },
    {
        path: 'general-data',
        component: PolicyGeneralDataComponent,
        pathMatch: 'full',
        data: {
            breadcrumb: 'INSURERS.TITLES.GENERAL_DATA'
        },
        canDeactivate: [unsavedChangesGuard()]
    },
    {
        path: 'prices',
        component: PolicyPricesComponent,
        pathMatch: 'full',
        data: {
            breadcrumb: 'INSURERS.TITLES.PRICES'
        }
    },
    {
        path: 'terms-conditions',
        component: PolicyTermsAndConditionsComponent,
        pathMatch: 'full',
        data: {
            breadcrumb: 'INSURERS.TITLES.TERMS_AND_CONDITIONS'
        },
        canDeactivate: [unsavedChangesGuard()]
    }

];
