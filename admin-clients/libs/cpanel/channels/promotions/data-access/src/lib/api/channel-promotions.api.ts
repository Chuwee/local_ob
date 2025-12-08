import { buildHttpParams } from '@OneboxTM/utils-http';
import { CommunicationTextContent } from '@admin-clients/cpanel/shared/data-access';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ChannelPromotionEvents, PutChannelPromotionEvents } from '../models/channel-promotion-events.model';
import { ChannelPromotionPriceTypes, PutChannelPromotionPriceTypes } from '../models/channel-promotion-price-types.model';
import { ChannelPromotionSessions, PutChannelPromotionSessions } from '../models/channel-promotion-sessions.model';
import { ChannelPromotion } from '../models/channel-promotion.model';
import { GetChannelPromotionsResponse } from '../models/get-channel-promotions-response.model';
import { PostChannelPromotion } from '../models/post-channel-promotion.model';

@Injectable({
    providedIn: 'root'
})
export class ChannelPromotionsApi {

    private readonly BASE_API = inject(APP_BASE_API);
    private readonly CHANNELS_API = `${this.BASE_API}/mgmt-api/v1/channels`;
    private readonly PROMOTIONS = '/promotions';

    private readonly _http = inject(HttpClient);

    getPromotionsList(channelId: number, request: PageableFilter): Observable<GetChannelPromotionsResponse> {
        const params = buildHttpParams(request);
        return this._http.get<GetChannelPromotionsResponse>(
            `${this.CHANNELS_API}/${channelId}${this.PROMOTIONS}`, { params }
        );
    }

    getPromotion(channelId: number, promotionId: number): Observable<ChannelPromotion> {
        return this._http.get<ChannelPromotion>(`${this.CHANNELS_API}/${channelId}${this.PROMOTIONS}/${promotionId}`);
    }

    postPromotion(channelId: number, promotion: PostChannelPromotion): Observable<{ id: number }> {
        return this._http.post<{ id: number }>(`${this.CHANNELS_API}/${channelId}${this.PROMOTIONS}`, promotion);
    }

    putPromotion(channelId: number, promotionId: number, request: ChannelPromotion): Observable<void> {
        return this._http.put<void>(`${this.CHANNELS_API}/${channelId}${this.PROMOTIONS}/${promotionId}`, request);
    }

    deletePromotion(channelId: number, promotionId: number): Observable<void> {
        return this._http.delete<void>(`${this.CHANNELS_API}/${channelId}${this.PROMOTIONS}/${promotionId}`);
    }

    clonePromotion(channelId: number, promotionId: number): Observable<{ id: number }> {
        return this._http.post<{ id: number }>(`${this.CHANNELS_API}/${channelId}${this.PROMOTIONS}/${promotionId}/clone`, {});
    }

    getPromotionContents(channelId: number, promotionId: number): Observable<CommunicationTextContent[]> {
        return this._http.get<CommunicationTextContent[]>(
            `${this.CHANNELS_API}/${channelId}${this.PROMOTIONS}/${promotionId}/channel-contents/texts`
        );
    }

    postPromotionContents(channelId: number, promotionId: number, contents: CommunicationTextContent[]): Observable<void> {
        return this._http.post<void>(`${this.CHANNELS_API}/${channelId}${this.PROMOTIONS}/${promotionId}/channel-contents/texts`, contents);
    }

    getPromotionEvents(channelId: number, promotionId: number): Observable<ChannelPromotionEvents> {
        return this._http.get<ChannelPromotionEvents>(`${this.CHANNELS_API}/${channelId}${this.PROMOTIONS}/${promotionId}/events`);
    }

    putPromotionEvents(channelId: number, promotionId: number, request: PutChannelPromotionEvents): Observable<void> {
        return this._http.put<void>(`${this.CHANNELS_API}/${channelId}${this.PROMOTIONS}/${promotionId}/events`, request);
    }

    getPromotionSessions(channelId: number, promotionId: number): Observable<ChannelPromotionSessions> {
        return this._http.get<ChannelPromotionSessions>(`${this.CHANNELS_API}/${channelId}${this.PROMOTIONS}/${promotionId}/sessions`);
    }

    putPromotionSessions(channelId: number, promotionId: number, request: PutChannelPromotionSessions): Observable<void> {
        return this._http.put<void>(`${this.CHANNELS_API}/${channelId}${this.PROMOTIONS}/${promotionId}/sessions`, request);
    }

    getPromotionPriceTypes(channelId: number, promotionId: number): Observable<ChannelPromotionPriceTypes> {
        return this._http.get<ChannelPromotionPriceTypes>(`${this.CHANNELS_API}/${channelId}${this.PROMOTIONS}/${promotionId}/price-types`);
    }

    putPromotionPriceTypes(channelId: number, promotionId: number, request: PutChannelPromotionPriceTypes): Observable<void> {
        return this._http.put<void>(`${this.CHANNELS_API}/${channelId}${this.PROMOTIONS}/${promotionId}/price-types`, request);
    }

}
