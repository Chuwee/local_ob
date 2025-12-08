import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { Channel } from '@admin-clients/cpanel/channels/data-access';
import { EventChannelsService, eventChannelsProviders } from '@admin-clients/cpanel/promoters/events/channels/data-access';
import {
    EventChannelsScopeType, AttendantsStatus, Event, EventAttendantField, EventsService, PutEvent
} from '@admin-clients/cpanel/promoters/events/data-access';
import { ArchivedEventMgrComponent } from '@admin-clients/cpanel/promoters/events/feature';
import {
    EphemeralMessageService, SearchablePaginatedSelectionLoadEvent, SearchablePaginatedSelectionModule
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { atLeastOneOfSelectedField, booleanOrMerge, FormControlHandler } from '@admin-clients/shared/utility/utils';
import { animate, state, style, transition, trigger } from '@angular/animations';
import { AsyncPipe, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit, QueryList, ViewChildren } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, UntypedFormControl, Validators } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, concat, Observable, switchMap, throwError } from 'rxjs';
import { debounceTime, filter, map, shareReplay, take, tap, toArray } from 'rxjs/operators';
import { EventAttendantsFieldsComponent } from './fields/attendants-fields.component';
import { EventAttendantsBlockEditSectorsComponent } from './sectors/attendants-block-edit-sectors.component';

interface FormData {
    status: boolean;
    settings: {
        edit_attendant: boolean;
        autofill: boolean;
        disallow_autofill_edition: boolean;
        edit_autofill_disallowed_sectors: number[];
        channelsScope: {
            type: EventChannelsScopeType;
            channels: Channel[];
            addNewEventChannelRelationships: boolean;
        };
    };
    fields: EventAttendantField[];
}

@Component({
    imports: [
        NgIf, AsyncPipe,
        FlexLayoutModule,
        ReactiveFormsModule,
        MaterialModule,
        TranslatePipe,
        FormContainerComponent,
        SearchablePaginatedSelectionModule,
        EventAttendantsFieldsComponent,
        ArchivedEventMgrComponent,
        EventAttendantsBlockEditSectorsComponent
    ],
    selector: 'app-event-attendants',
    templateUrl: './attendants.component.html',
    styleUrls: ['./attendants.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [
        eventChannelsProviders
    ],
    animations: [
        trigger('expandChannelSelection', [
            state('expanded', style({ height: '*' })),
            state('collapsed', style({ height: '0' })),
            state('*', style({ height: '0' })),
            transition('expanded <=> collapsed', [animate('0.1s')])
        ])
    ]
})
export class EventAttendantsComponent implements OnInit, WritingComponent {
    private readonly _destroyRef = inject(DestroyRef);
    private readonly _fb = inject(FormBuilder);

    private readonly _eventsService = inject(EventsService);
    private readonly _eventChannelsService = inject(EventChannelsService);
    private readonly _ephemeralMessage = inject(EphemeralMessageService);

    readonly attendantsChannelsScopeType = EventChannelsScopeType;

    @ViewChildren(MatExpansionPanel) matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    readonly form = this._fb.group({
        status: false,
        settings: this._fb.group({
            edit_attendant: null as boolean,
            autofill: { value: null as boolean, disabled: true },
            disallow_autofill_edition: { value: null as boolean, disabled: true },
            disallow_all_sectors: null as boolean,
            edit_autofill_disallowed_sectors: [{ value: null as number[], disabled: true }, Validators.required],
            channelsScope: this._fb.group({
                type: [null as EventChannelsScopeType],
                channels: [[] as Channel[], Validators.required],
                addNewEventChannelRelationships: null as boolean
            })
        }),
        fields: [null as EventAttendantField[], [Validators.required, atLeastOneOfSelectedField('mandatory')]]
    });

    readonly isLoadingOrSaving$ = booleanOrMerge([
        this._eventsService.eventAttendantFields.loading$(),
        this._eventsService.event.inProgress$()
    ]);

    readonly isAvet$ = this._eventsService.event.get$().pipe(
        map(event => EventsService.isAvet(event)),
        shareReplay({ bufferSize: 1, refCount: true })
    );

    readonly channelsListPageSize = 10;
    readonly channelsMetadata$ = this._eventChannelsService.eventChannelsList.getMetaData$();
    readonly channelsLoading$ = this._eventChannelsService.eventChannelsList.inProgress$();
    readonly channels$ = this._eventChannelsService.eventChannelsList.getData$()
        .pipe(
            filter(Boolean),
            map(eventChannels => eventChannels.map(ec => ec.channel))
        );

    get channelsControl(): UntypedFormControl {
        return this.form.controls.settings.controls.channelsScope.controls.channels;
    }

    ngOnInit(): void {
        this._eventsService.event.get$()
            .pipe(filter(Boolean), take(1))
            .subscribe(event => {
                this._eventChannelsService.eventChannelsList.load(event.id, { limit: 0 });
                this._eventsService.eventAttendantFields.load(event.id);
                if (EventsService.isAvet(event)) {
                    this.form.controls.settings.controls.autofill.enable();
                }
            });

        this.form.controls.status.valueChanges
            .pipe(takeUntilDestroyed(this._destroyRef))
            .subscribe(status => {
                if (status) {
                    this.form.controls.settings.controls.channelsScope.enable({ onlySelf: true });
                    this.form.controls.settings.controls.edit_attendant.enable({ onlySelf: true });
                    this.form.controls.fields.enable({ onlySelf: true });
                    if (this.isAvet$) this.form.controls.settings.controls.autofill.enable();
                } else {
                    this.form.controls.settings.disable();
                    this.form.controls.fields.disable();
                }
            });

        this.form.controls.settings.controls.channelsScope.controls.type.valueChanges
            .pipe(
                debounceTime(0),
                takeUntilDestroyed(this._destroyRef)
            )
            .subscribe(type => {
                if (this.form.value.status && type === EventChannelsScopeType.list) {
                    this.form.controls.settings.controls.channelsScope.controls.channels.enable();
                    this.form.controls.settings.controls.channelsScope.controls.addNewEventChannelRelationships.enable();
                } else {
                    this.form.controls.settings.controls.channelsScope.controls.channels.disable();
                    this.form.controls.settings.controls.channelsScope.controls.addNewEventChannelRelationships.disable();
                }
            });

        this.form.controls.settings.controls.autofill.valueChanges
            .pipe(takeUntilDestroyed(this._destroyRef))
            .subscribe(autoFill => {
                if (this.form.value.status && this.form.controls.settings.controls.autofill.value) {
                    if (autoFill) {
                        this.form.controls.settings.controls.disallow_autofill_edition.enable();
                    } else {
                        this.form.controls.settings.controls.disallow_autofill_edition.disable();
                    }
                }
            });

        this.form.controls.settings.controls.disallow_autofill_edition.valueChanges
            .pipe(takeUntilDestroyed(this._destroyRef))
            .subscribe(disallowEdition => {
                if (!disallowEdition) {
                    // removed mark on checkbox that blocks the edition so now is allowed
                    this.form.controls.settings.controls.edit_autofill_disallowed_sectors.disable();
                }
            });

        this.form.controls.settings.controls.disallow_all_sectors.valueChanges
            .pipe(takeUntilDestroyed(this._destroyRef))
            .subscribe(disallowAllSectors => {
                if (!disallowAllSectors) {
                    this.form.controls.settings.controls.edit_autofill_disallowed_sectors.enable();
                } else if (disallowAllSectors) {
                    this.form.controls.settings.controls.edit_autofill_disallowed_sectors.disable();
                }
            });

        this.updateFormData();
    }

    refresh(): void {
        this._eventsService.event.get$().pipe(take(1))
            .subscribe(event => {
                this._eventsService.event.load(event.id.toString());
                this._eventsService.eventAttendantFields.load(event.id);
            });
        this.form.markAsPristine();
    }

    save$(): Observable<void[]> {
        // TODO: markAsDirty in order to reflect error in child component
        this.form.controls.settings.controls.edit_autofill_disallowed_sectors.markAsDirty();
        if (this.form.valid) {
            return this._eventsService.event.get$()
                .pipe(
                    take(1),
                    switchMap(event => {
                        const obs$: Observable<void>[] = [];
                        if (this.form.controls.status.dirty || this.form.controls.settings.dirty) {
                            const settings: PutEvent['settings'] = {};
                            const value = this.form.value.settings;
                            settings.attendant_tickets = {
                                status: this.form.value.status ? AttendantsStatus.active : AttendantsStatus.disabled
                            };
                            if (settings.attendant_tickets.status === AttendantsStatus.active) {
                                settings.attendant_tickets.channels_scope = {
                                    type: value.channelsScope.type
                                };
                                if (settings.attendant_tickets.channels_scope.type === EventChannelsScopeType.list) {
                                    settings.attendant_tickets.channels_scope.channels
                                        = value.channelsScope.channels?.map(channel => channel.id);
                                    settings.attendant_tickets.channels_scope.add_new_event_channel_relationships
                                        = value.channelsScope.addNewEventChannelRelationships;
                                }
                                if (EventsService.isAvet(event)) {
                                    settings.attendant_tickets.autofill = value.autofill;
                                    if (value.disallow_autofill_edition) {
                                        if (!value.disallow_all_sectors) {
                                            settings.attendant_tickets.edit_autofill = true;
                                            settings.attendant_tickets.edit_autofill_disallowed_sectors =
                                                value.edit_autofill_disallowed_sectors;
                                        } else if (value.disallow_all_sectors) {
                                            settings.attendant_tickets.edit_autofill = false;
                                            settings.attendant_tickets.edit_autofill_disallowed_sectors = [];
                                        }
                                    } else {
                                        settings.attendant_tickets.edit_autofill = true;
                                        settings.attendant_tickets.edit_autofill_disallowed_sectors = [];
                                    }
                                }
                                settings.attendant_tickets.edit_attendant = value.edit_attendant;
                            }
                            obs$.push(this._eventsService.event.update(event.id, { settings }));
                        }
                        if (this.form.value.status && this.form.controls.fields.dirty) {
                            obs$.push(this._eventsService.eventAttendantFields.create(event.id, this.form.value.fields));
                        }
                        return concat(...obs$).pipe(
                            toArray(),
                            tap(() => {
                                this._ephemeralMessage.showSaveSuccess();
                                this.refresh();
                            })
                        );
                    })
                );
        } else {
            FormControlHandler.markAllControlsAsTouched(this.form);
            // the component that controls this form control uses the change event to update the view and show it's invalid state
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this.matExpansionPanelQueryList);
            return throwError(() => 'invalid form');
        }
    }

    save(): void {
        this.save$().subscribe();
    }

    channelsFilterChangeHandler(filters: SearchablePaginatedSelectionLoadEvent): void {
        this._eventsService.event.get$().pipe(take(1)).subscribe(event =>
            this._eventChannelsService.eventChannelsList.load(event.id, { limit: this.channelsListPageSize, ...filters })
        );
    }

    private updateFormData(): void {
        combineLatest([
            this._eventsService.event.get$().pipe(filter(Boolean)),
            this._eventsService.eventAttendantFields.getData$().pipe(filter(Boolean))
        ])
            .pipe(takeUntilDestroyed(this._destroyRef))
            .subscribe(([event, fields]) => {
                fields.sort((a, b) => a.order - b.order);
                this.form.reset(this.getEventFormValue(event.settings.attendant_tickets, fields));
            });
    }

    private getEventFormValue(
        attendantTickets: Partial<Event['settings']['attendant_tickets']>, fields: EventAttendantField[]
    ): FormData {
        const val = {
            status: attendantTickets?.status === AttendantsStatus.active,
            settings: {
                edit_attendant: attendantTickets?.edit_attendant,
                autofill: attendantTickets?.autofill,
                disallow_autofill_edition: attendantTickets?.edit_autofill_disallowed_sectors?.length > 0 ?
                    attendantTickets?.edit_autofill : !attendantTickets?.edit_autofill,
                disallow_all_sectors: attendantTickets?.edit_autofill_disallowed_sectors?.length > 0 ? false : true,
                edit_autofill_disallowed_sectors: attendantTickets?.edit_autofill_disallowed_sectors,
                channelsScope: {
                    type: attendantTickets?.channels_scope?.type || EventChannelsScopeType.all,
                    channels: attendantTickets?.channels_scope?.channels,
                    addNewEventChannelRelationships:
                        !!attendantTickets?.channels_scope?.add_new_event_channel_relationships
                }
            },
            fields
        };
        return val;
    }
}
