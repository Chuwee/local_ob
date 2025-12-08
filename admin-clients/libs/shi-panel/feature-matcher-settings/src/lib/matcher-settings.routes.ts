import { authCanActivateGuard } from '@admin-clients/shi-panel/data-access-auth';
import { Routes } from '@angular/router';

export const MATCHER_SETTINGS_ROUTES: Routes = [
    {
        path: '',
        canActivate: [authCanActivateGuard],
        loadChildren: () => import('./matcher-settings/matcher-settings-routing.module').then(m => m.MatcherSettingsRoutingModule)
    }
];
