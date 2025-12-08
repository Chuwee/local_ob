import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MultiSessionCapacityComponent } from '../capacity/multi-session-capacity.component';
import { MultiSessionCommunicationComponent } from '../communication/multi-session-communication.component';
import { MultiSessionOtherSettingsComponent } from '../other-settings/multi-session-other-settings.component';
import { MultiSessionPlanningComponent } from '../planning/multi-session-planning.component';
import { MultiSessionDetailsComponent } from './multi-session-details.component';

const routes: Routes = [
    {
        path: '',
        component: MultiSessionDetailsComponent,
        children: [
            {
                path: '',
                pathMatch: 'full',
                redirectTo: 'planning'
            },
            {
                path: 'planning',
                pathMatch: 'full',
                component: MultiSessionPlanningComponent,
                data: {
                    breadcrumb: 'EVENTS.PLANNING'
                },
                canDeactivate: [unsavedChangesGuard()]
            },
            {
                path: 'communication',
                pathMatch: 'full',
                component: MultiSessionCommunicationComponent,
                data: {
                    breadcrumb: 'EVENTS.COMMUNICATION'
                },
                canDeactivate: [unsavedChangesGuard()]
            },
            {
                path: 'capacity',
                pathMatch: 'full',
                component: MultiSessionCapacityComponent,
                data: {
                    breadcrumb: 'EVENTS.CAPACITY'
                },
                canDeactivate: [unsavedChangesGuard()]
            },
            {
                path: 'other-settings',
                pathMatch: 'full',
                component: MultiSessionOtherSettingsComponent,
                data: {
                    breadcrumb: 'EVENTS.OTHER_SETTINGS'
                },
                canDeactivate: [unsavedChangesGuard()]
            }
        ]
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class MultiSessionDetailsRoutingModule {
}
