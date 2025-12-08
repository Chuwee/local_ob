
import { Routes } from '@angular/router';
import { BiSubscriptionsListComponent } from './list/bi-subscriptions-list.component';

export const BI_SUBSCRIPTIONS_ROUTES: Routes = [
    {
        path: '',
        children: [
            {
                path: '',
                component: BiSubscriptionsListComponent
            }
        ]
    }
];
