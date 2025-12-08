import { authCanActivateGuard } from '@admin-clients/cpanel/core/data-access';
import { AdminChannelsService } from '@admin-clients/cpanel/migration/channels/data-access';
import { Routes } from '@angular/router';
import { AdminChannelsListComponent } from './list/admin-channels-list.component';

export const routes: Routes = [
    {
        path: '',
        providers: [
            AdminChannelsService
        ],
        canActivate: [authCanActivateGuard],
        component: AdminChannelsListComponent
    }
];

