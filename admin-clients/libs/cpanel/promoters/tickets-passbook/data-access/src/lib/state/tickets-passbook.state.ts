import { Injectable } from '@angular/core';
import { BaseStateProp } from '@admin-clients/shared/utility/state';
import { GetTicketPassbookResponse } from '../models/get-ticket-passbook-response.model';
import { TicketPassbookAvailableFields } from '../models/ticket-passbook-available-fields.model';
import { TicketPassbookLiterals } from '../models/ticket-passbook-literals.model';
import { TicketPassbook } from '../models/ticket-passbook.model';

@Injectable({
    providedIn: 'root'
})
export class TicketsPassbookState {

    // Ticket passbook list
    private _ticketPassbookList = new BaseStateProp<GetTicketPassbookResponse>();
    readonly setTicketPassbookList = this._ticketPassbookList.setValueFunction();
    readonly getTicketPassbookList$ = this._ticketPassbookList.getValueFunction();
    readonly setTicketPassbookListInProgress = this._ticketPassbookList.setInProgressFunction();
    readonly isTicketPassbookListInProgress$ = this._ticketPassbookList.getInProgressFunction();
    // Ticket passbook saving
    private _ticketPassbookSaving = new BaseStateProp<boolean>();
    readonly setTicketPassbookSaving = this._ticketPassbookSaving.setInProgressFunction();
    readonly isTicketPassbookSaving$ = this._ticketPassbookSaving.getInProgressFunction();
    // Ticket passbook
    private _ticketPassbook = new BaseStateProp<TicketPassbook>();
    readonly setTicketPassbook = this._ticketPassbook.setValueFunction();
    readonly getTicketPassbook$ = this._ticketPassbook.getValueFunction();
    readonly setTicketPassbookLoading = this._ticketPassbook.setInProgressFunction();
    readonly isTicketPassbookLoading$ = this._ticketPassbook.getInProgressFunction();
    readonly getTicketPassbookError$ = this._ticketPassbook.getErrorFunction();
    readonly setTicketPassbookError = this._ticketPassbook.setErrorFunction();
    // Ticket passbook template literals
    private _ticketPassbookTemplateLiterals = new BaseStateProp<TicketPassbookLiterals>();
    readonly setTicketPassbookTemplateLiterals = this._ticketPassbookTemplateLiterals.setValueFunction();
    readonly getTicketPassbookTemplateLiterals$ = this._ticketPassbookTemplateLiterals.getValueFunction();
    readonly setTicketPassbookTemplateLiteralsInProgress = this._ticketPassbookTemplateLiterals.setInProgressFunction();
    readonly isTicketPassbookTemplateLiteralsInProgress$ = this._ticketPassbookTemplateLiterals.getInProgressFunction();
    // Ticket passbook template literals saving
    private _ticketPassbookTemplateLiteralsSaving = new BaseStateProp<boolean>();
    readonly setTicketPassbookTemplateLiteralsSaving = this._ticketPassbookTemplateLiteralsSaving.setInProgressFunction();
    readonly isTicketPassbookTemplateLiteralsSaving$ = this._ticketPassbookTemplateLiteralsSaving.getInProgressFunction();
    // Ticket passbook template downloading
    private _downloadPassbookPreviewDownloadUrl = new BaseStateProp<{ download_url: string }>();
    readonly setTicketPassbookPreviewDownloadUrl = this._downloadPassbookPreviewDownloadUrl.setInProgressFunction();
    readonly isTicketPassbookPreviewDownloadUrl$ = this._downloadPassbookPreviewDownloadUrl.getInProgressFunction();
    // Ticket passbook available fields
    private _ticketPassbookAvailableFields = new BaseStateProp<TicketPassbookAvailableFields[]>();
    readonly setTicketPassbookAvailableFields = this._ticketPassbookAvailableFields.setValueFunction();
    readonly getTicketPassbookAvailableFields$ = this._ticketPassbookAvailableFields.getValueFunction();
    readonly setTicketPassbookAvailableFieldsInProgress = this._ticketPassbookAvailableFields.setInProgressFunction();
    readonly isTicketPassbookAvailableFieldsInProgress$ = this._ticketPassbookAvailableFields.getInProgressFunction();

    // Ticket passbook available fields
    private _ticketPassbookAvailableCustomLiterals = new BaseStateProp<string[]>();
    readonly setTicketPassbookAvailableCustomLiterals = this._ticketPassbookAvailableCustomLiterals.setValueFunction();
    readonly getTicketPassbookAvailableCustomLiterals$ = this._ticketPassbookAvailableCustomLiterals.getValueFunction();
    readonly setTicketPassbookAvailableCustomLiteralsInProgress = this._ticketPassbookAvailableCustomLiterals.setInProgressFunction();
    readonly isTicketPassbookAvailableCustomLiteralsInProgress$ = this._ticketPassbookAvailableCustomLiterals.getInProgressFunction();

    // Ticket passbook available placeholders
    private _ticketPassbookAvailableCustomPlaceholders = new BaseStateProp<string[]>();
    readonly setTicketPassbookAvailableCustomPlaceholders = this._ticketPassbookAvailableCustomPlaceholders.setValueFunction();
    readonly getTicketPassbookAvailableCustomPlaceholders$ = this._ticketPassbookAvailableCustomPlaceholders.getValueFunction();
    readonly setTicketPassbookAvailableCustomPlaceholdersInProgress
        = this._ticketPassbookAvailableCustomPlaceholders.setInProgressFunction();

    readonly isTicketPassbookAvailableCustomPlaceholdersInProgress$
        = this._ticketPassbookAvailableCustomPlaceholders.getInProgressFunction();

}
