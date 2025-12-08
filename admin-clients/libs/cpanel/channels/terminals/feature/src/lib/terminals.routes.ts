import { authCanActivateGuard } from '@admin-clients/cpanel/core/data-access';
import { terminalsProviders } from '@admin-clients/cpanel-channels-terminals-data-access';
import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { Routes } from '@angular/router';
import { TerminalsListComponent } from './list/terminals-list.component';
import { terminalDetailsResolver } from './terminal/terminal-details-resolver';
import { TerminalDetailsComponent } from './terminal/terminal-details.component';

export const routes: Routes = [
    {
        path: '',
        children: [
            {
                path: '',
                canActivate: [authCanActivateGuard],
                component: TerminalsListComponent
            },
            {
                path: ':terminalId',
                component: TerminalDetailsComponent,
                canDeactivate: [unsavedChangesGuard()],
                resolve: { terminal: terminalDetailsResolver },
                data: { breadcrumb: 'TERMINAL_NAME' }
            }
        ],
        providers: terminalsProviders()
    }
];
