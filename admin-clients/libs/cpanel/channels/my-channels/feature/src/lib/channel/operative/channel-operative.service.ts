import { getListData, getMetadata, mapMetadata, Metadata, StateManager } from '@OneboxTM/utils-state';
import {
    ChannelSurcharge, ChannelCommission,
    ChannelsExtendedApi, ChannelsExtendedState, ChannelFormsDataType,
    ChannelCrossSaleRestriction, ChannelSharingSettings,
    PutChannelDeliverySettings, ChannelDeliverySettings, ChannelForms,
    AdditionalCondition, ChannelBlacklistType, GetChannelBlacklistRequest,
    ChannelBlacklistItem, ChannelBlacklistStatus, ChannelBookingSettings,
    ChannelAuthVendorsSso, ChannelAuthVendorsUserData, PutChannelSurchargeTaxes
} from '@admin-clients/cpanel/channels/data-access';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { forkJoin, Observable, of } from 'rxjs';
import { catchError, finalize, mapTo } from 'rxjs/operators';

@Injectable({
    providedIn: 'root'
})
export class ChannelOperativeService {

    // Event Sales Restrictions - Cross Event Restrictions
    readonly crossSaleRestrictions = Object.freeze({
        load: (channelId: number) =>
            StateManager.load(
                this._channelsState.crossSaleRestrictions,
                this._channelsApi.getCrossSaleRestrictions(channelId)
            ),
        update: (channelId: number, data: ChannelCrossSaleRestriction[]) =>
            StateManager.inProgress(
                this._channelsState.crossSaleRestrictions,
                this._channelsApi.putCrossSaleRestrictions(channelId, data)
            ),
        clear: () => this._channelsState.crossSaleRestrictions.setValue(null),
        loading$: () => this._channelsState.crossSaleRestrictions.isInProgress$(),
        get$: () => this._channelsState.crossSaleRestrictions.getValue$()
    });

    // Operative - Sharing
    readonly sharingSettings = Object.freeze({
        load: (channelId: number) =>
            StateManager.load(
                this._channelsState.sharingSettings,
                this._channelsApi.getSharingSettings(channelId)
            ),
        update: (channelId: number, data: ChannelSharingSettings) =>
            StateManager.inProgress(
                this._channelsState.sharingSettings,
                this._channelsApi.putSharingSettings(channelId, data)
            ),
        clear: () => this._channelsState.sharingSettings.setValue(null),
        loading$: () => this._channelsState.sharingSettings.isInProgress$(),
        get$: () => this._channelsState.sharingSettings.getValue$()
    });

    readonly surchargeTaxes = Object.freeze({
        load: (channelId: number) =>
            StateManager.load(
                this._channelsState.channelSurchargeTaxes,
                this._channelsApi.getChannelSurchargeTaxes(channelId)
            ),
        update: (channelId: number, data: PutChannelSurchargeTaxes) =>
            StateManager.inProgress(
                this._channelsState.channelSurchargeTaxes,
                this._channelsApi.putChannelSurchargeTaxes(channelId, data)
            ),
        clear: () => this._channelsState.channelSurchargeTaxes.setValue(null),
        loading$: () => this._channelsState.channelSurchargeTaxes.isInProgress$(),
        get$: () => this._channelsState.channelSurchargeTaxes.getValue$()
    });

    constructor(private _channelsApi: ChannelsExtendedApi, private _channelsState: ChannelsExtendedState) { }

    loadChannelSurcharges(channelId: string): void {
        this._channelsState.setChannelSurchargesError(null);
        this._channelsState.setChannelSurchargesLoading(true);
        this._channelsApi.getChannelSurcharges(channelId)
            .pipe(
                catchError(error => {
                    this._channelsState.setChannelSurchargesError(error);
                    return of(null);
                }),
                finalize(() => this._channelsState.setChannelSurchargesLoading(false))
            )
            .subscribe(surcharges =>
                this._channelsState.setChannelSurcharges(surcharges)
            );
    }

    getChannelSurcharges$(): Observable<ChannelSurcharge[]> {
        return this._channelsState.getChannelSurcharges$();
    }

    getChannelSurchargesError$(): Observable<HttpErrorResponse> {
        return this._channelsState.getChannelSurchargesError$();
    }

    isChannelSurchargesLoading$(): Observable<boolean> {
        return this._channelsState.isChannelSurchargesLoading$();
    }

    isChannelSurchargesSaving$(): Observable<boolean> {
        return this._channelsState.isChannelSurchargesSaving$();
    }

    clearChannelSurcharges(): void {
        this._channelsState.setChannelSurcharges(null);
    }

    saveChannelSurcharges(channelId: string, surcharges: ChannelSurcharge[]): Observable<void> {
        this._channelsState.setChannelSurchargesError(null);
        this._channelsState.setChannelSurchargesSaving(true);
        return this._channelsApi.postChannelSurcharges(channelId, surcharges)
            .pipe(
                catchError(error => {
                    this._channelsState.setChannelSurchargesError(error);
                    throw error;
                }),
                finalize(() => this._channelsState.setChannelSurchargesSaving(false))
            );
    }

    loadChannelCommissions(channelId: string): void {
        this._channelsState.setChannelCommissionsError(null);
        this._channelsState.setChannelCommissionsLoading(true);
        this._channelsApi.getChannelCommissions(channelId)
            .pipe(
                catchError(error => {
                    this._channelsState.setChannelCommissionsError(error);
                    return of(null);
                }),
                finalize(() => this._channelsState.setChannelCommissionsLoading(false))
            )
            .subscribe(commissions =>
                this._channelsState.setChannelCommissions(commissions)
            );
    }

    getChannelCommissions$(): Observable<ChannelCommission[]> {
        return this._channelsState.getChannelCommissions$();
    }

    clearChannelCommissions(): void {
        this._channelsState.setChannelCommissions(null);
    }

    isChannelCommissionsSaving$(): Observable<boolean> {
        return this._channelsState.isChannelCommissionsSaving$();
    }

    isChannelCommissionsLoading$(): Observable<boolean> {
        return this._channelsState.isChannelCommissionsLoading$();
    }

    saveChannelCommissions(channelId: string, commissions: ChannelCommission[]): Observable<void> {
        this._channelsState.setChannelCommissionsError(null);
        this._channelsState.setChannelCommissionsSaving(true);
        return this._channelsApi.postChannelCommissions(channelId, commissions)
            .pipe(
                catchError(error => {
                    this._channelsState.setChannelCommissionsError(error);
                    throw error;
                }),
                finalize(() => this._channelsState.setChannelCommissionsSaving(false))
            );
    }

    loadChannelDeliveryMethods(channelId: string): void {
        this._channelsState.setChannelDeliveryMethodsError(null);
        this._channelsState.setChannelDeliveryMethodsLoading(true);
        this._channelsApi.getChannelDeliveryMethods(channelId)
            .pipe(
                catchError(error => {
                    this._channelsState.setChannelDeliveryMethodsError(error);
                    return of(null);
                }),
                finalize(() => this._channelsState.setChannelDeliveryMethodsLoading(false))
            )
            .subscribe(deliveryMethods =>
                this._channelsState.setChannelDeliveryMethods(deliveryMethods)
            );
    }

    updateChannelDeliveryMethods(
        { channelId, deliveryMethods }: { channelId: string; deliveryMethods: PutChannelDeliverySettings }
    ): Observable<void> {
        this._channelsState.setChannelDeliveryMethodsSaving(true);
        this._channelsState.setChannelDeliveryMethodsError(null);
        return this._channelsApi.putChannelDeliveryMethods(channelId, deliveryMethods)
            .pipe(
                catchError(error => {
                    this._channelsState.setChannelDeliveryMethodsError(error);
                    throw error;
                }),
                finalize(() => this._channelsState.setChannelDeliveryMethodsSaving(false))
            );
    }

    isChannelDeliveryMethodsInProgress$(): Observable<boolean> {
        return booleanOrMerge([
            this._channelsState.isChannelDeliveryMethodsLoading$(),
            this._channelsState.isChannelDeliveryMethodsSaving$()
        ]);
    }

    clearChannelDeliveryMethods(): void {
        this._channelsState.setChannelDeliveryMethods(null);
    }

    getChannelDeliveryMethodsError$(): Observable<HttpErrorResponse> {
        return this._channelsState.getChannelDeliveryMethodsError$();
    }

    getChannelDeliveryMethods$(): Observable<ChannelDeliverySettings> {
        return this._channelsState.getChannelDeliveryMethods$();
    }

    loadChannelForms(channelId: number): void {
        this._channelsState.setChannelFormsError(null);
        this._channelsState.setChannelFormsLoading(true);
        this._channelsApi.getChannelForms(channelId, ChannelFormsDataType.buyerDataForms)
            .pipe(
                catchError(error => {
                    this._channelsState.setChannelFormsError(error);
                    return of(null);
                }),
                finalize(() => this._channelsState.setChannelFormsLoading(false))
            )
            .subscribe(channelForms =>
                this._channelsState.setChannelForms(channelForms)
            );
    }

    loadBuyerDataProtectionDataForms(channelId: number): void {
        this._channelsState.setBuyerDataProtectionDataFormsError(null);
        this._channelsState.setBuyerDataProtectionDataFormsLoading(true);
        this._channelsApi.getChannelForms(channelId, ChannelFormsDataType.dataProtectionForms)
            .pipe(
                catchError(error => {
                    this._channelsState.setBuyerDataProtectionDataFormsError(error);
                    return of(null);
                }),
                finalize(() => this._channelsState.setBuyerDataProtectionDataFormsLoading(false))
            )
            .subscribe(buyerDataProtectionDataForms =>
                this._channelsState.setBuyerDataProtectionDataForms(buyerDataProtectionDataForms)
            );
    }

    updateChannelForms(channelId: number, channelForms: ChannelForms): Observable<void> {
        this._channelsState.setChannelFormsSaving(true);
        this._channelsState.setChannelFormsError(null);
        return this._channelsApi.putChannelForms(channelId, channelForms, ChannelFormsDataType.buyerDataForms)
            .pipe(
                catchError(error => {
                    this._channelsState.setChannelFormsError(error);
                    throw error;
                }),
                finalize(() => this._channelsState.setChannelFormsSaving(false))
            );
    }

    updateBuyerDataProtectionDataForms(channelId: number, channelForms: ChannelForms): Observable<void> {
        this._channelsState.setBuyerDataProtectionDataFormsSaving(true);
        this._channelsState.setBuyerDataProtectionDataFormsError(null);
        return this._channelsApi.putChannelForms(channelId, channelForms, ChannelFormsDataType.dataProtectionForms)
            .pipe(
                catchError(error => {
                    this._channelsState.setBuyerDataProtectionDataFormsError(error);
                    throw error;
                }),
                finalize(() => this._channelsState.setBuyerDataProtectionDataFormsSaving(false))
            );
    }

    isChannelFormsInProgress$(): Observable<boolean> {
        return booleanOrMerge([
            this._channelsState.isChannelFormsLoading$(),
            this._channelsState.isChannelFormsSaving$()
        ]);
    }

    isBuyerDataProtectionDataFormsInProgress$(): Observable<boolean> {
        return booleanOrMerge([
            this._channelsState.isBuyerDataProtectionDataFormsLoading$(),
            this._channelsState.isBuyerDataProtectionDataFormsSaving$()
        ]);
    }

    clearChannelForms$(): void {
        this._channelsState.setChannelForms(null);
    }

    getChannelFormsError$(): Observable<HttpErrorResponse> {
        return this._channelsState.getChannelFormsError$();
    }

    clearBuyerDataProtectionDataForms$(): void {
        this._channelsState.setBuyerDataProtectionDataForms(null);
    }

    getBuyerDataProtectionDataFormsError$(): Observable<HttpErrorResponse> {
        return this._channelsState.getBuyerDataProtectionDataFormsError$();
    }

    getChannelForms$(): Observable<ChannelForms> {
        return this._channelsState.getChannelForms$();
    }

    getBuyerDataProtectionDataForms$(): Observable<ChannelForms> {
        return this._channelsState.getBuyerDataProtectionDataForms$();
    }

    loadAdditionalConditions(channelId: number): void {
        this._channelsState.setAdditionalConditionsLoading(true);
        this._channelsApi.getAdditionalConditions(channelId)
            .pipe(
                finalize(() => this._channelsState.setAdditionalConditionsLoading(false))
            )
            .subscribe(channelForms =>
                this._channelsState.setAdditionalConditions(channelForms)
            );
    }

    getAdditionalConditions$(): Observable<AdditionalCondition[]> {
        return this._channelsState.getAdditionalConditions$();
    }

    clearAdditionalConditions$(): void {
        this._channelsState.setAdditionalConditions(null);
    }

    updateAdditionalConditions(channelId: number, additionalConditions: AdditionalCondition[]): Observable<void> {
        this._channelsState.setAdditionalConditionSaving(true);
        this._channelsState.setAdditionalConditionError(null);
        return forkJoin(additionalConditions.map(condition =>
            this._channelsApi.putAdditionalCondition(channelId, condition.id, {
                ...condition,
                id: undefined
            })
        ))
            .pipe(
                mapTo(undefined),
                catchError(error => {
                    this._channelsState.setAdditionalConditionError(error);
                    throw error;
                }),
                finalize(() => this._channelsState.setAdditionalConditionSaving(false))
            );
    }

    createAdditionalCondition(channelId: number, additionalCondition: AdditionalCondition): Observable<{ id: number }> {
        this._channelsState.setAdditionalConditionSaving(true);
        this._channelsState.setAdditionalConditionError(null);
        return this._channelsApi.postAdditionalCondition(channelId, additionalCondition)
            .pipe(
                catchError(error => {
                    this._channelsState.setAdditionalConditionError(error);
                    throw error;
                }),
                finalize(() => this._channelsState.setAdditionalConditionSaving(false))
            );
    }

    updateAdditionalCondition(channelId: number, adCondId: number, additionalCondition: AdditionalCondition): Observable<void> {
        this._channelsState.setAdditionalConditionSaving(true);
        this._channelsState.setAdditionalConditionError(null);
        return this._channelsApi.putAdditionalCondition(channelId, adCondId, additionalCondition)
            .pipe(
                catchError(error => {
                    this._channelsState.setAdditionalConditionError(error);
                    throw error;
                }),
                finalize(() => this._channelsState.setAdditionalConditionSaving(false))
            );
    }

    isAdditionalConditionSaving$(): Observable<boolean> {
        return this._channelsState.isAdditionalConditionSaving$();
    }

    getAdditionalConditionError$(): Observable<HttpErrorResponse> {
        return this._channelsState.getAdditionalConditionError$();
    }

    isAdditionalConditionsInProgress$(): Observable<boolean> {
        return booleanOrMerge([
            this._channelsState.isAdditionalConditionsLoading$(),
            this._channelsState.isAdditionalConditionSaving$()
        ]);
    }

    deleteAdditionalCondition(channelId: number, conditionId: number): Observable<void> {
        this._channelsState.setAdditionalConditionSaving(true);
        this._channelsState.setAdditionalConditionError(null);
        return this._channelsApi.deleteAdditionalCondition(channelId, conditionId)
            .pipe(
                catchError(error => {
                    this._channelsState.setAdditionalConditionError(error);
                    throw error;
                }),
                finalize(() => this._channelsState.setAdditionalConditionSaving(false))
            );
    }

    loadChannelBlacklist(channelId: number, type: ChannelBlacklistType, filter?: GetChannelBlacklistRequest): void {
        this._channelsState.setChannelBlacklistLoading(type)(true);
        this._channelsApi.getChannelBlacklist(channelId, type, filter)
            .pipe(
                mapMetadata(),
                catchError(_ => of(null)),
                finalize(() => this._channelsState.setChannelBlacklistLoading(type)(false))
            )
            .subscribe(deliveryMethods =>
                this._channelsState.setChannelBlacklist(type)(deliveryMethods)
            );
    }

    getChannelBlacklistData$(type: ChannelBlacklistType): Observable<ChannelBlacklistItem[]> {
        return this._channelsState.getChannelBlacklist$(type)().pipe(getListData());
    }

    getChannelBlacklistMetadata$(type: ChannelBlacklistType): Observable<Metadata> {
        return this._channelsState.getChannelBlacklist$(type)().pipe(getMetadata());
    }

    clearChannelBlacklist$(type: ChannelBlacklistType): void {
        this._channelsState.setChannelBlacklist(type)(null);
    }

    createChannelBlacklist(
        channelId: number, type: ChannelBlacklistType, blacklist: ChannelBlacklistItem[]
    ): Observable<void> {
        this._channelsState.setChannelBlacklistSaving(true);
        return this._channelsApi.postChannelBlacklist(channelId, type, blacklist)
            .pipe(
                finalize(() => this._channelsState.setChannelBlacklistSaving(false))
            );
    }

    isChannelBlacklistInProgress$(type?: ChannelBlacklistType): Observable<boolean> {
        const obs$ = [this._channelsState.isChannelBlacklistSaving$()];
        if (type) {
            obs$.push(this._channelsState.isChannelBlacklistLoading$(type)());
        } else {
            obs$.push(
                this._channelsState.isChannelBlacklistLoading$(ChannelBlacklistType.email)(),
                this._channelsState.isChannelBlacklistLoading$(ChannelBlacklistType.nif)()
            );
        }
        return booleanOrMerge(obs$);
    }

    deleteChannelBlacklist(channelId: number, type: ChannelBlacklistType): Observable<void> {
        this._channelsState.setChannelBlacklistSaving(true);
        return this._channelsApi.deleteChannelBlacklist(channelId, type)
            .pipe(
                finalize(() => this._channelsState.setChannelBlacklistSaving(false))
            );
    }

    loadBlacklistStatus(channelId: number, type: ChannelBlacklistType): void {
        this._channelsState.setChannelBlacklistStatusLoading(type)(true);
        this._channelsApi.getChannelBlacklistStatus(channelId, type)
            .pipe(
                catchError(_ => of(null)),
                finalize(() => this._channelsState.setChannelBlacklistStatusLoading(type)(false))
            )
            .subscribe(deliveryMethods =>
                this._channelsState.setChannelBlacklistStatus(type)(deliveryMethods)
            );
    }

    getChannelBlacklistStatus$(type: ChannelBlacklistType): Observable<ChannelBlacklistStatus> {
        return this._channelsState.getChannelBlacklistStatus$(type)();
    }

    clearChannelBlacklistStatus$(type: ChannelBlacklistType): void {
        this._channelsState.setChannelBlacklistStatus(type)(null);
    }

    updateChannelBlacklistStatus(
        channelId: number, type: ChannelBlacklistType, status: ChannelBlacklistStatus
    ): Observable<void> {
        this._channelsState.setChannelBlacklistStatusSaving(type)(true);
        return this._channelsApi.putChannelBlacklistStatus(channelId, type, status)
            .pipe(
                finalize(() => this._channelsState.setChannelBlacklistStatusSaving(type)(false))
            );
    }

    isChannelBlacklistStatusInProgress$(): Observable<boolean> {
        return booleanOrMerge([
            this._channelsState.isChannelBlacklistStatusLoading$(ChannelBlacklistType.email)(),
            this._channelsState.isChannelBlacklistStatusLoading$(ChannelBlacklistType.nif)(),
            this._channelsState.isChannelBlacklistStatusSaving$(ChannelBlacklistType.email)(),
            this._channelsState.isChannelBlacklistStatusSaving$(ChannelBlacklistType.nif)()
        ]);
    }

    deleteChannelBlacklistItem(channelId: number, type: ChannelBlacklistType, value: string): Observable<void> {
        this._channelsState.setChannelBlacklistItemSaving(true);
        this._channelsState.setChannelBlacklistItemError(null);
        return this._channelsApi.deleteChannelBlacklistItem(channelId, type, value)
            .pipe(
                catchError(error => {
                    this._channelsState.setChannelBlacklistItemError(error);
                    throw error;
                }),
                finalize(() => this._channelsState.setChannelBlacklistItemSaving(false))
            );
    }

    isChannelBlacklistItemInProgress$(): Observable<boolean> {
        return booleanOrMerge([
            this._channelsState.isChannelBlacklistItemLoading$(),
            this._channelsState.isChannelBlacklistItemSaving$()
        ]);
    }

    loadChannelBookingSettings(channelId: string): void {
        this._channelsState.setChannelBookingSettingsError(null);
        this._channelsState.setChannelBookingSettingsInProgress(true);
        this._channelsApi.getChannelBookingSettings(channelId)
            .pipe(
                catchError(error => {
                    this._channelsState.setChannelBookingSettingsError(error);
                    return of(null);
                }),
                finalize(() => this._channelsState.setChannelBookingSettingsInProgress(false))
            )
            .subscribe(bookingSettings =>
                this._channelsState.setChannelBookingSettings(bookingSettings)
            );
    }

    getChannelBookingSettings$(): Observable<ChannelBookingSettings> {
        return this._channelsState.getChannelBookingSettings$();
    }

    saveChannelBookingSettings(channelId: string, bookingConfig: ChannelBookingSettings): Observable<void> {
        this._channelsState.setChannelBookingSettingsError(null);
        this._channelsState.setChannelBookingSettingsInProgress(true);
        return this._channelsApi.putChannelBookingSettings(channelId, bookingConfig)
            .pipe(
                catchError(error => {
                    this._channelsState.setChannelBookingSettingsError(error);
                    throw error;
                }),
                finalize(() => this._channelsState.setChannelBookingSettingsInProgress(false))
            );
    }

    isChannelBookingSettingsInProgress$(): Observable<boolean> {
        return this._channelsState.isChannelBookingSettingsInProgress$();
    }

    clearChannelBookingSettings(): void {
        this._channelsState.setChannelBookingSettings(null);
    }

    loadAuthVendorUserData(channelId: number): void {
        this._channelsState.setAuthVendorUserDataError(null);
        this._channelsState.setAuthVendorUserDataLoading(true);
        this._channelsApi.getAuthVendorsUserData(channelId)
            .pipe(
                catchError(error => {
                    this._channelsState.setAuthVendorUserDataError(error);
                    throw error;
                }),
                finalize(() => this._channelsState.setAuthVendorUserDataLoading(false))
            ).subscribe(authVendorUserData =>
                this._channelsState.setAuthVendorUserData(authVendorUserData)
            );
    }

    isAuthVendorUserDataLoading$(): Observable<boolean> {
        return this._channelsState.isAuthVendorUserDataLoading$();
    }

    getAuthVendorUserData$(): Observable<ChannelAuthVendorsUserData> {
        return this._channelsState.getAuthVendorUserData$();
    }

    updateAuthVendorUserData(channelId: number, data: ChannelAuthVendorsUserData): Observable<void> {
        return this._channelsApi.putAuthVendorsUserData(channelId, data);
    }

    loadAuthVendorSso(channelId: number): void {
        this._channelsState.setAuthVendorSsoError(null);
        this._channelsState.setAuthVendorSsoLoading(true);
        this._channelsApi.getAuthVendorsSso(channelId)
            .pipe(
                catchError(error => {
                    this._channelsState.setAuthVendorSsoError(error);
                    throw error;
                }),
                finalize(() => this._channelsState.setAuthVendorSsoLoading(false))
            ).subscribe(authVendorSso =>
                this._channelsState.setAuthVendorSso(authVendorSso)
            );
    }

    isAuthVendorSsoLoading$(): Observable<boolean> {
        return this._channelsState.isAuthVendorSsoLoading$();
    }

    getAuthVendorSso$(): Observable<ChannelAuthVendorsSso> {
        return this._channelsState.getAuthVendorSso$();
    }

    updateAuthVendorSso(channelId: number, data: ChannelAuthVendorsSso): Observable<void> {
        return this._channelsApi.putAuthVendorsSso(channelId, data);
    }

}
