import {
    CloneSessionRequest, EventSessionsService, EventSessionsState, Session,
    SessionsListCountersService
} from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { booleanOrMerge, dateIsAfter, dateIsBefore, dateTimeValidator, joinCrossValidations } from '@admin-clients/shared/utility/utils';
import { SeatStatus } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { VenueTemplateBlockingReason, VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { ChangeDetectionStrategy, Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Observable, Subject } from 'rxjs';

@Component({
    selector: 'app-clone-session-dialog',
    templateUrl: './clone-session-dialog.component.html',
    styleUrls: ['./clone-session-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [
        EventSessionsService, EventSessionsState
    ],
    standalone: false
})
export class CloneSessionDialogComponent implements OnInit, OnDestroy {
    private _onDestroy: Subject<void> = new Subject();
    private _fromSession: Session;
    form: UntypedFormGroup;
    seatStatus = SeatStatus;
    venueTplBlockingReasons$: Observable<VenueTemplateBlockingReason[]>;
    isSessionPackLinked: boolean;
    reqInProgress$: Observable<boolean>;
    hasFortressVenue: boolean;

    constructor(
        private _dialogRef: MatDialogRef<CloneSessionDialogComponent>,
        private _sessionsService: EventSessionsService,
        private _fb: UntypedFormBuilder,
        private _venueTplsSrv: VenueTemplatesService,
        private _countersSrv: SessionsListCountersService,
        @Inject(MAT_DIALOG_DATA) data: { fromSession: Session; hasFortressVenue: boolean }
    ) {
        this._dialogRef.addPanelClass(DialogSize.LARGE);
        this._dialogRef.disableClose = false;
        this._fromSession = data.fromSession;
        this.hasFortressVenue = data.hasFortressVenue || false;
        this.isSessionPackLinked = !!(data.fromSession.session_ids?.length);
    }

    ngOnInit(): void {
        this.form = this._fb.group({
            name: [this._fromSession.name, [Validators.required]],
            startDate: [this._fromSession.start_date, [Validators.required]],
            endDate: [this._fromSession.end_date],
            reference: [this._fromSession.reference],
            sessionPackSeatsTarget: [SeatStatus.free, [Validators.required]]
        });

        if (this.hasFortressVenue) {
            this.#initFortressValidations();
        }

        if (this.isSessionPackLinked) {
            this._venueTplsSrv.loadVenueTemplateBlockingReasons(this._fromSession.venue_template.id);
            this.venueTplBlockingReasons$ = this._venueTplsSrv.getVenueTemplateBlockingReasons$();
        }
        this.reqInProgress$ = booleanOrMerge([
            this._venueTplsSrv.isVenueTemplateBlockingReasonsLoading$(),
            this._sessionsService.isSessionSaving$()
        ]);
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    cloneSession(): void {
        if (this.isValid()) {
            const eventId = this._fromSession.event.id;
            const fromSessionId = this._fromSession.id;
            let sessionPackSeatsTarget = this.form.value.sessionPackSeatsTarget;
            if (typeof sessionPackSeatsTarget === 'object') {
                sessionPackSeatsTarget = (sessionPackSeatsTarget as VenueTemplateBlockingReason).id.toString();
            }
            const clonedSessionData: CloneSessionRequest = {
                name: this.form.value.name,
                start_date: this.form.value.startDate,
                session_pack_seats_target: sessionPackSeatsTarget
            };
            if (this.form.value.endDate) {
                clonedSessionData.end_date = this.form.value.endDate;
            }
            if (this.form.value.reference) {
                clonedSessionData.reference = this.form.value.reference;
            }
            this._countersSrv.resetGroupsStatusCounters();
            this._sessionsService.cloneSession(eventId, fromSessionId, clonedSessionData)
                .subscribe(id => this.close(id));
        }
    }

    close(newVenueTemplateId: number = null): void {
        this._dialogRef.close(newVenueTemplateId);
    }

    private isValid(): boolean {
        if (this.form.valid) {
            return true;
        } else {
            this.form.markAllAsTouched();
            return false;
        }
    }

    #initFortressValidations(): void {
        const startDate = this.form.get('startDate');
        const endDate = this.form.get('endDate');

        startDate.addValidators(
            dateTimeValidator(dateIsBefore, 'startDateAfterEndDate', endDate)
        );

        endDate.addValidators([
            Validators.required,
            dateTimeValidator(dateIsAfter, 'endDateBeforeStartDate', startDate)
        ]);

        joinCrossValidations([
            startDate,
            endDate
        ], this._onDestroy);
    }
}
