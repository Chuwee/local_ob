import { BaseStateProp } from '@admin-clients/shared/utility/state';
import { Injectable } from '@angular/core';
import { GetTicketTemplatesResponse } from '../models/get-ticket-templates-response.model';
import { GetTicketTemplateImage } from '../models/ticket-template-image.model';
import { TicketTemplateText } from '../models/ticket-template-text.model';
import { TicketTemplate, TicketTemplateDesign } from '../models/ticket-template.model';

@Injectable({
    providedIn: 'root'
})
export class TicketTemplatesState {
    // Ticket templates list
    private _ticketTemplatesList: BaseStateProp<GetTicketTemplatesResponse> = new BaseStateProp<GetTicketTemplatesResponse>();
    readonly setTicketTemplatesList = this._ticketTemplatesList.setValueFunction();
    readonly getTicketTemplatesList$ = this._ticketTemplatesList.getValueFunction();
    readonly setTicketTemplatesListInProgress = this._ticketTemplatesList.setInProgressFunction();
    readonly isTicketTemplatesListInProgress$ = this._ticketTemplatesList.getInProgressFunction();
    // Ticket template
    private _ticketTemplate: BaseStateProp<TicketTemplate> = new BaseStateProp<TicketTemplate>();
    readonly setTicketTemplate = this._ticketTemplate.setValueFunction();
    readonly getTicketTemplate$ = this._ticketTemplate.getValueFunction();
    readonly setTicketTemplateError = this._ticketTemplate.setErrorFunction();
    readonly getTicketTemplateError$ = this._ticketTemplate.getErrorFunction();
    readonly setTicketTemplateInProgress = this._ticketTemplate.setInProgressFunction();
    readonly isTicketTemplateInProgress$ = this._ticketTemplate.getInProgressFunction();
    // DESGINS LIST
    private _designsList: BaseStateProp<TicketTemplateDesign[]> = new BaseStateProp<TicketTemplateDesign[]>();
    readonly setDesignsList = this._designsList.setValueFunction();
    readonly getDesignsList$ = this._designsList.getValueFunction();
    readonly setDesignsListInProgress = this._designsList.setInProgressFunction();
    readonly isDesignsListInProgress$ = this._designsList.getInProgressFunction();
    // PRINTERS LIST
    private _printersList: BaseStateProp<string[]> = new BaseStateProp<string[]>();
    readonly setPrintersList = this._printersList.setValueFunction();
    readonly getPrintersList$ = this._printersList.getValueFunction();
    readonly setPrintersListInProgress = this._printersList.setInProgressFunction();
    readonly isPrintersListInProgress$ = this._printersList.getInProgressFunction();
    // PAPERS LIST
    private _paperTypesList: BaseStateProp<string[]> = new BaseStateProp<string[]>();
    readonly setPaperTypesList = this._paperTypesList.setValueFunction();
    readonly getPaperTypesList$ = this._paperTypesList.getValueFunction();
    readonly setPaperTypesListInProgress = this._paperTypesList.setInProgressFunction();
    readonly isPaperTypesListInProgress$ = this._paperTypesList.getInProgressFunction();
    // TEXTS CONTENTS
    private _ticketTemplateTexts = new BaseStateProp<TicketTemplateText[]>();
    readonly setTicketTemplateTexts = this._ticketTemplateTexts.setValueFunction();
    readonly getTicketTemplateTexts$ = this._ticketTemplateTexts.getValueFunction();
    readonly setTicketTemplateTextsInProgress = this._ticketTemplateTexts.setInProgressFunction();
    readonly isTicketTemplateTextsInProgress$ = this._ticketTemplateTexts.getInProgressFunction();
    readonly setTicketTemplateTextsError = this._ticketTemplateTexts.setErrorFunction();
    readonly getTicketTemplateTextsError$ = this._ticketTemplateTexts.getErrorFunction();
    // LITERALS
    private _ticketTemplateLiterals = new BaseStateProp<TicketTemplateText[]>();
    readonly setTicketTemplateLiterals = this._ticketTemplateLiterals.setValueFunction();
    readonly getTicketTemplateLiterals$ = this._ticketTemplateLiterals.getValueFunction();
    readonly setTicketTemplateLiteralsInProgress = this._ticketTemplateLiterals.setInProgressFunction();
    readonly isTicketTemplateLiteralsInProgress$ = this._ticketTemplateLiterals.getInProgressFunction();
    readonly setTicketTemplateLiteralsError = this._ticketTemplateLiterals.setErrorFunction();
    readonly getTicketTemplateLiteralsError$ = this._ticketTemplateLiterals.getErrorFunction();
    // IMAGES
    private _ticketTemplateImages = new BaseStateProp<GetTicketTemplateImage[]>();
    readonly setTicketTemplateImages = this._ticketTemplateImages.setValueFunction();
    readonly getTicketTemplateImages$ = this._ticketTemplateImages.getValueFunction();
    readonly setTicketTemplateImagesInProgress = this._ticketTemplateImages.setInProgressFunction();
    readonly isTicketTemplateImagesInProgress$ = this._ticketTemplateImages.getInProgressFunction();
    readonly setTicketTemplateImagesError = this._ticketTemplateImages.setErrorFunction();
    readonly getTicketTemplateImagesError$ = this._ticketTemplateImages.getErrorFunction();
    // TICKET PREVIEW
    private _ticketPdfPreviewDownloading = new BaseStateProp<void>();
    readonly setTicketPdfPreviewDownloading = this._ticketPdfPreviewDownloading.setInProgressFunction();
    readonly isTicketPdfPreviewDownloading$ = this._ticketPdfPreviewDownloading.getInProgressFunction();
    // CLONE TICKET TEMPLATE
    private _ticketTemplateCloning = new BaseStateProp<void>();
    readonly setTicketTemplateCloning = this._ticketTemplateCloning.setInProgressFunction();
    readonly isTicketTemplateCloning$ = this._ticketTemplateCloning.getInProgressFunction();
}
