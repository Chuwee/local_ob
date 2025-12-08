import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { BiReportPrompt, PostBiReportAnswerRequest } from '@admin-clients/cpanel/bi/data-access';
import { DateTimeModule, PopoverDateRangePickerComponent } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, Input, OnDestroy, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatError, MatFormField, MatHint, MatPrefix } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';
import moment from 'moment-timezone';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        DateTimeModule, PopoverDateRangePickerComponent, FormControlErrorsComponent, TranslatePipe, ReactiveFormsModule,
        FlexLayoutModule, MatPrefix, MatFormField, MatIcon, MatError, MatHint
    ],
    selector: 'app-bi-report-prompts-date-range-picker',
    templateUrl: './bi-report-prompts-date-range-picker.component.html'
})
export class BiReportPromptsDateRangePickerComponent implements OnInit, OnDestroy {
    readonly #destroyRef = inject(DestroyRef);

    readonly formControl = inject(FormBuilder).nonNullable.control(null as { startDate: string; endDate: string });

    @Input() form: FormGroup;
    @Input() requestCtrl: FormControl<PostBiReportAnswerRequest>;
    @Input() biReportPrompt: BiReportPrompt;

    ngOnInit(): void {
        this.form.addControl(`${this.biReportPrompt.id}picker`, this.formControl, { emitEvent: false });

        if (this.biReportPrompt.restrictions.required) {
            this.formControl.setValidators(Validators.required);
        }

        setTimeout(() => {
            this.formControl.setValue({
                startDate: moment().startOf('day').toISOString(),
                endDate: moment().endOf('day').toISOString()
            });
        });

        this.requestCtrl.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(request => {
                if (this.form.invalid) return;
                request.push({
                    prompt_id: this.biReportPrompt.id,
                    answers: this.formControl.value ? [this.formControl.value.startDate, this.formControl.value.endDate] : []
                });
                this.requestCtrl.setValue(request, { emitEvent: false });
            });
    }

    ngOnDestroy(): void {
        this.form.removeControl(`${this.biReportPrompt.id}picker`, { emitEvent: false });
    }
}
