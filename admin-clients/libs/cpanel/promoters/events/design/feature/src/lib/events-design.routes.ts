import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { Routes } from '@angular/router';
import { EventsDesignComponent } from './events-design.component';

export const EVENT_DESIGN_ROUTES: Routes = [
    {
        path: '',
        component: EventsDesignComponent,
        data: {
            breadcrumb: 'EVENTS.TITLES.DESIGN'
        },
        children: [
            {
                path: '',
                redirectTo: 'sessions',
                pathMatch: 'full'
            },
            {
                path: 'sessions',
                loadComponent: () => import('./sessions/events-design-sessions.component')
                    .then(m => m.EventsDesignSessionsComponent),
                data: {
                    breadcrumb: 'EVENTS.TITLES.SESSIONS'
                },
                canDeactivate: [unsavedChangesGuard()]
            },
            {
                path: 'post-booking-questions',
                loadComponent: () => import('./post-booking-questions/events-design-post-booking-questions.component')
                    .then(m => m.EventsDesignPostBookingQuestionsComponent),
                data: {
                    breadcrumb: 'EVENTS.TITLES.POST_BOOKING_QUESTIONS'
                },
                canDeactivate: [unsavedChangesGuard()]
            }
        ]
    }
];
