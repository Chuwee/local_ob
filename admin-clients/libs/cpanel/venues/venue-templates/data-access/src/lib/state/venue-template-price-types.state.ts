import { TicketContentImage, TicketContentText } from '@admin-clients/cpanel/promoters/events/data-access';
import { IdName } from '@admin-clients/shared/data-access/models';
import { BaseStateProp } from '@admin-clients/shared/utility/state';
import { Injectable } from '@angular/core';
import { GetPriceTypeRestricion, RestrictedPriceZones } from '../models/price-type-restriction.model';
import { VenueTemplatePriceTypeChannelContent } from '../models/venue-template-price-type-channel-content.model';

@Injectable({
    providedIn: 'root'
})
export class VenueTemplatePriceTypesState {
    // venue template price type channel content
    private readonly _priceTypeChannelContents = new BaseStateProp<{
        priceTypeId: number;
        contents: VenueTemplatePriceTypeChannelContent[];
    }>();

    readonly getTypeChannelContents$ = this._priceTypeChannelContents.getValueFunction();
    readonly setPriceTypeChannelContents = this._priceTypeChannelContents.setValueFunction();
    readonly isPriceTypeChannelContentsLoading$ = this._priceTypeChannelContents.getInProgressFunction();
    readonly setPriceTypeChannelContentsLoading = this._priceTypeChannelContents.setInProgressFunction();
    // venue template price type channel content saving
    private _priceTypesChannelContentsSaving = new BaseStateProp<boolean>();
    readonly setPriceTypesChannelContentsSaving = this._priceTypesChannelContentsSaving.setInProgressFunction();
    readonly isPriceTypesChannelContentsSaving$ = this._priceTypesChannelContentsSaving.getInProgressFunction();
    // venue template restricted price types
    private readonly _restrictedPriceTypes = new BaseStateProp<RestrictedPriceZones>();
    readonly getRestrictedPriceTypes$ = this._restrictedPriceTypes.getValueFunction();
    readonly setRestrictedPriceTypes = this._restrictedPriceTypes.setValueFunction();
    readonly isRestrictedPriceTypesInProgress$ = this._restrictedPriceTypes.getInProgressFunction();
    readonly setRestrictedPriceTypesInProgress = this._restrictedPriceTypes.setInProgressFunction();
    // venue template price type restriction
    private readonly _priceTypeRestriction = new BaseStateProp<GetPriceTypeRestricion>();
    readonly getPriceTypeRestriction$ = this._priceTypeRestriction.getValueFunction();
    readonly setPriceTypeRestriction = this._priceTypeRestriction.setValueFunction();
    readonly isPriceTypeRestrictionInProgress$ = this._priceTypeRestriction.getInProgressFunction();
    readonly setPriceTypeRestrictionInProgress = this._priceTypeRestriction.setInProgressFunction();
    // venue template price type ticket PDF TEXTS content
    private readonly _modifiedTicketContentPriceTypes = new BaseStateProp<IdName[]>();
    readonly getModifiedTicketContentPriceTypes$ = this._modifiedTicketContentPriceTypes.getValueFunction();
    readonly setModifiedTicketContentPriceTypes = this._modifiedTicketContentPriceTypes.setValueFunction();
    readonly isModifiedTicketContentPriceTypesLoading$ = this._modifiedTicketContentPriceTypes.getInProgressFunction();
    readonly setModifiedTicketContentPriceTypesLoading = this._modifiedTicketContentPriceTypes.setInProgressFunction();
    // venue template price type ticket PDF TEXTS content
    private readonly _priceTypeTicketPdfTexts = new BaseStateProp<TicketContentText[]>();
    readonly getPriceTypeTicketPdfTexts$ = this._priceTypeTicketPdfTexts.getValueFunction();
    readonly setPriceTypeTicketPdfTexts = this._priceTypeTicketPdfTexts.setValueFunction();
    readonly isPriceTypeTicketPdfTextsLoading$ = this._priceTypeTicketPdfTexts.getInProgressFunction();
    readonly setPriceTypeTicketPdfTextsLoading = this._priceTypeTicketPdfTexts.setInProgressFunction();
    // venue template price type ticket PDF IMAGES content
    private readonly _priceTypeTicketPdfImages = new BaseStateProp<TicketContentImage[]>();
    readonly getPriceTypeTicketPdfImages$ = this._priceTypeTicketPdfImages.getValueFunction();
    readonly setPriceTypeTicketPdfImages = this._priceTypeTicketPdfImages.setValueFunction();
    readonly isPriceTypeTicketPdfImagesLoading$ = this._priceTypeTicketPdfImages.getInProgressFunction();
    readonly setPriceTypeTicketPdfImagesLoading = this._priceTypeTicketPdfImages.setInProgressFunction();
    // venue template price type ticket PRINTER TEXTS content
    private readonly _priceTypeTicketPrinterTexts = new BaseStateProp<TicketContentText[]>();
    readonly getPriceTypeTicketPrinterTexts$ = this._priceTypeTicketPrinterTexts.getValueFunction();
    readonly setPriceTypeTicketPrinterTexts = this._priceTypeTicketPrinterTexts.setValueFunction();
    readonly isPriceTypeTicketPrinterTextsLoading$ = this._priceTypeTicketPrinterTexts.getInProgressFunction();
    readonly setPriceTypeTicketPrinterTextsLoading = this._priceTypeTicketPrinterTexts.setInProgressFunction();
    // venue template price type ticket PRINTER IMAGES content
    private readonly _priceTypeTicketPrinterImages = new BaseStateProp<TicketContentImage[]>();
    readonly getPriceTypeTicketPrinterImages$ = this._priceTypeTicketPrinterImages.getValueFunction();
    readonly setPriceTypeTicketPrinterImages = this._priceTypeTicketPrinterImages.setValueFunction();
    readonly isPriceTypeTicketPrinterImagesLoading$ = this._priceTypeTicketPrinterImages.getInProgressFunction();
    readonly setPriceTypeTicketPrinterImagesLoading = this._priceTypeTicketPrinterImages.setInProgressFunction();
    // venue template price type ticket PASSBOOK TEXTS content
    private readonly _priceTypeTicketPassbookTexts = new BaseStateProp<TicketContentText[]>();
    readonly getPriceTypeTicketPassbookTexts$ = this._priceTypeTicketPassbookTexts.getValueFunction();
    readonly setPriceTypeTicketPassbookTexts = this._priceTypeTicketPassbookTexts.setValueFunction();
    readonly isPriceTypeTicketPassbookTextsLoading$ = this._priceTypeTicketPassbookTexts.getInProgressFunction();
    readonly setPriceTypeTicketPassbookTextsLoading = this._priceTypeTicketPassbookTexts.setInProgressFunction();
    // venue template price type ticket PASSBOOK IMAGES content
    private readonly _priceTypeTicketPassbookImages = new BaseStateProp<TicketContentImage[]>();
    readonly getPriceTypeTicketPassbookImages$ = this._priceTypeTicketPassbookImages.getValueFunction();
    readonly setPriceTypeTicketPassbookImages = this._priceTypeTicketPassbookImages.setValueFunction();
    readonly isPriceTypeTicketPassbookImagesLoading$ = this._priceTypeTicketPassbookImages.getInProgressFunction();
    readonly setPriceTypeTicketPassbookImagesLoading = this._priceTypeTicketPassbookImages.setInProgressFunction();
}
