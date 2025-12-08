import {
    NotificationDetail, NotificationStatus,
    NotificationsService, exportDataRecipients
} from '@admin-clients/cpanel/notifications/data-access';
import { eventChannelsProviders, EventChannelsService } from '@admin-clients/cpanel/promoters/events/channels/data-access';
import { Event, EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { EventSessionsService } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { TableColConfigService } from '@admin-clients/shared/common/data-access';
import { EphemeralMessageService, ExportDialogComponent, ObMatDialogConfig } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { DateTimeFormats, ExportFormat } from '@admin-clients/shared/data-access/models';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { NgIf, NgFor, AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit, inject } from '@angular/core';
import { FlexModule } from '@angular/flex-layout/flex';
import { MatDialog } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, map, Observable, Subject, takeUntil, tap } from 'rxjs';

@Component({
    selector: 'app-notification-summary',
    templateUrl: './notification-summary.component.html',
    styleUrls: ['./notification-summary.component.scss'],
    providers: [
        eventChannelsProviders
    ],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [FlexModule, NgIf, NgFor, MaterialModule, TranslatePipe, AsyncPipe, DateTimePipe]
})
export class NotificationSummaryComponent implements OnInit, OnDestroy {
    private _tableSrv = inject(TableColConfigService);

    private _onDestroy = new Subject<void>();

    readonly dateTimeFormats = DateTimeFormats;

    @Input() notificationDetail$: Observable<NotificationDetail>;
    @Input() subject: string;

    notificationDetail: NotificationDetail;
    isExportLoading$: Observable<boolean>;
    event$: Observable<Event>;
    sessionNames$: Observable<string[]>;
    channelNames$: Observable<string[]>;
    notificationStatus = NotificationStatus;
    isLoading$: Observable<boolean>;

    constructor(
        private _notificationsService: NotificationsService,
        private _ephemeralMessageService: EphemeralMessageService,
        private _eventsService: EventsService,
        private _dialog: MatDialog,
        private _eventSessionsService: EventSessionsService,
        private _eventChannelService: EventChannelsService
    ) { }

    ngOnInit(): void {
        this.isLoading$ = booleanOrMerge([
            this._notificationsService.isNotificationDetailsLoading$(),
            this._eventSessionsService.sessionList.inProgress$(),
            this._eventChannelService.eventChannelsList.inProgress$()
        ]);

        this.notificationDetail$.pipe(
            filter(notification => !!notification),
            takeUntil(this._onDestroy))
            .subscribe(notificationDetail => {
                this.notificationDetail = notificationDetail;
                if (notificationDetail.recipients?.filter?.event_ids.length && (notificationDetail.status === this.notificationStatus.sent
                    || notificationDetail.status === this.notificationStatus.inProgress)) {
                    this._eventsService.event.load(notificationDetail.recipients?.filter?.event_ids[0].toString());
                    this.event$ = this._eventsService.event.get$().pipe(
                        filter(event => !!event),
                        tap(event => {
                            this.setSessionNames(event.id, notificationDetail.recipients?.filter?.session_ids);
                            this.setChannelNames(event.id, notificationDetail.recipients?.filter?.channel_ids);
                        }));
                }
            });

        this.isExportLoading$ = this._notificationsService.isExportRecipientsLoading$();

    }

    ngOnDestroy(): void {
        this._notificationsService.clearNotificationDetail();
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    exportRecipients(): void {
        const config = new ObMatDialogConfig({
            exportData: exportDataRecipients,
            exportFormat: ExportFormat.csv,
            selectedFields: this._tableSrv.getColumns('EXP_NOTIFICATIONS_SUMMARY')
        });
        this._dialog.open(ExportDialogComponent, config)
            .beforeClosed()
            .pipe(filter(Boolean))
            .subscribe(exportList => {
                const req = {
                    entity_id: this.notificationDetail.entity.id,
                    event_id: this.notificationDetail.recipients?.filter?.event_ids,
                    session_id: this.notificationDetail.recipients?.filter?.session_ids,
                    channel_id: this.notificationDetail.recipients?.filter?.channel_ids,
                    purchase_from: this.notificationDetail.recipients?.filter?.purchase_date?.from,
                    purchase_to: this.notificationDetail.recipients?.filter?.purchase_date?.to,
                    exclude_comercial_mailing_not_allowed: this.notificationDetail.recipients?.filter
                        ?.exclude_commercial_mailing_not_allowed
                };

                this._tableSrv.setColumns('EXP_NOTIFICATIONS_SUMMARY', exportList.fields.map(resultData => resultData.field));
                this._notificationsService.exportRecipients(req, exportList, this.notificationDetail.code)
                    .pipe(filter(result => !!result.export_id))
                    .subscribe(() => {
                        this._ephemeralMessageService.showSuccess({ msgKey: 'ACTIONS.EXPORT.OK.MESSAGE' });
                    });
            });
    }

    setSessionNames(eventId: number, sessions: number[]): void {
        if (sessions) {
            let sessionNames: string[];
            const req = {
                filterByIds: sessions
            };
            this._eventSessionsService.sessionList.load(eventId, req);
            this.sessionNames$ = this._eventSessionsService.sessionList.get$()
                .pipe(
                    filter(Boolean),
                    map(sessions => {
                        sessionNames = sessions.data.map(session => session.name);
                        return sessionNames;
                    }));
        }
    }

    setChannelNames(eventId: number, channelIds: number[]): void {
        if (channelIds) {
            let channelNames: string[];
            this._eventChannelService.eventChannelsList.load(eventId, {
                limit: 999,
                offset: 0,
                sort: 'name:asc'
            });
            this.channelNames$ = this._eventChannelService.eventChannelsList.getData$()
                .pipe(
                    filter(channels => !!channels),
                    map(channels => {
                        const filteredChannels = channels.filter(channel => channelIds.find(channelId => channelId === channel.channel.id));
                        channelNames = filteredChannels.map(channel => channel.channel.name);
                        if (!channelNames.length) {
                            channelNames = channels.map(channel => channel.channel.name);
                        }
                        return channelNames;
                    }));
        }
    }

}
