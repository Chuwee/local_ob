import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { forceDatesTimezone, forceToDefaultTimezone } from '@admin-clients/cpanel/common/utils';
import { Event, EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import {
    EventSessionsService, PutSession, Session, SessionDatesFormValidation, SessionType
} from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { VenuesService } from '@admin-clients/cpanel/venues/data-access';
import { EventType } from '@admin-clients/shared/common/data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, QueryList, ViewChildren } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { AbstractControl, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, first, map, Observable, shareReplay, Subject, switchMap, take, takeUntil, tap, throwError, withLatestFrom } from 'rxjs';
import { SessionAccessControlConfigurationComponent } from './configuration/session-access-control-configuration.component';
import { SessionExternalBarcodesComponent } from './external-barcodes/session-external-barcodes.component';
import { SessionInternalBarcodesComponent } from './internal-barcodes/session-internal-barcodes.component';

@Component({
    selector: 'app-session-access-control',
    templateUrl: './session-access-control.component.html',
    styleUrls: ['./session-access-control.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [SessionDatesFormValidation],
    imports: [
        FormContainerComponent, MaterialModule, ReactiveFormsModule,
        TranslatePipe, FlexLayoutModule, CommonModule,
        SessionInternalBarcodesComponent, SessionAccessControlConfigurationComponent,
        SessionExternalBarcodesComponent
    ]
})
export class SessionAccessControlComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    private _formChanged = new Subject<void>();
    private _sessionId: number;
    private _eventId: number;

    @ViewChildren(MatExpansionPanel)
    private _matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    form: FormGroup;
    configurationForm: FormGroup;

    sessionType: SessionType;
    sessionTypes = SessionType;

    isAvet$: Observable<boolean>;
    reqInProgress$: Observable<boolean>;

    constructor(
        private _sessionDatesFormValidation: SessionDatesFormValidation,
        private _ephemeralMessage: EphemeralMessageService,
        private _sessionsSrv: EventSessionsService,
        private _eventsSrv: EventsService,
        private _venueSrv: VenuesService,
        private _fb: FormBuilder
    ) { }

    ngOnInit(): void {
        this.initForm();
        this.model();
    }

    ngOnDestroy(): void {
        this._formChanged.next(null);
        this._onDestroy.next(null);
        this._formChanged.complete();
        this._onDestroy.complete();
    }

    save(): void {
        this.save$().subscribe();
    }

    save$(): Observable<Session> {
        if (this.form.valid) {
            return this._sessionsSrv.session.get$()
                .pipe(
                    take(1),
                    switchMap(session => {
                        const sessionToSave: PutSession = { settings: { access_control: this.form.value.access_control } };
                        forceDatesTimezone(sessionToSave, session.venue_template.venue.timezone);
                        return this._sessionsSrv.updateSession(session.event.id, session.id, sessionToSave)
                            .pipe(map(() => session));
                    }),
                    tap(session => {
                        this._ephemeralMessage.showSaveSuccess();
                        this._sessionsSrv.session.load(session.event.id, session.id);
                        this._sessionsSrv.setRefreshSessionsList();
                    })
                );
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanelQueryList);
            return throwError(() => 'invalid form');
        }
    }

    cancel(): void {
        this._sessionsSrv.session.load(this._sessionId, this._eventId);
    }

    private initForm(): void {
        this.configurationForm = this._fb.group({
            admission_dates: this._fb.group({
                override: false,
                start: [null, Validators.required],
                end: [null, Validators.required]
            }),
            space: this._fb.group({
                override: false,
                id: [null, Validators.required]
            })
        });

        this.form = this._fb.group({
            access_control: this.configurationForm
        });

    }

    private model(): void {
        this.reqInProgress$ = booleanOrMerge([
            this._sessionsSrv.session.loading$(),
            this._sessionsSrv.isSessionSaving$(),
            this._venueSrv.isVenueLoading$()
        ]);

        this.isAvet$ = this._eventsSrv.event.get$()
            .pipe(
                take(1),
                map(event => event.type === EventType.avet),
                takeUntil(this._onDestroy),
                shareReplay(1)
            );

        this.form.get('access_control.admission_dates.override').valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(value => {
                this.changeControlEnabledState(this.form.get('access_control.admission_dates.start'), value);
                this.changeControlEnabledState(this.form.get('access_control.admission_dates.end'), value);
            });

        this.form.get('access_control.space.override').valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(value => this.changeControlEnabledState(this.form.get('access_control.space.id'), value));

        this._sessionsSrv.session.get$()
            .pipe(
                filter(value => !!value),
                withLatestFrom(this._eventsSrv.event.get$().pipe(first())),
                takeUntil(this._onDestroy)
            )
            .subscribe(([session, event]) => {
                this._formChanged.next();
                this.sessionType = session.type;
                this._sessionId = session.id;
                this._eventId = event.id;
                this._venueSrv.loadVenue(session.venue_template.venue.id);
                this.updateForms(session, event);
            });
    }

    private changeControlEnabledState(control: AbstractControl, isEnabled: boolean): void {
        if (isEnabled) {
            control.enable();
        } else {
            control.disable();
        }
    }

    private updateForms(session: Session, event: Event): void {
        this.form.reset();
        this.form.patchValue({
            access_control: {
                admission_dates: {
                    override: session.settings?.access_control?.admission_dates?.override,
                    start: forceToDefaultTimezone(session.settings?.access_control?.admission_dates?.start),
                    end: forceToDefaultTimezone(session.settings?.access_control?.admission_dates?.end)
                },
                space: {
                    override: session.settings?.access_control?.space?.override,
                    id: session.settings?.access_control?.space?.id
                }
            }
        });

        this._sessionDatesFormValidation.addSessionDateValidations(
            {
                accessControlOverride: this.form.get('access_control.admission_dates.override'),
                accessControlStartDate: this.form.get('access_control.admission_dates.start'),
                accessControlEndDate: this.form.get('access_control.admission_dates.end')
            },
            event,
            this._formChanged
        );
        this.form.markAsPristine();
    }
}
