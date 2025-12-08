import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { authCanActivateGuard } from '@admin-clients/shi-panel/data-access-auth';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MatcherSettingsPageComponent } from './details/matcher-settings-page.component';
import { ExportSettingsComponent } from './export-settings/export-settings.component';
import { ListSettingsComponent } from './list-settings/list-settings.component';

const routes: Routes = [{
    path: '',
    component: MatcherSettingsPageComponent,
    children: [
        {
            path: '',
            redirectTo: 'list-settings',
            pathMatch: 'full'
        },
        {
            path: 'list-settings',
            component: ListSettingsComponent,
            data: {
                breadcrumb: 'MATCHER_SETTINGS.TITLES.LIST_SETTINGS'
            },
            canActivate: [authCanActivateGuard],
            canDeactivate: [unsavedChangesGuard()]
        },
        {
            path: 'export-settings',
            component: ExportSettingsComponent,
            data: {
                breadcrumb: 'MATCHER_SETTINGS.TITLES.EXPORT_SETTINGS'
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
export class MatcherSettingsRoutingModule { }
