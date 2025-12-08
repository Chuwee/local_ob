import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { forceDatesTimezone } from '@admin-clients/cpanel/common/utils';
import {
    EventFieldsRestriction, EventsService, SessionFieldsRestrictions
} from '@admin-clients/cpanel/promoters/events/data-access';
import {
    EventSessionsService, SessionBeforeAfter, SessionRate, SessionRelativeTimeUnits, SessionsAccessSpaceMode,
    SessionsAccessTimeMode, SessionsDateMode, SessionStatus
} from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { Space, VenuesService } from '@admin-clients/cpanel/venues/data-access';
import { Tax, EntitiesBaseService, EventType } from '@admin-clients/shared/common/data-access';
import { ObMatDialogConfig } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ActivitySaleType } from '@admin-clients/shared/venues/data-access/activity-venue-tpls';
import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit, QueryList, ViewChildren } from '@angular/core';
import { AbstractControl, UntypedFormBuilder, UntypedFormGroup, ValidationErrors, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatExpansionPanel } from '@angular/material/expansion';
import moment from 'moment-timezone';
import { combineLatest, Observable, Subject, throwError } from 'rxjs';
import { first, map, shareReplay, switchMap, take, takeUntil, tap } from 'rxjs/operators';
import {
    MultiSessionChangesVerificationDialogComponent
} from '../changes-verification/multi-session-changes-verification-dialog.component';

@Component({
    selector: 'app-multi-planning',
    templateUrl: './multi-session-planning.component.html',
    styleUrls: ['./multi-session-planning.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class MultiSessionPlanningComponent implements OnInit, OnDestroy, WritingComponent {
    private _formBuilder = inject(UntypedFormBuilder);
    private _sessionsSrv = inject(EventSessionsService);
    private _eventsSrv = inject(EventsService);
    private _venueSrv = inject(VenuesService);
    private _entitiesSrv = inject(EntitiesBaseService);
    private _matDialog = inject(MatDialog);
    private _onDestroy = new Subject<void>();
    @ViewChildren(MatExpansionPanel)
    private _matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    private _eventId: number;

    private get _sessionRatesValue(): Map<number, SessionRate> {
        return this.form.get('generalForm.rates')?.value;
    }

    readonly activitySaleType = ActivitySaleType;
    isLoading$: Observable<boolean>;
    eventFieldsRestrictions = EventFieldsRestriction;
    sessionStatuses: SessionStatus[] = [SessionStatus.scheduled, SessionStatus.preview, SessionStatus.ready, SessionStatus.cancelled];
    rates$: Observable<SessionRate[]>;
    taxes$: Observable<Tax[]>;
    isAvet$: Observable<boolean>;
    showAccessSpace$: Observable<boolean>;
    spaces$: Observable<Space[]>;
    isGroupsAllowed: boolean;
    form: UntypedFormGroup;

    get generalForm(): UntypedFormGroup {
        return this.form.get('generalForm') as UntypedFormGroup;
    }

    get operativeDatesForm(): UntypedFormGroup {
        return this.form.get('operativeDatesForm') as UntypedFormGroup;
    }

    get operativeForm(): UntypedFormGroup {
        return this.form.get('operativeForm') as UntypedFormGroup;
    }

    showBookingSettings$: Observable<boolean>;
    showSecondaryMarketSettings$ = this._entitiesSrv.getEntity$()
        .pipe(first(Boolean), map(entity => entity.settings.allow_secondary_market), shareReplay(1));

    sessionsDateMode = SessionsDateMode;
    sessionsAccessTimeMode = SessionsAccessTimeMode;
    timeUnitList = Object.values(SessionRelativeTimeUnits);
    relativeTimeUnits = SessionRelativeTimeUnits;
    sessionBeforeAfterList = Object.values(SessionBeforeAfter);
    sessionsAccessSpaceMode = SessionsAccessSpaceMode;

    ngOnInit(): void {
        this.initForms();
        combineLatest([
            this._entitiesSrv.getEntity$().pipe(first(Boolean)),
            this._eventsSrv.event.get$()
                .pipe(first(Boolean))
        ])
            .subscribe(([entity, event]) => {
                this._eventId = event.id;
                this._entitiesSrv.loadEntityTaxes(event.entity.id);
                this._eventsSrv.eventRates.load(event.id.toString());
                const isBookingsEnabled = event.settings.bookings?.enable;
                const isSecondaryMarketEnabled = entity.settings.allow_secondary_market;
                this.initFormChangesSubscriptions(isBookingsEnabled, isSecondaryMarketEnabled);
                this.isGroupsAllowed = event.settings.groups.allowed;
            });
        this.model();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    save(): void {
        this.save$().subscribe();
    }

    save$(): Observable<boolean> {
        if (this.generalForm.valid && !this.isTrulyInvalidOperativeDatesForm()) {
            return this._sessionsSrv.getSelectedSessions$()
                .pipe(
                    take(1),
                    switchMap(sessions => {
                        const sessionIds = sessions.map(sw => sw.session.id);
                        const values = this.form.value;
                        const putSessions: { [key: string]: any } = {
                            ids: sessionIds,
                            value: {}
                        };

                        if (values.generalForm.name) {
                            putSessions['value'].name = values.generalForm.name;
                        }
                        if (values.generalForm.status) {
                            putSessions['value'].status = values.generalForm.status;
                        }
                        if (values.generalForm.ticketTax) {
                            putSessions['value'].settings = putSessions['value'].settings || {};
                            putSessions['value'].settings.taxes = putSessions['value'].settings.taxes || {};
                            putSessions['value'].settings.taxes.ticket = {
                                id: values.generalForm.ticketTax.id
                            };
                        }
                        if (values.generalForm.editRates) {
                            const rates = Array.from(values.generalForm.rates.values());
                            putSessions['value'].settings = putSessions['value'].settings || {};
                            putSessions['value'].settings.rates = rates.map((rate: { id?: number; default?: boolean }) => ({
                                id: rate.id,
                                default: rate.default
                            }));
                        }

                        if (this.isGroupsAllowed && values.operativeDatesForm.activitySaleType.editEnable) {
                            putSessions['value'].settings = putSessions['value'].settings || {};
                            putSessions['value'].settings.activity_sale_type = values.operativeDatesForm.activitySaleType.enable;
                        }
                        ['release', 'booking', 'sale', 'secondary_market_sale'].forEach(sectionName => {
                            const section = values.operativeDatesForm[sectionName];
                            if (section) {
                                if (section.editEnable) {
                                    putSessions['value'].settings = putSessions['value'].settings || {};
                                    putSessions['value'].settings[sectionName] = putSessions['value'].settings[sectionName] || {};
                                    putSessions['value'].settings[sectionName].enable = section.enable;
                                }

                                if (sectionName === 'release') {
                                    if (section.editDate) {
                                        putSessions['value'].settings = putSessions['value'].settings || {};
                                        putSessions['value'].settings[sectionName] = putSessions['value'].settings[sectionName] || {};
                                        if (section.dateMode === SessionsDateMode.fixed) {
                                            putSessions['value'].settings[sectionName].date = section.date;
                                        } else {
                                            putSessions['value'].settings[sectionName].date
                                                = this.getRelativeDateToken(section.relativeDate);
                                        }
                                    }
                                } else {
                                    ['start', 'end'].forEach(subSection => {
                                        if (section[subSection].editDate) {
                                            putSessions['value'].settings = putSessions['value'].settings || {};
                                            putSessions['value'].settings[sectionName] = putSessions['value'].settings[sectionName] || {};
                                            if (section[subSection].dateMode === SessionsDateMode.fixed) {
                                                putSessions['value'].settings[sectionName][subSection + '_date'] = section[subSection].date;
                                            } else {
                                                putSessions['value'].settings[sectionName][subSection + '_date']
                                                    = this.getRelativeDateToken(section[subSection].relativeDate);
                                            }
                                        }
                                    });
                                }
                            }
                        });
                        forceDatesTimezone(putSessions['value'], sessions[0].session.venue_template.venue.timezone);

                        if (values.generalForm.accessControl.editAccessTime) {
                            putSessions['value'].settings = putSessions['value'].settings || {};
                            putSessions['value'].settings.access_control = putSessions['value'].settings.access_control || {};
                            putSessions['value'].settings.access_control.admission_dates = {
                                override: values.generalForm.accessControl.accessTimeMode === SessionsAccessTimeMode.relative
                            };
                            if (values.generalForm.accessControl.accessTimeMode === SessionsAccessTimeMode.relative) {
                                const relativeStart = this.getRelativeDateToken(values.generalForm.accessControl.relativeAccessTime.opening);
                                const relativeEnd = this.getRelativeDateToken(values.generalForm.accessControl.relativeAccessTime.closing);
                                putSessions['value'].settings.access_control.admission_dates.start = relativeStart;
                                putSessions['value'].settings.access_control.admission_dates.end = relativeEnd;
                            }
                        }

                        if (values.generalForm.accessControl.editAccessSpace) {
                            putSessions['value'].settings = putSessions['value'].settings || {};
                            putSessions['value'].settings.access_control = putSessions['value'].settings.access_control || {};
                            putSessions['value'].settings.access_control.space = {
                                override: values.generalForm.accessControl.accessSpaceMode === SessionsAccessSpaceMode.custom
                            };
                            if (values.generalForm.accessControl.accessSpaceMode === SessionsAccessSpaceMode.custom) {
                                putSessions['value'].settings.access_control.space.id = values.generalForm.accessControl.customAccessSpace;
                            }
                        }

                        return this._matDialog.open<
                            MultiSessionChangesVerificationDialogComponent, { eventId; putSessions; sessions }, boolean
                        >(MultiSessionChangesVerificationDialogComponent, new ObMatDialogConfig({
                            eventId: this._eventId,
                            putSessions,
                            sessions
                        })).beforeClosed()
                            .pipe(
                                tap(isSaved => {
                                    if (isSaved) {
                                        this.form.reset();
                                        this._sessionsSrv.setRefreshSessionsList();
                                    }
                                })
                            );
                    })
                );
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanelQueryList);
            return throwError(() => 'invalid form');
        }
    }

    cancelChanges(): void {
        this.form.reset();
    }

    isDefaultRate(rateId: number): boolean {
        return this._sessionRatesValue?.get(rateId)?.default;
    }

    setDefaultRate(rateId: number): void {
        if (!this._sessionRatesValue.get(rateId)?.default) {
            this._sessionRatesValue.set(rateId, { id: rateId, default: true } as SessionRate);
            this._sessionRatesValue.forEach(r => {
                if (r.id !== rateId) {
                    r.default = false;
                }
            });
        }
        this.touchAndValidateRates();
    }

    isVisibleRate(rateId: number): boolean {
        return !!this._sessionRatesValue?.get(rateId);
    }

    setVisibleRate(rateId: number): void {
        if (this._sessionRatesValue.get(rateId)) {
            if (!this._sessionRatesValue.get(rateId).default) {
                this._sessionRatesValue.delete(rateId);
            }
        } else {
            this._sessionRatesValue.set(rateId, { id: rateId, default: false } as SessionRate);
        }
        this.touchAndValidateRates();
    }

    isTrulyInvalidOperativeDatesForm(): boolean {
        let isTrulyInvalid = false;
        if (!this.operativeDatesForm.valid) {
            const releaseGroup = this.operativeDatesForm.get('release');
            if (releaseGroup.invalid) {
                const releaseEnabled = this.operativeDatesForm.get('release.enable').value;
                const isReleaseDateRequired = this.operativeDatesForm.get('release.date').hasError('required');
                if (releaseEnabled || isReleaseDateRequired) {
                    isTrulyInvalid = true;
                }
            }
            const bookingGroup = this.operativeDatesForm.get('booking');
            if (bookingGroup.invalid) {
                const bookingEnabled = this.operativeDatesForm.get('booking.enable').value;
                const isBookingStartDateRequired = this.operativeDatesForm.get('booking.start.date').hasError('required');
                const isBookingEndDateRequired = this.operativeDatesForm.get('booking.end.date').hasError('required');
                if (bookingEnabled || isBookingStartDateRequired || isBookingEndDateRequired) {
                    isTrulyInvalid = true;
                }
            }
            const saleGroup = this.operativeDatesForm.get('sale');
            if (saleGroup.invalid) {
                const saleEnabled = this.operativeDatesForm.get('sale.enable').value;
                const isSaleStartDateRequired = this.operativeDatesForm.get('sale.start.date').hasError('required');
                const isSaleEndDateRequired = this.operativeDatesForm.get('sale.end.date').hasError('required');
                if (saleEnabled || isSaleStartDateRequired || isSaleEndDateRequired) {
                    isTrulyInvalid = true;
                }
            }
        }

        return isTrulyInvalid;
    }

    getFirstFieldError(formControlPath: string): string {
        const formControl = this.operativeDatesForm.get(formControlPath);
        return formControl.errors && formControl.errors &&
            Object.keys(formControl.errors)[0];
    }

    private model(): void {
        this.isLoading$ = booleanOrMerge([
            this._entitiesSrv.isEntityLoading$(),
            this._entitiesSrv.isEntityTaxesLoading(),
            this._venueSrv.isVenueLoading$(),
            this._eventsSrv.eventRates.inProgress$(),
            this._sessionsSrv.isSessionSaving$()
        ]);
        this.taxes$ = this._entitiesSrv.getEntityTaxes$();
        this.rates$ = this._eventsSrv.eventRates.get$()
            .pipe(
                first(value => value !== null),
                map(rates => {
                    const sessionRates: Map<number, SessionRate> = new Map();
                    rates.forEach(rate => {
                        if (rate.default) {
                            sessionRates.set(rate.id, rate);
                        }
                    });
                    this.form.get('generalForm.rates').setValue(sessionRates);

                    return rates.map(rate => ({ id: rate.id, name: rate.name } as SessionRate));
                }));
        this.isAvet$ = this._eventsSrv.event.get$()
            .pipe(
                take(1),
                map(event => event.type === EventType.avet),
                takeUntil(this._onDestroy),
                shareReplay(1)
            );
        this.showBookingSettings$ = this._eventsSrv.event.get$()
            .pipe(map(event => event.settings.bookings?.enable));

        this.showAccessSpace$ = this._sessionsSrv.getSelectedSessions$()
            .pipe(
                takeUntil(this._onDestroy),
                map(sessions => sessions.every((sw, i, swArr) =>
                    i === 0 || swArr[i - 1].session.venue_template.venue.id === sw.session.venue_template.venue.id)
                )
            );
        this.spaces$ = this._venueSrv.getVenue$().pipe(map(venue => venue?.spaces));
    }

    private initForms(): void {
        const fb = this._formBuilder;
        const generalForm = fb.group({
            name: [null, Validators.maxLength(SessionFieldsRestrictions.sessionNameLength)],
            status: null,
            ticketTax: null,
            editRates: false,
            rates: [{ value: null, disabled: true }, MultiSessionPlanningComponent.requiredMap],
            accessControl: fb.group({
                editAccessTime: false,
                accessTimeMode: [{ value: SessionsAccessTimeMode.automatic, disabled: true }, Validators.required],
                relativeAccessTime: fb.group({
                    opening: fb.group({
                        duration: [{ value: 1, disabled: true }, [Validators.required, Validators.min(1), Validators.max(999)]],
                        timeUnit: [{ value: SessionRelativeTimeUnits.minutes, disabled: true }, Validators.required]
                    }),
                    closing: fb.group({
                        duration: [{ value: 1, disabled: true }, [Validators.required, Validators.min(1), Validators.max(999)]],
                        timeUnit: [{ value: SessionRelativeTimeUnits.minutes, disabled: true }, Validators.required],
                        beforeAfter: [{ value: SessionBeforeAfter.before, disabled: true }, Validators.required]
                    })
                }),
                editAccessSpace: false,
                accessSpaceMode: [{ value: SessionsAccessSpaceMode.default, disabled: true }, Validators.required],
                customAccessSpace: [{ value: null, disabled: true }, Validators.required]
            })
        });
        const operativeDatesForm = fb.group({
            activitySaleType: fb.group({
                editEnable: false,
                enable: [{ value: ActivitySaleType.individual, disabled: true }, Validators.required]
            }),
            release: fb.group({
                editEnable: false,
                enable: [{ value: true, disabled: true }, Validators.required],
                editDate: false,
                dateMode: [{ value: SessionsDateMode.fixed, disabled: true }, Validators.required],
                date: [{ value: null, disabled: true }, Validators.required],
                relativeDate: fb.group({
                    duration: [{ value: 1, disabled: true }, [Validators.required, Validators.min(1), Validators.max(999)]],
                    timeUnit: [{ value: SessionRelativeTimeUnits.minutes, disabled: true }, Validators.required],
                    addTime: [{ value: false, disabled: true }, Validators.required],
                    fixedTime: [{ value: null, disabled: true }, Validators.required]
                })
            }),
            booking: fb.group({
                editEnable: false,
                enable: [{ value: true, disabled: true }, Validators.required],
                start: fb.group({
                    editDate: false,
                    dateMode: [{ value: SessionsDateMode.fixed, disabled: true }, Validators.required],
                    date: [{ value: null, disabled: true }, Validators.required],
                    relativeDate: fb.group({
                        duration: [{ value: 1, disabled: true }, [Validators.required, Validators.min(1), Validators.max(999)]],
                        timeUnit: [{ value: SessionRelativeTimeUnits.minutes, disabled: true }, Validators.required],
                        addTime: [{ value: false, disabled: true }, Validators.required],
                        fixedTime: [{ value: null, disabled: true }, Validators.required]
                    })
                }),
                end: fb.group({
                    editDate: false,
                    dateMode: [{ value: SessionsDateMode.fixed, disabled: true }, Validators.required],
                    date: [{ value: null, disabled: true }, Validators.required],
                    relativeDate: fb.group({
                        duration: [{ value: 1, disabled: true }, [Validators.required, Validators.min(1), Validators.max(999)]],
                        timeUnit: [{ value: SessionRelativeTimeUnits.minutes, disabled: true }, Validators.required],
                        addTime: [{ value: false, disabled: true }, Validators.required],
                        fixedTime: [{ value: null, disabled: true }, Validators.required],
                        beforeAfter: [{ value: SessionBeforeAfter.before, disabled: true }, Validators.required]
                    })
                })
            }),
            sale: fb.group({
                editEnable: false,
                enable: [{ value: true, disabled: true }, Validators.required],
                start: fb.group({
                    editDate: false,
                    dateMode: [{ value: SessionsDateMode.fixed, disabled: true }, Validators.required],
                    date: [{ value: null, disabled: true }, Validators.required],
                    relativeDate: fb.group({
                        duration: [{ value: 1, disabled: true }, [Validators.required, Validators.min(1), Validators.max(999)]],
                        timeUnit: [{ value: SessionRelativeTimeUnits.minutes, disabled: true }, Validators.required],
                        addTime: [{ value: false, disabled: true }, Validators.required],
                        fixedTime: [{ value: null, disabled: true }, Validators.required]
                    })
                }),
                end: fb.group({
                    editDate: false,
                    dateMode: [{ value: SessionsDateMode.fixed, disabled: true }, Validators.required],
                    date: [{ value: null, disabled: true }, Validators.required],
                    relativeDate: fb.group({
                        duration: [{ value: 1, disabled: true }, [Validators.required, Validators.min(1), Validators.max(999)]],
                        timeUnit: [{ value: SessionRelativeTimeUnits.minutes, disabled: true }, Validators.required],
                        addTime: [{ value: false, disabled: true }, Validators.required],
                        fixedTime: [{ value: null, disabled: true }, Validators.required],
                        beforeAfter: [{ value: SessionBeforeAfter.before, disabled: true }, Validators.required]
                    })
                })
            }),
            secondary_market_sale: fb.group({
                editEnable: false,
                enable: [{ value: true, disabled: true }, Validators.required],
                start: fb.group({
                    editDate: false,
                    dateMode: [{ value: SessionsDateMode.fixed, disabled: true }, Validators.required],
                    date: [{ value: null, disabled: true }, Validators.required],
                    relativeDate: fb.group({
                        duration: [{ value: 1, disabled: true }, [Validators.required, Validators.min(1), Validators.max(999)]],
                        timeUnit: [{ value: SessionRelativeTimeUnits.minutes, disabled: true }, Validators.required],
                        addTime: [{ value: false, disabled: true }, Validators.required],
                        fixedTime: [{ value: null, disabled: true }, Validators.required]
                    })
                }),
                end: fb.group({
                    editDate: false,
                    dateMode: [{ value: SessionsDateMode.fixed, disabled: true }, Validators.required],
                    date: [{ value: null, disabled: true }, Validators.required],
                    relativeDate: fb.group({
                        duration: [{ value: 1, disabled: true }, [Validators.required, Validators.min(1), Validators.max(999)]],
                        timeUnit: [{ value: SessionRelativeTimeUnits.minutes, disabled: true }, Validators.required],
                        addTime: [{ value: false, disabled: true }, Validators.required],
                        fixedTime: [{ value: null, disabled: true }, Validators.required],
                        beforeAfter: [{ value: SessionBeforeAfter.before, disabled: true }, Validators.required]
                    })
                })
            })
        });
        this.form = fb.group({
            generalForm,
            operativeDatesForm
        });
    }

    private initFormChangesSubscriptions(isBookingsEnabled: boolean, isSecondaryMarketEnabled: boolean): void {
        this.generalForm.get('editRates').valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(value => {
                this.changeControlEnabledState(this.generalForm.get('rates'), value);
            });

        this.initGroupEditionCheckChangesSubscriptions(this.operativeDatesForm, 'activitySaleType', 'editEnable');
        this.initGroupEditionCheckChangesSubscriptions(this.operativeDatesForm, 'release', 'editEnable');
        this.initGroupEditionCheckChangesSubscriptions(this.operativeDatesForm, 'release', 'editDate');
        this.initGroupDateModeChangesSubscriptions(this.operativeDatesForm, 'release');

        this.initGroupEditionCheckChangesSubscriptions(this.operativeDatesForm, 'sale', 'editEnable');
        this.initGroupEditionCheckChangesSubscriptions(this.operativeDatesForm, 'sale.start', 'editDate');
        this.initGroupEditionCheckChangesSubscriptions(this.operativeDatesForm, 'sale.end', 'editDate');
        this.initGroupDateModeChangesSubscriptions(this.operativeDatesForm, 'sale.start');
        this.initGroupDateModeChangesSubscriptions(this.operativeDatesForm, 'sale.end');

        if (isSecondaryMarketEnabled) {
            this.initGroupEditionCheckChangesSubscriptions(this.operativeDatesForm, 'secondary_market_sale', 'editEnable');
            this.initGroupEditionCheckChangesSubscriptions(this.operativeDatesForm, 'secondary_market_sale.start', 'editDate');
            this.initGroupEditionCheckChangesSubscriptions(this.operativeDatesForm, 'secondary_market_sale.end', 'editDate');
            this.initGroupDateModeChangesSubscriptions(this.operativeDatesForm, 'secondary_market_sale.start');
            this.initGroupDateModeChangesSubscriptions(this.operativeDatesForm, 'secondary_market_sale.end');
        } else {
            this.operativeDatesForm.get('secondary_market_sale').disable();
            this.operativeDatesForm.get('secondary_market_sale').updateValueAndValidity();
        }

        if (isBookingsEnabled) {
            this.initGroupEditionCheckChangesSubscriptions(this.operativeDatesForm, 'booking', 'editEnable');
            this.initGroupEditionCheckChangesSubscriptions(this.operativeDatesForm, 'booking.start', 'editDate');
            this.initGroupEditionCheckChangesSubscriptions(this.operativeDatesForm, 'booking.end', 'editDate');
            this.initGroupDateModeChangesSubscriptions(this.operativeDatesForm, 'booking.start');
            this.initGroupDateModeChangesSubscriptions(this.operativeDatesForm, 'booking.end');
        } else {
            this.operativeDatesForm.get('booking').disable();
            this.operativeDatesForm.get('booking').updateValueAndValidity();
        }

        this.initAccessControlChangesSubscriptions(this.generalForm);
    }

    private initGroupEditionCheckChangesSubscriptions(form: UntypedFormGroup, groupName: string, checkName: string): void {
        form.get(`${groupName}.${checkName}`).valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe((isChecked: boolean) => {
                if (isChecked) {
                    switch (checkName) {
                        case 'editEnable':
                            form.get(groupName + '.enable').enable();
                            break;
                        case 'editDate':
                            form.get(groupName + '.dateMode').enable();
                            break;
                    }
                } else {
                    switch (checkName) {
                        case 'editEnable':
                            form.get(groupName + '.enable').disable();
                            break;
                        case 'editDate':
                            form.get(groupName + '.dateMode').disable();
                            form.get(groupName + '.date').disable();
                            form.get(groupName + '.relativeDate').disable();
                            break;
                    }
                }
            });
        form.get(`${groupName}.${checkName}`).updateValueAndValidity({ emitEvent: true });
    }

    private initGroupDateModeChangesSubscriptions(form: UntypedFormGroup, groupName: string): void {
        form.get(groupName + '.dateMode').valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe((dateMode: SessionsDateMode) => {
                if (dateMode === SessionsDateMode.fixed) {
                    form.get(groupName + '.date').enable();
                    form.get(groupName + '.relativeDate').disable();
                } else if (dateMode === SessionsDateMode.relative) {
                    form.get(groupName + '.date').disable();
                    form.get(groupName + '.relativeDate').enable();
                    const timeUnit = form.get(groupName + '.relativeDate.timeUnit').value;
                    this.toggleAddFixedTimeCtrls(form, timeUnit, groupName);
                }
            });
        form.get(groupName + '.relativeDate.timeUnit').valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(timeUnit => {
                this.toggleAddFixedTimeCtrls(form, timeUnit, groupName);
            });
        form.get(groupName + '.relativeDate.addTime').valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe((isAddTimeChecked: boolean) => {
                if (isAddTimeChecked) {
                    form.get(groupName + '.relativeDate.fixedTime').enable();
                } else {
                    form.get(groupName + '.relativeDate.fixedTime').disable();
                }
            });
    }

    private toggleAddFixedTimeCtrls(form: UntypedFormGroup, timeUnit: SessionRelativeTimeUnits, groupName: string): void {
        if (timeUnit === SessionRelativeTimeUnits.minutes || timeUnit === SessionRelativeTimeUnits.hours) {
            form.get(groupName + '.relativeDate.addTime').disable();
            form.get(groupName + '.relativeDate.fixedTime').disable();
        } else if (timeUnit === SessionRelativeTimeUnits.days || timeUnit === SessionRelativeTimeUnits.weeks ||
            timeUnit === SessionRelativeTimeUnits.months) {
            form.get(groupName + '.relativeDate.addTime').enable();
            if (form.get(groupName + '.relativeDate.addTime').value === true) {
                form.get(groupName + '.relativeDate.fixedTime').enable();
            }
        }
    }

    private initAccessControlChangesSubscriptions(form: UntypedFormGroup): void {
        form.get('accessControl.editAccessTime').valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe((isChecked: boolean) => {
                if (isChecked) {
                    form.get('accessControl.accessTimeMode').enable();
                } else {
                    form.get('accessControl.accessTimeMode').disable();
                    form.get('accessControl.relativeAccessTime.opening.duration').disable();
                    form.get('accessControl.relativeAccessTime.opening.timeUnit').disable();
                    form.get('accessControl.relativeAccessTime.closing.duration').disable();
                    form.get('accessControl.relativeAccessTime.closing.timeUnit').disable();
                    form.get('accessControl.relativeAccessTime.closing.beforeAfter').disable();
                }
            });
        form.get('accessControl.accessTimeMode').valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe((accessTimeMode: SessionsAccessTimeMode) => {
                if (accessTimeMode === SessionsAccessTimeMode.automatic) {
                    form.get('accessControl.relativeAccessTime.opening.duration').disable();
                    form.get('accessControl.relativeAccessTime.opening.timeUnit').disable();
                    form.get('accessControl.relativeAccessTime.closing.duration').disable();
                    form.get('accessControl.relativeAccessTime.closing.timeUnit').disable();
                    form.get('accessControl.relativeAccessTime.closing.beforeAfter').disable();
                } else if (accessTimeMode === SessionsAccessTimeMode.relative) {
                    form.get('accessControl.relativeAccessTime.opening.duration').enable();
                    form.get('accessControl.relativeAccessTime.opening.timeUnit').enable();
                    form.get('accessControl.relativeAccessTime.closing.duration').enable();
                    form.get('accessControl.relativeAccessTime.closing.timeUnit').enable();
                    form.get('accessControl.relativeAccessTime.closing.beforeAfter').enable();
                }
            });

        form.get('accessControl.editAccessSpace').valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe((isChecked: boolean) => {
                if (isChecked) {
                    form.get('accessControl.accessSpaceMode').enable();
                } else {
                    form.get('accessControl.accessSpaceMode').disable();
                    form.get('accessControl.customAccessSpace').disable();
                }
            });
        form.get('accessControl.accessSpaceMode').valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe((accessSpaceMode: SessionsAccessSpaceMode) => {
                if (accessSpaceMode === SessionsAccessSpaceMode.default) {
                    form.get('accessControl.customAccessSpace').disable();
                } else if (accessSpaceMode === SessionsAccessSpaceMode.custom) {
                    form.get('accessControl.customAccessSpace').enable();
                }
            });

        form.get('accessControl.editAccessTime').updateValueAndValidity({ emitEvent: true });
        form.get('accessControl.editAccessSpace').updateValueAndValidity({ emitEvent: true });
    }

    private changeControlEnabledState(control: AbstractControl, isEnabled: boolean): void {
        if (isEnabled) {
            control.enable();
        } else {
            control.disable();
        }
    }

    private getRelativeDateToken(formGroupValues: { [key: string]: string }): string {
        let relDateToken = formGroupValues['duration'];
        relDateToken += ':' + formGroupValues['timeUnit'];
        if (formGroupValues['beforeAfter']) {
            relDateToken += ':' + formGroupValues['beforeAfter'];
        } else {
            relDateToken += ':' + SessionBeforeAfter.before;
        }
        if (formGroupValues['addTime']) {
            relDateToken += ':' + moment(formGroupValues['fixedTime'], 'HH:mm').format('HH.mm');
        }

        return relDateToken;
    }

    private touchAndValidateRates(): void {
        const ratesControl = this.generalForm.get('rates');
        ratesControl.markAsDirty();
        ratesControl.markAsTouched();
        ratesControl.updateValueAndValidity();
    }

    private static requiredMap(control: AbstractControl): ValidationErrors | null {
        return control.value == null || control.value.size < 1 ? { required: true } : null;
    }
}
