import { authCanActivateGuard } from '@admin-clients/cpanel/core/data-access';
import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { Routes } from '@angular/router';
import { NotificationSettingDetailsComponent } from './setting-details/notification-setting-details.component';

export const routes: Routes = [
    {
        path: '',
        component: NotificationSettingDetailsComponent,
        canActivate: [authCanActivateGuard],
        canDeactivate: [unsavedChangesGuard()],
        data: {
            breadcrumb: 'TITLES.NOTIFICATIONS.MY_NOTIFICATION_SETTINGS'
        }
    }
];