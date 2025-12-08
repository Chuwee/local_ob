import { StateManager, mapMetadata, getListData, getMetadata } from '@OneboxTM/utils-state';
import {
    ExportRequest
} from '@admin-clients/shared/data-access/models';
import { Injectable, inject } from '@angular/core';
import { map, Observable, tap, withLatestFrom } from 'rxjs';
import { PayoutsApi } from './api/payouts.api';
import { GetPayoutsRequest } from './models/get-payouts-request.model';
import { PayoutStatus } from './models/payout.model';
import { PayoutsState } from './state/payouts.state';

@Injectable()
export class PayoutsService {

    #payoutsApi = inject(PayoutsApi);
    #payoutsState = inject(PayoutsState);

    readonly payoutsList = Object.freeze({
        load: (request: GetPayoutsRequest, relevance = false): void => {
            if (relevance && request.q) {
                request.sort = null;
            }
            return StateManager.load(
                this.#payoutsState.payoutsList,
                this.#payoutsApi.getPayouts(request).pipe(mapMetadata())
            );
        },
        getData$: () => this.#payoutsState.payoutsList.getValue$().pipe(getListData()),
        getMetaData$: () => this.#payoutsState.payoutsList.getValue$().pipe(getMetadata()),
        export: (request: GetPayoutsRequest, data: ExportRequest) => StateManager.inProgress(
            this.#payoutsState.exportPayouts,
            this.#payoutsApi.exportPayouts(request, data)
        ),

        error$: () => this.#payoutsState.payoutsList.getError$(),
        loading$: () => this.#payoutsState.payoutsList.isInProgress$(),
        loadingExport$: () => this.#payoutsState.exportPayouts.isInProgress$(),
        clear: () => this.#payoutsState.payoutsList.setValue(null)
    });

    updatePayoutStatus(id: string, status: PayoutStatus): Observable<void> {
        return this.#payoutsApi.putPayoutStatus(id, status)
            .pipe(
                withLatestFrom(this.#payoutsState.payoutsList.getValue$()),
                tap(([_, list]) => {
                    const payout = list.data.find(element => element.uuid === id);
                    if (payout) {
                        payout.payout_status = status;
                    }
                    this.#payoutsState.payoutsList.setValue(list);
                }),
                map(() => null));
    }

}
