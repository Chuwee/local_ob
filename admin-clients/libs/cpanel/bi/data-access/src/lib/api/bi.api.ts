import { buildHttpParams } from '@OneboxTM/utils-http';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BiImpersonation } from '../models/bi-impersonation.model';
import {
    BiHistoryReport, BiReport, BiReportPrompt, BiReportPromptAnswer, BiReportPromptHierarchyStepAnswer, GetBiReportPromptAnswersRequest,
    GetBiReportPromptHierarchyRequest, GetBiReportsHistoryRequest, GetBiReportsRequest, PostBiReportAnswerRequest,
    PostBiReportAnswerResponse
} from '../models/bi-reports.model';
import {
    BiContactsList, BiSubscription, BiSubscriptionDetail, BiSubscriptionFormatType, BiSubscriptionRecipient, BiSubscriptionSchedule,
    GetBiSubscriptionsLinkResponse, PostBiContact, PostBiSubscriptionRecipients, PutBiSubscription
} from '../models/bi-subscriptions.model';
import { BiUser, BiUsersList, BiUsersRequest, PutBiUser } from '../models/bi-users.model';

@Injectable({ providedIn: 'root' })
export class BiApi {
    private readonly BASE_API = inject(APP_BASE_API);
    private readonly BI_API = `${this.BASE_API}/bi-api/v1`;
    private readonly REPORTS_SEGMENT = '/reports';
    private readonly HISTORY_SEGMENT = '/recent';
    private readonly PROMPTS = '/prompts';
    private readonly SUBSCRIPTIONS_SEGMENT = '/subscriptions';
    private readonly CONTACTS_SEGMENT = '/contacts';
    private readonly IMPERSONATION = 'ob-impersonation';
    private readonly USERS_SEGMENT = '/users/bi-embedded';
    private readonly _http = inject(HttpClient);

    getReports(request: GetBiReportsRequest = {}): Observable<BiReport[]> {
        const options = this.getOptions(request);
        return this._http.get<BiReport[]>(`${this.BI_API}${this.REPORTS_SEGMENT}`, options);
    }

    getReportsHistory(request: GetBiReportsHistoryRequest): Observable<BiHistoryReport[]> {
        const options = this.getOptions(request);
        return this._http.get<BiHistoryReport[]>(`${this.BI_API}${this.REPORTS_SEGMENT}${this.HISTORY_SEGMENT}`, options);
    }

    getReport(id: string, request: BiImpersonation): Observable<BiReport> {
        const options = this.getOptions(request);
        return this._http.get<BiReport>(`${this.BI_API}${this.REPORTS_SEGMENT}/${id}`, options);
    }

    getReportPrompts(id: string, request: BiImpersonation): Observable<BiReportPrompt[]> {
        const options = this.getOptions(request);
        return this._http.get<BiReportPrompt[]>(`${this.BI_API}${this.REPORTS_SEGMENT}/${id}${this.PROMPTS}`, options);
    }

    getReportPromptAnswers(
        reportId: string,
        promptId: string,
        request: GetBiReportPromptAnswersRequest = {}
    ): Observable<BiReportPromptAnswer[]> {
        const options = this.getOptions(request);
        return this._http.get<BiReportPromptAnswer[]>(
            `${this.BI_API}${this.REPORTS_SEGMENT}/${reportId}${this.PROMPTS}/${promptId}/answers`,
            options
        );
    }

    getReportPromptHierarchySteps(reportId: string, promptId: string, request: BiImpersonation): Observable<unknown> {
        const options = this.getOptions(request);
        return this._http.get<BiReportPromptAnswer[]>(
            `${this.BI_API}${this.REPORTS_SEGMENT}/${reportId}${this.PROMPTS}/${promptId}/hierarchy/steps`, options);
    }

    getReportPromptHierarchyStepAnswers(
        reportId: string,
        promptId: string,
        stepId: string,
        request: GetBiReportPromptHierarchyRequest = {}
    ): Observable<BiReportPromptHierarchyStepAnswer[]> {
        const { previous, ...rest } = request;
        const previousSteps = previous.reduce((acc, value) => {
            acc[value.step] = value.answers.join(', ');
            return acc;
        }, {});
        const options = this.getOptions({ ...rest, ...previousSteps });
        return this._http.get<BiReportPromptHierarchyStepAnswer[]>(
            `${this.BI_API}${this.REPORTS_SEGMENT}/${reportId}${this.PROMPTS}/${promptId}/hierarchy/steps/${stepId}/answers`,
            options
        );
    }

    postReportAnswers(reportId: string, body: PostBiReportAnswerRequest, impersonation: string): Observable<PostBiReportAnswerResponse> {
        const options = this.getOptions({ impersonation });
        return this._http.post<PostBiReportAnswerResponse>(
            `${this.BI_API}${this.REPORTS_SEGMENT}/${reportId}${this.PROMPTS}/answer`, body, options);
    }

    getSubscriptions(request: BiImpersonation): Observable<BiSubscription[]> {
        const options = this.getOptions(request);
        return this._http.get<BiSubscription[]>(`${this.BI_API}${this.SUBSCRIPTIONS_SEGMENT}`, options);
    }

    getSubscription(id: string, request: BiImpersonation): Observable<BiSubscriptionDetail> {
        const options = this.getOptions(request);
        return this._http.get<BiSubscriptionDetail>(`${this.BI_API}${this.SUBSCRIPTIONS_SEGMENT}/${id}`, options);
    }

    putSubscription(id: string, body: PutBiSubscription, impersonation?: string): Observable<void> {
        const options = this.getOptions({ impersonation });
        return this._http.put<void>(`${this.BI_API}${this.SUBSCRIPTIONS_SEGMENT}/${id}`, body, options);
    }

    sendSubscription(id: string, impersonation?: string): Observable<void> {
        const options = this.getOptions({ impersonation });
        return this._http.post<void>(`${this.BI_API}${this.SUBSCRIPTIONS_SEGMENT}/${id}/send`, null, options);
    }

    deleteSubscription(id: string, impersonation?: string): Observable<void> {
        const options = this.getOptions({ impersonation });
        return this._http.delete<void>(`${this.BI_API}${this.SUBSCRIPTIONS_SEGMENT}/${id}`, options);
    }

    getSubscriptionRecipients(id: string, request: BiImpersonation): Observable<BiSubscriptionRecipient[]> {
        const options = this.getOptions(request);
        return this._http.get<BiSubscriptionRecipient[]>(`${this.BI_API}${this.SUBSCRIPTIONS_SEGMENT}/${id}/recipients`, options);
    }

    postRecipients(id: string, body: PostBiSubscriptionRecipients, impersonation?: string): Observable<void> {
        const options = this.getOptions({ impersonation });
        return this._http.post<void>(`${this.BI_API}${this.SUBSCRIPTIONS_SEGMENT}/${id}/recipients`, body, options);
    }

    getSubscriptionsFormats(request: BiImpersonation): Observable<BiSubscriptionFormatType[]> {
        const options = this.getOptions(request);
        return this._http.get<BiSubscriptionFormatType[]>(`${this.BI_API}${this.SUBSCRIPTIONS_SEGMENT}/formats`, options);
    }

    getSubscriptionsSchedules(request: BiImpersonation): Observable<BiSubscriptionSchedule[]> {
        const options = this.getOptions(request);
        return this._http.get<BiSubscriptionSchedule[]>(`${this.BI_API}${this.SUBSCRIPTIONS_SEGMENT}/schedules`, options);
    }

    getContacts(request: PageableFilter & BiImpersonation): Observable<BiContactsList> {
        const options = this.getOptions(request);
        return this._http.get<BiContactsList>(`${this.BI_API}${this.CONTACTS_SEGMENT}`, options);
    }

    postContact(body: PostBiContact, impersonation?: string): Observable<void> {
        const options = this.getOptions({ impersonation });
        return this._http.post<void>(`${this.BI_API}${this.CONTACTS_SEGMENT}`, body, options);
    }

    deleteContact(id: string, impersonation?: string): Observable<void> {
        const options = this.getOptions({ impersonation });
        return this._http.delete<void>(`${this.BI_API}${this.CONTACTS_SEGMENT}/${id}`, options);
    }

    getSubscriptionsLink(impersonation?: string): Observable<GetBiSubscriptionsLinkResponse> {
        const options = this.getOptions({ impersonation });
        return this._http.get<GetBiSubscriptionsLinkResponse>(`${this.BI_API}${this.SUBSCRIPTIONS_SEGMENT}/link`, options);
    }

    getUsers(request?: BiUsersRequest): Observable<BiUsersList> {
        const params = buildHttpParams({ ...request });
        return this._http.get<BiUsersList>(`${this.BI_API}${this.USERS_SEGMENT}`, { params });
    }

    getUser(id: number): Observable<BiUser> {
        return this._http.get<BiUser>(`${this.BI_API}${this.USERS_SEGMENT}/${id}`);
    }

    postUser(user: BiUser): Observable<void> {
        return this._http.post<void>(`${this.BI_API}${this.USERS_SEGMENT}`, user);
    }

    putUser(id: number, body: PutBiUser): Observable<void> {
        return this._http.put<void>(`${this.BI_API}${this.USERS_SEGMENT}/${id}`, body);
    }

    deleteUser(id: number): Observable<void> {
        return this._http.delete<void>(`${this.BI_API}${this.USERS_SEGMENT}/${id}`);
    }

    postUserPassword(id: number): Observable<void> {
        return this._http.post<void>(`${this.BI_API}${this.USERS_SEGMENT}/${id}/regenerate-password`, null);
    }

    private getOptions<T extends BiImpersonation>(request: T): unknown {
        const { impersonation, ...otherParams } = request;
        const params = buildHttpParams(otherParams);
        if (impersonation) {
            return { params, headers: new HttpHeaders().set(this.IMPERSONATION, impersonation.toString()) };
        } else {
            return { params };
        }
    }
}
