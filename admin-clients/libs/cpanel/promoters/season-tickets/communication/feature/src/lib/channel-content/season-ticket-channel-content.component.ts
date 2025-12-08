import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import {
    EventChannelContentFieldsRestrictions
} from '@admin-clients/cpanel/promoters/events/communication/data-access';
import {
    SeasonTicketsService
} from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import {
    SeasonTicketCommunicationService, seasonTicketChannelSliderRestrictions, seasonTicketChannelBannerRestrictions,
    seasonTicketChannelCardRestrictions, SeasonTicketChannelContentFieldsRestrictions, SeasonTicketChannelContentText,
    SeasonTicketChannelContentTextType, SeasonTicketChannelContentImage, SeasonTicketChannelContentImageType,
    SeasonTicketChannelContentImageRequest,
    provideSeasonTicketCommunicationService
} from '@admin-clients/cpanel-promoters-season-tickets-communication-data-access';
import { MessageDialogService, UnsavedChangesDialogResult } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { ObFile } from '@admin-clients/shared/data-access/models';
import { booleanOrMerge, FormControlHandler } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit, QueryList, ViewChildren } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AbstractControl, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { BehaviorSubject, combineLatest, firstValueFrom, Observable, of, throwError, zip } from 'rxjs';
import { catchError, distinctUntilChanged, filter, first, map, switchMap, take, tap } from 'rxjs/operators';

@Component({
    selector: 'app-season-ticket-channel-content',
    templateUrl: './season-ticket-channel-content.component.html',
    styleUrls: ['./season-ticket-channel-content.component.scss'],
    providers: [
        provideSeasonTicketCommunicationService()
    ],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SeasonTicketChannelContentComponent implements OnInit, WritingComponent {
    private readonly _seasonTicketsSrv = inject(SeasonTicketsService);
    private readonly _seasonTicketCommunicationSrv = inject(SeasonTicketCommunicationService);
    private readonly _fb = inject(UntypedFormBuilder);
    private readonly _msgDialogSrv = inject(MessageDialogService);
    private readonly _destroyRef = inject(DestroyRef);

    private readonly _language = new BehaviorSubject<string>(null);
    @ViewChildren(MatExpansionPanel)
    private _matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    readonly isGenerationStatusError$ = this._seasonTicketsSrv.seasonTicketStatus.isGenerationStatusError$()
        .pipe(distinctUntilChanged());

    readonly seasonTicketChannelContentForm = this._fb.group({
        title: [null,
            [Validators.maxLength(EventChannelContentFieldsRestrictions.titleLength)]
        ],
        subtitle: [null,
            [Validators.maxLength(EventChannelContentFieldsRestrictions.subtitleLength)]
        ],
        durationLanguage: [null,
            [Validators.maxLength(EventChannelContentFieldsRestrictions.durationLanguageLength)]
        ],
        shortDescription: [null],
        longDescription: [null],
        slider1: null,
        slider2: null,
        slider3: null,
        slider4: null,
        slider5: null
    });

    readonly seasonTicketChannelInvoiceBannerForm = this._fb.group({ invoiceBanner: null });
    readonly seasonTicketChannelCardForm = this._fb.group({ card: null });
    readonly form = this._fb.group({
        seasonTicketChannelContent: this.seasonTicketChannelContentForm,
        seasonTicketChannelInvoiceBanner: this.seasonTicketChannelInvoiceBannerForm,
        seasonTicketChannelCard: this.seasonTicketChannelCardForm
    });

    readonly sliderRestrictions = seasonTicketChannelSliderRestrictions;
    readonly bannerRestrictions = seasonTicketChannelBannerRestrictions;
    readonly cardRestrictions = seasonTicketChannelCardRestrictions;
    readonly restrictions = SeasonTicketChannelContentFieldsRestrictions;
    readonly languageList$ = this._seasonTicketsSrv.seasonTicket.get$()
        .pipe(
            filter(seasonTicket => !!seasonTicket.settings?.languages),
            map(seasonTicket => seasonTicket.settings.languages),
            tap(languages => this._language.next(languages.default)),
            map(languages => languages.selected),
            take(1)
        );

    readonly language$ = this._language.asObservable();

    readonly isLoadingOrSaving$ = booleanOrMerge([
        this._seasonTicketCommunicationSrv.isSeasonTicketChannelContentTextsLoading$(),
        this._seasonTicketCommunicationSrv.isSeasonTicketChannelContentImagesLoading$(),
        this._seasonTicketCommunicationSrv.isSeasonTicketChannelContentTextsSaving$(),
        this._seasonTicketCommunicationSrv.isSeasonTicketChannelContentImagesSaving$(),
        this._seasonTicketCommunicationSrv.isSeasonTicketChannelContentImagesRemoving$()
    ]);

    canChangeLanguage: (() => Observable<boolean>) = () => this.validateIfCanChangeLanguage();

    ngOnInit(): void {
        this._seasonTicketsSrv.seasonTicket.get$()
            .pipe(first())
            .subscribe(seasonTicket => {
                this._seasonTicketCommunicationSrv.loadSeasonTicketChannelContentTexts(seasonTicket.id);
                this._seasonTicketCommunicationSrv.loadSeasonTicketChannelContentImages(seasonTicket.id);
            });

        combineLatest([
            this.language$,
            this._seasonTicketCommunicationSrv.getSeasonTicketChannelContentTexts(),
            this._seasonTicketCommunicationSrv.getSeasonTicketChannelContentImages$(),
            this.form.valueChanges
        ])
            .pipe(
                filter(([language, texts, images]) => !!language && !!texts && !!images),
                takeUntilDestroyed(this._destroyRef)
            )
            .subscribe(([language, texts, images]) => {
                this.applyOnTextFields(language, texts,
                    (field, value) => FormControlHandler.checkAndRefreshDirtyState(field, value));
                this.applyOnImageFields(language, images,
                    (field, value) => FormControlHandler.checkAndRefreshDirtyState(field, value));
            });

        this.refreshFormDataHandler();
    }

    async cancel(): Promise<void> {
        const seasonTicket = await firstValueFrom(this._seasonTicketsSrv.seasonTicket.get$());
        this._seasonTicketCommunicationSrv.loadSeasonTicketChannelContentTexts(seasonTicket.id);
        this._seasonTicketCommunicationSrv.loadSeasonTicketChannelContentImages(seasonTicket.id);
        this.form.markAsPristine();
    }

    save(): void {
        this.save$().subscribe();
    }

    save$(): Observable<void | void[]> {
        if (this.form.valid) {
            return this._seasonTicketsSrv.seasonTicket.get$()
                .pipe(
                    switchMap(seasonTicket => {
                        const textsToSave: SeasonTicketChannelContentText[] = [];
                        this.addTextToSave(
                            textsToSave,
                            'title',
                            SeasonTicketChannelContentTextType.title
                        );
                        this.addTextToSave(
                            textsToSave,
                            'subtitle',
                            SeasonTicketChannelContentTextType.subtitle
                        );
                        this.addTextToSave(
                            textsToSave,
                            'durationLanguage',
                            SeasonTicketChannelContentTextType.lengthAndLanguage
                        );
                        this.addTextToSave(
                            textsToSave,
                            'shortDescription',
                            SeasonTicketChannelContentTextType.shortDescription
                        );
                        this.addTextToSave(
                            textsToSave,
                            'longDescription',
                            SeasonTicketChannelContentTextType.longDescription
                        );
                        const operations = this.saveOrDeleteImages(seasonTicket.id);
                        if (textsToSave.length > 0) {
                            operations.push(
                                this._seasonTicketCommunicationSrv.saveSeasonTicketChannelContentTexts(
                                    seasonTicket.id,
                                    textsToSave
                                )
                            );
                        }
                        return zip(...operations)
                            .pipe(
                                tap(() => {
                                    this._seasonTicketCommunicationSrv.loadSeasonTicketChannelContentTexts(seasonTicket.id);
                                    this._seasonTicketCommunicationSrv.loadSeasonTicketChannelContentImages(seasonTicket.id);
                                })
                            );
                    })
                );
        } else {
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanelQueryList);
            return throwError(() => 'invalid form');
        }
    }

    changeLanguage(newLanguage: string): void {
        this._language.next(newLanguage);
    }

    canDeactivate(): Observable<boolean> {
        return this._seasonTicketsSrv.seasonTicketStatus.isGenerationStatusError$()
            .pipe(
                switchMap(isGenerationStatusError => {
                    if (this.form.dirty && !isGenerationStatusError) {
                        return this._msgDialogSrv.openRichUnsavedChangesWarn().pipe(
                            switchMap(result => {
                                if (result === UnsavedChangesDialogResult.continue) {
                                    return of(true);
                                } else if (result === UnsavedChangesDialogResult.save) {
                                    return this.save$().pipe(
                                        switchMap(() => of(true)),
                                        catchError(() => of(false))
                                    );
                                }
                                return of(false);
                            })
                        );
                    } else {
                        return of(true);
                    }
                }),
                take(1)
            );
    }

    private refreshFormDataHandler(): void {
        combineLatest([
            this.language$,
            this._seasonTicketCommunicationSrv.getSeasonTicketChannelContentTexts()
        ]).pipe(
            filter(data => data.every(Boolean)),
            takeUntilDestroyed(this._destroyRef)
        ).subscribe(([language, texts]) => {
            this.applyOnTextFields(language, texts, (field, value) => field.setValue(value));
        });

        combineLatest([
            this.language$,
            this._seasonTicketCommunicationSrv.getSeasonTicketChannelContentImages$()
        ]).pipe(
            filter(data => data.every(Boolean)),
            takeUntilDestroyed(this._destroyRef)
        ).subscribe(([language, images]) => {
            this.applyOnImageFields(language, images, (field, value) => field.setValue(value));
        });
    }

    private applyOnTextFields(language: string, texts: SeasonTicketChannelContentText[],
        doOnField: (field: AbstractControl, value: string) => void): void {
        this.applyOnTextField('title', SeasonTicketChannelContentTextType.title, language, texts, doOnField);
        this.applyOnTextField('subtitle', SeasonTicketChannelContentTextType.subtitle, language, texts, doOnField);
        this.applyOnTextField('durationLanguage', SeasonTicketChannelContentTextType.lengthAndLanguage, language, texts, doOnField);
        this.applyOnTextField('shortDescription', SeasonTicketChannelContentTextType.shortDescription, language, texts, doOnField);
        this.applyOnTextField('longDescription', SeasonTicketChannelContentTextType.longDescription, language, texts, doOnField);
    }

    private applyOnTextField(
        fieldName: string,
        type: SeasonTicketChannelContentTextType,
        language: string,
        texts: SeasonTicketChannelContentText[],
        doOnField: (field: AbstractControl, value: string) => void
    ): void {
        const field = this.seasonTicketChannelContentForm.get(fieldName);
        for (const text of texts) {
            if (text.language === language && text.type === type) {
                doOnField(field, text.value);
                return;
            }
        }
        doOnField(field, null);
    }

    private applyOnImageFields(language: string, images: SeasonTicketChannelContentImage[],
        doOnField: (field: AbstractControl, value: Partial<ObFile>) => void): void {
        this.applyOnImageField(this.seasonTicketChannelInvoiceBannerForm, 'invoiceBanner',
            SeasonTicketChannelContentImageType.promoterBanner, null, language, images, doOnField);
        this.applyOnImageField(this.seasonTicketChannelCardForm, 'card',
            SeasonTicketChannelContentImageType.card, null, language, images, doOnField);
        this.applyOnImageField(this.seasonTicketChannelContentForm, 'slider1',
            SeasonTicketChannelContentImageType.landscape, 1, language, images, doOnField);
        this.applyOnImageField(this.seasonTicketChannelContentForm, 'slider2',
            SeasonTicketChannelContentImageType.landscape, 2, language, images, doOnField);
        this.applyOnImageField(this.seasonTicketChannelContentForm, 'slider3',
            SeasonTicketChannelContentImageType.landscape, 3, language, images, doOnField);
        this.applyOnImageField(this.seasonTicketChannelContentForm, 'slider4',
            SeasonTicketChannelContentImageType.landscape, 4, language, images, doOnField);
        this.applyOnImageField(this.seasonTicketChannelContentForm, 'slider5',
            SeasonTicketChannelContentImageType.landscape, 5, language, images, doOnField);
    }

    private applyOnImageField(
        form: UntypedFormGroup, fieldName: string, type: SeasonTicketChannelContentImageType, position: number,
        language: string, images: SeasonTicketChannelContentImage[],
        doOnField: (field: AbstractControl, value: Partial<ObFile>) => void): void {
        const field = form.get(fieldName);
        for (const image of images) {
            if (image.language === language && image.type === type && (!position || position === image.position)) {
                doOnField(field, { data: image.image_url, altText: image.alt_text });
                return;
            }
        }
        doOnField(field, null);
    }

    private saveOrDeleteImages(seasonTicketId: number): Observable<void>[] {
        const fields = [
            {
                field: this.seasonTicketChannelInvoiceBannerForm.get('invoiceBanner'),
                type: SeasonTicketChannelContentImageType.promoterBanner,
                position: null
            },
            {
                field: this.seasonTicketChannelCardForm.get('card'),
                type: SeasonTicketChannelContentImageType.card,
                position: null
            },
            {
                field: this.seasonTicketChannelContentForm.get('slider1'),
                type: SeasonTicketChannelContentImageType.landscape,
                position: 1
            },
            {
                field: this.seasonTicketChannelContentForm.get('slider2'),
                type: SeasonTicketChannelContentImageType.landscape,
                position: 2
            },
            {
                field: this.seasonTicketChannelContentForm.get('slider3'),
                type: SeasonTicketChannelContentImageType.landscape,
                position: 3
            },
            {
                field: this.seasonTicketChannelContentForm.get('slider4'),
                type: SeasonTicketChannelContentImageType.landscape,
                position: 4
            },
            {
                field: this.seasonTicketChannelContentForm.get('slider5'),
                type: SeasonTicketChannelContentImageType.landscape,
                position: 5
            }
        ];
        const imagesToUpload = [] as SeasonTicketChannelContentImageRequest[];
        const imagesToDelete = [] as SeasonTicketChannelContentImageRequest[];
        const lang = this._language.getValue();
        for (const field of fields) {
            if (field.field.dirty) {
                const image = {
                    language: lang,
                    type: field.type,
                    position: field.position
                } as SeasonTicketChannelContentImageRequest;
                if (field.field.value) {
                    image.image = field.field.value.data;
                    image.alt_text = field.field.value.altText;
                    imagesToUpload.push(image);
                } else {
                    imagesToDelete.push(image);
                }
            }
        }
        const operations = [] as Observable<void>[];
        if (imagesToUpload.length > 0) {
            operations.push(this._seasonTicketCommunicationSrv.saveSeasonTicketChannelContentImages$(seasonTicketId, imagesToUpload));
        }
        if (imagesToDelete.length > 0) {
            operations.push(this._seasonTicketCommunicationSrv.deleteSeasonTicketChannelContentImages$(seasonTicketId, imagesToDelete));
        }
        return operations;
    }

    private addTextToSave(
        textsToSave: SeasonTicketChannelContentText[],
        fieldName: string,
        textType: SeasonTicketChannelContentTextType
    ): void {
        const field = this.seasonTicketChannelContentForm.get(fieldName);
        if (field.dirty) {
            textsToSave.push({
                language: this._language.getValue(),
                type: textType,
                value: field.value
            });
        }
    }

    private validateIfCanChangeLanguage(): Observable<boolean> {
        if (this.form.dirty) {
            return this._msgDialogSrv.defaultUnsavedChangesWarn();
        }
        return of(true);
    }
}
