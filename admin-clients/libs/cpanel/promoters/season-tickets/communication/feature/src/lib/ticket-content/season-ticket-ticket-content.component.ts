import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, QueryList, ViewChild, ViewChildren } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { BehaviorSubject, forkJoin, Observable, of, Subject, throwError } from 'rxjs';
import { map, take, takeLast, tap } from 'rxjs/operators';
import { SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import {
    SeasonTicketCommunicationService, SeasonTicketTicketContentImageFields,
    SeasonTicketTicketContentImageRequest, SeasonTicketTicketContentText, SeasonTicketTicketContentTextFields,
    SeasonTicketTicketTemplate, SeasonTicketTicketTemplateFields, provideSeasonTicketCommunicationService
} from '@admin-clients/cpanel-promoters-season-tickets-communication-data-access';
import { TicketTemplatesService } from '@admin-clients/cpanel-promoters-ticket-templates-data-access';
import { TicketType } from '@admin-clients/shared/common/data-access';
import {
    EphemeralMessageService, IconManagerService, MessageDialogService, starIcon, ticketPassbookIcon, ticketPdfIcon, ticketPrinterIcon
} from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { SeasonTicketTicketContentPassbookComponent } from './ticket-content-passbook/season-ticket-ticket-content-passbook.component';
import { SeasonTicketTicketContentPdfComponent } from './ticket-content-pdf/season-ticket-ticket-content-pdf.component';
import { SeasonTicketTicketContentPrinterComponent } from './ticket-content-printer/season-ticket-ticket-content-printer.component';
import { SeasonTicketTicketTemplatesComponent } from './ticket-templates/season-ticket-ticket-templates.component';

@Component({
    selector: 'app-season-ticket-ticket-content',
    templateUrl: './season-ticket-ticket-content.component.html',
    styleUrls: ['./season-ticket-ticket-content.component.scss'],
    providers: [
        provideSeasonTicketCommunicationService()
    ],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SeasonTicketTicketContentComponent implements OnInit, OnDestroy, WritingComponent {
    private _selectedLanguage = new BehaviorSubject<string>(null);
    private _onDestroy = new Subject<void>();

    @ViewChildren(MatExpansionPanel)
    private _matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    @ViewChild('templatesContent')
    private _templatesComponent: SeasonTicketTicketTemplatesComponent;

    @ViewChild('pdfContent')
    private _pdfContentComponent: SeasonTicketTicketContentPdfComponent;

    @ViewChild('printerContent')
    private _printerContentComponent: SeasonTicketTicketContentPrinterComponent;

    @ViewChild('passbookContent')
    private _passbookContentComponent: SeasonTicketTicketContentPassbookComponent;

    ticketType = TicketType.general;
    form: UntypedFormGroup;
    isLoadingOrSaving$: Observable<boolean>;
    selectedLanguage$ = this._selectedLanguage.asObservable();
    languages$: Observable<string[]>;
    passbookEnabled = false;

    constructor(
        private _fb: UntypedFormBuilder,
        private _ephemeralMessageService: EphemeralMessageService,
        private _seasonTicketsService: SeasonTicketsService,
        private _seasonTicketCommunicationService: SeasonTicketCommunicationService,
        private _ticketTemplatesService: TicketTemplatesService,
        private _msgDialogService: MessageDialogService,
        iconManagerService: IconManagerService
    ) {
        iconManagerService.addIconDefinition(ticketPdfIcon, ticketPrinterIcon, ticketPassbookIcon, starIcon);
    }

    ngOnInit(): void {
        this.form = this._fb.group({});

        this.languages$ = this._seasonTicketsService.seasonTicket.get$()
            .pipe(
                take(1),
                map(seasonTicket => seasonTicket.settings.languages),
                tap(languages => this._selectedLanguage.next(languages.default)),
                map(languages => languages.selected)
            );
        this.passbookEnabled = !(this.ticketType === TicketType.invitation);

        this.isLoadingOrSaving$ = booleanOrMerge([
            this._ticketTemplatesService.isTicketTemplatesLoading$(),
            this._seasonTicketCommunicationService.isSeasonTicketTicketTemplatesLoading$(),
            this._seasonTicketCommunicationService.isSeasonTicketTicketTemplatesSaving$(),
            this._seasonTicketCommunicationService.isSeasonTicketTicketPdfContentTextsLoading$(),
            this._seasonTicketCommunicationService.isSeasonTicketTicketPdfContentImagesLoading$(),
            this._seasonTicketCommunicationService.isSeasonTicketTicketPdfContentTextsSaving$(),
            this._seasonTicketCommunicationService.isSeasonTicketTicketPdfContentImagesSaving$(),
            this._seasonTicketCommunicationService.isSeasonTicketTicketPdfContentImagesRemoving$(),
            this._seasonTicketCommunicationService.isTicketPdfPreviewDownloading$(),
            this._seasonTicketCommunicationService.isSeasonTicketTicketPrinterContentTextsLoading$(),
            this._seasonTicketCommunicationService.isSeasonTicketTicketPrinterContentImagesLoading$(),
            this._seasonTicketCommunicationService.isSeasonTicketTicketPrinterContentTextsSaving$(),
            this._seasonTicketCommunicationService.isSeasonTicketTicketPrinterContentImagesSaving$(),
            this._seasonTicketCommunicationService.isSeasonTicketTicketPrinterContentImagesRemoving$(),
            this._seasonTicketCommunicationService.isSeasonTicketTicketPassbookContentImagesLoading$(),
            this._seasonTicketCommunicationService.isSeasonTicketTicketPassbookContentImagesSaving$(),
            this._seasonTicketCommunicationService.isSeasonTicketTicketPassbookContentImagesRemoving$()
        ]);
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    canChangeLanguage: (() => Observable<boolean>) = () => this.validateIfCanChangeLanguage();

    cancel(): void {
        if (this._templatesComponent) {
            this._templatesComponent.cancel();
        }
        if (this._pdfContentComponent) {
            this._pdfContentComponent.cancel();
        }
        if (this._printerContentComponent) {
            this._printerContentComponent.cancel();
        }
        if (this._passbookContentComponent) {
            this._passbookContentComponent.cancel();
        }
    }

    getTemplateFields(contentForm: UntypedFormGroup, templateFields: SeasonTicketTicketTemplateFields[]): SeasonTicketTicketTemplate[] {
        const templatesToSave: SeasonTicketTicketTemplate[] = [];
        templateFields.forEach(templateField => {
            const field = contentForm.get(templateField.formField);
            if (field.dirty) {
                templatesToSave.push({ id: field.value, type: templateField.type, format: templateField.format });
            }
        });
        return templatesToSave;
    }

    getTextFields(
        contentForm: UntypedFormGroup,
        textFields: SeasonTicketTicketContentTextFields[],
        language: string
    ): SeasonTicketTicketContentText[] {
        const textsToSave: SeasonTicketTicketContentText[] = [];
        textFields.forEach(textField => {
            const field = contentForm.get(textField.formField);
            if (field.dirty) {
                textsToSave.push({ type: textField.type, value: field.value, language });
            }
        });
        return textsToSave;
    }

    getImageFields(
        contentForm: UntypedFormGroup, imageFields: SeasonTicketTicketContentImageFields[], language: string
    ): { [key: string]: SeasonTicketTicketContentImageRequest[] } {
        const imagesToSave: SeasonTicketTicketContentImageRequest[] = [];
        const imagesToDelete: SeasonTicketTicketContentImageRequest[] = [];
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

    save$(): Observable<unknown> {
        if (this.form.valid) {
            const obs$: Observable<void | void[]>[] = [];
            if (this._templatesComponent) {
                obs$.push(...this._templatesComponent.save(this.getTemplateFields.bind(this)));
            }
            if (this._pdfContentComponent) {
                obs$.push(...this._pdfContentComponent.save(this.getTextFields.bind(this), this.getImageFields.bind(this)));
            }
            if (this._printerContentComponent) {
                obs$.push(...this._printerContentComponent.save(this.getTextFields.bind(this), this.getImageFields.bind(this)));
            }
            if (this._passbookContentComponent) {
                obs$.push(...this._passbookContentComponent.save(this.getTextFields.bind(this), this.getImageFields.bind(this)));
            }
            return forkJoin(obs$)
                .pipe(
                    takeLast(1),
                    tap(() => {
                        this._ephemeralMessageService.showSaveSuccess();
                    })
                );
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanelQueryList);
            return throwError(() => 'invalid form');
        }
    }

    save(): void {
        this.save$().subscribe();
    }

    changeLanguage(newLanguage: string): void {
        this._selectedLanguage.next(newLanguage);
    }

    private validateIfCanChangeLanguage(): Observable<boolean> {
        if (this.form.dirty) {
            return this._msgDialogService.defaultUnsavedChangesWarn();
        }
        return of(true);
    }
}
