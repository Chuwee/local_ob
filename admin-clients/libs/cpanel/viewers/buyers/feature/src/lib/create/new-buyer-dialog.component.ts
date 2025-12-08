import { BuyersService, Buyer } from '@admin-clients/cpanel-viewers-buyers-data-access';
import { CountriesService, Country, LanguagesService, Region, RegionsService } from '@admin-clients/shared/common/data-access';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TranslateService } from '@ngx-translate/core';
import { combineLatest, Observable, Subject } from 'rxjs';
import { debounceTime, filter, map, tap } from 'rxjs/operators';
import { NewBuyerDialogData } from './model/new-buyer-dialog-data.model';

@Component({
    selector: 'app-new-buyer-dialog',
    templateUrl: './new-buyer-dialog.component.html',
    styleUrls: ['./new-buyer-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class NewBuyerDialogComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    private _entityId: number;

    form: UntypedFormGroup;
    countries$: Observable<Country[]>;
    provinces$: Observable<Region[]>;
    languages$: Observable<{ code: string; name: string }[]>;
    isCreating$: Observable<boolean>;

    constructor(
        private _dialogRef: MatDialogRef<NewBuyerDialogData>,
        @Inject(MAT_DIALOG_DATA) private _data: NewBuyerDialogData,
        private _fb: UntypedFormBuilder,
        private _translateSrv: TranslateService,
        private _buyersSrv: BuyersService,
        private _countriesSrv: CountriesService,
        private _regionsSrv: RegionsService,
        private _languagesSrv: LanguagesService
    ) {
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
        this._dialogRef.disableClose = false;
        this._entityId = _data.entityId;
    }

    ngOnInit(): void {
        this.isCreating$ = this._buyersSrv.isUpdatingBuyer$();
        this._languagesSrv.loadLanguages();
        this.form = this._fb.group({
            email: [null, [Validators.required, Validators.email]],
            name: [null, [Validators.required]],
            surname: [null, [Validators.required]],
            language: [null, [Validators.required]],
            country: [null, [Validators.required]],
            countrySubdivision: [{ value: null, disabled: true }, [Validators.required]]
        });
        this.languages$ = this._languagesSrv.getLanguages$()
            .pipe(map(languages => {
                if (languages) {
                    return languages.map(language => ({
                        code: language.code,
                        name: this._translateSrv.instant('LANGUAGES.' + language.code)
                    }));
                } else {
                    return [];
                }
            }));
        this.countries$ = this._countriesSrv.getCountries$()
            .pipe(
                tap(countries => {
                    if (!countries) {
                        this._countriesSrv.loadCountries();
                    }
                }),
                filter(countries => countries !== null)
            );
        this.provinces$ = combineLatest([
            this._regionsSrv.getRegions$(),
            this.form.get('country').valueChanges
        ])
            .pipe(
                debounceTime(100),
                tap(([provinces]) => !provinces && this._regionsSrv.loadRegions()),
                map(([provinces, selectedCountry]) => provinces?.filter(elem => elem.code.startsWith(selectedCountry))),
                tap(regions => {
                    if (!regions?.length) {
                        this.form.get('countrySubdivision').disable();
                    } else {
                        this.form.get('countrySubdivision').enable();
                    }
                })
            );
    }

    ngOnDestroy(): void {
        this._languagesSrv.clearLanguages();
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    create(): void {
        if (this.isValid()) {
            const value = this.form.value;
            this._buyersSrv.createBuyer({
                entity_id: this._entityId,
                email: value.email,
                name: value.name,
                surname: value.surname,
                language: value.language,
                location: {
                    country: {
                        code: value.country
                    },
                    country_subdivision: value.countrySubdivision ?
                        { code: value.countrySubdivision } : undefined
                }
            } as Buyer)
                .subscribe(response => this.close(response.id));
        }
    }

    close(id?: string): void {
        this._dialogRef.close(id);
    }

    private isValid(): boolean {
        if (this.form.valid) {
            return true;
        } else {
            this.form.markAllAsTouched();
            return false;
        }
    }
}
