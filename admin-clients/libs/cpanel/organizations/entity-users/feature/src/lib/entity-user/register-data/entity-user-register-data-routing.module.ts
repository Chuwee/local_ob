import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { EntityUserRegisterDataComponent } from './entity-user-register-data.component';
import { RegisterDataPrincipalInfoComponent } from './principal-info/register-data-principal-info.component';
import { RegisterDataSecurityComponent } from './security/register-data-security.component';

const routes: Routes = [{
    path: '',
    component: EntityUserRegisterDataComponent,
    children: [
        {
            path: '',
            pathMatch: 'full',
            redirectTo: 'principal-info'
        },
        {
            path: 'principal-info',
            component: RegisterDataPrincipalInfoComponent,
            canDeactivate: [unsavedChangesGuard()],
            pathMatch: 'full',
            data: {
                breadcrumb: 'USER.GENERAL_DATA'
            }
        },
        {
            path: 'security',
            component: RegisterDataSecurityComponent,
            pathMatch: 'full',
            data: {
                breadcrumb: 'USERS.TITLES.SECURITY'
            }
        }
    ]
}];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class EntityUserRegisterDataRoutingModule { }
