import { StateManager } from '@OneboxTM/utils-state';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { DonationProvidersApi } from './api/donation-providers.api';
import { DonationProvider } from './model/donation-provider.model';
import { DonationProvidersState } from './state/donation-providers.state';

export * from './model/donation-provider.model';
export * from './model/donations-providers.enum';

@Injectable({
    providedIn: 'root'
})
export class DonationProvidersService {

    private readonly _api = inject(DonationProvidersApi);
    private readonly _state = inject(DonationProvidersState);

    loadDonationProviders(): void {
        StateManager.loadIfNull(
            this._state.donationProviders,
            this._api.getDonationProviders()
        );
    }

    getDonationProviders$(): Observable<DonationProvider[]> {
        return this._state.donationProviders.getValue$();
    }

    isDonationProvidersInProgress$(): Observable<boolean> {
        return this._state.donationProviders.isInProgress$();
    }

}
