
import { CountriesService } from '@admin-clients/shared/common/data-access';
import { CSV_FILE_PROCESSOR } from '@admin-clients/shared/common/feature/csv';
import { Chip, ChipsComponent, DialogSize, ObDialog } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { AbstractControl, FormBuilder, ReactiveFormsModule, ValidationErrors } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, Observable, map, take } from 'rxjs';

@Component({
    imports: [
        CommonModule,
        TranslatePipe,
        ReactiveFormsModule,
        MaterialModule,
        FlexLayoutModule,
        ChipsComponent
    ],
    selector: 'app-launch-matcher-listings',
    templateUrl: './launch-matcher.component.html',
    styleUrls: ['./launch-matcher.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [{
        provide: CSV_FILE_PROCESSOR,
        useExisting: LaunchMatcherComponent
    }]
})
export class LaunchMatcherComponent extends ObDialog<LaunchMatcherComponent, {}, {
    countries: string[], taxonomies: string[]
}> implements OnInit {
    readonly #fb = inject(FormBuilder);
    readonly #dialogRef = inject(MatDialogRef<LaunchMatcherComponent>);
    readonly #countriesService = inject(CountriesService);

    readonly form = this.#fb.group({
        countries: [[] as string[]],
        taxonomies: [[] as string[]],
        countryInput: ['' as string, null, [c => this.countryValidator(c)]],
        taxonomyInput: ['' as string]
    });

    readonly #countriesChips = new BehaviorSubject<Chip[]>([]);
    readonly #taxonomiesChips = new BehaviorSubject<Chip[]>([]);

    readonly countriesChips$ = this.#countriesChips.asObservable();
    readonly taxonomiesChips$ = this.#taxonomiesChips.asObservable();

    constructor() {
        super(DialogSize.MEDIUM);
    }

    ngOnInit(): void {
        this.#countriesService.loadCountries();
    }

    close(countries?: string[], taxonomies?: string[]): void {
        if (countries?.length > 0 || taxonomies?.length > 0) {
            this.#dialogRef.close({ countries, taxonomies });
        } else {
            this.#dialogRef.close();
        }
    }

    launchMatcher(): void {
        this.close(this.form.value.countries, this.form.value.taxonomies);
    }

    addChip(field: string): void {
        const formControl = field === 'countries' ? this.form.controls.countries : this.form.controls.taxonomies;
        const chips = field === 'countries' ? this.#countriesChips : this.#taxonomiesChips;
        const formValue = field === 'countries' ? this.form.value.countries : this.form.value.taxonomies;
        const inputValue = field === 'countries' ? this.form.value.countryInput : this.form.value.taxonomyInput;
        const inputControl = field === 'countries' ? this.form.controls.countryInput : this.form.controls.taxonomyInput;

        if (inputControl.valid && inputValue.length > 0) {
            if (formValue.length === 0) {
                formControl.setValue([inputValue]);
                chips.next([{ label: inputValue, value: inputValue }]);
            } else if (!formValue.includes(inputValue)) {
                formControl.setValue(formValue.concat([inputValue]));
                chips.next(chips.getValue().concat([{ label: inputValue, value: inputValue }]));
            }
            inputControl.setValue('');
        } else {
            inputControl.markAsTouched();
        }
    }

    removeChip(chip: Chip, field: string): void {
        const formControl = field === 'countries' ? this.form.controls.countries : this.form.controls.taxonomies;
        const chips = field === 'countries' ? this.#countriesChips : this.#taxonomiesChips;
        const inputControl = field === 'countries' ? this.form.controls.countryInput : this.form.controls.taxonomyInput;
        const formValue = field === 'countries' ? this.form.value.countries : this.form.value.taxonomies;

        formControl.setValue(formValue.filter(id => id !== chip.value));
        chips.next(chips.getValue().filter(c => c.value !== chip.value));
        inputControl.setValue('');
    }

    private countryValidator(control: AbstractControl): Observable<ValidationErrors | null> {
        return this.#countriesService.getCountries$().pipe(
            take(1),
            map(countries => (control.value.length === 0 || countries?.find(c => c.code === control.value)) ? null : { countryNotValid: true })
        );
    }
}
