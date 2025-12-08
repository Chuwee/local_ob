import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { EntitiesBaseService, Entity, EntitiesFilterFields } from '@admin-clients/shared/common/data-access';
import { SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, DestroyRef, inject, input, OnInit, output } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule, UntypedFormGroup } from '@angular/forms';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatDialogContent } from '@angular/material/dialog';
import { MatError, MatFormField, MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { MatOption, MatSelect } from '@angular/material/select';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, combineLatest, Observable } from 'rxjs';
import { filter, first, map } from 'rxjs/operators';
import { importDateFormats } from '../models/import-date-formats.enum';
import { ProductTypeImport } from '../models/product-type-import.enum';

@Component({
    selector: 'app-import-customer-options',
    imports: [
        TranslatePipe, ReactiveFormsModule, MatFormField, MatLabel, MatSelect, MatOption, AsyncPipe, MatCheckbox, MatRadioButton,
        MatProgressSpinner, SelectSearchComponent, FormControlErrorsComponent, MatDialogContent, MatError, MatRadioGroup,
        MatDialogContent, MatIcon, MatTooltip
    ],
    templateUrl: './import-customer-options.component.html',
    styleUrls: ['./import-customer-options.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ImportCustomerOptionsComponent implements OnInit {
    readonly #authSrv = inject(AuthenticationService);
    readonly #entitiesSrv = inject(EntitiesBaseService);
    readonly #destroyRef = inject(DestroyRef);

    readonly $optionsFormGroup = input.required<UntypedFormGroup>({ alias: 'optionsFormGroup' });
    readonly $entityControlName = input.required<string>({ alias: 'entityControlName' });
    readonly $overrideControlName = input.required<string>({ alias: 'overrideControlName' });
    readonly $productsControlName = input.required<string>({ alias: 'productsControlName' });
    readonly $productVendorControlName = input.required<string>({ alias: 'productVendorControlName' });
    readonly $productsWithVendorControlName = input.required<string>({ alias: 'productsWithVendorControlName' });
    readonly $dateFormatControlName = input.required<string>({ alias: 'dateFormatControlName' });
    readonly $productsTypeControlName = input.required<string>({ alias: 'productsTypeControlName' });
    readonly isLoading = output<boolean>();

    readonly #isProductImportDisabledBS = new BehaviorSubject<boolean>(false);
    readonly isProductImportDisabled$ = this.#isProductImportDisabledBS.asObservable();
    readonly $entityControl = computed(() => this.$optionsFormGroup()?.get(this.$entityControlName()));

    entities$: Observable<Partial<Entity>[]>;
    isLoading$: Observable<boolean>;
    productTypeImport = ProductTypeImport;

    dateFormats = importDateFormats;

    readonly canSelectEntity$ = this.#authSrv.canReadMultipleEntities$();
    readonly $entityVendorConfig = toSignal(this.#entitiesSrv.getEntity$()
        .pipe(filter(Boolean), map(entity => entity?.settings?.external_integration?.auth_vendor)));

    ngOnInit(): void {
        this.#setLoading();
        this.#setEntities();
        this.#setProductTypeImportDisabled();
        this.#productsChangeHandler();
        this.#productWithVendorChangeHandler();
        this.#entityChangeHandler();
    }

    compareById(option: Entity, option2: Entity): boolean {
        return option?.id === option2?.id;
    }

    #setEntities(): void {
        combineLatest([
            this.canSelectEntity$,
            this.#authSrv.getLoggedUser$().pipe(first())
        ])
            .pipe(
                map(([canSelectEntity, user]) => {
                    if (!canSelectEntity) {
                        this.$entityControl()?.patchValue(user.entity);
                        this.$entityControl()?.markAsDirty();
                    } else {
                        this.entities$ = this.#entitiesSrv.entityList.getData$()
                            .pipe(filter(Boolean));
                        this.#entitiesSrv.entityList.load({
                            limit: 999,
                            sort: 'name:asc',
                            fields: [
                                EntitiesFilterFields.operatorId,
                                EntitiesFilterFields.name,
                                EntitiesFilterFields.allowMembers
                            ]
                        });
                    }
                })
            )
            .subscribe();
    }

    #setLoading(): void {
        const isLoading$ = this.#entitiesSrv.entityList.inProgress$();
        this.isLoading$ = isLoading$;
        isLoading$
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(isLoading => this.isLoading.emit(isLoading));
    }

    #productsChangeHandler(): void {
        this.$optionsFormGroup()?.get(this.$productsControlName())?.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(isProductsImport => this.#enableProductTypeImport(isProductsImport));
    }

    #productWithVendorChangeHandler(): void {
        this.$optionsFormGroup()?.get(this.$productsWithVendorControlName())?.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(isProductsWithVendor => {
                const productVendorControl = this.$optionsFormGroup()?.get(this.$productVendorControlName());
                if (isProductsWithVendor) {
                    productVendorControl?.enable({ emitEvent: false });
                    if (this.$entityVendorConfig()?.vendor_id?.length === 1) {
                        const vendorId = this.$entityVendorConfig()?.vendor_id[0];
                        productVendorControl?.patchValue(vendorId, { emitEvent: false });
                    }
                } else if (!isProductsWithVendor) {
                    productVendorControl?.disable({ emitEvent: false });
                }
            });
    }

    #entityChangeHandler(): void {
        this.$entityControl()?.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(() => {
                this.$optionsFormGroup()?.get(this.$productsWithVendorControlName())?.setValue(false, { emitEvent: false });
            });
    }

    #enableProductTypeImport(isProductsImport: boolean): void {
        const productsTypeControl = this.$optionsFormGroup()?.get(this.$productsTypeControlName());
        if (isProductsImport && productsTypeControl?.disabled) {
            productsTypeControl?.enable({ emitEvent: false });
            this.#isProductImportDisabledBS.next(false);
        } else if (!isProductsImport && productsTypeControl?.enabled) {
            productsTypeControl?.disable({ emitEvent: false });
            this.#isProductImportDisabledBS.next(true);
        }
    }

    #setProductTypeImportDisabled(): void {
        if (!this.$optionsFormGroup()?.get(this.$productsControlName())?.value) {
            this.#isProductImportDisabledBS.next(true);
        } else {
            this.#isProductImportDisabledBS.next(false);
        }
    }
}
