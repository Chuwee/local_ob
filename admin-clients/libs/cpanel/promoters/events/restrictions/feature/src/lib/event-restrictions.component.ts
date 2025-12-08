import { ExternalEntityService } from '@admin-clients/cpanel/organizations/entities/feature';
import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import {
    EventRestriction,
    EventRestrictionListElem,
    EventRestrictionsService,
    eventRestrictionsProviders
} from '@admin-clients/cpanel/promoters/events/restrictions/data-access';
import { EventType } from '@admin-clients/shared/common/data-access';
import {
    EmptyStateComponent, EphemeralMessageService, MessageDialogService, ObMatDialogConfig
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { StdVenueTplService, StdVenueTplsState } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { AsyncPipe, NgClass } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import {
    MatDialog
} from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { Subject, filter, first, map, switchMap, takeUntil, tap } from 'rxjs';
import { EventRestrictionDialogComponent } from './dialog/event-restriction-dialog.component';
import { EventRestrictionComponent } from './restriction/event-restriction.component';

@Component({
    imports: [
        AsyncPipe, NgClass, ReactiveFormsModule, FlexLayoutModule, MaterialModule,
        TranslatePipe, FormContainerComponent, EventRestrictionComponent, EmptyStateComponent
    ],
    providers: [StdVenueTplsState, StdVenueTplService, ...eventRestrictionsProviders],
    selector: 'app-event-restrictions',
    templateUrl: './event-restrictions.component.html',
    styleUrls: ['./event-restrictions.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EventRestrictionsComponent implements OnInit, OnDestroy, WritingComponent {
    private readonly _onDestroy = new Subject<void>();
    private readonly _eventsService = inject(EventsService);
    private readonly _eventRestrictionsService = inject(EventRestrictionsService);
    private readonly _externalService = inject(ExternalEntityService);
    private readonly _venueTplService = inject(StdVenueTplService);
    private readonly _matDialog = inject(MatDialog);
    private readonly _ephemeralMessageService = inject(EphemeralMessageService);
    private readonly _msgDialogService = inject(MessageDialogService);

    private _eventId: number;

    readonly isAvetEvent$ = this._eventsService.event.get$()
        .pipe(
            first(Boolean),
            map(event => event.type === EventType.avet)
        );

    readonly restrictions$ = this._eventRestrictionsService.restrictions.list.get$().pipe(
        filter(Boolean),
        tap(restrictions => restrictions?.forEach(r => this.form.addControl(r.sid, new FormGroup({}))))
    );

    readonly isInProgress$ = booleanOrMerge([
        this._eventRestrictionsService.restrictions.list.loading$(),
        this._eventRestrictionsService.restrictions.structure.loading$(),
        this._externalService.capacities.loading$(),
        this._externalService.roles.loading$(),
        this._externalService.periodicities.loading$(),
        this._externalService.terms.loading$()
    ]);

    readonly form = new FormGroup({});

    ngOnInit(): void {
        this._eventsService.event.get$()
            .pipe(
                first(Boolean),
                takeUntil(this._onDestroy)
            )
            .subscribe(event => {
                const eventId = event.id;
                const entityId = event.entity.id;
                //On AVET events always one venue template only
                const venueTemplateId = event.venue_templates[0]?.id;
                this._eventId = eventId;
                this.loadData(eventId, entityId, venueTemplateId);
            });
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._eventRestrictionsService.restrictions.clear();
        this._externalService.roles.clear();
        this._externalService.periodicities.clear();
        this._externalService.capacities.clear();
        this._externalService.terms.clear();
    }

    trackBy = (i: number, item: EventRestriction): string => item.sid;

    createRestriction(): void {
        const data = new ObMatDialogConfig(null);
        this._matDialog.open<
            EventRestrictionDialogComponent, EventRestrictionListElem, EventRestrictionListElem
        >(EventRestrictionDialogComponent, data)
            .beforeClosed().pipe(
                filter(Boolean),
                switchMap(restriction => this._eventRestrictionsService.restrictions.create(this._eventId, restriction))
            ).subscribe(response => {
                const sid = response.code;
                this._ephemeralMessageService.showSuccess({ msgKey: 'EVENTS.RESTRICTIONS.FORMS.FEEDBACKS.RESTRICTION_CREATED' });
                this.scrollToNewRestriction(sid);
            });
    }

    deleteRestriction(restriction: Partial<EventRestriction>): void {
        this._msgDialogService.showDeleteConfirmation({
            confirmation: {
                title: 'EVENTS.RESTRICTIONS.DIALOGS.DELETE_ELEMENT_TITLE',
                message: 'EVENTS.RESTRICTIONS.DIALOGS.DELETE_ELEMENT_MESSAGE',
                messageParams: { name: restriction.name }
            },
            delete$: this._eventRestrictionsService.restrictions.delete(this._eventId, restriction),
            success: {
                msgKey: 'EVENTS.RESTRICTIONS.FORMS.FEEDBACKS.RESTRICTION_DELETED',
                msgParams: { name: restriction.name }
            }
        });
    }

    loadRestrictionDetail(restriction: Partial<EventRestriction>): void {
        if (restriction.loaded) return;
        this._eventRestrictionsService.restrictions.load(this._eventId, restriction).subscribe();
    }

    renameRestriction(restriction: Partial<EventRestriction>): void {
        const data = new ObMatDialogConfig(restriction);
        this._matDialog.open<EventRestrictionDialogComponent, Partial<EventRestriction>, EventRestriction>(EventRestrictionDialogComponent, data)
            .beforeClosed()
            .pipe(
                filter(Boolean),
                switchMap(changes => this._eventRestrictionsService.restrictions.update(this._eventId, changes))
            )
            .subscribe(() => this._ephemeralMessageService.showSaveSuccess());
    }

    private loadData(eventId: number, entityId: number, venueTemplateId: number): void {
        this._eventRestrictionsService.restrictions.structure.load(eventId);
        this._eventRestrictionsService.restrictions.list.load(eventId);
        this._venueTplService.loadSectors(venueTemplateId);
        this._externalService.capacities.load(entityId);
        this._externalService.roles.load(entityId);
        this._externalService.periodicities.load(entityId);
        this._externalService.terms.load(entityId);
    }

    private scrollToNewRestriction(sid: string): void {
        setTimeout(() => {
            const element = document.getElementById(sid);
            element?.scrollIntoView({ behavior: 'smooth', block: 'center' });
        }, 500);
    }
}
