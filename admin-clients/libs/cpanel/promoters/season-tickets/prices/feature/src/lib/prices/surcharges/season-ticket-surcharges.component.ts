import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import {
    PutSeasonTicketChangeSeats, SeasonTicketSurcharge, SeasonTicketSurchargeType, SeasonTicketsService, SeasonTicketStatus
} from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import { EntitiesBaseService, EntitySurchargeType } from '@admin-clients/shared/common/data-access';
import {
    EphemeralMessageService, cleanRangesBeforeSave,
    ContextNotificationComponent, RangeTableComponent, CurrencyInputComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { LocalCurrencyPipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { RangeElement } from '@admin-clients/shared-utility-models';
import { CommonModule } from '@angular/common';
import {
    AfterViewInit,
    ChangeDetectionStrategy, ChangeDetectorRef, Component, DestroyRef, ElementRef, inject, OnDestroy, OnInit,
    viewChild
} from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, firstValueFrom, forkJoin, merge, Observable, throwError } from 'rxjs';
import { distinctUntilChanged, filter, first, map, shareReplay, switchMap, tap, withLatestFrom } from 'rxjs/operators';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormContainerComponent,
        ContextNotificationComponent,
        FlexLayoutModule,
        MaterialModule,
        CommonModule,
        RangeTableComponent,
        ReactiveFormsModule,
        TranslatePipe,
        CurrencyInputComponent,
        LocalCurrencyPipe,
        FormControlErrorsComponent
    ],
    selector: 'app-season-ticket-surcharges',
    templateUrl: './season-ticket-surcharges.component.html',
    styleUrls: ['./season-ticket-surcharges.component.scss']
})
export class SeasonTicketSurchargesComponent implements OnInit, OnDestroy, WritingComponent, AfterViewInit {
    readonly #seasonTicketsSrv = inject(SeasonTicketsService);
    readonly #entitiesService = inject(EntitiesBaseService);
    readonly #fb = inject(UntypedFormBuilder);
    readonly #ref = inject(ChangeDetectorRef);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #destroyRef = inject(DestroyRef);
    readonly #route = inject(ActivatedRoute);
    private readonly _secondaryMarketSurcharges = viewChild<ElementRef>('secondaryMarket');

    #importBtnClicked = false;

    readonly types = SeasonTicketSurchargeType;
    readonly form = this.#fb.group({
        fixed_surcharge: this.#fb.control({ value: null as number, disabled: true }, Validators.min(0))
    });

    readonly isGenerationStatusReady$ = this.#seasonTicketsSrv.seasonTicketStatus.isGenerationStatusReady$().pipe(distinctUntilChanged());
    readonly isGenerationStatusInProgress$ = this.#seasonTicketsSrv.seasonTicketStatus.isGenerationStatusInProgress$()
        .pipe(distinctUntilChanged());

    readonly isLoadingOrSaving$ = booleanOrMerge([
        this.#seasonTicketsSrv.seasonTicket.inProgress$(),
        this.#seasonTicketsSrv.seasonTicketStatus.inProgress$(),
        this.#seasonTicketsSrv.isSeasonTicketSurchargesLoading$(),
        this.#seasonTicketsSrv.isSeasonTicketSurchargesSaving$(),
        this.#entitiesService.surcharges.inProgress$()
    ]).pipe(
        takeUntilDestroyed(this.#destroyRef),
        shareReplay(1)
    );

    readonly hasSecondaryMarket$ = this.#entitiesService.getEntity$().pipe(map(entity => entity.settings?.allow_secondary_market));
    readonly isSeasonTicketEditable$ = this.#seasonTicketsSrv.seasonTicketStatus.get$()
        .pipe(
            map(seasonTicketStatus => (
                !!seasonTicketStatus.status &&
                (
                    seasonTicketStatus.status === SeasonTicketStatus.setUp ||
                    seasonTicketStatus.status === SeasonTicketStatus.pendingPublication
                )
            ))
        );

    readonly surcharges$ = merge(
        this.#seasonTicketsSrv.getSeasonTicketSurcharges$(),
        this.#entitiesService.surcharges.get$()
            .pipe(
                filter(Boolean),
                withLatestFrom(this.#seasonTicketsSrv.getSeasonTicketSurcharges$().pipe(filter(Boolean))),
                map(([entitySurcharges, seasonTicketSurcharges]) => {
                    seasonTicketSurcharges
                        .find(surcharges => surcharges.type === SeasonTicketSurchargeType.generic)
                        .ranges = entitySurcharges.find(surcharges => surcharges.type === EntitySurchargeType.generic).ranges;
                    this.#importBtnClicked = true;
                    return seasonTicketSurcharges;
                })
            )
    );

    surcharges = new Map<SeasonTicketSurchargeType, RangeElement[]>();
    showChangeSeatSurcharges: boolean;

    $showSecondaryMarketSurcharges = toSignal(this.hasSecondaryMarket$);

    readonly currency$ = this.#seasonTicketsSrv.seasonTicket.get$()
        .pipe(map(seasonTicket => seasonTicket.currency_code));

    ngOnInit(): void {
        this.#entitiesService.surcharges.clear();
        this.#seasonTicketsSrv.clearSeasonTicketSurcharges();

        this.configureSurchargeLimit(this.types.generic);
        this.configureSurchargeLimit(this.types.promotion);
        this.configureSurchargeLimit(this.types.invitation);
        this.configureSurchargeLimit(this.types.changeSeat);
        this.configureSurchargeLimit(this.types.secondaryMarket);

        this.surcharges$
            .pipe(filter(Boolean), takeUntilDestroyed(this.#destroyRef))
            .subscribe(surcharges => {
                this.initializeMap(surcharges);
                surcharges.forEach(surcharge => {
                    this.surcharges.set(surcharge.type, surcharge.ranges);
                    this.setSurchargeLimit(surcharge);
                });
                if (this.#importBtnClicked) {
                    this.#importBtnClicked = false;
                    setTimeout(() => {
                        this.form.get(SeasonTicketSurchargeType.generic).markAsDirty();
                        this.#ref.markForCheck();
                    });
                } else {
                    this.form.markAsPristine();
                    setTimeout(() => this.#ref.markForCheck());
                }
            });

        this.#seasonTicketsSrv.seasonTicketStatus.isGenerationStatusReady$()
            .pipe(filter(Boolean), takeUntilDestroyed(this.#destroyRef))
            .subscribe(() => {
                this.#seasonTicketsSrv.seasonTicket.get$()
                    .pipe(first())
                    .subscribe(seasonTicket => {
                        this.#seasonTicketsSrv.loadSeasonTicketSurcharges(seasonTicket.id);
                        if (seasonTicket.settings.operative.allow_change_seat) {
                            this.#seasonTicketsSrv.seasonTicketChangeSeat.load(seasonTicket.id);
                        }
                    });
            });

        this.#seasonTicketsSrv.seasonTicket.get$()
            .pipe(first())
            .subscribe(seasonTicket => {
                this.showChangeSeatSurcharges = seasonTicket.settings.operative.allow_change_seat;
                if (seasonTicket.settings.operative.allow_change_seat) {
                    this.form.get('fixed_surcharge').enable();
                } else {
                    this.form.get('fixed_surcharge').disable();
                }
            });

        this.#seasonTicketsSrv.seasonTicketChangeSeat.get$()
            .pipe(filter(Boolean), takeUntilDestroyed(this.#destroyRef))
            .subscribe(changeSeats =>
                this.form.patchValue({ fixed_surcharge: changeSeats.fixed_surcharge })
            );
    }

    ngOnDestroy(): void {
        this.#seasonTicketsSrv.clearSeasonTicketSurcharges();
        this.#entitiesService.surcharges.clear();
    }

    async importEntitySurcharges(): Promise<void> {
        const seasonTicket = await firstValueFrom(this.#seasonTicketsSrv.seasonTicket.get$());
        this.#entitiesService.surcharges.load(seasonTicket.entity.id);
    }

    async cancel(): Promise<void> {
        const seasonTicket = await firstValueFrom(this.#seasonTicketsSrv.seasonTicket.get$());
        this.#seasonTicketsSrv.loadSeasonTicketSurcharges(seasonTicket.id);
        if (seasonTicket.settings.operative.allow_change_seat) {
            this.#seasonTicketsSrv.seasonTicketChangeSeat.load(seasonTicket.id);
        }
    }

    async save(): Promise<void> {
        const seasonTicket = await firstValueFrom(this.#seasonTicketsSrv.seasonTicket.get$());
        this.save$()
            .subscribe(() => {
                this.#seasonTicketsSrv.loadSeasonTicketSurcharges(seasonTicket.id);
                if (seasonTicket.settings.operative.allow_change_seat) {
                    this.#seasonTicketsSrv.seasonTicketChangeSeat.load(seasonTicket.id);
                }
                this.#ref.markForCheck();
            });
    }

    save$(): Observable<unknown> {
        if (this.form.valid) {
            const obs$: Observable<unknown>[] = [];
            const seasonTicketSurcharges: SeasonTicketSurcharge[] = [];
            const seasonTicketChangeSeat: PutSeasonTicketChangeSeats = { fixed_surcharge: this.form.get('fixed_surcharge').value };
            for (const [type] of this.surcharges) {
                if (type !== SeasonTicketSurchargeType.secondaryMarket || this.$showSecondaryMarketSurcharges()) {
                    seasonTicketSurcharges.push({
                        type,
                        limit: this.form.get(type).value.limit,
                        ranges: cleanRangesBeforeSave(this.form.get(type).value.ranges)
                    });
                }
            }
            this.form.markAsPristine();
            this.form.markAsUntouched();
            return this.#seasonTicketsSrv.seasonTicket.get$()
                .pipe(
                    first(),
                    switchMap(seasonTicket => {
                        obs$.push(this.#seasonTicketsSrv.saveSeasonTicketSurcharges(seasonTicket.id.toString(), seasonTicketSurcharges));
                        if (seasonTicket.settings.operative.allow_change_seat) {
                            obs$.push(this.#seasonTicketsSrv.seasonTicketChangeSeat.update(seasonTicket.id, seasonTicketChangeSeat));
                        }
                        return forkJoin(obs$).pipe(tap(() => this.#ephemeralSrv.showSaveSuccess()));
                    }
                    )
                );
        } else {
            this.form.markAllAsTouched();
            this.#ref.markForCheck();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return throwError(() => 'invalid form');
        }
    }

    ngAfterViewInit(): void {
        combineLatest([this.hasSecondaryMarket$.pipe(first(Boolean)), this.surcharges$.pipe(first(Boolean))])
            .subscribe(([hasSecondaryMarket, _]) => {
                if (hasSecondaryMarket && this.#route.snapshot.queryParamMap.get('from') === 'secondary-market') {
                    setTimeout(() => {
                        (this._secondaryMarketSurcharges()).nativeElement.scrollIntoView({ behavior: 'smooth', block: 'center' });

                    });
                }
            });
    }

    private initializeMap(surcharges: SeasonTicketSurcharge[]): void {
        this.surcharges = new Map();
        surcharges.forEach(surcharge =>
            this.surcharges.set(surcharge.type, [{ from: 0, values: {} }]));
    }

    private configureSurchargeLimit(surchargeType: SeasonTicketSurchargeType): void {
        this.form.addControl(surchargeType, this.#fb.group({
            limit: this.#fb.group({
                enabled: [false],
                min: [{ value: null, disabled: true }],
                max: [{ value: null, disabled: true }]
            })
        }));
        (this.form.get(`${surchargeType}.limit.enabled`).valueChanges as Observable<boolean>)
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(isLimitEnabled => {
                const minCtrl = this.form.get(`${surchargeType}.limit.min`);
                const maxCtrl = this.form.get(`${surchargeType}.limit.max`);
                if (isLimitEnabled) {
                    minCtrl.enable();
                    minCtrl.setValidators([Validators.required, Validators.min(0)]);
                    maxCtrl.enable();
                    maxCtrl.setValidators([Validators.required, Validators.min(0)]);
                } else {
                    minCtrl.disable();
                    minCtrl.setValidators([]);
                    maxCtrl.disable();
                    maxCtrl.setValidators([]);
                }
                minCtrl.updateValueAndValidity();
                maxCtrl.updateValueAndValidity();
            });
    }

    private setSurchargeLimit(surcharge: SeasonTicketSurcharge): void {
        this.form.get(`${surcharge.type}.limit.enabled`).setValue(surcharge.limit?.enabled);
        this.form.get(`${surcharge.type}.limit.min`).setValue(surcharge.limit?.min);
        this.form.get(`${surcharge.type}.limit.max`).setValue(surcharge.limit?.max);
    }
}
