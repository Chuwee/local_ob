import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { B2bClientConditionsComponent } from './conditions/b2b-client-conditions.component';
import { b2bClientDetailsResolver } from './details/b2b-client-details-resolver';
import { B2bClientDetailsComponent } from './details/b2b-client-details.component';
import { B2bClientEconomicManagementComponent } from './economic-management/b2b-client-economic-management.component';
import { B2bClientGeneralDataComponent } from './general-data/b2b-client-general-data.component';
import { B2bClientUsersManagementComponent } from './users-management/b2b-client-users-management.component';

const routes: Routes = [{
    path: '',
    component: B2bClientDetailsComponent,
    resolve: {
        b2bClient: b2bClientDetailsResolver
    },
    children: [
        {
            path: '',
            redirectTo: 'general-data',
            pathMatch: 'full'
        },
        {
            path: 'general-data',
            component: B2bClientGeneralDataComponent,
            canDeactivate: [unsavedChangesGuard()],
            data: {
                breadcrumb: 'B2B_CLIENTS.GENERAL_DATA'
            }
        },
        {
            path: 'economic-management',
            component: B2bClientEconomicManagementComponent,
            canDeactivate: [unsavedChangesGuard()],
            data: {
                breadcrumb: 'B2B_CLIENTS.ECONOMIC_MANAGEMENT.TITLE'
            }
        },
        {
            path: 'conditions',
            component: B2bClientConditionsComponent,
            canDeactivate: [unsavedChangesGuard()],
            data: {
                breadcrumb: 'B2B_CLIENTS.CONDITIONS.TITLE'
            }
        },
        {
            path: 'users-management',
            component: B2bClientUsersManagementComponent,
            canDeactivate: [unsavedChangesGuard()],
            data: {
                breadcrumb: 'B2B_CLIENTS.USERS_MANAGEMENT.TITLE'
            }
        }
    ]
}];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class B2bClientRoutingModule { }
