import { Routes } from '@angular/router';
import { BiUserDetailsComponent } from './details/bi-user-details.component';
import { BiUserGeneralDataComponent } from './general-data/bi-user-general-data.component';
import { BiUsersListComponent } from './list/bi-users-list.component';

export const BI_USERS_ROUTES: Routes = [
    {
        path: '',
        component: BiUsersListComponent
    },
    {
        path: ':userId',
        component: BiUserDetailsComponent,
        children: [
            {
                path: '',
                redirectTo: 'general-data',
                pathMatch: 'full'
            },
            {
                path: 'general-data',
                component: BiUserGeneralDataComponent
            }
        ]
    }
];
