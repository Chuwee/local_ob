import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import {
    AfterViewInit,
    ChangeDetectionStrategy,
    Component, DestroyRef, inject,
    OnInit,
    QueryList,
    ViewChild,
    ViewChildren
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AbstractControl, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MAT_DATE_FORMATS } from '@angular/material/core';
import { MatExpansionPanel } from '@angular/material/expansion';
import moment from 'moment';
import { combineLatest, forkJoin, Observable, of, throwError } from 'rxjs';
import { filter, finalize, first, map, shareReplay, switchMap, take, tap } from 'rxjs/operators';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { CommunicationTextContent, CommunicationContentTextType as TextType } from '@admin-clients/cpanel/shared/data-access';
import {
    GiftCardExpirationTimePeriod,
    GiftCardGroupContentImageType as ImageType, GiftCardGroupContentImage,
    GiftCardGroupContentImageRequest, GiftCardGroupDateType, giftCardGroupImageRestrictions,
    PutGiftCardGroupConfig, PutVoucherGroup, VoucherGroupFieldRestrictions, VouchersService
} from '@admin-clients/cpanel-vouchers-data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { EphemeralMessageService, TabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { ObFile } from '@admin-clients/shared/data-access/models';
import {
    booleanOrMerge, rangeValidator, FormControlHandler, atLeastOneRequiredInFormGroup
} from '@admin-clients/shared/utility/utils';

@Component({
    selector: 'app-gift-card-group-sale',
    templateUrl: './gift-card-group-sale.component.html',
    styleUrls: ['./gift-card-group-sale.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class GiftCardGroupSaleComponent implements OnInit, AfterViewInit {
    readonly #destroyRef = inject(DestroyRef);
    readonly #fb = inject(UntypedFormBuilder);
    readonly #voucherSrv = inject(VouchersService);
    readonly #entitiesService = inject(EntitiesBaseService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #auth = inject(AuthenticationService);
    readonly #formats = inject(MAT_DATE_FORMATS);

    @ViewChildren(MatExpansionPanel) private readonly _matExpansionPanels: QueryList<MatExpansionPanel>;

    @ViewChild('languageTabs') private readonly _languageTabs: TabsMenuComponent;

    #giftCardGroupId: number;

    readonly fieldRestrictions = VoucherGroupFieldRestrictions;
    readonly dateFormat = moment.localeData().longDateFormat(this.#formats.display.dateInput).toLowerCase();

    readonly reqInProgress$ = booleanOrMerge([
        this.#voucherSrv.isVoucherGroupSaving$(),
        this.#voucherSrv.isGiftCardImageContentsSaving$(),
        this.#voucherSrv.isGiftCardImageContentsRemoving$(),
        this.#voucherSrv.isGiftCardGroupConfigSaving$(),
        this.#voucherSrv.isGiftCardTextContentsSaving$()
    ]);

    readonly giftCardGroup$ = this.#voucherSrv.getVoucherGroup$()
        .pipe(
            filter(giftCardGroup => !!giftCardGroup),
            tap(giftCardGroup => {
                this.#giftCardGroupId = giftCardGroup.id;
                this.form.get('operativeForm.expiration').patchValue({
                    type: giftCardGroup.expiration?.type,
                    date: giftCardGroup.expiration?.date || null,
                    relative: {
                        timeValue: giftCardGroup.expiration.amount || null,
                        timeUnit: giftCardGroup.expiration.time_period || null
                    }
                });
                this.form.markAsPristine();
            }),
            takeUntilDestroyed(this.#destroyRef)
        );

    readonly giftCardGroupConfig$ = this.#voucherSrv.getGiftCardGroupConfig$()
        .pipe(
            tap(config => {
                this.form.get('operativeForm.range').patchValue({
                    minValue: config?.price_range?.from || null,
                    maxValue: config?.price_range?.to || null
                });
                this.form.markAsPristine();
            })
        );

    readonly languages$ = this.#entitiesService.getEntity$()
        .pipe(
            first(entity => !!entity),
            map(entity => entity.settings?.languages.available),
            tap(langs => {
                langs.forEach(lang => {
                    this.contentsForm.addControl(lang, this.#fb.group({
                        name: [null, [
                            Validators.required,
                            Validators.maxLength(this.fieldRestrictions.maxNameLength),
                            Validators.minLength(this.fieldRestrictions.minNameLength)]],
                        description: [null, Validators.required],
                        images: this.#fb.group({
                            image1: null,
                            image2: null,
                            image3: null
                        }, { validators: [atLeastOneRequiredInFormGroup()] })
                    }));
                });
            })
        );

    readonly contentsForm = this.#fb.group({});
    readonly operativeForm = this.#fb.group({
        range: this.#fb.group({
            minValue: [null, [Validators.required]],
            maxValue: [null, [Validators.required]]
        }, { validators: [rangeValidator('minValue', 'maxValue')] }),
        expiration: this.#fb.group({
            type: null,
            date: null,
            relative: this.#fb.group({
                timeValue: null,
                timeUnit: null
            })
        })
    });

    readonly form = this.#fb.group({
        contents: this.contentsForm,
        operativeForm: this.operativeForm
    });

    readonly giftCardImgRestrictions = giftCardGroupImageRestrictions;
    readonly giftCardGroupDateType = GiftCardGroupDateType;
    readonly timeUnitList = Object.values(GiftCardExpirationTimePeriod);
    dateType: GiftCardGroupDateType;

    readonly currency$ = this.#voucherSrv.getVoucherGroup$()
        .pipe(
            first(),
            switchMap(voucherGroup => {
                if (voucherGroup.currency_code) {
                    return of(voucherGroup.currency_code);
                } else {
                    return this.#auth.getLoggedUser$()
                        .pipe(first(), map(user => user.currency));
                }
            }),
            shareReplay({ bufferSize: 1, refCount: true })
        );

    ngOnInit(): void {
        this.form.get('operativeForm.expiration.type').valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(value => {
                if (value === this.giftCardGroupDateType.fixed) {
                    this.dateType = this.giftCardGroupDateType.fixed;
                } else if (value === this.giftCardGroupDateType.relative) {
                    this.dateType = this.giftCardGroupDateType.relative;
                } else {
                    this.dateType = this.giftCardGroupDateType.disabled;
                }
            });
    }

    ngAfterViewInit(): void {
        combineLatest([
            this.languages$,
            this.#voucherSrv.getGiftCardImageContents$(),
            this.#voucherSrv.getGiftCardTextContents$()
        ])
            .pipe(
                takeUntilDestroyed(this.#destroyRef),
                filter(values => values.every(value => !!value)),
                finalize(() => this.form.markAsPristine()))
            .subscribe(([languages, images, texts]) => {
                languages.forEach(language => {
                    this.applyOnTextFields(language, texts, (field, value) => {
                        if (value) {
                            field.setValue(value);
                        }
                    });
                    this.applyOnImageFields(language, images, (field, value) => {
                        if (value) {
                            field.setValue(value);
                        }
                    });
                });
            });

        combineLatest([
            this.languages$,
            this.#voucherSrv.getGiftCardImageContents$(),
            this.#voucherSrv.getGiftCardTextContents$(),
            this.form.valueChanges
        ])
            .pipe(
                filter(([languages, images, texts]) => !!languages && !!texts && !!images),
                takeUntilDestroyed(this.#destroyRef)
            )
            .subscribe(([languages, images, texts]) => {
                languages.forEach(language => {
                    this.applyOnTextFields(language, texts, (field, value) => FormControlHandler.checkAndRefreshDirtyState(field, value));
                    this.applyOnImageFields(language, images, (field, value) => FormControlHandler.checkAndRefreshDirtyState(field, value));
                });
            });
    }

    cancel(): void {
        this.#voucherSrv.loadVoucherGroup(this.#giftCardGroupId);
    }

    save(): void {
        this.save$().subscribe();
    }

    save$(): Observable<void[]> {
        if (this.form.valid) {
            const textsToSave: CommunicationTextContent[] = [];
            this.addTextToSave(textsToSave, 'name', TextType.name);
            this.addTextToSave(textsToSave, 'description', TextType.description);
            const obs$ = this.saveOrDeleteImages();
            if (textsToSave.length > 0) {
                obs$.push(this.#voucherSrv.saveGiftCardTextContents(this.#giftCardGroupId, textsToSave));
            }
            const rangeForm = this.form.get('operativeForm.range');
            if (rangeForm?.touched) {
                const updatedConfig: PutGiftCardGroupConfig = {
                    price_range: {
                        from: rangeForm.value.minValue,
                        to: rangeForm.value.maxValue
                    }
                };
                obs$.push(this.#voucherSrv.saveGiftCardGroupConfig(this.#giftCardGroupId, updatedConfig));
            }
            const expirationForm = this.form.get('operativeForm.expiration');
            const relativeDateForm = this.form.get('operativeForm.expiration.relative');
            if (expirationForm?.touched) {
                const updatedGroup: PutVoucherGroup = {
                    expiration: {
                        type: expirationForm.value.type,
                        date: expirationForm.value.type === this.giftCardGroupDateType.fixed ? expirationForm.value.date : null,
                        amount: expirationForm.value.type === this.giftCardGroupDateType.relative ? relativeDateForm.value.timeValue : null,
                        time_period: expirationForm.value.type === this.giftCardGroupDateType.relative
                            ? relativeDateForm.value.timeUnit
                            : null
                    }
                };
                obs$.push(this.#voucherSrv.saveVoucherGroup(this.#giftCardGroupId, updatedGroup));
            }
            return forkJoin(obs$)
                .pipe(
                    tap(() => {
                        this.#ephemeralSrv.showSaveSuccess();
                        this.#voucherSrv.loadVoucherGroup(this.#giftCardGroupId);
                    })
                );
        } else {
            this.form.markAllAsTouched();
            this._languageTabs.goToInvalidCtrlTab();
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanels);
            return throwError(() => 'invalid form');
        }
    }

    private saveOrDeleteImages(): Observable<void>[] {
        const obs$ = [] as Observable<void>[];
        this.languages$.pipe(take(1)).subscribe(languages => {
            const imagesToUpload = [] as GiftCardGroupContentImageRequest[];
            const imagesToDelete = [] as GiftCardGroupContentImageRequest[];
            languages.forEach(lang => {
                const fields = [
                    {
                        field: this.form.get(`contents.${lang}.images.image1`),
                        type: ImageType.cardDesign1
                    },
                    {
                        field: this.form.get(`contents.${lang}.images.image2`),
                        type: ImageType.cardDesign2
                    },
                    {
                        field: this.form.get(`contents.${lang}.images.image3`),
                        type: ImageType.cardDesign3
                    }
                ];
                for (const field of fields) {
                    if (field.field.dirty) {
                        const image = {
                            language: lang,
                            type: field.type
                        } as GiftCardGroupContentImageRequest;
                        if (field.field.value) {
                            image.image = field.field.value.data;
                            image.alt_text = field.field.value.altText;
                            imagesToUpload.push(image);
                        } else {
                            imagesToDelete.push(image);
                        }
                    }
                }
            });
            if (imagesToUpload.length > 0) {
                obs$.push(this.#voucherSrv.saveGiftCardGroupContentImages(this.#giftCardGroupId, imagesToUpload));
            }
            if (imagesToDelete.length > 0) {
                obs$.push(this.#voucherSrv.deleteGiftCardContentImages(this.#giftCardGroupId, imagesToDelete));
            }
        });
        return obs$;
    }

    private applyOnTextFields(language: string, texts: CommunicationTextContent[],
        doOnField: (field: AbstractControl, value: string) => void): void {
        this.applyOnTextField(`${language}.name`, TextType.name, language, texts, doOnField);
        this.applyOnTextField(`${language}.description`, TextType.description, language, texts, doOnField);
    }

    private applyOnTextField(fieldName: string, type: TextType, language: string, texts: CommunicationTextContent[],
        doOnField: (field: AbstractControl, value: string) => void): void {
        const field = this.contentsForm.get(fieldName);
        for (const text of texts) {
            if (text.language === language && text.type === type) {
                doOnField(field, text.value);
                return;
            }
        }
        doOnField(field, null);
    }

    private addTextToSave(textsToSave: CommunicationTextContent[], fieldName: string, textType: TextType): void {
        this.languages$.pipe(take(1)).subscribe(languages => {
            languages.forEach(language => {
                const field = this.contentsForm.get(`${language}.${fieldName}`);
                if (field.dirty) {
                    textsToSave.push({
                        language,
                        type: textType,
                        value: field.value
                    });
                }
            });
        });
    }

    private applyOnImageFields(
        language: string, images: GiftCardGroupContentImage[],
        doOnField: (field: AbstractControl, value: Partial<ObFile>) => void): void {
        this.applyOnImageField(
            this.contentsForm.get(`${language}.images`) as UntypedFormGroup,
            'image1',
            ImageType.cardDesign1,
            language,
            images,
            doOnField
        );
        this.applyOnImageField(
            this.contentsForm.get(`${language}.images`) as UntypedFormGroup,
            'image2',
            ImageType.cardDesign2,
            language,
            images,
            doOnField
        );
        this.applyOnImageField(
            this.contentsForm.get(`${language}.images`) as UntypedFormGroup,
            'image3',
            ImageType.cardDesign3,
            language,
            images,
            doOnField
        );
    }

    private applyOnImageField(
        form: UntypedFormGroup,
        fieldName: string,
        type: ImageType,
        language: string,
        images: GiftCardGroupContentImage[],
        doOnField: (field: AbstractControl, value: Partial<ObFile>) => void
    ): void {
        const field = form.get(fieldName);
        for (const image of images) {
            if (image.language === language && image.type === type) {
                doOnField(field, { data: image.image_url, altText: image.alt_text });
                return;
            }
        }
        doOnField(field, null);
    }

}
