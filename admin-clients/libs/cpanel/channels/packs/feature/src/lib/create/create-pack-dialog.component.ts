import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { Metadata } from '@OneboxTM/utils-state';
import { CreatePackRequest, PacksService } from '@admin-clients/cpanel/channels/packs/data-access';
import { Event, eventsProviders } from '@admin-clients/cpanel/promoters/events/data-access';
import {
    eventSessionsProviders, EventSessionsService, GetSessionsRequest, SessionsFilterFields,
    SessionStatus
} from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import {
    SalesRequestsEventStatus, SalesRequestsService, SalesRequestsStatus
} from '@admin-clients/cpanel-channels-sales-requests-data-access';
import {
    DialogSize, EphemeralMessageService, SelectSearchComponent, SelectServerSearchComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { VenueTemplatesService, VenueTemplateStatus } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { filter, map, tap, throwError } from 'rxjs';

@Component({
    selector: 'app-create-pack-dialog',
    templateUrl: './create-pack-dialog.component.html',
    styleUrls: ['./create-pack-dialog.component.scss'],
    imports: [
        AsyncPipe, MaterialModule, TranslatePipe, ReactiveFormsModule, FormControlErrorsComponent, FlexLayoutModule,
        SelectServerSearchComponent, DateTimePipe, SelectSearchComponent
    ],
    providers: [eventsProviders, eventSessionsProviders],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CreatePackDialogComponent implements OnInit {
    readonly #packsSrv = inject(PacksService);
    readonly #dialogRef = inject(MatDialogRef<CreatePackDialogComponent>);
    readonly #fb = inject(UntypedFormBuilder);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);
    readonly #onDestroy = inject(DestroyRef);
    readonly #eventSessionsService = inject(EventSessionsService);
    readonly #venueTemplatesSrv = inject(VenueTemplatesService);
    readonly #salesRequestSrv = inject(SalesRequestsService);
    readonly #PAGE_LIMIT = 20;
    readonly #translate = inject(TranslateService);

    readonly data = inject<{ channelId: number; entityId: number; packId?: number }>(MAT_DIALOG_DATA);

    readonly form = this.#fb.group({
        name: [null as string, [Validators.required, Validators.maxLength(50)]],
        type: [null as 'AUTOMATIC' | 'MANUAL', [Validators.required]],
        event: [{ value: null as Event, disabled: true }, [Validators.required]],
        session: [{ value: null as number, disabled: true }, [Validators.required]],
        venueTpl: [{ value: null as number, disabled: true }]
    });

    readonly creationTypes = {
        automatic: 'AUTOMATIC',
        manual: 'MANUAL'
    };

    readonly events$ = this.#salesRequestSrv.getSalesRequestsListData$().pipe(
        filter(Boolean),
        map(salesRequests => salesRequests.map(salesRequest => salesRequest.event))
    );

    readonly sessions$ = this.#eventSessionsService.sessionList.get$().pipe(
        filter(Boolean),
        map(sessions => [
            { id: 'ALL', name: this.#translate.instant('CHANNELS.PACK.ALL_SESSIONS') },
            ...sessions.data
        ]),
        tap(sessions => {
            if (sessions.length === 1) {
                this.form.controls['session'].setValue(sessions[0]);
            }
        })
    );

    readonly venueTemplates$ = this.#venueTemplatesSrv.getVenueTemplatesList$().pipe(
        filter(Boolean),
        map(value => value.data),
        tap(vtpls => {
            if (vtpls.length === 1) {
                this.form.controls['venueTpl'].setValue(vtpls[0].id);
                this.form.controls['venueTpl'].disable();
            }
        }));

    readonly isLoading$ = booleanOrMerge([
        this.#salesRequestSrv.isSalesRequestsListLoading$(),
        this.#packsSrv.pack.loading$()
    ]);

    readonly moreEventsAvailable$ = this.#salesRequestSrv.getSalesRequestsListMetadata$()
        .pipe(map((metadata: Metadata) => metadata?.offset + metadata?.limit < metadata?.total));

    readonly moreSessionsAvailable$ = this.#eventSessionsService.sessionList.getMetadata$()
        .pipe(map((metadata: Metadata) => metadata?.offset + metadata?.limit < metadata?.total));

    readonly dateTimeFormats = DateTimeFormats;

    constructor() {
        this.#dialogRef.addPanelClass(DialogSize.MEDIUM);
        this.#dialogRef.disableClose = false;
    }

    ngOnInit(): void {
        this.#eventSessionsService.sessionList.clear();

        this.form.controls['type'].valueChanges
            .pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe(type => {
                if (type === this.creationTypes.automatic) {
                    this.form.controls['event'].enable({ emitEvent: false });
                    this.form.controls['event'].markAsPristine();
                    this.form.controls['event'].markAsUntouched();
                    this.#salesRequestSrv.clearSalesRequestsList();
                    this.#salesRequestSrv.loadSalesRequestsList({
                        limit: 20, offset: 0, status: [SalesRequestsStatus.accepted], channel: this.data.channelId, sort: 'name:asc',
                        event_status: [SalesRequestsEventStatus.ready, SalesRequestsEventStatus.planned,
                        SalesRequestsEventStatus.inProgramming]
                    });
                } else if (type === this.creationTypes.manual) {
                    this.form.controls['event'].disable({ emitEvent: false });
                    this.form.controls['session'].disable({ emitEvent: false });
                    this.form.controls['venueTpl'].disable({ emitEvent: false });
                }
            });

        this.form.controls['event'].valueChanges
            .pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe(event => {
                this.form.controls['session'].reset({ value: null, disabled: false });
                this.form.controls['venueTpl'].reset({ value: null, disabled: true });
                this.#eventSessionsService.sessionList.load(event.id,
                    {
                        fields: [SessionsFilterFields.name, SessionsFilterFields.venueTemplateId,
                        SessionsFilterFields.startDate, SessionsFilterFields.settingsSmartbookingStatus,
                        SessionsFilterFields.settingsSmartbookingRelatedSession, SessionsFilterFields.venueTplType],
                        status: [SessionStatus.preview, SessionStatus.ready, SessionStatus.scheduled]
                    }
                );
            });

        this.form.controls['session'].valueChanges
            .pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe(() => {
                this.form.controls['venueTpl'].reset({ value: null, disabled: false });
                this.#venueTemplatesSrv.loadVenueTemplatesList({
                    limit: 999, offset: 0, entityId: this.form.controls['event'].value.entity.id,
                    eventId: this.form.controls['event'].value.id, sort: 'name:asc', status: [VenueTemplateStatus.active]
                });
            });
    }

    setItemType(): void {
        if (this.form.controls['session'].value === 0) {
            this.form.controls['venueTpl'].addValidators([Validators.required]);
        }
    }

    createSessionPack(): void {
        if (this.form.valid) {
            const mainItem: any = {
                item_id: this.form.controls['session'].value?.id !== 'ALL' ? this.form.controls['session'].value?.id
                    : this.form.controls['event'].value?.id,
                type: this.form.controls['venueTpl'].value ? 'EVENT' : 'SESSION'
            };

            if (mainItem.type === 'EVENT') {
                mainItem.venue_template_id = this.form.controls['venueTpl'].value;
            }

            const req: CreatePackRequest = {
                name: this.form.controls['name'].value,
                type: this.form.controls['type'].value,
                main_item: mainItem
            };

            this.#packsSrv.pack.create(this.data.channelId, req)
                .subscribe(pack => {
                    this.#ephemeralMessageService.showSuccess({ msgKey: 'CHANNELS.CREATE_PACK_SUCCESS' });
                    this.close(pack.id);
                });
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            throwError(() => 'invalid form');
        }
    }

    close(packId: number = null): void {
        this.#dialogRef.close(packId);
    }

    loadEvents(q: string, next = false): void {
        this.#salesRequestSrv.loadServerSearchSalesRequestList({
            limit: this.#PAGE_LIMIT,
            sort: 'name:asc',
            status: [SalesRequestsStatus.accepted],
            channel: this.data.channelId,
            event_status: [SalesRequestsEventStatus.ready, SalesRequestsEventStatus.planned, SalesRequestsEventStatus.inProgramming],
            q
        }, next);
    }

    loadSessions(q: string, next = false): void {
        const request: GetSessionsRequest = {
            limit: this.#PAGE_LIMIT,
            sort: 'name:asc',
            fields: [SessionsFilterFields.name, SessionsFilterFields.venueTemplateId,
            SessionsFilterFields.startDate, SessionsFilterFields.settingsSmartbookingStatus,
            SessionsFilterFields.settingsSmartbookingRelatedSession, SessionsFilterFields.venueTplType],
            status: [SessionStatus.preview, SessionStatus.ready, SessionStatus.scheduled],
            q
        };
        if (!next) {
            this.#eventSessionsService.sessionList.load(this.form.controls['event'].value.id, request);
        } else {
            this.#eventSessionsService.sessionList.loadMore(this.form.controls['event'].value.id, request);
        }
    }
}
