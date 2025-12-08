import { Metadata } from '@OneboxTM/utils-state';
import { HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, finalize, map, switchMap } from 'rxjs/operators';
import { TicketsPassbookApi } from './api/tickets-passbook.api';
import { GetTicketPassbookRequest } from './models/get-ticket-passbook-request.model';
import { PostTicketPassbok } from './models/post-ticket-passbook.model';
import { TicketPassbookAvailableFields } from './models/ticket-passbook-available-fields.model';
import { TicketPassbookLiterals } from './models/ticket-passbook-literals.model';
import { TicketPassbookType } from './models/ticket-passbook-type.enum';
import { PutTicketPassbook, TicketPassbook } from './models/ticket-passbook.model';
import { TicketsPassbookState } from './state/tickets-passbook.state';

@Injectable({
    providedIn: 'root'
})
export class TicketsPassbookService {

    constructor(
        private _ticketsPassbookApi: TicketsPassbookApi,
        private _ticketPassbookState: TicketsPassbookState
    ) { }

    loadTicketPassbookList(request: GetTicketPassbookRequest): void {
        this._ticketPassbookState.setTicketPassbookListInProgress(true);
        this._ticketsPassbookApi.getTicketsPassbook(request)
            .pipe(
                catchError(() => of(null)),
                finalize(() => this._ticketPassbookState.setTicketPassbookListInProgress(false))
            ).subscribe(response =>
                this._ticketPassbookState.setTicketPassbookList(response)
            );
    }

    getTicketPassbookListData$(): Observable<TicketPassbook[]> {
        return this._ticketPassbookState.getTicketPassbookList$()
            .pipe(map(ticketPassbook => ticketPassbook?.data));
    }

    getTicketPassbookListMetadata$(): Observable<Metadata> {
        return this._ticketPassbookState.getTicketPassbookList$()
            .pipe(map(ticketPassbook =>
                ticketPassbook?.metadata && Object.assign(new Metadata(), ticketPassbook.metadata)));
    }

    isTicketsPassbookListInProgress$(): Observable<boolean> {
        return this._ticketPassbookState.isTicketPassbookListInProgress$();
    }

    deleteTicketPassbook(id: string, entityId: string): Observable<void> {
        return this._ticketsPassbookApi.deleteTicketPassbook(id, entityId);
    }

    createTicketPassbook(ticketPassbook: PostTicketPassbok): Observable<number> {
        this._ticketPassbookState.setTicketPassbookSaving(true);
        return this._ticketsPassbookApi.postTicketPassbook(ticketPassbook)
            .pipe(
                map(result => result.code),
                finalize(() => this._ticketPassbookState.setTicketPassbookSaving(false))
            );
    }

    isTicketPassbookSaving$(): Observable<boolean> {
        return this._ticketPassbookState.isTicketPassbookSaving$();
    }

    clearTicketPassbook(): void {
        this._ticketPassbookState.setTicketPassbook(null);
    }

    loadTicketPassbook(ticketPassbookId: string, entityId: string): void {
        this._ticketPassbookState.setTicketPassbookError(null);
        this._ticketPassbookState.setTicketPassbookLoading(true);
        this._ticketsPassbookApi.getTicketPassbook(ticketPassbookId, entityId)
            .pipe(
                catchError(error => {
                    this._ticketPassbookState.setTicketPassbookError(error);
                    return of(null);
                }),
                finalize(() => this._ticketPassbookState.setTicketPassbookLoading(false))
            )
            .subscribe(event =>
                this._ticketPassbookState.setTicketPassbook(event)
            );
    }

    getTicketPassbook$(): Observable<TicketPassbook> {
        return this._ticketPassbookState.getTicketPassbook$();
    }

    getTicketPassbookError$(): Observable<HttpErrorResponse> {
        return this._ticketPassbookState.getTicketPassbookError$();
    }

    isTicketPassbookLoading$(): Observable<boolean> {
        return this._ticketPassbookState.isTicketPassbookLoading$();
    }

    updateTicketPassbook(ticketPassbook: PutTicketPassbook, entityId: string): Observable<void> {
        this._ticketPassbookState.setTicketPassbookSaving(true);
        return this._ticketsPassbookApi.putTicketPassbook(ticketPassbook, entityId)
            .pipe(finalize(() => this._ticketPassbookState.setTicketPassbookSaving(false)));
    }

    updateTicketPassbookDefault(ticketPassBookId: { code: string; entityId: number }, isDefault: boolean): Observable<boolean> {
        const putTicketPassbook: PutTicketPassbook = {
            code: ticketPassBookId.code,
            default_passbook: isDefault
        };
        return this._ticketsPassbookApi.putTicketPassbook(putTicketPassbook, ticketPassBookId.entityId.toString())
            .pipe(switchMap(() => of(true)), catchError(() => of(false)));
    }

    loadTicketPassbookTemplateLiterals(ticketPassbookCode: string, langCode: string, entityId: string): void {
        this._ticketPassbookState.setTicketPassbookTemplateLiteralsInProgress(true);
        this._ticketsPassbookApi.getTicketPassbookTemplateLiterals(ticketPassbookCode, langCode, entityId)
            .pipe(finalize(() => this._ticketPassbookState.setTicketPassbookTemplateLiteralsInProgress(false)))
            .subscribe(ticketPassbookTemplateLiterals =>
                this._ticketPassbookState.setTicketPassbookTemplateLiterals(ticketPassbookTemplateLiterals)
            );
    }

    getTicketPassbookTemplateLiterals$(): Observable<TicketPassbookLiterals> {
        return this._ticketPassbookState.getTicketPassbookTemplateLiterals$();
    }

    isTicketPassbookTemplateLiteralsInProgress$(): Observable<boolean> {
        return this._ticketPassbookState.isTicketPassbookTemplateLiteralsInProgress$();
    }

    clearTicketPassbookTemplateLiterals(): void {
        this._ticketPassbookState.setTicketPassbookTemplateLiterals(null);
    }

    updateTicketPassbookTemplateLiterals(
        ticketPassbookTemplateLiterals: TicketPassbookLiterals[],
        ticketPassbookCode: string,
        langCode: string,
        entityId: string
    ): Observable<void> {
        this._ticketPassbookState.setTicketPassbookTemplateLiteralsSaving(true);
        return this._ticketsPassbookApi.putTicketPassbookTemplateLiterals(
            ticketPassbookTemplateLiterals, ticketPassbookCode, langCode, entityId
        )
            .pipe(finalize(() => this._ticketPassbookState.setTicketPassbookTemplateLiteralsSaving(false)));
    }

    isTicketPassbookTemplateLiteralsSaving$(): Observable<boolean> {
        return this._ticketPassbookState.isTicketPassbookTemplateLiteralsSaving$();
    }

    getDownloadUrlTicketPassbookPreview$(passbookCode: string, entityId: string, language?: string): Observable<{ download_url: string }> {
        this._ticketPassbookState.setTicketPassbookPreviewDownloadUrl(true);
        return this._ticketsPassbookApi.getDownloadUrlTicketPassbookPreview$(passbookCode, entityId, language).pipe(
            finalize(() => this._ticketPassbookState.setTicketPassbookPreviewDownloadUrl(false))
        );
    }

    isTicketPassbookPreviewDownloadUrl$(): Observable<boolean> {
        return this._ticketPassbookState.isTicketPassbookPreviewDownloadUrl$();
    }

    loadTicketPassbookAvailableFields(type: TicketPassbookType): void {
        this._ticketPassbookState.setTicketPassbookAvailableFieldsInProgress(true);
        this._ticketsPassbookApi.getTicketPassbookAvailableFields(type)
            .pipe(finalize(() => this._ticketPassbookState.setTicketPassbookAvailableFieldsInProgress(false)))
            .subscribe(ticketPassbookAvailableFields =>
                this._ticketPassbookState.setTicketPassbookAvailableFields(ticketPassbookAvailableFields)
            );
    }

    getTicketPassbookAvailableFields$(): Observable<TicketPassbookAvailableFields[]> {
        return this._ticketPassbookState.getTicketPassbookAvailableFields$();
    }

    clearTicketPassbookAvailableFields(): void {
        this._ticketPassbookState.setTicketPassbookAvailableFields(null);
    }

    isTicketPassbookAvailableFieldsInProgress$(): Observable<boolean> {
        return this._ticketPassbookState.isTicketPassbookAvailableFieldsInProgress$();
    }

    loadTicketPassbookCustomTemplateLiterals(): void {
        this._ticketPassbookState.setTicketPassbookAvailableCustomLiteralsInProgress(true);
        this._ticketsPassbookApi.getTicketPassbookCustomTemplateLiterals()
            .pipe(
                finalize(() => this._ticketPassbookState.setTicketPassbookAvailableCustomLiteralsInProgress(false))
            )
            .subscribe(this._ticketPassbookState.setTicketPassbookAvailableCustomLiterals);
    }

    getTicketPassbookAvailableCustomLiterals$(): Observable<string[]> {
        return this._ticketPassbookState.getTicketPassbookAvailableCustomLiterals$();
    }

    setTicketPassbookAvailableCustomLiterals$(value: string[]): void {
        this._ticketPassbookState.setTicketPassbookAvailableCustomLiterals(value);
    }

    isTicketPassbookAvailableCustomLiteralsInProgress$(): Observable<boolean> {
        return this._ticketPassbookState.isTicketPassbookAvailableCustomLiteralsInProgress$();
    }

    loadTicketPassbookCustomTemplatePlaceholders(type: TicketPassbookType): void {
        this._ticketPassbookState.setTicketPassbookAvailableCustomPlaceholdersInProgress(true);
        this._ticketsPassbookApi.getTicketPassbookCustomTemplatePlaceholders(type)
            .pipe(
                finalize(() => this._ticketPassbookState.setTicketPassbookAvailableCustomPlaceholdersInProgress(false))
            )
            .subscribe(this._ticketPassbookState.setTicketPassbookAvailableCustomPlaceholders);
    }

    getTicketPassbookAvailableCustomPlaceholders$(): Observable<string[]> {
        return this._ticketPassbookState.getTicketPassbookAvailableCustomPlaceholders$();
    }

    setTicketPassbookAvailableCustomPlaceholders$(value: string[]): void {
        this._ticketPassbookState.setTicketPassbookAvailableCustomPlaceholders(value);
    }

    isTicketPassbookAvailableCustomPlaceholdersInProgress$(): Observable<boolean> {
        return this._ticketPassbookState.isTicketPassbookAvailableCustomPlaceholdersInProgress$();
    }

}
