import { PromotionChannels } from '@admin-clients/cpanel/promoters/data-access';
import { CommunicationTextContent } from '@admin-clients/cpanel/shared/data-access';
import { BaseStateProp } from '@admin-clients/shared/utility/state';
import { Injectable } from '@angular/core';
import { GetPromotionTplsResponse } from '../models/get-promotion-tpls-response.model';
import { PromotionTpl } from '../models/promotion-tpl.model';

@Injectable({
    providedIn: 'root'
})
export class PromotionTplsState {
    // promotion templates
    private _promotionTpls: BaseStateProp<GetPromotionTplsResponse> = new BaseStateProp<GetPromotionTplsResponse>();
    readonly setPromotionTpls = this._promotionTpls.setValueFunction();
    readonly getPromotionTpls$ = this._promotionTpls.getValueFunction();
    readonly setPromotionTplsLoading = this._promotionTpls.setInProgressFunction();
    readonly isPromotionTplsLoading$ = this._promotionTpls.getInProgressFunction();
    // promotion template
    private _promotionTpl: BaseStateProp<PromotionTpl> = new BaseStateProp<PromotionTpl>();
    readonly setPromotionTpl = this._promotionTpl.setValueFunction();
    readonly getPromotionTpl$ = this._promotionTpl.getValueFunction();
    readonly setPromotionTplLoading = this._promotionTpl.setInProgressFunction();
    readonly isPromotionTplLoading$ = this._promotionTpl.getInProgressFunction();
    readonly setPromotionTplError = this._promotionTpl.setErrorFunction();
    readonly getPromotionTplError$ = this._promotionTpl.getErrorFunction();
    // promotion template saving
    private _promotionTplSaving = new BaseStateProp<void>();
    readonly setPromotionTplSaving = this._promotionTplSaving.setInProgressFunction();
    readonly isPromotionTplSaving$ = this._promotionTplSaving.getInProgressFunction();
    readonly setPromotionTplSavingError = this._promotionTplSaving.setErrorFunction();
    readonly getPromotionTplSavingError$ = this._promotionTplSaving.getErrorFunction();
    // promotion template communication elements
    private _promotionTplChannelContents = new BaseStateProp<CommunicationTextContent[]>();
    readonly getPromotionTplChannelContents$ = this._promotionTplChannelContents.getValueFunction();
    readonly setPromotionTplChannelContents = this._promotionTplChannelContents.setValueFunction();
    readonly isPromotionTplChannelContentsLoading$ = this._promotionTplChannelContents.getInProgressFunction();
    readonly setPromotionTplChannelContentsLoading = this._promotionTplChannelContents.setInProgressFunction();
    readonly getPromotionTplChannelContentsError$ = this._promotionTplChannelContents.getErrorFunction();
    readonly setPromotionTplChannelContentsError = this._promotionTplChannelContents.setErrorFunction();
    // promotion template communication elements saving
    private _promotionTplChannelContentsSaving = new BaseStateProp<void>();
    readonly setPromotionTplChannelContentsSaving = this._promotionTplChannelContentsSaving.setInProgressFunction();
    readonly isPromotionTplChannelContentsSaving$ = this._promotionTplChannelContentsSaving.getInProgressFunction();
    readonly setPromotionTplChannelContentsSavingError = this._promotionTplChannelContentsSaving.setErrorFunction();
    readonly getPromotionTplChannelContentsSavingError$ = this._promotionTplChannelContentsSaving.getErrorFunction();
    // promotion template channels
    private _promotionTplChannels = new BaseStateProp<PromotionChannels>();
    readonly getPromotionTplChannels$ = this._promotionTplChannels.getValueFunction();
    readonly setPromotionTplChannels = this._promotionTplChannels.setValueFunction();
    readonly getPromotionTplChannelsLoading$ = this._promotionTplChannels.getInProgressFunction();
    readonly setPromotionTplChannelsLoading = this._promotionTplChannels.setInProgressFunction();
    readonly getPromotionTplChannelsError$ = this._promotionTplChannels.getErrorFunction();
    readonly setPromotionTplChannelsError = this._promotionTplChannels.setErrorFunction();
    // promotion template channels saving
    private _promotionTplChannelsSaving = new BaseStateProp<void>();
    readonly isPromotionTplChannelsSaving$ = this._promotionTplChannelsSaving.getInProgressFunction();
    readonly setPromotionTplChannelsSaving = this._promotionTplChannelsSaving.setInProgressFunction();
}
