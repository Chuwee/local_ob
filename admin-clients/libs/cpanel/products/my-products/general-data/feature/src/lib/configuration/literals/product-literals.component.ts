import {
    ProductAttribute, ProductAttributeValueListChannelContents, ProductsService
} from '@admin-clients/cpanel/products/my-products/data-access';
import {
    EphemeralMessageService, LanguageBarComponent, MessageDialogService, SearchTableComponent, UnsavedChangesDialogResult
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnDestroy, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import {
    first, map, filter, tap, shareReplay, firstValueFrom, switchMap, combineLatest, BehaviorSubject, Observable, of, forkJoin, from
} from 'rxjs';

@Component({
    selector: 'app-product-literals',
    imports: [
        FormContainerComponent, LanguageBarComponent, ReactiveFormsModule, MaterialModule,
        AsyncPipe, TranslatePipe, FlexLayoutModule, SearchTableComponent
    ],
    templateUrl: './product-literals.component.html',
    styleUrls: ['./product-literals.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductConfigLiteralsComponent implements OnInit, OnDestroy {
    readonly #fb = inject(FormBuilder);
    readonly #productsSrv = inject(ProductsService);
    readonly #messageDialogService = inject(MessageDialogService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #destroyRef = inject(DestroyRef);

    readonly columns = ['key', 'value'];

    readonly selectedAttributeId = this.#fb.control(null as number);
    readonly selectedAttributeName = this.#fb.control(null as string);
    readonly attributeLiteralForm = this.#fb.control(null as string);
    readonly valuesLiteralsForm = this.#fb.group({});

    readonly form = this.#fb.group({
        attr: this.attributeLiteralForm,
        values: this.valuesLiteralsForm
    });

    readonly isInProgress$ = booleanOrMerge([
        this.#productsSrv.product.inProgress$(),
        this.#productsSrv.product.attribute.loading$(),
        this.#productsSrv.product.attributeChannelContents.loading$(),
        this.#productsSrv.product.attributeValuesList.loading$(),
        this.#productsSrv.product.attributeValueChannelContents.loading$()
    ]);

    readonly selectedLanguageBS = new BehaviorSubject<string>(null);
    readonly languageList$ = this.#productsSrv.product.get$()
        .pipe(
            first(Boolean),
            tap(product => this.#productsSrv.product.languages.load(product.product_id)),
            switchMap(() => this.#productsSrv.product.languages.get$()
                .pipe(
                    first(Boolean),
                    tap(languages => {
                        const defaultLang = languages?.find(lang => lang.is_default).code;
                        this.selectedLanguageBS.next(defaultLang);
                    }),
                    map(langs => langs.map(lang => lang.code))
                )
            ),
            shareReplay({ bufferSize: 1, refCount: true })
        );

    readonly attributes$ = this.#productsSrv.product.get$()
        .pipe(
            first(Boolean),
            tap(product => {
                this.#productId = product.product_id;
                this.#productsSrv.product.attributesList.load(product.product_id);
            }),
            switchMap(() => this.#productsSrv.product.attributesList.get$()
                .pipe(
                    filter(Boolean),
                    tap(attributes => this.#allAttributes = attributes)
                )),
            shareReplay({ bufferSize: 1, refCount: true })
        );

    #productId: number;
    #allAttributes: ProductAttribute[];

    readonly setAttributeForm$ = combineLatest([
        this.#productsSrv.product.attributeChannelContents.getData$(),
        this.selectedLanguageBS // only as a trigger
    ]).pipe(
        filter(data => data.every(Boolean)),
        tap(([attributeContents, _]) => {
            this.attributeLiteralForm.setValue(attributeContents?.[0]?.value);
        }));

    readonly valuesLiterals$ = combineLatest([
        this.#productsSrv.product.attributeValuesListChannelContents.getData$(),
        this.#productsSrv.product.attributeValuesList.getData$(),
        this.selectedLanguageBS //only as a trigger
    ]).pipe(
        filter(data => data.every(Boolean)),
        map(([valuesLiterals, values, _]) => values.map(value => {
            const valueWithLiteral = valuesLiterals.find(valueLiteral => valueLiteral.valueId === value.value_id);
            const valueGroup = this.#fb.group(
                { key: value.name, value: valueWithLiteral?.value }
            );
            this.valuesLiteralsForm.setControl(String(value.value_id), valueGroup);
            const valueForTable = {
                id: value.value_id,
                key: value.name,
                value: valueWithLiteral?.value
            };
            return valueForTable;
        })),
        shareReplay({ bufferSize: 1, refCount: true })
    );

    ngOnInit(): void {
        combineLatest([
            this.#productsSrv.product.get$(),
            this.attributes$,
            this.selectedLanguageBS
        ]).pipe(
            first(data => data.every(Boolean)),
            tap(([product, attributes, language]) => {
                const defaultAttr = attributes[0];
                this.#productsSrv.product.attributeValuesList.load(product.product_id, defaultAttr.attribute_id);
                this.#productsSrv.product.attributeChannelContents.load(
                    product.product_id, defaultAttr.attribute_id, language);
                this.#productsSrv.product.attributeValuesListChannelContents.load(
                    product.product_id, defaultAttr.attribute_id, language);
                this.selectedAttributeId.setValue(defaultAttr.attribute_id, { emitEvent: false });
                this.selectedAttributeName.setValue(defaultAttr.name, { emitEvent: false });
            })
        ).subscribe();

        this.selectedAttributeId.valueChanges.pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(selectedAttributeId => {
                this.selectedAttributeName.setValue(this.#allAttributes.find(attr => attr.attribute_id === selectedAttributeId).name);
                this.#productsSrv.product.attributeValuesList.load(this.#productId, selectedAttributeId);
                this.reload();
            });
    }

    ngOnDestroy(): void {
        this.#productsSrv.product.languages.clear();
        this.#productsSrv.product.attributesList.clear();
        this.#productsSrv.product.attributeValuesList.clear();
        this.#productsSrv.product.attributeValuesListChannelContents.clear();
    }

    filter = (q: string, { key, value }: { key: string; value: string }): boolean =>
        key.toLowerCase().includes(q.toLowerCase()) ||
        value?.toLowerCase().includes(q.toLowerCase());

    async save$(): Promise<boolean> {
        if (this.form.valid) {
            const requests: Observable<void>[] = [];
            if (this.attributeLiteralForm.dirty) {
                requests.push(this.#productsSrv.product.attributeChannelContents.save(
                    this.#productId,
                    this.selectedAttributeId.value,
                    [{
                        language: this.selectedLanguageBS.getValue(),
                        value: this.attributeLiteralForm.value,
                        key: 'ATTRIBUTE_NAME' // this might be an enum in the future
                    }]
                ));
            }

            const valueList = await firstValueFrom(this.#productsSrv.product.attributeValuesList.getData$());
            const oldLiterals = await firstValueFrom(this.#productsSrv.product.attributeValuesListChannelContents.getData$());
            const formliterals = this.valuesLiteralsForm.value as Record<string, { key: string; value: string }>;
            const result: ProductAttributeValueListChannelContents[] = [];
            valueList.forEach(value => {
                const newLiteral = formliterals?.[value.value_id].value;
                const oldLiteral = oldLiterals.find(literal => literal.valueId === value.value_id);
                if (newLiteral != null && (!oldLiteral?.value || newLiteral !== oldLiteral?.value)) {
                    result.push({
                        valueId: value.value_id,
                        key: 'VALUE_NAME',  // this might be an enum in the future
                        value: newLiteral,
                        language: this.selectedLanguageBS.getValue()
                    });
                }
            });

            if (result.length) {
                requests.push(this.#productsSrv.product.attributeValuesListChannelContents.save(
                    this.#productId,
                    this.selectedAttributeId.value,
                    result
                ));
            }
            if (requests.length) {
                forkJoin(requests).subscribe(() => {
                    this.reload();
                    this.#ephemeralSrv.showSaveSuccess();
                });
            }
            return true;
        } else {
            this.form.markAsPristine();
            return false;
        }
    }

    reload(): void {
        this.loadAttributeLiterals(this.selectedLanguageBS.getValue());
        this.loadValuesLiterals(this.selectedLanguageBS.getValue());
        this.form.markAsPristine();
    }

    async loadAttributeLiterals(lang?: string): Promise<void> {
        const productId = await firstValueFrom(this.#productsSrv.product.get$().pipe(first(), map(prod => prod.product_id)));
        const attributeId = this.selectedAttributeId.value;
        this.#productsSrv.product.attributeChannelContents.load(productId, attributeId, lang);
        this.valuesLiteralsForm.markAsPristine();
    }

    async loadValuesLiterals(lang?: string): Promise<void> {
        const productId = await firstValueFrom(this.#productsSrv.product.get$().pipe(first(), map(prod => prod.product_id)));
        const attributeId = this.selectedAttributeId.value;
        this.#productsSrv.product.attributeValuesListChannelContents.load(productId, attributeId, lang);
        this.valuesLiteralsForm.markAsPristine();
    }

    changeLanguage(newLanguage: string): void {
        this.selectedLanguageBS.next(newLanguage);
        this.reload();
    }

    canChangeLanguage: (() => Observable<boolean>) = () => this.validateIfCanChangeLanguage();

    private validateIfCanChangeLanguage(): Observable<boolean> {
        if (this.form.dirty) {
            return this.#messageDialogService.openRichUnsavedChangesWarn().pipe(
                switchMap(res => {
                    if (res === UnsavedChangesDialogResult.cancel) {
                        return of(false);
                    } else if (res === UnsavedChangesDialogResult.continue) {
                        return of(true);
                    } else {
                        return from(this.save$());
                    }
                }));
        }
        return of(true);
    }
}
