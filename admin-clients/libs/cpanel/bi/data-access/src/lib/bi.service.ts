import { getListData, getMetadata, mapMetadata, StateManager } from '@OneboxTM/utils-state';
import { inject, Injectable } from '@angular/core';
import { catchError, first, map, Observable, of, switchMap } from 'rxjs';
import { BiApi } from './api/bi.api';
import { BiImpersonation } from './models/bi-impersonation.model';
import {
    BiReportPromptAnswer,
    BiReportPromptHierarchyStepAnswer,
    GetBiReportPromptAnswersRequest,
    GetBiReportPromptHierarchyRequest,
    GetBiReportsHistoryRequest,
    GetBiReportsRequest,
    PostBiReportAnswerRequest
} from './models/bi-reports.model';
import type { BiUser, BiUsersRequest, PutBiUser } from './models/bi-users.model';
import { BiState } from './state/bi.state';

@Injectable({ providedIn: 'root' })
export class BiService {
    readonly #api = inject(BiApi);
    readonly #state = inject(BiState);

    readonly reportsList = Object.freeze({
        load: (request: GetBiReportsRequest = {}) => StateManager.load(
            this.#state.reportsList,
            this.#api.getReports(request)
        ),
        get$: () => this.#state.reportsList.getValue$(),
        error$: () => this.#state.reportsList.getError$(),
        loading$: () => this.#state.reportsList.isInProgress$(),
        clear: () => this.#state.reportsList.setValue(null)
    });

    readonly reportsSearch = Object.freeze({
        load: (request: GetBiReportsRequest = {}) => StateManager.load(
            this.#state.reportsSearch,
            this.#api.getReports(request)
        ),
        get$: () => this.#state.reportsSearch.getValue$(),
        error$: () => this.#state.reportsSearch.getError$(),
        loading$: () => this.#state.reportsSearch.isInProgress$(),
        clear: () => this.#state.reportsSearch.setValue(null)
    });

    readonly reportsHistoryList = Object.freeze({
        load: (request: GetBiReportsHistoryRequest = {}) => StateManager.load(
            this.#state.reportsHistoryList,
            this.#api.getReportsHistory(request)
        ),
        get$: () => this.#state.reportsHistoryList.getValue$(),
        error$: () => this.#state.reportsHistoryList.getError$(),
        loading$: () => this.#state.reportsHistoryList.isInProgress$(),
        clear: () => this.#state.reportsHistoryList.setValue(null)
    });

    readonly report = Object.freeze({
        load: (id: string, request: BiImpersonation) => StateManager.load(
            this.#state.report,
            this.#api.getReport(id, request)
        ),
        get$: () => this.#state.report.getValue$(),
        error$: () => this.#state.report.getError$(),
        loading$: () => this.#state.report.isInProgress$(),
        clear: () => this.#state.report.setValue(null)
    });

    readonly reportPrompts = Object.freeze({
        load: (id: string, request: BiImpersonation) => StateManager.load(
            this.#state.reportPrompts,
            this.#api.getReportPrompts(id, request)
        ),
        get$: () => this.#state.reportPrompts.getValue$(),
        error$: () => this.#state.reportPrompts.getError$(),
        loading$: () => this.#state.reportPrompts.isInProgress$(),
        clear: () => this.#state.reportPrompts.setValue(null)
    });

    readonly reportPromptAnswers = Object.freeze({
        load: (reportId: string, promptId: string, request?: GetBiReportPromptAnswersRequest) => StateManager.load(
            this.#state.reportPromptAnswers,
            this.#api.getReportPromptAnswers(reportId, promptId, request)
        ),
        get$: () => this.#state.reportPromptAnswers.getValue$(),
        loading$: () => this.#state.reportPromptAnswers.isInProgress$(),
        loadMore: (reportId: string, promptId: string, request: GetBiReportPromptAnswersRequest, nextPage = false) => {
            const currentObservable$ = this.#state.moreReportPromptAnswers.getValue$();
            let result: Observable<BiReportPromptAnswer[]>;
            if (!nextPage) {
                result = this.#api.getReportPromptAnswers(reportId, promptId, request).pipe(catchError(() => of(null)));
            } else {
                result = currentObservable$
                    .pipe(
                        first(),
                        switchMap(response => this.#api.getReportPromptAnswers(reportId, promptId, request)
                            .pipe(
                                map(nextResponse => response.concat(nextResponse))
                            ))
                    );
            }
            StateManager.load(this.#state.moreReportPromptAnswers, result);
        },
        getMore$: () => this.#state.moreReportPromptAnswers.getValue$(),
        setMore: (answers: BiReportPromptAnswer[]) => this.#state.moreReportPromptAnswers.setValue(answers)
    });

    readonly reportPromptHierarchySteps = Object.freeze({
        load: (reportId: string, promptId: string, request: BiImpersonation) => StateManager.load(
            this.#state.reportPromptHierarchySteps,
            this.#api.getReportPromptHierarchySteps(reportId, promptId, request)
        ),
        get$: () => this.#state.reportPromptHierarchySteps.getValue$(),
        loading$: () => this.#state.reportPromptHierarchySteps.isInProgress$()
    });

    readonly reportPromptHierarchyStepAnswers = Object.freeze({
        loadMore: (reportId: string, promptId: string, stepId: string, request: GetBiReportPromptHierarchyRequest, nextPage = false) => {
            const currentObservable$ = this.#state.moreReportPromptHierarchyStepAnswers.getValue$();
            let result: Observable<BiReportPromptHierarchyStepAnswer[]>;
            if (!nextPage) {
                result = this.#api.getReportPromptHierarchyStepAnswers(reportId, promptId, stepId, request)
                    .pipe(catchError(() => of(null)));
            } else {
                result = currentObservable$
                    .pipe(
                        first(),
                        switchMap(response => this.#api.getReportPromptHierarchyStepAnswers(reportId, promptId, stepId, request)
                            .pipe(
                                map(nextResponse => response.concat(nextResponse))
                            ))
                    );
            }
            StateManager.load(this.#state.moreReportPromptHierarchyStepAnswers, result);
        },
        cancelMore: () => this.#state.moreReportPromptHierarchyStepAnswers.triggerCancellation(),
        getMore$: () => this.#state.moreReportPromptHierarchyStepAnswers.getValue$(),
        loadingMore$: () => this.#state.moreReportPromptHierarchyStepAnswers.isInProgress$(),
        clearMore: () => this.#state.moreReportPromptHierarchyStepAnswers.setValue(null)
    });

    readonly reportAnswer = Object.freeze({
        post: (reportId: string, body: PostBiReportAnswerRequest, impersonation?: string) => StateManager.inProgress(
            this.#state.reportAnswers,
            this.#api.postReportAnswers(reportId, body, impersonation)
        ),
        loading$: () => this.#state.reportAnswers.isInProgress$()
    });

    readonly subscriptions = Object.freeze({
        load: (request: BiImpersonation = {}): void => StateManager.load(
            this.#state.subscriptions,
            this.#api.getSubscriptions(request)
        ),
        get$: () => this.#state.subscriptions.getValue$(),
        error$: () => this.#state.subscriptions.getError$(),
        loading$: () => this.#state.subscriptions.isInProgress$(),
        clear: () => this.#state.subscriptions.setValue(null)
    });

    readonly subscription = Object.freeze({
        delete: (id: string, impersonation?: string) => StateManager.inProgress(
            this.#state.detail,
            this.#api.deleteSubscription(id, impersonation)
        ),
        send: (id: string, impersonation?: string) => StateManager.inProgress(
            this.#state.detail,
            this.#api.sendSubscription(id, impersonation)
        ),
        loading$: () => this.#state.detail.isInProgress$()
    });

    readonly subscriptionsLink = Object.freeze({
        load: (impersonation?: string) => StateManager.load(
            this.#state.subscriptionsLink,
            this.#api.getSubscriptionsLink(impersonation)
        ),
        get$: () => this.#state.subscriptionsLink.getValue$(),
        error$: () => this.#state.subscriptionsLink.getError$(),
        loading$: () => this.#state.subscriptionsLink.isInProgress$(),
        clear: () => this.#state.subscriptionsLink.setValue(null)
    });

    readonly usersList = Object.freeze({
        load: (request?: BiUsersRequest) => StateManager.load(
            this.#state.usersSupersetList,
            this.#api.getUsers(request).pipe(mapMetadata())
        ),
        getData$: () => this.#state.usersSupersetList.getValue$().pipe(getListData()),
        getMetadata$: () => this.#state.usersSupersetList.getValue$().pipe(getMetadata()),
        error$: () => this.#state.usersSupersetList.getError$(),
        loading$: () => this.#state.usersSupersetList.isInProgress$(),
        clear: () => this.#state.usersSupersetList.setValue(null),
        create: (user: BiUser) => StateManager.inProgress(
            this.#state.usersSupersetList,
            this.#api.postUser(user)
        ),
        regeneratePassword: (id: number) => StateManager.inProgress(
            this.#state.usersSupersetList,
            this.#api.postUserPassword(id)
        ),
        delete: (id: number) => StateManager.inProgress(
            this.#state.usersSupersetList,
            this.#api.deleteUser(id)
        )
    });

    readonly userDetails = Object.freeze({
        load: (id: number) => StateManager.load(
            this.#state.userSupersetDetails,
            this.#api.getUser(id)
        ),
        get$: () => this.#state.userSupersetDetails.getValue$(),
        error$: () => this.#state.userSupersetDetails.getError$(),
        loading$: () => this.#state.userSupersetDetails.isInProgress$(),
        update: (id: number, body: PutBiUser) => StateManager.inProgress(
            this.#state.userSupersetDetails,
            this.#api.putUser(id, body)
        )
    });
}
