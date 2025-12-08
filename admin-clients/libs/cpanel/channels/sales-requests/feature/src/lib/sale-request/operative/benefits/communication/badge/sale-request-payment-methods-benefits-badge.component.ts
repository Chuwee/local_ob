import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { SaleRequestGatewayBenefitBinGroup, SalesRequestsService } from '@admin-clients/cpanel-channels-sales-requests-data-access';
import { ColorPickerComponent, TabDirective, TabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { ChangeDetectionStrategy, Component, effect, inject, input, model, OnDestroy, OnInit, viewChild } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, FormGroupDirective, ReactiveFormsModule, StatusChangeEvent, TouchedChangeEvent, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, map, merge } from 'rxjs';
import { VmSaleRequestGatewayBenefit } from '../../vm-sale-request-gateway-benefit.model';

type Badge = SaleRequestGatewayBenefitBinGroup['checkout_communication_elements']['badge'];

@Component({
    selector: 'app-sale-request-payment-methods-benefits-badge',
    templateUrl: './sale-request-payment-methods-benefits-badge.component.html',
    styleUrls: ['./sale-request-payment-methods-benefits-badge.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        TabsMenuComponent, TabDirective, ColorPickerComponent,
        MaterialModule, ReactiveFormsModule, TranslatePipe, FormControlErrorsComponent
    ]
})
export class SaleRequestPaymentMethodsBenefitsBadgeComponent implements OnInit, OnDestroy {
    readonly #fb = inject(FormBuilder);
    readonly #formGroup = inject(FormGroupDirective, { optional: true });
    readonly #salesRequestsSrv = inject(SalesRequestsService);

    readonly $textTabs = viewChild<TabsMenuComponent>('languageTabs');

    readonly $vmSaleRequestGatewayBenefit = input.required<VmSaleRequestGatewayBenefit>({ alias: 'vmSaleRequestGatewayBenefit' });
    readonly $postBadge = model.required<Badge>({ alias: 'postBadge' });

    readonly badgeCtrl = this.#fb.nonNullable.record<string>({});
    readonly backgroundColor = this.#fb.nonNullable.control('#E8E8E8', [Validators.required]);
    readonly textColor = this.#fb.nonNullable.control('#7A7A7A', [Validators.required]);

    readonly languages$ = toSignal(
        this.#salesRequestsSrv.getSaleRequest$().pipe(
            filter(Boolean),
            map(saleRequest => saleRequest.languages.selected ?? [])
        ),
        { initialValue: [] as string[] }
    );

    readonly defaultLanguageIndex$ = toSignal(this.#salesRequestsSrv.getSaleRequest$().pipe(
        map(saleRequest => saleRequest.languages.selected.findIndex(lang => lang === saleRequest.languages.default))
    ));

    constructor() {
        effect(() => {
            if (this.$vmSaleRequestGatewayBenefit()?.beingModified) {
                this.badgeCtrl.enable({ emitEvent: false });
                this.backgroundColor.enable({ emitEvent: false });
                this.textColor.enable({ emitEvent: false });
            } else {
                const [binGroup] = this.$vmSaleRequestGatewayBenefit()?.bin_groups;
                const badge = binGroup?.checkout_communication_elements?.badge;
                this.badgeCtrl.reset(badge?.text, { emitEvent: false });
                this.backgroundColor.reset(badge?.background_color, { emitEvent: false });
                this.textColor.reset(badge?.text_color, { emitEvent: false });

                this.badgeCtrl.disable({ emitEvent: false });
                this.backgroundColor.disable({ emitEvent: false });
                this.textColor.disable({ emitEvent: false });
            }
        });
        ;

        merge(
            this.badgeCtrl.valueChanges,
            this.backgroundColor.valueChanges,
            this.textColor.valueChanges
        )
            .pipe(takeUntilDestroyed())
            .subscribe(() => {
                this.$postBadge.set({
                    text: this.badgeCtrl.getRawValue() as Record<string, string>,
                    background_color: this.backgroundColor.value,
                    text_color: this.textColor.value
                });
            });

        this.badgeCtrl.events
            .pipe(
                filter(e => e instanceof StatusChangeEvent || e instanceof TouchedChangeEvent),
                takeUntilDestroyed()
            )
            .subscribe(event => {
                if (event.source.status !== 'INVALID' || !event.source.touched) return;
                this.$textTabs().goToInvalidCtrlTab();
            });
    }

    ngOnInit(): void {
        this.languages$().forEach(language => {
            const ctrl = this.#fb.nonNullable.control('', [Validators.required, Validators.maxLength(25)]);
            this.badgeCtrl.addControl(language, ctrl, { emitEvent: false });
        });
        this.#formGroup?.control?.addControl('text', this.badgeCtrl, { emitEvent: false });
        this.#formGroup?.control?.addControl('text_color', this.textColor, { emitEvent: false });
        this.#formGroup?.control?.addControl('background_color', this.backgroundColor, { emitEvent: false });

        const badge = this.$postBadge();
        if (badge) {
            this.badgeCtrl.patchValue(badge.text ?? {}, { emitEvent: false });
            this.backgroundColor.setValue(badge.background_color ?? this.backgroundColor.value, { emitEvent: false });
            this.textColor.setValue(badge.text_color ?? this.textColor.value, { emitEvent: false });
        }

    }

    ngOnDestroy(): void {
        this.#formGroup?.control?.removeControl('text_color', { emitEvent: false });
        this.#formGroup?.control?.removeControl('background_color', { emitEvent: false });
        this.#formGroup?.control?.removeControl('text', { emitEvent: false });

    }
}
