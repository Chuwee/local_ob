import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { EventCommunicationService } from '@admin-clients/cpanel/promoters/events/communication/data-access';
import {
    EventAvetConnection, EventsService, TicketContentImageType, TicketContentImageRequest, TicketContentTextType, TicketContentText
} from '@admin-clients/cpanel/promoters/events/data-access';
import { VenueTemplatePriceTypesService } from '@admin-clients/cpanel/venues/venue-templates/data-access';
import { TicketTemplatesService } from '@admin-clients/cpanel-promoters-ticket-templates-data-access';
import { TicketType, EventType } from '@admin-clients/shared/common/data-access';
import {
    EphemeralMessageService, IconManagerService, starIcon, ticketPassbookIcon, ticketPdfIcon, ticketPrinterIcon
} from '@admin-clients/shared/common/ui/components';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import {
    VenueTemplate, VenueTemplatePriceType, VenueTemplatesService, VenueTemplateStatus
} from '@admin-clients/shared/venues/data-access/venue-tpls';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, Input, OnDestroy, OnInit, QueryList, ViewChildren } from '@angular/core';
import { AbstractControl, UntypedFormBuilder, UntypedFormControl, UntypedFormGroup } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { BehaviorSubject, combineLatest, forkJoin, Observable, Subject, throwError } from 'rxjs';
import { distinctUntilChanged, filter, first, map, shareReplay, take, takeUntil, tap } from 'rxjs/operators';
import { PASSBOOK_IMAGE_FIELDS, PASSBOOK_TEXT_FIELDS } from './models/ticket-content-passbook';
import { PDF_IMAGE_FIELDS, PDF_TEXT_FIELDS } from './models/ticket-content-pdf';
import { PRINTER_IMAGE_FIELDS, PRINTER_TEXT_FIELDS } from './models/ticket-content-printer';

type SelectPriceType = VenueTemplatePriceType & { edited?: boolean };

@Component({
    selector: 'app-price-type-ticket-content',
    templateUrl: './price-type-ticket-content.component.html',
    styleUrls: ['./price-type-ticket-content.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class PriceTypeTicketContentComponent implements OnInit, OnDestroy {
    private readonly _onDestroy = new Subject<void>();
    private _selectedLanguage = new BehaviorSubject<string>(null);

    @ViewChildren(MatExpansionPanel)
    private _matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    @Input() ticketType = TicketType.general;

    form: UntypedFormGroup;
    currentFormGroup: UntypedFormGroup;
    priceTypeCtrl: UntypedFormControl;
    venueTplCtrl: UntypedFormControl;

    isLoadingOrSaving$: Observable<boolean>;
    selectedLanguage$ = this._selectedLanguage.asObservable();
    languages$: Observable<string[]>;
    passbookEnabled$: Observable<boolean>;

    priceTypes$: Observable<SelectPriceType[]>;
    templates$: Observable<VenueTemplate[]>;

    pdfTexts$ = this._venueTemplatePriceTypesSrv.getPriceTypeTicketPdfTexts$()
        .pipe(map(texts => this.filterByLanguage(texts)));

    pdfImages$ = this._venueTemplatePriceTypesSrv.getPriceTypeTicketPdfImages$()
        .pipe(map(images => this.filterByLanguage(images)));

    printerTexts$ = this._venueTemplatePriceTypesSrv.getPriceTypeTicketPrinterTexts$()
        .pipe(map(texts => this.filterByLanguage(texts)));

    printerImages$ = this._venueTemplatePriceTypesSrv.getPriceTypeTicketPrinterImages$()
        .pipe(map(images => this.filterByLanguage(images)));

    passbookTexts$ = this._venueTemplatePriceTypesSrv.getPriceTypeTicketPassbookTexts$()
        .pipe(map(texts => this.filterByLanguage(texts)));

    passbookImages$ = this._venueTemplatePriceTypesSrv.getPriceTypeTicketPassbookImages$()
        .pipe(map(images => this.filterByLanguage(images)));

    constructor(
        private _fb: UntypedFormBuilder,
        private _eventsService: EventsService,
        private _ticketTemplatesService: TicketTemplatesService,
        private _iconManagerSrv: IconManagerService,
        private _venueTplSrv: VenueTemplatesService,
        private _venueTemplatePriceTypesSrv: VenueTemplatePriceTypesService,
        private _eventCommunicationService: EventCommunicationService,
        private _ephemeralMsg: EphemeralMessageService,
        private _ref: ChangeDetectorRef
    ) {
        this._iconManagerSrv.addIconDefinition(
            ticketPdfIcon,
            ticketPrinterIcon,
            ticketPassbookIcon,
            starIcon
        );
    }

    ngOnInit(): void {
        // base form
        this.form = this._fb.group({});

        this.priceTypeCtrl = this._fb.control(null);
        this.venueTplCtrl = this._fb.control(null);

        this._venueTplSrv.clearVenueTemplatePriceTypes();
        this._venueTplSrv.clearVenueTemplateList();

        this.passbookEnabled$ = this._eventsService.event.get$()
            .pipe(
                map(event =>
                    !(this.ticketType === TicketType.invitation ||
                        (event.type === EventType.avet && event.additional_config?.avet_config === EventAvetConnection.ws)
                    )
                )
            );

        this.priceTypes$ = combineLatest([
            this._venueTplSrv.getVenueTemplatePriceTypes$(),
            this._venueTemplatePriceTypesSrv.getModifiedTicketContentPriceTypes$()
        ]).pipe(
            filter(([priceTypes, modifiedPriceTypes]) => !!priceTypes && !!modifiedPriceTypes),
            map(([priceTypes, modifiedPriceTypes]) =>
                priceTypes.map(priceType => ({
                    ...priceType,
                    edited: !!modifiedPriceTypes.find(({ id }) => priceType.id === id)
                }))
            )
        );

        this.templates$ = this._venueTplSrv.getVenueTemplatesList$()
            .pipe(
                filter(v => v !== null),
                map(value => value.data),
                tap(venueTemplates => {
                    if (venueTemplates?.length === 1) {
                        this.venueTplCtrl.setValue(venueTemplates[0]);
                    }
                }),
                takeUntil(this._onDestroy),
                shareReplay(1)
            );

        // seteamos en el lenguaje default la lista
        this.languages$ = this._eventsService.event.get$()
            .pipe(
                take(1),
                map(event => event.settings.languages),
                tap(languages => this._selectedLanguage.next(languages.default)),
                map(languages => languages.selected)
            );

        combineLatest([
            this.priceTypeCtrl.valueChanges,
            this.languages$,
            this.selectedLanguage$
        ])
            .pipe(
                filter(data => !data.some(elem => !elem)),
                takeUntil(this._onDestroy)
            )
            .subscribe(([priceType, languages, lang]) => {
                const template = this.venueTplCtrl.value;
                if (!this.form.get([String(template.id), String(priceType.id)])) {
                    this.loadAll(template.id, priceType.id);
                }
                this.initDynamicFormFields(template.id, priceType.id, languages);
                this.changePriceType(template.id, priceType.id, lang);
            });

        this._eventsService.event.get$()
            .pipe(
                first(value => value !== null),
                takeUntil(this._onDestroy)
            )
            .subscribe(event => {
                const req = {
                    limit: 999,
                    offset: 0,
                    sort: 'name:asc',
                    eventId: event.id,
                    status: [VenueTemplateStatus.active]
                };
                this._venueTplSrv.loadVenueTemplatesList(req);
                this._eventCommunicationService.loadEventTicketTemplates(event.id);
                this._ticketTemplatesService.loadTicketTemplates({
                    entity_id: event.entity.id,
                    limit: 999,
                    sort: 'name:asc'
                });
            });

        // cargamos la tabla de precios asociada al recinto seleccionado
        this.venueTplCtrl.valueChanges
            .pipe(
                tap(() => this.priceTypeCtrl.setValue(null)),
                filter(venueTpl => venueTpl?.id),
                distinctUntilChanged(),
                takeUntil(this._onDestroy)
            )
            .subscribe(venueTpl => {
                this.currentFormGroup = null;
                this._venueTplSrv.clearVenueTemplatePriceTypes();
                this._venueTemplatePriceTypesSrv.loadModifiedTicketContentPriceTypes(venueTpl.id);
                this._venueTplSrv.loadVenueTemplatePriceTypes(venueTpl.id);
            });

        this.isLoadingOrSaving$ = booleanOrMerge([
            this._ticketTemplatesService.isTicketTemplatesLoading$(),
            this._venueTplSrv.isVenueTemplatePriceTypesLoading$(),
            this._venueTemplatePriceTypesSrv.isPriceTypeTicketContentsLoading$()
        ]);
    }

    ngOnDestroy(): void {
        this._venueTemplatePriceTypesSrv.cancelAllPriceTypeTicketContents();
        this._eventCommunicationService.cancelRequests();
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    cancel(): void {
        this.clearAll();
        this.form = this._fb.group({});
        this._selectedLanguage.next(this._selectedLanguage.getValue());
    }

    save$(): Observable<void[]> {
        if (this.form.valid && this.form.dirty) {
            const obs$: Observable<void>[] = [];
            Object.keys(this.form.value).forEach(templateId => {
                const templateForm = this.form.get(templateId);
                templateForm?.dirty &&
                    Object.keys(templateForm.value).forEach(priceTypeId => {
                        const priceTypeForm = this.form.get([templateId, priceTypeId]);
                        if (priceTypeForm?.dirty) {
                            // prepare PDF content save
                            const pdfContent = this.contentsToSave(priceTypeForm, 'pdf', PDF_TEXT_FIELDS, PDF_IMAGE_FIELDS);
                            // prepare PRINTER content save
                            const printerContent = this.contentsToSave(priceTypeForm, 'printer', PRINTER_TEXT_FIELDS, PRINTER_IMAGE_FIELDS);
                            // prepare PASSBOOK content save
                            const passbookContent = this.contentsToSave(
                                priceTypeForm, 'passbook', PASSBOOK_TEXT_FIELDS, PASSBOOK_IMAGE_FIELDS
                            );

                            obs$.push(
                                this._venueTemplatePriceTypesSrv.savePriceTypeTicketPdfTexts(
                                    +templateId, +priceTypeId, pdfContent.texts),
                                this._venueTemplatePriceTypesSrv.savePriceTypeTicketPdfImages(
                                    +templateId, +priceTypeId, pdfContent.images),
                                this._venueTemplatePriceTypesSrv.deletePriceTypeTicketPdfImages(
                                    +templateId, +priceTypeId, pdfContent.deleteImages),
                                this._venueTemplatePriceTypesSrv.savePriceTypeTicketPrinterTexts(
                                    +templateId, +priceTypeId, printerContent.texts),
                                this._venueTemplatePriceTypesSrv.savePriceTypeTicketPrinterImages(
                                    +templateId, +priceTypeId, printerContent.images),
                                this._venueTemplatePriceTypesSrv.deletePriceTypeTicketPrinterImages(
                                    +templateId, +priceTypeId, printerContent.deleteImages),
                                this._venueTemplatePriceTypesSrv.savePriceTypeTicketPassbookTexts(
                                    +templateId, +priceTypeId, passbookContent.texts),
                                this._venueTemplatePriceTypesSrv.savePriceTypeTicketPassbookImages(
                                    +templateId, +priceTypeId, passbookContent.images),
                                this._venueTemplatePriceTypesSrv.deletePriceTypeTicketPassbookImages(
                                    +templateId, +priceTypeId, passbookContent.deleteImages)
                            );
                        }
                    });
            });

            return forkJoin(obs$.filter(Boolean)).pipe(
                tap(() => this._ephemeralMsg.showSaveSuccess()),
                takeUntil(this._onDestroy)
            );
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanelQueryList);

            return throwError(() => 'invalid form');
        }
    }

    save(): void {
        this.save$().subscribe(() => {
            this.cancel();
        });
    }

    changeLanguage(newLanguage: string): void {
        this._selectedLanguage.next(newLanguage);
    }

    private contentsToSave(
        form: AbstractControl, group: string, textFields: { type: TicketContentTextType }[], imageFields: { type: TicketContentImageType }[]
    ): { texts: TicketContentText[]; images: TicketContentImageRequest[]; deleteImages: TicketContentImageRequest[] } {
        const texts: TicketContentText[] = [];
        const images: TicketContentImageRequest[] = [];
        const deleteImages: TicketContentImageRequest[] = [];
        form?.dirty &&
            Object.keys(form.value).forEach(language => {
                const groupForm = form.get([language, group]);
                groupForm?.dirty &&
                    textFields.forEach(({ type }) => {
                        const fieldCtrl = groupForm.get(type);
                        if (fieldCtrl?.dirty) {
                            texts.push({ type, language, value: fieldCtrl.value });
                        }
                    });
                groupForm?.dirty &&
                    imageFields.forEach(({ type }) => {
                        const fieldCtrl = groupForm.get(type);
                        if (fieldCtrl?.dirty) {
                            if (fieldCtrl.value?.data) {
                                images.push({ type, image: fieldCtrl.value?.data, language });
                            } else {
                                deleteImages.push({ type, image: null, language });
                            }
                        }
                    });
            });
        return { texts, images, deleteImages };
    }

    private changePriceType(templateId: number, priceTypeId: number, language: string): void {
        this.currentFormGroup = this.form.get([String(templateId), String(priceTypeId), language]) as UntypedFormGroup;
        this._ref.markForCheck();
    }

    private initDynamicFormFields(venueTemplateId: number, priceTypeId: number, languages: string[]): void {
        if (languages?.length) {
            let templateGroup = this.form.get(String(venueTemplateId)) as UntypedFormGroup;
            if (!templateGroup) {
                templateGroup = this._fb.group({});
                this.form.addControl(String(venueTemplateId), templateGroup);
            }
            let priceTypeGroup = templateGroup.get(String(priceTypeId)) as UntypedFormGroup;
            if (!priceTypeGroup) {
                priceTypeGroup = this._fb.group({});
                templateGroup.addControl(String(priceTypeId), priceTypeGroup);
            }
            languages.forEach(language => {
                let languageGroup = priceTypeGroup.get(language) as UntypedFormGroup;
                if (!languageGroup) {
                    languageGroup = this._fb.group({});
                    priceTypeGroup.addControl(language, languageGroup);
                }
            });
        }
    }

    private loadAll(templateId: number, priceTypeId: number): void {
        this.clearAll();
        this._venueTemplatePriceTypesSrv.loadAllPriceTypeTicketContents(templateId, priceTypeId);
    }

    private clearAll(): void {
        this._venueTemplatePriceTypesSrv.clearAllPriceTypeTicketContents();
    }

    private filterByLanguage(content: { language: string }[]): { language: string }[] {
        return content?.filter(elem => elem.language === this._selectedLanguage.getValue());
    }
}
