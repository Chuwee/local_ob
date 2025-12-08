import { buildHttpParams } from '@OneboxTM/utils-http';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { ExportDelivery, ExportField, ExportFormat, ExportResponse } from '@admin-clients/shared/data-access/models';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { GetNotificationsRequest } from '../models/get-notifications-request.model';
import { GetNotificationsResponse } from '../models/get-notifications-response.model';
import { GetRecipientsRequest } from '../models/get-recipients-request.model';
import { GetTotalRecipientsRequest } from '../models/get-total-recipients-request.model';
import { GetTotalRecipientsResponse } from '../models/get-total-recipients-response.model';
import { NotificationContent } from '../models/notification-content.model';
import { NotificationDetail } from '../models/notification-detail.model';
import { PostNotification } from '../models/post-notification.model';
import { PutNotificationEmail } from '../models/put-notification.model';

@Injectable({
    providedIn: 'root'
})
export class NotificationsApi {

    private readonly BASE_API = inject(APP_BASE_API);
    private readonly BASE_NOTIFICATIONS_URL = `${this.BASE_API}/customers-mgmt-api/v1/notifications`;
    private readonly RECIPIENTS_NOTIFICATIONS_URL = `${this.BASE_API}/customers-mgmt-api/v1/notification-recipients`;

    private readonly _http = inject(HttpClient);

    getNotifications(request: GetNotificationsRequest): Observable<GetNotificationsResponse> {
        const params = buildHttpParams(request);
        return this._http.get<GetNotificationsResponse>(this.BASE_NOTIFICATIONS_URL, { params });
    }

    getNotification(code: string): Observable<NotificationDetail> {
        return this._http.get<NotificationDetail>(`${this.BASE_NOTIFICATIONS_URL}/${code}`);
    }

    postNotification(notification: PostNotification): Observable<{ code: string }> {
        return this._http.post<{ code: string }>(this.BASE_NOTIFICATIONS_URL, notification);
    }

    putNotification(code: string, notification: PutNotificationEmail): Observable<void> {
        return this._http.put<void>(`${this.BASE_NOTIFICATIONS_URL}/${code}`, notification);
    }

    deleteNotification(code: string): Observable<void> {
        return this._http.delete<void>(`${this.BASE_NOTIFICATIONS_URL}/${code}`, {});
    }

    getNotificationContents(code: string): Observable<NotificationContent[]> {
        return this._http.get<NotificationContent[]>(`${this.BASE_NOTIFICATIONS_URL}/${code}/contents`);
    }

    putNotificationContents(code: string, request: NotificationContent[]): Observable<void> {
        return this._http.put<void>(`${this.BASE_NOTIFICATIONS_URL}/${code}/contents`, request);
    }

    getNotificationRecipients(req: GetTotalRecipientsRequest): Observable<GetTotalRecipientsResponse> {
        const params = buildHttpParams(req);
        return this._http.get<GetTotalRecipientsResponse>(this.RECIPIENTS_NOTIFICATIONS_URL, { params });
    }

    exportRecipients(
        filter: GetRecipientsRequest,
        format: ExportFormat,
        fields: ExportField[],
        delivery: ExportDelivery,
        notificationCode?: string
    ): Observable<ExportResponse> {
        return this._http.post<ExportResponse>(this.RECIPIENTS_NOTIFICATIONS_URL + '/exports', {
            notification_code: notificationCode, filter, format, fields, delivery
        }, {});
    }

    sendNotification(code: string): Observable<void> {
        return this._http.post<void>(`${this.BASE_NOTIFICATIONS_URL}/${code}/send`, null);
    }

    sendTestNotification(code: string, emails: string[]): Observable<void> {
        return this._http.post<void>(`${this.BASE_NOTIFICATIONS_URL}/${code}/send-preview`, { recipients: emails });
    }
}
