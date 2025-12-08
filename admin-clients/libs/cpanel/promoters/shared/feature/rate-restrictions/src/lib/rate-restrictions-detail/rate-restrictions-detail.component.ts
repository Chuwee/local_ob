import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import {
    Rate, RATE_RESTRICTIONS_CHANNELS_SERVICE, RATE_RESTRICTIONS_SERVICE, RateRestrictions, RateRestrictionsChannel
} from '@admin-clients/cpanel/promoters/shared/data-access';
import { EntityCustomerType } from '@admin-clients/shared/common/data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { PrefixPipe } from '@admin-clients/shared/utility/pipes';
import { dateIsAfter, dateIsBefore, dateTimeValidator, maxDecimalLength } from '@admin-clients/shared/utility/utils';
import { VenueTemplatePriceTypeGrouped } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, effect, ElementRef, inject, input, booleanAttribute } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MAT_DATE_FORMATS, MatDateFormats } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatRadioModule } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';
import { RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import moment from 'moment';
import { combineLatest, Observable, throwError } from 'rxjs';

const rateRelationsRestrictionsType = ['TICKETS_NUMBER', 'MAX_TICKETS_NUMBER'] as const;
type RateRelationsRestrictionsType = typeof rateRelationsRestrictionsType[number];

const ageRestrictionMode = ['TO_CUSTOM', 'TO_ACTUAL'] as const;
type AgeRestrictionMode = typeof ageRestrictionMode[number];

@Component({
    selector: 'app-rate-restrictions-detail',
    templateUrl: './rate-restrictions-detail.component.html',
    styleUrls: ['./rate-restrictions-detail.component.scss'],
    imports: [TranslatePipe, ReactiveFormsModule, MatFormFieldModule, MatCheckboxModule, MatRadioModule, MatInputModule,
        MatSelectModule, FormControlErrorsComponent, MatListModule, MatIconModule, MatDatepickerModule, AsyncPipe,
        MatButton, MatIconButton, PrefixPipe, MatProgressSpinnerModule, RouterLink
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class RateRestrictionsDetailComponent {
    readonly #fb = inject(FormBuilder);
    readonly #formats: MatDateFormats = inject(MAT_DATE_FORMATS);
    readonly #elementRef = inject(ElementRef);
    readonly #ephemeralService = inject(EphemeralMessageService);
    readonly #rateRestrictionsSrv = inject(RATE_RESTRICTIONS_SERVICE);
    readonly #rateRestrictionsChannelsSrv = inject(RATE_RESTRICTIONS_CHANNELS_SERVICE);

    readonly $contextId = input.required<number>({ alias: 'contextId' });
    readonly $rateRestrictions = input.required<RateRestrictions>({ alias: 'rateRestrictions' });
    readonly $channelsList = input<RateRestrictionsChannel[]>([], { alias: 'channelsList' });
    readonly $rates = input.required<Rate[]>({ alias: 'rates' });
    readonly $groupedVenueTemplatePriceTypes = input.required<VenueTemplatePriceTypeGrouped[]>({ alias: 'groupedVenueTemplatePriceTypes' });
    readonly $customerTypes = input.required<EntityCustomerType[]>({ alias: 'customerTypes' });
    readonly $showPeriodRestrictions = input(false, { alias: 'showPeriodRestrictions', transform: booleanAttribute });

    readonly $isLoading = toSignal(this.#rateRestrictionsSrv.ratesRestrictions.inProgress$());

    readonly $filteredRates = computed(() => this.$rates()?.filter(rate => rate.id !== this.$rateRestrictions()?.rate.id));
    readonly dateFormat = moment.localeData().longDateFormat(this.#formats.display.dateInput).toLowerCase();
    readonly rateRelationsRestrictionsType = rateRelationsRestrictionsType;
    readonly ageRestrictionMode = ageRestrictionMode;
    readonly channelsPath = this.#rateRestrictionsChannelsSrv.channelsPath;

    readonly form = this.#fb.group({
        rate_relations_restriction_enabled: [false],
        date_restriction_enabled: [false],
        customer_type_restriction_enabled: [false],
        rate_relations_restriction: this.#fb.group({
            type: [rateRelationsRestrictionsType[0] as RateRelationsRestrictionsType],
            locked_tickets_number: [0, [Validators.required, Validators.min(1), maxDecimalLength(0)]],
            required_tickets_number: [0, [Validators.required, Validators.min(1), maxDecimalLength(0)]],
            required_rate_ids: [[] as number[], [Validators.required]],
            restricted_price_zone_ids: [[] as number[], [Validators.required]],
            use_all_zone_prices: [true],
            price_zone_criteria_any: [false],
            apply_to_b2b: [false]
        }),
        price_type_restriction_enabled: [false],
        price_type_restriction: this.#fb.group({
            restricted_price_type_ids: [[] as number[], [Validators.required]],
            apply_to_b2b: [false]
        }),
        channel_restriction_enabled: [false],
        channel_restriction: [[] as number[], [Validators.required]],
        date_restriction: this.#fb.group({
            mode: [ageRestrictionMode[0] as AgeRestrictionMode, [Validators.required]],
            from: [null as Date, [dateTimeValidator(dateIsBefore, 'dateIsFuture', new Date())]],
            to: [null as Date, [Validators.required, dateTimeValidator(dateIsBefore, 'dateIsFuture', new Date())]]
        }),
        customer_type_restriction: [[] as number[], [Validators.required]],
        period_restriction_enabled: [false],
        max_item_restriction_enabled: [false],
        max_item_restriction: [0, [Validators.required, Validators.min(1), maxDecimalLength(0)]]
    });

    constructor() {
        this.form.controls.date_restriction.controls.to
            .addValidators(dateTimeValidator(dateIsAfter, 'dateIsFuture', this.form.get('date_restriction.from')));
        this.#initFormHandlers();
        effect(() => {
            if (!this.$rateRestrictions()) return;
            this.#patchFormValue();
        });
    }

    save(): void {
        this.save$().subscribe(() => {
            this.#ephemeralService.showSaveSuccess();
            this.form.markAsPristine();
            this.#rateRestrictionsSrv.ratesRestrictions.load(this.$contextId());
        });
    }

    save$(): Observable<void> {
        if (!this.form.valid) {
            this.#markFormAsTouched();
            return throwError(() => 'invalid form');
        }

        const body = { ...this.form.value } as any;

        // Save all boolean control values, including disabled ones
        Object.entries(this.form.controls)
            .filter(([_, control]) => typeof control.value === 'boolean')
            .forEach(([key, control]) => {
                body[key] = control.value;
            });

        if (body.rate_relations_restriction) {
            delete body.rate_relations_restriction.type;
            body.rate_relations_restriction.price_zone_criteria = body.rate_relations_restriction.price_zone_criteria_any ? 'ANY' : 'EQUAL';
            delete body.rate_relations_restriction.price_zone_criteria_any;
        }

        if (body.date_restriction) {
            body.date_restriction.from = body.date_restriction?.from?.toISOString() || null;
            body.date_restriction.to = body.date_restriction?.to?.toISOString() || null;
        }

        if (body.period_restriction_enabled) {
            body.period_restriction = ['RENEWAL'];
        }
        return this.#rateRestrictionsSrv.ratesRestrictions.update(this.$contextId(), this.$rateRestrictions().rate.id, body);
    }

    cancel(): void {
        this.#rateRestrictionsSrv.ratesRestrictions.load(this.$contextId());
    }

    #patchFormValue(): void {
        this.form.reset({
            ...this.$rateRestrictions().restrictions,
            channel_restriction_enabled: this.$rateRestrictions().restrictions.channel_restriction_enabled ?? false,
            price_type_restriction: {
                apply_to_b2b: this.$rateRestrictions().restrictions.price_type_restriction?.apply_to_b2b ?? false,
                restricted_price_type_ids: this.$rateRestrictions().restrictions.price_type_restriction?.restricted_price_type_ids || []
            },
            rate_relations_restriction: {
                ...this.$rateRestrictions().restrictions.rate_relations_restriction,
                price_zone_criteria_any: this.$rateRestrictions().restrictions.rate_relations_restriction?.price_zone_criteria === 'ANY',
                use_all_zone_prices: this.$rateRestrictions().restrictions.rate_relations_restriction?.use_all_zone_prices ?? true,
                apply_to_b2b: this.$rateRestrictions().restrictions.rate_relations_restriction?.apply_to_b2b ?? false,
                type: this.$rateRestrictions().restrictions.rate_relations_restriction?.locked_tickets_number
                    ? 'MAX_TICKETS_NUMBER'
                    : 'TICKETS_NUMBER'
            },
            date_restriction: {
                mode: this.$rateRestrictions().restrictions.date_restriction?.to ? 'TO_CUSTOM' : 'TO_ACTUAL',
                from: new Date(this.$rateRestrictions().restrictions.date_restriction?.from),
                to: new Date(this.$rateRestrictions().restrictions.date_restriction?.to)
            },
            max_item_restriction_enabled: this.$rateRestrictions().restrictions.max_item_restriction_enabled ?? false,
            max_item_restriction: this.$rateRestrictions().restrictions.max_item_restriction ?? 0
        });

        if (this.form.value.period_restriction_enabled) {
            this.form.disable({ onlySelf: true, emitEvent: false });
            this.form.get('period_restriction_enabled').enable({ emitEvent: false });
        }

        this.form.controls.rate_relations_restriction_enabled.updateValueAndValidity();
        this.form.markAsUntouched();
        this.form.markAsPristine();
    }

    #handleFormChanges(
        controlToWatch: string,
        controlsToToggle: string[],
        enableLogic: (value) => boolean = value => value
    ): void {
        this.form.get(controlToWatch).valueChanges
            .pipe(takeUntilDestroyed())
            .subscribe(value => {
                if (enableLogic(value)) {
                    controlsToToggle.forEach(control => this.form.get(control).enable());
                } else {
                    controlsToToggle.forEach(control => this.form.get(control).disable());
                }
            });
    }

    #initFormHandlers(): void {
        this.#handleFormChanges('date_restriction_enabled', ['date_restriction', 'date_restriction.mode']);
        this.#handleFormChanges('customer_type_restriction_enabled', ['customer_type_restriction']);
        this.#handleFormChanges('rate_relations_restriction.use_all_zone_prices', ['rate_relations_restriction.restricted_price_zone_ids'], value => !value);
        this.#handleFormChanges('rate_relations_restriction_enabled', ['rate_relations_restriction']);
        this.#handleFormChanges('price_type_restriction_enabled', ['price_type_restriction']);
        this.#handleFormChanges('channel_restriction_enabled', ['channel_restriction']);
        this.#handleFormChanges('date_restriction.mode', ['date_restriction.to'], value => value === 'TO_CUSTOM');
        this.#handleFormChanges('max_item_restriction_enabled', ['max_item_restriction']);
        this.#initBooleanControlsHandlers();

        combineLatest([
            this.form.get('rate_relations_restriction.type').valueChanges,
            this.form.get('rate_relations_restriction_enabled').valueChanges
        ]).pipe(takeUntilDestroyed())
            .subscribe(([type, isEnabled]) => {
                if (isEnabled) {
                    if (type === rateRelationsRestrictionsType[0]) {
                        this.form.get('rate_relations_restriction.required_tickets_number').enable();
                        this.form.get('rate_relations_restriction.locked_tickets_number').disable();
                    } else {
                        this.form.get('rate_relations_restriction.required_tickets_number').disable();
                        this.form.get('rate_relations_restriction.locked_tickets_number').enable();
                    }
                } else {
                    this.form.get('rate_relations_restriction.restricted_price_zone_ids').disable();
                }
            });
    }

    #initBooleanControlsHandlers(): void {
        const booleanControls = Object.entries(this.form.controls)
            .filter(([key, control]) => key !== 'period_restriction_enabled' && typeof control.value === 'boolean')
            .map(([key]) => this.form.get(key));
        const periodControl = this.form.get('period_restriction_enabled');

        combineLatest([
            periodControl.valueChanges,
            ...booleanControls.map(control => control.valueChanges)
        ]).pipe(takeUntilDestroyed())
            .subscribe(([periodValue, ...otherValues]) => {
                const anyOtherChecked = otherValues.some(value => value === true);

                if (periodValue) {
                    booleanControls.forEach(control => {
                        control.setValue(false, { emitEvent: false });
                        control.disable({ emitEvent: false });
                    });
                    periodControl.enable({ emitEvent: false });
                } else if (anyOtherChecked) {
                    periodControl.disable({ emitEvent: false });
                    periodControl.setValue(false, { emitEvent: false });
                } else {
                    periodControl.enable({ emitEvent: false });
                    booleanControls.forEach(control => control.enable({ emitEvent: false }));
                }
            });
    }

    #markFormAsTouched(): void {
        this.form.markAllAsTouched();
        this.form.updateValueAndValidity();
        this.form.get('rate_relations_restriction.restricted_price_zone_ids').markAsTouched();
        this.form.get('rate_relations_restriction.restricted_price_zone_ids').updateValueAndValidity();
        this.form.get('price_type_restriction').markAsTouched();
        this.form.get('price_type_restriction').updateValueAndValidity();
        scrollIntoFirstInvalidFieldOrErrorMsg(this.#elementRef.nativeElement);
    }
}
