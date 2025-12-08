import { authCanActivateGuard } from '@admin-clients/cpanel/core/data-access';
import { Routes } from '@angular/router';
import { LiteralsComponent } from './literals.component';

export const LITERALS_ROUTES: Routes = [
    {
        path: '',
        component: LiteralsComponent,
        canActivate: [authCanActivateGuard]
    }
];
