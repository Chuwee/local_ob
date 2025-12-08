import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { BiReportPrompt, BiReportPromptAnswer, PostBiReportAnswerRequest } from '@admin-clients/cpanel/bi/data-access';
import { maxSelectedItems } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, Input, OnDestroy, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, ValidatorFn, Validators } from '@angular/forms';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatError } from '@angular/material/form-field';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        ReactiveFormsModule, FormControlErrorsComponent, FlexLayoutModule, MatCheckbox, MatError
    ],
    selector: 'app-bi-report-prompts-selection-check',
    templateUrl: './bi-report-prompts-selection-check.component.html'
})
export class BiReportPromptsSelectionCheckComponent implements OnInit, OnDestroy {
    readonly #fb = inject(FormBuilder);
    readonly #destroyRef = inject(DestroyRef);

    readonly formArray = this.#fb.array([] as boolean[]);
    readonly formControl = this.#fb.control(null as BiReportPromptAnswer[]);

    @Input() form: FormGroup;
    @Input() requestCtrl: FormControl<PostBiReportAnswerRequest>;
    @Input() biReportPrompt: BiReportPrompt;
    @Input() answers: BiReportPromptAnswer[];

    ngOnInit(): void {
        this.form.addControl(this.biReportPrompt.id, this.formControl, { emitEvent: false });

        const validators: ValidatorFn[] = [];

        if (this.biReportPrompt.restrictions.required) {
            validators.push(Validators.required);
        }

        if (this.biReportPrompt.restrictions.max_selections) {
            validators.push(maxSelectedItems(this.biReportPrompt.restrictions.max_selections));
        }

        if (validators.length) {
            this.formControl.setValidators(validators);
        }

        this.answers.forEach(answer => {
            const foundDefaultAnswer = this.biReportPrompt.default_answers
                .find(defaultAnswer => defaultAnswer.id === answer.id);
            this.formArray.push(this.#fb.control(!!foundDefaultAnswer), { emitEvent: false });
        });

        if (this.biReportPrompt.default_answers.length) {
            this.formControl.setValue(this.biReportPrompt.default_answers);
        }

        this.formArray.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(values => {
                const resultValues = values
                    .map((value, index) => {
                        if (value) {
                            return this.answers[index];
                        } else {
                            return null;
                        }
                    })
                    .filter(Boolean);
                this.formControl.setValue(resultValues);
            });

        this.requestCtrl.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(request => {
                // Case: required but not touched, in order to set the invalid state in the form
                this.formControl.setValue(this.formControl.getRawValue(), { emitEvent: false });
                if (this.form.invalid) return;

                request.push({
                    prompt_id: this.biReportPrompt.id,
                    answers: this.formControl.value?.map(value => value.id) ?? []
                });
                this.requestCtrl.setValue(request, { emitEvent: false });
            });
    }

    ngOnDestroy(): void {
        this.form.removeControl(this.biReportPrompt.id, { emitEvent: false });
    }
}
