import { authCanActivateGuard } from '@admin-clients/shi-panel/data-access-auth';
import { Routes } from '@angular/router';

export const SALES_SETTINGS_ROUTES: Routes = [
    {
        path: '',
        canActivate: [authCanActivateGuard],
        loadChildren: () => import('./sales-settings/sales-settings-routing.module').then(m => m.SalesSettingsRoutingModule)
    }
];
