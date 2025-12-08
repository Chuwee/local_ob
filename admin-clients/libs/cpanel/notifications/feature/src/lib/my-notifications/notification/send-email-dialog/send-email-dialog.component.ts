import {
    NotificationsService,
    NotificationSessionsScope,
    NotificationChannelsScope
} from '@admin-clients/cpanel/notifications/data-access';
import { eventChannelsProviders, EventChannelsService } from '@admin-clients/cpanel/promoters/events/channels/data-access';
import { Event, EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { EventSessionsService } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { NgIf, NgFor, AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FlexModule } from '@angular/flex-layout/flex';
import { UntypedFormGroup } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, map, Observable, Subject } from 'rxjs';

@Component({
    selector: 'app-send-email-dialog',
    templateUrl: './send-email-dialog.component.html',
    styleUrls: ['./send-email-dialog.component.scss'],
    providers: [
        eventChannelsProviders
    ],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [FlexModule, MaterialModule, NgIf, NgFor, TranslatePipe, AsyncPipe]
})
export class SendEmailDialogComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();

    form: UntypedFormGroup;
    isInProgress$: Observable<boolean>;
    isExportLoading$: Observable<boolean>;
    totalRecipients$: Observable<number>;
    event$: Observable<Event>;
    subject: string;
    notCode: string;
    sessionNames$: Observable<string[]>;
    channelNames$: Observable<string[]>;
    isLoading$: Observable<boolean>;

    constructor(
        private _dialogRef: MatDialogRef<SendEmailDialogComponent>,
        private _notificationsService: NotificationsService,
        private _eventsService: EventsService,
        private _eventSessionsService: EventSessionsService,
        private _eventChannelService: EventChannelsService,
        @Inject(MAT_DIALOG_DATA) private _data: {
            eventId: string;
            totalRecipients$: Observable<number>;
            subject: string;
            notCode: string;
            sessions;
            channels;
        }
    ) {
        this._dialogRef.addPanelClass(DialogSize.LARGE);
        this.totalRecipients$ = _data.totalRecipients$.pipe(map(recipients => recipients));
        this.subject = _data.subject;
        this.notCode = _data.notCode;

        this._eventsService.event.load(_data.eventId);
        const eventId = Number(_data.eventId);

        let sessionIds = [];
        if (_data.sessions.type === NotificationSessionsScope.restricted) {
            sessionIds = _data.sessions.selected.map(session => session.id);
        }
        let channelIds = [];
        if (_data.channels.type === NotificationChannelsScope.restricted) {
            channelIds = _data.channels.selected.map(channel => channel.id);
        }

        this.setSessionNames(eventId, sessionIds);
        this.setChannelNames(eventId, channelIds);
    }

    ngOnInit(): void {
        this.isLoading$ = booleanOrMerge([
            this._eventSessionsService.sessionList.inProgress$(),
            this._eventChannelService.eventChannelsList.inProgress$()
        ]);

        this.event$ = this._eventsService.event.get$().pipe(map(event => event));
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    close(): void {
        this._dialogRef.close();
    }

    submit(): void {
        this._notificationsService.sendNotification(this.notCode).subscribe(() => {
            this._dialogRef.close(true);
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
