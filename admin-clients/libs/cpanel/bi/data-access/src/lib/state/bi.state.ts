import { StateProperty } from '@OneboxTM/utils-state';
import { Injectable } from '@angular/core';
import {
    BiHistoryReport, BiReport, BiReportPrompt, BiReportPromptAnswer, BiReportPromptHierarchyStep, BiReportPromptHierarchyStepAnswer,
    PostBiReportAnswerResponse
} from '../models/bi-reports.model';
import {
    BiContactsList, BiSubscription, BiSubscriptionDetail, BiSubscriptionFormatType, BiSubscriptionRecipient, GetBiSubscriptionsLinkResponse
} from '../models/bi-subscriptions.model';
import type { BiUser, BiUsersList } from '../models/bi-users.model';

@Injectable({ providedIn: 'root' })
export class BiState {
    readonly reportsList = new StateProperty<BiReport[]>();
    readonly reportsSearch = new StateProperty<BiReport[]>();
    readonly reportsHistoryList = new StateProperty<BiHistoryReport[]>();
    readonly report = new StateProperty<BiReport>();
    readonly reportPrompts = new StateProperty<BiReportPrompt[]>();
    readonly reportPromptAnswers = new StateProperty<BiReportPromptAnswer[]>();
    readonly moreReportPromptAnswers = new StateProperty<BiReportPromptAnswer[]>();
    readonly reportPromptHierarchySteps = new StateProperty<BiReportPromptHierarchyStep[]>();
    readonly moreReportPromptHierarchyStepAnswers = new StateProperty<BiReportPromptHierarchyStepAnswer[]>();
    readonly reportAnswers = new StateProperty<PostBiReportAnswerResponse>();
    readonly subscriptions = new StateProperty<BiSubscription[]>();
    readonly subscription = new StateProperty<BiSubscription>();
    readonly detail = new StateProperty<BiSubscriptionDetail>();
    readonly recipients = new StateProperty<BiSubscriptionRecipient[]>();
    readonly schedules = new StateProperty<{ id: string; name: string }[]>();
    readonly formats = new StateProperty<BiSubscriptionFormatType[]>();
    readonly contacts = new StateProperty<BiContactsList>();
    readonly allContacts = new StateProperty<BiContactsList>();
    readonly subscriptionsLink = new StateProperty<GetBiSubscriptionsLinkResponse>();
    readonly usersSupersetList = new StateProperty<BiUsersList>();
    readonly userSupersetDetails = new StateProperty<BiUser>();
}
