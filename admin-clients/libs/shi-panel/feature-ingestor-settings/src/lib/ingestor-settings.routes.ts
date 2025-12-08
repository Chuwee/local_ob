import { authCanActivateGuard } from '@admin-clients/shi-panel/data-access-auth';
import { Routes } from '@angular/router';

export const INGESTOR_SETTINGS_ROUTES: Routes = [
    {
        path: '',
        canActivate: [authCanActivateGuard],
        loadChildren: () => import('./ingestor-settings/ingestor-settings-routing.module').then(m => m.IngestorSettingsRoutingModule)
    }
];
