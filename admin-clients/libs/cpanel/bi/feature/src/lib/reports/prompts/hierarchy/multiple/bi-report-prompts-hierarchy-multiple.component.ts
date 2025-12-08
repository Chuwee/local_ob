import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import {
    biProviders, BiReport, BiReportPrompt, BiReportPromptHierarchyStep, BiReportPromptHierarchyStepAnswer, BiService,
    GetBiReportPromptHierarchyRequest, PostBiReportAnswerRequest
} from '@admin-clients/cpanel/bi/data-access';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import {
    Chip, ChipsComponent, HelpButtonComponent, SelectServerSearchComponent
} from '@admin-clients/shared/common/ui/components';
import { maxSelectedItems } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit, inject, DestroyRef } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, ValidatorFn, Validators } from '@angular/forms';
import { MatError, MatFormField, MatSuffix } from '@angular/material/form-field';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, first, map } from 'rxjs';
import { VmBiReportPromptsHierarchyResponse } from '../models/bi-report-prompts-hierarchy.model';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FlexLayoutModule, ReactiveFormsModule, ChipsComponent, SelectServerSearchComponent, FormControlErrorsComponent,
        HelpButtonComponent, TranslatePipe, MatFormField, MatSuffix, MatError
    ],
    providers: [biProviders],
    selector: 'app-bi-report-prompts-hierarchy-multiple',
    templateUrl: './bi-report-prompts-hierarchy-multiple.component.html'
})
export class BiReportPromptsHierarchyMultipleComponent implements OnInit, OnDestroy {
    readonly #biReportsSrv = inject(BiService);
    readonly #auth = inject(AuthenticationService);
    readonly #destroyRef = inject(DestroyRef);

    #q: string;
    #offset = 0;

    readonly formControl = inject(FormBuilder).nonNullable.control<BiReportPromptHierarchyStepAnswer[]>({ value: [], disabled: true });
    readonly answers$ = this.#biReportsSrv.reportPromptHierarchyStepAnswers.getMore$();
    readonly moreAnswersAvailable$ = this.#biReportsSrv.reportPromptHierarchyStepAnswers.getMore$()
        .pipe(map(answers => answers?.length === this.#offset));

    readonly chipsBS = new BehaviorSubject<Chip[]>([]);
    readonly maxSelection = Number.MAX_VALUE;

    @Input() form: FormGroup;
    @Input() biReportPrompt: BiReportPrompt;
    @Input() biReport: BiReport;
    @Input() step: BiReportPromptHierarchyStep;
    @Input() index: number;
    @Input() responsesCtrl: FormControl<VmBiReportPromptsHierarchyResponse[]>;
    @Input() responseChangedCtrl: FormControl<number>;
    @Input() requestCtrl: FormControl<PostBiReportAnswerRequest>;
    @Input() limit: number;
    @Input() isLast: boolean;

    ngOnInit(): void {
        if (this.index === 0) {
            this.formControl.enable({ emitEvent: false });
        }

        this.form.addControl(`${this.biReportPrompt.id}${this.index}`, this.formControl, { emitEvent: false });

        const validators: ValidatorFn[] = [];

        if (this.biReportPrompt.restrictions.required && this.index === 0) {
            validators.push(Validators.required);
        }

        if (this.biReportPrompt.restrictions.max_selections && this.isLast) {
            validators.push(maxSelectedItems(this.biReportPrompt.restrictions.max_selections));
        } else if (this.biReportPrompt.restrictions.max_selections) {
            validators.push(maxSelectedItems(5));
        }

        if (validators.length) {
            this.formControl.setValidators(validators);
        }

        this.formControl.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(selectedResponses => {
                if (this.requestCtrl.value) return;

                if (!selectedResponses.length) {
                    const responses = this.responsesCtrl.value
                        .map((response, index) => {
                            if (index !== this.index) {
                                return response;
                            }
                            return {
                                ...response,
                                answers: []
                            };
                        });
                    this.responsesCtrl.setValue(responses);
                    this.responseChangedCtrl.setValue(this.index);
                    this.chipsBS.next([]);
                    return;
                }

                const responses = this.responsesCtrl.value
                    .map((response, index) => {
                        if (index !== this.index) {
                            return response;
                        }
                        return {
                            ...response,
                            answers: selectedResponses
                        };
                    });
                this.responsesCtrl.setValue(responses);
                this.responseChangedCtrl.setValue(this.index);
                const chips = this.mapAnswersToChips(selectedResponses);
                this.chipsBS.next(chips);
            });

        this.responseChangedCtrl.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(responseChanged => {
                if (responseChanged < this.index - 1 && this.formControl.enabled) {
                    this.#biReportsSrv.reportPromptHierarchyStepAnswers.cancelMore();
                    this.#biReportsSrv.reportPromptHierarchyStepAnswers.clearMore();
                    this.#offset = 0;

                    this.formControl.disable({ emitEvent: false });
                    this.removeChips();
                } else if (responseChanged === this.index - 1) {
                    this.#biReportsSrv.reportPromptHierarchyStepAnswers.cancelMore();
                    this.#biReportsSrv.reportPromptHierarchyStepAnswers.clearMore();
                    this.#offset = 0;

                    const previousResponseWithAnswers = this.responsesCtrl.value
                        .find((response, index) => index === this.index - 1 && response.answers.length);

                    if (previousResponseWithAnswers && this.formControl.disabled) {
                        this.formControl.enable({ emitEvent: false });
                    } else if (!previousResponseWithAnswers && this.formControl.enabled) {
                        this.formControl.disable({ emitEvent: false });
                    }
                    this.removeChips();
                }
            });
    }

    ngOnDestroy(): void {
        this.form.removeControl(`${this.biReportPrompt.id}${this.index}`, { emitEvent: false });
    }

    loadHierarchy(q?: string, next = false): void {
        this.#auth.impersonation.get$()
            .pipe(first())
            .subscribe(impersonation => {
                if (this.#q !== q) {
                    this.#offset = 0;
                }
                this.#q = q;

                const request: GetBiReportPromptHierarchyRequest = {
                    limit: this.limit,
                    q: this.#q,
                    offset: this.#offset,
                    previous: this.responsesCtrl.value
                        .slice(0, this.index)
                        .map(response => ({
                            step: response.id,
                            answers: response.answers.map(answer => answer.id)
                        })),
                    impersonation
                };
                this.#biReportsSrv.reportPromptHierarchyStepAnswers.loadMore(
                    this.biReport.id, this.biReportPrompt.id, this.step.id, request, next);
                this.#offset += this.limit;
            });
    }

    removeChip(chip: Chip): void {
        const selectedAnswers = this.formControl.value.filter(answer => answer.id !== chip.key);
        this.formControl.setValue(selectedAnswers);
        const chips = this.mapAnswersToChips(selectedAnswers);
        this.chipsBS.next(chips);
    }

    removeChips(): void {
        this.formControl.setValue([]);
        this.chipsBS.next([]);
    }

    private mapAnswersToChips(answers: BiReportPromptHierarchyStepAnswer[]): Chip[] {
        return answers.map(defaultAnswer => ({ label: defaultAnswer.name, key: defaultAnswer.id }));
    }
}
