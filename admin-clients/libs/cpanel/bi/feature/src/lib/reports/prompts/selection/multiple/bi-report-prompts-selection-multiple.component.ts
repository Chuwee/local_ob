import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { BiReport, BiReportPrompt, BiReportPromptAnswer, BiService, PostBiReportAnswerRequest } from '@admin-clients/cpanel/bi/data-access';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import {
    Chip, ChipsComponent, HelpButtonComponent, SelectServerSearchComponent
} from '@admin-clients/shared/common/ui/components';
import { maxSelectedItems } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, Input, OnDestroy, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, ValidatorFn, Validators } from '@angular/forms';
import { MatError, MatFormField, MatSuffix } from '@angular/material/form-field';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, first, map } from 'rxjs';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FlexLayoutModule, ReactiveFormsModule, ChipsComponent, TranslatePipe, FormControlErrorsComponent, SelectServerSearchComponent,
        HelpButtonComponent, MatFormField, MatSuffix, MatError
    ],
    selector: 'app-bi-report-prompts-selection-multiple',
    templateUrl: './bi-report-prompts-selection-multiple.component.html'
})
export class BiReportPromptsSelectionMultipleComponent implements OnInit, OnDestroy {
    readonly #biReportsSrv = inject(BiService);
    readonly #auth = inject(AuthenticationService);
    readonly #destroyRef = inject(DestroyRef);

    #q: string;
    #offset: number;
    #isFirst = true;

    readonly formControl = inject(FormBuilder).control(null as BiReportPromptAnswer[]);
    readonly chipsBS = new BehaviorSubject<Chip[]>([]);
    readonly answers$ = this.#biReportsSrv.reportPromptAnswers.getMore$();
    readonly moreAnswersAvailable$ = this.#biReportsSrv.reportPromptAnswers.getMore$()
        .pipe(map(answers => answers?.length === this.#offset));

    readonly maxSelection = Number.MAX_VALUE;

    @Input() form: FormGroup;
    @Input() requestCtrl: FormControl<PostBiReportAnswerRequest>;
    @Input() biReportPrompt: BiReportPrompt;
    @Input() biReport: BiReport;
    @Input() limit: number;

    ngOnInit(): void {
        this.#offset = this.limit;
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

        if (this.biReportPrompt.default_answers.length) {
            this.formControl.setValue(this.biReportPrompt.default_answers, { emitEvent: false });
            const chips = this.#mapAnswersToChips(this.biReportPrompt.default_answers);
            this.chipsBS.next(chips);
        }

        this.formControl.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(selectedAnswers => {
                if (this.requestCtrl.value) return;

                if (!selectedAnswers?.length) {
                    this.chipsBS.next([]);
                    return;
                }
                const chips = this.#mapAnswersToChips(selectedAnswers);
                this.chipsBS.next(chips);
            });

        this.requestCtrl.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(request => {
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

    removeChip(chip: Chip): void {
        const selectedAnswers = this.formControl.value.filter(answer => answer.id !== chip.key);
        this.formControl.setValue(selectedAnswers, { emitEvent: false });
        const chips = this.#mapAnswersToChips(selectedAnswers);
        this.chipsBS.next(chips);
    }

    removeChips(): void {
        this.formControl.setValue([], { emitEvent: false });
        this.chipsBS.next([]);
    }

    #mapAnswersToChips(answers: BiReportPromptAnswer[]): Chip[] {
        return answers.map(defaultAnswer => ({ label: defaultAnswer.name, key: defaultAnswer.id }));
    }
}
