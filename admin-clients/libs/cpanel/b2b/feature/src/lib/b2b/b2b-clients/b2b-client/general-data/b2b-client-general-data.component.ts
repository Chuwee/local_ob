import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { B2bClient, B2bClientCategoryType, B2bService, PutB2bClient } from '@admin-clients/cpanel/b2b/data-access';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import {
    Country, Region, CountriesService, RegionsService
} from '@admin-clients/shared/common/data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, QueryList, ViewChildren } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { combineLatest, Observable, Subject, throwError } from 'rxjs';
import { filter, map, startWith, takeUntil, tap } from 'rxjs/operators';
import { B2bClientTaxIdValidator } from '../../../validators/b2b-client-tax-id-validator';
import { b2bClientFieldsRestrictions } from '../../models/b2b-client-fields-restrictions';

@Component({
    selector: 'app-b2b-client-general-data',
    templateUrl: './b2b-client-general-data.component.html',
    styleUrls: ['./b2b-client-general-data.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class B2bClientGeneralDataComponent implements OnInit, OnDestroy, WritingComponent {
    private _onDestroy = new Subject<void>();
    private _b2bClientId: number;
    private _clientEntityId: number;
    private _isOperator: boolean;
    private _asyncValidatorIsInited = false;
    @ViewChildren(MatExpansionPanel)
    private _matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    form: FormGroup;
    basicDataGroup: FormGroup;
    contactDataGroup: FormGroup;
    b2bClientIdClient$: Observable<number>;
    b2bClientIataCode$: Observable<string>;
    countries$: Observable<Country[]>;
    regions$: Observable<Region[]>;
    filteredRegions$: Observable<Region[]>;
    isInProgress$: Observable<boolean>;

    readonly categoryTypes = B2bClientCategoryType;

    constructor(
        private _fb: FormBuilder,
        private _b2bSrv: B2bService,
        private _countriesService: CountriesService,
        private _regionsService: RegionsService,
        private _auth: AuthenticationService,
        private _ephemeralMessage: EphemeralMessageService
    ) { }

    ngOnInit(): void {
        this.initForm();

        combineLatest([
            this._auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR]),
            this._b2bSrv.getB2bClient$().pipe(filter(b2bClient => !!b2bClient))
        ])
            .pipe(takeUntil(this._onDestroy))
            .subscribe(([isOperator, b2bClient]) => {
                //Check if async validator is already inited
                if (!this._asyncValidatorIsInited) {
                    const taxIdAsyncValidator = isOperator
                        ? [B2bClientTaxIdValidator.createValidator(this._b2bSrv, b2bClient.entity?.id, b2bClient.id)]
                        : [B2bClientTaxIdValidator.createValidator(this._b2bSrv, null, b2bClient.id)];
                    this.basicDataGroup.get('taxId').addAsyncValidators(taxIdAsyncValidator);
                    this._asyncValidatorIsInited = true;
                }

                this._b2bClientId = b2bClient.id;
                this._isOperator = isOperator;
                this._clientEntityId = b2bClient.entity?.id;

                this.updateFormValues(b2bClient);

                this._countriesService.loadCountries({ sort: true });
                this._regionsService.loadRegions();
            });

        this.initComponentModels();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._countriesService.clearCountries();
        this._regionsService.clearRegions();
    }

    cancel(): void {
        this.reloadModels();
    }

    save(): void {
        this.save$().subscribe(() => this.reloadModels());
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            const basicDataGroup = this.basicDataGroup.value;
            const contactDataGroup = this.contactDataGroup.value;
            const request: PutB2bClient = {
                entity_id: this._isOperator ? this._clientEntityId : undefined,
                name: basicDataGroup.name,
                category_type: basicDataGroup.categoryType,
                tax_id: basicDataGroup.taxId,
                business_name: basicDataGroup.businessName,
                description: basicDataGroup.description,
                keywords: basicDataGroup.keywords ? this.buildKeywordsArray(basicDataGroup.keywords) : [],
                country: { code: contactDataGroup.country },
                country_subdivision: contactDataGroup.countrySubdivision
                    ? { code: contactDataGroup.countrySubdivision }
                    : { code: 'NOT_DEF' },
                contact_data: {
                    contact_person: contactDataGroup.contactPerson,
                    address: contactDataGroup.address,
                    email: contactDataGroup.email,
                    phone: contactDataGroup.phone
                }
            };
            return this._b2bSrv.saveB2bClient(this._b2bClientId, request)
                .pipe(tap(() => this._ephemeralMessage.showSaveSuccess()));
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanelQueryList);
            return throwError(() => 'invalid form');
        }
    }

    private initForm(): void {
        this.basicDataGroup = this._fb.group({
            taxId: [null, [Validators.required, Validators.maxLength(b2bClientFieldsRestrictions.b2bClientTaxIdMaxLength)]],
            categoryType: [null, Validators.required],
            name: [null, [Validators.required, Validators.maxLength(b2bClientFieldsRestrictions.b2bClientNameMaxLength)]],
            businessName: [null, [Validators.required, Validators.maxLength(b2bClientFieldsRestrictions.b2bClientBusinessNameMaxLength)]],
            iataCode: [null],
            description: [null, Validators.maxLength(b2bClientFieldsRestrictions.b2bClientDescriptionMaxLength)],
            keywords: [null, Validators.maxLength(b2bClientFieldsRestrictions.b2bClientKeywordsMaxLength)]
        });
        this.contactDataGroup = this._fb.group({
            contactPerson: [null, Validators.required],
            email: [null, [Validators.required, Validators.email]],
            phone: [null, [Validators.required, Validators.pattern(b2bClientFieldsRestrictions.b2bClientPhonePattern)]],
            address: null,
            country: [null, Validators.required],
            countrySubdivision: [{ value: null, disabled: true }, Validators.required]
        });
        this.form = this._fb.group({
            basicDataGroup: this.basicDataGroup,
            contactDataGroup: this.contactDataGroup
        });
    }

    private updateFormValues(b2bClient: B2bClient): void {
        this.form.patchValue({
            basicDataGroup: {
                taxId: b2bClient.tax_id,
                categoryType: b2bClient.category_type,
                name: b2bClient.name,
                businessName: b2bClient.business_name,
                iataCode: b2bClient.iata_code,
                description: b2bClient.description,
                keywords: String(b2bClient.keywords)
            },
            contactDataGroup: {
                contactPerson: b2bClient.contact_data?.contact_person,
                email: b2bClient.contact_data?.email,
                phone: b2bClient.contact_data?.phone,
                address: b2bClient.contact_data?.address,
                country: b2bClient.country?.code,
                countrySubdivision: b2bClient.country_subdivision?.code
            }
        });
        this.form.markAsPristine();
    }

    private initComponentModels(): void {
        this.b2bClientIdClient$ = this._b2bSrv.getB2bClient$()
            .pipe(
                filter(b2bClient => !!b2bClient),
                map(b2bClient => b2bClient.client_id)
            );

        this.b2bClientIataCode$ = this._b2bSrv.getB2bClient$()
            .pipe(
                filter(b2bClient => !!b2bClient),
                map(b2bClient => b2bClient.iata_code)
            );

        this.countries$ = this._countriesService.getCountries$().pipe(filter(countries => !!countries));

        this.regions$ = this._regionsService.getRegions$();

        this.filteredRegions$ = combineLatest([
            this.contactDataGroup.get('country').valueChanges
                .pipe(startWith(this.contactDataGroup.get('country').value as string)),
            this.regions$
        ]).pipe(
            filter(([_, regions]) => !!regions),
            map(([countryCode, regions]) =>
                regions.filter(region => region.code.startsWith(countryCode + '-'))
            ),
            tap(regions => {
                const countrySubdivisionField = this.contactDataGroup.get('countrySubdivision');
                if (regions.length === 1) {
                    countrySubdivisionField.enable();
                    this.contactDataGroup.get('countrySubdivision').setValue(regions[0].code);
                } else if (regions.length > 0) {
                    countrySubdivisionField.enable();
                } else {
                    countrySubdivisionField.disable();
                    countrySubdivisionField.setValue(null);
                }
            })
        );

        this.isInProgress$ = booleanOrMerge([
            this._countriesService.isCountriesLoading$(),
            this._regionsService.isRegionsLoading$(),
            this._b2bSrv.isB2bClientInProgress$()
        ]);
    }

    private reloadModels(): void {
        if (this._isOperator) {
            this._b2bSrv.loadB2bClient(this._b2bClientId, this._clientEntityId);
        } else {
            this._b2bSrv.loadB2bClient(this._b2bClientId);
        }
        this.form.markAsPristine();
    }

    private buildKeywordsArray(keywords: string): string[] {
        const keywordsArr = keywords.split(',');
        return keywordsArr;
    }

}
