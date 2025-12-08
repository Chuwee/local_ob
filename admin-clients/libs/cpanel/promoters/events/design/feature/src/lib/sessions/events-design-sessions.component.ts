import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import {
    EventsService, PutEvent, SessionCalendarSelectionType, SessionCalendarType, SessionSelectionCardOrientation,
    SessionSelectionType
} from '@admin-clients/cpanel/promoters/events/data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { EphemeralMessageService, ObMatDialogConfig, RadioImageButtonComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, QueryList, ViewChildren, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatExpansionPanel } from '@angular/material/expansion';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, combineLatest, filter, map, tap, throwError } from 'rxjs';
import { SessionAvailabilityInfoDialogComponent } from './session-availability-info-dialog/session-availability-info-dialog.component';

type ListCardFormat = 'NONE_IMAGE' | SessionSelectionCardOrientation;

@Component({
    selector: 'ob-events-design-sessions',
    templateUrl: './events-design-sessions.component.html',
    styleUrls: ['./events-design-sessions.component.scss'],
    imports: [
        FormContainerComponent,
        RadioImageButtonComponent,
        ReactiveFormsModule,
        FlexLayoutModule,
        TranslatePipe,
        MaterialModule,
        CommonModule
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EventsDesignSessionsComponent {
    readonly #eventsSrv = inject(EventsService);
    readonly #entitiesSrv = inject(EntitiesBaseService);
    readonly #ephemeralMsgSrv = inject(EphemeralMessageService);
    readonly #fb = inject(FormBuilder);
    readonly #matDialog = inject(MatDialog);

    @ViewChildren(MatExpansionPanel)
    private readonly _matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    #eventId: number;

    readonly loading$ = this.#eventsSrv.event.inProgress$();
    readonly eventConfig$ = this.#eventsSrv.event.get$().pipe(
        filter(Boolean),
        map(event => {
            this.#eventId = event.id;
            return event.settings;
        })
    );

    readonly isV4$ = this.#entitiesSrv.getEntity$().pipe(filter(Boolean), map(entity => !!entity.settings.enable_v4_configs));

    readonly form = this.#fb.group({
        defaultDisplay: null as SessionSelectionType,
        list: this.#fb.group({
            enabled: false,
            cardFormat: [null as ListCardFormat | null, Validators.required]
        }),
        calendar: this.#fb.group({
            enabled: false,
            dayFormat: [null as SessionCalendarType, Validators.required],
            timeFormat: [null as SessionCalendarSelectionType, Validators.required]
        }),
        common: this.#fb.group({
            showAvailability: false,
            showMinPrices: false,
            changeDate: this.#fb.group({
                enabled: false,
                calendar: this.#fb.group({
                    dayFormat: [null as SessionCalendarType, Validators.required],
                    timeFormat: [null as SessionCalendarSelectionType, Validators.required]
                })
            })
        })
    });

    get listConfig(): Partial<{ enabled: boolean; image: boolean; cardFormat: ListCardFormat }> {
        return this.form.controls.list.value;
    }

    get calendarConfig(): Partial<{ enabled: boolean; dayFormat: SessionCalendarType; timeFormat: SessionCalendarSelectionType }> {
        return this.form.controls.calendar.value;
    }

    get changeDateConfig(): Partial<{ enabled: boolean; dayFormat: SessionCalendarType; timeFormat: SessionCalendarSelectionType }> {
        return this.form.controls.common.controls.changeDate.controls.calendar.value;
    }

    isFestival = false;

    constructor() {
        combineLatest([
            this.form.controls.list?.controls.enabled.valueChanges,
            this.form.controls.calendar?.controls.enabled?.valueChanges
        ])
            .pipe(takeUntilDestroyed())
            .subscribe({
                next: ([enabledList, enabledCalendar]) => {
                    if (enabledList && !enabledCalendar) {
                        this.form.controls.defaultDisplay.patchValue('LIST');
                        this.form.controls.list.controls.enabled.disable({ emitEvent: false });
                        this.form.controls.calendar.controls.enabled.enable({ emitEvent: false });
                    } else if (!enabledList && enabledCalendar) {
                        this.form.controls.defaultDisplay.patchValue('CALENDAR');
                        this.form.controls.calendar.controls.enabled.disable({ emitEvent: false });
                        this.form.controls.list.controls.enabled.enable({ emitEvent: false });
                    } else {
                        this.form.controls.calendar.controls.enabled.enable({ emitEvent: false });
                        this.form.controls.list.controls.enabled.enable({ emitEvent: false });
                    }
                }
            });

        this.eventConfig$
            .pipe(takeUntilDestroyed())
            .subscribe(
                ({ whitelabel_settings: { ui_settings: settings }, festival }) => {
                    const selectionList = settings?.session_selection.list;
                    const listCardFormat: ListCardFormat = selectionList?.contains_image ? selectionList?.card_design : 'NONE_IMAGE';
                    const sessionsConfig = {
                        defaultDisplay: settings?.session_selection.type,
                        list: {
                            enabled: selectionList.enabled || settings?.session_selection.type === 'LIST',
                            cardFormat: listCardFormat
                        },
                        calendar: {
                            enabled: settings?.session_selection.calendar?.enabled || settings?.session_selection.type === 'CALENDAR',
                            dayFormat: settings?.session_selection.calendar?.type,
                            timeFormat: settings?.session_selection.calendar?.session_select
                        },
                        common: {
                            showAvailability: settings?.session_selection?.show_availability,
                            showMinPrices: settings?.session?.show_price_from,
                            changeDate: {
                                enabled: settings?.seat_selection?.change_session === 'ALLOW',
                                calendar: {
                                    dayFormat: settings?.seat_selection?.calendar?.type,
                                    timeFormat: settings?.seat_selection?.calendar?.session_select
                                }
                            }
                        }
                    };
                    this.isFestival = !!festival;
                    this.form.patchValue(sessionsConfig);
                    this.form.markAsPristine();
                }
            );
    }

    save(): void {
        this.save$().subscribe();
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            const formValue = this.form.value;
            const updatedEvent: PutEvent = {
                settings: {
                    whitelabel_settings: {
                        ui_settings: {
                            session: { show_price_from: !!formValue.common.showMinPrices },
                            session_selection: {
                                type: formValue.defaultDisplay,
                                show_availability: formValue.common.showAvailability,
                                restrict_selection_type: this.isFestival ||
                                    (formValue.list.enabled && !formValue.calendar?.enabled) ||
                                    (!formValue.list.enabled && formValue.calendar?.enabled)
                            },
                            seat_selection: {
                                change_session: formValue.common.changeDate.enabled ? 'ALLOW' : 'NONE',
                                calendar: {
                                    type: formValue.common.changeDate.calendar.dayFormat,
                                    session_select: formValue.common.changeDate.calendar.timeFormat
                                }
                            }
                        }
                    }
                }
            };
            if (this.isFestival) {
                updatedEvent.settings.whitelabel_settings.ui_settings.session_selection.list = { contains_image: true };
            } else {
                const cardFormat = formValue.list.cardFormat;
                updatedEvent.settings.whitelabel_settings.ui_settings.session_selection.list =
                {
                    enabled: this.form.controls.list?.controls.enabled.value,
                    contains_image: !(cardFormat === 'NONE_IMAGE'),
                    card_design: (cardFormat === 'NONE_IMAGE') ? 'HORIZONTAL' : cardFormat,
                    media: (cardFormat === 'NONE_IMAGE') ? 'NONE' : 'IMAGE'
                };
                updatedEvent.settings.whitelabel_settings.ui_settings.session_selection.calendar =
                {
                    enabled: this.form.controls.calendar?.controls.enabled.value,
                    type: formValue.calendar.dayFormat,
                    session_select: formValue.calendar.timeFormat
                };
            }
            return this.#eventsSrv.event
                .update(this.#eventId, updatedEvent)
                .pipe(
                    tap(() => {
                        this.#ephemeralMsgSrv.showSaveSuccess();
                        this.#eventsSrv.event.load(this.#eventId.toString());
                    })
                );
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanelQueryList);
            return throwError(() => 'invalid form');
        }
    }

    cancel(): void {
        this.#eventsSrv.event.load(this.#eventId.toString());
    }

    setDefaultDisplay(config: SessionSelectionType): void {
        this.form.controls.defaultDisplay.patchValue(config);
        this.form.controls.defaultDisplay.markAsDirty();
    }

    openSessionAvailabilityInfoDialog(event: Event): void {
        event.preventDefault();
        this.#matDialog.open(SessionAvailabilityInfoDialogComponent, new ObMatDialogConfig({}));
    }

}
