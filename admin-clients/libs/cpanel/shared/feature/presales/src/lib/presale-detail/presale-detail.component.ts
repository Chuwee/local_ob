import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { Presale, PresalePut, PRESALES_SERVICE, ValidationRangeTypes, ValidatorTypes } from '@admin-clients/cpanel/shared/data-access';
import { EntitiesBaseService, ExternalInventoryProviders } from '@admin-clients/shared/common/data-access';
import {
    DateTimeModule, EphemeralMessageService, SearchablePaginatedSelectionLoadEvent, SearchablePaginatedSelectionModule
} from '@admin-clients/shared/common/ui/components';
import { IdName } from '@admin-clients/shared/data-access/models';
import { PrefixPipe } from '@admin-clients/shared/utility/pipes';
import { dateTimeValidator, dateIsBefore, dateIsAfter, maxDecimalLength } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import {
    AfterViewInit, ChangeDetectionStrategy, Component, DestroyRef, OnInit, QueryList, ViewChildren, inject,
    input, effect
} from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDividerModule } from '@angular/material/divider';
import { MatExpansionPanel } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatRadioModule } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import {
    BehaviorSubject, Observable, catchError, combineLatest, filter, map, shareReplay, throwError, first
} from 'rxjs';

const PAGE_SIZE = 5;

@Component({
    selector: 'app-presale-detail',
    templateUrl: './presale-detail.component.html',
    styleUrls: ['./presale-detail.component.scss'],
    imports: [
        AsyncPipe, TranslatePipe, ReactiveFormsModule, SearchablePaginatedSelectionModule, DateTimeModule, PrefixPipe,
        MatRadioModule, MatSlideToggleModule, MatDividerModule, MatIconModule, MatProgressSpinnerModule, MatTooltipModule, MatButtonModule,
        MatFormFieldModule, MatSelectModule, MatCheckboxModule, MatInputModule, RouterLink
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PresaleDetailComponent implements OnInit, AfterViewInit {

    @ViewChildren(MatExpansionPanel) private _matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    readonly $isAvetEvent = input<boolean>(false, { alias: 'isAvetEvent' });
    readonly $isSmartBooking = input<boolean>(false, { alias: 'isSmartBooking' });
    readonly $externalInventoryProvider = input<ExternalInventoryProviders>(null, { alias: 'externalInventoryProvider' });
    readonly $presale = input<Presale>(null, { alias: 'presale' });

    readonly #entitiesSrv = inject(EntitiesBaseService);
    readonly #fb = inject(FormBuilder);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #destroyRef = inject(DestroyRef);
    readonly #presaleSrv = inject(PRESALES_SERVICE);
    readonly #defaultChannels = new BehaviorSubject<{ id: number; name: string }[]>(null);
    readonly #filter = new BehaviorSubject({
        offset: 0,
        q: null as string,
        selectedOnly: false
    });

    readonly $selectedOnly = toSignal(this.#filter.asObservable().pipe(map(filter => filter.selectedOnly)));
    readonly $customerTypes = toSignal(this.#entitiesSrv.entityCustomerTypes.get$());
    readonly channels$ = combineLatest([
        this.#defaultChannels.asObservable().pipe(filter(Boolean)),
        this.#filter.asObservable()
    ]).pipe(
        map(([channels, filter]) => {
            if (filter.selectedOnly) {
                channels = channels.filter(c =>
                    !!this.channelsControl.value.find(channel => channel.id === c.id));
            }
            if (filter.q?.length) {
                channels = channels.filter(channel =>
                    channel.name.toLowerCase().includes(filter.q.toLowerCase()));
            }
            return {
                data: channels.slice(filter.offset, filter.offset + PAGE_SIZE),
                metadata: { total: channels.length, offset: filter.offset, limit: PAGE_SIZE }
            };
        }),
        shareReplay({ bufferSize: 1, refCount: true })
    );

    readonly form = this.#fb.group({
        active: null as boolean,
        presales: this.#fb.group({
            customer_types: [[] as number[]],
            dates: [null as ValidationRangeTypes, Validators.required],
            start_date: [{ value: null as string, disabled: true }, [Validators.required]],
            end_date: [{ value: null as string, disabled: true }, [Validators.required]],
            channels: [null as IdName[], Validators.required],
            loyalty_points_enabled: null as boolean,
            loyalty_points_amount: [{ value: null as number, disabled: true },
            [Validators.required, Validators.min(1), maxDecimalLength(0)]],
            member_tickets_limit_enabled: null as boolean,
            member_tickets_limit: [{ value: null as number, disabled: true }, [Validators.required, Validators.pattern(/^[0-9]*$/)]],
            multiple_purchase: false as boolean
        })
    });

    readonly pageSize = PAGE_SIZE;
    readonly loading$ = this.#presaleSrv.isLoading$();

    readonly totalChannels$ = this.channels$.pipe(map(ce => ce.metadata?.total));
    readonly channelsList$ = this.channels$.pipe(map(ce => ce.data));
    readonly channelsListMetadata$ = this.channels$.pipe(map(ce => ce.metadata));
    readonly channelsControl = this.form.controls.presales.controls.channels;

    readonly $showLoyaltyPointsSettings = toSignal(this.#entitiesSrv.getEntity$().pipe(
        first(Boolean),
        map(entity => entity.settings?.allow_loyalty_points)
    ));

    validationTypes = ValidationRangeTypes;
    validatorTypes = ValidatorTypes;
    presaleDetail!: Presale;

    constructor() {
        effect(() => {
            const value = this.$presale();
            if (value) {
                this.presaleDetail = value;
                this.form.controls.active.patchValue(value.active);

                this.#defaultChannels.next(value.channels);

                this.form.controls.presales.patchValue({
                    dates: value.presale_period.type,
                    start_date: value.presale_period.start_date,
                    end_date: value.presale_period.end_date,
                    channels: value.channels.filter(channel => channel.selected),
                    customer_types: value.customer_types?.filter(customerType => customerType.selected)
                        .map(customerType => customerType.id) || [],
                    loyalty_points_enabled: value.loyalty_program?.enabled,
                    loyalty_points_amount: value.loyalty_program?.points,
                    member_tickets_limit_enabled: value.member_tickets_limit_enabled,
                    member_tickets_limit: value.member_tickets_limit,
                    multiple_purchase: value.settings?.multiple_purchase
                });

                if (this.$externalInventoryProvider() === ExternalInventoryProviders.sga) {
                    this.form.controls.presales.controls.customer_types.disable();
                    this.form.controls.presales.controls.dates.disable();
                    this.form.controls.presales.controls.start_date.disable();
                    this.form.controls.presales.controls.end_date.disable();
                }
                if (!this.form.valid) {
                    this.form.controls.active.disable();
                }

                this.form.markAsPristine();
                this.form.markAsUntouched();
            }
        });
    }

    ngOnInit(): void {
        this.#initForms();
    }

    ngAfterViewInit(): void {
        const presale = this.$presale();
        if (presale) {
            this.channelsControl.setValue(presale.channels.filter(channel => channel.selected));
            this.form.markAsPristine();
        }
    }

    cancel(): void {
        this.#presaleSrv.load();
    }

    save(): void {
        this.save$().subscribe(() => {
            this.#ephemeralSrv.showSaveSuccess();
            this.form.markAsPristine();
            this.#presaleSrv.load();
        });
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            const data = this.form.getRawValue();
            const presale = this.$presale();
            const presales: PresalePut = {
                name: presale.name,
                active: data.active,
                presale_period: {
                    type: data.presales.dates,
                    start_date: data.presales?.start_date,
                    end_date: data.presales?.end_date
                },
                channels: data.presales?.channels?.map(channel => channel.id),
                ...(presale.validator_type === this.validatorTypes.customers && {
                    customer_types: data.presales?.customer_types,
                    ...(this.$showLoyaltyPointsSettings() && {
                        loyalty_program: {
                            enabled: data.presales.loyalty_points_enabled,
                            points: data.presales.loyalty_points_amount
                        }
                    }),
                    ...(!this.$isAvetEvent() && {
                        member_tickets_limit: data.presales?.member_tickets_limit,
                        member_tickets_limit_enabled: data.presales?.member_tickets_limit_enabled
                    })
                }),
                settings: {
                    multiple_purchase: data.presales?.multiple_purchase
                }
            };
            return this.#presaleSrv.update(presale.id, presales);
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanelQueryList);
            return throwError(() => 'invalid form');
        }
    }

    activate(activated: boolean): void {
        const presale: PresalePut = { active: activated };
        const currentPresale = this.$presale();
        this.presaleDetail.active = activated;
        this.form.controls.active.markAsPristine();
        this.#presaleSrv.update(currentPresale.id, presale)
            .pipe(catchError(error => {
                this.form.controls.active.reset(this.presaleDetail.active);
                throw error;
            })).subscribe(() => {
                this.#ephemeralSrv.showSuccess({
                    msgKey: this.form.controls.active.getRawValue() ?
                        'EVENTS.SESSION.PRESALES.ACTIVATE_PRESALE_SUCCESS' : 'EVENTS.SESSION.PRESALES.DEACTIVATE_PRESALE_SUCCESS'
                });
                this.#presaleSrv.load();
            });
    }

    changeSelectedOnly(): void {
        this.#filter.next({
            ...this.#filter.value,
            selectedOnly: !this.#filter.value.selectedOnly,
            offset: 0
        });
    }

    loadPagedChannels({ offset, q }: SearchablePaginatedSelectionLoadEvent): void {
        this.#filter.next({
            ...this.#filter.value,
            q,
            offset
        });
    }

    #dateRangeValidator(value: ValidationRangeTypes): void {
        if (value !== ValidationRangeTypes.dateRange) {
            this.form.controls.presales.controls.start_date.disable();
            this.form.controls.presales.controls.end_date.disable();
        } else {
            this.form.controls.presales.controls.start_date.enable();
            this.form.controls.presales.controls.end_date.enable();
        }
    }

    #memberTicketsLimitValidator(value: boolean): void {
        const memberTicketsLimitControl = this.form.controls.presales.controls.member_tickets_limit;

        if (value) {
            memberTicketsLimitControl.reset();
            memberTicketsLimitControl.enable();
            const presale = this.$presale();
            memberTicketsLimitControl.setValue(presale?.member_tickets_limit ?? 1);
        } else {
            memberTicketsLimitControl.disable();
            memberTicketsLimitControl.setValue(null);
        }
    }

    #loyaltyPointsValidator(value: boolean): void {
        const loyaltyPointsAmountControl = this.form.controls.presales.controls.loyalty_points_amount;

        if (value) {
            loyaltyPointsAmountControl.reset();
            loyaltyPointsAmountControl.enable();
            const presale = this.$presale();
            loyaltyPointsAmountControl.setValue(presale?.loyalty_program?.points);
        } else {
            loyaltyPointsAmountControl.disable();
            loyaltyPointsAmountControl.setValue(null);
        }
    }

    #initForms(): void {
        const controls = this.form.controls.presales.controls;
        controls.start_date.addValidators(
            dateTimeValidator(dateIsBefore, 'presaleStartAfterPresaleEnd', controls.end_date)
        );
        controls.end_date.addValidators(
            dateTimeValidator(dateIsAfter, 'presaleStartAfterPresaleEnd', controls.start_date)
        );

        this.#memberTicketsLimitValidator(controls.member_tickets_limit_enabled.value);

        controls.member_tickets_limit_enabled.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(value => this.#memberTicketsLimitValidator(value));

        this.#loyaltyPointsValidator(controls.loyalty_points_enabled.value);

        controls.loyalty_points_enabled.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(value => this.#loyaltyPointsValidator(value));

        this.#dateRangeValidator(controls.dates.value);

        controls.dates.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(value => this.#dateRangeValidator(value));

    }
}
