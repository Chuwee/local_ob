import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { authCanActivateGuard } from '@admin-clients/shi-panel/data-access-auth';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SalesSettingsPageComponent } from './details/sales-settings-page.component';
import { ErrorSettingsComponent } from './error-settings/error-settings.component';
import { ExportSettingsComponent } from './export-settings/export-settings.component';

const routes: Routes = [{
    path: '',
    component: SalesSettingsPageComponent,
    children: [
        {
            path: '',
            redirectTo: 'export-settings',
            pathMatch: 'full'
        },
        {
            path: 'export-settings',
            component: ExportSettingsComponent,
            data: {
                breadcrumb: 'SALES_SETTINGS.TITLES.EXPORT_SETTINGS'
            },
            canActivate: [authCanActivateGuard],
            canDeactivate: [unsavedChangesGuard()]
        },
        {
            path: 'error-settings',
            component: ErrorSettingsComponent,
            data: {
                breadcrumb: 'SALES_SETTINGS.TITLES.ERROR_SETTINGS'
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
export class SalesSettingsRoutingModule { }
