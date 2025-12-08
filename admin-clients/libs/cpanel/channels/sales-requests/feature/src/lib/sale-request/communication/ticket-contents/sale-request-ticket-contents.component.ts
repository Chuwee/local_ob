import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ChannelType } from '@admin-clients/cpanel/channels/data-access';
import {
    PutSaleRequestTicketContentImage, SaleRequestTicketContentImageField,
    SaleRequest, SalesRequestsService
} from '@admin-clients/cpanel-channels-sales-requests-data-access';
import { MessageDialogService, EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { booleanOrMerge, FormControlHandler } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, QueryList, ViewChild, ViewChildren } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { BehaviorSubject, forkJoin, Observable, of, Subject, throwError } from 'rxjs';
import { filter, map, shareReplay, tap } from 'rxjs/operators';
import { SaleRequestTicketContentPdfComponent } from './ticket-content-pdf/sale-request-ticket-content-pdf.component';
import { SaleRequestTicketContentPrinterComponent } from './ticket-content-printer/sale-request-ticket-content-printer.component';

@Component({
    selector: 'app-sale-request-ticket-contents',
    templateUrl: './sale-request-ticket-contents.component.html',
    styleUrls: ['./sale-request-ticket-contents.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SaleRequestTicketContentsComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    private _language = new BehaviorSubject<string>(null);

    @ViewChildren(MatExpansionPanel)
    private _matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    @ViewChild('pdfContent')
    private _pdfContentComponent: SaleRequestTicketContentPdfComponent;

    @ViewChild('printerContent')
    private _printerContentComponent: SaleRequestTicketContentPrinterComponent;

    form: UntypedFormGroup;
    isLoadingOrSaving$: Observable<boolean>;
    saleRequest$: Observable<SaleRequest>;
    languageList$: Observable<string[]>;
    selectedLanguage$ = this._language.asObservable();
    channelTypes = ChannelType;

    constructor(
        private _fb: UntypedFormBuilder,
        private _salesRequestsService: SalesRequestsService,
        private _ephemeralMessage: EphemeralMessageService,
        private _messageDialogService: MessageDialogService
    ) { }

    ngOnInit(): void {
        this.form = this._fb.group({});

        this.isLoadingOrSaving$ = booleanOrMerge([
            this._salesRequestsService.isTicketPdfContentTextsLoading$(),
            this._salesRequestsService.isTicketPdfContentImagesLoading$(),
            this._salesRequestsService.isTicketPdfContentImagesSaving$(),
            this._salesRequestsService.isTicketPdfContentImagesRemoving$(),
            this._salesRequestsService.isTicketPdfContentTemplateLoading$(),
            this._salesRequestsService.isTicketPdfPreviewDownloading$(),
            this._salesRequestsService.isTicketPrinterContentTextsLoading$(),
            this._salesRequestsService.isTicketPrinterContentImagesLoading$(),
            this._salesRequestsService.isTicketPrinterContentImagesSaving$(),
            this._salesRequestsService.isTicketPrinterContentImagesRemoving$(),
            this._salesRequestsService.isTicketPrinterContentTemplateLoading$()
        ]);

        this.saleRequest$ = this._salesRequestsService.getSaleRequest$()
            .pipe(
                filter(saleRequest => !!saleRequest),
                shareReplay(1)
            );

        this.languageList$ = this._salesRequestsService.getSaleRequest$()
            .pipe(
                map(saleRequest => saleRequest.languages),
                tap(languages => {
                    if (!languages?.default && languages?.selected.length) {
                        languages.default = languages.selected[0];
                    }
                    this._language.next(languages?.default);
                }),
                map(languages => languages?.selected)
            );
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    canChangeLanguage: (() => Observable<boolean>) = () => this.validateIfCanChangeLanguage();

    cancel(): void {
        this._pdfContentComponent.cancel();
        if (this._printerContentComponent) {
            this._printerContentComponent.cancel();
        }
    }

    getImageFields(
        contentForm: UntypedFormGroup, imageFields: SaleRequestTicketContentImageField[], language: string
    ): { [key: string]: PutSaleRequestTicketContentImage[] } {
        const imagesToSave: PutSaleRequestTicketContentImage[] = [];
        const imagesToDelete: PutSaleRequestTicketContentImage[] = [];
        imageFields.forEach(imageField => {
            const field = contentForm.get(imageField.formField);
            if (field.dirty) {
                const imageValue = field.value;
                if (imageValue?.data) {
                    imagesToSave.push({ type: imageField.type, image: imageValue?.data, language });
                } else {
                    imagesToDelete.push({ type: imageField.type, image: null, language });
                }
            }
        });
        return { imagesToSave, imagesToDelete };
    }

    save(): void {
        this.save$().subscribe(() => {
            this.form.markAsPristine();
        });
    }

    save$(): Observable<void[]> {
        if (this.form.valid) {
            const obs$: Observable<void>[] = [];
            if (this._pdfContentComponent) {
                obs$.push(...this._pdfContentComponent.save(this.getImageFields.bind(this)));
            }
            if (this._printerContentComponent) {
                obs$.push(...this._printerContentComponent.save(this.getImageFields.bind(this)));
            }
            return forkJoin(obs$)
                .pipe(
                    tap(() => {
                        this._ephemeralMessage.showSaveSuccess();
                    }));
        } else {
            FormControlHandler.markAllControlsAsTouched(this.form);
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanelQueryList);
            return throwError(() => 'invalid form');
        }
    }

    changeLanguage(newLanguage: string): void {
        this._language.next(newLanguage);
    }

    private validateIfCanChangeLanguage(): Observable<boolean> {
        if (this.form.dirty) {
            return this._messageDialogService.defaultUnsavedChangesWarn();
        }
        return of(true);
    }
}
