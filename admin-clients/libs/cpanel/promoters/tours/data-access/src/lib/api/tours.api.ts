import { buildHttpParams } from '@OneboxTM/utils-http';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { GetToursRequest } from '../models/get-tours-request.model';
import { GetToursResponse } from '../models/get-tours-response.model';
import { PostTour } from '../models/post-tour.model';
import { PutTour } from '../models/put-tour.model';
import { Tour } from '../models/tour.model';

@Injectable({
    providedIn: 'root'
})
export class ToursApi {

    private readonly BASE_API = inject(APP_BASE_API);
    private readonly TOURS_API = `${this.BASE_API}/mgmt-api/v1/events/tours`;

    private readonly _http = inject(HttpClient);

    getTours(request: GetToursRequest): Observable<GetToursResponse> {
        const params = buildHttpParams({
            entity_id: request.entityId,
            status: request.status
        });
        return this._http.get<GetToursResponse>(this.TOURS_API, { params });
    }

    getTour(id: number): Observable<Tour> {
        return this._http.get<Tour>(`${this.TOURS_API}/${id}`);
    }

    deleteTour(id: number): Observable<void> {
        return this._http.delete<void>(`${this.TOURS_API}/${id}`);
    }

    postTour(tour: PostTour): Observable<{ id: number }> {
        return this._http.post<{ id: number }>(`${this.TOURS_API}`, tour);
    }

    putTour(tour: PutTour): Observable<void> {
        return this._http.put<void>(`${this.TOURS_API}/${tour.id}`, tour);
    }
}
