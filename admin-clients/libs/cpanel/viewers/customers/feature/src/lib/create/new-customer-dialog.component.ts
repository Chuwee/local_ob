import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import {
    CustomersService, CustomerFieldsRestrictions,
    CustomerGender, CustomerApiCodeError, CustomerType, PostCustomer,
    CustomerFormField,
    customerTitle,
    CustomerTitle
} from '@admin-clients/cpanel-viewers-customers-data-access';
import {
    CountriesService, Region, RegionsService, Entity, EntitiesBaseService, EntitiesBaseState, EntitiesFilterFields
} from '@admin-clients/shared/common/data-access';
import { DialogSize, SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { ApiErrorResponse } from '@admin-clients/shared/core/data-access';
import { Id } from '@admin-clients/shared/data-access/models';
import {
    allRequiredIfOneHasValueInFormGroup, booleanOrMerge,
    dateIsSameOrBefore, dateValidator
} from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, computed, effect, inject } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MAT_DATE_FORMATS, MatDateFormats, MatOption } from '@angular/material/core';
import { MatDatepicker, MatDatepickerInput } from '@angular/material/datepicker';
import { MAT_DIALOG_DATA, MatDialogActions, MatDialogContent, MatDialogRef, MatDialogTitle } from '@angular/material/dialog';
import { MatFormField, MatLabel, MatError, MatSuffix } from '@angular/material/form-field';
import { MatIcon, MatIconModule } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatSelect, MatSelectTrigger } from '@angular/material/select';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import moment, { Moment } from 'moment-timezone';
import { BehaviorSubject, combineLatest, iif, of } from 'rxjs';
import { catchError, filter, first, map, startWith, switchMap, tap } from 'rxjs/operators';
import { getFormattedDate, getObjectWithoutIgnoredValues } from '../utils/formating-functions';
import { VmNewCustomer } from './model/vm-new-customer.model';

@Component({
    selector: 'app-new-customer-dialog',
    templateUrl: './new-customer-dialog.component.html',
    styleUrls: ['./new-customer-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [EntitiesBaseState, EntitiesBaseService],
    imports: [
        MatIconModule, ReactiveFormsModule, FormControlErrorsComponent, FlexLayoutModule, TranslatePipe, AsyncPipe, SelectSearchComponent,
        MatFormField, MatLabel, MatSelect, MatOption, MatDatepicker, MatDatepickerInput, MatSelectTrigger, MatProgressSpinner, MatError,
        MatDialogTitle, MatDialogContent, MatDialogActions, MatIconButton, MatIcon, MatInput, MatButton, MatSuffix
    ]
})
export class NewCustomerDialogComponent implements OnDestroy {
    readonly #provincesBS: BehaviorSubject<Region[]> = new BehaviorSubject<Region[]>(null);
    readonly #dialogRef = inject(MatDialogRef<NewCustomerDialogComponent>);
    readonly #fb = inject(FormBuilder);
    readonly #auth = inject(AuthenticationService);
    readonly #entitiesSrv = inject(EntitiesBaseService);
    readonly #countriesSrv = inject(CountriesService);
    readonly #regionsSrv = inject(RegionsService);
    readonly #customersSrv = inject(CustomersService);
    readonly #translate = inject(TranslateService);
    readonly #formats: MatDateFormats = inject(MAT_DATE_FORMATS);
    readonly #data = inject<{ entityId: number }>(MAT_DIALOG_DATA);

    readonly form = this.#fb.group({
        entity: this.#fb.control(null as Entity, Validators.required),
        location: this.#fb.group({
            address: this.#fb.control({ value: '', disabled: true }, Validators.maxLength(CustomerFieldsRestrictions.customerAddressMaxLength)),
            city: this.#fb.control({ value: '', disabled: true }, Validators.maxLength(CustomerFieldsRestrictions.customerCityMaxLength)),
            country: this.#fb.control({ value: '', disabled: true }),
            postal_code: this.#fb.control({ value: '', disabled: true }, Validators.maxLength(CustomerFieldsRestrictions.customerPostalCodeMaxLength)),
            province_code: this.#fb.control({ value: '', disabled: true })
        }),
        birthday: this.#fb.control({ value: null as Moment, disabled: true }),
        email: this.#fb.control({ value: '', disabled: true }, [
            Validators.maxLength(CustomerFieldsRestrictions.customerEmailMaxLength)
        ]),
        gender: this.#fb.control({ value: null as CustomerGender, disabled: true }),
        identification: this.#fb.control({ value: '', disabled: true }),
        member_id: this.#fb.control({ value: '', disabled: false }),
        language: this.#fb.control({ value: '', disabled: true }),
        name: this.#fb.control({ value: '', disabled: true }, [Validators.maxLength(CustomerFieldsRestrictions.customerNameMaxLength)]),
        title: this.#fb.control({ value: null as CustomerTitle, disabled: true }),
        phone: this.#fb.control({ value: '', disabled: true }, [
            Validators.maxLength(CustomerFieldsRestrictions.customerPhoneMaxLength),
            Validators.pattern(CustomerFieldsRestrictions.customerPhonePattern)
        ]),
        phone_2: this.#fb.control({ value: '', disabled: true }, [
            Validators.maxLength(CustomerFieldsRestrictions.customerPhoneMaxLength),
            Validators.pattern(CustomerFieldsRestrictions.customerPhonePattern)
        ]),
        address_2: this.#fb.control({ value: '', disabled: true }, Validators.maxLength(CustomerFieldsRestrictions.customerAddressMaxLength)),
        surname: this.#fb.control({ value: '', disabled: true }, [Validators.maxLength(CustomerFieldsRestrictions.customerSurnameMaxLength)]),
        int_phone: this.#fb.group({
            phone: this.#fb.control('', [
                Validators.maxLength(CustomerFieldsRestrictions.customerPhoneMaxLength),
                Validators.pattern(CustomerFieldsRestrictions.customerPhonePattern)
            ]),
            phone_prefix: this.#fb.control('')
        }, { validators: allRequiredIfOneHasValueInFormGroup() }),
        vendor: this.#fb.group({
            vendor_name: this.#fb.control(null as string),
            vendor_id: this.#fb.control(null as string)
        })
    });

    readonly isOperator$ = this.#auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR]);
    readonly countries$ = this.#countriesSrv.getCountries$();
    readonly provinces$ = this.#provincesBS.asObservable();
    readonly entities$ = this.#entitiesSrv.entityList.getData$()
        .pipe(
            tap(entities => {
                entities?.length && this.form.controls.entity.setValue(entities.find(entity => entity.id === this.#data.entityId) || null);
            })
        );

    readonly $languageCodes = toSignal(combineLatest([this.isOperator$, this.#entitiesSrv.getEntity$()])
        .pipe(
            filter(([isOperator, entity]) => isOperator !== null && Boolean(entity)),
            switchMap(([isOperator, entity]) => iif(
                () => isOperator,
                of(entity?.settings?.languages?.available),
                this.#auth.getLoggedUser$().pipe(filter(Boolean), map(user => user.entity?.settings?.languages?.available))
            ))
        ));

    readonly $entityExternalVendorConfig = toSignal(this.#entitiesSrv.getEntity$().pipe(filter(Boolean),
        map(entity => entity.settings?.external_integration?.auth_vendor)));

    readonly $entityAuthConfig = toSignal(this.#entitiesSrv.authConfig.get$(), { initialValue: null });

    readonly $vendorSelectOptions = computed(() => {
        const vendorIds = this.$entityExternalVendorConfig()?.vendor_id ?? [];
        if (!this.$entityAuthConfig()) return vendorIds;
        const selectedVendorId = this.$entityAuthConfig().authenticators
            ?.find(a => a.type === 'VENDOR' && a.customer_creation === 'ENABLED')?.id;
        return selectedVendorId && vendorIds.includes(selectedVendorId) ? [selectedVendorId] : vendorIds;
    });

    readonly $entityMemberIdGeneration = toSignal(this.#entitiesSrv.getEntity$().pipe(filter(Boolean),
        map(entity => entity.settings?.member_id_generation || 'DEFAULT')));

    readonly isLoadingOrSaving$ = booleanOrMerge([
        this.#countriesSrv.isCountriesLoading$(),
        this.#regionsSrv.isRegionsLoading$(),
        this.#customersSrv.customer.loading$(),
        this.#entitiesSrv.entityList.inProgress$(),
        this.#entitiesSrv.isEntityLoading$(),
        this.#customersSrv.customer.forms.adminCustomer.loading$()
    ]);

    readonly prefixList$ = this.#customersSrv.customer.forms.adminCustomer.get$()
        .pipe(filter(Boolean), startWith([] as CustomerFormField[]), map(form => form.flat())).pipe(
            map(adminFields =>
                adminFields.find(
                    adminCustomer => adminCustomer.name === 'int_phone')?.fields.find(
                        field => field.name === 'phone_prefix').values.map(
                            labelItem => ({
                                value: labelItem.value,
                                label: labelItem.label[this.$activeLang()],
                                unicode: labelItem.unicode
                            })))

        );

    readonly dateFormat = moment.localeData().longDateFormat(this.#formats.display.dateInput).toLowerCase();

    readonly customerGenderEnum = CustomerGender;
    readonly customerGenderValues = Object.values(CustomerGender).filter(gender => gender !== CustomerGender.none);
    readonly customerType = CustomerType;
    readonly customerTitle = customerTitle;

    readonly $adminCustomerFields = toSignal(this.#customersSrv.customer.forms.adminCustomer.get$()
        .pipe(filter(Boolean), startWith([] as CustomerFormField[]), map(form => form?.flat())));

    readonly $adminCustomerFieldNames = computed(() => this.$adminCustomerFields()?.map(field => field.name) || []);

    readonly $listPhonePrefix = computed(() => this.$adminCustomerFields().find(
        adminCustomer => adminCustomer.name === 'int_phone').fields.find(field => field.name === 'phone_prefix').values);

    $activeLang = toSignal(this.#translate.onLangChange.pipe(
        startWith({ lang: this.#translate.getCurrentLang() }),
        map(lang => lang?.lang)));

    constructor() {
        this.#dialogRef.addPanelClass(DialogSize.MEDIUM);
        this.#dialogRef.disableClose = false;
        this.#customersSrv.customer.forms.adminCustomer.load(this.#data.entityId);
        this.#countriesSrv.loadCountries();
        this.#regionsSrv.loadRegions();
        this.#entitiesSrv.authConfig.load(this.#data.entityId);
        this.#vendorChangeHandler();
        this.#setEntities();
        this.#initForm();
        this.#countryChangeHandler();
        this.#entityChangeHandler();

        effect(() => {
            this.#customersSrv.customer.forms.fieldsHandler(
                this.$adminCustomerFields(), this.$adminCustomerFieldNames(), this.form.controls, ['entity', 'type'], ['member_id']);
            if (this.$entityExternalVendorConfig()?.enabled) {
                this.form.controls.vendor.enable();
            } else {
                this.form.controls.vendor.disable();
            }
        });
    }

    ngOnDestroy(): void {
        this.#customersSrv.customer.forms.adminCustomer.clear();
        this.#entitiesSrv.authConfig.clear();
        this.#dialogRef.close();
    }

    compareById(option: Entity, option2: Entity): boolean {
        return option?.id === option2?.id;
    }

    close(): void {
        this.#dialogRef.close({});
    }

    createCustomer(): void {
        if (!this.form.valid) {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
        } else {
            const postCustomer = getObjectWithoutIgnoredValues<PostCustomer>(
                this.#postCustomerMapper(), [null, '']
            );

            this.#customersSrv.customer.create(postCustomer)
                .pipe(
                    tap((customerId: Id) =>
                        this.#dialogRef.close({ customerId: customerId.id })
                    ),
                    catchError(error => {
                        this.#setFormErrorByApiErrorResponse(error);
                        return of(null);
                    })
                ).subscribe();
        }
    }

    internationalPhoneCountry(): string {
        return this.$listPhonePrefix().find(
            // eslint-disable-next-line @typescript-eslint/dot-notation
            prefix => prefix.value === this.form.controls.int_phone.controls['phone_prefix'].value)?.unicode;
    }

    #postCustomerMapper(): PostCustomer {
        const fields = this.$adminCustomerFieldNames();
        const value = this.form.value;
        const entityVendorConfig = this.$entityExternalVendorConfig();
        const vmNewCustomer: VmNewCustomer = {
            name: value.name,
            surname: value.surname,
            email: value.email,
            language: value.language,
            entity_id: value.entity.id,
            member_id: value.member_id
        };

        if (fields.includes('address') || fields.includes('address_2') || fields.includes('city') || fields.includes('country')
            || fields.includes('postal_code') || fields.includes('province_code')) {
            vmNewCustomer.location = {};
        }

        if (fields.includes('title')) vmNewCustomer.title = value.title;
        if (fields.includes('birthday')) vmNewCustomer.birthday = value.birthday;
        if (fields.includes('gender')) vmNewCustomer.gender = value.gender;
        if (fields.includes('identification')) vmNewCustomer.id_card = value.identification;
        if (fields.includes('phone')) vmNewCustomer.phone = value.phone;
        if (fields.includes('phone_2')) vmNewCustomer.phone_2 = value.phone_2;
        if (fields.includes('int_phone')) {
            vmNewCustomer.int_phone = {
                phone: value.int_phone.phone,
                phone_prefix: value.int_phone.phone_prefix
            };
        }
        if (fields.includes('address')) vmNewCustomer.location.address = value.location.address;
        if (fields.includes('address_2')) vmNewCustomer.address_2 = value.address_2;
        if (fields.includes('city')) vmNewCustomer.location.city = value.location.city;
        if (fields.includes('country')) vmNewCustomer.location.country = value.location.country;
        if (fields.includes('postal_code')) vmNewCustomer.location.postal_code = value.location.postal_code;
        if (fields.includes('province_code')) vmNewCustomer.location.country_subdivision = value.location.province_code;
        if (entityVendorConfig?.enabled && entityVendorConfig?.vendor_id?.length
            && value.vendor?.vendor_name && value.vendor?.vendor_id) {
            vmNewCustomer.vendor = {
                name: value.vendor.vendor_name,
                id: value.vendor.vendor_id
            };
        }

        const { birthday, ...rest } = vmNewCustomer;
        const postCustomer: PostCustomer = { ...rest };

        if (birthday) {
            postCustomer.birthday = getFormattedDate(vmNewCustomer.birthday);
        }
        return postCustomer;
    }

    #setFormErrorByApiErrorResponse(apiErrorResponse: ApiErrorResponse): void {
        const { error: { code } } = apiErrorResponse;
        if (code === CustomerApiCodeError.customerAlreadyExists) {
            this.form.controls.email.markAsTouched();
            this.form.controls.email.setErrors({ customerAlreadyExists: true });
            setTimeout(() => scrollIntoFirstInvalidFieldOrErrorMsg(document));
        } else if (code === CustomerApiCodeError.customerDuplicatedMemberId) {
            this.form.controls.member_id.markAsTouched();
            this.form.controls.member_id.setErrors({ customerDuplicatedMemberId: true });
            setTimeout(() => scrollIntoFirstInvalidFieldOrErrorMsg(document));
        }
    }

    #initForm(): void {
        const today = moment();
        this.form.controls.birthday.setValidators([
            dateValidator(
                dateIsSameOrBefore, this.#translate.instant('CUSTOMERS.FORMS.ERRORS.BIRTHDAY_AFTER_TODAY').toLowerCase(),
                today
            )
        ]);
    }

    #setEntities(): void {
        this.#auth.getLoggedUser$()
            .pipe(
                filter(Boolean),
                takeUntilDestroyed()
            )
            .subscribe(user => {
                if (!user.roles.some(role => role.code === UserRoles.OPR_MGR)) {
                    this.form.controls.entity.patchValue(user.entity as Entity);
                    this.form.controls.entity.markAsDirty();
                    this.#entitiesSrv.loadEntity(user.entity.id);
                } else {
                    this.#entitiesSrv.entityList.load({
                        limit: 999,
                        sort: 'name:asc',
                        fields: [
                            EntitiesFilterFields.name,
                            EntitiesFilterFields.allowMembers,
                            EntitiesFilterFields.operatorId
                        ]
                    });
                }
            });

    }

    #countryChangeHandler(): void {
        combineLatest([
            this.#regionsSrv.getRegions$().pipe(first(value => !!value)),
            this.form.controls.location.controls.country.valueChanges.pipe(startWith(null as string))
        ]).pipe(
            map(([provinces]) => this.#filterProvincesByCountrySelected(provinces)),
            takeUntilDestroyed()
        ).subscribe(filteredProvinces => {
            const provinceCodeCtrl = this.form.controls.location.controls.province_code;
            if (filteredProvinces.length > 1) {
                provinceCodeCtrl.enable();
            } else {
                provinceCodeCtrl.disable();
                provinceCodeCtrl.setValue(null);
                provinceCodeCtrl.markAsPristine();
                provinceCodeCtrl.markAsUntouched();
            }
            this.#provincesBS.next(filteredProvinces);
        });
    }

    #filterProvincesByCountrySelected(provinces: Region[]): Region[] {
        return provinces.filter(province =>
            province.code.startsWith(this.form.controls.location.controls.country.value + '-'));
    }

    #entityChangeHandler(): void {
        this.form.controls.entity.valueChanges
            .pipe(
                filter(Boolean),
                takeUntilDestroyed())
            .subscribe((entity: Entity) => {
                this.#customersSrv.customer.forms.adminCustomer.load(entity.id);
                this.#entitiesSrv.loadEntity(entity.id);
                this.#entitiesSrv.authConfig.load(entity.id);
                if (this.form.controls.email.hasError('customerAlreadyExists')) {
                    this.form.controls.email.setErrors({ customerAlreadyExists: null });
                    this.form.controls.email.updateValueAndValidity();
                }
                if (this.form.controls.member_id.hasError('customerDuplicatedMemberId')) {
                    this.form.controls.member_id.setErrors({ customerDuplicatedMemberId: null });
                    this.form.controls.member_id.updateValueAndValidity();
                }
                this.form.controls.vendor.reset();
            });
    }

    #vendorChangeHandler(): void {
        this.form.controls.vendor.controls.vendor_name.valueChanges
            .pipe(takeUntilDestroyed())
            .subscribe(vendor => {
                if (vendor) {
                    this.form.controls.vendor.controls.vendor_id.enable();
                    this.form.controls.vendor.controls.vendor_id.addValidators(Validators.required);
                } else {
                    this.form.controls.vendor.controls.vendor_id.disable();
                    this.form.controls.vendor.controls.vendor_id.removeValidators(Validators.required);
                }
                this.form.controls.vendor.controls.vendor_id.updateValueAndValidity();
            });
    }

}
