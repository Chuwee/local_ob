import {
    TicketContentImage, TicketContentImageRequest, TicketContentImageType, TicketContentText
} from '@admin-clients/cpanel/promoters/events/data-access';
import { TicketType } from '@admin-clients/shared/common/data-access';
import { inject, Injectable } from '@angular/core';
import { Observable, of, Subject, switchMap, zip } from 'rxjs';
import { catchError, finalize, takeUntil } from 'rxjs/operators';
import { EventCommunicationApi } from './api/event-communication.api';
import { EventChannelContentImageType } from './models/event-channel-content-image-type.enum';
import { EventChannelContentImage } from './models/event-channel-content-image.model';
import { EventChannelContentImageRequest } from './models/event-channel-content-image.request.model';
import { EventChannelContentText } from './models/event-channel-content-text.model';
import { EventTicketTemplateType } from './models/event-ticket-template-type.enum';
import { EventTicketTemplate } from './models/event-ticket-template.model';
import { TicketContentFormat } from './models/ticket-content-format.enum';
import { EventCommunicationState } from './state/event-communication.state';

@Injectable({
    providedIn: 'root'
})
export class EventCommunicationService {
    private _api = inject(EventCommunicationApi);
    private _state = inject(EventCommunicationState);

    private _cancelRequests = new Subject<void>();

    cancelRequests(): void {
        this._cancelRequests.next();
    }

    loadEventChannelContentTexts(eventId: number): void {
        this._state.setChannelContentTextsInProgress(true);
        this._api.getEventChannelContentTexts(eventId)
            .pipe(
                catchError(() => of(null)),
                finalize(() => this._state.setChannelContentTextsInProgress(false)),
                takeUntil(this._cancelRequests)
            )
            .subscribe(contents => this._state.setChannelContentTexts(contents));
    }

    getEventChannelContentTexts$(): Observable<EventChannelContentText[]> {
        return this._state.getChannelContentTexts$();
    }

    isEventChannelContentTextsLoading$(): Observable<boolean> {
        return this._state.isChannelContentTextsInProgress$();
    }

    saveEventChannelContentTexts(eventId: number, textsToSave: EventChannelContentText[]): Observable<void> {
        this._state.setChannelContentTextsSaving(true);
        return this._api.postEventChannelContentTexts(eventId, textsToSave)
            .pipe(finalize(() => this._state.setChannelContentTextsSaving(false)));
    }

    isEventChannelContentTextsSaving$(): Observable<boolean> {
        return this._state.isChannelContentTextsSaving$();
    }

    clearEventChannelContentTexts(): void {
        this._state.setChannelContentTexts(null);
    }

    loadEventChannelContentImages(eventId: number, language?: string, type?: EventChannelContentImageType): void {
        this._state.setChannelContentImagesInProgress(true);
        this._api.getEventChannelContentImages(eventId, language, type)
            .pipe(
                catchError(() => of(null)),
                finalize(() => this._state.setChannelContentImagesInProgress(false)),
                takeUntil(this._cancelRequests)
            )
            .subscribe(contents => this._state.setChannelContentImages(contents));
    }

    clearEventChannelContentImages(): void {
        this._state.setChannelContentImages(null);
    }

    getEventChannelContentImages$(): Observable<EventChannelContentImage[]> {
        return this._state.getChannelContentImages$();
    }

    isEventChannelContentImagesLoading$(): Observable<boolean> {
        return this._state.isChannelContentImagesInProgress$();
    }

    saveEventChannelContentImages(eventId: number, imagesToSave: EventChannelContentImageRequest[]): Observable<void> {
        this._state.setChannelContentImagesSaving(true);
        return this._api.postEventChannelContentImages(eventId, imagesToSave)
            .pipe(finalize(() => this._state.setChannelContentImagesSaving(false)));
    }

    isEventChannelContentImagesSaving$(): Observable<boolean> {
        return this._state.isChannelContentImagesSaving$();
    }

    deleteEventChannelContentImages(eventId: number, imagesToDelete: EventChannelContentImageRequest[]): Observable<void> {
        this._state.setChannelContentImagesRemoving(true);
        return zip(
            ...imagesToDelete.map(request =>
                this._api.deleteEventChannelContentImage(eventId, request.language, request.type, request.position)
            )
        ).pipe(
            switchMap(() => of(null)),
            catchError(() => of(null)),
            finalize(() => this._state.setChannelContentImagesRemoving(false))
        );
    }

    isEventChannelContentImagesRemoving$(): Observable<boolean> {
        return this._state.isChannelContentImagesRemoving$();
    }

    loadEventTicketTemplates(eventId: number): void {
        this._state.setTicketTemplatesInProgress(true);
        this._api.getEventTicketTemplates(eventId)
            .pipe(
                catchError(() => of(null)),
                finalize(() => this._state.setTicketTemplatesInProgress(false))
            )
            .subscribe(templates => this._state.setTicketTemplates(templates));
    }

    getEventTicketTemplates$(): Observable<EventTicketTemplate[]> {
        return this._state.getTicketTemplates$();
    }

    isEventTicketTemplatesLoading$(): Observable<boolean> {
        return this._state.isTicketTemplatesInProgress$();
    }

    saveEventTicketTemplates(eventId: number, templatesToSave: EventTicketTemplate[]): Observable<void> {
        this._state.setTicketTemplatesSaving(true);
        return zip(
            ...templatesToSave.map(template => {
                const isPassbook = template.format === TicketContentFormat.passbook;
                const templateId = !isPassbook ? { id: template.id } : { code: template.id.toString() };
                return this._api.postEventTicketTemplate(eventId, templateId, template.type, template.format);
            })
        ).pipe(
            switchMap(() => of(null)),
            catchError(() => of(null)),
            finalize(() => this._state.setTicketTemplatesSaving(false))
        );
    }

    isEventTicketTemplatesSaving$(): Observable<boolean> {
        return this._state.isTicketTemplatesSaving$();
    }

    loadEventTicketPdfContentTexts(ticketType: TicketType, eventId: number): void {
        this._state.setTicketPdfContentTextsInProgress(true);
        this._api.getEventTicketContentTexts(ticketType, eventId, TicketContentFormat.pdf)
            .pipe(
                catchError(() => of(null)),
                finalize(() => this._state.setTicketPdfContentTextsInProgress(false)),
                takeUntil(this._cancelRequests)
            )
            .subscribe(contents => this._state.setTicketPdfContentTexts(contents));
    }

    getEventTicketPdfContentTexts$(): Observable<TicketContentText[]> {
        return this._state.getTicketPdfContentTexts$();
    }

    isEventTicketPdfContentTextsLoading$(): Observable<boolean> {
        return this._state.isTicketPdfContentTextsInProgress$();
    }

    saveEventTicketPdfContentTexts(ticketType: TicketType, eventId: number, textsToSave: TicketContentText[]): Observable<void> {
        this._state.setTicketPdfContentTextsSaving(true);
        return this._api.postEventTicketContentTexts(ticketType, eventId, TicketContentFormat.pdf, textsToSave)
            .pipe(finalize(() => this._state.setTicketPdfContentTextsSaving(false)));
    }

    isEventTicketPdfContentTextsSaving$(): Observable<boolean> {
        return this._state.isTicketPdfContentTextsSaving$();
    }

    loadEventTicketPdfContentImages(ticketType: TicketType, eventId: number, language?: string, type?: TicketContentImageType): void {
        this._state.setTicketPdfContentImagesInProgress(true);
        this._api.getEventTicketContentImages(ticketType, eventId, TicketContentFormat.pdf, language, type)
            .pipe(
                catchError(() => of(null)),
                finalize(() => this._state.setTicketPdfContentImagesInProgress(false)),
                takeUntil(this._cancelRequests)
            )
            .subscribe(contents => this._state.setTicketPdfContentImages(contents));
    }

    getEventTicketPdfContentImages$(): Observable<TicketContentImage[]> {
        return this._state.getTicketPdfContentImages$();
    }

    isEventTicketPdfContentImagesLoading$(): Observable<boolean> {
        return this._state.isTicketPdfContentImagesInProgress$();
    }

    saveEventTicketPdfContentImages(
        ticketType: TicketType, eventId: number, imagesToSave: TicketContentImageRequest[]
    ): Observable<void> {
        this._state.setTicketPdfContentImagesSaving(true);
        return this._api.postEventTicketContentImages(ticketType, eventId, TicketContentFormat.pdf, imagesToSave)
            .pipe(finalize(() => this._state.setTicketPdfContentImagesSaving(false)));
    }

    isEventTicketPdfContentImagesSaving$(): Observable<boolean> {
        return this._state.isTicketPdfContentImagesSaving$();
    }

    deleteEventTicketPdfContentImages(
        ticketType: TicketType, eventId: number, imagesToDelete: TicketContentImageRequest[]
    ): Observable<void> {
        this._state.setTicketPdfContentImagesRemoving(true);
        return zip(
            ...imagesToDelete.map(request =>
                this._api.deleteEventTicketContentImage(ticketType, eventId, TicketContentFormat.pdf, request.language, request.type)
            )
        ).pipe(
            switchMap(() => of(null)),
            catchError(() => of(null)),
            finalize(() => this._state.setTicketPdfContentImagesRemoving(false))
        );
    }

    isEventTicketPdfContentImagesRemoving$(): Observable<boolean> {
        return this._state.isTicketPdfContentImagesRemoving$();
    }

    loadEventTicketPrinterContentTexts(ticketType: TicketType, eventId: number): void {
        this._state.setTicketPrinterContentTextsInProgress(true);
        this._api.getEventTicketContentTexts(ticketType, eventId, TicketContentFormat.printer)
            .pipe(
                catchError(() => of(null)),
                finalize(() => this._state.setTicketPrinterContentTextsInProgress(false)),
                takeUntil(this._cancelRequests)
            )
            .subscribe(contents => this._state.setTicketPrinterContentTexts(contents));
    }

    getEventTicketPrinterContentTexts$(): Observable<TicketContentText[]> {
        return this._state.getTicketPrinterContentTexts$();
    }

    isEventTicketPrinterContentTextsLoading$(): Observable<boolean> {
        return this._state.isTicketPrinterContentTextsInProgress$();
    }

    saveEventTicketPrinterContentTexts(ticketType: TicketType, eventId: number, textsToSave: TicketContentText[]): Observable<void> {
        this._state.setTicketPrinterContentTextsSaving(true);
        return this._api.postEventTicketContentTexts(ticketType, eventId, TicketContentFormat.printer, textsToSave)
            .pipe(finalize(() => this._state.setTicketPrinterContentTextsSaving(false)));
    }

    isEventTicketPrinterContentTextsSaving$(): Observable<boolean> {
        return this._state.isTicketPrinterContentTextsSaving$();
    }

    loadEventTicketPassbookContentTexts(ticketType: TicketType, eventId: number): void {
        this._state.setTicketPassbookContentTextsInProgress(true);
        this._api.getEventTicketContentTexts(ticketType, eventId, TicketContentFormat.passbook)
            .pipe(
                catchError(() => of(null)),
                finalize(() => this._state.setTicketPassbookContentTextsInProgress(false)),
                takeUntil(this._cancelRequests)
            )
            .subscribe(contents => this._state.setTicketPassbookContentTexts(contents));
    }

    getEventTicketPassbookContentTexts$(): Observable<TicketContentText[]> {
        return this._state.getTicketPassbookContentTexts$();
    }

    isEventTicketPassbookContentTextsLoading$(): Observable<boolean> {
        return this._state.isTicketPassbookContentTextsInProgress$();
    }

    saveEventTicketPassbookContentTexts(ticketType: TicketType, eventId: number, textsToSave: TicketContentText[]): Observable<void> {
        this._state.setTicketPassbookContentTextsSaving(true);
        return this._api.postEventTicketContentTexts(ticketType, eventId, TicketContentFormat.passbook, textsToSave)
            .pipe(finalize(() => this._state.setTicketPassbookContentTextsSaving(false)));
    }

    isEventTicketPassbookContentTextsSaving$(): Observable<boolean> {
        return this._state.isTicketPassbookContentTextsSaving$();
    }

    loadEventTicketPrinterContentImages(
        ticketType: TicketType, eventId: number, language?: string, type?: TicketContentImageType
    ): void {
        this._state.setTicketPrinterContentImagesInProgress(true);
        this._api.getEventTicketContentImages(ticketType, eventId, TicketContentFormat.printer, language, type)
            .pipe(
                catchError(() => of(null)),
                finalize(() => this._state.setTicketPrinterContentImagesInProgress(false)),
                takeUntil(this._cancelRequests)
            )
            .subscribe(contents => this._state.setTicketPrinterContentImages(contents));
    }

    getEventTicketPrinterContentImages$(): Observable<TicketContentImage[]> {
        return this._state.getTicketPrinterContentImages$();
    }

    isEventTicketPrinterContentImagesLoading$(): Observable<boolean> {
        return this._state.isTicketPrinterContentImagesInProgress$();
    }

    saveEventTicketPrinterContentImages(
        ticketType: TicketType, eventId: number, imagesToSave: TicketContentImageRequest[]
    ): Observable<void> {
        this._state.setTicketPrinterContentImagesSaving(true);
        return this._api.postEventTicketContentImages(ticketType, eventId, TicketContentFormat.printer, imagesToSave)
            .pipe(finalize(() => this._state.setTicketPrinterContentImagesSaving(false)));
    }

    isEventTicketPrinterContentImagesSaving$(): Observable<boolean> {
        return this._state.isTicketPrinterContentImagesSaving$();
    }

    deleteEventTicketPrinterContentImages(
        ticketType: TicketType, eventId: number, imagesToDelete: TicketContentImageRequest[]
    ): Observable<void> {
        this._state.setTicketPrinterContentImagesRemoving(true);
        return zip(
            ...imagesToDelete.map(request =>
                this._api.deleteEventTicketContentImage(ticketType, eventId, TicketContentFormat.printer, request.language, request.type)
            )
        ).pipe(
            switchMap(() => of(null)),
            catchError(() => of(null)),
            finalize(() => this._state.setTicketPrinterContentImagesRemoving(false))
        );
    }

    isEventTicketPrinterContentImagesRemoving$(): Observable<boolean> {
        return this._state.isTicketPrinterContentImagesRemoving$();
    }

    loadEventTicketPassbookContentImages(
        ticketType: TicketType, eventId: number, language?: string, type?: TicketContentImageType
    ): void {
        this._state.setTicketPassbookContentImagesInProgress(true);
        this._api.getEventTicketContentImages(ticketType, eventId, TicketContentFormat.passbook, language, type)
            .pipe(
                catchError(() => of(null)),
                finalize(() => this._state.setTicketPassbookContentImagesInProgress(false)),
                takeUntil(this._cancelRequests)
            )
            .subscribe(contents => this._state.setTicketPassbookContentImages(contents));
    }

    getEventTicketPassbookContentImages$(): Observable<TicketContentImage[]> {
        return this._state.getTicketPassbookContentImages$();
    }

    isEventTicketPassbookContentImagesLoading$(): Observable<boolean> {
        return this._state.isTicketPassbookContentImagesInProgress$();
    }

    saveEventTicketPassbookContentImages(
        ticketType: TicketType, eventId: number, imagesToSave: TicketContentImageRequest[]
    ): Observable<void> {
        this._state.setTicketPassbookContentImagesSaving(true);
        return this._api.postEventTicketContentImages(ticketType, eventId, TicketContentFormat.passbook, imagesToSave)
            .pipe(finalize(() => this._state.setTicketPassbookContentImagesSaving(false)));
    }

    isEventTicketPassbookContentImagesSaving$(): Observable<boolean> {
        return this._state.isTicketPassbookContentImagesSaving$();
    }

    deleteEventTicketPassbookContentImages(
        ticketType: TicketType, eventId: number, imagesToDelete: TicketContentImageRequest[]
    ): Observable<void> {
        this._state.setTicketPassbookContentImagesRemoving(true);
        return zip(
            ...imagesToDelete.map(request =>
                this._api.deleteEventTicketContentImage(ticketType, eventId, TicketContentFormat.passbook, request.language, request.type)
            )
        ).pipe(
            switchMap(() => of(null)),
            catchError(() => of(null)),
            finalize(() => this._state.setTicketPassbookContentImagesRemoving(false))
        );
    }

    isEventTicketPassbookContentImagesRemoving$(): Observable<boolean> {
        return this._state.isTicketPassbookContentImagesRemoving$();
    }

    downloadTicketPdfPreview$(eventId: number, type: EventTicketTemplateType, language: string): Observable<{ url: string }> {
        this._state.setTicketPdfPreviewDownloading(true);
        return this._api.downloadTicketPdfPreview$(eventId, type, language)
            .pipe(finalize(() => this._state.setTicketPdfPreviewDownloading(false)));
    }

    isTicketPdfPreviewDownloading$(): Observable<boolean> {
        return this._state.isTicketPdfPreviewDownloading$();
    }

    getDownloadUrlPassbookPreview$(eventId: number): Observable<{ download_url: string }> {
        this._state.setDownloadUrlPassbookPreviewDownloadingInProgress(true);
        return this._api.getDownloadUrlPassbookPreview$(eventId)
            .pipe(finalize(() => this._state.setDownloadUrlPassbookPreviewDownloadingInProgress(false)));
    }

    isDownloadUrlPassbookPreviewInProgress$(): Observable<boolean> {
        return this._state.isDownloadUrlPassbookPreviewInProgress$();
    }
}
