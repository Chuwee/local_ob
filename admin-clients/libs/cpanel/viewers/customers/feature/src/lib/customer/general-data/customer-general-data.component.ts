/* eslint-disable @typescript-eslint/dot-notation */
import {
    FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg,
    TranslateFormErrorPipe
} from '@OneboxTM/feature-form-control-errors';
import { ENVIRONMENT_TOKEN } from '@OneboxTM/utils-environment';
import { ChannelsService, ChannelStatus, ChannelType } from '@admin-clients/cpanel/channels/data-access';
import { AuthenticationService, User, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { EntitiesService } from '@admin-clients/cpanel/organizations/entities/data-access';
import {
    CustomersService, CustomerContentImageField, PutCustomerContentImage, CustomerFieldsRestrictions,
    CustomerGender, Customer, CustomerApiCodeError, CustomerType, PutCustomer, CustomerProductClientIds,
    CustomerFormField, customerTitle, CustomerTitle, CustomerStatus
} from '@admin-clients/cpanel-viewers-customers-data-access';
import {
    CountriesService, Region, RegionsService, EntitiesBaseService
} from '@admin-clients/shared/common/data-access';
import {
    DialogSize,
    EmptyStateTinyComponent, EphemeralMessageService, googleLogoIcon,
    IconManagerService, ObMatDialogConfig, openDialog, SelectSearchComponent
} from '@admin-clients/shared/common/ui/components';
import { ApiErrorResponse } from '@admin-clients/shared/core/data-access';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { LocalDateTimePipe } from '@admin-clients/shared/utility/pipes';
import {
    allRequiredIfOneHasValueInFormGroup, booleanOrMerge,
    dateIsSameOrBefore, dateValidator
} from '@admin-clients/shared/utility/utils';
import { AsyncPipe, LowerCasePipe } from '@angular/common';
import {
    ChangeDetectionStrategy, Component, DestroyRef, OnDestroy, QueryList, ViewChild, ViewChildren,
    computed, effect, inject
} from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDateFormats, MAT_DATE_FORMATS } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatDialog } from '@angular/material/dialog';
import { MatDivider } from '@angular/material/divider';
import { MatExpansionModule, MatExpansionPanel } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSpinner } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import moment from 'moment-timezone';
import { BehaviorSubject, combineLatest, forkJoin, iif, Observable, of, throwError } from 'rxjs';
import { catchError, distinctUntilChanged, filter, first, map, startWith, switchMap, tap } from 'rxjs/operators';
import {
    getFormattedDate,
    getObjectWithChangedValues, getObjectWithoutIgnoredValues
} from '../../utils/formating-functions';
import { ProductClientIdValidator } from '../../validators/product-client-id-validator';
import {
    CustomerPrivateAreaDialogComponent
} from './customer-private-area-dialog/customer-private-area-dialog.component';
import { CustomerTypesHistoricDialogComponent } from './customer-types-historic-dialog/customer-types-historic-dialog.component';
import { CustomerGeneralDataSidebarComponent } from './sidebar/customer-general-data-sidebar.component';

const writingRoles = [UserRoles.CRM_MGR, UserRoles.OPR_MGR, UserRoles.ENT_MGR];
@Component({
    selector: 'app-customer-general-data',
    imports: [TranslatePipe, FormContainerComponent, AsyncPipe, MatExpansionModule, ReactiveFormsModule,
        MatFormFieldModule, MatInputModule, FormControlErrorsComponent, FlexLayoutModule, MatIconModule,
        MatSelectModule, SelectSearchComponent, MatDatepickerModule, MatButtonModule, LocalDateTimePipe,
        MatTooltipModule, MatCheckboxModule, MatTableModule, EmptyStateTinyComponent, CustomerGeneralDataSidebarComponent,
        MatSpinner, TranslateFormErrorPipe, LowerCasePipe, MatDivider, MatDivider
    ],
    templateUrl: './customer-general-data.component.html',
    styleUrls: ['./customer-general-data.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CustomerGeneralDataComponent implements OnDestroy {
    readonly #auth = inject(AuthenticationService);
    readonly #fb = inject(FormBuilder);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);
    readonly #entitiesSrv = inject(EntitiesBaseService);
    readonly #entitiesService = inject(EntitiesService);
    readonly #countriesSrv = inject(CountriesService);
    readonly #regionsSrv = inject(RegionsService);
    readonly #customersSrv = inject(CustomersService);
    readonly #translate = inject(TranslateService);
    readonly #formats: MatDateFormats = inject(MAT_DATE_FORMATS);
    readonly #iconManagerSrv = inject(IconManagerService);
    readonly #channelsSrv = inject(ChannelsService);
    readonly #matDialog = inject(MatDialog);
    readonly #env = inject(ENVIRONMENT_TOKEN);
    readonly #destroyRef = inject(DestroyRef);
    @ViewChildren(MatExpansionPanel)
    private _matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    @ViewChild('customerSidebarContent')
    private _customerSidebarContentComponent: CustomerGeneralDataSidebarComponent;

    readonly generalInfoForm = this.#fb.group({
        birthday: this.#fb.control({ value: '', disabled: true }),
        gender: null as CustomerGender,
        identification: this.#fb.control({ value: '', disabled: true }),
        language: this.#fb.control({ value: '', disabled: true }),
        name: this.#fb.control({ value: '', disabled: true }, [
            Validators.required,
            Validators.maxLength(CustomerFieldsRestrictions.customerNameMaxLength)
        ]),
        title: this.#fb.control({ value: null as CustomerTitle, disabled: true }),
        surname: this.#fb.control({ value: '', disabled: true }, [
            Validators.required,
            Validators.maxLength(CustomerFieldsRestrictions.customerSurnameMaxLength)
        ]),
        customer_types: this.#fb.control({ value: [] as number[], disabled: false }),
        member_data: this.#fb.group({
            id: this.#fb.control({ value: '', disabled: true }),
            allow_product_client_ids: false
        }),
        phone: this.#fb.control('', [
            Validators.maxLength(CustomerFieldsRestrictions.customerPhoneMaxLength),
            Validators.pattern(CustomerFieldsRestrictions.customerPhonePattern)
        ]),
        phone_2: this.#fb.control({ value: '', disabled: true }, [
            Validators.maxLength(CustomerFieldsRestrictions.customerPhoneMaxLength),
            Validators.pattern(CustomerFieldsRestrictions.customerPhonePattern)
        ]),
        address_2: this.#fb.control({ value: '', disabled: true },
            Validators.maxLength(CustomerFieldsRestrictions.customerAddressMaxLength)),
        email: this.#fb.control('', [
            Validators.email,
            Validators.required,
            Validators.maxLength(CustomerFieldsRestrictions.customerEmailMaxLength)
        ]),
        int_phone: this.#fb.group({
            phone: this.#fb.control('', [
                Validators.maxLength(CustomerFieldsRestrictions.customerPhoneMaxLength),
                Validators.pattern(CustomerFieldsRestrictions.customerPhonePattern)
            ]),
            phone_prefix: this.#fb.control('')
        }, { validators: allRequiredIfOneHasValueInFormGroup() })
    });

    readonly addressInfoForm = this.#fb.group({
        address: this.#fb.control({ value: '', disabled: true }, Validators.maxLength(CustomerFieldsRestrictions.customerAddressMaxLength)),
        city: this.#fb.control({ value: '', disabled: true }, Validators.maxLength(CustomerFieldsRestrictions.customerCityMaxLength)),
        country: this.#fb.control({ value: '', disabled: true }),
        postal_code: this.#fb.control({ value: '', disabled: true },
            Validators.maxLength(CustomerFieldsRestrictions.customerPostalCodeMaxLength)),
        province_code: this.#fb.control({ value: '', disabled: true })
    });

    readonly form = this.#fb.group({
        generalInfoForm: this.generalInfoForm,
        addressInfoForm: this.addressInfoForm
    });

    #user: User;
    readonly #provincesBS = new BehaviorSubject<Region[]>(null);
    readonly #entityAllowMembersBS = new BehaviorSubject<boolean>(null);

    readonly dateFormat = moment.localeData().longDateFormat(this.#formats.display.dateInput).toLowerCase();

    readonly $entityCustomerTypes = toSignal(this.#entitiesService.entityCustomerTypes.get$());

    readonly $entityChannels = toSignal(this.#channelsSrv.channelsList.getList$());
    readonly $customerTypesHistoric = toSignal(this.#customersSrv.customerTypesHistoric.get$());

    readonly $entityCustomerTypesAutomatic = computed(() => this.$entityCustomerTypes()
        ?.filter(ct => ct.assignation_type === 'AUTOMATIC'));

    readonly $entityCustomerTypesManual = computed(() => this.$entityCustomerTypes()
        ?.filter(ct => ct.assignation_type === 'MANUAL'));

    readonly $entityMemberIdGeneration = toSignal(this.#entitiesSrv.getEntity$().pipe(filter(Boolean),
        map(entity => entity.settings?.member_id_generation || 'DEFAULT')));

    readonly $entityCustomerTypesNames = computed(() => this.$customer().customer_types
        ?.filter(ct => this.$entityCustomerTypes()?.some(ect => ect.id === ct.id)).map(ct => ct.name).join(', ') || '');

    readonly $customersDomainSettings = toSignal(this.#entitiesSrv.getEntity$()
        .pipe(map(entity => entity?.settings?.customers_domain_settings)));

    readonly $hasRolesToImpersonate = toSignal(this.#auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.ENT_MGR, UserRoles.CRM_MGR])
        .pipe(first()));

    readonly $isLockedCustomer = computed(() => this.$customer()?.status === CustomerStatus.locked);
    readonly $isManaged = computed(() => this.$customer()?.is_managed);
    readonly $canImpersonate = computed(() => this.$hasRolesToImpersonate() && !this.$isLockedCustomer() && !this.$isManaged());

    readonly #isOperator$ = this.#auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR]);
    readonly canLoggedUserWrite$ = this.#auth.hasLoggedUserSomeRoles$(writingRoles);
    readonly $customer = toSignal(this.#customersSrv.customer.get$().pipe(filter(Boolean)));
    readonly countries$ = this.#countriesSrv.getCountries$();
    readonly provinces$ = this.#provincesBS.asObservable();
    readonly entityAllowMembers$ = this.#entityAllowMembersBS.asObservable();

    readonly languageCodes$ = this.#isOperator$
        .pipe(
            filter(isOperator => isOperator !== null),
            switchMap(isOperator => iif(
                () => isOperator,
                this.#entitiesSrv.getEntity$()
                    .pipe(
                        first(Boolean),
                        map(entity => entity.settings?.languages?.available)
                    ),
                of(this.#user.entity.settings?.languages?.available)
            ))
        );

    readonly prefixList$ = this.#customersSrv.customer.forms.adminCustomer.get$()
        .pipe(filter(Boolean), startWith([] as CustomerFormField[]), map(adminCustomerForm => adminCustomerForm.flat())).pipe(
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

    readonly customerCountryReadOnly$ = this.canLoggedUserWrite$
        .pipe(
            filter(Boolean),
            switchMap(canWrite => {
                if (!canWrite) {
                    return this.#countriesSrv.getCountries$()
                        .pipe(map(countries =>
                            countries?.find(country => country?.code === this.$customer().location.country.code)?.name));
                }
                return of();
            })
        );

    readonly customerProvinceReadOnly$ = this.canLoggedUserWrite$
        .pipe(
            filter(Boolean),
            switchMap(canWrite => {
                if (!canWrite) {
                    return this.#provincesBS.asObservable()
                        .pipe(map(provinces =>
                            provinces?.find(province => province?.code === this.$customer().location.country_subdivision.code)?.name));
                }
                return of();
            })
        );

    readonly $customerAge = computed(() => {
        if (this.$customer().birthday) {
            const birthDate = moment(this.$customer().birthday);
            const today = moment();
            return today.diff(birthDate, 'years');
        }
        return null;
    });

    readonly isLoadingOrSaving$ = booleanOrMerge([
        this.#customersSrv.customer.loading$(),
        this.#customersSrv.customer.image.loading$(),
        this.#customersSrv.customer.forms.adminCustomer.loading$(),
        this.#countriesSrv.isCountriesLoading$(),
        this.#regionsSrv.isRegionsLoading$(),
        this.#entitiesSrv.isEntityLoading$(),
        this.#entitiesService.entityCustomerTypes.inProgress$(),
        this.#customersSrv.customerTypesHistoric.loading$()
    ]);

    readonly isSaveCancelDisabled$ = combineLatest([
        this.isLoadingOrSaving$,
        this.canLoggedUserWrite$,
        this.form.valueChanges.pipe(startWith(null as unknown))
    ]).pipe(
        map(([isLoading, canLoggedUserWrite]) =>
            isLoading || !canLoggedUserWrite || this.form.pristine
        ),
        distinctUntilChanged()
    );

    readonly $adminCustomerFields = toSignal(this.#customersSrv.customer.forms.adminCustomer.get$()
        .pipe(filter(Boolean), startWith([] as CustomerFormField[]), map(adminCustomerForm => adminCustomerForm.flat())));

    $activeLang = toSignal(this.#translate.onLangChange.pipe(
        startWith({ lang: this.#translate.getCurrentLang() }),
        map(lang => lang?.lang)));

    readonly $listPhonePrefix = computed(() => this.$adminCustomerFields().find(
        adminCustomer => adminCustomer.name === 'int_phone').fields.find(field => field.name === 'phone_prefix').values);

    readonly $adminCustomerFieldNames = computed(() => this.$adminCustomerFields()?.map(field => field.name));
    readonly $uneditableFields = computed(() => this.$adminCustomerFieldNames()?.filter(fieldName =>
        this.$adminCustomerFields().find(field => field.name === fieldName && field.uneditable)));

    readonly customerTitle = customerTitle;
    readonly customerGenderEnum = CustomerGender;
    readonly customerGenderValues = Object.values(CustomerGender).filter(gender => gender !== CustomerGender.none);
    readonly dateTimeFormats = DateTimeFormats;
    membersIdsList: CustomerProductClientIds[];

    get memberDataFormGroup(): UntypedFormGroup {
        return this.generalInfoForm.get('member_data') as UntypedFormGroup;
    }

    get getMemberIdControl(): UntypedFormControl {
        return this.memberDataFormGroup.get('id') as UntypedFormControl;
    }

    get provinceControl(): UntypedFormControl {
        return this.addressInfoForm.get('province_code') as UntypedFormControl;
    }

    get countryControl(): FormControl {
        return this.addressInfoForm.get('country') as UntypedFormControl;
    }

    constructor() {
        this.#entitiesSrv.getEntity$()
            .pipe(
                filter(Boolean),
                takeUntilDestroyed()
            )
            .subscribe(entity => {
                this.#customersSrv.customer.forms.adminCustomer.load(entity.id);
                if (this.$canImpersonate()) {
                    this.#channelsSrv.channelsList.load({
                        limit: 999,
                        offset: 0,
                        sort: 'name:asc',
                        entityId: entity.id,
                        status: [ChannelStatus.active],
                        type: ChannelType.web
                    });
                }
            });

        this.#countriesSrv.loadCountries();
        this.#regionsSrv.loadRegions();
        this.#loadCustomerTypesHistoric();
        this.#setUser();
        this.#initForm();
        this.#setCountryChangeHandler();
        this.#setCustomerChangeHandler();
        this.#setEntityAllowMembers();

        const ctrls = { ...this.generalInfoForm.controls, ...this.addressInfoForm.controls };
        effect(() =>
            this.#customersSrv.customer.forms.fieldsHandler(
                this.$adminCustomerFields(), this.$adminCustomerFieldNames(), ctrls,
                [], ['customer_types', 'id', 'allow_product_client_ids']
            ));

        this.#iconManagerSrv.addIconDefinition(googleLogoIcon);
    }

    ngOnDestroy(): void {
        this.#customersSrv.customerForm.clear();
        this.#customersSrv.customerTypesHistoric.clear();
    }

    cancel(): void {
        this.#customersSrv.customer.load(this.$customer().id, this.$customer().entity?.id?.toString());
        this.#loadCustomerTypesHistoric();
        if (this._customerSidebarContentComponent.customerSidebarContentForm.dirty) {
            this._customerSidebarContentComponent.cancel();
        }
        this.form.markAsPristine();
        this.form.markAsUntouched();
    }

    save(): void {
        this.save$().subscribe();
    }

    internationalPhoneCountry(): string {
        return this.$listPhonePrefix().find(
            prefix => prefix.value === this.generalInfoForm.controls.int_phone.controls['phone_prefix'].value)?.unicode;
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            const generalInfoValue = this.generalInfoForm.value;
            const fields = this.$adminCustomerFieldNames();
            const obs$: Observable<void>[] = [];
            if (generalInfoValue.birthday) {
                generalInfoValue.birthday = getFormattedDate(this.generalInfoForm.value.birthday);
            }
            const updatedCustomer = this.#buildDynamicPutCustomer(fields);

            let putCustomer: Partial<PutCustomer> = getObjectWithoutIgnoredValues<Partial<PutCustomer>>(
                updatedCustomer,
                [undefined]
            );
            putCustomer = getObjectWithChangedValues<Partial<PutCustomer>>(
                putCustomer,
                new Map([
                    ['language', { oldValue: null, newValue: '' }],
                    ['country', { oldValue: null, newValue: '' }],
                    ['province_code', { oldValue: null, newValue: '' }]
                ])
            );
            const { ...memberDataFormGroup } = this.memberDataFormGroup.value;
            if (putCustomer.member_data && this.memberDataFormGroup.value.product_client_ids) {
                const productClientIds = Object.entries(memberDataFormGroup.product_client_ids)
                    .map(([productId, clientId]) => ({
                        product_id: parseInt(productId),
                        client_id: clientId as string
                    }));
                putCustomer.member_data.product_client_ids = productClientIds;
            }

            if (this._customerSidebarContentComponent.customerSidebarContentForm.dirty) {
                obs$.push(...this._customerSidebarContentComponent.save(this.#getImageField.bind(this)));
            }

            obs$.push(this.#customersSrv.customer.save(
                this.$customer().id, putCustomer as PutCustomer, this.$customer().entity?.id?.toString()));

            return forkJoin(obs$).pipe(
                tap(() => {
                    this.#ephemeralMessageService.showSuccess({ msgKey: 'CUSTOMER.UPDATE_SUCCESS' });
                    this.form.markAsPristine();
                    this.form.markAsUntouched();
                    this.#customersSrv.customer.load(this.$customer().id, this.$customer().entity?.id?.toString());
                    this.#loadCustomerTypesHistoric();
                }),
                catchError(error => {
                    this.#setFormErrorByApiErrorResponse(error);
                    return [];
                })
            );
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanelQueryList);
            return throwError(() => 'invalid form');
        }
    }

    goToPrivateArea(): void {
        const customersDomainSettings = this.$customersDomainSettings();
        const domain = customersDomainSettings?.enabled ?
            customersDomainSettings.domains?.find(domain => domain.default)?.domain :
            this.#getDefaultDomain(this.#env.env);

        const entityChannels = this.$entityChannels();
        this.#customersSrv.customerToken.impersonate(this.$customer()?.id)
            .pipe(
                switchMap(token => {
                    if (entityChannels?.length > 1) {
                        return openDialog(this.#matDialog, CustomerPrivateAreaDialogComponent)
                            .afterClosed()
                            .pipe(map(channel => ({ token, channelUrl: channel?.url.split('/').pop() })));
                    } else {
                        return of({ token, channelUrl: entityChannels?.[0].url.split('/').pop() });
                    }
                }))
            .subscribe(({ token, channelUrl }) => {
                if (channelUrl) {
                    window.open(`https://${domain}/${channelUrl}/login?token=${token}`, '_blank');
                }
            });
    }

    #getDefaultDomain(env: string): string {
        switch (env) {
            case 'pro': return 'account.oneboxtds.com';
            case 'pre': return 'account.oneboxtds.net';
            default: return 'account01.oneboxtds.net';
        }
    }

    openCustomerTypesHistoricDialog(): void {
        const data = { customer: this.$customer(), customerTypesHistoric: this.$customerTypesHistoric() };
        this.#matDialog.open(CustomerTypesHistoricDialogComponent, new ObMatDialogConfig(
            data, undefined, DialogSize.LATERAL
        )).beforeClosed();
    }

    #buildDynamicPutCustomer(fields: string[]): PutCustomer {
        const generalInfoValue = this.generalInfoForm.value;
        const addressInfoValue = this.addressInfoForm.value;

        const updatedCustomer: PutCustomer = {
            name: generalInfoValue.name,
            surname: generalInfoValue.surname,
            email: generalInfoValue.email,
            customer_types: generalInfoValue.customer_types,
            member_data: { ...generalInfoValue.member_data }
        };

        const locationFields = ['country', 'province_code', 'city', 'postal_code', 'address', 'address_2'];
        if (fields.some(field => locationFields.includes(field))) {
            updatedCustomer.location = {};
        }
        if (fields.includes('title')) updatedCustomer.title = generalInfoValue.title;
        if (fields.includes('birthday')) updatedCustomer.birthday = generalInfoValue.birthday;
        if (fields.includes('phone')) updatedCustomer.phone = generalInfoValue.phone;
        if (fields.includes('phone_2')) updatedCustomer.phone_2 = generalInfoValue.phone_2;
        if (fields.includes('address_2')) updatedCustomer.address_2 = generalInfoValue.address_2;
        if (fields.includes('int_phone') && !this.generalInfoForm.controls.int_phone.disabled) {
            updatedCustomer.int_phone = {
                phone: generalInfoValue.int_phone.phone,
                phone_prefix: generalInfoValue.int_phone.phone_prefix ?? ''
            };
        }
        if (fields.includes('country')) updatedCustomer.location.country = addressInfoValue.country;
        if (fields.includes('province_code')) updatedCustomer.location.country_subdivision = addressInfoValue.province_code;
        if (fields.includes('city')) updatedCustomer.location.city = addressInfoValue.city;
        if (fields.includes('postal_code')) updatedCustomer.location.postal_code = addressInfoValue.postal_code;
        if (fields.includes('address')) updatedCustomer.location.address = addressInfoValue.address;
        if (fields.includes('identification')) updatedCustomer.id_card = generalInfoValue.identification;
        if (fields.includes('gender')) updatedCustomer.gender = generalInfoValue.gender;
        if (fields.includes('language')) updatedCustomer.language = generalInfoValue.language;

        return updatedCustomer;
    }

    #getImageField(contentForm: UntypedFormGroup, imageField: CustomerContentImageField): PutCustomerContentImage {
        let image: PutCustomerContentImage;
        const field = contentForm.get(imageField.formField);
        const type = imageField.type;
        if (field.dirty) {
            const imageValue = field.value;
            if (imageValue?.data) {
                image = { type, image: imageValue?.data };
            } else {
                image = {};
            }
        }
        return image;
    }

    #setFormErrorByApiErrorResponse(apiErrorResponse: ApiErrorResponse): void {
        const { error: { code } } = apiErrorResponse;
        if (code === CustomerApiCodeError.customerDuplicatedEmail) {
            this.generalInfoForm.markAsTouched();
            this.generalInfoForm.get('email').setErrors({ customerDuplicatedEmail: true });
            setTimeout(() => scrollIntoFirstInvalidFieldOrErrorMsg(document));
        } else if (code === CustomerApiCodeError.customerDuplicatedMemberId) {
            this.getMemberIdControl.markAsTouched();
            this.getMemberIdControl.setErrors({ customerDuplicatedMemberId: true });
            setTimeout(() => scrollIntoFirstInvalidFieldOrErrorMsg(document));
        }
    }

    #setUser(): void {
        this.#auth.getLoggedUser$()
            .pipe(first(user => user !== null))
            .subscribe(user => {
                this.#user = user;
            });

    }

    #loadEntityCustomTypes(entityId: number): void {
        this.#entitiesService.entityCustomerTypes.load(entityId);
    }

    #loadCustomerTypesHistoric(): void {
        this.#customersSrv.customer.get$()
            .pipe(first(Boolean), takeUntilDestroyed(this.#destroyRef))
            .subscribe(customer => {
                this.#customersSrv.customerTypesHistoric.load(customer.id, customer.entity?.id?.toString());
            });
    }

    #initForm(): void {
        const today = moment();
        this.form.get('generalInfoForm').get('birthday').addValidators([
            dateValidator(
                dateIsSameOrBefore, this.#translate.instant('CUSTOMERS.FORMS.ERRORS.BIRTHDAY_AFTER_TODAY').toLowerCase(),
                today
            )
        ]);

        this.#updateMembersInfo(this.$customer());

        this.#customersSrv.customerForm.set(this.form);

        this.memberDataFormGroup.get('allow_product_client_ids').valueChanges
            .pipe(takeUntilDestroyed())
            .subscribe(allowProductClientIds => {
                const productClientForm = this.memberDataFormGroup.controls['product_client_ids'] as FormGroup;
                if (!allowProductClientIds) {
                    productClientForm.disable();
                } else {
                    productClientForm.enable();
                }
            });
    }

    #setEntityAllowMembers(): void {
        this.#isOperator$
            .pipe(filter(isOperator => isOperator !== null), takeUntilDestroyed())
            .subscribe(isOperator => {
                if (isOperator) {
                    this.#entitiesSrv.getEntity$()
                        .pipe(first(Boolean))
                        .subscribe(entity => {
                            this.#entityAllowMembersBS.next(entity.settings?.allow_members);
                        });
                } else {
                    this.#entityAllowMembersBS.next(this.#user.entity.settings?.allow_members);
                }
            });

    }

    #setCountryChangeHandler(): void {
        combineLatest([
            this.#regionsSrv.getRegions$().pipe(first(value => !!value)),
            this.countryControl.valueChanges.pipe(startWith(null as string))
        ]).pipe(
            map(([provinces]) => this.#filterProvincesByCountrySelected(provinces)),
            takeUntilDestroyed()
        ).subscribe(filteredProvinces => {
            if (filteredProvinces.length > 1) {
                this.provinceControl.enable();
            } else {
                this.provinceControl.disable();
                this.provinceControl.setValue(null);
                this.provinceControl.markAsPristine();
                this.provinceControl.markAsUntouched();
            }
            this.#provincesBS.next(filteredProvinces);
        });
    }

    #filterProvincesByCountrySelected(provinces: Region[]): Region[] {
        return provinces.filter(province =>
            province.code.startsWith(this.countryControl.value + '-'));
    }

    #setCustomerChangeHandler(): void {
        this.#customersSrv.customer.get$()
            .pipe(takeUntilDestroyed())
            .subscribe(customer => {
                this.#loadEntityCustomTypes(customer.entity?.id);
                this.#updateGeneralInfoForm(customer);
                this.#updateAddressInfoForm(customer);
                this.#updateMembersInfo(customer);
                this.form.markAsPristine();
                this.form.markAsUntouched();

                if (customer.type === CustomerType.member) {
                    this.getMemberIdControl.enable();
                } else {
                    this.getMemberIdControl.disable();
                    this.getMemberIdControl.setValue(null);
                }
            });
    }

    #updateGeneralInfoForm(customer: Customer): void {
        this.generalInfoForm.patchValue({
            birthday: customer.birthday,
            gender: customer.gender,
            identification: customer.id_card,
            language: customer.language,
            title: customer.title,
            name: customer.name,
            surname: customer.surname,
            customer_types: customer.customer_types?.map(customerType => customerType.id) || [],
            member_data: {
                id: customer.member_data?.id,
                allow_product_client_ids: customer.member_data?.allow_product_client_ids
            },
            email: customer.email,
            phone: customer.phone,
            phone_2: customer.phone_2,
            address_2: customer?.address_2,
            int_phone: {
                phone: customer.int_phone?.phone,
                phone_prefix: customer.int_phone?.phone_prefix
            }
        }, { emitEvent: false });
    }

    #updateAddressInfoForm(customer: Customer): void {
        this.addressInfoForm.patchValue({
            address: customer.location?.address,
            city: customer.location?.city,
            country: customer.location?.country?.code,
            postal_code: customer.location?.postal_code,
            province_code: customer.location?.country_subdivision?.code
        });
    }

    #updateMembersInfo(customer: Customer): void {
        const memberDataGroup = this.generalInfoForm.controls['member_data'] as FormGroup;
        const productClientForm = memberDataGroup.controls['product_client_ids'] as FormGroup;
        const seasonTicketFields: Record<string, unknown> = {};

        this.membersIdsList = customer.member_data.product_client_ids;
        this.membersIdsList?.forEach(seasonTicket => {
            const productClientIdValidator = ProductClientIdValidator.createValidator(
                this.#customersSrv,
                this.$customer().id,
                seasonTicket.event.id,
                String(this.$customer().entity?.id)
            );
            seasonTicketFields[String(seasonTicket.event.id)] = seasonTicket.client_id;
            if (productClientForm) {
                memberDataGroup.controls['product_client_ids'].get(String(seasonTicket.event.id))
                    ?.setValidators([Validators.required]);
                memberDataGroup.controls['product_client_ids'].get(String(seasonTicket.event.id))
                    ?.setAsyncValidators([productClientIdValidator]);
            }
        });

        if (productClientForm) {
            productClientForm.patchValue(seasonTicketFields);
        } else {
            memberDataGroup.addControl('product_client_ids', this.#fb.group(seasonTicketFields));
        }
        if (this.$customer().type === CustomerType.basic) {
            memberDataGroup.get('allow_product_client_ids').disable();
        } else {
            memberDataGroup.get('allow_product_client_ids').enable();
        }
    }

    isGoogleEmail(customer: Customer): boolean {
        return customer.auth_providers?.some(auth => auth.id === 'GOOGLE');
    }
}
