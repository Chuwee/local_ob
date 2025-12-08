import { Metadata } from '@OneboxTM/utils-state';
import { HttpErrorResponse } from '@angular/common/http';
import { Injectable, Provider } from '@angular/core';
import { Observable, of, zip } from 'rxjs';
import { catchError, finalize, map, switchMap } from 'rxjs/operators';
import { TicketTemplatesApi } from './api/ticket-templates.api';
import { GetTicketTemplatesRequest } from './models/get-ticket-templates-request.model';
import { PostTicketTemplate } from './models/post-ticket-template.model';
import { PutTicketTemplate } from './models/put-ticket-template.model';
import { TicketTemplateImageType } from './models/ticket-template-image-type.enum';
import { GetTicketTemplateImage, PostTicketTemplateImage } from './models/ticket-template-image.model';
import { TicketTemplateText } from './models/ticket-template-text.model';
import { TicketTemplate, TicketTemplateDesign } from './models/ticket-template.model';
import { TicketTemplatesState } from './state/ticket-templates.state';

export const provideTicketTemplateService = (): Provider => [
    TicketTemplatesApi,
    TicketTemplatesState,
    TicketTemplatesService
];

@Injectable({
    providedIn: 'root'
})
export class TicketTemplatesService {

    constructor(
        private _ticketTemplatesApi: TicketTemplatesApi,
        private _ticketTemplatesState: TicketTemplatesState
    ) { }

    loadTicketTemplates(request: GetTicketTemplatesRequest): void {
        this._ticketTemplatesState.setTicketTemplatesListInProgress(true);
        this._ticketTemplatesApi.getTicketTemplates(request)
            .pipe(
                catchError(() => of(null)),
                finalize(() => this._ticketTemplatesState.setTicketTemplatesListInProgress(false))
            ).subscribe(response =>
                this._ticketTemplatesState.setTicketTemplatesList(response)
            );
    }

    getTicketTemplatesListMetadata$(): Observable<Metadata> {
        return this._ticketTemplatesState.getTicketTemplatesList$()
            .pipe(map(response => response?.metadata
                && Object.assign(new Metadata(), response.metadata)));
    }

    getTicketTemplates$(): Observable<TicketTemplate[]> {
        return this._ticketTemplatesState.getTicketTemplatesList$()
            .pipe(map(response => response?.data));
    }

    isTicketTemplatesLoading$(): Observable<boolean> {
        return this._ticketTemplatesState.isTicketTemplatesListInProgress$();
    }

    cloneTicketTemplate(id: number, name: string, entityId: number): Observable<number> {
        this._ticketTemplatesState.setTicketTemplateCloning(true);
        return this._ticketTemplatesApi.cloneTicketTemplate(id, name, entityId)
            .pipe(
                map(result => result.id),
                finalize(() => this._ticketTemplatesState.setTicketTemplateCloning(false))
            );
    }

    isTicketTemplatesCloning$(): Observable<boolean> {
        return this._ticketTemplatesState.isTicketTemplateCloning$();
    }

    createTicketTemplate(ticketTemplate: PostTicketTemplate): Observable<number> {
        this._ticketTemplatesState.setTicketTemplateInProgress(true);
        return this._ticketTemplatesApi.postTicketTemplate(ticketTemplate)
            .pipe(
                map(result => result.id),
                finalize(() => this._ticketTemplatesState.setTicketTemplateInProgress(false))
            );
    }

    isTicketTemplateInProgress$(): Observable<boolean> {
        return this._ticketTemplatesState.isTicketTemplateInProgress$();
    }

    deleteTicketTemplate(id: string): Observable<void> {
        return this._ticketTemplatesApi.deleteTicketTemplate(id);
    }

    saveTicketTemplate(id: number, ticketTemplate: PutTicketTemplate): Observable<void> {
        return this._ticketTemplatesApi.putTicketTemplate(id, ticketTemplate);
    }

    loadDesigns(): void {
        this._ticketTemplatesState.setDesignsListInProgress(true);
        this._ticketTemplatesApi.getDesigns()
            .pipe(
                catchError(() => of(null)),
                finalize(() => this._ticketTemplatesState.setDesignsListInProgress(false))
            )
            .subscribe(list =>
                this._ticketTemplatesState.setDesignsList(list)
            );
    }

    loadPaperTypes(): void {
        this._ticketTemplatesState.setPaperTypesListInProgress(true);
        this._ticketTemplatesApi.getPaperTypes()
            .pipe(
                catchError(() => of(null)),
                finalize(() => this._ticketTemplatesState.setPaperTypesListInProgress(false))
            )
            .subscribe(list =>
                this._ticketTemplatesState.setPaperTypesList(list)
            );
    }

    loadPrinters(): void {
        this._ticketTemplatesState.setPrintersListInProgress(true);
        this._ticketTemplatesApi.getPrinters()
            .pipe(
                catchError(() => of(null)),
                finalize(() => this._ticketTemplatesState.setPrintersListInProgress(false))
            )
            .subscribe(list =>
                this._ticketTemplatesState.setPrintersList(list)
            );
    }

    getDesignsList$(): Observable<TicketTemplateDesign[]> {
        return this._ticketTemplatesState.getDesignsList$();
    }

    getPaperTypesList$(): Observable<string[]> {
        return this._ticketTemplatesState.getPaperTypesList$();
    }

    getPrintersList$(): Observable<string[]> {
        return this._ticketTemplatesState.getPrintersList$();
    }

    loadTicketTemplate(ticketTemplateId: string): void {
        this._ticketTemplatesState.setTicketTemplateError(null);
        this._ticketTemplatesState.setTicketTemplateInProgress(true);
        this._ticketTemplatesApi.getTicketTemplate(ticketTemplateId)
            .pipe(
                catchError(error => {
                    this._ticketTemplatesState.setTicketTemplateError(error);
                    return of(null);
                }),
                finalize(() => this._ticketTemplatesState.setTicketTemplateInProgress(false))
            )
            .subscribe(event =>
                this._ticketTemplatesState.setTicketTemplate(event)
            );
    }

    getTicketTemplate$(): Observable<TicketTemplate> {
        return this._ticketTemplatesState.getTicketTemplate$();
    }

    getTicketTemplateError$(): Observable<HttpErrorResponse> {
        return this._ticketTemplatesState.getTicketTemplateError$();
    }

    updateTicketTemplate(ticketTemplate: PutTicketTemplate): Observable<void> {
        this._ticketTemplatesState.setTicketTemplateInProgress(true);
        return this._ticketTemplatesApi.putTicketTemplate(ticketTemplate.id, ticketTemplate)
            .pipe(finalize(() => this._ticketTemplatesState.setTicketTemplateInProgress(false)));
    }

    updateTicketTemplateDefault(ticketTemplateId: number, isDefault: boolean): Observable<boolean> {
        return this._ticketTemplatesApi.putTicketTemplate(ticketTemplateId, { default: isDefault })
            .pipe(switchMap(() => of(true)), catchError(() => of(false)));
    }

    clearTicketTemplate(): void {
        this._ticketTemplatesState.setTicketTemplate(null);
    }

    loadTicketTemplateTexts(id: number, langCode?: string): void {
        this._ticketTemplatesState.setTicketTemplateTextsError(null);
        this._ticketTemplatesState.setTicketTemplateTextsInProgress(true);
        this._ticketTemplatesApi.getTicketTemplateTexts(id, langCode)
            .pipe(
                catchError(error => {
                    this._ticketTemplatesState.setTicketTemplateTextsError(error);
                    return of(null);
                }),
                finalize(() => this._ticketTemplatesState.setTicketTemplateTextsInProgress(false))
            )
            .subscribe(texts =>
                this._ticketTemplatesState.setTicketTemplateTexts(texts)
            );
    }

    getTicketTemplateTexts$(): Observable<TicketTemplateText[]> {
        return this._ticketTemplatesState.getTicketTemplateTexts$();
    }

    clearTicketTemplateTexts(): void {
        this._ticketTemplatesState.setTicketTemplateTexts(null);
    }

    isTicketTemplateTextsInProgress$(): Observable<boolean> {
        return this._ticketTemplatesState.isTicketTemplateTextsInProgress$();
    }

    updateTicketTemplateTexts(literals: TicketTemplateText[], id: number): Observable<TicketTemplateText[]> {
        this._ticketTemplatesState.setTicketTemplateTextsInProgress(true);
        return this._ticketTemplatesApi.postTicketTemplateTexts(literals, id)
            .pipe(finalize(() => this._ticketTemplatesState.setTicketTemplateTextsInProgress(false)));
    }

    loadTicketTemplateLiterals(id: number, langCode?: string): void {
        this._ticketTemplatesState.setTicketTemplateLiteralsError(null);
        this._ticketTemplatesState.setTicketTemplateLiteralsInProgress(true);
        this._ticketTemplatesApi.getTicketTemplateLiterals(id, langCode)
            .pipe(
                catchError(error => {
                    this._ticketTemplatesState.setTicketTemplateLiteralsError(error);
                    return of(null);
                }),
                finalize(() => this._ticketTemplatesState.setTicketTemplateLiteralsInProgress(false))
            )
            .subscribe(texts =>
                this._ticketTemplatesState.setTicketTemplateLiterals(texts)
            );
    }

    getTicketTemplateLiterals$(): Observable<TicketTemplateText[]> {
        return this._ticketTemplatesState.getTicketTemplateLiterals$();
    }

    clearTicketTemplateLiterals(): void {
        this._ticketTemplatesState.setTicketTemplateLiterals(null);
    }

    isTicketTemplateLiteralsInProgress$(): Observable<boolean> {
        return this._ticketTemplatesState.isTicketTemplateLiteralsInProgress$();
    }

    updateTicketTemplateLiterals(literals: TicketTemplateText[], id: number): Observable<TicketTemplateText[]> {
        this._ticketTemplatesState.setTicketTemplateLiteralsInProgress(true);
        return this._ticketTemplatesApi.postTicketTemplateLiterals(literals, id)
            .pipe(finalize(() => this._ticketTemplatesState.setTicketTemplateLiteralsInProgress(false)));
    }

    loadTicketTemplateImages(
        { id, design: { format } }: TicketTemplate,
        language?: string,
        type?: TicketTemplateImageType
    ): void {
        this._ticketTemplatesState.setTicketTemplateImagesError(null);
        this._ticketTemplatesState.setTicketTemplateImagesInProgress(true);
        this._ticketTemplatesApi.getTicketTemplateImages$(id, format, language, type)
            .pipe(
                catchError(error => {
                    this._ticketTemplatesState.setTicketTemplateImagesError(error);
                    return of(null);
                }),
                finalize(() => this._ticketTemplatesState.setTicketTemplateImagesInProgress(false))
            )
            .subscribe(images =>
                this._ticketTemplatesState.setTicketTemplateImages(images)
            );
    }

    getTicketTemplateImages$(): Observable<GetTicketTemplateImage[]> {
        return this._ticketTemplatesState.getTicketTemplateImages$();
    }

    clearTicketTemplateImages(): void {
        this._ticketTemplatesState.setTicketTemplateImages(null);
    }

    isTicketTemplateImagesInProgress$(): Observable<boolean> {
        return this._ticketTemplatesState.isTicketTemplateImagesInProgress$();
    }

    updateTicketTemplateImages({ id, design: { format } }: TicketTemplate, images: PostTicketTemplateImage[]): Observable<void> {
        return this._ticketTemplatesApi.postTicketTemplateImages$(id, format, images);
    }

    deleteTicketTemplateImage(
        { id, design: { format } }: TicketTemplate, language: string, type: TicketTemplateImageType
    ): Observable<void> {
        return this._ticketTemplatesApi.deleteTicketTemplateImage$(id, format, language, type);
    }

    saveTicketTemplateImages(ticketTemplate: TicketTemplate, images: PostTicketTemplateImage[]): Observable<void[]> {
        this._ticketTemplatesState.setTicketTemplateImagesInProgress(true);

        const deletedImages = images?.filter(data => !data.image);
        const updatedImages = images?.filter(data => !!data.image);

        const operations = [
            ...(deletedImages?.map(image =>
                this.deleteTicketTemplateImage(ticketTemplate, image.language, image.type).pipe(
                    // if there is an error here, the image was probably been deleted before,
                    // ignore and continue with other operations (popup will appear)
                    catchError(() => of(null)))) || [])
        ];

        if (updatedImages?.length) {
            operations.push(this.updateTicketTemplateImages(ticketTemplate, updatedImages));
        }

        return zip(...operations)
            .pipe(finalize(() => this._ticketTemplatesState.setTicketTemplateImagesInProgress(false)));
    }

    downloadTicketPdfPreview$(ticketId: number, language: string): Observable<{ url: string }> {
        this._ticketTemplatesState.setTicketPdfPreviewDownloading(true);
        return this._ticketTemplatesApi.downloadTicketPdfPreview$(ticketId, language).pipe(
            finalize(() => this._ticketTemplatesState.setTicketPdfPreviewDownloading(false))
        );
    }

    isTicketPdfPreviewDownloading$(): Observable<boolean> {
        return this._ticketTemplatesState.isTicketPdfPreviewDownloading$();
    }

}
