import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import { PacksService } from '@admin-clients/cpanel/channels/packs/data-access';
import {
    TicketContentFieldsRestrictions as TextRestrictions, eventTicketImageRestrictions
} from '@admin-clients/cpanel/promoters/events/communication/data-access';
import {
    TicketContentImageType as ImageType, TicketContentImage, TicketContentImageFields, TicketContentImageRequest,
    TicketContentTextType as TextType, TicketContentText, TicketContentTextFields
} from '@admin-clients/cpanel/promoters/events/data-access';
import { TicketTemplate } from '@admin-clients/cpanel-promoters-ticket-templates-data-access';
import { ImageUploaderComponent } from '@admin-clients/shared/common/ui/components';
import { FormControlHandler } from '@admin-clients/shared/utility/utils';
import { AfterViewInit, ChangeDetectionStrategy, Component, DestroyRef, inject, Input, OnInit } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MatDividerModule } from '@angular/material/divider';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, Observable } from 'rxjs';
import { filter, map, tap } from 'rxjs/operators';

@Component({
    selector: 'app-pack-ticket-content-pdf',
    templateUrl: './pack-ticket-content-pdf.component.html',
    styleUrls: ['./pack-ticket-content-pdf.component.scss'],
    imports: [
        TranslatePipe, FlexLayoutModule, ImageUploaderComponent, ReactiveFormsModule, MatExpansionModule,
        MatInputModule, FormControlErrorsComponent, MatIconModule, MatDividerModule
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PackTicketContentPdfComponent implements OnInit, AfterViewInit {
    readonly #onDestroy = inject(DestroyRef);
    readonly #fb = inject(UntypedFormBuilder);
    readonly #packsSrv = inject(PacksService);
    readonly #channelsSrv = inject(ChannelsService);
    // readonly #ticketTemplatesService = inject(TicketTemplatesService);

    #images$: Observable<TicketContentImage[]>;
    #texts$: Observable<TicketContentText[]>;
    #textFields: TicketContentTextFields[];
    #imageFields: TicketContentImageFields[];
    #selectedLanguage: string;

    textType = TextType;
    imageType = ImageType;
    pdfContentExpanded = true;
    textRestrictions = TextRestrictions;
    imageRestrictions = eventTicketImageRestrictions;
    pdfContentForm: UntypedFormGroup;
    pdfTemplateSelected$: Observable<TicketTemplate>;

    readonly $pack = toSignal(this.#packsSrv.pack.get$().pipe(filter(Boolean)));
    readonly $channel = toSignal(this.#channelsSrv.getChannel$());

    @Input() form: UntypedFormGroup;
    @Input() language$: Observable<string>;

    ngOnInit(): void {
        this.prepareFields();
        this.initForms();

        combineLatest([
            this.#packsSrv.pack.get$(),
            this.#channelsSrv.getChannel$()
        ]).pipe(
            filter(resp => resp.every(Boolean)),
            takeUntilDestroyed(this.#onDestroy)
        ).subscribe(() => this.loadContents());

        this.language$
            .pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe(lang => (this.#selectedLanguage = lang));
    }

    ngAfterViewInit(): void {
        this.refreshFormDataHandler();
    }

    cancel(): void {
        this.pdfContentForm.markAsPristine();
        this.#packsSrv.packTicketTexts.load(this.$channel().id, this.$pack().id);
        this.#packsSrv.packTicketImages.load(this.$channel().id, this.$pack().id);
    }

    save(
        getTextFields: (contentForm: UntypedFormGroup, textFields: TicketContentTextFields[], language: string) => TicketContentText[],
        getImageFields: (contentForm: UntypedFormGroup, imageFields: TicketContentImageFields[], language: string) =>
            { [key: string]: TicketContentImageRequest[] }
    ): Observable<void | void[]>[] {
        const obsToSave$: Observable<void | void[]>[] = [];

        const textsToSave = getTextFields(this.pdfContentForm, this.#textFields, this.#selectedLanguage);
        if (textsToSave.length > 0) {
            obsToSave$.push(this.#packsSrv.packTicketTexts.save(this.$channel().id, this.$pack().id, textsToSave));
        }

        const { imagesToSave, imagesToDelete } = getImageFields(this.pdfContentForm, this.#imageFields, this.#selectedLanguage);
        if (imagesToSave.length > 0) {
            obsToSave$.push(this.#packsSrv.packTicketImages.save(this.$channel().id, this.$pack().id, imagesToSave));
        }

        if (imagesToDelete.length > 0) {
            obsToSave$.push(this.#packsSrv.packTicketImages.delete(
                this.$channel().id, this.$pack().id, imagesToDelete
            ));
        }

        if (obsToSave$.length) {
            obsToSave$[obsToSave$.length - 1] = obsToSave$[obsToSave$.length - 1]
                .pipe(
                    tap(() => this.cancel()) // reloads data from backend
                );
        }
        return obsToSave$;
    }

    private prepareFields(): void {
        this.#textFields = [
            {
                type: TextType.title,
                validators: [Validators.maxLength(TextRestrictions.titlePdfLength)]
            },
            {
                type: TextType.subtitle,
                validators: [Validators.maxLength(TextRestrictions.subtitlePdfLength)]
            },
            {
                type: TextType.additionalData,
                validators: [Validators.maxLength(TextRestrictions.additionalDataPdfLength)]
            }
        ];
        this.#imageFields = [{ type: ImageType.body }, { type: ImageType.bannerMain }, { type: ImageType.bannerSecondary }];
    }

    private initForms(): void {
        const pdfFields = {};

        this.#textFields.forEach(textField => {
            pdfFields[textField.type] = [null];
        });
        this.#imageFields.forEach(imageField => {
            pdfFields[imageField.type] = null;
        });

        this.pdfContentForm = this.#fb.group(pdfFields);
        this.form.addControl('pdfContent', this.pdfContentForm);
    }

    private loadContents(): void {
        // load texts and images
        this.#packsSrv.packTicketTexts.load(this.$channel().id, this.$pack().id);
        this.#packsSrv.packTicketImages.load(this.$channel().id, this.$pack().id);

        this.#texts$ = combineLatest([
            this.#packsSrv.packTicketTexts.get$(),
            this.language$
        ]).pipe(
            filter(([pdfTexts, language]) => !!pdfTexts && !!language),
            tap(([pdfTexts, language]) => {
                this.#textFields.forEach(textField => {
                    const field = this.pdfContentForm.get(textField.type);
                    field.reset();
                    for (const text of pdfTexts) {
                        if (text.language === language && text.type === textField.type) {
                            field.setValue(text.value);
                            return;
                        }
                    }
                });
            }),
            map(([texts, _]) => texts)
        );

        this.#images$ = combineLatest([
            this.#packsSrv.packTicketImages.get$(),
            this.language$
        ]).pipe(
            filter(([images, language]) => !!images && !!language),
            map(([images, language]) =>
                images.filter(img => img.language === language)
            ),
            tap(images => {
                this.#imageFields.forEach(imageField => {
                    const field = this.pdfContentForm.get(imageField.type);
                    field.reset();
                    for (const image of images) {
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
            this.#texts$,
            this.#images$,
            this.form.valueChanges
        ]).pipe(
            filter(([language, texts, images]) => !!language && !!texts && !!images),
            takeUntilDestroyed(this.#onDestroy)
        ).subscribe(([, , images]) => {
            this.#imageFields.forEach(imageField => {
                const field = this.pdfContentForm.get(imageField.type);
                const originalValue = images.filter(img => img.type === imageField.type)[0]?.image_url || null;
                FormControlHandler.checkAndRefreshDirtyState(field, originalValue);
            });
        });
    }
}
