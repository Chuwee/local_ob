import { buildHttpParams } from '@OneboxTM/utils-http';
import { GetEntitiesResponse } from '@admin-clients/shared/common/data-access';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { DestinationChannelResponse } from '../models/destination-channel-response.model';
import { DestinationChannel } from '../models/destination-channel.model';

@Injectable({ providedIn: 'root' })
export class FeverApi {
    readonly #http = inject(HttpClient);
    readonly #BASE_API = inject(APP_BASE_API);
    readonly #FEVER_API = `${this.#BASE_API}/fever-api/v1`;
    readonly #ENTITIES_FVZONE = '/entities/fv-zone';

    getEntities(): Observable<GetEntitiesResponse> {
        return this.#http.get<GetEntitiesResponse>(`${this.#FEVER_API}${this.#ENTITIES_FVZONE}`);
    }

    getDestinationChannels(entityId: number, type: string = 'marketplace'): Observable<DestinationChannel[]> {
        const params = buildHttpParams({
            entity: entityId,
            type
        });
        return this.#http.get<DestinationChannelResponse>(`${this.#FEVER_API}/channels`, { params })
            .pipe(map(res => res.channels));
    }
}
