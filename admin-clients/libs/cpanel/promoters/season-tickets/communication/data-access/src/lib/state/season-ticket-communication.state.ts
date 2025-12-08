import { BaseStateProp } from '@admin-clients/shared/utility/state';
import { Injectable } from '@angular/core';
import { SeasonTicketChannelContentImage } from '../models/season-ticket-channel-content-image.model';
import { SeasonTicketChannelContentText } from '../models/season-ticket-channel-content-text.model';
import { SeasonTicketTicketContentImage } from '../models/season-ticket-ticket-content-image.model';
import { SeasonTicketTicketContentText } from '../models/season-ticket-ticket-content-text.model';
import { SeasonTicketTicketTemplate } from '../models/season-ticket-ticket-template.model';

@Injectable()
export class SeasonTicketCommunicationState {

    private readonly _channelContentTexts: BaseStateProp<SeasonTicketChannelContentText[]> =
        new BaseStateProp<SeasonTicketChannelContentText[]>();

    readonly setChannelContentTexts = this._channelContentTexts.setValueFunction();
    readonly getChannelContentTexts$ = this._channelContentTexts.getValueFunction();
    readonly setChannelContentTextsInProgress = this._channelContentTexts.setInProgressFunction();
    readonly isChannelContentTextsInProgress$ = this._channelContentTexts.getInProgressFunction();

    private readonly _channelContentTextsSaving = new BaseStateProp<void>();
    readonly setChannelContentTextsSaving = this._channelContentTextsSaving.setInProgressFunction();
    readonly isChannelContentTextsSaving$ = this._channelContentTextsSaving.getInProgressFunction();

    private readonly _channelContentImages: BaseStateProp<SeasonTicketChannelContentImage[]> =
        new BaseStateProp<SeasonTicketChannelContentImage[]>();

    readonly setChannelContentImages = this._channelContentImages.setValueFunction();
    readonly getChannelContentImages$ = this._channelContentImages.getValueFunction();
    readonly setChannelContentImagesInProgress = this._channelContentImages.setInProgressFunction();
    readonly isChannelContentImagesInProgress$ = this._channelContentImages.getInProgressFunction();

    private readonly _channelContentImagesSaving = new BaseStateProp<void>();
    readonly setChannelContentImagesSaving = this._channelContentImagesSaving.setInProgressFunction();
    readonly isChannelContentImagesSaving$ = this._channelContentImagesSaving.getInProgressFunction();

    private readonly _channelContentImagesRemoving = new BaseStateProp<void>();
    readonly setChannelContentImagesRemoving = this._channelContentImagesRemoving.setInProgressFunction();
    readonly isChannelContentImagesRemoving$ = this._channelContentImagesRemoving.getInProgressFunction();

    private readonly _ticketTemplates: BaseStateProp<SeasonTicketTicketTemplate[]> =
        new BaseStateProp<SeasonTicketTicketTemplate[]>();

    readonly setTicketTemplates = this._ticketTemplates.setValueFunction();
    readonly getTicketTemplates$ = this._ticketTemplates.getValueFunction();
    readonly setTicketTemplatesInProgress = this._ticketTemplates.setInProgressFunction();
    readonly isTicketTemplatesInProgress$ = this._ticketTemplates.getInProgressFunction();

    private readonly _ticketTemplatesSaving = new BaseStateProp<void>();
    readonly setTicketTemplatesSaving = this._ticketTemplatesSaving.setInProgressFunction();
    readonly isTicketTemplatesSaving$ = this._ticketTemplatesSaving.getInProgressFunction();

    private readonly _ticketPdfContentTexts: BaseStateProp<SeasonTicketTicketContentText[]> =
        new BaseStateProp<SeasonTicketTicketContentText[]>();

    readonly setTicketPdfContentTexts = this._ticketPdfContentTexts.setValueFunction();
    readonly getTicketPdfContentTexts$ = this._ticketPdfContentTexts.getValueFunction();
    readonly setTicketPdfContentTextsInProgress = this._ticketPdfContentTexts.setInProgressFunction();
    readonly isTicketPdfContentTextsInProgress$ = this._ticketPdfContentTexts.getInProgressFunction();

    private readonly _ticketPdfContentTextsSaving = new BaseStateProp<void>();
    readonly setTicketPdfContentTextsSaving = this._ticketPdfContentTextsSaving.setInProgressFunction();
    readonly isTicketPdfContentTextsSaving$ = this._ticketPdfContentTextsSaving.getInProgressFunction();

    private readonly _ticketPdfContentImages: BaseStateProp<SeasonTicketTicketContentImage[]> =
        new BaseStateProp<SeasonTicketTicketContentImage[]>();

    readonly setTicketPdfContentImages = this._ticketPdfContentImages.setValueFunction();
    readonly getTicketPdfContentImages$ = this._ticketPdfContentImages.getValueFunction();
    readonly setTicketPdfContentImagesInProgress = this._ticketPdfContentImages.setInProgressFunction();
    readonly isTicketPdfContentImagesInProgress$ = this._ticketPdfContentImages.getInProgressFunction();

    private readonly _ticketPdfContentImagesSaving = new BaseStateProp<void>();
    readonly setTicketPdfContentImagesSaving = this._ticketPdfContentImagesSaving.setInProgressFunction();
    readonly isTicketPdfContentImagesSaving$ = this._ticketPdfContentImagesSaving.getInProgressFunction();

    private readonly _ticketPdfContentImagesRemoving = new BaseStateProp<void>();
    readonly setTicketPdfContentImagesRemoving = this._ticketPdfContentImagesRemoving.setInProgressFunction();
    readonly isTicketPdfContentImagesRemoving$ = this._ticketPdfContentImagesRemoving.getInProgressFunction();

    private readonly _ticketPdfPreviewDownloading = new BaseStateProp<void>();
    readonly setTicketPdfPreviewDownloading = this._ticketPdfPreviewDownloading.setInProgressFunction();
    readonly isTicketPdfPreviewDownloading$ = this._ticketPdfPreviewDownloading.getInProgressFunction();

    private readonly _ticketPrinterContentTexts: BaseStateProp<SeasonTicketTicketContentText[]> =
        new BaseStateProp<SeasonTicketTicketContentText[]>();

    readonly setTicketPrinterContentTexts = this._ticketPrinterContentTexts.setValueFunction();
    readonly getTicketPrinterContentTexts$ = this._ticketPrinterContentTexts.getValueFunction();
    readonly setTicketPrinterContentTextsInProgress = this._ticketPrinterContentTexts.setInProgressFunction();
    readonly isTicketPrinterContentTextsInProgress$ = this._ticketPrinterContentTexts.getInProgressFunction();

    private readonly _ticketPrinterContentTextsSaving = new BaseStateProp<void>();
    readonly setTicketPrinterContentTextsSaving = this._ticketPrinterContentTextsSaving.setInProgressFunction();
    readonly isTicketPrinterContentTextsSaving$ = this._ticketPrinterContentTextsSaving.getInProgressFunction();

    private readonly _ticketPrinterContentImages: BaseStateProp<SeasonTicketTicketContentImage[]> =
        new BaseStateProp<SeasonTicketTicketContentImage[]>();

    readonly setTicketPrinterContentImages = this._ticketPrinterContentImages.setValueFunction();
    readonly getTicketPrinterContentImages$ = this._ticketPrinterContentImages.getValueFunction();
    readonly setTicketPrinterContentImagesInProgress = this._ticketPrinterContentImages.setInProgressFunction();
    readonly isTicketPrinterContentImagesInProgress$ = this._ticketPrinterContentImages.getInProgressFunction();

    private readonly _ticketPrinterContentImagesSaving = new BaseStateProp<void>();
    readonly setTicketPrinterContentImagesSaving = this._ticketPrinterContentImagesSaving.setInProgressFunction();
    readonly isTicketPrinterContentImagesSaving$ = this._ticketPrinterContentImagesSaving.getInProgressFunction();

    private readonly _ticketPrinterContentImagesRemoving = new BaseStateProp<void>();
    readonly setTicketPrinterContentImagesRemoving = this._ticketPrinterContentImagesRemoving.setInProgressFunction();
    readonly isTicketPrinterContentImagesRemoving$ = this._ticketPrinterContentImagesRemoving.getInProgressFunction();

    private readonly _ticketPassbookContentImages: BaseStateProp<SeasonTicketTicketContentImage[]> =
        new BaseStateProp<SeasonTicketTicketContentImage[]>();

    readonly setTicketPassbookContentImages = this._ticketPassbookContentImages.setValueFunction();
    readonly getTicketPassbookContentImages$ = this._ticketPassbookContentImages.getValueFunction();
    readonly setTicketPassbookContentImagesInProgress = this._ticketPassbookContentImages.setInProgressFunction();
    readonly isTicketPassbookContentImagesInProgress$ = this._ticketPassbookContentImages.getInProgressFunction();

    private readonly _ticketPassbookContentImagesSaving = new BaseStateProp<void>();
    readonly setTicketPassbookContentImagesSaving = this._ticketPassbookContentImagesSaving.setInProgressFunction();
    readonly isTicketPassbookContentImagesSaving$ = this._ticketPassbookContentImagesSaving.getInProgressFunction();

    private readonly _ticketPassbookContentImagesRemoving = new BaseStateProp<void>();
    readonly setTicketPassbookContentImagesRemoving = this._ticketPassbookContentImagesRemoving.setInProgressFunction();
    readonly isTicketPassbookContentImagesRemoving$ = this._ticketPassbookContentImagesRemoving.getInProgressFunction();

    private _ticketPassbookContentTexts: BaseStateProp<SeasonTicketTicketContentText[]> =
        new BaseStateProp<SeasonTicketTicketContentText[]>();

    readonly setTicketPassbookContentTexts = this._ticketPassbookContentTexts.setValueFunction();
    readonly getTicketPassbookContentTexts$ = this._ticketPassbookContentTexts.getValueFunction();
    readonly setTicketPassbookContentTextsInProgress = this._ticketPassbookContentTexts.setInProgressFunction();
    readonly isTicketPassbookContentTextsInProgress$ = this._ticketPassbookContentTexts.getInProgressFunction();

    private _ticketPassbookContentTextsSaving = new BaseStateProp<void>();
    readonly setTicketPassbookContentTextsSaving = this._ticketPassbookContentTextsSaving.setInProgressFunction();
    readonly isTicketPassbookContentTextsSaving$ = this._ticketPassbookContentTextsSaving.getInProgressFunction();

    private _getDownloadUrlPassbookPreviewDownloading = new BaseStateProp<void>();
    readonly setDownloadUrlPassbookPreviewDownloadingInProgress = this._getDownloadUrlPassbookPreviewDownloading.setInProgressFunction();
    readonly isDownloadUrlPassbookPreviewInProgress$ = this._getDownloadUrlPassbookPreviewDownloading.getInProgressFunction();
}
