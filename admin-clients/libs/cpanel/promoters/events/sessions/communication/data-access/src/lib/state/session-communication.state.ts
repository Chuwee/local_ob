import { BaseStateProp } from '@admin-clients/shared/utility/state';
import { Injectable } from '@angular/core';
import { SessionChannelImage } from '../models/session-channel-image.model';
import { SessionChannelText } from '../models/session-channel-text.model';
import { SessionTicketImage } from '../models/session-ticket-image.model';
import { SessionTicketText } from '../models/session-ticket-text.model';

@Injectable({
    providedIn: 'root'
})
export class SessionCommunicationState {
    // communication Channel Texts
    private readonly _channelTexts = new BaseStateProp<SessionChannelText[]>();
    readonly getChannelTexts$ = this._channelTexts.getValueFunction();
    readonly setChannelTexts = this._channelTexts.setValueFunction();
    readonly isChannelTextsInProgress$ = this._channelTexts.getInProgressFunction();
    readonly setChannelTextsInProgress = this._channelTexts.setInProgressFunction();
    // communication Channel Images
    private readonly _channelImages = new BaseStateProp<SessionChannelImage[]>();
    readonly getChannelImages$ = this._channelImages.getValueFunction();
    readonly setChannelImages = this._channelImages.setValueFunction();
    readonly isChannelImagesInProgress$ = this._channelImages.getInProgressFunction();
    readonly setChannelImagesInProgress = this._channelImages.setInProgressFunction();
    private readonly _deletechannelImages = new BaseStateProp<SessionChannelImage[]>();
    readonly isChannelImagesDeleteInProgress$ = this._deletechannelImages.getInProgressFunction();
    readonly setChannelImagesDeleteInProgress = this._deletechannelImages.setInProgressFunction();
    // communication Ticket Texts
    private readonly _ticketPdfTexts = new BaseStateProp<SessionTicketText[]>();
    readonly getTicketPdfTexts$ = this._ticketPdfTexts.getValueFunction();
    readonly setTicketPdfTexts = this._ticketPdfTexts.setValueFunction();
    readonly isTicketPdfTextsInProgress$ = this._ticketPdfTexts.getInProgressFunction();
    readonly setTicketPdfTextsInProgress = this._ticketPdfTexts.setInProgressFunction();
    // communication Ticket Texts
    private readonly _ticketPrinterTexts = new BaseStateProp<SessionTicketText[]>();
    readonly getTicketPrinterTexts$ = this._ticketPrinterTexts.getValueFunction();
    readonly setTicketPrinterTexts = this._ticketPrinterTexts.setValueFunction();
    readonly isTicketPrinterTextsInProgress$ = this._ticketPrinterTexts.getInProgressFunction();
    readonly setTicketPrinterTextsInProgress = this._ticketPrinterTexts.setInProgressFunction();
    // communication Ticket pdf Images
    private readonly _ticketPdfImages = new BaseStateProp<SessionTicketImage[]>();
    readonly getTicketPdfImages$ = this._ticketPdfImages.getValueFunction();
    readonly setTicketPdfImages = this._ticketPdfImages.setValueFunction();
    readonly isTicketPdfImagesInProgress$ = this._ticketPdfImages.getInProgressFunction();
    readonly setTicketPdfImagesInProgress = this._ticketPdfImages.setInProgressFunction();
    private readonly _deleteTicketPdfImages = new BaseStateProp<SessionTicketImage[]>();
    readonly isTicketPdfImagesDeleteInProgress$ = this._deleteTicketPdfImages.getInProgressFunction();
    readonly setTicketPdfImagesDeleteInProgress = this._deleteTicketPdfImages.setInProgressFunction();
    // communication Ticket printer Images
    private readonly _ticketPrinterImages = new BaseStateProp<SessionTicketImage[]>();
    readonly getTicketPrinterImages$ = this._ticketPrinterImages.getValueFunction();
    readonly setTicketPrinterImages = this._ticketPrinterImages.setValueFunction();
    readonly isTicketPrinterImagesInProgress$ = this._ticketPrinterImages.getInProgressFunction();
    readonly setTicketPrinterImagesInProgress = this._ticketPrinterImages.setInProgressFunction();
    private readonly _deleteTicketPrinterImages = new BaseStateProp<SessionTicketImage[]>();
    readonly isTicketPrinterImagesDeleteInProgress$ = this._deleteTicketPrinterImages.getInProgressFunction();
    readonly setTicketPrinterImagesDeleteInProgress = this._deleteTicketPrinterImages.setInProgressFunction();
    // communication Ticket passbook Texts
    private _ticketPassbookContentTexts: BaseStateProp<SessionTicketText[]> = new BaseStateProp<SessionTicketText[]>();
    readonly setTicketPassbookContentTexts = this._ticketPassbookContentTexts.setValueFunction();
    readonly getTicketPassbookContentTexts$ = this._ticketPassbookContentTexts.getValueFunction();
    readonly setTicketPassbookContentTextsInProgress = this._ticketPassbookContentTexts.setInProgressFunction();
    readonly isTicketPassbookContentTextsInProgress$ = this._ticketPassbookContentTexts.getInProgressFunction();

    private _ticketPassbookContentTextsSaving = new BaseStateProp<void>();
    readonly setTicketPassbookContentTextsSaving = this._ticketPassbookContentTextsSaving.setInProgressFunction();
    readonly isTicketPassbookContentTextsSaving$ = this._ticketPassbookContentTextsSaving.getInProgressFunction();
    // communication Ticket passbook Images
    private _ticketPassbookContentImages: BaseStateProp<SessionTicketImage[]> = new BaseStateProp<SessionTicketImage[]>();
    readonly setTicketPassbookContentImages = this._ticketPassbookContentImages.setValueFunction();
    readonly getTicketPassbookContentImages$ = this._ticketPassbookContentImages.getValueFunction();
    readonly setTicketPassbookContentImagesInProgress = this._ticketPassbookContentImages.setInProgressFunction();
    readonly isTicketPassbookContentImagesInProgress$ = this._ticketPassbookContentImages.getInProgressFunction();

    private _ticketPassbookContentImagesSaving = new BaseStateProp<void>();
    readonly setTicketPassbookContentImagesSaving = this._ticketPassbookContentImagesSaving.setInProgressFunction();
    readonly isTicketPassbookContentImagesSaving$ = this._ticketPassbookContentImagesSaving.getInProgressFunction();

    private _ticketPassbookContentImagesRemoving = new BaseStateProp<void>();
    readonly setTicketPassbookContentImagesRemoving = this._ticketPassbookContentImagesRemoving.setInProgressFunction();
    readonly isTicketPassbookContentImagesRemoving$ = this._ticketPassbookContentImagesRemoving.getInProgressFunction();
    //downloading passbook preview
    private _getDownloadUrlPassbookPreviewDownloading = new BaseStateProp<void>();
    readonly setDownloadUrlPassbookPreviewDownloadingInProgress = this._getDownloadUrlPassbookPreviewDownloading.setInProgressFunction();
    readonly isDownloadUrlPassbookPreviewInProgress$ = this._getDownloadUrlPassbookPreviewDownloading.getInProgressFunction();
}
