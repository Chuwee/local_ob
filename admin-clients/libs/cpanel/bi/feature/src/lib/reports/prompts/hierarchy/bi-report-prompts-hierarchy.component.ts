import {
    biProviders, BiReport, BiReportPrompt, BiService, PostBiReportAnswerRequest
} from '@admin-clients/cpanel/bi/data-access';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, EventEmitter, inject, Input, OnInit, Output } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { filter, first } from 'rxjs';
import { VmBiReportPromptsHierarchyResponse } from './models/bi-report-prompts-hierarchy.model';
import { BiReportPromptsHierarchyMultipleComponent } from './multiple/bi-report-prompts-hierarchy-multiple.component';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        BiReportPromptsHierarchyMultipleComponent, AsyncPipe
    ],
    providers: [biProviders],
    selector: 'app-bi-report-prompts-hierarchy',
    templateUrl: './bi-report-prompts-hierarchy.component.html'
})
export class BiReportPromptsHierarchyComponent implements OnInit {
    readonly #biReportsSrv = inject(BiService);
    readonly #auth = inject(AuthenticationService);
    readonly #destroyRef = inject(DestroyRef);
    readonly #fb = inject(FormBuilder);

    readonly responsesCtrl = this.#fb.nonNullable.control<VmBiReportPromptsHierarchyResponse[]>([]);
    readonly responseChangedCtrl = this.#fb.nonNullable.control(null as number);
    readonly steps$ = this.#biReportsSrv.reportPromptHierarchySteps.get$();
    readonly limit = 10;

    @Input() biReportPrompt: BiReportPrompt;
    @Input() biReport: BiReport;
    @Input() form: FormGroup;
    @Input() requestCtrl: FormControl<PostBiReportAnswerRequest>;

    @Output() loadingEmitter = new EventEmitter<{ promptId: string; isLoading: boolean }>();

    ngOnInit(): void {
        this.#auth.impersonation.get$()
            .pipe(first())
            .subscribe(impersonation => {
                this.#biReportsSrv.reportPromptHierarchySteps.load(
                    this.biReport.id,
                    this.biReportPrompt.id,
                    { impersonation }
                );
            });

        this.#biReportsSrv.reportPromptHierarchySteps.get$()
            .pipe(filter(Boolean), takeUntilDestroyed(this.#destroyRef))
            .subscribe(steps => {
                const responses: VmBiReportPromptsHierarchyResponse[] = steps
                    .map(step => ({
                           id: step.id,
                           answers: []
                       }));
                this.responsesCtrl.reset(responses, { emitEvent: false });
            });

        this.#biReportsSrv.reportPromptHierarchySteps.loading$()
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(isLoading => this.loadingEmitter.emit({ promptId: this.biReportPrompt.id, isLoading }));

        this.requestCtrl.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(request => {
                if (this.form.invalid) return;

                const responses = this.responsesCtrl.value
                    .filter(response => response.answers.length);
                const response = responses[responses.length - 1];
                request.push({
                    prompt_id: this.biReportPrompt.id,
                    answers: response?.answers.map(value => value.id) ?? [],
                    hierarchy_step: response.id
                });
                this.requestCtrl.setValue(request, { emitEvent: false });
            });
    }
}
