import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { Metadata } from '@OneboxTM/utils-state';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { CreatePackRequest, PacksService } from '@admin-clients/cpanel/packs/my-packs/data-access';
import { Event, eventsProviders, EventsService, EventStatus } from '@admin-clients/cpanel/promoters/events/data-access';
import {
    eventSessionsProviders, EventSessionsService, GetSessionsRequest, SessionsFilterFields, SessionStatus
} from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { EntitiesBaseService, EntitiesFilterFields, Entity } from '@admin-clients/shared/common/data-access';
import {
    DialogSize, EphemeralMessageService, SelectSearchComponent, SelectServerSearchComponent
} from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats, IdName } from '@admin-clients/shared/data-access/models';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge, maxSelectedItems } from '@admin-clients/shared/utility/utils';
import { VenueTemplatesService, VenueTemplateStatus } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { AsyncPipe, NgClass } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatCheckbox } from '@angular/material/checkbox';
import { MAT_DIALOG_DATA, MatDialogActions, MatDialogContent, MatDialogRef, MatDialogTitle } from '@angular/material/dialog';
import { MatDivider } from '@angular/material/divider';
import { MatLabel, MatFormField, MatError } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatOption, MatSelect } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, filter, map, shareReplay, switchMap, tap, throwError } from 'rxjs';
import { SUBITEMS_LIMIT } from '../pack/elements/pack-elements.component';

@Component({
    selector: 'app-create-pack-dialog',
    templateUrl: './create-pack-dialog.component.html',
    styleUrls: ['./create-pack-dialog.component.scss'],
    imports: [
        AsyncPipe, TranslatePipe, ReactiveFormsModule, FormControlErrorsComponent, FlexLayoutModule,
        SelectServerSearchComponent, DateTimePipe, SelectSearchComponent, MatDivider, MatLabel, MatCheckbox, MatFormField,
        MatSelect, MatOption, MatInput, MatIcon, MatProgressSpinner, MatDialogTitle, MatDialogContent, MatDialogActions,
        MatButton, MatIconButton, MatError, NgClass
    ],
    providers: [eventsProviders, eventSessionsProviders],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CreatePackDialogComponent implements OnInit {
    readonly #packsSrv = inject(PacksService);
    readonly #dialogRef = inject(MatDialogRef<CreatePackDialogComponent>);
    readonly #fb = inject(FormBuilder);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);
    readonly #onDestroy = inject(DestroyRef);
    readonly #eventSessionsService = inject(EventSessionsService);
    readonly #venueTemplatesSrv = inject(VenueTemplatesService);
    readonly #eventsSrv = inject(EventsService);
    readonly #PAGE_LIMIT = 20;
    readonly #auth = inject(AuthenticationService);
    readonly #entitiesService = inject(EntitiesBaseService);

    readonly subItemsLimit = SUBITEMS_LIMIT;

    readonly data = inject<{ entityId: number; packId?: number }>(MAT_DIALOG_DATA);

    readonly form = this.#fb.group({
        entity: [null as IdName, [Validators.required]],
        name: [null as string, [Validators.required, Validators.maxLength(50)]],
        //TODO: fix when we have manual types
        type: ['AUTOMATIC' as 'AUTOMATIC' | 'MANUAL', [Validators.required]],
        event: [{ value: null as Event, disabled: true }, [Validators.required]],
        session: this.#fb.control<IdName[]>({ value: [], disabled: true }, [maxSelectedItems(SUBITEMS_LIMIT)]),
        allowAddSessions: [false, [Validators.required]],
        venueTpl: [{ value: null as number, disabled: true }],
        ticket_taxes: [{ value: null as number, disabled: true }, [Validators.required]]
    });

    readonly creationTypes = {
        automatic: 'AUTOMATIC',
        manual: 'MANUAL'
    };

    readonly canSelectEntity$ = this.#auth.canReadMultipleEntities$();

    readonly entities$ = combineLatest([
        this.#auth.getLoggedUser$().pipe(filter(Boolean)),
        this.canSelectEntity$
    ]).pipe(
        switchMap(([user, canSelectEntity]) => {
            if (canSelectEntity) {
                this.#entitiesService.entityList.load({
                    limit: 999,
                    sort: 'name:asc',
                    fields: [
                        EntitiesFilterFields.name,
                        EntitiesFilterFields.allowActivityEvents,
                        EntitiesFilterFields.allowAvetIntegration
                    ],
                    type: 'EVENT_ENTITY'
                });
                return this.#entitiesService.entityList.getData$();
            } else {
                this.#entitiesService.loadEntity(user.entity.id);
                this.loadTaxesAndEnableControl(user.entity.id);
                return this.#entitiesService.getEntity$().pipe(
                    filter(Boolean),
                    map(entity => [entity])
                );
            }
        }),
        tap((entities: Entity[]) => {
            if (entities && entities.length === 1) {
                this.form.patchValue({ entity: entities[0] });
                this.loadTaxesAndEnableControl(entities[0].id);
            }
        }),
        takeUntilDestroyed(this.#onDestroy),
        shareReplay({ bufferSize: 1, refCount: true })
    );

    readonly moreEntitiesAvailable$ = this.#entitiesService.entityList.getMetadata$()
        .pipe(map(metadata => metadata?.offset + metadata?.limit < metadata?.total));

    readonly $entityTaxes = toSignal(this.#entitiesService.getEntityTaxes$().pipe(filter(Boolean)));

    readonly events$ = this.#eventsSrv.eventsList.getData$();

    readonly sessions$ = this.#eventSessionsService.sessionList.getData$();

    readonly venueTemplates$ = this.#venueTemplatesSrv.getVenueTemplatesList$().pipe(
        filter(Boolean),
        map(value => value.data),
        tap(vtpls => {
            if (vtpls.length === 1) {
                this.form.controls.venueTpl.setValue(vtpls[0].id);
                this.form.controls.venueTpl.disable();
            }
        }));

    readonly isLoading$ = booleanOrMerge([
        this.#eventsSrv.eventsList.loading$(),
        this.#packsSrv.pack.loading$()
    ]);

    readonly moreEventsAvailable$ = this.#eventsSrv.eventsList.getMetadata$()
        .pipe(map((metadata: Metadata) => metadata?.offset + metadata?.limit < metadata?.total));

    readonly moreSessionsAvailable$ = this.#eventSessionsService.sessionList.getMetadata$()
        .pipe(map((metadata: Metadata) => metadata?.offset + metadata?.limit < metadata?.total));

    readonly dateTimeFormats = DateTimeFormats;

    constructor() {
        this.#dialogRef.addPanelClass(DialogSize.MEDIUM);
        this.#dialogRef.disableClose = false;
    }

    ngOnInit(): void {
        this.entities$.subscribe();

        this.#eventSessionsService.sessionList.clear();

        //TODO: Fix when we have manual packs
        // this.form.controls.type.valueChanges
        //     .pipe(takeUntilDestroyed(this.#onDestroy))
        //     .subscribe(type => {
        // if (type === this.creationTypes.automatic) {
        // this.form.controls.event.enable({ emitEvent: false });
        // this.form.controls.event.markAsPristine();
        // this.form.controls.event.markAsUntouched();
        // this.#eventsSrv.eventsList.clear();
        // this.#eventsSrv.eventsList.load({
        //     limit: 20, offset: 0, status: [EventStatus.inProgramming, EventStatus.planned, EventStatus.ready],
        //     entityId: this.form.controls.entity.value.id, sort: 'name:asc'
        // });
        // } else if (type === this.creationTypes.manual) {
        //     this.form.controls.event.disable({ emitEvent: false });
        //     this.form.controls.session.disable({ emitEvent: false });
        //     this.form.controls.venueTpl.disable({ emitEvent: false });
        // }
        // });

        this.form.controls.entity.valueChanges
            .pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe(entity => {
                this.form.controls.event.reset({ value: null, disabled: false });
                this.form.controls.session.reset({ value: null, disabled: true });
                this.form.controls.venueTpl.reset({ value: null, disabled: true });
                this.form.controls.event.enable({ emitEvent: false });
                this.form.controls.event.markAsPristine();
                this.form.controls.event.markAsUntouched();
                this.loadTaxesAndEnableControl(entity.id);
                this.#eventsSrv.eventsList.clear();
                this.#eventsSrv.eventsList.load({
                    limit: 20, offset: 0, status: [EventStatus.inProgramming, EventStatus.planned, EventStatus.ready],
                    entityId: this.form.controls.entity.value.id, sort: 'name:asc'
                });
            });

        this.form.controls.event.valueChanges
            .pipe(
                filter(Boolean),
                takeUntilDestroyed(this.#onDestroy)
            ).subscribe(event => {
                this.form.controls.venueTpl.reset({ value: null, disabled: false });
                this.form.controls.session.reset({ value: null, disabled: true });
                this.#venueTemplatesSrv.loadVenueTemplatesList({
                    limit: 999, offset: 0, entityId: event.entity.id,
                    eventId: event.id, sort: 'name:asc', status: [VenueTemplateStatus.active]
                });
            });

        this.form.controls.venueTpl.valueChanges
            .pipe(
                filter(Boolean),
                filter(() => !!this.form.controls.event.value?.id),
                takeUntilDestroyed(this.#onDestroy)
            ).subscribe(venueTplId => {
                this.form.controls.session.reset({ value: null, disabled: false });
                const eventId = this.form.controls.event.value.id;
                this.#eventSessionsService.sessionList.load(eventId,
                    {
                        fields: [SessionsFilterFields.name, SessionsFilterFields.venueTemplateId,
                        SessionsFilterFields.startDate, SessionsFilterFields.settingsSmartbookingStatus,
                        SessionsFilterFields.settingsSmartbookingRelatedSession, SessionsFilterFields.venueTplType],
                        status: [SessionStatus.preview, SessionStatus.ready, SessionStatus.scheduled],
                        venueTplId
                    }
                );
            });

        this.form.controls.session.valueChanges
            .pipe(
                filter(Boolean),
                takeUntilDestroyed(this.#onDestroy)
            )
            .subscribe(() => {
                const allowAddSessionsControl = this.form.controls.allowAddSessions;
                if (allowAddSessionsControl.value) {
                    allowAddSessionsControl.setValue(false);
                }
            });
    }

    createSessionPack(): void {
        const selectedSessions = this.form.controls.session.value || [];
        if (this.form.valid) {
            const mainItemType = this.#getMainItemType(selectedSessions);

            const req: CreatePackRequest = {
                name: this.form.controls.name.value,
                type: this.form.controls.type.value,
                main_item: {
                    item_id: this.#getMainItemId(mainItemType),
                    type: mainItemType
                },
                entity_id: this.form.controls.entity.value.id,
                tax_id: this.form.controls.ticket_taxes.value
            };

            if (mainItemType === 'EVENT') {
                const subItemIds = selectedSessions?.map(session => session.id) || [];
                req.main_item.sub_item_ids = subItemIds;
                req.main_item.venue_template_id = this.form.controls.venueTpl.value;
            }

            this.#packsSrv.pack.create(req)
                .subscribe(pack => {
                    this.#ephemeralMessageService.showSuccess({ msgKey: 'PACKS.CREATE_PACK_SUCCESS' });
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

    loadEntities(q: string, next = false): void {
        this.#entitiesService.loadServerSearchEntityList({
            limit: this.#PAGE_LIMIT,
            sort: 'name:asc',
            fields: [EntitiesFilterFields.name],
            type: 'CHANNEL_ENTITY', //TODO: Check this, should be entity?
            q
        }, next);
    }

    loadEvents(q: string): void {
        this.#eventsSrv.eventsList.load({
            limit: this.#PAGE_LIMIT,
            offset: 0,
            sort: 'name:asc',
            status: [EventStatus.inProgramming, EventStatus.planned, EventStatus.ready],
            entityId: this.form.controls.entity.value.id,
            q
        });
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

        const eventId = this.form.controls.event.value.id;
        const venueTplId = this.form.controls.venueTpl.value;

        if (!next) {
            this.#eventSessionsService.sessionList.load(eventId, {
                ...request,
                venueTplId
            });
        } else {
            this.#eventSessionsService.sessionList.loadMore(eventId, {
                ...request,
                venueTplId
            });
        }
    }

    loadTaxesAndEnableControl(entityId: number): void {
        this.#entitiesService.loadEntityTaxes(entityId);
        this.form.controls.ticket_taxes.enable();
    }

    #getMainItemType(selectedSessions: { id: number; name: string }[] | null): 'EVENT' | 'SESSION' {
        if (selectedSessions && selectedSessions.length === 1 && this.form.controls.allowAddSessions.value) {
            return 'SESSION';
        }
        return 'EVENT';
    }

    #getMainItemId(itemType: 'EVENT' | 'SESSION'): number {
        if (itemType !== 'EVENT') {
            const subSessions = this.form.controls.session.value;
            if (subSessions && Array.isArray(subSessions) && subSessions.length > 0 && subSessions[0]) {
                return subSessions[0].id;
            }
        }

        return this.form.controls.event.value.id;
    }
}
