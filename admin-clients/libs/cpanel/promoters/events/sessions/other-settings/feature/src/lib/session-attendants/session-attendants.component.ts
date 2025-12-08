import { EventChannelsService } from '@admin-clients/cpanel/promoters/events/channels/data-access';
import { EventChannelsScopeType, EventAvetConnection, EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import {
    EventSessionsService, SessionAttendantsStatus, SessionAttendantsTickets
} from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { EventType } from '@admin-clients/shared/common/data-access';
import { SearchablePaginatedSelectionModule } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { IdName, PageableFilter } from '@admin-clients/shared/data-access/models';
import { animate, state, style, transition, trigger } from '@angular/animations';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, Input, OnDestroy, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import {
    AbstractControl, FormBuilder, ReactiveFormsModule, UntypedFormGroup, Validators
} from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { debounceTime, firstValueFrom, Subject, switchMap } from 'rxjs';
import { filter, first, map, shareReplay, takeUntil } from 'rxjs/operators';

const PAGESIZE = 10;

@Component({
    selector: 'app-session-attendants',
    changeDetection: ChangeDetectionStrategy.OnPush,
    styleUrls: ['./session-attendants.component.scss'],
    templateUrl: './session-attendants.component.html',
    animations: [
        trigger('expandSelection', [
            state('expanded', style({ height: '*' })),
            state('collapsed', style({ height: '0' })),
            transition('expanded <=> collapsed', [animate('0.1s')])
        ])
    ],
    imports: [
        ReactiveFormsModule, FlexLayoutModule, CommonModule,
        TranslatePipe, MaterialModule, SearchablePaginatedSelectionModule
    ]
})
export class SessionAttendantsComponent implements OnInit, OnDestroy {
    private readonly _onDestroy = new Subject<void>();

    private readonly _ref = inject(ChangeDetectorRef);
    private readonly _fb = inject(FormBuilder);

    private readonly _eventsSrv = inject(EventsService);
    private readonly _sessionsService = inject(EventSessionsService);
    private readonly _eventChannelsService = inject(EventChannelsService);

    readonly pageSize = PAGESIZE;

    readonly channelList$ = this._eventChannelsService.eventChannelsList.getData$()
        .pipe(
            map(eventChannels => eventChannels?.map(eventChannel => eventChannel.channel)),
            shareReplay({ refCount: true, bufferSize: 1 })
        );

    readonly metadata$ = this._eventChannelsService.eventChannelsList.getMetaData$();
    readonly isLoading$ = this._eventChannelsService.eventChannelsList.inProgress$();

    readonly attendantsChannelsScopeType = EventChannelsScopeType;
    readonly isAvetSocket$ = this._eventsSrv.event.get$()
        .pipe(
            first(Boolean),
            map(event => event.type === EventType.avet && event.additional_config?.avet_config === EventAvetConnection.socket)
        );

    readonly comForm = this._fb.group({
        inheritAttendantsFromEvent: null as boolean,
        attendantsTickets: this._fb.group({
            status: null as boolean,
            channelsScope: this._fb.group({
                type: [null as EventChannelsScopeType, [Validators.required]],
                channels: [null as IdName[], [Validators.required]],
                addNewEventChannelRelationships: null as boolean
            }),
            autofill: null as boolean,
            editAutofill: null as boolean
        })
    });

    readonly channelsControl = this.comForm.controls.attendantsTickets.controls.channelsScope.controls.channels;

    @Input()
    set form(value: UntypedFormGroup) {
        value.addControl('attendants', this.comForm, { emitEvent: false });
    }

    ngOnInit(): void {
        this.attendantsFormChangeHandler();
        this.updateAttendantsForm();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    markForCheck(): void {
        this._ref.markForCheck();
    }

    async filterChangeHandler(filters: Partial<PageableFilter>): Promise<void> {
        const event = await firstValueFrom(this._eventsSrv.event.get$());
        this._eventChannelsService.eventChannelsList.load(event.id, { limit: this.pageSize, ...filters });
    }

    getValue(): SessionAttendantsTickets {
        const value = this.comForm.getRawValue();
        return this.comForm.value.inheritAttendantsFromEvent && { status: SessionAttendantsStatus.eventConfig }
            || {
            status: value.attendantsTickets.status ? SessionAttendantsStatus.active : SessionAttendantsStatus.disabled,
            autofill: value.attendantsTickets.autofill,
            edit_autofill: value.attendantsTickets.editAutofill,
            channels_scope: value.attendantsTickets.status ?
                {
                    type: value.attendantsTickets.channelsScope.type,
                    channels: value.attendantsTickets.channelsScope.type === EventChannelsScopeType.list ?
                        value.attendantsTickets.channelsScope.channels?.map(channel => channel.id)
                        : undefined,
                    add_new_event_channel_relationships:
                        value.attendantsTickets.channelsScope.type === EventChannelsScopeType.list ?
                            !!value.attendantsTickets.channelsScope.addNewEventChannelRelationships
                            : undefined
                }
                : undefined
        };
    }

    private updateAttendantsForm(): void {
        this.channelList$
            .pipe(
                first(Boolean),
                switchMap(() => this._sessionsService.session.get$()),
                filter(Boolean),
                takeUntil(this._onDestroy)
            )
            .subscribe(session => {
                this.comForm.reset({
                    inheritAttendantsFromEvent: session.settings.attendant_tickets?.status === SessionAttendantsStatus.eventConfig,
                    attendantsTickets: {
                        status: session.settings.attendant_tickets?.status === SessionAttendantsStatus.active,
                        channelsScope: {
                            type: session.settings.attendant_tickets?.channels_scope?.type || EventChannelsScopeType.all,
                            channels: session.settings.attendant_tickets?.channels_scope?.channels || [],
                            addNewEventChannelRelationships:
                                !!session.settings.attendant_tickets?.channels_scope?.add_new_event_channel_relationships
                        },
                        autofill: !!session.settings.attendant_tickets?.autofill,
                        editAutofill: !!session.settings.attendant_tickets?.edit_autofill
                    }
                });
            });
    }

    private attendantsFormChangeHandler(): void {
        const changeEnabled = (control: AbstractControl, enable: boolean): void => {
            if (enable && control.disabled) {
                control.enable();
            } else if (!enable && control.enabled) {
                control.disable();
            }
        };
        this.comForm.valueChanges
            .pipe(
                debounceTime(0),
                takeUntil(this._onDestroy)
            )
            .subscribe(value => {
                changeEnabled(this.comForm.controls.attendantsTickets, !value.inheritAttendantsFromEvent);
                changeEnabled(this.comForm.controls.attendantsTickets.controls.channelsScope, value.attendantsTickets?.status);
                changeEnabled(
                    this.comForm.controls.attendantsTickets.controls.channelsScope.controls.addNewEventChannelRelationships,
                    value.attendantsTickets?.channelsScope?.type === EventChannelsScopeType.list
                );
                changeEnabled(
                    this.comForm.controls.attendantsTickets.controls.channelsScope.controls.channels,
                    value.attendantsTickets?.channelsScope?.type === EventChannelsScopeType.list
                );
                changeEnabled(this.comForm.controls.attendantsTickets.controls.autofill, value.attendantsTickets?.status);
                changeEnabled(this.comForm.controls.attendantsTickets.controls.editAutofill, value.attendantsTickets?.autofill);
            });
    }
}
