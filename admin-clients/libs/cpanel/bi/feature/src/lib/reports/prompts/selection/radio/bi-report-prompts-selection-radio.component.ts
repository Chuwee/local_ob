import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { BiReportPrompt, BiReportPromptAnswer, PostBiReportAnswerRequest } from '@admin-clients/cpanel/bi/data-access';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, Input, OnDestroy, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatError } from '@angular/material/form-field';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        ReactiveFormsModule, FormControlErrorsComponent, MatRadioGroup, MatRadioButton, MatError, FlexLayoutModule
    ],
    selector: 'app-bi-report-prompts-selection-radio',
    templateUrl: './bi-report-prompts-selection-radio.component.html'
})
export class BiReportPromptsSelectionRadioComponent implements OnInit, OnDestroy {
    readonly #destroyRef = inject(DestroyRef);
    readonly formControl =  inject(FormBuilder).control('');

    @Input() form: FormGroup;
    @Input() requestCtrl: FormControl<PostBiReportAnswerRequest>;
    @Input() biReportPrompt: BiReportPrompt;
    @Input() answers: BiReportPromptAnswer[];

    ngOnInit(): void {
        this.form.addControl(this.biReportPrompt.id, this.formControl, { emitEvent: false });

        if (this.biReportPrompt.restrictions.required) {
            this.formControl.setValidators(Validators.required);
        }

        if (this.biReportPrompt.default_answers) {
            //It is a radio button, so there is only one default answer
            this.formControl.setValue(this.biReportPrompt.default_answers[0].id, { emitEvent: false });
        }

        this.requestCtrl.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(request => {
               if (this.form.invalid) return;

               request.push({
                   prompt_id: this.biReportPrompt.id,
                   answers: this.formControl.value ? [this.formControl.value] : []
               });
               this.requestCtrl.setValue(request, { emitEvent: false });
            });
    }

    ngOnDestroy(): void {
        this.form.removeControl(this.biReportPrompt.id, { emitEvent: false });
    }
}
