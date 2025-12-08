import {
    TicketContentFieldsRestrictions, eventTicketImageRestrictions, EventCommunicationService, eventTicketContetPassbookFieldsRelations
} from '@admin-clients/cpanel/promoters/events/communication/data-access';
import {
    EventsService, TicketContentImageType as ImageType, TicketContentImage, TicketContentImageFields, TicketContentImageRequest,
    TicketContentText, TicketContentTextType as TextType, TicketContentTextFields
} from '@admin-clients/cpanel/promoters/events/data-access';
import { TicketPassbook, TicketsPassbookService } from '@admin-clients/cpanel-promoters-tickets-passbook-data-access';
import { TicketType } from '@admin-clients/shared/common/data-access';
import { FormControlHandler } from '@admin-clients/shared/utility/utils';
import { AfterViewInit, ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { combineLatest, Observable, Subject } from 'rxjs';
import { filter, first, map, pairwise, shareReplay, startWith, take, takeUntil, tap } from 'rxjs/operators';

@Component({
    selector: 'app-event-ticket-content-passbook',
    templateUrl: './event-ticket-content-passbook.component.html',
    styleUrls: ['./event-ticket-content-passbook.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class EventTicketContentPassbookComponent
    implements OnInit, OnDestroy, AfterViewInit {
    private _onDestroy = new Subject<void>();

    private _eventId: number;
    private _entityId: number;
    private _images$: Observable<TicketContentImage[]>;
    private _texts$: Observable<TicketContentText[]>;
    private _textFields: TicketContentTextFields[];
    private _imageFields: TicketContentImageFields[];
    private _selectedLanguage: string;

    textType = TextType;
    imageType = ImageType;
    passbookContentExpanded: boolean;
    textRestrictions = TicketContentFieldsRestrictions;
    imageRestrictions = eventTicketImageRestrictions;
    passbookContentForm: UntypedFormGroup;
    ticketPassbook$: Observable<TicketPassbook>;

    @Input() form: UntypedFormGroup;
    @Input() language$: Observable<string>;
    @Input() ticketType = TicketType.general;

    constructor(
        private _fb: UntypedFormBuilder,
        private _eventService: EventsService,
        private _ticketsPassbookService: TicketsPassbookService,
        private _eventCommunicationService: EventCommunicationService
    ) { }

    ngOnInit(): void {
        this.prepareFields();
        this.initForms();
        this.loadContents();
        this.language$
            .pipe(takeUntil(this._onDestroy))
            .subscribe(lang => (this._selectedLanguage = lang));
    }

    ngAfterViewInit(): void {
        this.refreshFormDataHandler();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    cancel(): void {
        this.passbookContentForm.markAsPristine();
        this._eventCommunicationService.loadEventTicketPassbookContentTexts(this.ticketType, this._eventId);
        this._eventCommunicationService.loadEventTicketPassbookContentImages(this.ticketType, this._eventId);
    }

    save(
        getTextFields: (contentForm: UntypedFormGroup, textFields: TicketContentTextFields[], language: string) => TicketContentText[],
        getImageFields: (contentForm: UntypedFormGroup, imageFields: TicketContentImageFields[], language: string) =>
            { [key: string]: TicketContentImageRequest[] }
    ): Observable<void | void[]>[] {
        const obsToSave$: Observable<void | void[]>[] = [];

        const textsToSave = getTextFields(this.passbookContentForm, this._textFields, this._selectedLanguage);
        if (textsToSave.length > 0) {
            obsToSave$.push(
                this._eventCommunicationService.saveEventTicketPassbookContentTexts(this.ticketType, this._eventId, textsToSave)
            );
        }

        const { imagesToSave, imagesToDelete } = getImageFields(this.passbookContentForm, this._imageFields, this._selectedLanguage);
        if (imagesToSave.length > 0) {
            obsToSave$.push(
                this._eventCommunicationService.saveEventTicketPassbookContentImages(this.ticketType, this._eventId, imagesToSave)
            );
        }

        if (imagesToDelete.length > 0) {
            obsToSave$.push(
                this._eventCommunicationService.deleteEventTicketPassbookContentImages(this.ticketType, this._eventId, imagesToDelete)
            );
        }

        if (obsToSave$.length) {
            obsToSave$[obsToSave$.length - 1] = obsToSave$[obsToSave$.length - 1]
                .pipe(
                    tap(() => this.cancel()) // reloads data from backend
                );
        }
        return obsToSave$;
    }

    isPassbookFieldInactive$(field?: string): Observable<boolean> {
        return this.ticketPassbook$.pipe(
            filter(ticketPasbook => !!ticketPasbook),
            map(ticketPassbook => {
                const ticketPassbookValues = [].concat(
                    ...Object.values(ticketPassbook)
                );
                if (field) {
                    // Check if X field exist in passbook
                    const passbookFields = Object.entries(
                        eventTicketContetPassbookFieldsRelations
                    )
                        .filter(([key, value]) => value === field)
                        .map(([key]) => key);
                    return !ticketPassbookValues.some(properties =>
                        passbookFields.includes(properties?.key) &&
                        field === eventTicketContetPassbookFieldsRelations[properties?.key]
                    );
                } else {
                    // Check if any field exist in passbook
                    const passbookFieldKeys = Object.keys(eventTicketContetPassbookFieldsRelations);
                    const filteredField = ticketPassbookValues
                        .filter(properties => properties?.key)
                        .map(({ key }) => key);
                    return !passbookFieldKeys.some(key => filteredField.includes(key));
                }
            })
        );
    }

    allPassbookFieldsInactive$(): Observable<boolean> {
        return this.isPassbookFieldInactive$();
    }

    downloadPassbook(): void {
        this._eventCommunicationService.getDownloadUrlPassbookPreview$(this._eventId)
            .pipe(first())
            .subscribe(({ download_url: downloadUrl }) =>
                window.open(downloadUrl, '_blank')
            );
    }

    private prepareFields(): void {
        this._textFields = [
            {
                type: TextType.title,
                validators: [Validators.maxLength(TicketContentFieldsRestrictions.titlePassbookLength)]
            },
            {
                type: TextType.additionalData1,
                validators: [Validators.maxLength(TicketContentFieldsRestrictions.additionalDataPassbookLength)]
            },
            {
                type: TextType.additionalData2,
                validators: [Validators.maxLength(TicketContentFieldsRestrictions.additionalDataPassbookLength)]
            },
            {
                type: TextType.additionalData3,
                validators: [Validators.maxLength(TicketContentFieldsRestrictions.additionalDataPassbookLength)]
            }
        ];

        this._imageFields = [{ type: ImageType.strip }];
    }

    private initForms(): void {
        const passbookFields = {};

        this._textFields.forEach(textField => { passbookFields[textField.type] = [null, textField.validators]; });
        this._imageFields.forEach(imageField => { passbookFields[imageField.type] = null; });

        this.passbookContentForm = this._fb.group(passbookFields);
        this.form.addControl('passbookContent', this.passbookContentForm);
    }

    private loadContents(): void {
        // load texts and images
        this._eventService.event.get$()
            .pipe(
                take(1),
                tap(event => {
                    this._eventId = event.id;
                    this._entityId = event.entity.id;
                    this._eventCommunicationService.loadEventTicketPassbookContentTexts(this.ticketType, this._eventId);
                    this._eventCommunicationService.loadEventTicketPassbookContentImages(this.ticketType, this._eventId);
                })
            ).subscribe();

        this.ticketPassbook$ = this._ticketsPassbookService.getTicketPassbook$()
            .pipe(
                filter(ticketPassbook => !!ticketPassbook),
                takeUntil(this._onDestroy),
                shareReplay(1)
            );

        this._texts$ = combineLatest([
            this._eventCommunicationService.getEventTicketPassbookContentTexts$(),
            this.language$
        ]).pipe(
            filter(([passbookTexts, language]) => !!passbookTexts && !!language),
            tap(([passbookTexts, language]) => {
                this._textFields.forEach(textField => {
                    const field = this.passbookContentForm.get(textField.type);
                    field.reset();
                    for (const text of passbookTexts) {
                        if (text.language === language && text.type === textField.type) {
                            field.setValue(text.value);
                            return;
                        }
                    }
                });
            }),
            map(([texts, _]) => texts)
        );

        this._images$ = combineLatest([
            this._eventCommunicationService.getEventTicketPassbookContentImages$(),
            this.language$
        ]).pipe(
            filter(([passbookImages, language]) => !!passbookImages && !!language),
            map(([passbookImages, language]) => passbookImages.filter(img => img.language === language)),
            tap(passbookImages => {
                this._imageFields.forEach(imageField => {
                    const field = this.passbookContentForm.get(imageField.type);
                    field.reset();
                    for (const image of passbookImages) {
                        if (image.type === imageField.type) {
                            field.setValue(image.image_url);
                            return;
                        }
                    }
                });
            })
        );
    }

    private refreshFormDataHandler(): void {
        combineLatest([
            this.language$,
            this._texts$,
            this._images$,
            this.form.valueChanges
        ]).pipe(
            filter(data => data.every(item => !!item)),
            takeUntil(this._onDestroy)
        ).subscribe(([, , images]) => {
            this._imageFields.forEach(imageField => {
                const field = this.passbookContentForm.get(imageField.type);
                const originalValue =
                    images.filter(img => img.type === imageField.type)[0]?.image_url || null;
                FormControlHandler.checkAndRefreshDirtyState(field, originalValue);
            });
        });

        this.form.get('templatesForm.singlePassbookTemplate').valueChanges
            .pipe(
                takeUntil(this._onDestroy),
                filter(passbookId => !!passbookId),
                startWith(this.form.get('templatesForm.singlePassbookTemplate').value),
                pairwise(),
                tap(([prevPassbookId, newPassbookId]) => {
                    if (prevPassbookId !== newPassbookId) {
                        this._ticketsPassbookService.loadTicketPassbook(newPassbookId, this._entityId.toString());
                    }
                })
            ).subscribe();
    }
}
