import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { Routes } from '@angular/router';
import { collectiveDetailsResolver } from './details/collective-details-resolver';
import { CollectiveDetailsComponent } from './details/collective-details.component';
import { CollectiveGeneralDataComponent } from './general-data/collective-general-data.component';

export const routes: Routes = [{
    path: '',
    component: CollectiveDetailsComponent,
    resolve: {
        collective: collectiveDetailsResolver
    },
    children: [
        {
            path: '',
            pathMatch: 'full',
            redirectTo: 'general-data'
        },
        {
            path: 'general-data',
            component: CollectiveGeneralDataComponent,
            canDeactivate: [unsavedChangesGuard()],
            data: {
                breadcrumb: 'COLLECTIVE.GENERAL_DATA'
            }
        },
        {
            path: 'collective-codes',
            loadChildren: () => import('./codes/collective-codes.module').then(m => m.CollectiveCodesModule),
            canDeactivate: [unsavedChangesGuard()],
            data: {
                breadcrumb: 'COLLECTIVE.CODE_LIST'
            }
        }
    ]
}];