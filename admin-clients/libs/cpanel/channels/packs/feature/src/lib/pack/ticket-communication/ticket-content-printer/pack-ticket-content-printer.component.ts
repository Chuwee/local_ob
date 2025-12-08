import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import { PacksService } from '@admin-clients/cpanel/channels/packs/data-access';
import {
    TicketContentFieldsRestrictions, eventTicketImageRestrictions, TicketContentFieldsRestrictions as Restrictions
} from '@admin-clients/cpanel/promoters/events/communication/data-access';
import {
    TicketContentImageType, TicketContentImage, TicketContentImageFields, TicketContentImageRequest,
    TicketContentTextType, TicketContentText, TicketContentTextFields
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
import { MatInputModule } from '@angular/material/input';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, Observable } from 'rxjs';
import { filter, map, tap } from 'rxjs/operators';

const PRINTER_TEXT_FIELDS = [
    {
        type: TicketContentTextType.title,
        validators: [Validators.maxLength(Restrictions.titlePrinterLength)]
    },
    {
        type: TicketContentTextType.subtitle,
        validators: [Validators.maxLength(Restrictions.subtitlePrinterLength)]
    },
    {
        type: TicketContentTextType.additionalData,
        validators: [
            Validators.maxLength(Restrictions.additionalDataPrinterLength)
        ]
    }
];
@Component({
    selector: 'app-pack-ticket-content-printer',
    templateUrl: './pack-ticket-content-printer.component.html',
    styleUrls: ['./pack-ticket-content-printer.component.scss'],
    imports: [
        TranslatePipe, FlexLayoutModule, ImageUploaderComponent, ReactiveFormsModule, MatExpansionModule,
        MatInputModule, FormControlErrorsComponent, MatDividerModule
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PackTicketContentPrinterComponent implements OnInit, AfterViewInit {
    readonly #onDestroy = inject(DestroyRef);
    readonly #fb = inject(UntypedFormBuilder);
    readonly #packsSrv = inject(PacksService);
    readonly #channelsSrv = inject(ChannelsService);

    readonly #imageFields = [
        { type: TicketContentImageType.body },
        { type: TicketContentImageType.bannerMain }
    ];

    #images$: Observable<TicketContentImage[]>;
    #texts$: Observable<TicketContentText[]>;
    #selectedLanguage: string;

    readonly textType = TicketContentTextType;
    readonly imageType = TicketContentImageType;
    readonly textRestrictions = TicketContentFieldsRestrictions;
    readonly imageRestrictions = eventTicketImageRestrictions;
    printerContentExpanded = true;
    printerContentForm: UntypedFormGroup;
    printerTemplateSelected$: Observable<TicketTemplate>;

    @Input() form: UntypedFormGroup;
    @Input() language$: Observable<string>;

    readonly $pack = toSignal(this.#packsSrv.pack.get$().pipe(filter(Boolean)));
    readonly $channel = toSignal(this.#channelsSrv.getChannel$());

    ngOnInit(): void {
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
        this.printerContentForm.markAsPristine();
        this.#packsSrv.packPrinterTexts.load(this.$channel().id, this.$pack().id);
        this.#packsSrv.packPrinterImages.load(this.$channel().id, this.$pack().id);
    }

    save(
        getTextFields: (contentForm: UntypedFormGroup, textFields: TicketContentTextFields[], language: string) => TicketContentText[],
        getImageFields: (contentForm: UntypedFormGroup, imageFields: TicketContentImageFields[], language: string
        ) => { [key: string]: TicketContentImageRequest[] }
    ): Observable<void | void[]>[] {
        const obsToSave$: Observable<void | void[]>[] = [];

        const textsToSave = getTextFields(this.printerContentForm, PRINTER_TEXT_FIELDS, this.#selectedLanguage);
        if (textsToSave.length > 0) {
            obsToSave$.push(
                this.#packsSrv.packPrinterTexts.save(this.$channel().id, this.$pack().id, textsToSave)
            );
        }

        const { imagesToSave, imagesToDelete } = getImageFields(this.printerContentForm, this.#imageFields, this.#selectedLanguage);
        if (imagesToSave.length > 0) {
            obsToSave$.push(
                this.#packsSrv.packPrinterImages.save(this.$channel().id, this.$pack().id, imagesToSave)
            );
        }

        if (imagesToDelete.length > 0) {
            obsToSave$.push(
                this.#packsSrv.packPrinterImages.delete(this.$channel().id, this.$pack().id, imagesToDelete)
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

    private initForms(): void {
        const printerFields = {};

        PRINTER_TEXT_FIELDS.forEach(textField => (printerFields[textField.type] = [null, textField.validators]));
        this.#imageFields.forEach(imageField => (printerFields[imageField.type] = null));

        this.printerContentForm = this.#fb.group(printerFields);
        this.form.addControl('printerContent', this.printerContentForm);
    }

    private loadContents(): void {
        // load texts and images
        this.#packsSrv.packPrinterTexts.load(this.$channel().id, this.$pack().id);
        this.#packsSrv.packPrinterImages.load(this.$channel().id, this.$pack().id);

        this.#texts$ = combineLatest([
            this.#packsSrv.packPrinterTexts.get$(),
            this.language$
        ]).pipe(
            filter(([printerTexts, language]) => !!printerTexts && !!language),
            tap(([printerTexts, language]) => {
                PRINTER_TEXT_FIELDS.forEach(textField => {
                    const field = this.printerContentForm.get(textField.type);
                    field.reset();
                    for (const text of printerTexts) {
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
            this.#packsSrv.packPrinterImages.get$(),
            this.language$
        ]).pipe(
            filter(([printerImages, language]) => !!printerImages && !!language),
            map(([printerImages, language]) => printerImages.filter(img => img.language === language)),
            tap(printerImages => {
                this.#imageFields.forEach(imageField => {
                    const field = this.printerContentForm.get(imageField.type);
                    field.reset();
                    for (const image of printerImages) {
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
                const field = this.printerContentForm.get(imageField.type);
                const originalValue = images.filter(img => img.type === imageField.type)[0]?.image_url || null;
                FormControlHandler.checkAndRefreshDirtyState(field, originalValue);
            });
        });
    }
}
