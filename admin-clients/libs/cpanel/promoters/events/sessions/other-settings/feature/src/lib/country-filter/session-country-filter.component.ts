import { EventSessionsService, SessionCountryFilter } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { CountriesService } from '@admin-clients/shared/common/data-access';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { animate, state, style, transition, trigger } from '@angular/animations';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, Input, OnDestroy, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormControl, UntypedFormGroup, ValidationErrors } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { Subject } from 'rxjs';
import { filter, takeUntil } from 'rxjs/operators';

@Component({
    selector: 'app-session-country-filter',
    changeDetection: ChangeDetectionStrategy.OnPush,
    animations: [
        trigger('expandSelection', [
            state('expanded', style({ height: '*' })),
            state('collapsed', style({ height: '0' })),
            transition('expanded <=> collapsed', [animate('0.1s')])
        ])
    ],
    styleUrls: ['./session-country-filter.component.scss'],
    templateUrl: './session-country-filter.component.html',
    imports: [
        MaterialModule, ReactiveFormsModule, TranslatePipe, CommonModule,
        FlexLayoutModule
    ]
})
export class SessionCountryFilterComponent implements OnInit, OnDestroy {
    private readonly _sessionsService = inject(EventSessionsService);
    private readonly _onDestroy = new Subject<void>();
    readonly countries$ = inject(CountriesService).getCountries$().pipe(filter(countries => !!countries));
    readonly countryFilterFormGroup = inject(UntypedFormBuilder)
        .group({
            enable: [{ value: null }],
            countries: [{ value: null, disabled: true }]
        }, {
            validators: [SessionCountryFilterComponent.validateCountries]
        });

    @Input() set form(value: UntypedFormGroup) {
        if (value.contains('countryFilter')) {
            return;
        }
        value.addControl('countryFilter', this.countryFilterFormGroup, { emitEvent: false });
    }

    readonly markForCheck = ((): () => void => {
        const cdr = inject(ChangeDetectorRef);
        return () => cdr.markForCheck();
    })();

    ngOnInit(): void {
        this.countryFilterFormChangeHandler();
        this.updateCountryFilterForm();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        const form = this.countryFilterFormGroup.parent as UntypedFormGroup;
        form.removeControl('countryFilter', { emitEvent: false });
    }

    getValue(): SessionCountryFilter {
        return {
            enable: this.countryFilterFormGroup.value.enable,
            countries: this.countryFilterFormGroup.value.countries
        };
    }

    private countryFilterFormChangeHandler(): void {
        this.countryFilterFormGroup.get('enable').valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe((enable: boolean) => {
                const countries = this.countryFilterFormGroup.get('countries') as UntypedFormControl;
                if (!enable) {
                    countries.disable({ emitEvent: false });
                } else {
                    countries.enable({ emitEvent: false });
                }
            });
    }

    private updateCountryFilterForm(): void {
        this._sessionsService.session.get$()
            .pipe(filter(session => !!session))
            .subscribe(session => {
                this.countryFilterFormGroup.patchValue({
                    enable: session.settings?.country_filter?.enable,
                    countries: session.settings?.country_filter?.countries
                }, { onlySelf: true });
                this.countryFilterFormGroup.markAsPristine();
                this.countryFilterFormGroup.markAsUntouched();
            });
    }

    private static validateCountries(countryForm: UntypedFormGroup): ValidationErrors {
        if (countryForm.get('enable').value
            && !countryForm.get('countries').value?.length) {
            return { countryListEmpty: true };
        }
        return null;
    }
}
