import {
    SaleRequestGatewayBenefitBinGroup, SalesRequestsService
} from '@admin-clients/cpanel-channels-sales-requests-data-access';
import { RichTextAreaComponent, TabDirective, TabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { ObFormFieldLabelDirective } from '@admin-clients/shared/common/ui/ob-material';
import {
    ChangeDetectionStrategy, Component, effect, inject, input, model, OnDestroy, OnInit, viewChild
} from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import {
    FormBuilder, FormGroupDirective, ReactiveFormsModule, StatusChangeEvent, TouchedChangeEvent, Validators
} from '@angular/forms';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, map } from 'rxjs/operators';
import { VmSaleRequestGatewayBenefit } from '../../vm-sale-request-gateway-benefit.model';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        TabsMenuComponent, TabDirective, TranslatePipe, MatFormField, MatLabel, ObFormFieldLabelDirective, RichTextAreaComponent,
        ReactiveFormsModule
    ],
    selector: 'app-sale-request-payment-methods-benefits-description',
    templateUrl: './sale-request-payment-methods-benefits-description.component.html',
    styleUrl: './sale-request-payment-methods-benefits-description.component.scss',
    host: {
        // eslint-disable-next-line @typescript-eslint/naming-convention
        '[class]': '"flex flex-col"'
    }
})
export class SaleRequestPaymentMethodsBenefitsDescriptionComponent implements OnInit, OnDestroy {
    private _$textTabs = viewChild(TabsMenuComponent);

    readonly #formGroup = inject(FormGroupDirective);
    readonly #salesRequestsSrv = inject(SalesRequestsService);
    readonly #fb = inject(FormBuilder);

    readonly $vmSaleRequestGatewayBenefit = input.required<VmSaleRequestGatewayBenefit>({ alias: 'vmSaleRequestGatewayBenefit' });
    readonly $postDescription = model
        .required <SaleRequestGatewayBenefitBinGroup['checkout_communication_elements']['description'] >({ alias: 'postDescription' });

    readonly descriptionsCtrl = this.#fb.nonNullable.record<string>({});

    readonly languages$ = toSignal(this.#salesRequestsSrv.getSaleRequest$()
        .pipe(map(saleRequest => saleRequest.languages.selected)));

    readonly defaultLanguageIndex$ = toSignal(this.#salesRequestsSrv.getSaleRequest$()
        .pipe(
            map(saleRequest =>
                saleRequest.languages.selected.findIndex(language => language === saleRequest.languages.default)
            )
        ));

    constructor() {
        effect(() => {
            const vmSaleRequestGatewayBenefit = this.$vmSaleRequestGatewayBenefit();

            if (vmSaleRequestGatewayBenefit.beingModified) {
                this.descriptionsCtrl.enable({ emitEvent: false });
            } else {
                const [binGroup] = vmSaleRequestGatewayBenefit.bin_groups;

                this.descriptionsCtrl.reset(binGroup.checkout_communication_elements.description, { emitEvent: false });
                this.descriptionsCtrl.disable({ emitEvent: false });
            }
        });

        this.descriptionsCtrl.valueChanges
            .pipe(takeUntilDestroyed())
            .subscribe(values => {
                this.$postDescription.set(values);
            });

        this.descriptionsCtrl.events
            .pipe(
                filter(event => event instanceof StatusChangeEvent || event instanceof TouchedChangeEvent),
                takeUntilDestroyed()
            )
            .subscribe(event => {
                if (event.source.status !== 'INVALID' || !event.source.touched) return;
                this._$textTabs().goToInvalidCtrlTab();
            });
    }

    ngOnInit(): void {
        this.languages$().forEach(language => {
            const ctrl = this.#fb.nonNullable.control('', Validators.required);
            this.descriptionsCtrl.addControl(language, ctrl, { emitEvent: false });
        });
        this.#formGroup.control.addControl('descriptionsCtrl', this.descriptionsCtrl, { emitEvent: false });
    }

    ngOnDestroy(): void {
        this.#formGroup.control.removeControl('descriptionsCtrl', { emitEvent: false });
    }
}
