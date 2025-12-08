import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { Routes } from '@angular/router';
import { InsurerDetailsComponent } from './details/insurer-details.component';
import { insurerDetailsResolver } from './details/insurer-details.resolver';
import { InsurerGeneralDataComponent } from './general-data/insurer-general-data.component';

export const INSURER_ROUTES: Routes = [
    {
        path: '',
        component: InsurerDetailsComponent,
        resolve: {
            insurer: insurerDetailsResolver
        },
        children: [
            {
                path: '',
                redirectTo: 'general-data',
                pathMatch: 'full'
            },
            {
                path: 'general-data',
                component: InsurerGeneralDataComponent,
                //canDeactivate: [unsavedChangesGuard()], TO DO: When enable edition
                data: {
                    breadcrumb: 'INSURERS.TITLES.GENERAL_DATA'
                }
            },
            {
                path: 'policies',
                loadChildren: () => import('./policies/insurer-policies.routes').then(m => m.INSURER_POLICIES_ROUTES),
                data: {
                    breadcrumb: 'INSURERS.TITLES.POLICIES'
                },
                canDeactivate: [unsavedChangesGuard()]
            }

        ]
    }
];
