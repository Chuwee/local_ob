import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import {
    BiReport, BiReportPrompt, BiReportPromptAnswer, BiService, PostBiReportAnswerRequest
} from '@admin-clients/cpanel/bi/data-access';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { HelpButtonComponent, SelectServerSearchComponent } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, Input, OnDestroy, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatError, MatFormField, MatSuffix } from '@angular/material/form-field';
import { first, map } from 'rxjs';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FlexLayoutModule, ReactiveFormsModule, SelectServerSearchComponent, FormControlErrorsComponent,
        HelpButtonComponent, MatFormField, MatSuffix, MatError
    ],
    selector: 'app-bi-report-prompts-selection-single',
    templateUrl: 'bi-report-prompts-selection-single.component.html'
})
export class BiReportPromptsSelectionSingleComponent implements OnInit, OnDestroy {
    readonly #biReportsSrv = inject(BiService);
    readonly #auth = inject(AuthenticationService);
    readonly #destroyRef = inject(DestroyRef);

    #q: string;
    #offset: number;
    #isFirst = true;

    readonly formControl = inject(FormBuilder).control(null as BiReportPromptAnswer);
    readonly answers$ = this.#biReportsSrv.reportPromptAnswers.getMore$();
    readonly moreAnswersAvailable$ = this.#biReportsSrv.reportPromptAnswers.getMore$()
        .pipe(map(answers => answers?.length === this.#offset));

    @Input() form: FormGroup;
    @Input() requestCtrl: FormControl<PostBiReportAnswerRequest>;
    @Input() biReportPrompt: BiReportPrompt;
    @Input() biReport: BiReport;
    @Input() limit: number;

    ngOnInit(): void {
        this.#offset = this.limit;
        this.form.addControl(this.biReportPrompt.id, this.formControl, { emitEvent: false });

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
                    answers: this.formControl.value ? [this.formControl.value.id] : []
                });
                this.requestCtrl.setValue(request, { emitEvent: false });
            });
    }

    ngOnDestroy(): void {
        this.form.removeControl(this.biReportPrompt.id, { emitEvent: false });
    }

    loadAnswers(q?: string, next = false): void {
        if (this.#isFirst) {
            this.#isFirst = false;
            this.#biReportsSrv.reportPromptAnswers.get$()
                .pipe(first())
                .subscribe(answers => {
                    this.#biReportsSrv.reportPromptAnswers.setMore(answers);
                });
        } else {
            this.#auth.impersonation.get$()
                .pipe(first())
                .subscribe(impersonation => {
                    if (this.#q !== q) {
                        this.#offset = 0;
                    }
                    this.#biReportsSrv.reportPromptAnswers.loadMore(this.biReport.id, this.biReportPrompt.id, {
                        offset: this.#offset,
                        limit: this.limit,
                        q,
                        impersonation
                    }, next);
                    this.#q = q;
                    this.#offset += this.limit;
                });
        }
    }
}
