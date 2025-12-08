import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { EventSessionsService, Session, SessionCapacityActivityService } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import {
    ActivityVenueTemplateGroupsComponent, ActivityVenueTemplateLimitsComponent, ActivityVenueTemplatePriceTypesGatesComponent
} from '@admin-clients/cpanel-common-venue-templates-feature';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import {
    ACTIVITY_GROUPS_SERVICE, ACTIVITY_LIMITS_SERVICE, ACTIVITY_PRICE_TYPES_GATES_SERVICE, ActivityGroupsComponentService,
    ActivityLimitsComponentService, ActivitySaleType, SessionActivityGroupsConfig
} from '@admin-clients/shared/venues/data-access/activity-venue-tpls';
import { VenueTemplateType, VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import {
    ChangeDetectionStrategy, Component, DestroyRef, Inject, OnDestroy, OnInit, QueryList, ViewChild, ViewChildren, inject
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { combineLatest, concat, EMPTY, Observable, throwError } from 'rxjs';
import { filter, map, startWith, tap } from 'rxjs/operators';

@Component({
    selector: 'app-session-capacity-activity',
    templateUrl: './session-capacity-activity.component.html',
    styleUrls: ['./session-capacity-activity.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [
        SessionCapacityActivityService,
        { provide: ACTIVITY_LIMITS_SERVICE, useExisting: SessionCapacityActivityService },
        { provide: ACTIVITY_PRICE_TYPES_GATES_SERVICE, useExisting: SessionCapacityActivityService },
        { provide: ACTIVITY_GROUPS_SERVICE, useExisting: SessionCapacityActivityService }
    ],
    standalone: false
})
export class SessionCapacityActivityComponent implements OnInit, OnDestroy, WritingComponent {
    readonly #fb = inject(UntypedFormBuilder);
    readonly #ephemeralMessage = inject(EphemeralMessageService);
    readonly #venueTemplateSrv = inject(VenueTemplatesService);
    readonly #eventSessionsSrv = inject(EventSessionsService);
    readonly #onDestroy = inject(DestroyRef);

    @ViewChildren(MatExpansionPanel) protected matExpansionPanelQueryList: QueryList<MatExpansionPanel>;
    @ViewChild(ActivityVenueTemplateLimitsComponent) protected activityLimitsComponent: ActivityVenueTemplateLimitsComponent;
    @ViewChild(ActivityVenueTemplateGroupsComponent) protected activityGroupsComponent: ActivityVenueTemplateGroupsComponent;
    @ViewChild(ActivityVenueTemplatePriceTypesGatesComponent)
    protected activityGatesComponent: ActivityVenueTemplatePriceTypesGatesComponent;

    #eventId: number;
    #sessionId: number;
    readonly activitySaleType = ActivitySaleType;
    form: UntypedFormGroup;
    limitsForm: UntypedFormGroup;
    gatesForm: UntypedFormGroup;
    groupsForm: UntypedFormGroup;
    mainForm: UntypedFormGroup;
    loading$: Observable<boolean>;
    session$: Observable<Session>;
    venueTemplateName$: Observable<string>;
    isSmartBooking = false;

    constructor(
        @Inject(ACTIVITY_LIMITS_SERVICE) private _activityLimitsService: ActivityLimitsComponentService,
        @Inject(ACTIVITY_GROUPS_SERVICE) private _activityGroupsService: ActivityGroupsComponentService<SessionActivityGroupsConfig>
    ) { }

    ngOnInit(): void {
        this.#venueTemplateSrv.venueTpl.clear();
        this.#venueTemplateSrv.clearVenueTemplateData();
        this.session$ = this.#eventSessionsSrv.session.get$().pipe(
            tap(session => this.isSmartBooking = (
                session.venue_template.type === VenueTemplateType.activity
                && session.settings?.smart_booking?.type === 'SMART_BOOKING'
            ))
        );
        this.venueTemplateName$ = this._activityGroupsService.getActivityGroupsConfig$()
            .pipe(
                filter(groupConfig => !!groupConfig),
                map(groupConfig => groupConfig.venue_template_name)
            );
        this.loading$ = booleanOrMerge([
            this._activityLimitsService.isActivityQuotaCapacityInProgress$(),
            this.#eventSessionsSrv.session.loading$(),
            this.#eventSessionsSrv.isSessionSaving$(),
            this._activityGroupsService.isActivityGroupsConfigInProgress$()
        ]);
        this.defineForm();
        this.setFormBehaviours();
        this.initFormData();
    }

    ngOnDestroy(): void {
        this._activityLimitsService.clearVenueTemplateQuotaCapacity();
    }

    cancelChanges(): void {
        this.#eventSessionsSrv.session.load(this.#eventId, this.#sessionId);
    }

    save(): void {
        this.save$().subscribe();
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            return concat(
                this.mainForm.dirty && this.saveSession() || EMPTY,
                this.limitsForm.dirty && this.activityLimitsComponent.save() || EMPTY,
                this.gatesForm.dirty && this.activityGatesComponent.save() || EMPTY,
                (this.groupsForm.dirty || this.form.get('useTemplateGroupConfig').dirty) &&
                this.activityGroupsComponent.save(this.form.get('useTemplateGroupConfig').value) || EMPTY
            ).pipe(
                tap(() => {
                    this.#ephemeralMessage.showSaveSuccess();
                    this.#eventSessionsSrv.session.load(this.#eventId, this.#sessionId);
                })
            );
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this.matExpansionPanelQueryList);
            return throwError(() => 'invalid form');
        }
    }

    refreshExternalAvailability(): void {
        this.#eventSessionsSrv.refreshExternalAvailability(this.#eventId, this.#sessionId)
            .subscribe(() => {
                this.#ephemeralMessage.showSuccess({ msgKey: 'EVENTS.SESSION_ACTIVITY_UPDATE_EXTERNAL_IN_PROGRESS' });
            });
    }

    private saveSession(): Observable<void> {
        return this.#eventSessionsSrv.updateSession(
            this.#eventId,
            this.#sessionId,
            {
                id: this.#sessionId,
                settings: {
                    use_venue_template_capacity_config: this.mainForm.get('useVenueTemplateCapacityConfig').value,
                    use_venue_template_access: this.mainForm.get('useVenueTemplateGatesConfig').value
                }
            }
        );
    }

    private defineForm(): void {
        this.form = this.#fb.group({
            limitsForm: this.#fb.group({}),
            gatesForm: this.#fb.group({}),
            useTemplateGroupConfig: null,
            groupsForm: this.#fb.group({}),
            mainForm: this.#fb.group({
                useVenueTemplateCapacityConfig: null,
                useVenueTemplateGatesConfig: null
            })
        });
        this.mainForm = this.form.get('mainForm') as UntypedFormGroup;
        this.limitsForm = this.form.get('limitsForm') as UntypedFormGroup;
        this.gatesForm = this.form.get('gatesForm') as UntypedFormGroup;
        this.groupsForm = this.form.get('groupsForm') as UntypedFormGroup;
    }

    private setFormBehaviours(): void {
        this.mainForm.get('useVenueTemplateCapacityConfig').valueChanges
            .pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe(value => {
                if (value) {
                    this.limitsForm.disable();
                } else {
                    this.limitsForm.enable();
                }
            });
        this.mainForm.get('useVenueTemplateGatesConfig').valueChanges
            .pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe(value => {
                if (value) {
                    this.gatesForm.disable();
                } else {
                    this.gatesForm.enable();
                }
            });
        this.session$.pipe(
            filter(Boolean),
            takeUntilDestroyed(this.#onDestroy)
        ).subscribe(session => {
            if (session.settings.activity_sale_type === ActivitySaleType.individual) {
                this.form.get('useTemplateGroupConfig').disable();
                this.groupsForm.disable();
            } else {
                this.form.get('useTemplateGroupConfig').enable();
                this.groupsForm.enable();
            }
        });
        this.form.get('useTemplateGroupConfig').valueChanges
            .pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe(useTemplateConfig => {
                if (useTemplateConfig) {
                    this.groupsForm.disable();
                } else {
                    this.groupsForm.enable();
                }
            });
    }

    private initFormData(): void {
        combineLatest([
            this.#eventSessionsSrv.session.get$().pipe(filter(Boolean)),
            this._activityGroupsService.getActivityGroupsConfig$().pipe(
                startWith(null as SessionActivityGroupsConfig)
            )
        ]).pipe(
            takeUntilDestroyed(this.#onDestroy)
        ).subscribe(([session, groupsConfig]) => {
            this.#eventId = session.event.id;
            this.#sessionId = session.id;
            if (groupsConfig) {
                this.form.get('useTemplateGroupConfig').setValue(groupsConfig.use_venue_template_group_config);
            }
            this.mainForm.reset({
                useVenueTemplateCapacityConfig: session.settings.use_venue_template_capacity_config,
                useVenueTemplateGatesConfig: session.settings.use_venue_template_access
            });
        });
    }
}
