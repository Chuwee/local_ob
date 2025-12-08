import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { authCanActivateGuard } from '@admin-clients/shi-panel/data-access-auth';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { IngestorSettingsPageComponent } from './details/ingestor-settings-page.component';
import { IngestorListSettingsComponent } from './ingestor-list-settings/ingestor-list-settings.component';
import { IngestorStatusComponent } from './ingestor-status/ingestor-status.component';

const routes: Routes = [{
    path: '',
    component: IngestorSettingsPageComponent,
    children: [
        {
            path: '',
            redirectTo: 'list-settings',
            pathMatch: 'full'
        },
        {
            path: 'list-settings',
            component: IngestorListSettingsComponent,
            data: {
                breadcrumb: 'INGESTOR_SETTINGS.TITLES.LIST_SETTINGS'
            },
            canActivate: [authCanActivateGuard],
            canDeactivate: [unsavedChangesGuard()]
        },
        {
            path: 'status',
            component: IngestorStatusComponent,
            data: {
                breadcrumb: 'INGESTOR_SETTINGS.TITLES.INGESTOR_STATUS'
            },
            canActivate: [authCanActivateGuard],
            canDeactivate: [unsavedChangesGuard()]
        }
    ]
}];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class IngestorSettingsRoutingModule { }
