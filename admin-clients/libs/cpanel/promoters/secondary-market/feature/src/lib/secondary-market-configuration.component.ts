import { AdmonitionComponent } from '@admin-clients/admonition';
import { SecondaryMarketConfig, SecondaryMarketPriceType } from '@admin-clients/cpanel/promoters/secondary-market/data-access';
import { HelpButtonComponent, PercentageInputComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { LocalNumberPipe, LocalCurrencyPipe } from '@admin-clients/shared/utility/pipes';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, input, viewChild } from '@angular/core';
import { takeUntilDestroyed, toObservable } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCheckbox } from '@angular/material/checkbox';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, startWith } from 'rxjs';
import { SecondaryMarketConfigPriceComponent } from './price/secondary-market-configuration-price.component';

@Component({
    selector: 'ob-secondary-market-configuration',
    templateUrl: './secondary-market-configuration.component.html',
    styleUrls: ['./secondary-market-configuration.component.css'],
    imports: [
        MaterialModule, TranslatePipe, ReactiveFormsModule, LocalNumberPipe,
        AdmonitionComponent, PercentageInputComponent, MatCheckbox,
        SecondaryMarketConfigPriceComponent, HelpButtonComponent
    ],
    providers: [LocalCurrencyPipe],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SecondaryMarketConfigComponent {
    readonly #fb = inject(FormBuilder);
    readonly #destroy = inject(DestroyRef);
    readonly #router = inject(Router);
    readonly #route = inject(ActivatedRoute);
    readonly priceComponent = viewChild(SecondaryMarketConfigPriceComponent);

    readonly $isSeasonTicket = input(null, { alias: 'isSeasonTicket' });
    readonly form = this.#fb.group({
        enabled: null as boolean,
        price: this.#fb.group({
            type: this.#fb.control(null as SecondaryMarketPriceType, {
                validators: [Validators.required],
                nonNullable: true
            }),
            restrictions: this.#fb.group({
                min: this.#fb.control<number>({ value: null, disabled: true }, {
                    nonNullable: true,
                    validators: [Validators.required, Validators.min(0)]
                }),
                max: this.#fb.control<number>({ value: null, disabled: true }, {
                    nonNullable: true,
                    validators: [Validators.required, Validators.min(0)]
                })
            })
        }),
        commission: this.#fb.group({
            percentage: this.#fb.control(0, {
                validators: [Validators.required, Validators.min(0), Validators.max(100)],
                nonNullable: true
            })
        }),
        sale_type: this.#fb.control({ value: null as 'PARTIAL' | 'FULL', disabled: true }, { validators: [Validators.required] }),
        additional_settings: this.#fb.group({
            hide_base_price: this.#fb.control({ value: null as boolean, disabled: true }),
            pay_to_balance: this.#fb.control({ value: null as boolean, disabled: true })

        }),
        num_sessions: this.#fb.control({ value: null as number, disabled: true }, { validators: [Validators.required, Validators.min(1)] })
    });

    constructor() {
        toObservable(this.$isSeasonTicket)
            .pipe(filter(Boolean), takeUntilDestroyed(this.#destroy)).subscribe(() => this.enableSeasonTicketControls());
        this.#overrideFormMethods();
        // toggle enabler switch if form valid
        this.form.statusChanges.pipe(
            startWith(this.form.status),
            takeUntilDestroyed(this.#destroy)
        ).subscribe(status => {
            if (status === 'VALID') {
                this.form.get('enabled').enable({ emitEvent: false });
            } else {
                this.form.get('enabled').disable({ emitEvent: false });
            }
        });
    }

    enableSeasonTicketControls(): void {
        this.form.controls.sale_type.enable();
        this.form.controls.additional_settings.controls.hide_base_price.enable();
        this.form.controls.additional_settings.controls.pay_to_balance.enable();
        this.form.controls.sale_type.valueChanges.pipe(takeUntilDestroyed(this.#destroy)).subscribe(value => {
            if (value === 'PARTIAL') {
                this.form.controls.price.patchValue({ type: 'PRORATED', restrictions: null });
                this.form.controls.num_sessions.enable();
            } else if (value === 'FULL') {
                if (this.form.controls.price.controls.type.value === 'PRORATED') {
                    this.form.controls.price.reset();
                }
                this.form.controls.additional_settings.controls.hide_base_price.patchValue(false);
                this.form.controls.num_sessions.disable();
            }
        });

    }

    goToSeasonTicketSurcharges(): void {
        this.#router.navigate(['../../prices/surcharges'], { relativeTo: this.#route, queryParams: { from: 'secondary-market' } });
    }

    #overrideFormMethods(): void {
        const originalReset = this.form.reset.bind(this.form);
        this.form.reset = (...args) => {
            originalReset(...args);
            this.#prepareModifierForms(args[0] as SecondaryMarketConfig);
        };
        const originalMarkAllAsTouched = this.form.markAllAsTouched.bind(this.form);
        this.form.markAllAsTouched = (...args) => {
            originalMarkAllAsTouched(...args);
            this.#touchModifierForms();
        };
    }

    #prepareModifierForms(value: SecondaryMarketConfig): void {
        this.priceComponent()?.prepareModifierForms(value);
    }

    #touchModifierForms(): void {
        return this.priceComponent()?.touchModifierForms();
    }

}
