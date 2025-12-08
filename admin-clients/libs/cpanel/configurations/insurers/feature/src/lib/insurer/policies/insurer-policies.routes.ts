
import { Routes } from '@angular/router';
import { PoliciesContainerComponent } from './container/policies-container.component';
import { PolicyDetailsComponent } from './policy/details/policy-details.component';
import { insurerPolicyDetailsResolver } from './policy/details/policy-details.resolver';

export const INSURER_POLICIES_ROUTES: Routes = [
    {
        path: '',
        component: PoliciesContainerComponent,
        children: [
            {
                path: '',
                component: null,
                pathMatch: 'full',
                children: []
            },
            {
                path: ':policyId',
                component: PolicyDetailsComponent,
                resolve: {
                    configuration: insurerPolicyDetailsResolver
                },
                data: {
                    breadcrumb: 'policyName'
                },
                loadChildren: () => import('./policy/details/policy-details.routes').then(m => m.POLICY_DETAILS_ROUTES)
            }
        ]
    }
];
