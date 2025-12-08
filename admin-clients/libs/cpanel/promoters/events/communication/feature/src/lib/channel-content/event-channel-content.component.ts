import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import {
    EventChannelContentFieldsRestrictions, EventChannelContentImage, EventChannelContentImageRequest,
    EventChannelContentImageType, EventChannelContentText, EventChannelContentTextType, EventCommunicationService,
    eventChannelBannerRestrictions, eventChannelMainImgRestrictions, eventChannelSecondaryImgRestrictions,
    eventChannelSliderRestrictions
} from '@admin-clients/cpanel/promoters/events/communication/data-access';
import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { EphemeralMessageService, MessageDialogService, UnsavedChangesDialogResult } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { ObFile } from '@admin-clients/shared/data-access/models';
import { booleanOrMerge, FormControlHandler } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnDestroy, OnInit, QueryList, ViewChildren } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { AbstractControl, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { BehaviorSubject, combineLatest, forkJoin, Observable, of, throwError } from 'rxjs';
import { catchError, filter, map, switchMap, take, tap } from 'rxjs/operators';

@Component({
    selector: 'app-event-channel-content',
    templateUrl: './event-channel-content.component.html',
    styleUrls: ['./event-channel-content.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class EventChannelContentComponent implements OnInit, OnDestroy, WritingComponent {
    @ViewChildren(MatExpansionPanel)
    private _matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    readonly #entitiesService = inject(EntitiesBaseService);
    readonly #eventsService = inject(EventsService);
    readonly #eventCommunicationService = inject(EventCommunicationService);
    readonly #fb = inject(UntypedFormBuilder);
    readonly #messageDialogService = inject(MessageDialogService);
    readonly #ephemeralMessage = inject(EphemeralMessageService);
    readonly #language = new BehaviorSubject<string>(null);
    readonly #destroyRef = inject(DestroyRef);
    #eventId: number;
    form: UntypedFormGroup;
    eventChannelContentForm: UntypedFormGroup;
    eventChannelInvoiceBannerForm: UntypedFormGroup;
    mainImgRestrictions = eventChannelMainImgRestrictions;
    secondaryImgRestrictions = eventChannelSecondaryImgRestrictions;
    bannerRestrictions = eventChannelBannerRestrictions;
    sliderRestrictions = eventChannelSliderRestrictions;
    restrictions = EventChannelContentFieldsRestrictions;
    languageList$: Observable<string[]>;
    language$ = this.#language.asObservable();

    readonly $allowPngConversion = toSignal(this.#entitiesService.getEntity$()
        .pipe(map(entity => entity?.settings?.allow_png_conversion ?? false))
    );

    readonly $isLoadingOrSaving = toSignal(booleanOrMerge([
        this.#eventCommunicationService.isEventChannelContentTextsLoading$(),
        this.#eventCommunicationService.isEventChannelContentImagesLoading$(),
        this.#eventCommunicationService.isEventChannelContentTextsSaving$(),
        this.#eventCommunicationService.isEventChannelContentImagesSaving$(),
        this.#eventCommunicationService.isEventChannelContentImagesRemoving$()
    ]));

    ngOnInit(): void {
        this.#initForms();

        this.languageList$ = this.#eventsService.event.get$().pipe(
            take(1),
            tap(event => {
                this.#eventId = event.id;
                this.#eventCommunicationService.loadEventChannelContentTexts(this.#eventId);
                this.#eventCommunicationService.loadEventChannelContentImages(this.#eventId);
            }),
            map(event => event.settings.languages),
            tap(languages => this.#language.next(languages?.default)),
            map(languages => languages?.selected)
        );

        this.#refreshFormDataHandler();
        this.#formChangeHandler();
    }

    ngOnDestroy(): void {
        this.#eventCommunicationService.clearEventChannelContentTexts();
        this.#eventCommunicationService.clearEventChannelContentImages();
    }

    canChangeLanguage: () => Observable<boolean> = () =>
        this.#validateIfCanChangeLanguage();

    cancel(): void {
        this.form.markAsPristine();
        this.#eventCommunicationService.loadEventChannelContentTexts(this.#eventId);
        this.#eventCommunicationService.loadEventChannelContentImages(this.#eventId);
    }

    save(): void {
        this.save$().subscribe(() => {
            this.#eventCommunicationService.loadEventChannelContentTexts(this.#eventId);
            this.#eventCommunicationService.loadEventChannelContentImages(this.#eventId);
        });
    }

    save$(): Observable<void | void[]> {
        if (this.form.valid) {
            const textsToSave: EventChannelContentText[] = [];
            this.#addTextToSave(textsToSave, 'title', EventChannelContentTextType.title);
            this.#addTextToSave(textsToSave, 'subtitle', EventChannelContentTextType.subtitle);
            this.#addTextToSave(textsToSave, 'durationLanguage', EventChannelContentTextType.lengthAndLanguage);
            this.#addTextToSave(textsToSave, 'shortDescription', EventChannelContentTextType.shortDescription);
            this.#addTextToSave(textsToSave, 'longDescription', EventChannelContentTextType.longDescription);
            const operations$ = this.#saveOrDeleteImages();
            if (textsToSave.length > 0) {
                operations$.push(this.#eventCommunicationService.saveEventChannelContentTexts(this.#eventId, textsToSave));
            }
            return forkJoin(operations$).pipe(tap(() => this.#ephemeralMessage.showSaveSuccess()));
        } else {
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanelQueryList);
            return throwError(() => 'invalid form');
        }
    }

    changeLanguage(newLanguage: string): void {
        this.#language.next(newLanguage);
    }

    #initForms(): void {
        this.eventChannelContentForm = this.#fb.group({
            title: [null, [Validators.maxLength(EventChannelContentFieldsRestrictions.titleLength)]],
            subtitle: [null, [Validators.maxLength(EventChannelContentFieldsRestrictions.subtitleLength)]],
            durationLanguage: [null, [Validators.maxLength(EventChannelContentFieldsRestrictions.durationLanguageLength)]],
            shortDescription: [null],
            longDescription: [null],
            mainImg: null,
            secondaryImg: null,
            slider1: null,
            slider2: null,
            slider3: null,
            slider4: null,
            slider5: null
        });

        this.eventChannelInvoiceBannerForm = this.#fb.group({ invoiceBanner: null });

        this.form = this.#fb.group({
            eventChannelContent: this.eventChannelContentForm,
            eventChannelInvoiceBanner: this.eventChannelInvoiceBannerForm
        });
    }

    #saveOrDeleteImages(): Observable<void>[] {
        const fields = [
            {
                field: this.eventChannelContentForm.get('mainImg'),
                type: EventChannelContentImageType.main,
                position: null
            },
            {
                field: this.eventChannelContentForm.get('secondaryImg'),
                type: EventChannelContentImageType.secondary,
                position: null
            },
            {
                field: this.eventChannelInvoiceBannerForm.get('invoiceBanner'),
                type: EventChannelContentImageType.promoterBanner,
                position: null
            },
            {
                field: this.eventChannelContentForm.get('slider1'),
                type: EventChannelContentImageType.landscape,
                position: 1
            },
            {
                field: this.eventChannelContentForm.get('slider2'),
                type: EventChannelContentImageType.landscape,
                position: 2
            },
            {
                field: this.eventChannelContentForm.get('slider3'),
                type: EventChannelContentImageType.landscape,
                position: 3
            },
            {
                field: this.eventChannelContentForm.get('slider4'),
                type: EventChannelContentImageType.landscape,
                position: 4
            },
            {
                field: this.eventChannelContentForm.get('slider5'),
                type: EventChannelContentImageType.landscape,
                position: 5
            }
        ];
        const imagesToUpload = [] as EventChannelContentImageRequest[];
        const imagesToDelete = [] as EventChannelContentImageRequest[];
        const lang = this.#language.getValue();
        for (const field of fields) {
            if (field.field.dirty) {
                const image = {
                    language: lang,
                    type: field.type,
                    position: field.position
                } as EventChannelContentImageRequest;
                if (field.field.value) {
                    image.image = field.field.value.data;
                    image.alt_text = field.field.value.altText;
                    imagesToUpload.push(image);
                } else {
                    imagesToDelete.push(image);
                }
            }
        }
        const operations: Observable<void>[] = [];
        if (imagesToUpload.length > 0) {
            operations.push(this.#eventCommunicationService.saveEventChannelContentImages(this.#eventId, imagesToUpload));
        }
        if (imagesToDelete.length > 0) {
            operations.push(this.#eventCommunicationService.deleteEventChannelContentImages(this.#eventId, imagesToDelete));
        }
        return operations;
    }

    #refreshFormDataHandler(): void {
        combineLatest([
            this.language$,
            this.#eventCommunicationService.getEventChannelContentTexts$()
        ]).pipe(
            filter(data => data.every(Boolean)),
            takeUntilDestroyed(this.#destroyRef)
        ).subscribe(([language, texts]) => {
            this.#applyOnTextFields(language, texts, (field, value) => field.setValue(value));
        });

        combineLatest([
            this.language$,
            this.#eventCommunicationService.getEventChannelContentImages$()
        ]).pipe(
            filter(data => data.every(Boolean)),
            takeUntilDestroyed(this.#destroyRef)
        ).subscribe(([language, images]) => {
            this.#applyOnImageFields(language, images, (field, value) => field.setValue(value));
        });
    }

    #formChangeHandler(): void {
        combineLatest([
            this.language$,
            this.#eventCommunicationService.getEventChannelContentTexts$(),
            this.#eventCommunicationService.getEventChannelContentImages$(),
            this.form.valueChanges
        ])
            .pipe(
                takeUntilDestroyed(this.#destroyRef),
                filter(([language, texts, images]) => !!language && !!texts && !!images)
            )
            .subscribe(([language, texts, images]) => {
                this.#applyOnTextFields(language, texts, (field, value) => FormControlHandler.checkAndRefreshDirtyState(field, value));
                this.#applyOnImageFields(language, images, (field, value) => FormControlHandler.checkAndRefreshDirtyState(field, value));
            });
    }

    #applyOnTextFields(
        language: string, texts: EventChannelContentText[], doOnField: (field: AbstractControl, value: string) => void): void {
        this.#applyOnTextField('title', EventChannelContentTextType.title, language, texts, doOnField);
        this.#applyOnTextField('subtitle', EventChannelContentTextType.subtitle, language, texts, doOnField);
        this.#applyOnTextField('durationLanguage', EventChannelContentTextType.lengthAndLanguage, language, texts, doOnField);
        this.#applyOnTextField('shortDescription', EventChannelContentTextType.shortDescription, language, texts, doOnField);
        this.#applyOnTextField('longDescription', EventChannelContentTextType.longDescription, language, texts, doOnField);
    }

    #applyOnTextField(
        fieldName: string, type: EventChannelContentTextType, language: string, texts: EventChannelContentText[],
        doOnField: (field: AbstractControl, value: string) => void): void {
        const field = this.eventChannelContentForm.get(fieldName);
        for (const text of texts) {
            if (text.language === language && text.type === type) {
                doOnField(field, text.value);
                return;
            }
        }
        doOnField(field, null);
    }

    #applyOnImageFields(
        language: string, images: EventChannelContentImage[],
        doOnField: (field: AbstractControl, value: Partial<ObFile>) => void): void {
        this.#applyOnImageField(
            this.eventChannelContentForm, 'mainImg', EventChannelContentImageType.main, null, language, images, doOnField
        );
        this.#applyOnImageField(
            this.eventChannelContentForm, 'secondaryImg', EventChannelContentImageType.secondary, null, language, images, doOnField
        );
        this.#applyOnImageField(
            this.eventChannelInvoiceBannerForm, 'invoiceBanner', EventChannelContentImageType.promoterBanner, null, language,
            images, doOnField
        );
        this.#applyOnImageField(
            this.eventChannelContentForm, 'slider1', EventChannelContentImageType.landscape, 1, language, images, doOnField
        );
        this.#applyOnImageField(
            this.eventChannelContentForm, 'slider2', EventChannelContentImageType.landscape, 2, language, images, doOnField
        );
        this.#applyOnImageField(
            this.eventChannelContentForm, 'slider3', EventChannelContentImageType.landscape, 3, language, images, doOnField
        );
        this.#applyOnImageField(
            this.eventChannelContentForm, 'slider4', EventChannelContentImageType.landscape, 4, language, images, doOnField
        );
        this.#applyOnImageField(
            this.eventChannelContentForm, 'slider5', EventChannelContentImageType.landscape, 5, language, images, doOnField
        );
    }

    #applyOnImageField(
        form: UntypedFormGroup, fieldName: string, type: EventChannelContentImageType, position: number, language: string,
        images: EventChannelContentImage[], doOnField: (field: AbstractControl, value: Partial<ObFile>) => void
    ): void {
        const field = form.get(fieldName);
        for (const image of images) {
            if (image.language === language && image.type === type && (!position || position === image.position)) {
                doOnField(field, { data: image.image_url, altText: image.alt_text });
                return;
            }
        }
        doOnField(field, null);
    }

    #addTextToSave(
        textsToSave: EventChannelContentText[], fieldName: string, textType: EventChannelContentTextType): void {
        const field = this.eventChannelContentForm.get(fieldName);
        if (field.dirty) {
            textsToSave.push({
                language: this.#language.getValue(),
                type: textType,
                value: field.value
            });
        }
    }

    #validateIfCanChangeLanguage(): Observable<boolean> {
        if (this.form.dirty) {
            return this.#messageDialogService.openRichUnsavedChangesWarn().pipe(
                switchMap(res => {
                    if (res === UnsavedChangesDialogResult.cancel) {
                        return of(false);
                    } else if (res === UnsavedChangesDialogResult.continue) {
                        return of(true);
                    } else {
                        return this.save$().pipe(
                            switchMap(() => of(true)),
                            catchError(() => of(false))
                        );
                    }
                }));
        }
        return of(true);
    }
}
