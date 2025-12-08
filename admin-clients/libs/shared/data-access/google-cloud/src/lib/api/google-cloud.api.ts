import { buildHttpParams } from '@OneboxTM/utils-http';
import { GOOGLE_CLOUD_API_KEY } from '@admin-clients/shared/core/data-access';
import { HttpBackend, HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Loader } from '@googlemaps/js-api-loader';
import { Observable, from } from 'rxjs';
import { GoogleMapsTimezoneResponse } from '../models/google-cloud.model';

@Injectable({ providedIn: 'root' })
export class GoogleCloudApi {
    readonly #apiKey = inject(GOOGLE_CLOUD_API_KEY);
    readonly #loader = new Loader({ apiKey: this.#apiKey });
    readonly #httpBackend = inject(HttpBackend);
    readonly #plainHttp = new HttpClient(this.#httpBackend);
    readonly #MAPS_API = 'https://maps.googleapis.com/maps/api/timezone/json';

    getPlacesLibrary$(): Observable<google.maps.PlacesLibrary> {
        return from(this.#loader.importLibrary('places'));
    }

    getMapsTimezone$(latitude: string, longitude: string): Observable<GoogleMapsTimezoneResponse> {
        const params = {
            location: `${latitude},${longitude}`,
            timestamp: Math.floor(Date.now() / 1000),
            key: this.#apiKey
        };
        return this.#plainHttp.get<GoogleMapsTimezoneResponse>(this.#MAPS_API, { params: buildHttpParams(params) });
    }
}
