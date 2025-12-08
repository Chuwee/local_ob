import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { BiReportPrompt, BiReportPromptAnswer, BiService, PostBiReportAnswerRequest } from '@admin-clients/cpanel/bi/data-access';
import { SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { compareWithIdOrCode } from '@admin-clients/shared/data-access/models';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, Input, OnDestroy, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatError, MatFormField } from '@angular/material/form-field';
import { MatOption, MatSelect } from '@angular/material/select';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { first } from 'rxjs';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormControlErrorsComponent, TranslatePipe, ReactiveFormsModule, FlexLayoutModule,
        SelectSearchComponent, MatFormField, MatSelect, MatOption, AsyncPipe, MatError, EllipsifyDirective, MatTooltip
    ],
    selector: 'app-bi-report-prompts-date-range-presets',
    templateUrl: './bi-report-prompts-date-range-presets.component.html'
})
export class BiReportPromptsDateRangePresetsComponent implements OnInit, OnDestroy {
    readonly #destroyRef = inject(DestroyRef);
    readonly #biReportsSrv = inject(BiService);

    readonly formControl = inject(FormBuilder).nonNullable.control(null as BiReportPromptAnswer);
    readonly answers$ = this.#biReportsSrv.reportPromptAnswers.get$().pipe(first(Boolean));

    @Input() form: FormGroup;
    @Input() requestCtrl: FormControl<PostBiReportAnswerRequest>;
    @Input() biReportPrompt: BiReportPrompt;

    readonly compareWith = compareWithIdOrCode;

    ngOnInit(): void {
        this.form.addControl(`${this.biReportPrompt.id}presets`, this.formControl, { emitEvent: false });

        if (this.biReportPrompt.restrictions.required) {
            this.formControl.setValidators(Validators.required);
        }

        if (this.biReportPrompt.default_answers.length) {
            this.formControl.setValue(this.biReportPrompt.default_answers[0], { emitEvent: false });
        }

        this.requestCtrl.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(request => {
                if (this.form.invalid) return;

                request.push({
                    prompt_id: this.biReportPrompt.id,
                    answers: this.formControl.value ? [this.formControl.value.id] : [],
                    preset: true
                });
                this.requestCtrl.setValue(request, { emitEvent: false });
            });
    }

    ngOnDestroy(): void {
        this.form.removeControl(`${this.biReportPrompt.id}presets`, { emitEvent: false });
    }
}
