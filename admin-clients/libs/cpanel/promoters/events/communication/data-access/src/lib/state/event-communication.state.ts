import { TicketContentImage, TicketContentText } from '@admin-clients/cpanel/promoters/events/data-access';
import { BaseStateProp } from '@admin-clients/shared/utility/state';
import { Injectable } from '@angular/core';
import { EventChannelContentImage } from '../models/event-channel-content-image.model';
import { EventChannelContentText } from '../models/event-channel-content-text.model';
import { EventTicketTemplate } from '../models/event-ticket-template.model';

@Injectable({
    providedIn: 'root'
})
export class EventCommunicationState {
    private _channelContentTexts = new BaseStateProp<EventChannelContentText[]>();
    readonly setChannelContentTexts = this._channelContentTexts.setValueFunction();
    readonly getChannelContentTexts$ = this._channelContentTexts.getValueFunction();
    readonly setChannelContentTextsInProgress = this._channelContentTexts.setInProgressFunction();
    readonly isChannelContentTextsInProgress$ = this._channelContentTexts.getInProgressFunction();

    private _channelContentTextsSaving = new BaseStateProp<void>();
    readonly setChannelContentTextsSaving = this._channelContentTextsSaving.setInProgressFunction();
    readonly isChannelContentTextsSaving$ = this._channelContentTextsSaving.getInProgressFunction();

    private _channelContentImages = new BaseStateProp<EventChannelContentImage[]>();
    readonly setChannelContentImages = this._channelContentImages.setValueFunction();
    readonly getChannelContentImages$ = this._channelContentImages.getValueFunction();
    readonly setChannelContentImagesInProgress = this._channelContentImages.setInProgressFunction();
    readonly isChannelContentImagesInProgress$ = this._channelContentImages.getInProgressFunction();

    private _channelContentImagesSaving = new BaseStateProp<void>();
    readonly setChannelContentImagesSaving = this._channelContentImagesSaving.setInProgressFunction();
    readonly isChannelContentImagesSaving$ = this._channelContentImagesSaving.getInProgressFunction();

    private _channelContentImagesRemoving = new BaseStateProp<void>();
    readonly setChannelContentImagesRemoving = this._channelContentImagesRemoving.setInProgressFunction();
    readonly isChannelContentImagesRemoving$ = this._channelContentImagesRemoving.getInProgressFunction();

    private _ticketTemplates = new BaseStateProp<EventTicketTemplate[]>();
    readonly setTicketTemplates = this._ticketTemplates.setValueFunction();
    readonly getTicketTemplates$ = this._ticketTemplates.getValueFunction();
    readonly setTicketTemplatesInProgress = this._ticketTemplates.setInProgressFunction();
    readonly isTicketTemplatesInProgress$ = this._ticketTemplates.getInProgressFunction();

    private _ticketTemplatesSaving = new BaseStateProp<void>();
    readonly setTicketTemplatesSaving = this._ticketTemplatesSaving.setInProgressFunction();
    readonly isTicketTemplatesSaving$ = this._ticketTemplatesSaving.getInProgressFunction();

    private _ticketPdfContentTexts = new BaseStateProp<TicketContentText[]>();
    readonly setTicketPdfContentTexts = this._ticketPdfContentTexts.setValueFunction();
    readonly getTicketPdfContentTexts$ = this._ticketPdfContentTexts.getValueFunction();
    readonly setTicketPdfContentTextsInProgress = this._ticketPdfContentTexts.setInProgressFunction();
    readonly isTicketPdfContentTextsInProgress$ = this._ticketPdfContentTexts.getInProgressFunction();

    private _ticketPdfContentTextsSaving = new BaseStateProp<void>();
    readonly setTicketPdfContentTextsSaving = this._ticketPdfContentTextsSaving.setInProgressFunction();
    readonly isTicketPdfContentTextsSaving$ = this._ticketPdfContentTextsSaving.getInProgressFunction();

    private _ticketPdfContentImages = new BaseStateProp<TicketContentImage[]>();
    readonly setTicketPdfContentImages = this._ticketPdfContentImages.setValueFunction();
    readonly getTicketPdfContentImages$ = this._ticketPdfContentImages.getValueFunction();
    readonly setTicketPdfContentImagesInProgress = this._ticketPdfContentImages.setInProgressFunction();
    readonly isTicketPdfContentImagesInProgress$ = this._ticketPdfContentImages.getInProgressFunction();

    private _ticketPdfContentImagesSaving = new BaseStateProp<void>();
    readonly setTicketPdfContentImagesSaving = this._ticketPdfContentImagesSaving.setInProgressFunction();
    readonly isTicketPdfContentImagesSaving$ = this._ticketPdfContentImagesSaving.getInProgressFunction();

    private _ticketPdfContentImagesRemoving = new BaseStateProp<void>();
    readonly setTicketPdfContentImagesRemoving = this._ticketPdfContentImagesRemoving.setInProgressFunction();
    readonly isTicketPdfContentImagesRemoving$ = this._ticketPdfContentImagesRemoving.getInProgressFunction();

    private _ticketPrinterContentTexts = new BaseStateProp<TicketContentText[]>();
    readonly setTicketPrinterContentTexts = this._ticketPrinterContentTexts.setValueFunction();
    readonly getTicketPrinterContentTexts$ = this._ticketPrinterContentTexts.getValueFunction();
    readonly setTicketPrinterContentTextsInProgress = this._ticketPrinterContentTexts.setInProgressFunction();
    readonly isTicketPrinterContentTextsInProgress$ = this._ticketPrinterContentTexts.getInProgressFunction();

    private _ticketPrinterContentTextsSaving = new BaseStateProp<void>();
    readonly setTicketPrinterContentTextsSaving = this._ticketPrinterContentTextsSaving.setInProgressFunction();
    readonly isTicketPrinterContentTextsSaving$ = this._ticketPrinterContentTextsSaving.getInProgressFunction();

    private _ticketPrinterContentImages = new BaseStateProp<TicketContentImage[]>();
    readonly setTicketPrinterContentImages = this._ticketPrinterContentImages.setValueFunction();
    readonly getTicketPrinterContentImages$ = this._ticketPrinterContentImages.getValueFunction();
    readonly setTicketPrinterContentImagesInProgress = this._ticketPrinterContentImages.setInProgressFunction();
    readonly isTicketPrinterContentImagesInProgress$ = this._ticketPrinterContentImages.getInProgressFunction();

    private _ticketPrinterContentImagesSaving = new BaseStateProp<void>();
    readonly setTicketPrinterContentImagesSaving = this._ticketPrinterContentImagesSaving.setInProgressFunction();
    readonly isTicketPrinterContentImagesSaving$ = this._ticketPrinterContentImagesSaving.getInProgressFunction();

    private _ticketPrinterContentImagesRemoving = new BaseStateProp<void>();
    readonly setTicketPrinterContentImagesRemoving = this._ticketPrinterContentImagesRemoving.setInProgressFunction();
    readonly isTicketPrinterContentImagesRemoving$ = this._ticketPrinterContentImagesRemoving.getInProgressFunction();

    private _ticketPassbookContentTexts = new BaseStateProp<TicketContentText[]>();
    readonly setTicketPassbookContentTexts = this._ticketPassbookContentTexts.setValueFunction();
    readonly getTicketPassbookContentTexts$ = this._ticketPassbookContentTexts.getValueFunction();
    readonly setTicketPassbookContentTextsInProgress = this._ticketPassbookContentTexts.setInProgressFunction();
    readonly isTicketPassbookContentTextsInProgress$ = this._ticketPassbookContentTexts.getInProgressFunction();

    private _ticketPassbookContentTextsSaving = new BaseStateProp<void>();
    readonly setTicketPassbookContentTextsSaving = this._ticketPassbookContentTextsSaving.setInProgressFunction();
    readonly isTicketPassbookContentTextsSaving$ = this._ticketPassbookContentTextsSaving.getInProgressFunction();

    private _ticketPassbookContentImages = new BaseStateProp<TicketContentImage[]>();
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

    private _ticketPdfPreviewDownloading = new BaseStateProp<void>();
    readonly setTicketPdfPreviewDownloading = this._ticketPdfPreviewDownloading.setInProgressFunction();
    readonly isTicketPdfPreviewDownloading$ = this._ticketPdfPreviewDownloading.getInProgressFunction();

    private _getDownloadUrlPassbookPreviewDownloading = new BaseStateProp<void>();
    readonly setDownloadUrlPassbookPreviewDownloadingInProgress = this._getDownloadUrlPassbookPreviewDownloading.setInProgressFunction();
    readonly isDownloadUrlPassbookPreviewInProgress$ = this._getDownloadUrlPassbookPreviewDownloading.getInProgressFunction();
}
