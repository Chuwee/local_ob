import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import { PacksService } from '@admin-clients/cpanel/channels/packs/data-access';
import {
    EventChannelContentFieldsRestrictions, EventChannelContentImage, EventChannelContentImageRequest,
    EventChannelContentImageType, EventChannelContentText, EventChannelContentTextType,
    eventChannelMainImgRestrictions, eventChannelSecondaryImgRestrictions, eventChannelSliderRestrictions
} from '@admin-clients/cpanel/promoters/events/communication/data-access';
import {
    EphemeralMessageService, ImageUploaderComponent, LanguageBarComponent, MessageDialogService, RichTextAreaComponent,
    UnsavedChangesDialogResult
} from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { ObFile } from '@admin-clients/shared/data-access/models';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { FormControlHandler, booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, OnDestroy, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { AbstractControl, ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, Observable, combineLatest, forkJoin, of, throwError } from 'rxjs';
import { catchError, filter, map, shareReplay, switchMap, tap } from 'rxjs/operators';

@Component({
    selector: 'app-pack-communication',
    templateUrl: './pack-communication.component.html',
    styleUrls: ['./pack-communication.component.scss'],
    imports: [
        FormContainerComponent, LanguageBarComponent, ReactiveFormsModule, FormControlErrorsComponent, RichTextAreaComponent,
        AsyncPipe, TranslatePipe, MatProgressSpinnerModule, ImageUploaderComponent, FlexLayoutModule, MatInputModule,
        MatExpansionModule, MatIconModule, MatButtonModule, MatDividerModule
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PackCommunicationComponent implements OnInit, OnDestroy, WritingComponent {
    readonly #packsSrv = inject(PacksService);
    readonly #channelsSrv = inject(ChannelsService);
    readonly #fb = inject(UntypedFormBuilder);
    readonly #messageDialogService = inject(MessageDialogService);
    readonly #ephemeralMessage = inject(EphemeralMessageService);
    readonly #onDestroy = inject(DestroyRef);
    readonly #language = new BehaviorSubject<string>(null);

    #channelId: number;
    #packId: number;
    readonly form = this.#fb.group({
        title: [null, [Validators.maxLength(EventChannelContentFieldsRestrictions.titleLength)]],
        subtitle: [null, [Validators.maxLength(EventChannelContentFieldsRestrictions.subtitleLength)]],
        durationLanguage: [null, [Validators.maxLength(EventChannelContentFieldsRestrictions.durationLanguageLength)]],
        shortDescription: [null],
        longDescription: [null],
        informativeMessage: [null],
        mainImg: null,
        secondaryImg: null,
        slider1: null,
        slider2: null,
        slider3: null,
        slider4: null,
        slider5: null
    });

    readonly mainImgRestrictions = eventChannelMainImgRestrictions;
    readonly secondaryImgRestrictions = eventChannelSecondaryImgRestrictions;
    readonly sliderRestrictions = eventChannelSliderRestrictions;
    readonly restrictions = EventChannelContentFieldsRestrictions;
    readonly isLoadingOrSaving$ = booleanOrMerge([
        this.#packsSrv.packTexts.loading$(),
        this.#packsSrv.packImages.loading$()
    ]);

    readonly languageList$ = this.#channelsSrv.getChannel$().pipe(
        filter(Boolean),
        tap(channel => {
            this.#language.next(channel.languages.default || channel.languages.selected[0]);
        }),
        map(channel => channel.languages.selected),
        shareReplay(1)
    );

    readonly language$ = this.#language.asObservable();
    packType: string;
    infoMessageExpanded = true;

    ngOnInit(): void {

        combineLatest([
            this.#packsSrv.pack.get$(),
            this.#channelsSrv.getChannel$()
        ]).pipe(
            filter(resp => resp.every(Boolean)),
            takeUntilDestroyed(this.#onDestroy)
        ).subscribe(([pack, channel]) => {
            this.#channelId = channel.id;
            this.#packId = pack.id;
            this.packType = pack.type;
            this.#packsSrv.packTexts.load(channel.id, pack.id);
            this.#packsSrv.packImages.load(channel.id, pack.id);
            this.refreshFormDataHandler();
            this.formChangeHandler();
        });
    }

    ngOnDestroy(): void {
        this.#packsSrv.packTexts.clear();
        this.#packsSrv.packImages.clear();
    }

    canChangeLanguage: () => Observable<boolean> = () =>
        this.validateIfCanChangeLanguage();

    cancel(): void {
        this.form.markAsPristine();
        this.#packsSrv.packTexts.load(this.#channelId, this.#packId);
        this.#packsSrv.packImages.load(this.#channelId, this.#packId);
    }

    save(): void {
        this.save$().subscribe(() => {
            this.#packsSrv.packTexts.load(this.#channelId, this.#packId);
            this.#packsSrv.packImages.load(this.#channelId, this.#packId);
        });
    }

    save$(): Observable<void | void[]> {
        if (this.form.valid) {
            const textsToSave: EventChannelContentText[] = [];
            this.addTextToSave(textsToSave, 'title', EventChannelContentTextType.title);
            this.addTextToSave(textsToSave, 'subtitle', EventChannelContentTextType.subtitle);
            this.addTextToSave(textsToSave, 'durationLanguage', EventChannelContentTextType.lengthAndLanguage);
            this.addTextToSave(textsToSave, 'shortDescription', EventChannelContentTextType.shortDescription);
            this.addTextToSave(textsToSave, 'longDescription', EventChannelContentTextType.longDescription);
            this.addTextToSave(textsToSave, 'informativeMessage', EventChannelContentTextType.informativeMessage);
            const operations$ = this.saveOrDeleteImages();
            if (textsToSave.length > 0) {
                operations$.push(this.#packsSrv.packTexts.save(this.#channelId, this.#packId, textsToSave));
            }
            return forkJoin(operations$).pipe(tap(() => this.#ephemeralMessage.showSaveSuccess()));
        } else {
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return throwError(() => 'invalid form');
        }
    }

    changeLanguage(newLanguage: string): void { this.#language.next(newLanguage); }

    private saveOrDeleteImages(): Observable<void>[] {
        const fields = [
            {
                field: this.form.get('mainImg'),
                type: EventChannelContentImageType.main,
                position: null
            },
            {
                field: this.form.get('secondaryImg'),
                type: EventChannelContentImageType.secondary,
                position: null
            },
            {
                field: this.form.get('slider1'),
                type: EventChannelContentImageType.landscape,
                position: 1
            },
            {
                field: this.form.get('slider2'),
                type: EventChannelContentImageType.landscape,
                position: 2
            },
            {
                field: this.form.get('slider3'),
                type: EventChannelContentImageType.landscape,
                position: 3
            },
            {
                field: this.form.get('slider4'),
                type: EventChannelContentImageType.landscape,
                position: 4
            },
            {
                field: this.form.get('slider5'),
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
            operations.push(this.#packsSrv.packImages.save(this.#channelId, this.#packId, imagesToUpload));
        }
        if (imagesToDelete.length > 0) {
            operations.push(this.#packsSrv.packImages.delete(this.#channelId, this.#packId, imagesToDelete));
        }
        return operations;
    }

    private refreshFormDataHandler(): void {
        combineLatest([
            this.language$,
            this.#packsSrv.packTexts.get$()
        ]).pipe(
            filter(data => data.every(Boolean)),
            takeUntilDestroyed(this.#onDestroy)
        ).subscribe(([language, texts]) => {
            this.applyOnTextFields(language, texts, (field, value) => field.setValue(value));
        });

        combineLatest([
            this.language$,
            this.#packsSrv.packImages.get$()
        ]).pipe(
            filter(data => data.every(Boolean)),
            takeUntilDestroyed(this.#onDestroy)
        ).subscribe(([language, images]) => {
            this.applyOnImageFields(language, images, (field, value) => field.setValue(value));
        });
    }

    private formChangeHandler(): void {
        combineLatest([
            this.language$,
            this.#packsSrv.packTexts.get$(),
            this.#packsSrv.packImages.get$(),
            this.form.valueChanges
        ]).pipe(
            takeUntilDestroyed(this.#onDestroy),
            filter(([language, texts, images]) => !!language && !!texts && !!images)
        ).subscribe(([language, texts, images]) => {
            this.applyOnTextFields(language, texts, (field, value) => FormControlHandler.checkAndRefreshDirtyState(field, value));
            this.applyOnImageFields(language, images, (field, value) => FormControlHandler.checkAndRefreshDirtyState(field, value));
        });
    }

    private applyOnTextFields(
        language: string, texts: EventChannelContentText[], doOnField: (field: AbstractControl, value: string) => void): void {
        this.applyOnTextField('title', EventChannelContentTextType.title, language, texts, doOnField);
        this.applyOnTextField('subtitle', EventChannelContentTextType.subtitle, language, texts, doOnField);
        this.applyOnTextField('durationLanguage', EventChannelContentTextType.lengthAndLanguage, language, texts, doOnField);
        this.applyOnTextField('shortDescription', EventChannelContentTextType.shortDescription, language, texts, doOnField);
        this.applyOnTextField('longDescription', EventChannelContentTextType.longDescription, language, texts, doOnField);
        this.applyOnTextField('informativeMessage', EventChannelContentTextType.informativeMessage, language, texts, doOnField);
    }

    private applyOnTextField(
        fieldName: string, type: EventChannelContentTextType, language: string, texts: EventChannelContentText[],
        doOnField: (field: AbstractControl, value: string) => void): void {
        const field = this.form.get(fieldName);
        for (const text of texts) {
            if (text.language === language && text.type === type) {
                doOnField(field, text.value);
                return;
            }
        }
        doOnField(field, null);
    }

    private applyOnImageFields(
        language: string, images: EventChannelContentImage[],
        doOnField: (field: AbstractControl, value: Partial<ObFile>) => void): void {
        this.applyOnImageField(
            this.form, 'mainImg', EventChannelContentImageType.main, null, language, images, doOnField
        );
        this.applyOnImageField(
            this.form, 'secondaryImg', EventChannelContentImageType.secondary, null, language, images, doOnField
        );
        this.applyOnImageField(
            this.form, 'slider1', EventChannelContentImageType.landscape, 1, language, images, doOnField
        );
        this.applyOnImageField(
            this.form, 'slider2', EventChannelContentImageType.landscape, 2, language, images, doOnField
        );
        this.applyOnImageField(
            this.form, 'slider3', EventChannelContentImageType.landscape, 3, language, images, doOnField
        );
        this.applyOnImageField(
            this.form, 'slider4', EventChannelContentImageType.landscape, 4, language, images, doOnField
        );
        this.applyOnImageField(
            this.form, 'slider5', EventChannelContentImageType.landscape, 5, language, images, doOnField
        );
    }

    private applyOnImageField(
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

    private addTextToSave(
        textsToSave: EventChannelContentText[], fieldName: string, textType: EventChannelContentTextType): void {
        const field = this.form.get(fieldName);
        if (field.dirty) {
            textsToSave.push({
                language: this.#language.getValue(),
                type: textType,
                value: field.value
            });
        }
    }

    private validateIfCanChangeLanguage(): Observable<boolean> {
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
