import { buildHttpParams } from '@OneboxTM/utils-http';
import { PromotionChannels } from '@admin-clients/cpanel/promoters/data-access';
import { PutSeasonTicketPromotionChannels } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import { CommunicationTextContent } from '@admin-clients/cpanel/shared/data-access';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {
    GetSeasonTicketPromotionsRequest,
    SeasonTicketPromotion,
    PostSeasonTicketPromotion,
    SeasonTicketPromotionPriceTypes,
    PutSeasonTicketPromotionPriceTypes,
    SeasonTicketPromotionRates,
    GetSeasonTicketPromotionsResponse,
    PutSeasonTicketPromotionRates
} from '../models/season-ticket-promotion.model';

@Injectable({ providedIn: 'root' })
export class SeasonTicketPromotionsApi {

    private readonly BASE_API = inject(APP_BASE_API);
    private readonly SEASON_TICKETS_API = `${this.BASE_API}/mgmt-api/v1/season-tickets`;
    private readonly PROMOTIONS = '/promotions';

    private readonly _http = inject(HttpClient);

    getPromotionsList(seasonTicketId: number, request: GetSeasonTicketPromotionsRequest): Observable<GetSeasonTicketPromotionsResponse> {
        const params = buildHttpParams(request);
        return this._http.get<GetSeasonTicketPromotionsResponse>(
            `${this.SEASON_TICKETS_API}/${seasonTicketId}${this.PROMOTIONS}`, { params }
        );
    }

    getPromotion(seasonTicketId: number, promotionId: number): Observable<SeasonTicketPromotion> {
        return this._http.get<SeasonTicketPromotion>(`${this.SEASON_TICKETS_API}/${seasonTicketId}${this.PROMOTIONS}/${promotionId}`);
    }

    postPromotion(seasonTicketId: number, promotion: PostSeasonTicketPromotion): Observable<{ id: number }> {
        return this._http.post<{ id: number }>(`${this.SEASON_TICKETS_API}/${seasonTicketId}${this.PROMOTIONS}`, promotion);
    }

    putPromotion(seasonTicketId: number, promotionId: number, request: SeasonTicketPromotion): Observable<void> {
        return this._http.put<void>(`${this.SEASON_TICKETS_API}/${seasonTicketId}${this.PROMOTIONS}/${promotionId}`, request);
    }

    deletePromotion(seasonTicketId: number, promotionId: number): Observable<void> {
        return this._http.delete<void>(`${this.SEASON_TICKETS_API}/${seasonTicketId}${this.PROMOTIONS}/${promotionId}`);
    }

    clonePromotion(seasonTicketId: number, promotionId: number): Observable<{ id: number }> {
        return this._http.post<{ id: number }>(`${this.SEASON_TICKETS_API}/${seasonTicketId}${this.PROMOTIONS}/${promotionId}/clone`, {});
    }

    getPromotionChannelTextContents(seasonTicketId: number, promotionId: number): Observable<CommunicationTextContent[]> {
        return this._http.get<CommunicationTextContent[]>(
            `${this.SEASON_TICKETS_API}/${seasonTicketId}${this.PROMOTIONS}/${promotionId}/channel-contents/texts`
        );
    }

    postPromotionChannelTextContents(seasonTicketId: number, promotionId: number, contents: CommunicationTextContent[]): Observable<void> {
        return this._http.post<void>(
            `${this.SEASON_TICKETS_API}/${seasonTicketId}${this.PROMOTIONS}/${promotionId}/channel-contents/texts`,
            contents
        );
    }

    getPromotionChannels(seasonTicketId: number, promotionId: number): Observable<PromotionChannels> {
        return this._http.get<PromotionChannels>(
            `${this.SEASON_TICKETS_API}/${seasonTicketId}${this.PROMOTIONS}/${promotionId}/channels`
        );
    }

    putPromotionChannels(seasonTicketId: number, promotionId: number, request: PutSeasonTicketPromotionChannels): Observable<void> {
        return this._http.put<void>(`${this.SEASON_TICKETS_API}/${seasonTicketId}${this.PROMOTIONS}/${promotionId}/channels`, request);
    }

    getPromotionPriceTypes(seasonTicketId: number, promotionId: number): Observable<SeasonTicketPromotionPriceTypes> {
        return this._http.get<SeasonTicketPromotionPriceTypes>(
            `${this.SEASON_TICKETS_API}/${seasonTicketId}${this.PROMOTIONS}/${promotionId}/price-types`
        );
    }

    putPromotionPriceTypes(seasonTicketId: number, promotionId: number, request: PutSeasonTicketPromotionPriceTypes): Observable<void> {
        return this._http.put<void>(`${this.SEASON_TICKETS_API}/${seasonTicketId}${this.PROMOTIONS}/${promotionId}/price-types`, request);
    }

    getPromotionRates(seasonTicketId: number, promotionId: number): Observable<SeasonTicketPromotionRates> {
        return this._http.get<SeasonTicketPromotionRates>(
            `${this.SEASON_TICKETS_API}/${seasonTicketId}${this.PROMOTIONS}/${promotionId}/rates`
        );
    }

    putPromotionRates(seasonTicketId: number, promotionId: number, request: PutSeasonTicketPromotionRates): Observable<void> {
        return this._http.put<void>(`${this.SEASON_TICKETS_API}/${seasonTicketId}${this.PROMOTIONS}/${promotionId}/rates`, request);
    }
}
