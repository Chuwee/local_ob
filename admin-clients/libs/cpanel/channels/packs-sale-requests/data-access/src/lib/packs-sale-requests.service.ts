import { getListData, getMetadata, mapMetadata, StateManager } from '@OneboxTM/utils-state';
import { inject, Injectable } from '@angular/core';
import { PacksSaleRequestsApi } from './api/packs-sale-requests.api';
import { PackSaleRequestsState } from './state/pack-sale-requests.state';
import { GetPacksSaleRequestsReq, PackSaleRequestStatus } from './models/pack-sale-request.model';

@Injectable()
export class PacksSaleRequestsService {
    readonly #api = inject(PacksSaleRequestsApi);
    readonly #state = inject(PackSaleRequestsState);

    readonly packsSaleRequestsList = Object.freeze({
        load: (request: GetPacksSaleRequestsReq) => StateManager.load(
            this.#state.packsSaleRequestsList,
            this.#api.getPackSaleRequests(request).pipe(mapMetadata())
        ),
        getData$: () => this.#state.packsSaleRequestsList.getValue$().pipe(getListData()),
        getMetadata$: () => this.#state.packsSaleRequestsList.getValue$().pipe(getMetadata()),
        loading$: () => this.#state.packsSaleRequestsList.isInProgress$(),
        clear: () => this.#state.packsSaleRequestsList.setValue(null)
    });

    readonly packSaleRequest = Object.freeze({
        status: Object.freeze({
            update: (packSaleRequestId: number, status: PackSaleRequestStatus) => StateManager.inProgress(
                this.#state.packsSaleRequestStatus,
                this.#api.putPackSaleRequestStatus(packSaleRequestId, status)
            ),
            inProgress$: () => this.#state.packsSaleRequestStatus.isInProgress$()
        })
    });
}
