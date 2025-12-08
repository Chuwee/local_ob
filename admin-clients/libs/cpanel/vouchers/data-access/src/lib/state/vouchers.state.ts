import { CommunicationTextContent } from '@admin-clients/cpanel/shared/data-access';
import { BaseStateProp } from '@admin-clients/shared/utility/state';
import { Injectable } from '@angular/core';
import { GetVoucherGroupsResponse } from '../models/get-voucher-groups-response.model';
import { GetVouchersResponse } from '../models/get-vouchers-response.model';
import { GiftCardGroupConfig } from '../models/gift-card-group-config.model';
import { GiftCardGroupContentImage } from '../models/gift-card-group-content-image.model';
import { VoucherGroup } from '../models/voucher-groups.model';
import { Voucher } from '../models/voucher.model';

@Injectable({
    providedIn: 'root'
})
export class VouchersState {

    private _voucherGroupsList = new BaseStateProp<GetVoucherGroupsResponse>();
    readonly setVoucherGroupsList = this._voucherGroupsList.setValueFunction();
    readonly getVoucherGroupsList$ = this._voucherGroupsList.getValueFunction();
    readonly setVoucherGroupsListLoading = this._voucherGroupsList.setInProgressFunction();
    readonly isVoucherGroupsListLoading$ = this._voucherGroupsList.getInProgressFunction();

    private _voucherGroup = new BaseStateProp<VoucherGroup>();
    readonly setVoucherGroup = this._voucherGroup.setValueFunction();
    readonly getVoucherGroup$ = this._voucherGroup.getValueFunction();
    readonly setVoucherGroupLoading = this._voucherGroup.setInProgressFunction();
    readonly isVoucherGroupLoading$ = this._voucherGroup.getInProgressFunction();

    private _voucherGroupSaving = new BaseStateProp<void>();
    readonly setVoucherGroupSaving = this._voucherGroupSaving.setInProgressFunction();
    readonly isVoucherGroupSaving$ = this._voucherGroupSaving.getInProgressFunction();
    readonly setVoucherGroupSavingError = this._voucherGroupSaving.setErrorFunction();
    readonly getVoucherGroupSavingError$ = this._voucherGroupSaving.getErrorFunction();

    private _giftCardGroupConfig = new BaseStateProp<GiftCardGroupConfig>();
    readonly setGiftCardGroupConfig = this._giftCardGroupConfig.setValueFunction();
    readonly getGiftCardGroupConfig$ = this._giftCardGroupConfig.getValueFunction();
    readonly setGiftCardGroupConfigLoading = this._giftCardGroupConfig.setInProgressFunction();
    readonly isGiftCardGroupConfigLoading$ = this._giftCardGroupConfig.getInProgressFunction();

    private _giftCardGroupConfigSaving = new BaseStateProp<void>();
    readonly setGiftCardGroupConfigSaving = this._giftCardGroupConfigSaving.setInProgressFunction();
    readonly isGiftCardGroupConfigSaving$ = this._giftCardGroupConfigSaving.getInProgressFunction();
    readonly setGiftCardGroupConfigSavingError = this._giftCardGroupConfigSaving.setErrorFunction();
    readonly getGiftCardGroupConfigSavingError$ = this._giftCardGroupConfigSaving.getErrorFunction();

    private _vouchers = new BaseStateProp<GetVouchersResponse>();
    readonly setVouchers = this._vouchers.setValueFunction();
    readonly getVouchers$ = this._vouchers.getValueFunction();
    readonly setVouchersLoading = this._vouchers.setInProgressFunction();
    readonly isVouchersLoading$ = this._vouchers.getInProgressFunction();

    private _vouchersSaving = new BaseStateProp<void>();
    readonly setVouchersSaving = this._vouchersSaving.setInProgressFunction();
    readonly isVouchersSaving$ = this._vouchersSaving.getInProgressFunction();
    readonly setVouchersSavingError = this._vouchersSaving.setErrorFunction();
    readonly getVouchersSavingError$ = this._vouchersSaving.getErrorFunction();

    private _voucher = new BaseStateProp<Voucher>();
    readonly setVoucher = this._voucher.setValueFunction();
    readonly getVoucher$ = this._voucher.getValueFunction();
    readonly setVoucherLoading = this._voucher.setInProgressFunction();
    readonly isVoucherLoading$ = this._voucher.getInProgressFunction();

    private _voucherSaving = new BaseStateProp<void>();
    readonly setVoucherSaving = this._voucherSaving.setInProgressFunction();
    readonly isVoucherSaving$ = this._voucherSaving.getInProgressFunction();
    readonly setVoucherSavingError = this._voucherSaving.setErrorFunction();
    readonly getVoucherSavingError$ = this._voucherSaving.getErrorFunction();

    private _voucherBalance = new BaseStateProp<number>();
    readonly setVoucherBalance = this._voucherBalance.setValueFunction();
    readonly getVoucherBalance$ = this._voucherBalance.getValueFunction();
    readonly setVoucherBalanceLoading = this._voucherBalance.setInProgressFunction();
    readonly isVoucherBalanceLoading$ = this._voucherBalance.getInProgressFunction();

    private _voucherBalanceSaving = new BaseStateProp<void>();
    readonly setVoucherBalanceSaving = this._voucherBalanceSaving.setInProgressFunction();
    readonly getVoucherBalanceSaving$ = this._voucherBalanceSaving.getInProgressFunction();
    readonly setVoucherBalanceSavingError = this._voucherBalanceSaving.setErrorFunction();
    readonly getVoucherBalanceSavingError$ = this._voucherBalanceSaving.getErrorFunction();

    private _voucherGroupChannels = new BaseStateProp<unknown>();
    readonly setVoucherGroupChannels = this._voucherGroupChannels.setValueFunction();
    readonly getVoucherGroupChannels$ = this._voucherGroupChannels.getValueFunction();
    readonly setVoucherGroupChannelsInProgress = this._voucherGroupChannels.setInProgressFunction();
    readonly isVoucherGroupChannelsInProgress$ = this._voucherGroupChannels.getInProgressFunction();

    private _voucherGroupChannelsSaving = new BaseStateProp<void>();
    readonly setVoucherGroupChannelsSaving = this._voucherGroupChannelsSaving.setInProgressFunction();
    readonly isVoucherGroupChannelsSaving$ = this._voucherGroupChannelsSaving.getInProgressFunction();
    readonly setVoucherGroupChannelsSavingError = this._voucherGroupChannelsSaving.setErrorFunction();
    readonly getVoucherGroupChannelsSavingError$ = this._voucherGroupChannelsSaving.getErrorFunction();

    private _voucherGroupContents = new BaseStateProp<CommunicationTextContent[]>();
    readonly setVoucherGroupContents = this._voucherGroupContents.setValueFunction();
    readonly getVoucherGroupContents$ = this._voucherGroupContents.getValueFunction();
    readonly setVoucherGroupContentsLoading = this._voucherGroupContents.setInProgressFunction();
    readonly isVoucherGroupContentsLoading$ = this._voucherGroupContents.getInProgressFunction();

    private _voucherGroupContentsSaving = new BaseStateProp<void>();
    readonly setVoucherGroupContentsSaving = this._voucherGroupContentsSaving.setInProgressFunction();
    readonly isVoucherGroupContentsSaving$ = this._voucherGroupContentsSaving.getInProgressFunction();
    readonly setVoucherGroupContentsSavingError = this._voucherGroupContentsSaving.setErrorFunction();
    readonly getVoucherGroupContentsSavingError$ = this._voucherGroupContentsSaving.getErrorFunction();

    private _giftCardTextContents = new BaseStateProp<CommunicationTextContent[]>();
    readonly setGiftCardTextContents = this._giftCardTextContents.setValueFunction();
    readonly getGiftCardTextContents$ = this._giftCardTextContents.getValueFunction();
    readonly setGiftCardTextContentsLoading = this._giftCardTextContents.setInProgressFunction();
    readonly isGiftCardTextContentsLoading$ = this._giftCardTextContents.getInProgressFunction();

    private _giftCardTextContentsSaving = new BaseStateProp<void>();
    readonly setGiftCardTextContentsSaving = this._giftCardTextContentsSaving.setInProgressFunction();
    readonly isGiftCardTextContentsSaving$ = this._giftCardTextContentsSaving.getInProgressFunction();
    readonly setGiftCardTextContentsSavingError = this._giftCardTextContentsSaving.setErrorFunction();
    readonly getGiftCardTextContentsSavingError$ = this._giftCardTextContentsSaving.getErrorFunction();

    private _giftCardContentImages = new BaseStateProp<GiftCardGroupContentImage[]>();
    readonly setGiftCardContentImages = this._giftCardContentImages.setValueFunction();
    readonly getGiftCardContentImages$ = this._giftCardContentImages.getValueFunction();
    readonly setGiftCardContentImagesLoading = this._giftCardContentImages.setInProgressFunction();
    readonly isGiftCardContentImagesLoading$ = this._giftCardContentImages.getInProgressFunction();

    private _giftCardContentImagesSaving = new BaseStateProp<void>();
    readonly setGiftCardContentImagesSaving = this._giftCardContentImagesSaving.setInProgressFunction();
    readonly isGiftCardContentImagesSaving$ = this._giftCardContentImagesSaving.getInProgressFunction();
    readonly setGiftCardContentImagesSavingError = this._giftCardContentImagesSaving.setErrorFunction();
    readonly getGiftCardContentImagesSavingError$ = this._giftCardContentImagesSaving.getErrorFunction();

    private _giftCardContentImagesRemoving = new BaseStateProp<void>();
    readonly setGiftCardContentImagesRemoving = this._giftCardContentImagesRemoving.setInProgressFunction();
    readonly isGiftCardContentImagesRemoving$ = this._giftCardContentImagesRemoving.getInProgressFunction();
    readonly setGiftCardContentImagesRemovingError = this._giftCardContentImagesRemoving.setErrorFunction();
    readonly getGiftCardContentImagesRemovingError$ = this._giftCardContentImagesRemoving.getErrorFunction();

    private _exportVouchers = new BaseStateProp<void>();
    readonly isExportVouchersLoading$ = this._exportVouchers.getInProgressFunction();
    readonly setExportVouchersLoading = this._exportVouchers.setInProgressFunction();

    private _resendVoucher = new BaseStateProp<void>();
    readonly isResendVoucherLoading$ = this._resendVoucher.getInProgressFunction();
    readonly setResendVoucherLoading = this._resendVoucher.setInProgressFunction();

}
