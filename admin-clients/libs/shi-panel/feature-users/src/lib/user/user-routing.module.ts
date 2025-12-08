import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { userDetailsResolver } from './details/user-details-resolver';
import { UserDetailsComponent } from './details/user-details.component';
import { UserGeneralDataComponent } from './general-data/user-general-data.component';
import { UserPermissionsComponent } from './permissions/permissions.component';

const routes: Routes = [{
    path: '',
    component: UserDetailsComponent,
    resolve: {
        event: userDetailsResolver
    },
    children: [
        {
            path: '',
            redirectTo: 'general-data',
            pathMatch: 'full'
        },
        {
            path: 'general-data',
            component: UserGeneralDataComponent,
            data: {
                breadcrumb: 'USER.GENERAL_DATA'
            },
            canDeactivate: [unsavedChangesGuard()]
        },
        {
            path: 'permissions',
            component: UserPermissionsComponent,
            data: {
                breadcrumb: 'USER.PERMISSIONS'
            },
            canDeactivate: [unsavedChangesGuard()]
        }
    ]
}];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class UserRoutingModule { }
