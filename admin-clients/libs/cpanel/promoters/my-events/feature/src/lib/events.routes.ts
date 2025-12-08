import { authCanActivateGuard } from '@admin-clients/cpanel/core/data-access';
import { Routes } from '@angular/router';
import { eventResolver } from './event-resolver';
import { EventsListComponent } from './list/events-list.component';

export const EVENTS_ROUTES: Routes = [
    {
        path: '',
        component: EventsListComponent,
        canActivate: [authCanActivateGuard]
    },
    {
        path: ':eventId',
        loadChildren: () => import('@admin-clients/cpanel-promoters-events-details-feature').then(m => m.EVENT_ROUTES),
        resolve: {
            event: eventResolver
        },
        data: {
            breadcrumb: 'eventName'
        }
    }
];
