import { StateProperty } from '@OneboxTM/utils-state';
import { Injectable } from '@angular/core';
import { ExternalProviderEvents } from '../models/external-provider-events.model';
import { ExternalProviderPresales } from '../models/external-provider-presales.model';
import { ExternalProviderSessions } from '../models/external-provider-sessions.model';

@Injectable({
    providedIn: 'root'
})
export class PromotersExternalProviderState {
    readonly providerEvents = new StateProperty<ExternalProviderEvents[]>();
    readonly providerSessions = new StateProperty<ExternalProviderSessions[]>();
    readonly providerSessionsPresales = new StateProperty<ExternalProviderPresales[]>();
    readonly providerSeasonTicketsPresales = new StateProperty<ExternalProviderPresales[]>();
}
