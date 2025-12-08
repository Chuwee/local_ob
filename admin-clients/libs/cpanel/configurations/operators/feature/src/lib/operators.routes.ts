import { authCanActivateGuard } from '@admin-clients/cpanel/core/data-access';
import { provideOperatorsState } from '@admin-clients/cpanel-configurations-operators-data-access';
import { Routes } from '@angular/router';
import { OperatorsListComponent } from './list/operators-list.component';

export const OPERATORS_ROUTES: Routes = [
    {
        path: '',
        providers: [
            provideOperatorsState()
        ],
        children: [
            {
                path: '',
                component: OperatorsListComponent,
                canActivate: [authCanActivateGuard]
            },
            {
                path: ':operatorId',
                loadChildren: () => import('./operator/operator.routes').then(r => r.OPERATOR_ROUTES),
                data: {
                    breadcrumb: 'TITLES.OPERATOR_DETAILS'
                }
            }
        ]
    }
];
