import { TicketContentFormat } from '@admin-clients/cpanel/promoters/events/communication/data-access';
import {
    TicketContentImage,
    TicketContentImageRequest,
    TicketContentText
} from '@admin-clients/cpanel/promoters/events/data-access';
import { IdName } from '@admin-clients/shared/data-access/models';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { Injectable } from '@angular/core';
import { Observable, of, Subject, zip } from 'rxjs';
import {
    catchError,
    finalize,
    switchMap,
    takeUntil,
    tap
} from 'rxjs/operators';
import { VenueTemplatePriceTypesApi } from './api/venue-template-price-types.api';
import {
    GetPriceTypeRestricion,
    PostPriceTypeRestriction,
    RestrictedPriceZones
} from './models/price-type-restriction.model';
import { VenueTemplatePriceTypeChannelContent } from './models/venue-template-price-type-channel-content.model';
import { VenueTemplatePriceTypesState } from './state/venue-template-price-types.state';

@Injectable({ providedIn: 'root' })
export class VenueTemplatePriceTypesService {
    private _cancelPriceTypeTicketContents = new Subject<void>();

    constructor(
        private _venueTemplatePriceTypesApi: VenueTemplatePriceTypesApi,
        private _venueTemplatePriceTypesState: VenueTemplatePriceTypesState
    ) { }

    // CHANNEL CONTENT

    // single price type channel contents
    loadPriceTypeChannelContent(
        venueTemplateId: number,
        priceTypeId: number
    ): void {
        this._venueTemplatePriceTypesState.setPriceTypeChannelContentsLoading(
            true
        );
        this._venueTemplatePriceTypesApi
            .getVenueTplPriceTypeChannelContent(venueTemplateId, priceTypeId)
            .pipe(
                finalize(() =>
                    this._venueTemplatePriceTypesState.setPriceTypeChannelContentsLoading(
                        false
                    )
                )
            )
            .subscribe(contents =>
                this._venueTemplatePriceTypesState.setPriceTypeChannelContents({
                    priceTypeId,
                    contents
                })
            );
    }

    getPriceTypeChannelContent$(): Observable<{
        priceTypeId: number;
        contents: VenueTemplatePriceTypeChannelContent[];
    }> {
        return this._venueTemplatePriceTypesState.getTypeChannelContents$();
    }

    isVenueTemplatePriceTypeChannelContentLoading$(): Observable<boolean> {
        return this._venueTemplatePriceTypesState.isPriceTypeChannelContentsLoading$();
    }

    savePriceTypeChannelContent(
        venueTemplateId: number,
        priceTypeId: string,
        textsToSave: VenueTemplatePriceTypeChannelContent[]
    ): Observable<void> {
        this._venueTemplatePriceTypesState.setPriceTypesChannelContentsSaving(
            true
        );
        return this._venueTemplatePriceTypesApi
            .postEventPriceTypeChannelContent(
                venueTemplateId,
                priceTypeId,
                textsToSave
            )
            .pipe(
                finalize(() =>
                    this._venueTemplatePriceTypesState.setPriceTypesChannelContentsSaving(
                        false
                    )
                )
            );
    }

    isPriceTypeChannelContentSaving$(): Observable<boolean> {
        return this._venueTemplatePriceTypesState.isPriceTypesChannelContentsSaving$();
    }

    clearPriceTypesChannelContent(): void {
        return this._venueTemplatePriceTypesState.setPriceTypeChannelContents(
            null
        );
    }

    // RESTRICTIONS

    loadRestrictedPriceTypes(venueTplId: number): void {
        this._venueTemplatePriceTypesState.setRestrictedPriceTypesInProgress(
            true
        );
        this._venueTemplatePriceTypesApi
            .getVenueTplRestrictedPriceTypes(venueTplId)
            .pipe(
                finalize(() =>
                    this._venueTemplatePriceTypesState.setRestrictedPriceTypesInProgress(
                        false
                    )
                )
            )
            .subscribe(pricetypes =>
                this._venueTemplatePriceTypesState.setRestrictedPriceTypes(
                    pricetypes
                )
            );
    }

    getRestrictedPriceTypes$(): Observable<RestrictedPriceZones> {
        return this._venueTemplatePriceTypesState.getRestrictedPriceTypes$();
    }

    clearRestrictedPriceTypes(): void {
        this._venueTemplatePriceTypesState.setRestrictedPriceTypes(null);
    }

    // wtf, load method that returns the result? so rare... check and make it work as usually
    loadPriceTypeRestriction$(
        venueTplId: number,
        priceTypeId: number
    ): Observable<GetPriceTypeRestricion> {
        this._venueTemplatePriceTypesState.setPriceTypeRestrictionInProgress(
            true
        );
        return this._venueTemplatePriceTypesApi
            .getPriceTypeRestriction(venueTplId, priceTypeId)
            .pipe(
                tap(restriction =>
                    this._venueTemplatePriceTypesState.setPriceTypeRestriction(
                        restriction
                    )
                ),
                finalize(() =>
                    this._venueTemplatePriceTypesState.setPriceTypeRestrictionInProgress(
                        false
                    )
                )
            );
    }

    clearPriceTypeRestrictions(): void {
        this._venueTemplatePriceTypesState.setPriceTypeRestriction(null);
    }

    getPriceTypeRestriction$(): Observable<GetPriceTypeRestricion> {
        return this._venueTemplatePriceTypesState.getPriceTypeRestriction$();
    }

    isPriceTypesRestrictionInProgress$(): Observable<boolean> {
        return this._venueTemplatePriceTypesState.isPriceTypeRestrictionInProgress$();
    }

    savePriceTypeRestriction(
        venueTplId: number,
        priceTypeId: number,
        restriction: PostPriceTypeRestriction
    ): Observable<void> {
        this._venueTemplatePriceTypesState.setPriceTypeRestrictionInProgress(
            true
        );
        return this._venueTemplatePriceTypesApi
            .postPriceTypeRestriction(venueTplId, priceTypeId, restriction)
            .pipe(
                finalize(() =>
                    this._venueTemplatePriceTypesState.setPriceTypeRestrictionInProgress(
                        false
                    )
                )
            );
    }

    deletePriceTypeRestriction(
        venueTplId: number,
        priceTypeId: number
    ): Observable<void> {
        this._venueTemplatePriceTypesState.setPriceTypeRestrictionInProgress(
            true
        );
        return this._venueTemplatePriceTypesApi
            .deletePriceTypeRestriction(venueTplId, priceTypeId)
            .pipe(
                finalize(() =>
                    this._venueTemplatePriceTypesState.setPriceTypeRestrictionInProgress(
                        false
                    )
                )
            );
    }

    // TICKET CONTENTS

    cancelAllPriceTypeTicketContents(): void {
        this._cancelPriceTypeTicketContents.next();
    }

    loadAllPriceTypeTicketContents(
        templateId: number,
        priceTypeId: number
    ): void {
        this.loadPriceTypeTicketPdfTexts(templateId, priceTypeId);
        this.loadPriceTypeTicketPdfImages(templateId, priceTypeId);
        this.loadPriceTypeTicketPrinterTexts(templateId, priceTypeId);
        this.loadPriceTypeTicketPrinterImages(templateId, priceTypeId);
        this.loadPriceTypeTicketPassbookTexts(templateId, priceTypeId);
        this.loadPriceTypeTicketPassbookImages(templateId, priceTypeId);
    }

    isPriceTypeTicketContentsLoading$(): Observable<boolean> {
        return booleanOrMerge([
            this.isPriceTypeTicketPdfTextsLoading$(),
            this.isPriceTypeTicketPdfImagesLoading$(),
            this.isPriceTypeTicketPrinterTextsLoading$(),
            this.isPriceTypeTicketPrinterImagesLoading$(),
            this.isPriceTypeTicketPassbookTextsLoading$(),
            this.isPriceTypeTicketPassbookImagesLoading$()
        ]);
    }

    clearAllPriceTypeTicketContents(): void {
        this.clearPriceTypesTicketPdfTexts();
        this.clearPriceTypesTicketPdfImages();
        this.clearPriceTypesTicketPrinterTexts();
        this.clearPriceTypesTicketPrinterImages();
        this.clearPriceTypesTicketPassbookTexts();
        this.clearPriceTypesTicketPassbookImages();
    }

    loadModifiedTicketContentPriceTypes(venueTplId: number): void {
        this._venueTemplatePriceTypesState.setModifiedTicketContentPriceTypesLoading(
            true
        );
        this._venueTemplatePriceTypesApi
            .getModifiedTicketContentPriceTypes(venueTplId)
            .pipe(
                takeUntil(this._cancelPriceTypeTicketContents),
                finalize(() =>
                    this._venueTemplatePriceTypesState.setModifiedTicketContentPriceTypesLoading(
                        false
                    )
                )
            )
            .subscribe(contents =>
                this._venueTemplatePriceTypesState.setModifiedTicketContentPriceTypes(
                    contents
                )
            );
    }

    getModifiedTicketContentPriceTypes$(): Observable<IdName[]> {
        return this._venueTemplatePriceTypesState.getModifiedTicketContentPriceTypes$();
    }

    // PRICE TYPE TICKET PDF TEXTS

    loadPriceTypeTicketPdfTexts(venueTplId: number, priceTypeId: number): void {
        this._venueTemplatePriceTypesState.setPriceTypeTicketPdfTextsLoading(
            true
        );
        this._venueTemplatePriceTypesApi
            .getPriceTypeTicketContentTexts(
                TicketContentFormat.pdf,
                venueTplId,
                priceTypeId
            )
            .pipe(
                takeUntil(this._cancelPriceTypeTicketContents),
                finalize(() =>
                    this._venueTemplatePriceTypesState.setPriceTypeTicketPdfTextsLoading(
                        false
                    )
                )
            )
            .subscribe(contents =>
                this._venueTemplatePriceTypesState.setPriceTypeTicketPdfTexts(
                    contents
                )
            );
    }

    getPriceTypeTicketPdfTexts$(): Observable<TicketContentText[]> {
        return this._venueTemplatePriceTypesState.getPriceTypeTicketPdfTexts$();
    }

    isPriceTypeTicketPdfTextsLoading$(): Observable<boolean> {
        return this._venueTemplatePriceTypesState.isPriceTypeTicketPdfTextsLoading$();
    }

    savePriceTypeTicketPdfTexts(
        venueTplId: number,
        priceTypeId: number,
        textsToSave: TicketContentText[]
    ): Observable<void> {
        if (!textsToSave?.length) {
            return of<void>(undefined);
        }
        this._venueTemplatePriceTypesState.setPriceTypeTicketPdfTextsLoading(
            true
        );
        return this._venueTemplatePriceTypesApi
            .postPriceTypeTicketContentTexts(
                TicketContentFormat.pdf,
                venueTplId,
                priceTypeId,
                textsToSave
            )
            .pipe(
                finalize(() =>
                    this._venueTemplatePriceTypesState.setPriceTypeTicketPdfTextsLoading(
                        false
                    )
                )
            );
    }

    clearPriceTypesTicketPdfTexts(): void {
        return this._venueTemplatePriceTypesState.setPriceTypeTicketPdfTexts(
            null
        );
    }

    // PRICE TYPE TICKET PDF IMAGES

    loadPriceTypeTicketPdfImages(
        venueTplId: number,
        priceTypeId: number
    ): void {
        this._venueTemplatePriceTypesState.setPriceTypeTicketPdfImagesLoading(
            true
        );
        this._venueTemplatePriceTypesApi
            .getPriceTypeTicketContentImages(
                TicketContentFormat.pdf,
                venueTplId,
                priceTypeId
            )
            .pipe(
                takeUntil(this._cancelPriceTypeTicketContents),
                finalize(() =>
                    this._venueTemplatePriceTypesState.setPriceTypeTicketPdfImagesLoading(
                        false
                    )
                )
            )
            .subscribe(images =>
                this._venueTemplatePriceTypesState.setPriceTypeTicketPdfImages(
                    images
                )
            );
    }

    getPriceTypeTicketPdfImages$(): Observable<TicketContentImage[]> {
        return this._venueTemplatePriceTypesState.getPriceTypeTicketPdfImages$();
    }

    isPriceTypeTicketPdfImagesLoading$(): Observable<boolean> {
        return this._venueTemplatePriceTypesState.isPriceTypeTicketPdfImagesLoading$();
    }

    savePriceTypeTicketPdfImages(
        venueTplId: number,
        priceTypeId: number,
        images: TicketContentImageRequest[]
    ): Observable<void> {
        if (!images?.length) {
            return null;
        }
        this._venueTemplatePriceTypesState.setPriceTypeTicketPdfImagesLoading(
            true
        );
        return this._venueTemplatePriceTypesApi
            .postPriceTypeTicketContentImages(
                TicketContentFormat.pdf,
                venueTplId,
                priceTypeId,
                images
            )
            .pipe(
                finalize(() =>
                    this._venueTemplatePriceTypesState.setPriceTypeTicketPdfImagesLoading(
                        false
                    )
                )
            );
    }

    deletePriceTypeTicketPdfImages(
        venueTplId: number,
        priceTypeId: number,
        imagesToDelete: TicketContentImageRequest[]
    ): Observable<void> {
        if (!imagesToDelete?.length) {
            return null;
        }
        this._venueTemplatePriceTypesState.setPriceTypeTicketPdfImagesLoading(
            true
        );
        return zip(
            ...imagesToDelete.map(request =>
                this._venueTemplatePriceTypesApi.deletePriceTypeTicketContentImages(
                    TicketContentFormat.pdf,
                    venueTplId,
                    priceTypeId,
                    request.language,
                    request.type
                )
            )
        ).pipe(
            switchMap(() => of(null)),
            catchError(() => of(null)),
            finalize(() =>
                this._venueTemplatePriceTypesState.setPriceTypeTicketPdfImagesLoading(
                    false
                )
            )
        );
    }

    clearPriceTypesTicketPdfImages(): void {
        return this._venueTemplatePriceTypesState.setPriceTypeTicketPdfImages(
            null
        );
    }

    // PRICE TYPE TICKET PRINTER TEXTS

    loadPriceTypeTicketPrinterTexts(
        venueTplId: number,
        priceTypeId: number
    ): void {
        this._venueTemplatePriceTypesState.setPriceTypeTicketPrinterTextsLoading(
            true
        );
        this._venueTemplatePriceTypesApi
            .getPriceTypeTicketContentTexts(
                TicketContentFormat.printer,
                venueTplId,
                priceTypeId
            )
            .pipe(
                takeUntil(this._cancelPriceTypeTicketContents),
                finalize(() =>
                    this._venueTemplatePriceTypesState.setPriceTypeTicketPrinterTextsLoading(
                        false
                    )
                )
            )
            .subscribe(contents =>
                this._venueTemplatePriceTypesState.setPriceTypeTicketPrinterTexts(
                    contents
                )
            );
    }

    getPriceTypeTicketPrinterTexts$(): Observable<TicketContentText[]> {
        return this._venueTemplatePriceTypesState.getPriceTypeTicketPrinterTexts$();
    }

    isPriceTypeTicketPrinterTextsLoading$(): Observable<boolean> {
        return this._venueTemplatePriceTypesState.isPriceTypeTicketPrinterTextsLoading$();
    }

    savePriceTypeTicketPrinterTexts(
        venueTplId: number,
        priceTypeId: number,
        textsToSave: TicketContentText[]
    ): Observable<void> {
        if (!textsToSave?.length) {
            return of<void>(undefined);
        }
        this._venueTemplatePriceTypesState.setPriceTypeTicketPrinterTextsLoading(
            true
        );
        return this._venueTemplatePriceTypesApi
            .postPriceTypeTicketContentTexts(
                TicketContentFormat.printer,
                venueTplId,
                priceTypeId,
                textsToSave
            )
            .pipe(
                finalize(() =>
                    this._venueTemplatePriceTypesState.setPriceTypeTicketPrinterTextsLoading(
                        false
                    )
                )
            );
    }

    clearPriceTypesTicketPrinterTexts(): void {
        return this._venueTemplatePriceTypesState.setPriceTypeTicketPrinterTexts(
            null
        );
    }

    // PRICE TYPE TICKET PRINTER IMAGES

    loadPriceTypeTicketPrinterImages(
        venueTplId: number,
        priceTypeId: number
    ): void {
        this._venueTemplatePriceTypesState.setPriceTypeTicketPrinterImagesLoading(
            true
        );
        this._venueTemplatePriceTypesApi
            .getPriceTypeTicketContentImages(
                TicketContentFormat.printer,
                venueTplId,
                priceTypeId
            )
            .pipe(
                takeUntil(this._cancelPriceTypeTicketContents),
                finalize(() =>
                    this._venueTemplatePriceTypesState.setPriceTypeTicketPrinterImagesLoading(
                        false
                    )
                )
            )
            .subscribe(images =>
                this._venueTemplatePriceTypesState.setPriceTypeTicketPrinterImages(
                    images
                )
            );
    }

    getPriceTypeTicketPrinterImages$(): Observable<TicketContentImage[]> {
        return this._venueTemplatePriceTypesState.getPriceTypeTicketPrinterImages$();
    }

    isPriceTypeTicketPrinterImagesLoading$(): Observable<boolean> {
        return this._venueTemplatePriceTypesState.isPriceTypeTicketPrinterImagesLoading$();
    }

    savePriceTypeTicketPrinterImages(
        venueTplId: number,
        priceTypeId: number,
        images: TicketContentImageRequest[]
    ): Observable<void> {
        if (!images?.length) {
            return null;
        }
        this._venueTemplatePriceTypesState.setPriceTypeTicketPrinterImagesLoading(
            true
        );
        return this._venueTemplatePriceTypesApi
            .postPriceTypeTicketContentImages(
                TicketContentFormat.printer,
                venueTplId,
                priceTypeId,
                images
            )
            .pipe(
                finalize(() =>
                    this._venueTemplatePriceTypesState.setPriceTypeTicketPrinterImagesLoading(
                        false
                    )
                )
            );
    }

    deletePriceTypeTicketPrinterImages(
        venueTplId: number,
        priceTypeId: number,
        imagesToDelete: TicketContentImageRequest[]
    ): Observable<void> {
        if (!imagesToDelete?.length) {
            return null;
        }
        this._venueTemplatePriceTypesState.setPriceTypeTicketPrinterImagesLoading(
            true
        );
        return zip(
            ...imagesToDelete.map(request =>
                this._venueTemplatePriceTypesApi.deletePriceTypeTicketContentImages(
                    TicketContentFormat.printer,
                    venueTplId,
                    priceTypeId,
                    request.language,
                    request.type
                )
            )
        ).pipe(
            switchMap(() => of(null)),
            catchError(() => of(null)),
            finalize(() =>
                this._venueTemplatePriceTypesState.setPriceTypeTicketPrinterImagesLoading(
                    false
                )
            )
        );
    }

    clearPriceTypesTicketPrinterImages(): void {
        return this._venueTemplatePriceTypesState.setPriceTypeTicketPrinterImages(
            null
        );
    }

    // PRICE TYPE TICKET PDF TEXTS

    loadPriceTypeTicketPassbookTexts(
        venueTplId: number,
        priceTypeId: number
    ): void {
        this._venueTemplatePriceTypesState.setPriceTypeTicketPassbookTextsLoading(
            true
        );
        this._venueTemplatePriceTypesApi
            .getPriceTypeTicketContentTexts(
                TicketContentFormat.passbook,
                venueTplId,
                priceTypeId
            )
            .pipe(
                takeUntil(this._cancelPriceTypeTicketContents),
                finalize(() =>
                    this._venueTemplatePriceTypesState.setPriceTypeTicketPassbookTextsLoading(
                        false
                    )
                )
            )
            .subscribe(contents =>
                this._venueTemplatePriceTypesState.setPriceTypeTicketPassbookTexts(
                    contents
                )
            );
    }

    getPriceTypeTicketPassbookTexts$(): Observable<TicketContentText[]> {
        return this._venueTemplatePriceTypesState.getPriceTypeTicketPassbookTexts$();
    }

    isPriceTypeTicketPassbookTextsLoading$(): Observable<boolean> {
        return this._venueTemplatePriceTypesState.isPriceTypeTicketPassbookTextsLoading$();
    }

    savePriceTypeTicketPassbookTexts(
        venueTplId: number,
        priceTypeId: number,
        textsToSave: TicketContentText[]
    ): Observable<void> {
        if (!textsToSave?.length) {
            return of<void>(undefined);
        }
        this._venueTemplatePriceTypesState.setPriceTypeTicketPassbookTextsLoading(
            true
        );
        return this._venueTemplatePriceTypesApi
            .postPriceTypeTicketContentTexts(
                TicketContentFormat.passbook,
                venueTplId,
                priceTypeId,
                textsToSave
            )
            .pipe(
                finalize(() =>
                    this._venueTemplatePriceTypesState.setPriceTypeTicketPassbookTextsLoading(
                        false
                    )
                )
            );
    }

    clearPriceTypesTicketPassbookTexts(): void {
        return this._venueTemplatePriceTypesState.setPriceTypeTicketPassbookTexts(
            null
        );
    }

    // PRICE TYPE TICKET PASSBOOK IMAGES

    loadPriceTypeTicketPassbookImages(
        venueTplId: number,
        priceTypeId: number
    ): void {
        this._venueTemplatePriceTypesState.setPriceTypeTicketPassbookImagesLoading(
            true
        );
        this._venueTemplatePriceTypesApi
            .getPriceTypeTicketContentImages(
                TicketContentFormat.passbook,
                venueTplId,
                priceTypeId
            )
            .pipe(
                takeUntil(this._cancelPriceTypeTicketContents),
                finalize(() =>
                    this._venueTemplatePriceTypesState.setPriceTypeTicketPassbookImagesLoading(
                        false
                    )
                )
            )
            .subscribe(images =>
                this._venueTemplatePriceTypesState.setPriceTypeTicketPassbookImages(
                    images
                )
            );
    }

    getPriceTypeTicketPassbookImages$(): Observable<TicketContentImage[]> {
        return this._venueTemplatePriceTypesState.getPriceTypeTicketPassbookImages$();
    }

    isPriceTypeTicketPassbookImagesLoading$(): Observable<boolean> {
        return this._venueTemplatePriceTypesState.isPriceTypeTicketPassbookImagesLoading$();
    }

    savePriceTypeTicketPassbookImages(
        venueTplId: number,
        priceTypeId: number,
        images: TicketContentImageRequest[]
    ): Observable<void> {
        if (!images?.length) {
            return null;
        }
        this._venueTemplatePriceTypesState.setPriceTypeTicketPassbookImagesLoading(
            true
        );
        return this._venueTemplatePriceTypesApi
            .postPriceTypeTicketContentImages(
                TicketContentFormat.passbook,
                venueTplId,
                priceTypeId,
                images
            )
            .pipe(
                finalize(() =>
                    this._venueTemplatePriceTypesState.setPriceTypeTicketPassbookImagesLoading(
                        false
                    )
                )
            );
    }

    deletePriceTypeTicketPassbookImages(
        venueTplId: number,
        priceTypeId: number,
        imagesToDelete: TicketContentImageRequest[]
    ): Observable<void> {
        if (!imagesToDelete?.length) {
            return null;
        }
        this._venueTemplatePriceTypesState.setPriceTypeTicketPassbookImagesLoading(
            true
        );
        return zip(
            ...imagesToDelete.map(request =>
                this._venueTemplatePriceTypesApi.deletePriceTypeTicketContentImages(
                    TicketContentFormat.passbook,
                    venueTplId,
                    priceTypeId,
                    request.language,
                    request.type
                )
            )
        ).pipe(
            switchMap(() => of(null)),
            catchError(() => of(null)),
            finalize(() =>
                this._venueTemplatePriceTypesState.setPriceTypeTicketPassbookImagesLoading(
                    false
                )
            )
        );
    }

    clearPriceTypesTicketPassbookImages(): void {
        return this._venueTemplatePriceTypesState.setPriceTypeTicketPassbookImages(
            null
        );
    }
}
