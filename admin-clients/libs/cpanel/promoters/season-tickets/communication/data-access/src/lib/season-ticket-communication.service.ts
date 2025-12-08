import { TicketTemplateFormat } from '@admin-clients/cpanel-promoters-ticket-templates-data-access';
import { TicketType } from '@admin-clients/shared/common/data-access';
import { Injectable, Provider } from '@angular/core';
import { Observable, of, switchMap, zip } from 'rxjs';
import { catchError, finalize } from 'rxjs/operators';
import { SeasonTicketCommunicationApi } from './api/season-ticket-communication.api';
import { SeasonTicketChannelContentImageType } from './models/season-ticket-channel-content-image-type.enum';
import { SeasonTicketChannelContentImage } from './models/season-ticket-channel-content-image.model';
import { SeasonTicketChannelContentImageRequest } from './models/season-ticket-channel-content-image.request.model';
import { SeasonTicketChannelContentText } from './models/season-ticket-channel-content-text.model';
import { SeasonTicketTemplateType } from './models/season-ticket-template-type.enum';
import { SeasonTicketTicketContentFormat } from './models/season-ticket-ticket-content-format.enum';
import { SeasonTicketTicketContentImageType } from './models/season-ticket-ticket-content-image-type.enum';
import { SeasonTicketTicketContentImage } from './models/season-ticket-ticket-content-image.model';
import { SeasonTicketTicketContentImageRequest } from './models/season-ticket-ticket-content-image.request.model';
import { SeasonTicketTicketContentText } from './models/season-ticket-ticket-content-text.model';
import { SeasonTicketTicketTemplate } from './models/season-ticket-ticket-template.model';
import { SeasonTicketCommunicationState } from './state/season-ticket-communication.state';

export const provideSeasonTicketCommunicationService = (): Provider => [
    SeasonTicketCommunicationApi,
    SeasonTicketCommunicationState,
    SeasonTicketCommunicationService
];

@Injectable()
export class SeasonTicketCommunicationService {

    constructor(
        private _seasonTicketCommunicationApi: SeasonTicketCommunicationApi,
        private _seasonTicketCommunicationState: SeasonTicketCommunicationState
    ) {
    }

    loadSeasonTicketChannelContentTexts(seasonTicketId: number): void {
        this._seasonTicketCommunicationState.setChannelContentTextsInProgress(true);
        this._seasonTicketCommunicationApi.getSeasonTicketChannelContentTexts(seasonTicketId)
            .pipe(
                catchError(() => of(null)),
                finalize(() =>
                    this._seasonTicketCommunicationState.setChannelContentTextsInProgress(false)
                )
            )
            .subscribe(contents =>
                this._seasonTicketCommunicationState.setChannelContentTexts(contents)
            );
    }

    getSeasonTicketChannelContentTexts(): Observable<SeasonTicketChannelContentText[]> {
        return this._seasonTicketCommunicationState.getChannelContentTexts$();
    }

    isSeasonTicketChannelContentTextsLoading$(): Observable<boolean> {
        return this._seasonTicketCommunicationState.isChannelContentTextsInProgress$();
    }

    saveSeasonTicketChannelContentTexts(
        seasonTicketId: number,
        textsToSave: SeasonTicketChannelContentText[]
    ): Observable<void> {
        this._seasonTicketCommunicationState.setChannelContentTextsSaving(true);
        return this._seasonTicketCommunicationApi.postSeasonTicketChannelContentTexts(
            seasonTicketId,
            textsToSave
        ).pipe(
            finalize(() =>
                this._seasonTicketCommunicationState.setChannelContentTextsSaving(false)
            )
        );
    }

    isSeasonTicketChannelContentTextsSaving$(): Observable<boolean> {
        return this._seasonTicketCommunicationState.isChannelContentTextsSaving$();
    }

    clearSeasonTicketChannelContentTexts(): void {
        this._seasonTicketCommunicationState.setChannelContentTexts(null);
    }

    loadSeasonTicketChannelContentImages(
        seasonTicketId: number,
        language?: string,
        type?: SeasonTicketChannelContentImageType
    ): void {
        this._seasonTicketCommunicationState.setChannelContentImagesInProgress(true);
        this._seasonTicketCommunicationApi.getSeasonTicketChannelContentImages$(
            seasonTicketId,
            language,
            type
        ).pipe(
            catchError(() => of(null)),
            finalize(() =>
                this._seasonTicketCommunicationState.setChannelContentImagesInProgress(false)
            )
        ).subscribe(contents => this._seasonTicketCommunicationState.setChannelContentImages(contents));
    }

    getSeasonTicketChannelContentImages$(): Observable<SeasonTicketChannelContentImage[]> {
        return this._seasonTicketCommunicationState.getChannelContentImages$();
    }

    isSeasonTicketChannelContentImagesLoading$(): Observable<boolean> {
        return this._seasonTicketCommunicationState.isChannelContentImagesInProgress$();
    }

    saveSeasonTicketChannelContentImages$(
        seasonTicketId: number,
        imagesToSave: SeasonTicketChannelContentImageRequest[]
    ): Observable<void> {
        this._seasonTicketCommunicationState.setChannelContentImagesSaving(true);
        return this._seasonTicketCommunicationApi.postSeasonTicketChannelContentImages(
            seasonTicketId,
            imagesToSave
        ).pipe(
            finalize(() =>
                this._seasonTicketCommunicationState.setChannelContentImagesSaving(false)
            )
        );
    }

    isSeasonTicketChannelContentImagesSaving$(): Observable<boolean> {
        return this._seasonTicketCommunicationState.isChannelContentImagesSaving$();
    }

    deleteSeasonTicketChannelContentImages$(
        seasonTicketId: number,
        imagesToDelete: SeasonTicketChannelContentImageRequest[]
    ): Observable<void> {
        this._seasonTicketCommunicationState.setChannelContentImagesRemoving(true);
        return zip(...imagesToDelete
            .map(request =>
                this._seasonTicketCommunicationApi.deleteSeasonTicketChannelContentImage$(
                    seasonTicketId,
                    request.language,
                    request.type,
                    request.position
                ))
        ).pipe(
            switchMap(() => of(null)),
            catchError(() => of(null)),
            finalize(() =>
                this._seasonTicketCommunicationState.setChannelContentImagesRemoving(false)
            )
        );
    }

    isSeasonTicketChannelContentImagesRemoving$(): Observable<boolean> {
        return this._seasonTicketCommunicationState.isChannelContentImagesRemoving$();
    }

    clearSeasonTicketChannelContentImages(): void {
        this._seasonTicketCommunicationState.setChannelContentImages(null);
    }

    loadSeasonTicketTicketTemplates(seasonTicketId: number): void {
        this._seasonTicketCommunicationState.setTicketTemplatesInProgress(true);
        this._seasonTicketCommunicationApi.getSeasonTicketTicketTemplates(seasonTicketId)
            .pipe(
                catchError(() => of(null)),
                finalize(() =>
                    this._seasonTicketCommunicationState.setTicketTemplatesInProgress(false)
                )
            )
            .subscribe(templates =>
                this._seasonTicketCommunicationState.setTicketTemplates(templates)
            );
    }

    getSeasonTicketTicketTemplates$(): Observable<SeasonTicketTicketTemplate[]> {
        return this._seasonTicketCommunicationState.getTicketTemplates$();
    }

    isSeasonTicketTicketTemplatesLoading$(): Observable<boolean> {
        return this._seasonTicketCommunicationState.isTicketTemplatesInProgress$();
    }

    saveSeasonTicketTicketTemplates(
        seasonTicketId: number,
        templatesToSave: SeasonTicketTicketTemplate[]
    ): Observable<void> {
        this._seasonTicketCommunicationState.setTicketTemplatesSaving(true);
        return zip(...templatesToSave.map(template => {
            const isPassbook = template.format === TicketTemplateFormat.passbook;
            const templateId = !isPassbook ? { id: template.id } : { code: template.id.toString() };
            return this._seasonTicketCommunicationApi
                .postSeasonTicketTicketTemplate$(seasonTicketId, templateId, template.format, template.type);
        }))
            .pipe(
                switchMap(() => of(null)),
                catchError(() => of(null)),
                finalize(() =>
                    this._seasonTicketCommunicationState.setTicketTemplatesSaving(false)
                )
            );
    }

    isSeasonTicketTicketTemplatesSaving$(): Observable<boolean> {
        return this._seasonTicketCommunicationState.isTicketTemplatesSaving$();
    }

    loadSeasonTicketTicketPdfContentTexts(ticketType: TicketType, seasonTicketId: number): void {
        this._seasonTicketCommunicationState.setTicketPdfContentTextsInProgress(true);
        this._seasonTicketCommunicationApi.getSeasonTicketTicketContentTexts$(
            ticketType,
            seasonTicketId,
            SeasonTicketTicketContentFormat.pdf
        ).pipe(
            catchError(() => of(null)),
            finalize(() =>
                this._seasonTicketCommunicationState.setTicketPdfContentTextsInProgress(false))
        ).subscribe(contents =>
            this._seasonTicketCommunicationState.setTicketPdfContentTexts(contents)
        );
    }

    getSeasonTicketTicketPdfContentTexts$(): Observable<SeasonTicketTicketContentText[]> {
        return this._seasonTicketCommunicationState.getTicketPdfContentTexts$();
    }

    isSeasonTicketTicketPdfContentTextsLoading$(): Observable<boolean> {
        return this._seasonTicketCommunicationState.isTicketPdfContentTextsInProgress$();
    }

    saveSeasonTicketTicketPdfContentTexts(
        ticketType: TicketType,
        seasonTicketId: number,
        textsToSave: SeasonTicketTicketContentText[]
    ): Observable<void> {
        this._seasonTicketCommunicationState.setTicketPdfContentTextsSaving(true);
        return this._seasonTicketCommunicationApi.postSeasonTicketTicketContentTexts$(
            ticketType,
            seasonTicketId,
            SeasonTicketTicketContentFormat.pdf,
            textsToSave
        ).pipe(
            finalize(() =>
                this._seasonTicketCommunicationState.setTicketPdfContentTextsSaving(false)
            )
        );
    }

    isSeasonTicketTicketPdfContentTextsSaving$(): Observable<boolean> {
        return this._seasonTicketCommunicationState.isTicketPdfContentTextsSaving$();
    }

    loadSeasonTicketTicketPdfContentImages(
        ticketType: TicketType,
        seasonTicketId: number,
        language?: string,
        type?: SeasonTicketTicketContentImageType
    ): void {
        this._seasonTicketCommunicationState.setTicketPdfContentImagesInProgress(true);
        this._seasonTicketCommunicationApi.getSeasonTicketTicketContentImages$(
            ticketType,
            seasonTicketId,
            SeasonTicketTicketContentFormat.pdf,
            language,
            type
        ).pipe(
            catchError(() => of(null)),
            finalize(() =>
                this._seasonTicketCommunicationState.setTicketPdfContentImagesInProgress(false))
        ).subscribe(contents =>
            this._seasonTicketCommunicationState.setTicketPdfContentImages(contents)
        );
    }

    getSeasonTicketTicketPdfContentImages$(): Observable<SeasonTicketTicketContentImage[]> {
        return this._seasonTicketCommunicationState.getTicketPdfContentImages$();
    }

    isSeasonTicketTicketPdfContentImagesLoading$(): Observable<boolean> {
        return this._seasonTicketCommunicationState.isTicketPdfContentImagesInProgress$();
    }

    saveSeasonTicketTicketPdfContentImages(
        ticketType: TicketType,
        seasonTicketId: number,
        imagesToSave: SeasonTicketTicketContentImageRequest[]
    ): Observable<void> {
        this._seasonTicketCommunicationState.setTicketPdfContentImagesSaving(true);
        return this._seasonTicketCommunicationApi.postSeasonTicketTicketContentImages$(
            ticketType,
            seasonTicketId,
            SeasonTicketTicketContentFormat.pdf,
            imagesToSave
        ).pipe(
            finalize(() =>
                this._seasonTicketCommunicationState.setTicketPdfContentImagesSaving(false)
            )
        );
    }

    isSeasonTicketTicketPdfContentImagesSaving$(): Observable<boolean> {
        return this._seasonTicketCommunicationState.isTicketPdfContentImagesSaving$();
    }

    deleteSeasonTicketTicketPdfContentImages(
        ticketType: TicketType,
        seasonTicketId: number,
        imagesToDelete: SeasonTicketTicketContentImageRequest[]
    ): Observable<void> {
        this._seasonTicketCommunicationState.setTicketPdfContentImagesRemoving(true);
        return zip(...imagesToDelete
            .map(request => this._seasonTicketCommunicationApi
                .deleteSeasonTicketTicketContentImage$(
                    ticketType,
                    seasonTicketId,
                    SeasonTicketTicketContentFormat.pdf,
                    request.language,
                    request.type
                )
            ))
            .pipe(
                switchMap(() => of(null)),
                catchError(() => of(null)),
                finalize(() =>
                    this._seasonTicketCommunicationState.setTicketPdfContentImagesRemoving(false)
                )
            );
    }

    isSeasonTicketTicketPdfContentImagesRemoving$(): Observable<boolean> {
        return this._seasonTicketCommunicationState.isTicketPdfContentImagesRemoving$();
    }

    downloadTicketPdfPreview$(
        seasonTicketId: number,
        type: SeasonTicketTemplateType,
        language: string
    ): Observable<{ url: string }> {
        this._seasonTicketCommunicationState.setTicketPdfPreviewDownloading(true);
        return this._seasonTicketCommunicationApi.downloadTicketPdfPreview$(seasonTicketId, type, language)
            .pipe(
                finalize(() =>
                    this._seasonTicketCommunicationState.setTicketPdfPreviewDownloading(false)
                )
            );
    }

    isTicketPdfPreviewDownloading$(): Observable<boolean> {
        return this._seasonTicketCommunicationState.isTicketPdfPreviewDownloading$();
    }

    loadSeasonTicketTicketPrinterContentTexts(ticketType: TicketType, seasonTicketId: number): void {
        this._seasonTicketCommunicationState.setTicketPrinterContentTextsInProgress(true);
        this._seasonTicketCommunicationApi.getSeasonTicketTicketContentTexts$(
            ticketType,
            seasonTicketId,
            SeasonTicketTicketContentFormat.printer
        ).pipe(
            catchError(() => of(null)),
            finalize(() =>
                this._seasonTicketCommunicationState.setTicketPrinterContentTextsInProgress(false)
            )
        ).subscribe(contents =>
            this._seasonTicketCommunicationState.setTicketPrinterContentTexts(contents)
        );
    }

    getSeasonTicketTicketPrinterContentTexts$(): Observable<SeasonTicketTicketContentText[]> {
        return this._seasonTicketCommunicationState.getTicketPrinterContentTexts$();
    }

    isSeasonTicketTicketPrinterContentTextsLoading$(): Observable<boolean> {
        return this._seasonTicketCommunicationState.isTicketPrinterContentTextsInProgress$();
    }

    saveSeasonTicketTicketPrinterContentTexts(
        ticketType: TicketType,
        seasonTicketId: number,
        textsToSave: SeasonTicketTicketContentText[]
    ): Observable<void> {
        this._seasonTicketCommunicationState.setTicketPrinterContentTextsSaving(true);
        return this._seasonTicketCommunicationApi.postSeasonTicketTicketContentTexts$(
            ticketType,
            seasonTicketId,
            SeasonTicketTicketContentFormat.printer,
            textsToSave
        ).pipe(
            finalize(() =>
                this._seasonTicketCommunicationState.setTicketPrinterContentTextsSaving(false)
            )
        );
    }

    isSeasonTicketTicketPrinterContentTextsSaving$(): Observable<boolean> {
        return this._seasonTicketCommunicationState.isTicketPrinterContentTextsSaving$();
    }

    loadSeasonTicketTicketPrinterContentImages(
        ticketType: TicketType,
        seasonTicketId: number,
        language?: string,
        type?: SeasonTicketTicketContentImageType
    ): void {
        this._seasonTicketCommunicationState.setTicketPrinterContentImagesInProgress(true);
        this._seasonTicketCommunicationApi.getSeasonTicketTicketContentImages$(
            ticketType,
            seasonTicketId,
            SeasonTicketTicketContentFormat.printer,
            language,
            type
        ).pipe(
            catchError(() => of(null)),
            finalize(() =>
                this._seasonTicketCommunicationState.setTicketPrinterContentImagesInProgress(false)
            )
        ).subscribe(contents =>
            this._seasonTicketCommunicationState.setTicketPrinterContentImages(contents)
        );
    }

    getSeasonTicketTicketPrinterContentImages$(): Observable<SeasonTicketTicketContentImage[]> {
        return this._seasonTicketCommunicationState.getTicketPrinterContentImages$();
    }

    isSeasonTicketTicketPrinterContentImagesLoading$(): Observable<boolean> {
        return this._seasonTicketCommunicationState.isTicketPrinterContentImagesInProgress$();
    }

    saveSeasonTicketTicketPrinterContentImages(
        ticketType: TicketType,
        seasonTicketId: number,
        imagesToSave: SeasonTicketTicketContentImageRequest[]
    ): Observable<void> {
        this._seasonTicketCommunicationState.setTicketPrinterContentImagesSaving(true);
        return this._seasonTicketCommunicationApi.postSeasonTicketTicketContentImages$(
            ticketType,
            seasonTicketId,
            SeasonTicketTicketContentFormat.printer,
            imagesToSave
        ).pipe(
            finalize(() =>
                this._seasonTicketCommunicationState.setTicketPrinterContentImagesSaving(false)
            )
        );
    }

    isSeasonTicketTicketPrinterContentImagesSaving$(): Observable<boolean> {
        return this._seasonTicketCommunicationState.isTicketPrinterContentImagesSaving$();
    }

    deleteSeasonTicketTicketPrinterContentImages(
        ticketType: TicketType,
        seasonTicketId: number,
        imagesToDelete: SeasonTicketTicketContentImageRequest[]
    ): Observable<void> {
        this._seasonTicketCommunicationState.setTicketPrinterContentImagesRemoving(true);
        return zip(...imagesToDelete
            .map(request => this._seasonTicketCommunicationApi
                .deleteSeasonTicketTicketContentImage$(
                    ticketType,
                    seasonTicketId,
                    SeasonTicketTicketContentFormat.printer,
                    request.language,
                    request.type
                )
            ))
            .pipe(
                switchMap(() => of(null)),
                catchError(() => of(null)),
                finalize(() =>
                    this._seasonTicketCommunicationState.setTicketPrinterContentImagesRemoving(false)
                )
            );
    }

    isSeasonTicketTicketPrinterContentImagesRemoving$(): Observable<boolean> {
        return this._seasonTicketCommunicationState.isTicketPrinterContentImagesRemoving$();
    }

    loadSeasonTicketTicketPassbookContentImages(
        ticketType: TicketType,
        seasonTicketId: number,
        language?: string,
        type?: SeasonTicketTicketContentImageType
    ): void {
        this._seasonTicketCommunicationState.setTicketPassbookContentImagesInProgress(true);
        this._seasonTicketCommunicationApi.getSeasonTicketTicketContentImages$(
            ticketType,
            seasonTicketId,
            SeasonTicketTicketContentFormat.passbook,
            language,
            type
        ).pipe(
            catchError(() => of(null)),
            finalize(() =>
                this._seasonTicketCommunicationState.setTicketPassbookContentImagesInProgress(false)
            )
        )
            .subscribe(contents =>
                this._seasonTicketCommunicationState.setTicketPassbookContentImages(contents)
            );
    }

    getSeasonTicketTicketPassbookContentImages$(): Observable<SeasonTicketTicketContentImage[]> {
        return this._seasonTicketCommunicationState.getTicketPassbookContentImages$();
    }

    isSeasonTicketTicketPassbookContentImagesLoading$(): Observable<boolean> {
        return this._seasonTicketCommunicationState.isTicketPassbookContentImagesInProgress$();
    }

    saveSeasonTicketTicketPassbookContentImages(
        ticketType: TicketType,
        seasonTicketId: number,
        imagesToSave: SeasonTicketTicketContentImageRequest[]
    ): Observable<void> {
        this._seasonTicketCommunicationState.setTicketPassbookContentImagesSaving(true);
        return this._seasonTicketCommunicationApi
            .postSeasonTicketTicketContentImages$(
                ticketType,
                seasonTicketId,
                SeasonTicketTicketContentFormat.passbook,
                imagesToSave
            )
            .pipe(
                finalize(() =>
                    this._seasonTicketCommunicationState.setTicketPassbookContentImagesSaving(false)
                )
            );
    }

    isSeasonTicketTicketPassbookContentImagesSaving$(): Observable<boolean> {
        return this._seasonTicketCommunicationState.isTicketPassbookContentImagesSaving$();
    }

    deleteSeasonTicketTicketPassbookContentImages(
        ticketType: TicketType,
        eventId: number,
        imagesToDelete: SeasonTicketTicketContentImageRequest[]
    ): Observable<void> {
        this._seasonTicketCommunicationState.setTicketPassbookContentImagesRemoving(true);
        return zip(...imagesToDelete
            .map(request => this._seasonTicketCommunicationApi
                .deleteSeasonTicketTicketContentImage$(
                    ticketType,
                    eventId,
                    SeasonTicketTicketContentFormat.passbook,
                    request.language,
                    request.type
                )
            ))
            .pipe(
                switchMap(() => of(null)),
                catchError(() => of(null)),
                finalize(() =>
                    this._seasonTicketCommunicationState.setTicketPassbookContentImagesRemoving(false)
                )
            );
    }

    isSeasonTicketTicketPassbookContentImagesRemoving$(): Observable<boolean> {
        return this._seasonTicketCommunicationState.isTicketPassbookContentImagesRemoving$();
    }

    loadSeasonTicketTicketPassbookContentTexts(ticketType: TicketType, eventId: number): void {
        this._seasonTicketCommunicationState.setTicketPassbookContentTextsInProgress(true);
        this._seasonTicketCommunicationApi.getSeasonTicketTicketContentTexts(ticketType, eventId, SeasonTicketTicketContentFormat.passbook)
            .pipe(
                catchError(() => of(null)),
                finalize(() => this._seasonTicketCommunicationState.setTicketPassbookContentTextsInProgress(false))
            )
            .subscribe(contents =>
                this._seasonTicketCommunicationState.setTicketPassbookContentTexts(contents)
            );
    }

    getSeasonTicketTicketPassbookContentTexts$(): Observable<SeasonTicketTicketContentText[]> {
        return this._seasonTicketCommunicationState.getTicketPassbookContentTexts$();
    }

    isSeasonTicketTicketPassbookContentTextsLoading$(): Observable<boolean> {
        return this._seasonTicketCommunicationState.isTicketPassbookContentTextsInProgress$();
    }

    saveSeasonTicketTicketPassbookContentTexts(
        ticketType: TicketType,
        eventId: number,
        textsToSave: SeasonTicketTicketContentText[]
    ): Observable<void> {
        this._seasonTicketCommunicationState.setTicketPassbookContentTextsSaving(true);
        return this._seasonTicketCommunicationApi
            .postSeasonTicketTicketContentTexts(ticketType, eventId, SeasonTicketTicketContentFormat.passbook, textsToSave)
            .pipe(finalize(() => this._seasonTicketCommunicationState.setTicketPassbookContentTextsSaving(false)));
    }

    isSeasonTicketTicketPassbookContentTextsSaving$(): Observable<boolean> {
        return this._seasonTicketCommunicationState.isTicketPassbookContentTextsSaving$();
    }

    getDownloadUrlPassbookPreview$(eventId: number): Observable<{ download_url: string }> {
        this._seasonTicketCommunicationState.setDownloadUrlPassbookPreviewDownloadingInProgress(true);
        return this._seasonTicketCommunicationApi.getDownloadUrlPassbookPreview$(eventId)
            .pipe(finalize(() => this._seasonTicketCommunicationState.setDownloadUrlPassbookPreviewDownloadingInProgress(false)));
    }

    isDownloadUrlPassbookPreviewInProgress$(): Observable<boolean> {
        return this._seasonTicketCommunicationState.isDownloadUrlPassbookPreviewInProgress$();
    }
}
