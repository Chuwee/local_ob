import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import {
    ProductChannelContentImageType, ProductChannelContentTextType,
    ProductChannelImageContent, ProductChannelTextContent,
    ProductFieldsRestriction, ProductsService, productChannelSliderRestrictions
} from '@admin-clients/cpanel/products/my-products/data-access';
import {
    EphemeralMessageService, ImageUploaderComponent,
    LanguageBarComponent, MessageDialogService,
    RichTextAreaComponent, UnsavedChangesDialogResult
} from '@admin-clients/shared/common/ui/components';
import { ObFile } from '@admin-clients/shared/data-access/models';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { FormControlHandler, booleanOrMerge, htmlContentMaxLengthValidator } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, QueryList, ViewChildren, inject } from '@angular/core';
import { FlexLayoutModule, FlexModule } from '@angular/flex-layout';
import { AbstractControl, FormBuilder, ReactiveFormsModule, UntypedFormGroup, Validators } from '@angular/forms';
import { MatExpansionModule, MatExpansionPanel } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import {
    BehaviorSubject, Observable, Subject, catchError, combineLatest, filter, first, forkJoin, map, of, switchMap, takeUntil, tap
} from 'rxjs';

@Component({
    selector: 'app-product-channel-contents',
    imports: [
        AsyncPipe,
        FormControlErrorsComponent,
        FormContainerComponent,
        LanguageBarComponent,
        RichTextAreaComponent,
        ImageUploaderComponent,
        MatExpansionModule,
        MatProgressSpinnerModule,
        TranslatePipe,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule,
        FlexLayoutModule,
        FlexModule
    ],
    templateUrl: './product-channel-contents.component.html',
    styleUrls: ['./product-channel-contents.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductChannelContentsComponent implements OnInit, OnDestroy {
    private readonly _fb = inject(FormBuilder);
    private readonly _productsSrv = inject(ProductsService);
    private readonly _msgDialogSrv = inject(MessageDialogService);
    private readonly _ephemeralMessage = inject(EphemeralMessageService);

    private _language = new BehaviorSubject<string>(null);
    private _productId: number;
    private _onDestroy = new Subject<void>();
    @ViewChildren(MatExpansionPanel)
    private _matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    readonly languageList$ = this._productsSrv.product.languages.get$()
        .pipe(
            first(Boolean),
            tap(languages => this._language.next(languages?.find(lang => lang.is_default).code)),
            map(languages => languages.map(lang => lang.code))
        );

    readonly fieldsRestrictions = ProductFieldsRestriction;
    readonly sliderRestrictions = productChannelSliderRestrictions;
    readonly language$ = this._language.asObservable();
    readonly isLoadingOrSaving$ = booleanOrMerge([
        this._productsSrv.product.languages.inProgress$(),
        this._productsSrv.product.channelContents.texts.loading$(),
        this._productsSrv.product.channelContents.images.loading$()
    ]);

    readonly form = this._fb.group({
        name: ['', Validators.maxLength(ProductFieldsRestriction.productNameLength)],
        description: ['', htmlContentMaxLengthValidator(ProductFieldsRestriction.productDescriptionLength)],
        notes: ['', htmlContentMaxLengthValidator(ProductFieldsRestriction.productNotesLength)],
        slider1: null as { data: string; name: string; altText: string },
        slider2: null as { data: string; name: string; altText: string },
        slider3: null as { data: string; name: string; altText: string },
        slider4: null as { data: string; name: string; altText: string },
        slider5: null as { data: string; name: string; altText: string }
    });

    ngOnInit(): void {
        this._productsSrv.product.get$()
            .pipe(takeUntil(this._onDestroy))
            .subscribe(product => {
                this._productId = product.product_id;
                this._productsSrv.product.languages.load(product.product_id);
                this._productsSrv.product.channelContents.texts.load(product.product_id);
                this._productsSrv.product.channelContents.images.load(product.product_id);
            });

        this.refreshFormDataHandler();
        this.formChangeHandler();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._productsSrv.product.channelContents.texts.clear();
        this._productsSrv.product.channelContents.images.clear();
    }

    canChangeLanguage: (() => Observable<boolean>) = () => this.validateIfCanChangeLanguage();

    cancel(): void {
        this.form.markAsPristine();
        this._productsSrv.product.channelContents.texts.load(this._productId);
        this._productsSrv.product.channelContents.images.load(this._productId);
    }

    save(): void {
        this.save$().subscribe(() => {
            this._productsSrv.product.channelContents.texts.load(this._productId);
            this._productsSrv.product.channelContents.images.load(this._productId);
        });
    }

    save$(): Observable<void | void[]> {
        if (this.form.valid) {
            const textsToSave: ProductChannelTextContent[] = [];
            this.addTextToSave(textsToSave, 'name', ProductChannelContentTextType.productName);
            this.addTextToSave(textsToSave, 'description', ProductChannelContentTextType.description);
            this.addTextToSave(textsToSave, 'notes', ProductChannelContentTextType.notes);
            const operations$ = this.saveOrDeleteImages();
            if (textsToSave.length > 0) {
                operations$.push(this._productsSrv.product.channelContents.texts.update(this._productId, textsToSave));
            }
            return forkJoin(operations$).pipe(tap(() => this._ephemeralMessage.showSaveSuccess()));
        } else {
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanelQueryList);
            return of();
        }
    }

    changeLanguage(newLanguage: string): void {
        this._language.next(newLanguage);
    }

    private refreshFormDataHandler(): void {
        combineLatest([
            this.language$,
            this._productsSrv.product.channelContents.texts.get$()
        ]).pipe(
            filter(resp => resp.every(Boolean)),
            takeUntil(this._onDestroy)
        ).subscribe(([language, texts]) => {
            this.applyOnTextFields(language, texts, (field, value) => field.setValue(value));
        });

        combineLatest([
            this.language$,
            this._productsSrv.product.channelContents.images.get$()
        ]).pipe(
            filter(resp => resp.every(Boolean)),
            takeUntil(this._onDestroy)
        ).subscribe(([language, images]) => {
            this.applyOnImageFields(language, images, (field, value) => field.setValue(value));
        });
    }

    private formChangeHandler(): void {
        combineLatest([
            this.language$,
            this._productsSrv.product.channelContents.texts.get$(),
            this._productsSrv.product.channelContents.images.get$(),
            this.form.valueChanges
        ])
            .pipe(
                takeUntil(this._onDestroy),
                filter(([language, texts, images]) => !!language && !!texts && !!images))
            .subscribe(([language, texts, images]) => {
                this.applyOnTextFields(language, texts,
                    (field, value) => FormControlHandler.checkAndRefreshDirtyState(field, value));
                this.applyOnImageFields(language, images,
                    (field, value) => FormControlHandler.checkAndRefreshDirtyState(field, value));
            });
    }

    private validateIfCanChangeLanguage(): Observable<boolean> {
        if (this.form.dirty) {
            return this._msgDialogSrv.openRichUnsavedChangesWarn().pipe(
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

    private saveOrDeleteImages(): Observable<void>[] {
        const fields = [{
            field: this.form.get('slider1'),
            type: ProductChannelContentImageType.landscape,
            position: 1
        }, {
            field: this.form.get('slider2'),
            type: ProductChannelContentImageType.landscape,
            position: 2
        }, {
            field: this.form.get('slider3'),
            type: ProductChannelContentImageType.landscape,
            position: 3
        }, {
            field: this.form.get('slider4'),
            type: ProductChannelContentImageType.landscape,
            position: 4
        }, {
            field: this.form.get('slider5'),
            type: ProductChannelContentImageType.landscape,
            position: 5
        }];
        const imagesToUpload = [] as ProductChannelImageContent[];
        const imagesToDelete = [] as ProductChannelImageContent[];
        const lang = this._language.getValue();
        for (const field of fields) {
            if (field.field.dirty) {
                const image = {
                    language: lang,
                    type: field.type,
                    position: field.position
                } as ProductChannelImageContent;
                if (field.field.value) {
                    image.image = field.field.value.name;
                    image.image_url = field.field.value.data;
                    image.alt_text = field.field.value.altText;
                    imagesToUpload.push(image);
                } else {
                    imagesToDelete.push(image);
                }
            }
        }
        const operations: Observable<void>[] = [];
        if (imagesToUpload.length > 0) {
            operations.push(this._productsSrv.product.channelContents.images.update(this._productId, imagesToUpload));
        }
        if (imagesToDelete.length > 0) {
            imagesToDelete.forEach(img =>
                operations.push(this._productsSrv.product.channelContents.images.delete(this._productId, lang, img.type, img.position)));
        }
        return operations;
    }

    private applyOnTextFields(
        language: string, texts: ProductChannelTextContent[], doOnField: (field: AbstractControl, value: string) => void
    ): void {
        this.applyOnTextField('name', ProductChannelContentTextType.productName, language, texts, doOnField);
        this.applyOnTextField('description', ProductChannelContentTextType.description, language, texts, doOnField);
        this.applyOnTextField('notes', ProductChannelContentTextType.notes, language, texts, doOnField);
    }

    private applyOnTextField(
        fieldName: string, type: ProductChannelContentTextType, language: string, texts: ProductChannelTextContent[],
        doOnField: (field: AbstractControl, value: string) => void
    ): void {
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
        language: string, images: ProductChannelImageContent[], doOnField: (field: AbstractControl, value: Partial<ObFile>) => void
    ): void {
        this.applyOnImageField(this.form, 'slider1', ProductChannelContentImageType.landscape, 1, language, images, doOnField);
        this.applyOnImageField(this.form, 'slider2', ProductChannelContentImageType.landscape, 2, language, images, doOnField);
        this.applyOnImageField(this.form, 'slider3', ProductChannelContentImageType.landscape, 3, language, images, doOnField);
        this.applyOnImageField(this.form, 'slider4', ProductChannelContentImageType.landscape, 4, language, images, doOnField);
        this.applyOnImageField(this.form, 'slider5', ProductChannelContentImageType.landscape, 5, language, images, doOnField);
    }

    private applyOnImageField(
        form: UntypedFormGroup, fieldName: string, type: ProductChannelContentImageType, position: number,
        language: string, images: ProductChannelImageContent[],
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

    private addTextToSave(textsToSave: ProductChannelTextContent[], fieldName: string, textType: ProductChannelContentTextType): void {
        const field = this.form.get(fieldName);
        if (field.dirty) {
            textsToSave.push({
                language: this._language.getValue(),
                type: textType,
                value: field.value
            });
        }
    }
}
