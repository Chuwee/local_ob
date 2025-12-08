import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import {
    SeasonTicketChangeSeatPrice, SeasonTicketsService, PutSeasonTicketChangeSeats
} from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import { EphemeralMessageService, ContextNotificationComponent } from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { SeatStatus } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { AsyncPipe } from '@angular/common';
import {
    ChangeDetectionStrategy, Component, OnDestroy, OnInit, QueryList, ViewChildren, inject, DestroyRef
} from '@angular/core';
import { FormBuilder, Validators, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatCheckbox } from '@angular/material/checkbox';
import {
    MatAccordion, MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle
} from '@angular/material/expansion';
import { MatError, MatFormField } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatProgressBar } from '@angular/material/progress-bar';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { MatOption, MatSelect } from '@angular/material/select';
import { MatTooltip } from '@angular/material/tooltip';
import { RouterModule } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, forkJoin, throwError } from 'rxjs';
import { distinctUntilChanged, filter, first, shareReplay, tap } from 'rxjs/operators';
import { ChangeSeatPriceZoneMatrixComponent } from './change-seat-price-zone-matrix/change-seat-price-zone-matrix.component';
import { MatSlideToggle } from '@angular/material/slide-toggle';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';

@Component({
    selector: 'app-season-ticket-change-seats',
    templateUrl: './season-ticket-change-seats.component.html',
    styleUrls: ['./season-ticket-change-seats.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormContainerComponent, ReactiveFormsModule, TranslatePipe, FormControlErrorsComponent,
        RouterModule, ContextNotificationComponent, ChangeSeatPriceZoneMatrixComponent, EllipsifyDirective,
        AsyncPipe, MatProgressBar, MatAccordion, MatExpansionPanel, MatExpansionPanelTitle, MatCheckbox, MatFormField,
        MatError, MatRadioGroup, MatRadioButton, MatSelect, MatOption, MatExpansionPanelHeader, MatIcon,
        MatProgressSpinner, MatTooltip, MatInput, MatSlideToggle
    ]
})
export class SeasonTicketChangeSeatsComponent implements OnInit, OnDestroy {
    @ViewChildren(MatExpansionPanel)
    private _matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    readonly #onDestroy = inject(DestroyRef);
    readonly #fb = inject(FormBuilder);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);
    readonly #seasonTicketSrv = inject(SeasonTicketsService);
    readonly #venueTemplateSrv = inject(VenueTemplatesService);
    readonly #ephemeralMessageSrv = inject(EphemeralMessageService);

    #finalSeasonTicketPricesData: SeasonTicketChangeSeatPrice[] = [];

    readonly $seasonTicket = toSignal(this.#seasonTicketSrv.seasonTicket.get$());
    readonly form = this.#fb.group({
        enable_max_value: false,
        max_value: [1, Validators.required],
        changed_seat_quota: this.#fb.group({
            enable: null as boolean,
            id: [null as number, Validators.required]
        }),
        // TODO: functionality temporarily discarded. it will return
        //changed_seat_status: [null as string, Validators.required],
        //changed_seat_block_reason_id: [null as number, Validators.required],
        /*limit_change_seat_quotas: this._fb.group({
            enable: null as boolean,
            quota_ids: this._fb.group({})
        }, { validators: this.getQuotaValidator() }),*/
        change_seats_prices_data_form: this.#fb.group({})
    });

    readonly allowChangeSeatForm = this.#fb.group({
        allow_change_seat: null as boolean
    })

    readonly isGenerationStatusInProgress$ = this.#seasonTicketSrv.seasonTicketStatus.isGenerationStatusInProgress$().pipe(distinctUntilChanged());
    readonly isGenerationStatusReady$ = this.#seasonTicketSrv.seasonTicketStatus.isGenerationStatusReady$().pipe(distinctUntilChanged());
    readonly isLoadingOrSaving$ = booleanOrMerge([
        this.#seasonTicketSrv.seasonTicketChangeSeat.loading$(),
        this.#seasonTicketSrv.seasonTicket.inProgress$(),
        this.#seasonTicketSrv.seasonTicketStatus.inProgress$(),
        this.#seasonTicketSrv.seasonTicketChangeSeatPrices.loading$()
    ]);

    readonly quotas$ = this.#venueTemplateSrv.getVenueTemplateQuotas$()
        .pipe(
            tap(value => value === null && this.#venueTemplateSrv.loadVenueTemplateQuotas(this.$seasonTicket().venue_templates[0].id)),
            first(Boolean),
            /*tap(quotas => quotas.forEach(quota =>
                this.form.controls.limit_change_seat_quotas.controls.quota_ids.addControl(quota.id.toString(), new FormControl()))
            ),*/
            shareReplay(1)
        );

    readonly blockingReasons$ = this.#venueTemplateSrv.getVenueTemplateBlockingReasons$()
        .pipe(
            tap(value => value === null && this.#venueTemplateSrv.loadVenueTemplateBlockingReasons(this.$seasonTicket().venue_templates[0].id)),
            first(Boolean),
            shareReplay(1)
        );

    readonly seatStatus = SeatStatus;

    seatStatuses = [SeatStatus.free, SeatStatus.promotorLocked, SeatStatus.kill];
    changeSeatsPricesData$: Observable<SeasonTicketChangeSeatPrice[]>;

    ngOnInit(): void {
        this.allowChangeSeatForm.controls.allow_change_seat.reset(this.$seasonTicket().settings.operative.allow_change_seat);
        this.#seasonTicketSrv.seasonTicketChangeSeat.load(this.$seasonTicket().id);
        this.form.markAsUntouched();
        this.form.markAsPristine();

        this.#seasonTicketSrv.seasonTicketChangeSeat.get$()
            .pipe(
                filter(Boolean),
                takeUntilDestroyed(this.#onDestroy)
            )
            .subscribe(changeSeats => {
                this.form.patchValue({
                    enable_max_value: changeSeats.enable_max_value,
                    max_value: changeSeats.max_value || 1,
                    changed_seat_quota: {
                        enable: changeSeats.changed_seat_quota?.enable,
                        id: changeSeats.changed_seat_quota?.id
                    }
                    // TODO: functionality temporarily discarded. it will return
                    //changed_seat_status: changeSeats.changed_seat_status,
                    //changed_seat_block_reason_id: changeSeats.changed_seat_block_reason_id,
                    /*limit_change_seat_quotas: {
                        enable: changeSeats.limit_change_seat_quotas?.enable || false,
                        quota_ids: []
                    }*/
                });

                /*changeSeats.limit_change_seat_quotas?.quota_ids.forEach(quota => {
                    this.form.controls.limit_change_seat_quotas.controls.quota_ids.get(quota.toString())?.patchValue(true);
                });*/

                if (!changeSeats?.enable_max_value) {
                    this.form.controls.max_value.disable();
                }
            });

        // Enable max value
        this.form.controls.enable_max_value.valueChanges
            .pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe(value => {
                if (!value) {
                    this.form.controls.max_value.disable();
                } else {
                    this.form.controls.max_value.enable();
                }
            });

        // TODO: functionality temporarily discarded. it will return
        /*this.form.controls.changed_seat_status.valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(value => {
                if (value === SeatStatus.promotorLocked) {
                    this.form.controls.changed_seat_block_reason_id.enable({ emitEvent: false });
                } else {
                    this.form.controls.changed_seat_block_reason_id.disable({ emitEvent: false });
                }
            });*/

        this.form.controls.changed_seat_quota.valueChanges
            .pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe(value => {
                const changedSeatQuotaFormGroup = this.form.controls.changed_seat_quota as FormGroup;
                if (value.enable) {
                    changedSeatQuotaFormGroup.controls['id'].enable({ emitEvent: false });
                } else {
                    changedSeatQuotaFormGroup.controls['id'].disable({ emitEvent: false });
                }
            });

        /*this.form.controls.limit_change_seat_quotas.valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(value => {
                if (value.enable) {
                    this.form.controls.limit_change_seat_quotas.controls.quota_ids.enable({ emitEvent: false });
                } else {
                    this.form.controls.limit_change_seat_quotas.controls.quota_ids.disable({ emitEvent: false });
                }
            });*/
    }

    ngOnDestroy(): void {
        this.#venueTemplateSrv.clearVenueTemplateQuotas();
    }

    handleStatusChange(isActive: boolean): void {
        this.#seasonTicketSrv.seasonTicket.save(
            this.$seasonTicket().id.toString(), { settings: { operative: { allow_change_seat: isActive } } }
        ).subscribe(() => {
            const successMessage = isActive ? 'SEASON_TICKET.CHANGE_SEATS.ENABLED_SUCCESS' : 'SEASON_TICKET.CHANGE_SEATS.DISABLED_SUCCESS';
            this.#ephemeralMessageSrv.showSuccess({ msgKey: successMessage });
            this.#seasonTicketSrv.seasonTicket.load(this.$seasonTicket().id.toString());
        });
    }

    save(): void {
        this.save$().subscribe(() => this.cancel());
    }

    save$(): Observable<unknown> {
        if (this.form.valid) {
            const obs$: Observable<void>[] = [];
            const changeSeats = this.form.value;
            const updateChangeSeat: PutSeasonTicketChangeSeats = {
                enable_max_value: changeSeats.enable_max_value || false,
                max_value: changeSeats.max_value || null,
                changed_seat_quota: changeSeats.changed_seat_quota || null
                // TODO: functionality temporarily discarded. it will return
                /* changed_seat_status: changeSeats.changed_seat_status || null,
                  changed_seat_block_reason_id: changeSeats.changed_seat_block_reason_id || null,
                  limit_change_seat_quotas: {
                      enable: changeSeats.limit_change_seat_quotas.enable || false,
                      quota_ids: this.getActiveQuotasIds() || []
                  } */
            };

            if (this.form.controls.change_seats_prices_data_form.dirty) {
                obs$.push(
                    this.#seasonTicketSrv.seasonTicketChangeSeatPrices.update(this.$seasonTicket().id, this.#finalSeasonTicketPricesData)
                        .pipe((tap(() => this.#seasonTicketSrv.seasonTicketChangeSeatPrices.load(this.$seasonTicket().id))))
                );
            }
            obs$.push(this.#seasonTicketSrv.seasonTicketChangeSeat.update(this.$seasonTicket().id, updateChangeSeat));

            return forkJoin(obs$)
                .pipe(tap(() => this.#ephemeralMessageService.showSaveSuccess()));
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanelQueryList);
            return throwError(() => 'invalid form');
        }
    }

    cancel(): void {
        if (this.form.controls.change_seats_prices_data_form.dirty) {
            this.#seasonTicketSrv.seasonTicketChangeSeatPrices.load(this.$seasonTicket().id);
        }
        this.form.markAsPristine();
        this.form.markAsUntouched();
        this.#seasonTicketSrv.seasonTicketChangeSeat.load(Number(this.$seasonTicket().id));
    }

    setFinalSeasonTicketPricesData(pricesData: SeasonTicketChangeSeatPrice[]): void {
        this.#finalSeasonTicketPricesData = pricesData;
    }

    /*private getQuotaValidator() {
        return (fg: FormGroup): ValidationErrors | null => {
            if (!fg.value.enable) {
                return null;
            }

            let selected = [];
            if (fg.value.quota_ids) {
                selected = Object.keys(fg.value.quota_ids).filter(id => fg.value.quota_ids[id]);
            }

            return selected.length > 0 ? null : { quotas: true };
        };
    }*/

    /*private getActiveQuotasIds(): number[] {
        return this.form.value.limit_change_seat_quotas.quota_ids ? Object.keys(this.form.value.limit_change_seat_quotas.quota_ids)
            .filter(value => this.form.value.limit_change_seat_quotas.quota_ids[value])
            .map(value => +value) : [];
    }*/
}
