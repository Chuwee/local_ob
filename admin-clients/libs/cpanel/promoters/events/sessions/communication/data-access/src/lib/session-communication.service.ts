import { Injectable } from '@angular/core';
import { Observable, of, zip } from 'rxjs';
import { finalize, catchError } from 'rxjs/operators';
import { SessionCommunicationApi } from './api/sessions-communication.api';
import { SessionChannelImageRequest } from './models/session-channel-image-request.model';
import { SessionChannelImage } from './models/session-channel-image.model';
import { SessionChannelText } from './models/session-channel-text.model';
import { SessionTicketContentFormat } from './models/session-ticket-content-format.enum';
import { SessionTicketImageRequest } from './models/session-ticket-image-request.model';
import { SessionTicketImage } from './models/session-ticket-image.model';
import { SessionTicketText } from './models/session-ticket-text.model';
import { SessionCommunicationState } from './state/session-communication.state';

@Injectable({
    providedIn: 'root'
})
export class SessionCommunicationService {

    constructor(
        private _sessionsCommunicationApi: SessionCommunicationApi,
        private _sessionsCommunicationState: SessionCommunicationState
    ) { }

    // CONTENT
    loadChannelTexts(eventId: number, sessionId: number): void {
        this._sessionsCommunicationState.setChannelTextsInProgress(true);
        this._sessionsCommunicationApi.getChannelTexts(eventId, sessionId)
            .pipe(finalize(() => this._sessionsCommunicationState.setChannelTextsInProgress(false)))
            .subscribe(channelTexts => this._sessionsCommunicationState.setChannelTexts(channelTexts));
    }

    getChannelTexts$(): Observable<SessionChannelText[]> {
        return this._sessionsCommunicationState.getChannelTexts$();
    }

    updateChannelTexts(eventId: number, sessionsIds: number[], texts: SessionChannelText[]): Observable<void> {
        if (texts.length) {
            this._sessionsCommunicationState.setChannelTextsInProgress(true);
            return this._sessionsCommunicationApi.postChannelTexts(eventId, sessionsIds, texts)
                .pipe(finalize(() => this._sessionsCommunicationState.setChannelTextsInProgress(false)));
        }
        return of(null);
    }

    isChannelTextsInProgress$(): Observable<boolean> {
        return this._sessionsCommunicationState.isChannelTextsInProgress$();
    }

    clearChannelTexts(): void {
        this._sessionsCommunicationState.setChannelTexts(null);
    }

    loadChannelImages(eventId: number, sessionId: number): void {
        this._sessionsCommunicationState.setChannelImagesInProgress(true);
        this._sessionsCommunicationApi.getChannelImages(eventId, sessionId)
            .pipe(finalize(() => this._sessionsCommunicationState.setChannelImagesInProgress(false)))
            .subscribe(channelImages => this._sessionsCommunicationState.setChannelImages(channelImages));
    }

    getChannelImages$(): Observable<SessionChannelImage[]> {
        return this._sessionsCommunicationState.getChannelImages$();
    }

    updateChannelImages(eventId: number, sessionIds: number[], images: SessionChannelImageRequest[]): Observable<void> {
        if (images.length) {
            this._sessionsCommunicationState.setChannelImagesInProgress(true);
            return this._sessionsCommunicationApi.postChannelImages(eventId, sessionIds, images)
                .pipe(finalize(() => this._sessionsCommunicationState.setChannelImagesInProgress(false)));
        }
        return of(null);
    }

    isChannelImagesInProgress$(): Observable<boolean> {
        return this._sessionsCommunicationState.isChannelImagesInProgress$();
    }

    deleteChannelImages(eventId: number, sessionIds: number[], images: SessionChannelImageRequest[]): Observable<void[]> {
        const deletes: Observable<void>[] = [];
        images.forEach(image => {
            deletes.push(this._sessionsCommunicationApi.deleteChannelImage(eventId, sessionIds, image));
        });

        if (deletes.length) {
            this._sessionsCommunicationState.setChannelImagesDeleteInProgress(true);
            return zip(...deletes).pipe(finalize(() => this._sessionsCommunicationState.setChannelImagesDeleteInProgress(false)));
        }
        return of(null);
    }

    setDefaultChannelImages(eventId: number, sessionIds: number[], language: string): Observable<void> {
        this._sessionsCommunicationState.setChannelImagesDeleteInProgress(true);
        return this._sessionsCommunicationApi.deleteAllChannelImage(eventId, sessionIds, language)
            .pipe(finalize(() => this._sessionsCommunicationState.setChannelImagesDeleteInProgress(false)));
    }

    isChannelImagesDeleteInProgress$(): Observable<boolean> {
        return this._sessionsCommunicationState.isChannelImagesDeleteInProgress$();
    }

    clearChannelImages(): void {
        this._sessionsCommunicationState.setChannelImages(null);
    }

    // TICKET CONTENT
    loadTicketPdfTexts(eventId: number, sessionId: number): void {
        this._sessionsCommunicationState.setTicketPdfTextsInProgress(true);
        this._sessionsCommunicationApi.getTicketTexts(SessionTicketContentFormat.pdf, eventId, sessionId)
            .pipe(finalize(() => this._sessionsCommunicationState.setTicketPdfTextsInProgress(false)))
            .subscribe(channelTexts => this._sessionsCommunicationState.setTicketPdfTexts(channelTexts));
    }

    getTicketPdfTexts$(): Observable<SessionTicketText[]> {
        return this._sessionsCommunicationState.getTicketPdfTexts$();
    }

    updateTicketPdfTexts(eventId: number, sessionIds: number[], texts: SessionTicketText[]): Observable<void> {
        if (texts.length) {
            this._sessionsCommunicationState.setTicketPdfTextsInProgress(true);
            return this._sessionsCommunicationApi.postTicketTexts(SessionTicketContentFormat.pdf, eventId, sessionIds, texts)
                .pipe(finalize(() => this._sessionsCommunicationState.setTicketPdfTextsInProgress(false)));
        }
        return of(null);
    }

    isTicketPdfTextsInProgress$(): Observable<boolean> {
        return this._sessionsCommunicationState.isTicketPdfTextsInProgress$();
    }

    clearTicketPdfTexts(): void {
        this._sessionsCommunicationState.setTicketPdfTexts(null);
    }

    loadTicketPrinterTexts(eventId: number, sessionId: number): void {
        this._sessionsCommunicationState.setTicketPrinterTextsInProgress(true);
        this._sessionsCommunicationApi.getTicketTexts(SessionTicketContentFormat.printer, eventId, sessionId)
            .pipe(finalize(() => this._sessionsCommunicationState.setTicketPrinterTextsInProgress(false)))
            .subscribe(channelTexts => this._sessionsCommunicationState.setTicketPrinterTexts(channelTexts));
    }

    getTicketPrinterTexts$(): Observable<SessionTicketText[]> {
        return this._sessionsCommunicationState.getTicketPrinterTexts$();
    }

    updateTicketPrinterTexts(eventId: number, sessionIds: number[], texts: SessionTicketText[]): Observable<void> {
        if (texts.length) {
            this._sessionsCommunicationState.setTicketPrinterTextsInProgress(true);
            return this._sessionsCommunicationApi.postTicketTexts(SessionTicketContentFormat.printer, eventId, sessionIds, texts)
                .pipe(finalize(() => this._sessionsCommunicationState.setTicketPrinterTextsInProgress(false)));
        }
        return of(null);
    }

    isTicketPrinterTextsInProgress$(): Observable<boolean> {
        return this._sessionsCommunicationState.isTicketPrinterTextsInProgress$();
    }

    clearTicketPrinterTexts(): void {
        this._sessionsCommunicationState.setTicketPrinterTexts(null);
    }

    loadTicketPdfImages(eventId: number, sessionId: number): void {
        this._sessionsCommunicationState.setTicketPdfImagesInProgress(true);
        this._sessionsCommunicationApi.getTicketImages(SessionTicketContentFormat.pdf, eventId, sessionId)
            .pipe(finalize(() => this._sessionsCommunicationState.setTicketPdfImagesInProgress(false)))
            .subscribe(channelImages => this._sessionsCommunicationState.setTicketPdfImages(channelImages));
    }

    getTicketPdfImages$(): Observable<SessionTicketImage[]> {
        return this._sessionsCommunicationState.getTicketPdfImages$();
    }

    updateTicketPdfImages(eventId: number, sessionIds: number[], images: SessionTicketImageRequest[]): Observable<void> {
        if (images.length) {
            this._sessionsCommunicationState.setTicketPdfImagesInProgress(true);
            return this._sessionsCommunicationApi.postTicketImages(SessionTicketContentFormat.pdf, eventId, sessionIds, images)
                .pipe(finalize(() => this._sessionsCommunicationState.setTicketPdfImagesInProgress(false)));
        }
        return of(null);
    }

    isTicketPdfImagesInProgress$(): Observable<boolean> {
        return this._sessionsCommunicationState.isTicketPdfImagesInProgress$();
    }

    deleteTicketPdfImages(eventId: number, sessionIds: number[], images: SessionTicketImageRequest[]): Observable<void[]> {
        const deletes: Observable<void>[] = [];
        images.forEach(image => {
            deletes.push(this._sessionsCommunicationApi.deleteTicketImage(SessionTicketContentFormat.pdf, eventId, sessionIds, image));
        });
        if (deletes.length) {
            this._sessionsCommunicationState.setTicketPdfImagesDeleteInProgress(true);
            return zip(...deletes).pipe(finalize(() => this._sessionsCommunicationState.setTicketPdfImagesDeleteInProgress(false)));
        }
        return of(null);
    }

    setDefaultTicketPdfImages(eventId: number, sessionIds: number[], language: string): Observable<void> {
        this._sessionsCommunicationState.setTicketPdfImagesDeleteInProgress(true);
        return this._sessionsCommunicationApi.deleteAllTicketImage(SessionTicketContentFormat.pdf, eventId, sessionIds, language)
            .pipe(finalize(() => this._sessionsCommunicationState.setTicketPdfImagesDeleteInProgress(false)));
    }

    isTicketPdfImagesDeleteInProgress$(): Observable<boolean> {
        return this._sessionsCommunicationState.isTicketPdfImagesDeleteInProgress$();
    }

    clearTicketPdfImages(): void {
        this._sessionsCommunicationState.setTicketPdfImages(null);
    }

    loadTicketPrinterImages(eventId: number, sessionId: number): void {
        this._sessionsCommunicationState.setTicketPrinterImagesInProgress(true);
        this._sessionsCommunicationApi.getTicketImages(SessionTicketContentFormat.printer, eventId, sessionId)
            .pipe(finalize(() => this._sessionsCommunicationState.setTicketPrinterImagesInProgress(false)))
            .subscribe(channelImages => this._sessionsCommunicationState.setTicketPrinterImages(channelImages));
    }

    getTicketPrinterImages$(): Observable<SessionTicketImage[]> {
        return this._sessionsCommunicationState.getTicketPrinterImages$();
    }

    updateTicketPrinterImages(eventId: number, sessionIds: number[], images: SessionTicketImageRequest[]): Observable<void> {
        if (images.length) {
            this._sessionsCommunicationState.setTicketPrinterImagesInProgress(true);
            return this._sessionsCommunicationApi.postTicketImages(SessionTicketContentFormat.printer, eventId, sessionIds, images)
                .pipe(finalize(() => this._sessionsCommunicationState.setTicketPrinterImagesInProgress(false)));
        }
        return of(null);
    }

    isTicketPrinterImagesInProgress$(): Observable<boolean> {
        return this._sessionsCommunicationState.isTicketPrinterImagesInProgress$();
    }

    deleteTicketPrinterImages(eventId: number, sessionIds: number[], images: SessionTicketImageRequest[]): Observable<void[]> {
        const deletes: Observable<void>[] = [];
        images.forEach(image => {
            deletes.push(this._sessionsCommunicationApi.deleteTicketImage(SessionTicketContentFormat.printer, eventId, sessionIds, image));
        });
        if (deletes.length) {
            this._sessionsCommunicationState.setTicketPrinterImagesDeleteInProgress(true);
            return zip(...deletes).pipe(finalize(() => this._sessionsCommunicationState.setTicketPrinterImagesDeleteInProgress(false)));
        }
        return of(null);
    }

    setDefaultTicketPrinterImages(eventId: number, sessionIds: number[], language: string): Observable<void> {
        this._sessionsCommunicationState.setTicketPrinterImagesDeleteInProgress(true);
        return this._sessionsCommunicationApi.deleteAllTicketImage(SessionTicketContentFormat.printer, eventId, sessionIds, language)
            .pipe(finalize(() => this._sessionsCommunicationState.setTicketPrinterImagesDeleteInProgress(false)));
    }

    isTicketPrinterImagesDeleteInProgress$(): Observable<boolean> {
        return this._sessionsCommunicationState.isTicketPrinterImagesDeleteInProgress$();
    }

    clearTicketPrinterImages(): void {
        this._sessionsCommunicationState.setTicketPrinterImages(null);
    }

    // PASSBOOK
    loadSessionTicketPassbookContentTexts(eventId: number, sessionId?: number): void {
        this._sessionsCommunicationState.setTicketPassbookContentTextsInProgress(true);
        this._sessionsCommunicationApi.getTicketTexts(SessionTicketContentFormat.passbook, eventId, sessionId)
            .pipe(
                catchError(() => of(null)),
                finalize(() => this._sessionsCommunicationState.setTicketPassbookContentTextsInProgress(false))
            )
            .subscribe(contents =>
                this._sessionsCommunicationState.setTicketPassbookContentTexts(contents)
            );
    }

    clearSessionTicketPassbookContentTexts(): void {
        return this._sessionsCommunicationState.setTicketPassbookContentTexts(null);
    }

    getSessionTicketPassbookContentTexts$(): Observable<SessionTicketText[]> {
        return this._sessionsCommunicationState.getTicketPassbookContentTexts$();
    }

    isSessionTicketPassbookContentTextsInProgress$(): Observable<boolean> {
        return this._sessionsCommunicationState.isTicketPassbookContentTextsInProgress$();
    }

    loadSessionTicketPassbookContentImages(eventId: number, sessionId: number): void {
        this._sessionsCommunicationState.setTicketPassbookContentImagesInProgress(true);
        this._sessionsCommunicationApi.getTicketImages(SessionTicketContentFormat.passbook, eventId, sessionId)
            .pipe(
                catchError(() => of(null)),
                finalize(() => this._sessionsCommunicationState.setTicketPassbookContentImagesInProgress(false))
            )
            .subscribe(contents =>
                this._sessionsCommunicationState.setTicketPassbookContentImages(contents)
            );
    }

    clearSessionTicketPassbookContentImages(): void {
        return this._sessionsCommunicationState.setTicketPassbookContentImages(null);
    }

    getSessionTicketPassbookContentImages$(): Observable<SessionTicketImage[]> {
        return this._sessionsCommunicationState.getTicketPassbookContentImages$();
    }

    isSessionTicketPassbookContentImagesInProgress$(): Observable<boolean> {
        return this._sessionsCommunicationState.isTicketPassbookContentImagesInProgress$();
    }

    saveSessionTicketPassbookContentTexts(eventId: number, sessionIds: number[], textsToSave: SessionTicketText[]): Observable<void> {
        this._sessionsCommunicationState.setTicketPassbookContentTextsSaving(true);
        return this._sessionsCommunicationApi.postTicketTexts(SessionTicketContentFormat.passbook, eventId, sessionIds, textsToSave)
            .pipe(finalize(() => this._sessionsCommunicationState.setTicketPassbookContentTextsSaving(false)));
    }

    isSessionTicketPassbookContentTextsSaving$(): Observable<boolean> {
        return this._sessionsCommunicationState.isTicketPassbookContentTextsSaving$();
    }

    saveSessionTicketPassbookContentImages(eventId: number, sessionIds: number[], image: SessionTicketImageRequest[]): Observable<void> {
        this._sessionsCommunicationState.setTicketPassbookContentImagesSaving(true);
        return this._sessionsCommunicationApi
            .postTicketImages(SessionTicketContentFormat.passbook, eventId, sessionIds, image)
            .pipe(finalize(() => this._sessionsCommunicationState.setTicketPassbookContentImagesSaving(false)));
    }

    isSessionTicketPassbookContentImagesSaving$(): Observable<boolean> {
        return this._sessionsCommunicationState.isTicketPassbookContentImagesSaving$();
    }

    deleteSessionTicketPassbookContentImages(
        eventId: number, sessionIds: number[], images: SessionTicketImageRequest[]
    ): Observable<void[]> {
        const deletes: Observable<void>[] = [];
        images.forEach(image => {
            deletes.push(this._sessionsCommunicationApi.deleteTicketImage(SessionTicketContentFormat.passbook, eventId, sessionIds, image));
        });
        if (deletes.length) {
            this._sessionsCommunicationState.setTicketPassbookContentImagesRemoving(true);
            return zip(...deletes).pipe(finalize(() => this._sessionsCommunicationState.setTicketPassbookContentImagesRemoving(false)));
        }
        return of(null);
    }

    isSessionTicketPassbookContentImagesRemoving$(): Observable<boolean> {
        return this._sessionsCommunicationState.isTicketPassbookContentImagesRemoving$();
    }

    getDownloadUrlPassbookPreview$(eventId: number, sessionId: number): Observable<{ download_url: string }> {
        this._sessionsCommunicationState.setDownloadUrlPassbookPreviewDownloadingInProgress(true);
        return this._sessionsCommunicationApi.getDownloadUrlPassbookPreview$(eventId, sessionId)
            .pipe(finalize(() => this._sessionsCommunicationState.setDownloadUrlPassbookPreviewDownloadingInProgress(false)));
    }

    isDownloadUrlPassbookPreviewInProgress$(): Observable<boolean> {
        return this._sessionsCommunicationState.isDownloadUrlPassbookPreviewInProgress$();
    }
}
