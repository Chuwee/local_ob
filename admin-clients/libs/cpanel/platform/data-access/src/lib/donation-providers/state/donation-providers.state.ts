import { StateProperty } from '@OneboxTM/utils-state';
import { Injectable } from '@angular/core';
import { DonationProvider } from '../model/donation-provider.model';

@Injectable({ providedIn: 'root' })
export class DonationProvidersState {

    readonly donationProviders = new StateProperty<DonationProvider[]>();

}
