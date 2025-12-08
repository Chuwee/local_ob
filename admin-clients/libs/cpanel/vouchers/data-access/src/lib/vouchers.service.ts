import { mapMetadata, Metadata } from '@OneboxTM/utils-state';
import { CommunicationTextContent } from '@admin-clients/cpanel/shared/data-access';
import { ExportRequest, ExportResponse, AggregatedData } from '@admin-clients/shared/data-access/models';
import { Injectable } from '@angular/core';
import { Observable, of, Subject, zip } from 'rxjs';
import { catchError, finalize, map, switchMap, takeUntil } from 'rxjs/operators';
import { VouchersApi } from './api/vouchers.api';
import { aggDataVouchers } from './constants/vouchers-aggregated-data';
import { GetVoucherGroupsRequest } from './models/get-voucher-groups-request.model';
import { GetVoucherRequest } from './models/get-vouchers-request.model';
import { GiftCardGroupConfig } from './models/gift-card-group-config.model';
import { GiftCardGroupContentImageType } from './models/gift-card-group-content-image-type.enum';
import { GiftCardGroupContentImage } from './models/gift-card-group-content-image.model';
import { GiftCardGroupContentImageRequest } from './models/gift-card-group-content-image.request.model';
import { PostVoucherGroup } from './models/post-voucher-group.model';
import { PostVoucher } from './models/post-voucher.model';
import { PutGiftCardGroupConfig } from './models/put-gift-card-group-config.model';
import { PutVoucherGroup } from './models/put-voucher-group.model';
import { PutVoucher } from './models/put-voucher.model';
import { ResendVoucherRequest } from './models/resend-voucher-request.model';
import { VoucherGroupStatus } from './models/voucher-group-status.enum';
import { VoucherGroup } from './models/voucher-groups.model';
import { Voucher } from './models/voucher.model';
import { VouchersState } from './state/vouchers.state';

@Injectable({
    providedIn: 'root'
})
export class VouchersService {
    private _cancelVouchersList = new Subject<void>();

    constructor(
        private _vouchersApi: VouchersApi,
        private _vouchersState: VouchersState
    ) { }

    loadVoucherGroup(id: number): void {
        this._vouchersState.setVoucherGroupLoading(true);
        this._vouchersApi.getVoucherGroup(id)
            .pipe(
                catchError(() => of(null)),
                finalize(() => this._vouchersState.setVoucherGroupLoading(false))
            )
            .subscribe(voucherGroup =>
                this._vouchersState.setVoucherGroup(voucherGroup)
            );
    }

    loadVoucherGroupContents(id: number): void {
        this._vouchersState.setVoucherGroupContentsLoading(true);
        this._vouchersApi.getVoucherGroupContents(id)
            .pipe(
                catchError(() => of(null)),
                finalize(() => this._vouchersState.setVoucherGroupContentsLoading(false))
            )
            .subscribe(contents =>
                this._vouchersState.setVoucherGroupContents(contents)
            );
    }

    loadVoucherGroupsList(request: GetVoucherGroupsRequest): void {
        this._vouchersState.setVoucherGroupsListLoading(true);
        this._vouchersApi.getVoucherGroups(request)
            .pipe(
                mapMetadata(),
                catchError(() => of(null)),
                finalize(() => this._vouchersState.setVoucherGroupsListLoading(false))
            )
            .subscribe(voucherGroups =>
                this._vouchersState.setVoucherGroupsList(voucherGroups)
            );
    }

    loadGiftCardTextContents(id: number): void {
        this._vouchersState.setGiftCardTextContentsLoading(true);
        this._vouchersApi.getGiftCardTextContents(id)
            .pipe(
                catchError(() => of(null)),
                finalize(() => this._vouchersState.setGiftCardTextContentsLoading(false))
            )
            .subscribe(contents =>
                this._vouchersState.setGiftCardTextContents(contents)
            );
    }

    loadGiftCardImageContents(id: number, language?: string, type?: GiftCardGroupContentImageType): void {
        this._vouchersState.setGiftCardContentImagesLoading(true);
        this._vouchersApi.getGiftCardGroupContentImages(id, language, type)
            .pipe(
                catchError(() => of(null)),
                finalize(() => this._vouchersState.setGiftCardContentImagesLoading(false))
            )
            .subscribe(contents =>
                this._vouchersState.setGiftCardContentImages(contents)
            );
    }

    loadGiftCardGroupConfig(id: number): void {
        this._vouchersState.setGiftCardGroupConfigLoading(true);
        this._vouchersApi.getGiftCardGroupConfig(id)
            .pipe(
                catchError(() => of(null)),
                finalize(() => this._vouchersState.setGiftCardGroupConfigLoading(false))
            )
            .subscribe(config =>
                this._vouchersState.setGiftCardGroupConfig(config)
            );
    }

    cancelVouchersList(): void {
        this._cancelVouchersList.next();
    }

    loadVouchers(voucherGroupIds: number, request: GetVoucherRequest): void {
        this._vouchersState.setVouchersLoading(true);
        this._vouchersApi.getVouchers(voucherGroupIds, request)
            .pipe(
                mapMetadata(),
                catchError(() => of(null)),
                takeUntil(this._cancelVouchersList),
                finalize(() => this._vouchersState.setVouchersLoading(false))
            )
            .subscribe(vouchers =>
                this._vouchersState.setVouchers(vouchers)
            );
    }

    loadVoucher(voucherGroupId: number, code: string): void {
        this._vouchersState.setVoucherLoading(true);
        this._vouchersApi.getVoucher(voucherGroupId, code)
            .pipe(
                catchError(() => of(null)),
                finalize(() => this._vouchersState.setVoucherLoading(false))
            )
            .subscribe(voucher =>
                this._vouchersState.setVoucher(voucher)
            );
    }

    getVoucherGroupsListData$(): Observable<VoucherGroup[]> {
        return this._vouchersState.getVoucherGroupsList$()
            .pipe(map(voucherGroups => voucherGroups?.data));
    }

    getVoucherGroupsListMetadata$(): Observable<Metadata> {
        return this._vouchersState.getVoucherGroupsList$().pipe(map(r => r?.metadata));
    }

    clearVoucherGroupsList(): void {
        this._vouchersState.setVoucherGroupsList(null);
    }

    getVoucherGroup$(): Observable<VoucherGroup> {
        return this._vouchersState.getVoucherGroup$();
    }

    getGiftCardGroupConfig$(): Observable<GiftCardGroupConfig> {
        return this._vouchersState.getGiftCardGroupConfig$();
    }

    getGiftCardTextContents$(): Observable<CommunicationTextContent[]> {
        return this._vouchersState.getGiftCardTextContents$();
    }

    getGiftCardImageContents$(): Observable<GiftCardGroupContentImage[]> {
        return this._vouchersState.getGiftCardContentImages$();
    }

    getVoucherListData$(): Observable<Voucher[]> {
        return this._vouchersState.getVouchers$()
            .pipe(map(vouchers => vouchers?.data));
    }

    getVoucherListMetadata$(): Observable<Metadata> {
        return this._vouchersState.getVouchers$()
            .pipe(map(vouchers => vouchers?.metadata));
    }

    getVoucherListAggregatedData$(): Observable<AggregatedData> {
        return this._vouchersState.getVouchers$()
            .pipe(map(vouchers => vouchers?.aggregated_data && new AggregatedData(vouchers.aggregated_data, aggDataVouchers)));
    }

    getVoucher$(): Observable<Voucher> {
        return this._vouchersState.getVoucher$();
    }

    getVoucherGroupContents$(): Observable<CommunicationTextContent[]> {
        return this._vouchersState.getVoucherGroupContents$();
    }

    isVoucherGroupsListLoading$(): Observable<boolean> {
        return this._vouchersState.isVoucherGroupsListLoading$();
    }

    isVoucherGroupSaving$(): Observable<boolean> {
        return this._vouchersState.isVoucherGroupSaving$();
    }

    isVoucherGroupLoading$(): Observable<boolean> {
        return this._vouchersState.isVoucherGroupLoading$();
    }

    isGiftCardGroupConfigLoading$(): Observable<boolean> {
        return this._vouchersState.isGiftCardGroupConfigLoading$();
    }

    isGiftCardTextContentsLoading$(): Observable<boolean> {
        return this._vouchersState.isGiftCardTextContentsLoading$();
    }

    isGiftCardImageContentsLoading$(): Observable<boolean> {
        return this._vouchersState.isGiftCardContentImagesLoading$();
    }

    isGiftCardGroupConfigSaving$(): Observable<boolean> {
        return this._vouchersState.isGiftCardGroupConfigSaving$();
    }

    isVoucherLoading$(): Observable<boolean> {
        return this._vouchersState.isVoucherLoading$();
    }

    isVoucherGroupContentsSaving$(): Observable<boolean> {
        return this._vouchersState.isVoucherGroupContentsSaving$();
    }

    isGiftCardTextContentsSaving$(): Observable<boolean> {
        return this._vouchersState.isGiftCardTextContentsSaving$();
    }

    isGiftCardImageContentsSaving$(): Observable<boolean> {
        return this._vouchersState.isGiftCardContentImagesSaving$();
    }

    isGiftCardImageContentsRemoving$(): Observable<boolean> {
        return this._vouchersState.isGiftCardContentImagesRemoving$();
    }

    isVoucherGroupChannelsSaving$(): Observable<boolean> {
        return this._vouchersState.isVoucherGroupChannelsSaving$();
    }

    deleteVoucherGroup(id: number): Observable<void> {
        return this._vouchersApi.deleteVoucherGroup(id);
    }

    deleteGiftCardGroupConfig(id: number): Observable<void> {
        return this._vouchersApi.deleteGiftCardGroupConfig(id);
    }

    deleteGiftCardContentImages(
        id: number,
        imagesToDelete: GiftCardGroupContentImageRequest[]): Observable<void> {
        this._vouchersState.setGiftCardContentImagesRemoving(true);
        return zip(...imagesToDelete
            .map(image => this._vouchersApi
                .deleteGiftCardContentImage(id, image.language, image.type)))
            .pipe(
                switchMap(() => of(null)),
                catchError(() => of(null)),
                finalize(() => this._vouchersState.setGiftCardContentImagesRemoving(false))
            );

    }

    deleteVoucher(id: number, code: string): Observable<void> {
        return this._vouchersApi.deleteVoucher(id, code);
    }

    createVoucherGroup(voucherGroup: PostVoucherGroup): Observable<number> {
        this._vouchersState.setVoucherGroupSaving(true);
        this._vouchersState.setVoucherGroupSavingError(null);
        return this._vouchersApi.postVoucherGroup(voucherGroup)
            .pipe(
                catchError(error => {
                    this._vouchersState.setVoucherGroupSavingError(error);
                    throw error;
                }),
                map(result => result.id),
                finalize(() => this._vouchersState.setVoucherGroupSaving(false))
            );
    }

    createVoucher(id: number, voucher: PostVoucher): Observable<string> {
        this._vouchersState.setVoucherSaving(true);
        this._vouchersState.setVoucherSavingError(null);
        return this._vouchersApi.postVoucher(id, voucher)
            .pipe(
                catchError(error => {
                    this._vouchersState.setVoucherSavingError(error);
                    throw error;
                }),
                map(result => result.code),
                finalize(() => this._vouchersState.setVoucherSaving(false))
            );
    }

    createVouchers(groupId: number, voucherCodesData: PostVoucher[]): Observable<string[]> {
        this._vouchersState.setVouchersSaving(true);
        return this._vouchersApi.postVouchers(groupId, voucherCodesData)
            .pipe(
                finalize(() => this._vouchersState.setVouchersSaving(false))
            );
    }

    saveVoucherGroup(id: number, voucherGroup: PutVoucherGroup): Observable<void> {
        this._vouchersState.setVoucherGroupSaving(true);
        this._vouchersState.setVoucherGroupSavingError(null);
        return this._vouchersApi.putVoucherGroup(id, voucherGroup)
            .pipe(
                catchError(error => {
                    this._vouchersState.setVoucherGroupSavingError(error);
                    throw error;
                }),
                finalize(() => this._vouchersState.setVoucherGroupSaving(false))
            );
    }

    saveGiftCardGroupConfig(id: number, config: PutGiftCardGroupConfig): Observable<void> {
        this._vouchersState.setGiftCardGroupConfigSaving(true);
        this._vouchersState.setGiftCardGroupConfigSavingError(null);
        return this._vouchersApi.putGiftCardGroupConfig(id, config)
            .pipe(
                catchError(error => {
                    this._vouchersState.setGiftCardGroupConfigSavingError(error);
                    throw error;
                }),
                finalize(() => this._vouchersState.setGiftCardGroupConfigSaving(false))
            );
    }

    saveGiftCardGroupContentImages(id: number, contents: GiftCardGroupContentImageRequest[]): Observable<void> {
        this._vouchersState.setGiftCardContentImagesSaving(true);
        this._vouchersState.setGiftCardContentImagesSavingError(null);
        return this._vouchersApi.postGiftCardGroupContentImages(id, contents)
            .pipe(
                catchError(error => {
                    this._vouchersState.setGiftCardContentImagesSavingError(error);
                    throw error;
                }),
                finalize(() => this._vouchersState.setGiftCardContentImagesSaving(false))
            );
    }

    updateVoucherGroupStatus(id: number, status: VoucherGroupStatus): Observable<void> {
        const updatedVoucher: PutVoucherGroup = { status };
        return this._vouchersApi.putVoucherGroup(id, updatedVoucher);
    }

    saveVoucherGroupChannels(voucherGroupId: number, voucherGroupChannels: PutVoucherGroup): Observable<void> {
        this._vouchersState.setVoucherGroupChannelsSaving(true);
        this._vouchersState.setVoucherGroupChannelsSavingError(null);
        return this._vouchersApi.putVoucherGroup(voucherGroupId, voucherGroupChannels)
            .pipe(
                catchError(error => {
                    this._vouchersState.setVoucherGroupChannelsSavingError(error);
                    throw error;
                }),
                finalize(() => this._vouchersState.setVoucherGroupChannelsSaving(false))
            );
    }

    saveVoucher(id: number, code: string, voucher: PutVoucher): Observable<void> {
        this._vouchersState.setVoucherSaving(true);
        this._vouchersState.setVoucherSavingError(null);
        return this._vouchersApi.putVoucher(id, code, voucher)
            .pipe(
                catchError(error => {
                    this._vouchersState.setVoucherSavingError(error);
                    throw error;
                }),
                finalize(() => this._vouchersState.setVoucherSaving(false))
            );
    }

    saveVoucherBalance(id: number, code: string, balance: PutVoucher): Observable<void> {
        this._vouchersState.setVoucherBalanceSaving(true);
        this._vouchersState.setVoucherBalanceSavingError(null);
        return this._vouchersApi.putVoucherBalance(id, code, balance)
            .pipe(
                catchError(error => {
                    this._vouchersState.setVoucherBalanceSavingError(error);
                    throw error;
                }),
                finalize(() => this._vouchersState.setVoucherBalanceSaving(false))
            );
    }

    saveVoucherGroupContents(id: number, contents: CommunicationTextContent[]): Observable<void> {
        this._vouchersState.setVoucherGroupContentsSaving(true);
        this._vouchersState.setVoucherGroupContentsSavingError(null);
        return this._vouchersApi.postVoucherGroupContents(id, contents)
            .pipe(
                catchError(error => {
                    this._vouchersState.setVoucherGroupContentsSavingError(error);
                    throw error;
                }),
                finalize(() => this._vouchersState.setVoucherGroupContentsSaving(false))
            );
    }

    clearVoucherGroup(): void {
        this._vouchersState.setVoucherGroup(null);
    }

    clearVoucher(): void {
        this._vouchersState.setVoucher(null);
    }

    clearVouchers(): void {
        this._vouchersState.setVouchers(null);
    }

    isVouchersLoading$(): Observable<boolean> {
        return this._vouchersState.isVouchersLoading$();
    }

    isVoucherGroupContentsLoading$(): Observable<boolean> {
        return this._vouchersState.isVoucherGroupContentsLoading$();
    }

    clearVoucherGroupContents(): void {
        this._vouchersState.setVoucherGroupContents(null);
    }

    clearGiftCardTextContents(): void {
        this._vouchersState.setGiftCardTextContents(null);
    }

    clearGiftCardImageContents(): void {
        this._vouchersState.setGiftCardContentImages(null);
    }

    clearGiftCardGroupConfig(): void {
        this._vouchersState.setGiftCardGroupConfig(null);
    }

    saveGiftCardTextContents(id: number, contents: CommunicationTextContent[]): Observable<void> {
        this._vouchersState.setGiftCardTextContentsSaving(true);
        this._vouchersState.setGiftCardTextContentsSavingError(null);
        return this._vouchersApi.postGiftCardTextContents(id, contents)
            .pipe(
                catchError(error => {
                    this._vouchersState.setGiftCardTextContentsSavingError(error);
                    throw error;
                }),
                finalize(() => this._vouchersState.setGiftCardTextContentsSaving(false))
            );
    }

    setVouchersSaving(saving: boolean): void {
        this._vouchersState.setVouchersSaving(saving);
    }

    isVouchersSaving$(): Observable<boolean> {
        return this._vouchersState.isVouchersSaving$();
    }

    exportVouchers(id: number, request: GetVoucherRequest, data: ExportRequest): Observable<ExportResponse> {
        this._vouchersState.setExportVouchersLoading(true);
        return this._vouchersApi.exportVouchers(id, request, data)
            .pipe(finalize(() => this._vouchersState.setExportVouchersLoading(false)));
    }

    resend(voucherGroupId: number, code: string, payload: ResendVoucherRequest): Observable<any> {
        this._vouchersState.setResendVoucherLoading(true);
        return this._vouchersApi.resend(voucherGroupId, code, payload)
            .pipe(
                catchError(error => error),
                finalize(() => this._vouchersState.setResendVoucherLoading(false))
            );
    }
}
