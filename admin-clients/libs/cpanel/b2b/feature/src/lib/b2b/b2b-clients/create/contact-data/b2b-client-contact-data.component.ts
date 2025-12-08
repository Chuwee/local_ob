import { Country, Region, CountriesService, RegionsService } from '@admin-clients/shared/common/data-access';
import { AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, Input, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { combineLatest, Observable, Subject } from 'rxjs';
import { filter, map, startWith, takeUntil, tap } from 'rxjs/operators';
import { b2bClientFieldsRestrictions } from '../../models/b2b-client-fields-restrictions';

@Component({
    selector: 'app-b2b-client-contact-data',
    templateUrl: './b2b-client-contact-data.component.html',
    styleUrls: ['./b2b-client-contact-data.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class B2bClientContactDataComponent implements OnInit, OnDestroy, AfterViewInit {
    private _onDestroy = new Subject<void>();

    @Input() mainForm: FormGroup;
    @Input() refreshChanges$: Observable<void>;
    contactDataform: FormGroup;
    countries$: Observable<Country[]>;
    regions$: Observable<Region[]>;
    filteredRegions$: Observable<Region[]>;
    isInProgress$: Observable<boolean>;

    constructor(
        private _fb: FormBuilder,
        private _ref: ChangeDetectorRef,
        private _countriesService: CountriesService,
        private _regionsService: RegionsService
    ) { }

    ngOnInit(): void {
        if (!this.mainForm.get('contactData')) {
            this.contactDataform = this._fb.group({
                contactPerson: [null, Validators.required],
                email: [null, [Validators.required, Validators.email]],
                phone: [null, [Validators.required, Validators.pattern(b2bClientFieldsRestrictions.b2bClientPhonePattern)]],
                address: null,
                country: [null, Validators.required],
                countrySubdivision: [{ value: null, disabled: true }, Validators.required]
            });
            this.mainForm.addControl('contactData', this.contactDataform);
        } else {
            this.contactDataform = this.mainForm.get('contactData') as FormGroup;
        }

        this.initComponentModels();
    }

    ngAfterViewInit(): void {
        this.refreshChanges$
            .pipe(takeUntil(this._onDestroy))
            .subscribe(() => {
                this._ref.detectChanges();
            });
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    private initComponentModels(): void {
        this.countries$ = this._countriesService.getCountries$().pipe(filter(countries => !!countries));

        this.regions$ = this._regionsService.getRegions$();

        this.filteredRegions$ = combineLatest([
            this.contactDataform.get('country').valueChanges
                .pipe(startWith(this.contactDataform.get('country').value as string)),
            this.regions$
        ]).pipe(
            filter(([_, regions]) => !!regions),
            map(([countryCode, regions]) =>
                regions.filter(region => region.code.startsWith(countryCode + '-'))
            ),
            tap(regions => {
                const countrySubdivisionField = this.contactDataform.get('countrySubdivision');
                if (regions.length === 1) {
                    countrySubdivisionField.enable();
                    this.contactDataform.get('countrySubdivision').setValue(regions[0].code);
                } else if (regions.length > 0) {
                    countrySubdivisionField.enable();
                } else {
                    countrySubdivisionField.disable();
                    countrySubdivisionField.setValue(null);
                }
            })
        );

        this.isInProgress$ = this._regionsService.isRegionsLoading$();
    }

}
