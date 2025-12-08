import { Metadata } from '@OneboxTM/utils-state';
import { PromotionChannels, PutPromotionChannels } from '@admin-clients/cpanel/promoters/data-access';
import { CommunicationTextContent } from '@admin-clients/cpanel/shared/data-access';
import { HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of, Subject } from 'rxjs';
import { catchError, filter, finalize, map, switchMap, takeUntil } from 'rxjs/operators';
import { PromotionTemplatesApi } from './api/promotion-tpls.api';
import { GetPromotionTplsRequest } from './models/get-promotion-tpls-request.model';
import { PromotionTplListElement } from './models/get-promotion-tpls-response.model';
import { PostPromotionTpl } from './models/post-promotion-tpl.model';
import { PromotionTpl } from './models/promotion-tpl.model';
import { PromotionTplsState } from './state/promotion-tpls.state';

@Injectable({
    providedIn: 'root'
})
export class PromotionTplsService {
    private _cancelPromotionRequest = new Subject<void>();
    private _cancelPromotionListRequest = new Subject<void>();

    constructor(
        private _promotionTplsApi: PromotionTemplatesApi,
        private _promotionTplsState: PromotionTplsState
    ) { }

    cancelRequests(): void {
        this._cancelPromotionListRequest.next();
        this._cancelPromotionRequest.next();
    }

    // LIST

    loadPromotionTemplates(request: GetPromotionTplsRequest): void {
        this._promotionTplsState.setPromotionTplsLoading(true);
        this._promotionTplsApi.getPromotionTemplates(request)
            .pipe(
                catchError(() => of(null)),
                finalize(() => this._promotionTplsState.setPromotionTplsLoading(false)),
                takeUntil(this._cancelPromotionListRequest)
            )
            .subscribe(promotionTemplates =>
                this._promotionTplsState.setPromotionTpls(promotionTemplates)
            );
    }

    getPromotionTemplatesData$(): Observable<PromotionTplListElement[]> {
        return this._promotionTplsState.getPromotionTpls$()
            .pipe(
                filter(value => !!value),
                map(promotionTemplates => promotionTemplates?.data)
            );
    }

    getPromotionTemplatesMetadata$(): Observable<Metadata> {
        return this._promotionTplsState.getPromotionTpls$()
            .pipe(
                map(promotionTemplates =>
                    promotionTemplates?.metadata && Object.assign(new Metadata(), promotionTemplates.metadata)));
    }

    isPromotionTemplatesLoading$(): Observable<boolean> {
        return this._promotionTplsState.isPromotionTplsLoading$();
    }

    // DETAILS

    loadPromotionTemplate(id: number): void {
        this._promotionTplsState.setPromotionTplError(null);
        this._promotionTplsState.setPromotionTplLoading(true);
        this._promotionTplsApi.getPromotionTemplate(id)
            .pipe(
                catchError(error => {
                    this._promotionTplsState.setPromotionTplError(error);
                    return of(null);
                }),
                finalize(() => this._promotionTplsState.setPromotionTplLoading(false)),
                takeUntil(this._cancelPromotionRequest)
            )
            .subscribe(promotionTemplate =>
                this._promotionTplsState.setPromotionTpl(promotionTemplate)
            );
    }

    clearPromotionTemplate(): void {
        this._promotionTplsState.setPromotionTpl(null);
    }

    getPromotionTemplate$(): Observable<PromotionTpl> {
        return this._promotionTplsState.getPromotionTpl$();
    }

    getPromotionTemplateError$(): Observable<HttpErrorResponse> {
        return this._promotionTplsState.getPromotionTplError$();
    }

    isPromotionTemplateLoading$(): Observable<boolean> {
        return this._promotionTplsState.isPromotionTplLoading$();
    }

    createPromotionTemplate(promotionTemplate: PostPromotionTpl): Observable<number> {
        this._promotionTplsState.setPromotionTplSaving(true);
        return this._promotionTplsApi.postPromotionTemplate(promotionTemplate)
            .pipe(
                map(result => result.id),
                finalize(() => this._promotionTplsState.setPromotionTplSaving(false))
            );
    }

    deletePromotionTemplate(id: number): Observable<void> {
        return this._promotionTplsApi.deletePromotionTemplate(id);
    }

    savePromotionTemplate(promotionTplId: number, promotionTpl: PromotionTpl): Observable<void> {
        this._promotionTplsState.setPromotionTplSaving(true);
        return this._promotionTplsApi.putPromotionTemplate(promotionTplId, promotionTpl)
            .pipe(finalize(() => this._promotionTplsState.setPromotionTplSaving(false)));
    }

    updatePromotionTemplateFavorite(promotionTplId: number, isFavorite: boolean): Observable<boolean> {
        return this._promotionTplsApi.putPromotionTemplate(promotionTplId, { favorite: isFavorite })
            .pipe(switchMap(() => of(true)), catchError(() => of(false)));
    }

    isPromotionTemplateSaving$(): Observable<boolean> {
        return this._promotionTplsState.isPromotionTplSaving$();
    }

    // PROMOTION TEMPLATE CHANNEL TEXT CONTENTS

    loadPromotionTplChannelContents(promotionId: number): void {
        this._promotionTplsState.setPromotionTplChannelContentsLoading(true);
        this._promotionTplsState.setPromotionTplChannelContentsError(null);
        this._promotionTplsApi.getPromotionTplChannelContents(promotionId)
            .pipe(
                catchError(error => {
                    this._promotionTplsState.setPromotionTplChannelContentsError(error);
                    return of(null);
                }),
                finalize(() => this._promotionTplsState.setPromotionTplChannelContentsLoading(false)),
                takeUntil(this._cancelPromotionRequest)
            )
            .subscribe(promotion =>
                this._promotionTplsState.setPromotionTplChannelContents(promotion)
            );
    }

    isPromotionTplChannelContentsLoading$(): Observable<boolean> {
        return this._promotionTplsState.isPromotionTplChannelContentsLoading$();
    }

    getPromotionTplChannelContents$(): Observable<CommunicationTextContent[]> {
        return this._promotionTplsState.getPromotionTplChannelContents$();
    }

    getPromotionTplChannelContentsError$(): Observable<HttpErrorResponse> {
        return this._promotionTplsState.getPromotionTplChannelContentsError$();
    }

    clearPromotionTplChannelContents(): void {
        this._promotionTplsState.setPromotionTplChannelContents(null);
    }

    savePromotionTplChannelContents(promotionId: number, contents: CommunicationTextContent[]): Observable<void> {
        this._promotionTplsState.setPromotionTplChannelContentsSaving(true);
        this._promotionTplsState.setPromotionTplChannelContentsError(null);
        return this._promotionTplsApi.postPromotionTplChannelContents(promotionId, contents)
            .pipe(
                catchError(error => {
                    this._promotionTplsState.setPromotionTplChannelContentsError(error);
                    return of(null);
                }),
                finalize(() => this._promotionTplsState.setPromotionTplChannelContentsSaving(false))
            );
    }

    isPromotionTplChannelContentsSaving$(): Observable<boolean> {
        return this._promotionTplsState.isPromotionTplChannelContentsSaving$();
    }

    // PRMOTION TEMPLATE CHANNELS

    loadPromotionTplChannels(promotionId: number): void {
        this._promotionTplsState.setPromotionTplChannelsLoading(true);
        this._promotionTplsState.setPromotionTplChannelsError(null);
        this._promotionTplsApi.getPromotionTplChannels(promotionId)
            .pipe(
                catchError(error => {
                    this._promotionTplsState.setPromotionTplChannelsError(error);
                    return of(null);
                }),
                finalize(() => this._promotionTplsState.setPromotionTplChannelsLoading(false)),
                takeUntil(this._cancelPromotionRequest)
            )
            .subscribe(operative =>
                this._promotionTplsState.setPromotionTplChannels(operative)
            );
    }

    isPromotionTplChannelsLoading$(): Observable<boolean> {
        return this._promotionTplsState.getPromotionTplChannelsLoading$();
    }

    getPromotionTplChannels$(): Observable<PromotionChannels> {
        return this._promotionTplsState.getPromotionTplChannels$();
    }

    getPromotionTplChannelsError$(): Observable<HttpErrorResponse> {
        return this._promotionTplsState.getPromotionTplChannelsError$();
    }

    clearPromotionTplChannels(): void {
        this._promotionTplsState.setPromotionTplChannels(null);
    }

    savePromotionTplChannels(promotionId: number, req: PutPromotionChannels): Observable<void> {
        this._promotionTplsState.setPromotionTplChannelsSaving(true);
        return this._promotionTplsApi.putPromotionTplChannels(promotionId, req)
            .pipe(
                finalize(() => this._promotionTplsState.setPromotionTplChannelsSaving(false))
            );
    }

    isPromotionTplChannelsSaving$(): Observable<boolean> {
        return this._promotionTplsState.isPromotionTplChannelsSaving$();
    }

}
