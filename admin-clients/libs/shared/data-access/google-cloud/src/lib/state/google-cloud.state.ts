/// <reference types="google.maps" />
import { StateProperty } from '@OneboxTM/utils-state';
import { Injectable } from '@angular/core';
import { GoogleMapsTimezoneResponse } from '../models/google-cloud.model';

@Injectable({ providedIn: 'root' })
export class GoogleCloudState {
    readonly placesLibrary = new StateProperty<google.maps.PlacesLibrary>();
    readonly timezone = new StateProperty<GoogleMapsTimezoneResponse>();
}
