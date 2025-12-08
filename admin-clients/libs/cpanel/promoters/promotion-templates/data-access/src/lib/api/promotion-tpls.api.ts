import { buildHttpParams } from '@OneboxTM/utils-http';
import { PromotionChannels, PutPromotionChannels } from '@admin-clients/cpanel/promoters/data-access';
import { CommunicationTextContent } from '@admin-clients/cpanel/shared/data-access';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { GetPromotionTplsRequest } from '../models/get-promotion-tpls-request.model';
import { GetPromotionTplsResponse } from '../models/get-promotion-tpls-response.model';
import { PostPromotionTpl } from '../models/post-promotion-tpl.model';
import { PromotionTpl } from '../models/promotion-tpl.model';

@Injectable({
    providedIn: 'root'
})
export class PromotionTemplatesApi {

    private readonly BASE_API = inject(APP_BASE_API);
    private readonly PROMOTION_TEMPLATES_API = `${this.BASE_API}/mgmt-api/v1/event-promotion-templates`;

    private readonly _http = inject(HttpClient);

    getPromotionTemplates(request: GetPromotionTplsRequest): Observable<GetPromotionTplsResponse> {
        const params = buildHttpParams({
            q: request.q,
            sort: request.sort,
            limit: request.limit,
            offset: request.offset,
            type: request.type,
            entity_id: request.entityId
        });
        return this._http.get<GetPromotionTplsResponse>(this.PROMOTION_TEMPLATES_API, { params });
    }

    getPromotionTemplate(promotionTplId: number): Observable<PromotionTpl> {
        return this._http.get<PromotionTpl>(`${this.PROMOTION_TEMPLATES_API}/${promotionTplId}`);
    }

    deletePromotionTemplate(promotionTplId: number): Observable<void> {
        return this._http.delete<void>(`${this.PROMOTION_TEMPLATES_API}/${promotionTplId}`);
    }

    postPromotionTemplate(promotionTpl: PostPromotionTpl): Observable<{ id: number }> {
        return this._http.post<{ id: number }>(this.PROMOTION_TEMPLATES_API, promotionTpl);
    }

    putPromotionTemplate(promotionTplId: number, promotionTpl: PromotionTpl): Observable<void> {
        return this._http.put<void>(`${this.PROMOTION_TEMPLATES_API}/${promotionTplId}`, promotionTpl);
    }

    getPromotionTplChannelContents(promotionTplId: number): Observable<CommunicationTextContent[]> {
        return this._http.get<CommunicationTextContent[]>(`${this.PROMOTION_TEMPLATES_API}/${promotionTplId}/channel-contents/texts`);
    }

    postPromotionTplChannelContents(promotionTplId: number, contents: CommunicationTextContent[]): Observable<void> {
        return this._http.post<void>(`${this.PROMOTION_TEMPLATES_API}/${promotionTplId}/channel-contents/texts`, contents);
    }

    getPromotionTplChannels(promotionId: number): Observable<PromotionChannels> {
        return this._http.get<PromotionChannels>(`${this.PROMOTION_TEMPLATES_API}/${promotionId}/channels`);
    }

    putPromotionTplChannels(promotionId: number, request: PutPromotionChannels): Observable<void> {
        return this._http.put<void>(`${this.PROMOTION_TEMPLATES_API}/${promotionId}/channels`, request);
    }
}
