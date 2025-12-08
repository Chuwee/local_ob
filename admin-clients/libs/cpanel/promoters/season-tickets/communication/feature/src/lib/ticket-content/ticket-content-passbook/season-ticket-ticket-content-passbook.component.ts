import { SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import {
    SeasonTicketTicketContentImage, SeasonTicketTicketContentText, SeasonTicketTicketContentTextFields,
    SeasonTicketTicketContentImageFields, SeasonTicketTicketContentFieldsRestrictions, seasonTicketTicketImageRestrictions,
    SeasonTicketCommunicationService, SeasonTicketTicketContentImageRequest, seasonTicketTicketContetPassbookFieldsRelations,
    SeasonTicketTicketContentTextType, SeasonTicketTicketContentImageType
} from '@admin-clients/cpanel-promoters-season-tickets-communication-data-access';
import { TicketPassbook, TicketsPassbookService } from '@admin-clients/cpanel-promoters-tickets-passbook-data-access';
import { TicketType } from '@admin-clients/shared/common/data-access';
import { FormControlHandler } from '@admin-clients/shared/utility/utils';
import { AfterViewInit, ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { combineLatest, Observable, Subject } from 'rxjs';
import { filter, first, map, pairwise, shareReplay, startWith, take, takeUntil, tap } from 'rxjs/operators';

@Component({
    selector: 'app-season-ticket-ticket-content-passbook',
    templateUrl: './season-ticket-ticket-content-passbook.component.html',
    styleUrls: ['./season-ticket-ticket-content-passbook.component.scss', '../season-ticket-ticket-content.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SeasonTicketTicketContentPassbookComponent implements OnInit, OnDestroy, AfterViewInit {
    private _onDestroy = new Subject<void>();

    private _seasonTicketId: number;
    private _entityId: number;
    private _images$: Observable<SeasonTicketTicketContentImage[]>;
    private _texts$: Observable<SeasonTicketTicketContentText[]>;
    private _textFields: SeasonTicketTicketContentTextFields[];
    private _imageFields: SeasonTicketTicketContentImageFields[];
    private _selectedLanguage: string;

    passbookContentExpanded: boolean;
    textRestrictions = SeasonTicketTicketContentFieldsRestrictions;
    imageRestrictions = seasonTicketTicketImageRestrictions;
    passbookContentForm: UntypedFormGroup;
    ticketPassbook$: Observable<TicketPassbook>;

    @Input() form: UntypedFormGroup;
    @Input() language$: Observable<string>;
    @Input() ticketType = TicketType.general;

    constructor(
        private _fb: UntypedFormBuilder,
        private _seasonTicketService: SeasonTicketsService,
        private _ticketsPassbookService: TicketsPassbookService,
        private _seasonTicketCommunicationService: SeasonTicketCommunicationService) {
    }

    ngOnInit(): void {
        this.prepareFields();
        this.initForms();
        this.loadContents();
        this.language$.pipe(takeUntil(this._onDestroy)).subscribe(lang => this._selectedLanguage = lang);
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
        this._seasonTicketCommunicationService.loadSeasonTicketTicketPassbookContentTexts(this.ticketType, this._seasonTicketId);
        this._seasonTicketCommunicationService.loadSeasonTicketTicketPassbookContentImages(this.ticketType, this._seasonTicketId);
    }

    save(getTextFields: (contentForm: UntypedFormGroup, textFields: SeasonTicketTicketContentTextFields[], language: string) =>
        SeasonTicketTicketContentText[],
        getImageFields: (contentForm: UntypedFormGroup, imageFields: SeasonTicketTicketContentImageFields[], language: string) =>
            { [key: string]: SeasonTicketTicketContentImageRequest[] }
    ): Observable<void | void[]>[] {
        const obsToSave$: Observable<void | void[]>[] = [];

        const textsToSave = getTextFields(this.passbookContentForm, this._textFields, this._selectedLanguage);
        if (textsToSave.length > 0) {
            obsToSave$.push(
                this._seasonTicketCommunicationService
                    .saveSeasonTicketTicketPassbookContentTexts(this.ticketType, this._seasonTicketId, textsToSave)
            );
        }

        const { imagesToSave, imagesToDelete } = getImageFields(this.passbookContentForm, this._imageFields, this._selectedLanguage);
        if (imagesToSave.length > 0) {
            obsToSave$.push(
                this._seasonTicketCommunicationService
                    .saveSeasonTicketTicketPassbookContentImages(this.ticketType, this._seasonTicketId, imagesToSave)
            );
        }

        if (imagesToDelete.length > 0) {
            obsToSave$.push(
                this._seasonTicketCommunicationService
                    .deleteSeasonTicketTicketPassbookContentImages(this.ticketType, this._seasonTicketId, imagesToDelete)
            );
        }

        if (obsToSave$.length) {
            obsToSave$[obsToSave$.length - 1] = obsToSave$[obsToSave$.length - 1].pipe(
                tap(() => this.cancel()) // reloads data from backend
            );
        }
        return obsToSave$;
    }

    isPassbookFieldActive$(field?: string): Observable<boolean> {
        return this.ticketPassbook$
            .pipe(
                filter(ticketPasbook => !!ticketPasbook),
                map(ticketPassbook => {
                    const ticketPassbookValues = [].concat(...Object.values(ticketPassbook));
                    if (field) {
                        // Check if X field exist in passbook
                        const passbookFields = Object.entries(seasonTicketTicketContetPassbookFieldsRelations)
                            .filter(([key, value]) => value === field).map(([key]) => key);
                        return !ticketPassbookValues.some(properties =>
                            passbookFields.includes(properties?.key)
                            && field === seasonTicketTicketContetPassbookFieldsRelations[properties?.key]);
                    } else {
                        // Check if any field exist in passbook
                        const passbookFieldKeys = Object.keys(seasonTicketTicketContetPassbookFieldsRelations);
                        const filteredField = ticketPassbookValues.filter(properties => properties?.key).map(({ key }) => key);
                        return !passbookFieldKeys.some(key => filteredField.includes(key));
                    }
                })
            );
    }

    downloadPassbook(): void {
        this._seasonTicketCommunicationService.getDownloadUrlPassbookPreview$(this._seasonTicketId)
            .pipe(first())
            .subscribe((res: { download_url: string }) => window.open(res.download_url, '_blank'));
    }

    private prepareFields(): void {
        this._textFields = [{
            formField: 'title',
            type: SeasonTicketTicketContentTextType.title,
            maxLength: SeasonTicketTicketContentFieldsRestrictions.titlePassbookLength
        }, {
            formField: 'additionalData1',
            type: SeasonTicketTicketContentTextType.additionalData1,
            maxLength: SeasonTicketTicketContentFieldsRestrictions.additionalDataPassbookLength
        }, {
            formField: 'additionalData2',
            type: SeasonTicketTicketContentTextType.additionalData2,
            maxLength: SeasonTicketTicketContentFieldsRestrictions.additionalDataPassbookLength
        }, {
            formField: 'additionalData3',
            type: SeasonTicketTicketContentTextType.additionalData3,
            maxLength: SeasonTicketTicketContentFieldsRestrictions.additionalDataPassbookLength
        }];
        this._imageFields = [{
            formField: 'strip',
            type: SeasonTicketTicketContentImageType.strip,
            maxSize: seasonTicketTicketImageRestrictions.passbookBannerMain.size
        }];
    }

    private initForms(): void {
        const passbookFields = {};

        this._textFields.forEach(textField => {
            passbookFields[textField.formField] = [null, [Validators.maxLength(textField.maxLength)]];
        });
        this._imageFields.forEach(imageField => {
            passbookFields[imageField.formField] = null;
        });

        this.passbookContentForm = this._fb.group(passbookFields);
        this.form.addControl('passbookContent', this.passbookContentForm);
    }

    private loadContents(): void {
        // load texts and images
        this._seasonTicketService.seasonTicket.get$()
            .pipe(
                take(1),
                tap(event => {
                    this._seasonTicketId = event.id;
                    this._entityId = event.entity.id;
                    this._seasonTicketCommunicationService
                        .loadSeasonTicketTicketPassbookContentTexts(this.ticketType, this._seasonTicketId);
                    this._seasonTicketCommunicationService
                        .loadSeasonTicketTicketPassbookContentImages(this.ticketType, this._seasonTicketId);
                })
            ).subscribe();

        this.ticketPassbook$ = this._ticketsPassbookService.getTicketPassbook$()
            .pipe(
                filter(ticketPassbook => !!ticketPassbook),
                takeUntil(this._onDestroy),
                shareReplay(1)
            );

        this._texts$ = combineLatest([
            this._seasonTicketCommunicationService.getSeasonTicketTicketPassbookContentTexts$(),
            this.language$
        ]).pipe(
            filter(([passbookTexts, language]) => !!passbookTexts && !!language),
            tap(([passbookTexts, language]) => {
                this._textFields.forEach(textField => {
                    const field = this.passbookContentForm.get(textField.formField);
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
            this._seasonTicketCommunicationService.getSeasonTicketTicketPassbookContentImages$(),
            this.language$
        ]).pipe(
            filter(([passbookImages, language]) => !!passbookImages && !!language),
            map(([passbookImages, language]) => passbookImages.filter(img => img.language === language)),
            tap(passbookImages => {
                this._imageFields.forEach(imageField => {
                    const field = this.passbookContentForm.get(imageField.formField);
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
                const field = this.passbookContentForm.get(imageField.formField);
                const originalValue = images.filter(img => img.type === imageField.type)[0]?.image_url || null;
                FormControlHandler.checkAndRefreshDirtyState(field, originalValue);
            });
        });

        this.form.get('templatesForm.seasonPackPassbookTemplate').valueChanges
            .pipe(
                takeUntil(this._onDestroy),
                filter(passbookId => !!passbookId),
                startWith(this.form.get('templatesForm.seasonPackPassbookTemplate').value),
                pairwise(),
                tap(([prevPassbookId, newPassbookId]) => {
                    if (prevPassbookId !== newPassbookId) {
                        this._ticketsPassbookService.loadTicketPassbook(newPassbookId, this._entityId.toString());
                    }
                })
            ).subscribe();
    }
}
