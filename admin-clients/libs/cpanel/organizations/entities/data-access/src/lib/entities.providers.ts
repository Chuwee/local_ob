import { IntegrationsApi } from '@admin-clients/cpanel/organizations/data-access';
import { Provider } from '@angular/core';
import { EntitiesApi } from './api/entities.api';
import { EntitiesService } from './entities.service';
import { EntitiesState } from './state/entities.state';

export const entitiesProviders: Provider[] = [
    EntitiesApi, EntitiesState, EntitiesService, IntegrationsApi
];
