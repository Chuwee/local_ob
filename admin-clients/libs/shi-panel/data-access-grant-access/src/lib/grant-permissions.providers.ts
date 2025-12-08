import { Provider } from '@angular/core';
import { GrantPermissionsApi } from './grant-permissions.api';
import { GrantPermissionsService } from './grant-permissions.service';
import { GrantPermissionsState } from './grant-permissions.state';

export const grantPermissionsProviders: Provider[] = [
    GrantPermissionsApi,
    GrantPermissionsService,
    GrantPermissionsState
];
