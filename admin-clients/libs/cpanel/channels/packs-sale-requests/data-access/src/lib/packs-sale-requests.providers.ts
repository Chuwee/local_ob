import { Provider } from '@angular/core';
import { PacksSaleRequestsApi } from './api/packs-sale-requests.api';
import { PacksSaleRequestsService } from './packs-sale-requests.service';
import { PackSaleRequestsState } from './state/pack-sale-requests.state';

export const packsSaleRequestsProviders: Provider[] = [
    PacksSaleRequestsApi,
    PackSaleRequestsState,
    PacksSaleRequestsService
];
