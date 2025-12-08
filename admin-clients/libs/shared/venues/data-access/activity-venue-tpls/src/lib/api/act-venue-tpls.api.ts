import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { VenueTemplatePriceTypeCapacity } from '../models/venue-template-price-type-capacity.model';
import { VenueTemplateQuotaCapacity } from '../models/venue-template-quota-capacity.model';

@Injectable()
export class ActVenueTplsApi {

    private readonly BASE_API = inject(APP_BASE_API);
    private readonly VENUE_TEMPLATES_API = `${this.BASE_API}/mgmt-api/v1/venue-templates`;

    private readonly _http = inject(HttpClient);

    // VENUE TEMPLATE STRUCTURE MANAGING

    getTemplateQuotaCapacity(venueTemplateId: number): Observable<VenueTemplateQuotaCapacity[]> {
        return this._http.get<VenueTemplateQuotaCapacity[]>(`${this.VENUE_TEMPLATES_API}/${venueTemplateId}/capacity/quotas`);
    }

    updateTemplateQuotaCapacity(venueTemplateId: number, quotaCapacities: VenueTemplateQuotaCapacity[]): Observable<void> {
        return this._http.put<void>(
            `${this.VENUE_TEMPLATES_API}/${venueTemplateId}/capacity/quotas`,
            quotaCapacities);
    }

    getTemplatePriceTypesCapacity(venueTemplateId: number): Observable<VenueTemplatePriceTypeCapacity[]> {
        return this._http.get<VenueTemplatePriceTypeCapacity[]>(`${this.VENUE_TEMPLATES_API}/${venueTemplateId}/capacity/price-types`);
    }
}
