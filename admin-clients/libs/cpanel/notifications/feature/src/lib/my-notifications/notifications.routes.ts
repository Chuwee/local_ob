import { authCanActivateGuard } from '@admin-clients/cpanel/core/data-access';
import { eventsProviders } from '@admin-clients/cpanel/promoters/events/data-access';
import { eventSessionsProviders } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { Routes } from '@angular/router';
import { notificationsGuard } from '../notifications.guard';
import { NotificationsListComponent } from './list/notifications-list.component';
import { notificationDetailsResolver } from './notification/details/notification-details-resolver';
import { NotificationDetailsComponent } from './notification/details/notification-details.component';

export const routes: Routes = [
    {
        path: '',
        canActivate: [notificationsGuard],
        providers: [
            ...eventsProviders,
            ...eventSessionsProviders
        ],
        children: [
            {
                path: '',
                canActivate: [authCanActivateGuard],
                component: NotificationsListComponent
            },
            {
                path: ':code',
                component: NotificationDetailsComponent,
                canDeactivate: [unsavedChangesGuard()],
                resolve: {
                    ticket: notificationDetailsResolver
                },
                data: {
                    breadcrumb: 'NOTIFICATIONS.EMAIL_NOTIFICATION.DETAIL.TITLE'
                }
            }
        ]
    }

];