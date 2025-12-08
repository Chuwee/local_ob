import { Metadata } from '@OneboxTM/utils-state';
import { EventChannelsService } from '@admin-clients/cpanel/promoters/events/channels/data-access';
import { EventSessionPackConf, EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import {
    EventSessionsService, Session, SessionsFilterFields, SessionType
} from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import {
    DialogSize, EphemeralMessageService, MessageDialogService, ObMatDialogConfig, SearchTableComponent
} from '@admin-clients/shared/common/ui/components';
import { ImageRestrictions } from '@admin-clients/shared/data-access/models';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, effect, inject, input } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule } from '@angular/forms';
import { MatIconButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatMenu, MatMenuItem, MatMenuTrigger } from '@angular/material/menu';
import { MatCell, MatCellDef, MatColumnDef, MatHeaderCell, MatHeaderCellDef } from '@angular/material/table';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, filter, map, Observable, switchMap } from 'rxjs';
import { EventChannelImageUploaderModalComponent, ImageOrigin } from '../image-uploader-modal/event-channel-image-uploader-modal.component';

export const eventChannelImageRestrictions: ImageRestrictions = { width: 800, height: 800, size: 92160 };

@Component({
    selector: 'app-event-channel-session-images',
    templateUrl: './event-channel-session-images.component.html',
    styleUrls: ['./event-channel-session-images.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        ReactiveFormsModule, SearchTableComponent, MatHeaderCell, MatHeaderCellDef, MatCell,
        MatColumnDef, MatCellDef, MatTooltip, MatIcon, MatIconButton, TranslatePipe, DatePipe,
        MatMenu, MatMenuItem, MatMenuTrigger
    ]
})

export class EventChannelSessionImagesComponent {
    readonly #dialog = inject(MatDialog);
    readonly #sessionsSrv = inject(EventSessionsService);
    readonly #eventChannelsSrv = inject(EventChannelsService);
    readonly #breakpointObserver = inject(BreakpointObserver);
    readonly #msgDialogService = inject(MessageDialogService);
    readonly #ephemeralMsgSrv = inject(EphemeralMessageService);
    readonly #eventService = inject(EventsService);

    readonly $eventId = input<number>(null, { alias: 'eventId' });
    readonly $eventChannelId = input<number>(null, { alias: 'eventChannelId' });

    readonly $isHandsetOrTablet = toSignal(this.#breakpointObserver
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(map(result => result.matches)));

    readonly $isSessionPack = toSignal(this.#eventService.event.get$().pipe(
        filter(Boolean),
        map(event => event.settings.session_pack === EventSessionPackConf.restricted ||
            event.settings.session_pack === EventSessionPackConf.unrestricted
        )
    ));

    readonly columns = ['name', 'start_date', 'image_origin', 'actions'];

    sessions$: Observable<Session[]>;
    metadata$: Observable<Metadata>;

    constructor() {
        this.#sessionsSrv.sessionList.clear();

        effect(() => {
            const eventId = this.$eventId();
            if (eventId) this.#loadSessionList();
        });

        this.sessions$ = combineLatest([
            this.#sessionsSrv.sessionList.getData$().pipe(filter(Boolean)),
            this.#eventChannelsSrv.eventSessionSquareImagesConfig.get$().pipe(filter(Boolean))
        ]).pipe(
            map(([sessions, config]) => sessions.map(session => ({
                id: session.id,
                name: session.name,
                start_date: session.start_date,
                image_origin: config?.find(s => s.session_id === session.id)?.image_origin as ImageOrigin
            })))
        );
    }

    uploadOrEditSessionImages(session: Session): void {
        this.#dialog.open(
            EventChannelImageUploaderModalComponent,
            new ObMatDialogConfig(
                { session, eventId: this.$eventId(), eventChannelId: this.$eventChannelId() },
                undefined,
                DialogSize.LATERAL
            )
        ).afterClosed()
            .subscribe(result => {
                if (!result) return;
                this.#eventChannelsSrv.eventSessionSquareImagesConfig.load(this.$eventId(), this.$eventChannelId());
            });
    }

    resetImagesForSession(session: Session): void {
        this.#msgDialogService.showWarn({
            size: DialogSize.MEDIUM,
            title: 'EVENTS.CHANNEL.SQUARE_SESSION_IMAGES.RESET_TITLE',
            message: 'EVENTS.CHANNEL.SQUARE_SESSION_IMAGES.RESET_MESSAGE',
            actionLabel: 'EVENTS.CHANNEL.SQUARE_SESSION_IMAGES.RESET',
            showCancelButton: true
        })
            .pipe(
                filter(Boolean),
                switchMap(() => this.#eventChannelsSrv.eventSessionSquareImages.deleteAll(
                    this.$eventId(), this.$eventChannelId(), session.id
                ))
            )
            .subscribe(() => {
                this.#loadSessionList();
                this.#ephemeralMsgSrv.showDeleteSuccess();
            });
    }

    #loadSessionList(): void {
        this.#sessionsSrv.sessionList.load(this.$eventId(), {
            limit: 999,
            sort: 'start_date:asc',
            fields: [SessionsFilterFields.id, SessionsFilterFields.startDate, SessionsFilterFields.name],
            ...(this.$isSessionPack() && {
                type: [SessionType.restrictedPack, SessionType.unrestrictedPack]
            })
        });

        this.#eventChannelsSrv.eventSessionSquareImagesConfig.clear();
        this.#eventChannelsSrv.eventSessionSquareImagesConfig.load(this.$eventId(), this.$eventChannelId());
    }
}
