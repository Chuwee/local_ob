import { Routes } from '@angular/router';
import { ProducersListComponent } from './list/producers-list.component';

export const routes: Routes = [
    {
        path: '',
        component: ProducersListComponent
    },
    {
        path: ':producerId',
        loadChildren: () => import('./producer/producer-details.routes').then(m => m.routes),
        data: {
            breadcrumb: 'TITLES.PRODUCER_DETAILS'
        }
    }
];