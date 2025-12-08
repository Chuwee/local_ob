
import { buildHttpParams } from '@OneboxTM/utils-http';
import { CommunicationTextContent } from '@admin-clients/cpanel/shared/data-access';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { ExportRequest, ExportResponse } from '@admin-clients/shared/data-access/models';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { GetVoucherGroupsRequest } from '../models/get-voucher-groups-request.model';
import { GetVoucherGroupsResponse } from '../models/get-voucher-groups-response.model';
import { GetVoucherRequest } from '../models/get-vouchers-request.model';
import { GetVouchersResponse } from '../models/get-vouchers-response.model';
import { GiftCardGroupConfig } from '../models/gift-card-group-config.model';
import { GiftCardGroupContentImageType } from '../models/gift-card-group-content-image-type.enum';
import { GiftCardGroupContentImage } from '../models/gift-card-group-content-image.model';
import { GiftCardGroupContentImageRequest } from '../models/gift-card-group-content-image.request.model';
import { PostVoucherGroup } from '../models/post-voucher-group.model';
import { PostVoucher } from '../models/post-voucher.model';
import { PutGiftCardGroupConfig } from '../models/put-gift-card-group-config.model';
import { PutVoucherGroup } from '../models/put-voucher-group.model';
import { PutVoucher } from '../models/put-voucher.model';
import { ResendVoucherRequest } from '../models/resend-voucher-request.model';
import { VoucherGroup } from '../models/voucher-groups.model';
import { Voucher } from '../models/voucher.model';

@Injectable({
    providedIn: 'root'
})
export class VouchersApi {

    private readonly BASE_API = inject(APP_BASE_API);
    private readonly VOUCHER_GROUPS_API = `${this.BASE_API}/mgmt-api/v1/voucher-groups`;
    private readonly VOUCHERS = 'vouchers';

    private readonly _http = inject(HttpClient);

    getVoucherGroup(id: number): Observable<VoucherGroup> {
        return this._http.get<VoucherGroup>(`${this.VOUCHER_GROUPS_API}/${id}`);
    }

    getVoucherGroups(request: GetVoucherGroupsRequest): Observable<GetVoucherGroupsResponse> {
        const params = buildHttpParams(request);
        return this._http.get<GetVoucherGroupsResponse>(`${this.VOUCHER_GROUPS_API}`, { params });
    }

    getVouchers(voucherGroupId: number, request: GetVoucherRequest): Observable<GetVouchersResponse> {
        const params = buildHttpParams({
            sort: request.sort,
            offset: request.offset,
            limit: request.limit,
            status: request.status,
            q: request.q,
            aggs: request.aggs
        });
        return this._http.get<GetVouchersResponse>(`${this.VOUCHER_GROUPS_API}/${voucherGroupId}/${this.VOUCHERS}`, { params });
    }

    getVoucher(voucherGroupId: number, code: string): Observable<Voucher> {
        return this._http.get<Voucher>(`${this.VOUCHER_GROUPS_API}/${voucherGroupId}/${this.VOUCHERS}/${code}`);
    }

    getGiftCardGroupConfig(id: number): Observable<GiftCardGroupConfig> {
        return this._http.get<GiftCardGroupConfig>(`${this.VOUCHER_GROUPS_API}/${id}/gift-card-config`);
    }

    putGiftCardGroupConfig(id: number, config: PutGiftCardGroupConfig): Observable<void> {
        return this._http.put<void>(`${this.VOUCHER_GROUPS_API}/${id}/gift-card-config`, config);
    }

    postVoucher(id: number, voucher: PostVoucher): Observable<{ code: string }> {
        return this._http.post<{ code: string }>(`${this.VOUCHER_GROUPS_API}/${id}/${this.VOUCHERS}`, voucher);
    }

    postVouchers(groupId: number, voucherCodesData: PostVoucher[]): Observable<string[]> {
        return this._http.post<string[]>(`${this.VOUCHER_GROUPS_API}/${groupId}/${this.VOUCHERS}/bulk`, voucherCodesData);
    }

    putVoucher(id: number, code: string, voucher: PutVoucher): Observable<void> {
        return this._http.put<void>(`${this.VOUCHER_GROUPS_API}/${id}/${this.VOUCHERS}/${code}`, voucher);
    }

    putVoucherBalance(id: number, code: string, balance: PutVoucher): Observable<void> {
        return this._http.put<void>(`${this.VOUCHER_GROUPS_API}/${id}/${this.VOUCHERS}/${code}/balance`, balance);
    }

    postVoucherGroup(voucherGroup: PostVoucherGroup): Observable<{ id: number }> {
        return this._http.post<{ id: number }>(`${this.VOUCHER_GROUPS_API}`, voucherGroup);
    }

    putVoucherGroup(id: number, voucherGroup: PutVoucherGroup): Observable<void> {
        return this._http.put<void>(`${this.VOUCHER_GROUPS_API}/${id}`, voucherGroup);
    }

    deleteVoucherGroup(id: number): Observable<void> {
        return this._http.delete<void>(`${this.VOUCHER_GROUPS_API}/${id}`);
    }

    deleteGiftCardGroupConfig(id: number): Observable<void> {
        return this._http.delete<void>(`${this.VOUCHER_GROUPS_API}/${id}/gift-card-config`);
    }

    deleteVoucher(id: number, code: string): Observable<void> {
        return this._http.delete<void>(`${this.VOUCHER_GROUPS_API}/${id}/${this.VOUCHERS}/${code}`);
    }

    getVoucherGroupContents(id: number): Observable<CommunicationTextContent[]> {
        return this._http.get<CommunicationTextContent[]>(`${this.VOUCHER_GROUPS_API}/${id}/channel-contents/texts`);
    }

    getGiftCardTextContents(id: number): Observable<CommunicationTextContent[]> {
        return this._http.get<CommunicationTextContent[]>(`${this.VOUCHER_GROUPS_API}/${id}/gift-card-contents/texts`);
    }

    postVoucherGroupContents(id: number, contents: CommunicationTextContent[]): Observable<void> {
        return this._http.post<void>(`${this.VOUCHER_GROUPS_API}/${id}/channel-contents/texts`, contents);
    }

    postGiftCardTextContents(id: number, contents: CommunicationTextContent[]): Observable<void> {
        return this._http.post<void>(`${this.VOUCHER_GROUPS_API}/${id}/gift-card-contents/texts`, contents);
    }

    getGiftCardGroupContentImages(
        id: number,
        language: string,
        type: GiftCardGroupContentImageType
    ): Observable<GiftCardGroupContentImage[]> {
        const params = buildHttpParams({ language, type });
        return this._http.get<GiftCardGroupContentImage[]>(`${this.VOUCHER_GROUPS_API}/${id}/gift-card-contents/images`, { params });
    }

    postGiftCardGroupContentImages(id: number, contents: GiftCardGroupContentImageRequest[]): Observable<void> {
        return this._http.post<void>(`${this.VOUCHER_GROUPS_API}/${id}/gift-card-contents/images`, contents);
    }

    deleteGiftCardContentImage(
        id: number,
        language: string,
        type: GiftCardGroupContentImageType
    ): Observable<void> {
        return this._http.delete<void>(`${this.VOUCHER_GROUPS_API}/${id}/gift-card-contents/images/languages/${language}/types/${type}`);
    }

    exportVouchers(id: number, request: GetVoucherRequest, body: ExportRequest): Observable<ExportResponse> {
        const params = buildHttpParams(request);
        return this._http.post<ExportResponse>(`${this.VOUCHER_GROUPS_API}/${id}/${this.VOUCHERS}/exports`, body, {
            params
        });
    }

    resend(voucherGroupId: number, code: string, payload: ResendVoucherRequest): Observable<void> {
        return this._http.post<void>(`${this.VOUCHER_GROUPS_API}/${voucherGroupId}/${this.VOUCHERS}/${code}/send-email`, payload);
    }

}
