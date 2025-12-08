import { ReviewCriteria } from '@admin-clients/cpanel/channels/data-access';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ControlValueAccessor, FormBuilder, FormsModule, NgControl, ReactiveFormsModule } from '@angular/forms';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'ob-review-config-criteria-control',
    template: `
        <div class="grid gap-4">
            <label class="mat-subtitle-2" for="criteriaGroup">{{ 'CHANNELS.REVIEWS.CONFIG.FORMS.CRITERIA_LABEL' | translate}}</label>
            <mat-radio-group id="criteriaGroup" class="grid gap-4" [formControl]="formControl">
                <mat-radio-button value="ALWAYS">{{ 'CHANNELS.REVIEWS.CONFIG.FORMS.CRITERIA_ALWAYS' | translate}}</mat-radio-button>
                <mat-radio-button value="ONLY_IF_VALIDATED">
                    {{ 'CHANNELS.REVIEWS.CONFIG.FORMS.CRITERIA_ONLY_IF_VALIDATED' | translate}}
                </mat-radio-button>
                <mat-radio-button value="NEVER">{{ 'CHANNELS.REVIEWS.CONFIG.FORMS.CRITERIA_NEVER' | translate}}</mat-radio-button>
            </mat-radio-group>
        </div>
    `,
    imports: [MatRadioButton, MatRadioGroup, TranslatePipe, ReactiveFormsModule, FormsModule],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ReviewConfigCriteriaControlComponent implements ControlValueAccessor {
    readonly ngControl = inject(NgControl, { optional: true, self: true });
    readonly formControl = inject(FormBuilder).control<ReviewCriteria>('ALWAYS');

    constructor() {
        if (this.ngControl) {
            this.ngControl.valueAccessor = this;
        }

        this.formControl.valueChanges
            .pipe(takeUntilDestroyed())
            .subscribe(value => {
                if (this.formControl.dirty) {
                    this.onChange(value);
                }
            });
    }

    writeValue(value: ReviewCriteria): void {
        this.formControl.setValue(value, { emitEvent: false });
    }

    registerOnChange(fn: (value: ReviewCriteria) => void): void {
        this.onChange = fn;
    }

    registerOnTouched(fn: () => void): void {
        this.onTouched = fn;
    }

    setDisabledState(isDisabled: boolean): void {
        isDisabled ? this.formControl.disable() : this.formControl.enable();
    }

    onChange: (value: ReviewCriteria) => void = () => { };
    onTouched: () => void = () => { };
}
