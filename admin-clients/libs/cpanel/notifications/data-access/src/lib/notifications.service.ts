import { getListData, getMetadata, mapMetadata, Metadata } from '@OneboxTM/utils-state';
import { AggregatedData, ExportRequest, ExportResponse } from '@admin-clients/shared/data-access/models';
import { HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, finalize, map, Observable, of } from 'rxjs';
import { NotificationsApi } from './api/notifications.api';
import { aggDataNotifications } from './constants/notifications-aggregated-data';
import { GetNotificationsRequest } from './models/get-notifications-request.model';
import { GetRecipientsRequest } from './models/get-recipients-request.model';
import { GetTotalRecipientsRequest } from './models/get-total-recipients-request.model';
import { NotificationContent } from './models/notification-content.model';
import { NotificationDetail } from './models/notification-detail.model';
import { NotificationEmails } from './models/notification-emails.model';
import { PostNotification } from './models/post-notification.model';
import { PutNotificationEmail } from './models/put-notification.model';
import { NotificationsState } from './state/notifications.state';

@Injectable({
    providedIn: 'root'
})
export class NotificationsService {

    constructor(
        private _notificationsApi: NotificationsApi,
        private _notificationsState: NotificationsState
    ) {
    }

    loadNotificationsList(request: GetNotificationsRequest): void {
        this._notificationsState.setNotificationsListLoading(true);
        this._notificationsApi.getNotifications(request)
            .pipe(
                mapMetadata(),
                catchError(() => of(null)),
                finalize(() => this._notificationsState.setNotificationsListLoading(false))
            )
            .subscribe(notifications =>
                this._notificationsState.setNotificationsList(notifications)
            );
    }

    getNotificationsListData$(): Observable<NotificationEmails[]> {
        return this._notificationsState.getNotificationsList$().pipe(getListData());
    }

    getNotificationsListMetadata$(): Observable<Metadata> {
        return this._notificationsState.getNotificationsList$().pipe(getMetadata());
    }

    getNotificationsListAggregatedData$(): Observable<AggregatedData> {
        return this._notificationsState.getNotificationsList$()
            .pipe(
                map(notifications =>
                    notifications?.aggregated_data && new AggregatedData(notifications.aggregated_data, aggDataNotifications)
                ));
    }

    isNotificationsListLoading$(): Observable<boolean> {
        return this._notificationsState.isNotificationsListLoading$();
    }

    createNotification(notification: PostNotification): Observable<{ code: string }> {
        this._notificationsState.setNotificationSaving(true);
        return this._notificationsApi.postNotification(notification)
            .pipe(finalize(() => this._notificationsState.setNotificationSaving(false)));
    }

    loadNotificationDetail(code: string): void {
        this._notificationsState.setNotificationDetailLoading(true);
        this._notificationsState.setNotificationDetailError(null);
        this._notificationsApi.getNotification(code)
            .pipe(
                catchError(error => {
                    this._notificationsState.setNotificationDetailError(error);
                    return of(null);
                }),
                finalize(() => this._notificationsState.setNotificationDetailLoading(false))
            )
            .subscribe(notificationDetail => {
                this._notificationsState.setNotificationDetail(notificationDetail);
            });
    }

    clearNotificationDetail(): void {
        this._notificationsState.setNotificationDetail(null);
    }

    getNotificationDetail$(): Observable<NotificationDetail> {
        return this._notificationsState.getNotificationDetail$();
    }

    getNotificationDetailError$(): Observable<HttpErrorResponse> {
        return this._notificationsState.getNotificationDetailError$();
    }

    isNotificationDetailsLoading$(): Observable<boolean> {
        return this._notificationsState.isNotificationDetailLoading$();
    }

    deleteNotification(code: string): Observable<void> {
        return this._notificationsApi.deleteNotification(code);
    }

    isNotificationSaving$(): Observable<boolean> {
        return this._notificationsState.isNotificationSaving$();
    }

    updateNotification(notification: PutNotificationEmail): Observable<void> {
        this._notificationsState.setNotificationSaving(true);
        return this._notificationsApi.putNotification(notification.code, notification)
            .pipe(finalize(() => this._notificationsState.setNotificationSaving(false)));
    }

    loadNotificationContents(code: string): void {
        this._notificationsState.setNotificationContentsLoading(true);
        this._notificationsApi.getNotificationContents(code)
            .pipe(
                catchError(() => of(null)),
                finalize(() => this._notificationsState.setNotificationContentsLoading(false))
            )
            .subscribe(notificationContents => this._notificationsState.setNotificationContents(notificationContents));
    }

    getNotificationContents$(): Observable<NotificationContent[]> {
        return this._notificationsState.getNotificationContents$();
    }

    isNotificationContentsLoading$(): Observable<boolean> {
        return this._notificationsState.isNotificationContentsLoading$();
    }

    updateNotificationContents(code: string, notificationContents: NotificationContent[]): Observable<void> {
        this._notificationsState.setNotificationContentsSaving(true);
        return this._notificationsApi.putNotificationContents(code, notificationContents)
            .pipe(finalize(() => this._notificationsState.setNotificationContentsSaving(false)));
    }

    loadNumberOfRecipients(request: GetTotalRecipientsRequest): void {
        this._notificationsState.setNumberOfRecipientsLoading(true);
        this._notificationsApi.getNotificationRecipients(request)
            .pipe(
                mapMetadata(),
                finalize(() => this._notificationsState.setNumberOfRecipientsLoading(false))
            )
            .subscribe(recipients => this._notificationsState.setNumberOfRecipients(recipients));
    }

    getTotalRecipients$(): Observable<Metadata> {
        return this._notificationsState.getNotificationRecipients$().pipe(getMetadata());
    }

    isTotalRecipientsLoading$(): Observable<boolean> {
        return this._notificationsState.isNumberOfRecipientsLoading$();
    }

    clearTotalRecipients(): void {
        this._notificationsState.setNumberOfRecipients(null);
    }

    exportRecipients(filter: GetRecipientsRequest, exportRequest: ExportRequest, notificationCode?: string): Observable<ExportResponse> {
        this._notificationsState.setExportRecipientsLoading(true);
        notificationCode = exportRequest.fields.findIndex(item => item.field === 'status') !== -1 ? notificationCode : undefined;
        return this._notificationsApi.exportRecipients(filter, exportRequest.format, exportRequest.fields, exportRequest.delivery,
            notificationCode)
            .pipe(finalize(() => this._notificationsState.setExportRecipientsLoading(false)));
    }

    isExportRecipientsLoading$(): Observable<boolean> {
        return this._notificationsState.isExportRecipientsLoading$();
    }

    sendNotification(code: string): Observable<void> {
        this._notificationsState.setNotificationSendInProgress(true);
        return this._notificationsApi.sendNotification(code)
            .pipe(finalize(() => this._notificationsState.setNotificationSendInProgress(false)));
    }

    isNotificationSendInProgress$(): Observable<boolean> {
        return this._notificationsState.isNotificationSendInProgress$();
    }

    sendTestNotification(code: string, emails: string[]): Observable<void> {
        this._notificationsState.setTestNotificationSendInProgress(true);
        return this._notificationsApi.sendTestNotification(code, emails)
            .pipe(finalize(() => this._notificationsState.setTestNotificationSendInProgress(false)));
    }

    isTestNotificationSendInProgress$(): Observable<boolean> {
        return this._notificationsState.isTestNotificationSendInProgress$();
    }
}
