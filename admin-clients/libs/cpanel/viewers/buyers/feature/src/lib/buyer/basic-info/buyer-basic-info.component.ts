import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { DocumentTypesService } from '@admin-clients/cpanel/platform/data-access';
import { BuyersService, BuyerGender, BuyerType, Buyer } from '@admin-clients/cpanel-viewers-buyers-data-access';
import { Country, CountriesService, Language, LanguagesService, Region, RegionsService } from '@admin-clients/shared/common/data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, ElementRef, Inject, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, ValidatorFn } from '@angular/forms';
import { MAT_DATE_FORMATS, MatDateFormats } from '@angular/material/core';
import moment from 'moment-timezone';
import { combineLatest, Observable, Subject, throwError } from 'rxjs';
import { filter, map, startWith, take, takeUntil, tap } from 'rxjs/operators';
import { buyerBasicInfoFormStruct as bff, BuyerBasicInfoFormValue } from './buyer-basic-info-form-def';

@Component({
    selector: 'app-buyer-basic-info',
    templateUrl: './buyer-basic-info.component.html',
    styleUrls: ['./buyer-basic-info.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class BuyerBasicInfoComponent implements OnInit, OnDestroy, WritingComponent {
    private _onDestroy = new Subject<void>();
    private _buyerId: string;
    readonly bff = bff;
    readonly buyerType = BuyerType;
    readonly buyerGenders = Object.values(BuyerGender);
    readonly dateFormat = moment.localeData().longDateFormat(this._formats.display.dateInput).toLowerCase();
    docTypes$: Observable<string[]>;
    form: UntypedFormGroup;
    reqInProgress$: Observable<boolean>;
    error$: Observable<HttpErrorResponse>;
    languages$: Observable<Language[]>;
    countries$: Observable<Country[]>;
    countrySubdivisions$: Observable<Region[]>;

    constructor(
        private _elRef: ElementRef,
        private _fb: UntypedFormBuilder,
        @Inject(MAT_DATE_FORMATS) private _formats: MatDateFormats,
        private _ephemeralMessageService: EphemeralMessageService,
        private _buyersSrv: BuyersService,
        private _languageSrv: LanguagesService,
        private _countriesSrv: CountriesService,
        private _regionsSrv: RegionsService,
        private _authSrv: AuthenticationService,
        private _docTypesSrv: DocumentTypesService
    ) { }

    ngOnInit(): void {
        this.reqInProgress$ = booleanOrMerge([
            this._buyersSrv.isBuyerLoading$(),
            this._buyersSrv.isUpdatingBuyer$()
        ]);
        this.docTypes$ = this._docTypesSrv.getDocTypes$();
        this._languageSrv.loadLanguages();
        this._countriesSrv.loadCountries();
        this._regionsSrv.loadRegions();
        this._authSrv.getLoggedUser$()
            .pipe(filter(u => !!u), take(1))
            .subscribe(user => this._docTypesSrv.loadDocumentTypes(user.entity.id));
        this.languages$ = this._languageSrv.getLanguages$();
        this.countries$ = this._countriesSrv.getCountries$();
        this.countrySubdivisions$ = this._regionsSrv.getRegions$();
        this.initForm();
        this.setFormBehaviours();
        this.setFormDataUpdater();
    }

    ngOnDestroy(): void {
        this._languageSrv.clearLanguages();
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    save(): void {
        this.save$().subscribe(() => this.form.reset());
    }

    save$(): Observable<void> {
        if (this.isValid()) {
            const value = this.form.value as BuyerBasicInfoFormValue;
            const buyerToSave: Buyer = {
                id: this._buyerId,
                // personal data
                identity_card: {
                    type: value.personalData.idCardType ?? undefined,
                    id: value.personalData.idCardId ?? undefined
                },
                name: value.personalData.name ?? undefined,
                surname: value.personalData.surname ?? undefined,
                type: value.personalData.type ?? undefined,
                date_of_birth:
                    (value.personalData.birthDate && moment(value.personalData.birthDate).format('YYYY-MM-DD')) ?? undefined,
                gender: value.personalData.gender ?? undefined,
                language: value.personalData.language ?? undefined,
                // location
                location: {
                    country: value.location.country ?
                        { code: value.location.country } : undefined,
                    country_subdivision: value.location.countrySubdivision ?
                        { code: value.location.countrySubdivision } : undefined,
                    city: value.location.city ?? undefined,
                    zip_code: value.location.zipCode ?? undefined,
                    address: value.location.address ?? undefined
                },
                // contact
                email: value.contact.email,
                phone: {
                    fix: value.contact.fix ?? undefined,
                    mobile: value.contact.mobile ?? undefined
                },
                // notes
                notes: value.notes ?? undefined
            };
            return this._buyersSrv.updateBuyer(buyerToSave)
                .pipe(
                    tap(() => {
                        this._ephemeralMessageService.showSaveSuccess();
                        this._buyersSrv.loadBuyer(this._buyerId);
                    })
                );
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return throwError(() => 'invalid form');
        }
    }

    cancel(): void {
        this.form.reset();
        this._buyersSrv.loadBuyer(this._buyerId);
    }

    private initForm(): void {
        this.form = this._fb.group({});
        this.addFormFields(this.form, bff);
        this.form.get([bff.contact.k, bff.contact.email.k]).disable();
    }

    private addFormFields(form: UntypedFormGroup, fields: unknown): void {
        Object.keys(fields)
            .filter(key => key !== 'k' && key !== 'g')
            .forEach(key => {
                const field = fields[key] as { g: boolean; val: ValidatorFn[] };
                if (field.g) {
                    const group = this._fb.group({});
                    this.addFormFields(group, field);
                    form.addControl(key, group);
                } else {
                    const control = this._fb.control(null, field.val);
                    form.addControl(key, control);
                }
            });
    }

    private setFormBehaviours(): void {
        this.countrySubdivisions$ = combineLatest([
            this.form.get([bff.location.k, bff.location.country.k]).valueChanges.pipe(startWith(null as string)),
            this._regionsSrv.getRegions$()
        ])
            .pipe(
                map(([selectedCountry, countrySubdivisions]) => {
                    selectedCountry = selectedCountry || this.form.get([bff.location.k, bff.location.country.k]).value;
                    if (selectedCountry?.length && countrySubdivisions?.length) {
                        return countrySubdivisions.filter(region => region.code.startsWith(selectedCountry));
                    } else {
                        return [];
                    }
                }),
                tap(resultRegions => {
                    if (resultRegions?.length) {
                        this.form.get([bff.location.k, bff.location.countrySubdivision.k]).enable();
                    } else {
                        this.form.get([bff.location.k, bff.location.countrySubdivision.k]).disable();
                    }
                })
            );
    }

    private setFormDataUpdater(): void {
        this._buyersSrv.getBuyer$()
            .pipe(
                filter(buyer => !!buyer),
                takeUntil(this._onDestroy)
            )
            .subscribe(buyer => {
                this._buyerId = buyer.id;
                this.form.setValue(this.buildFormValue(buyer));
            });
    }

    private buildFormValue(buyer: Buyer = null): BuyerBasicInfoFormValue {
        return {
            personalData: {
                type: buyer?.type ?? null,
                idCardId: buyer?.identity_card?.id ?? null,
                idCardType: buyer?.identity_card?.type ?? null,
                name: buyer?.name ?? null,
                surname: buyer?.surname ?? null,
                gender: buyer?.gender ?? null,
                language: buyer?.language ?? null,
                birthDate: buyer?.date_of_birth ?? null
            },
            location: {
                country: buyer?.location?.country?.code ?? null,
                countrySubdivision: buyer?.location?.country_subdivision?.code ?? null,
                city: buyer?.location?.city ?? null,
                zipCode: buyer?.location?.zip_code ?? null,
                address: buyer?.location?.address ?? null
            },
            contact: {
                email: buyer?.email ?? null,
                fix: buyer?.phone?.fix ?? null,
                mobile: buyer?.phone?.mobile ?? null
            },
            notes: buyer?.notes ?? null
        } as BuyerBasicInfoFormValue;
    }

    private isValid(): boolean {
        if (this.form.valid) {
            return true;
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(this._elRef.nativeElement);
            return false;
        }
    }
}
