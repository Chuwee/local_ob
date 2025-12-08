import { buildHttpParams } from '@OneboxTM/utils-http';
import { PromotionChannels, PutPromotionChannels } from '@admin-clients/cpanel/promoters/data-access';
import { CommunicationTextContent } from '@admin-clients/cpanel/shared/data-access';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { EventPromotionPacks } from '../models/event-promotion-packs.model';
import { EventPromotionPriceTypes } from '../models/event-promotion-price-types.model';
import { EventPromotionRates } from '../models/event-promotion-rates.model';
import { EventPromotionSessions } from '../models/event-promotion-sessions.model';
import { EventPromotion } from '../models/event-promotion.model';
import { GetEventPromotionsRequest } from '../models/get-event-promotions-request.model';
import { GetEventPromotionsResponse } from '../models/get-event-promotions-response.model';
import { PostEventPromotion } from '../models/post-event-promotion.model';
import { PutEventPromotionPriceTypes } from '../models/put-event-promotion-price-types.model';
import { PutEventPromotionRates } from '../models/put-event-promotion-rates.model';
import { PutEventPromotionSessions } from '../models/put-event-promotion-sessions.model';

@Injectable()
export class EventPromotionsApi {
    private readonly BASE_API = inject(APP_BASE_API);
    private readonly EVENTS_API = `${this.BASE_API}/mgmt-api/v1/events`;
    private readonly PROMOTIONS = '/promotions';
    private readonly _http = inject(HttpClient);

    getPromotionsList(eventId: number, request: GetEventPromotionsRequest): Observable<GetEventPromotionsResponse> {
        const params = buildHttpParams(request);
        return this._http.get<GetEventPromotionsResponse>(
            `${this.EVENTS_API}/${eventId}${this.PROMOTIONS}`, { params }
        );
    }

    getPromotion(eventId: number, promotionId: number): Observable<EventPromotion> {
        return this._http.get<EventPromotion>(`${this.EVENTS_API}/${eventId}${this.PROMOTIONS}/${promotionId}`);
    }

    postPromotion(eventId: number, promotion: PostEventPromotion): Observable<{ id: number }> {
        return this._http.post<{ id: number }>(`${this.EVENTS_API}/${eventId}${this.PROMOTIONS}`, promotion);
    }

    putPromotion(eventId: number, promotionId: number, request: EventPromotion): Observable<void> {
        return this._http.put<void>(`${this.EVENTS_API}/${eventId}${this.PROMOTIONS}/${promotionId}`, request);
    }

    deletePromotion(eventId: number, promotionId: number): Observable<void> {
        return this._http.delete<void>(`${this.EVENTS_API}/${eventId}${this.PROMOTIONS}/${promotionId}`);
    }

    clonePromotion(eventId: number, promotionId: number): Observable<{ id: number }> {
        return this._http.post<{ id: number }>(`${this.EVENTS_API}/${eventId}${this.PROMOTIONS}/${promotionId}/clone`, {});
    }

    getPromotionChannelTextContents(eventId: number, promotionId: number): Observable<CommunicationTextContent[]> {
        return this._http.get<CommunicationTextContent[]>(
            `${this.EVENTS_API}/${eventId}${this.PROMOTIONS}/${promotionId}/channel-contents/texts`
        );
    }

    postPromotionChannelTextContents(eventId: number, promotionId: number, contents: CommunicationTextContent[]): Observable<void> {
        return this._http.post<void>(`${this.EVENTS_API}/${eventId}${this.PROMOTIONS}/${promotionId}/channel-contents/texts`, contents);
    }

    getPromotionChannels(eventId: number, promotionId: number): Observable<PromotionChannels> {
        return this._http.get<PromotionChannels>(`${this.EVENTS_API}/${eventId}${this.PROMOTIONS}/${promotionId}/channels`);
    }

    putPromotionChannels(eventId: number, promotionId: number, request: PutPromotionChannels): Observable<void> {
        return this._http.put<void>(`${this.EVENTS_API}/${eventId}${this.PROMOTIONS}/${promotionId}/channels`, request);
    }

    getPromotionSessions(eventId: number, promotionId: number): Observable<EventPromotionSessions> {
        return this._http.get<EventPromotionSessions>(`${this.EVENTS_API}/${eventId}${this.PROMOTIONS}/${promotionId}/sessions`);
    }

    putPromotionSessions(eventId: number, promotionId: number, request: PutEventPromotionSessions): Observable<void> {
        return this._http.put<void>(`${this.EVENTS_API}/${eventId}${this.PROMOTIONS}/${promotionId}/sessions`, request);
    }

    getPromotionPriceTypes(eventId: number, promotionId: number): Observable<EventPromotionPriceTypes> {
        return this._http.get<EventPromotionPriceTypes>(`${this.EVENTS_API}/${eventId}${this.PROMOTIONS}/${promotionId}/price-types`);
    }

    putPromotionPriceTypes(eventId: number, promotionId: number, request: PutEventPromotionPriceTypes): Observable<void> {
        return this._http.put<void>(`${this.EVENTS_API}/${eventId}${this.PROMOTIONS}/${promotionId}/price-types`, request);
    }

    getPromotionRates(eventId: number, promotionId: number): Observable<EventPromotionRates> {
        return this._http.get<EventPromotionRates>(`${this.EVENTS_API}/${eventId}${this.PROMOTIONS}/${promotionId}/rates`);
    }

    putPromotionRates(eventId: number, promotionId: number, request: PutEventPromotionRates): Observable<void> {
        return this._http.put<void>(`${this.EVENTS_API}/${eventId}${this.PROMOTIONS}/${promotionId}/rates`, request);
    }

    getPromotionPacks(eventId: number, promotionId: number): Observable<EventPromotionPacks> {
        return this._http.get<EventPromotionPacks>(`${this.EVENTS_API}/${eventId}${this.PROMOTIONS}/${promotionId}/packs`);
    }

    putPromotionPacks(eventId: number, promotionId: number, request: EventPromotionPacks): Observable<void> {
        return this._http.put<void>(`${this.EVENTS_API}/${eventId}${this.PROMOTIONS}/${promotionId}/packs`, request);
    }
}
