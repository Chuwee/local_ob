import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { BiService, PostBiReportAnswerRequest } from '@admin-clients/cpanel/bi/data-access';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { Platform } from '@angular/cdk/platform';
import { AsyncPipe } from '@angular/common';
import {
    ChangeDetectionStrategy, Component, DestroyRef, inject, OnDestroy, OnInit, QueryList, ViewChildren
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import {
    BehaviorSubject, combineLatest, first, firstValueFrom, map, Observable, throwError
} from 'rxjs';
import { filter, finalize, switchMap, tap } from 'rxjs/operators';
import { BI_SUBMIT } from '../bi-reports.routes';
import { VmBiReportPrompt } from '../models/vm-reports.model';
import { BiReportPromptsDateComponent } from './date/bi-report-prompts-date.component';
import { BiReportPromptsDateRangeComponent } from './date-range/bi-report-prompts-date-range.component';
import { BiReportPromptsHierarchyComponent } from './hierarchy/bi-report-prompts-hierarchy.component';
import { BiReportPromptsSelectionComponent } from './selection/bi-report-prompts-selection.component';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        ReactiveFormsModule, FormContainerComponent, MaterialModule, FlexModule,
        BiReportPromptsDateRangeComponent, BiReportPromptsSelectionComponent, BiReportPromptsHierarchyComponent,
        BiReportPromptsDateComponent, AsyncPipe
    ],
    selector: 'app-bi-report-prompts',
    templateUrl: './bi-report-prompts.component.html'
})
export class BiReportPromptsComponent implements OnInit, OnDestroy {
    readonly #biSrv = inject(BiService);
    readonly #auth = inject(AuthenticationService);
    readonly #destroyRef = inject(DestroyRef);
    readonly #platform = inject(Platform);
    readonly #biSubmit = inject(BI_SUBMIT);

    @ViewChildren(MatExpansionPanel)
    private readonly _matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    readonly #loadingPromptsBS = new BehaviorSubject<Record<string, boolean>>({});

    readonly requestCtrl = inject(FormBuilder).nonNullable.control(null as PostBiReportAnswerRequest);
    readonly form = inject(FormBuilder).group({});
    readonly vmBiReportPromptsBS = new BehaviorSubject<VmBiReportPrompt[]>([]);
    readonly biReport$ = this.#biSrv.report.get$();
    readonly isInProgress$ = booleanOrMerge([
        this.#biSrv.report.loading$(),
        this.#biSrv.reportPrompts.loading$(),
        this.#biSrv.reportAnswer.loading$(),
        this.#loadingPromptsBS.pipe(map(loadingPrompts => Object.keys(loadingPrompts).some(promptId => loadingPrompts[promptId]))),
        this.vmBiReportPromptsBS.pipe(map(biReportPrompts =>
            biReportPrompts.filter(biReportPrompt => biReportPrompt.canRender).length !== biReportPrompts.length))
    ]);

    ngOnInit(): void {
        this.#biSrv.reportPrompts.get$()
            .pipe(filter(Boolean), takeUntilDestroyed(this.#destroyRef))
            .subscribe(reportPrompts => {
                if (reportPrompts.length === 0) {
                    this.save();
                    return;
                }

                let initiated = false;
                let canRender = false;
                const vmReportPrompts = reportPrompts.map(reportPrompt => {
                    if (reportPrompt.type === 'ELEMENT' || reportPrompt.type === 'OBJECT' || reportPrompt.type === 'HIERARCHY') {
                        canRender = !initiated;
                        initiated = true;
                        return {
                            ...reportPrompt,
                            canRender
                        } as VmBiReportPrompt;
                    } else {
                        return {
                            ...reportPrompt,
                            canRender: true
                        } as VmBiReportPrompt;
                    }
                });
                this.vmBiReportPromptsBS.next(vmReportPrompts);
            });

        this.#loadingPromptsBS
            .pipe(
                filter(loadingPrompts =>
                    !!Object.keys(loadingPrompts).length && !Object.keys(loadingPrompts).some(promptId => loadingPrompts[promptId])),
                takeUntilDestroyed(this.#destroyRef))
            .subscribe(() => {
                let initiated = false;
                const vmReportPrompts = this.vmBiReportPromptsBS.value.map(reportPrompt => {
                    if (!reportPrompt.canRender && !initiated) {
                        initiated = true;
                        return {
                            ...reportPrompt,
                            canRender: true
                        } as VmBiReportPrompt;
                    } else {
                        return reportPrompt;
                    }
                });
                this.vmBiReportPromptsBS.next(vmReportPrompts);
            });
    }

    ngOnDestroy(): void {
        this.#biSrv.reportPrompts.clear();
        this.#biSrv.report.clear();
    }

    save(): void {
        this.save$().subscribe();
    }

    save$(): Observable<unknown> {
        this.requestCtrl.setValue([]);
        if (this.form.valid) {
            return combineLatest([this.#biSrv.report.get$(), this.#auth.getLoggedUser$()])
                .pipe(
                    first(),
                    switchMap(([biReport, user]) =>
                        this.#auth.impersonation.get$()
                            .pipe(
                                first(),
                                switchMap(impersonation =>
                                    this.#biSrv.reportAnswer.post(biReport.id, this.requestCtrl.value, impersonation)
                                        .pipe(
                                            tap(response => {
                                                this.#biSubmit(response.url, user.reports.load, user.reports.logout, this.#platform);
                                            }),
                                            finalize(() => {
                                                this.requestCtrl.reset(null, { emitEvent: false });
                                            })
                                        )
                                )
                            )
                    )
                );
        } else {
            this.form.markAllAsTouched();
            this.form.setValue(this.form.getRawValue());
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanelQueryList);
            this.requestCtrl.reset(null, { emitEvent: false });
            return throwError(() => 'invalid form');
        }
    }

    async cancel(): Promise<void> {
        const { id } = await firstValueFrom((this.#biSrv.report.get$()));
        this.form.markAsUntouched();
        this.form.markAsPristine();
        this.#loadingPromptsBS.next({});
        this.vmBiReportPromptsBS.next([]);
        this.#auth.impersonation.get$()
            .pipe(first())
            .subscribe(impersonation => {
                this.#biSrv.report.load(id, { impersonation });
                this.#biSrv.reportPrompts.load(id, { impersonation });
            });
    }

    loadingEmitHandler(loading: { promptId: string; isLoading: boolean }): void {
        this.#loadingPromptsBS.value[loading.promptId] = loading.isLoading;
        this.#loadingPromptsBS.next(this.#loadingPromptsBS.value);
    }
}
