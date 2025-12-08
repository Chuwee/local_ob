import { BaseStateProp } from '@admin-clients/shared/utility/state';
import { Injectable } from '@angular/core';
import { GetNotificationsResponse } from '../models/get-notifications-response.model';
import { GetTotalRecipientsResponse } from '../models/get-total-recipients-response.model';
import { NotificationContent } from '../models/notification-content.model';
import { NotificationDetail } from '../models/notification-detail.model';

@Injectable({
    providedIn: 'root'
})
export class NotificationsState {
    private readonly _notificationsList = new BaseStateProp<GetNotificationsResponse>();
    readonly getNotificationsList$ = this._notificationsList.getValueFunction();
    readonly setNotificationsList = this._notificationsList.setValueFunction();
    readonly isNotificationsListLoading$ = this._notificationsList.getInProgressFunction();
    readonly setNotificationsListLoading = this._notificationsList.setInProgressFunction();

    private readonly _notificationSave = new BaseStateProp<void>();
    readonly isNotificationSaving$ = this._notificationSave.getInProgressFunction();
    readonly setNotificationSaving = this._notificationSave.setInProgressFunction();

    private readonly _notificationDetail = new BaseStateProp<NotificationDetail>();
    readonly getNotificationDetail$ = this._notificationDetail.getValueFunction();
    readonly setNotificationDetail = this._notificationDetail.setValueFunction();
    readonly getNotificationDetailError$ = this._notificationDetail.getErrorFunction();
    readonly setNotificationDetailError = this._notificationDetail.setErrorFunction();
    readonly isNotificationDetailLoading$ = this._notificationDetail.getInProgressFunction();
    readonly setNotificationDetailLoading = this._notificationDetail.setInProgressFunction();

    private readonly _notificationContents = new BaseStateProp<NotificationContent[]>();
    readonly getNotificationContents$ = this._notificationContents.getValueFunction();
    readonly setNotificationContents = this._notificationContents.setValueFunction();
    readonly isNotificationContentsLoading$ = this._notificationContents.getInProgressFunction();
    readonly setNotificationContentsLoading = this._notificationContents.setInProgressFunction();

    private readonly _notificationContentsSave = new BaseStateProp<void>();
    readonly isNotificationContentsSaving$ = this._notificationContentsSave.getInProgressFunction();
    readonly setNotificationContentsSaving = this._notificationContentsSave.setInProgressFunction();

    private readonly _numberOfRecipients = new BaseStateProp<GetTotalRecipientsResponse>();
    readonly getNotificationRecipients$ = this._numberOfRecipients.getValueFunction();
    readonly setNumberOfRecipients = this._numberOfRecipients.setValueFunction();
    readonly isNumberOfRecipientsLoading$ = this._numberOfRecipients.getInProgressFunction();
    readonly setNumberOfRecipientsLoading = this._numberOfRecipients.setInProgressFunction();

    private _exportRecipients = new BaseStateProp<void>();
    readonly isExportRecipientsLoading$ = this._exportRecipients.getInProgressFunction();
    readonly setExportRecipientsLoading = this._exportRecipients.setInProgressFunction();

    private readonly _notificationSend = new BaseStateProp<void>();
    readonly isNotificationSendInProgress$ = this._notificationSend.getInProgressFunction();
    readonly setNotificationSendInProgress = this._notificationSend.setInProgressFunction();

    private readonly _testNotificationSend = new BaseStateProp<void>();
    readonly isTestNotificationSendInProgress$ = this._testNotificationSend.getInProgressFunction();
    readonly setTestNotificationSendInProgress = this._testNotificationSend.setInProgressFunction();
}
