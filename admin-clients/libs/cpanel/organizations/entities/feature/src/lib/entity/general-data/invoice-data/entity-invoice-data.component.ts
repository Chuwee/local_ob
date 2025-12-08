import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { EntitiesService } from '@admin-clients/cpanel/organizations/entities/data-access';
import { Entity, Country, Region, CountriesService, RegionsService, PutEntity } from '@admin-clients/shared/common/data-access';
import { EphemeralMessageService, SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { AfterViewInit, ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit, QueryList } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import {
    AbstractControl,
    FormGroup, ReactiveFormsModule, UntypedFormBuilder, ValidationErrors, ValidatorFn, Validators
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatExpansionModule, MatExpansionPanel } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, throwError } from 'rxjs';
import { filter, map, tap } from 'rxjs/operators';

@Component({
    selector: 'app-entity-invoice-data',
    templateUrl: './entity-invoice-data.component.html',
    styleUrls: ['./entity-invoice-data.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MatExpansionModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatButtonModule, FormContainerComponent,
        FormControlErrorsComponent, AsyncPipe, ReactiveFormsModule, TranslatePipe, FlexLayoutModule, SelectSearchComponent,
        MatProgressSpinnerModule
    ]
})
export class EntityInvoiceDataComponent implements OnInit, AfterViewInit, WritingComponent {
    readonly #fb = inject(UntypedFormBuilder);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #entitiesSrv = inject(EntitiesService);
    readonly #countriesSrv = inject(CountriesService);
    readonly #regionsSrv = inject(RegionsService);
    readonly #onDestroy = inject(DestroyRef);

    private _matExpansionPanels: QueryList<MatExpansionPanel>;
    #entityId: number;

    form: FormGroup;
    entity$: Observable<Entity>;
    reqInProgress$: Observable<boolean>;
    countries$: Observable<Country[]>;
    regions$: Observable<Region[]>;

    ngOnInit(): void {
        this.initForm();
        this.model();
    }

    ngAfterViewInit(): void {
        this.refreshFormDataHandler();
    }

    save(): void {
        this.save$().subscribe(() => this.form.markAsPristine());
    }

    save$(): Observable<void> {
        this.markIbanAsDirty();
        if (this.form.valid) {
            const { address, country, region, city, postalCode, iban } = this.form.value;
            const bankAccount = Object.values(iban).join('');
            const updatedEntity: PutEntity = {
                invoice_data:
                {
                    address, city, postal_code: postalCode,
                    country: { code: country },
                    country_subdivision: { code: region || '' }
                }
            };
            if (bankAccount.length) {
                updatedEntity.invoice_data.bank_account = bankAccount;
            }
            return this.#entitiesSrv.updateEntity(this.#entityId, updatedEntity)
                .pipe(tap(() => {
                    this.#ephemeralSrv.showSuccess({ msgKey: 'ENTITY.UPDATE_SUCCESS' });
                    this.#entitiesSrv.loadEntity(this.#entityId);
                }));
        } else {
            this.showValidationErrors();
            return throwError(() => 'invalid form');
        }
    }

    cancel(): void {
        this.#entitiesSrv.loadEntity(this.#entityId);
    }

    private initForm(): void {
        const iban = this.#fb.group({
            countryCode: [null, [Validators.pattern('^[A-Z0-9]{4}$')]],
            bankEntity: [null, [Validators.pattern('^[0-9]{4}$')]],
            office: [null, [Validators.pattern('^[0-9]{4}$')]],
            control: [null, [Validators.pattern('^[0-9]{2}$')]],
            account: [null, [Validators.pattern('^[0-9]{10}$')]]
        }, { validators: [this.ibanValidator()] });

        this.form = this.#fb.group({
            address: [null, Validators.required],
            country: [null, Validators.required],
            region: null,
            city: null,
            postalCode: null,
            iban
        });
    }

    private ibanValidator(): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            const iban = Object.values(control.value).join('');
            const expr = /([A-Z]{2})\s*\t*(\d{22})/;
            if (!expr.test(iban) && !!iban.length) {
                return { invalidFormat: true };
            }
            return null;
        };
    }

    private model(): void {
        this.#countriesSrv.loadCountries();

        this.countries$ = this.#countriesSrv.getCountries$()
            .pipe(
                takeUntilDestroyed(this.#onDestroy),
                filter(countries => !!countries),
                map(countries => countries.map(country => ({
                    name: country.name,
                    code: country.code
                })))
            );

        this.regions$ = this.#regionsSrv.getRegions$()
            .pipe(
                takeUntilDestroyed(this.#onDestroy),
                filter(regions => !!regions),
                map(regions => regions.map(region => ({ name: region.name, code: region.code })))
            );

        this.entity$ = this.#entitiesSrv.getEntity$()
            .pipe(filter(entity => !!entity));

        this.reqInProgress$ = booleanOrMerge([
            this.#entitiesSrv.isEntityLoading$(),
            this.#entitiesSrv.isEntitySaving$(),
            this.#countriesSrv.isCountriesLoading$(),
            this.#regionsSrv.isRegionsLoading$()
        ]);
    }

    private refreshFormDataHandler(): void {
        this.form.get('country').valueChanges
            .pipe(
                takeUntilDestroyed(this.#onDestroy),
                filter(countryCode => !!countryCode)
            )
            .subscribe(countryCode => {
                this.#regionsSrv.loadSystemRegions(countryCode);
            });

        this.entity$
            .pipe(
                filter(entity => !!entity),
                takeUntilDestroyed(this.#onDestroy)
            ).subscribe(({ id, invoice_data: invoiceData }) => {
                this.#entityId = id;
                const countryCode = invoiceData?.bank_account?.slice(0, 4);
                const bankEntity = invoiceData?.bank_account?.slice(4, 8);
                const office = invoiceData?.bank_account?.slice(8, 12);
                const control = invoiceData?.bank_account?.slice(12, 14);
                const account = invoiceData?.bank_account?.slice(14, 24);
                this.form.patchValue({
                    address: invoiceData?.address,
                    city: invoiceData?.city,
                    postalCode: invoiceData?.postal_code,
                    country: invoiceData?.country?.code,
                    region: invoiceData?.country_subdivision?.code,
                    iban: { countryCode, bankEntity, office, control, account }
                });
                this.form.markAsPristine();
            });
    }

    private showValidationErrors(): void {
        this.form.markAllAsTouched();
        scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanels);
    }

    private markIbanAsDirty(): void {
        this.form.get('iban.countryCode').markAsDirty();
        this.form.get('iban.bankEntity').markAsDirty();
        this.form.get('iban.office').markAsDirty();
        this.form.get('iban.control').markAsDirty();
        this.form.get('iban.account').markAsDirty();
    }
}
