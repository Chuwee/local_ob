import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import {
    delayType, DelayType, PutSeasonTicketReleaseSeats
} from '@admin-clients/cpanel/promoters/season-tickets/locality-management/data-access';
import {
    ExcludedActionType, ExcludedSessionsConfigComponent
} from '@admin-clients/cpanel/promoters/season-tickets/locality-management/excluded-sessions-config/feature';
import {
    seasonTicketSessionsProviders, SeasonTicketSessionsService
} from '@admin-clients/cpanel/promoters/season-tickets/sessions/data-access';
import {
    ContextNotificationComponent, DialogSize, EphemeralMessageService, MessageDialogService, PercentageInputComponent
} from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { LocalNumberPipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge, maxDecimalLength, rangeValidator } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit, QueryList, ViewChildren } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatDivider } from '@angular/material/divider';
import {
    MatAccordion, MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle
} from '@angular/material/expansion';
import { MatError, MatFormField, MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatProgressBar } from '@angular/material/progress-bar';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { forkJoin, Observable, switchMap, throwError } from 'rxjs';
import { distinctUntilChanged, filter, map, tap } from 'rxjs/operators';
import { MatSlideToggle } from '@angular/material/slide-toggle';

@Component({
    selector: 'app-release-seats-configuration',
    imports: [
        FormContainerComponent, AsyncPipe, ContextNotificationComponent, TranslatePipe, MatProgressBar, MatIcon,
        MatAccordion, MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle, MatCheckbox,
        ReactiveFormsModule, MatRadioGroup, MatRadioButton, MatFormField, MatInput, MatError, MatDivider,
        FormControlErrorsComponent, PercentageInputComponent, LocalNumberPipe, MatTooltip, MatLabel,
        ExcludedSessionsConfigComponent, MatProgressSpinner, MatSlideToggle
    ],
    providers: [seasonTicketSessionsProviders],
    templateUrl: './release-seats-configuration.component.html',
    styleUrls: ['./release-seats-configuration.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ReleaseSeatsConfigurationComponent implements OnInit {
    @ViewChildren(MatExpansionPanel)
    private readonly _matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    readonly #onDestroy = inject(DestroyRef);
    readonly #fb = inject(FormBuilder);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);
    readonly #seasonTicketSessionsSrv = inject(SeasonTicketSessionsService);
    readonly #seasonTicketSrv = inject(SeasonTicketsService);
    readonly #ephemeralMessageSrv = inject(EphemeralMessageService);
    readonly #msgDialogSrv = inject(MessageDialogService);

    readonly $seasonTicket = toSignal(this.#seasonTicketSrv.seasonTicket.get$());
    readonly $excludedSessions = toSignal(this.#seasonTicketSrv.seasonTicketReleaseSeat.get$().pipe(
        filter(Boolean), map(releaseSeats => releaseSeats.excluded_sessions)));

    readonly form = this.#fb.group({
        customer_percentage: [30, [Validators.required, Validators.min(1)]],
        enable_release_delay: [false],
        release_delay_type: ['FROM' as DelayType, Validators.required],
        release_delay: this.#fb.group({
            from: [null as number, [Validators.required, Validators.min(1), maxDecimalLength(0)]],
            to: [3, [Validators.required, Validators.min(1), maxDecimalLength(0)]],
            range: this.#fb.group({
                min_delay_time: [null as number, [Validators.required, Validators.min(1), maxDecimalLength(0)]],
                max_delay_time: [3, [Validators.required, Validators.min(1), maxDecimalLength(0)]]
            }, { validators: [rangeValidator('max_delay_time', 'min_delay_time', false)] })
        }),
        enable_recover_delay: [false],
        recover_max_delay_time: [3, [Validators.min(1), maxDecimalLength(0)]],
        enable_max_releases: false,
        max_releases: [0, [Validators.required, Validators.min(1)]],
        earnings_limit_enabled: false,
        earnings_limit: [0, [Validators.required, Validators.min(1)]]
    });

    readonly allowReleaseSeatForm = this.#fb.group({
        allow_release_seat: null as boolean
    });

    readonly isGenerationStatusInProgress$ = this.#seasonTicketSrv.seasonTicketStatus.isGenerationStatusInProgress$()
        .pipe(distinctUntilChanged());

    readonly isGenerationStatusReady$ = this.#seasonTicketSrv.seasonTicketStatus.isGenerationStatusReady$().pipe(distinctUntilChanged());
    readonly isLoadingOrSaving$ = booleanOrMerge([
        this.#seasonTicketSrv.seasonTicket.inProgress$(),
        this.#seasonTicketSrv.seasonTicketStatus.inProgress$(),
        this.#seasonTicketSrv.seasonTicketReleaseSeat.loading$(),
        this.#seasonTicketSessionsSrv.sessions.loading$()
    ]);

    ngOnInit(): void {
        this.#seasonTicketSrv.seasonTicketReleaseSeat.load(this.$seasonTicket().id);
        this.allowReleaseSeatForm.controls.allow_release_seat.reset(this.$seasonTicket()?.settings.operative.allow_release_seat);
        this.form.markAsUntouched();
        this.form.markAsPristine();

        this.#seasonTicketSrv.seasonTicketReleaseSeat.get$()
            .pipe(
                filter(Boolean),
                takeUntilDestroyed(this.#onDestroy)
            )
            .subscribe(releaseSeats => {
                const minDelayTime = releaseSeats.release_min_delay_time || null;
                const maxDelayTime = releaseSeats.release_max_delay_time || null;
                const config = this.#getReleaseDelayConfig(minDelayTime, maxDelayTime);

                for (const [controlName, obj] of Object.entries(config)) {
                    let control = this.form.controls[controlName];
                    if (delayType.find(type => type.toLowerCase() === controlName)) {
                        control = this.form.controls.release_delay.controls[controlName];
                    }
                    control.patchValue(obj.value);
                    obj.enabled ? control.enable() : control.disable();
                }

                this.form.patchValue({
                    customer_percentage: releaseSeats.customer_percentage || 30,
                    enable_release_delay: releaseSeats.enable_release_delay,
                    enable_recover_delay: releaseSeats.enable_recover_delay,
                    recover_max_delay_time: releaseSeats.recover_max_delay_time || null,
                    enable_max_releases: releaseSeats.enable_max_releases,
                    max_releases: releaseSeats.max_releases || 0,
                    earnings_limit_enabled: releaseSeats.earnings_limit?.enabled,
                    earnings_limit: releaseSeats.earnings_limit?.percentage
                });
                this.form.updateValueAndValidity();
            });

        if (!this.form.controls.enable_max_releases.value) {
            this.form.controls.max_releases.disable({ emitEvent: false });
        }

        if (!this.form.controls.enable_recover_delay.value) {
            this.form.controls.recover_max_delay_time.disable();
        }

        this.form.controls.earnings_limit_enabled.valueChanges
            .pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe(value => {
                if (value) {
                    this.form.controls.earnings_limit.enable({ emitEvent: false });
                } else {
                    this.form.controls.earnings_limit.disable({ emitEvent: false });
                }
            });

        this.form.controls.enable_max_releases.valueChanges
            .pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe(enabled => {
                if (enabled) {
                    this.form.controls.max_releases.enable({ emitEvent: false });
                } else {
                    this.form.controls.max_releases.disable({ emitEvent: false });
                }
            });

        // Enable recover delay
        this.form.controls.enable_recover_delay.valueChanges
            .pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe(value => {
                if (!value) {
                    this.form.controls.recover_max_delay_time.disable();
                } else {
                    this.form.controls.recover_max_delay_time.enable();
                }
            });

        this.form.controls.enable_release_delay.valueChanges
            .pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe(value => {
                if (!value) {
                    this.form.controls.release_delay_type.disable();
                    this.form.controls.release_delay.disable();
                } else {
                    this.form.controls.release_delay_type.enable();
                    this.form.controls.release_delay.controls
                    [(this.form.controls.release_delay_type.value)?.toString().toLowerCase()]?.enable();
                }
            });

        this.form.controls.release_delay_type.valueChanges
            .pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe(type => {
                this.form.controls.release_delay.disable();
                this.form.controls.release_delay.controls[(type)?.toString().toLowerCase()]?.enable();
            });
    }

    save(): void {
        this.save$().subscribe(() => this.refresh());
    }

    save$(): Observable<unknown> {
        if (this.form.valid) {
            const obs$: Observable<void>[] = [];
            const releaseSeats = this.form.value;

            const minDelayTime = releaseSeats.release_delay?.from ?? releaseSeats.release_delay?.range?.min_delay_time ?? null;
            const maxDelayTime = releaseSeats.release_delay?.to ?? releaseSeats.release_delay?.range?.max_delay_time ?? null;

            const updateReleaseSeat: PutSeasonTicketReleaseSeats = {
                customer_percentage: releaseSeats.customer_percentage || 30,
                enable_release_delay: releaseSeats.enable_release_delay,
                release_min_delay_time: minDelayTime,
                release_max_delay_time: maxDelayTime,
                enable_recover_delay: releaseSeats.enable_recover_delay,
                recover_max_delay_time: releaseSeats.enable_recover_delay ? releaseSeats.recover_max_delay_time : null,
                enable_max_releases: releaseSeats.enable_max_releases || false,
                max_releases: releaseSeats.max_releases,
                earnings_limit: {
                    enabled: releaseSeats.earnings_limit_enabled || false,
                    percentage: releaseSeats.earnings_limit
                }
            };

            obs$.push(this.#seasonTicketSrv.seasonTicketReleaseSeat.update(this.$seasonTicket().id, updateReleaseSeat));

            return forkJoin(obs$)
                .pipe(tap(() => this.#ephemeralMessageService.showSaveSuccess()));
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanelQueryList);
            return throwError(() => 'invalid form');
        }
    }

    saveExcludedSessions(event: {type: ExcludedActionType, excludedSessions: number[]}): void {
        const successMessage = 'SEASON_TICKET.EXCLUDED_SESSIONS.RELEASE_SEAT_' + event.type + '_SAVED_SUCCESS';
        this.#seasonTicketSrv.seasonTicketReleaseSeat.update(this.$seasonTicket().id, { excluded_sessions: event.excludedSessions })
            .subscribe(() => {
                this.#ephemeralMessageSrv.showSuccess({ msgKey: successMessage });
                this.refresh();
            });
    }

    refresh(): void {
        this.form.markAsPristine();
        this.form.markAsUntouched();
        this.#seasonTicketSrv.seasonTicketReleaseSeat.load(Number(this.$seasonTicket().id));
    }

    handleStatusChange(isActive: boolean): void {
        const save$ = () =>
            this.#seasonTicketSrv.seasonTicket.save(
                this.$seasonTicket().id.toString(),
                { settings: { operative: { allow_release_seat: isActive } } }
            );

        const onSuccess = () => {
            const successMessage = isActive ? 'SEASON_TICKET.RELEASE_SEAT.ENABLED_SUCCESS' : 'SEASON_TICKET.RELEASE_SEAT.DISABLED_SUCCESS';
            this.#ephemeralMessageSrv.showSuccess({ msgKey: successMessage });
            this.#seasonTicketSrv.seasonTicket.load(this.$seasonTicket().id.toString());
        };

        if (!isActive) {
            this.#msgDialogSrv.showInfo({
                size: DialogSize.SMALL,
                title: 'SEASON_TICKET.GENERAL_DATA.RELEASE_ALERT_TITLE',
                message: 'SEASON_TICKET.GENERAL_DATA.RELEASE_ALERT_MESSAGE'
            })
                .pipe(switchMap(save$))
                .subscribe(onSuccess);
        } else {
            save$().subscribe(onSuccess);
        }
    }

    #getReleaseDelayConfig(minDelayTime: number | null, maxDelayTime: number | null):
        Record<string, { value: any; enabled: boolean }> {
        const isRange = minDelayTime !== null && maxDelayTime !== null;
        const isFrom = minDelayTime !== null && maxDelayTime === null;
        const isTo = maxDelayTime !== null && minDelayTime === null;

        return {
            release_delay_type: {
                value: isRange ? 'RANGE' : isFrom ? 'FROM' : isTo ? 'TO' : null,
                enabled: isRange || isFrom || isTo
            },
            range: {
                value: isRange ? { min_delay_time: minDelayTime, max_delay_time: maxDelayTime }
                    : { min_delay_time: null, max_delay_time: null },
                enabled: isRange
            },
            from: {
                value: isFrom ? minDelayTime : null,
                enabled: isFrom
            },
            to: {
                value: isTo ? maxDelayTime : null,
                enabled: isTo
            }
        };
    }

}
