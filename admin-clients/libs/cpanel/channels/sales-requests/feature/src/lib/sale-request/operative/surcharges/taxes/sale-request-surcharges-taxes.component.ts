import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import {
    PutSaleRequestSurchargeTaxes, SaleRequestSurchargeTaxesOrigin, SalesRequestsService
} from '@admin-clients/cpanel-channels-sales-requests-data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { FormControlHandler } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, effect, inject, input } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { MatOption, MatSelect } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, first, map, Observable } from 'rxjs';

export const SURCHARGE_TAXES_FORM_KEY = 'surchargeTaxes';

@Component({
    selector: 'app-sale-request-surcharges-taxes',
    imports: [
        ReactiveFormsModule, TranslatePipe, FormControlErrorsComponent,
        MatRadioButton, MatRadioGroup, MatFormField, MatSelect, MatOption, MatLabel
    ],
    templateUrl: './sale-request-surcharges-taxes.component.html',
    styleUrls: ['./sale-request-surcharges-taxes.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SaleRequestChannelSurchargesTaxesComponent {
    readonly #fb = inject(FormBuilder);
    readonly #entitiesService = inject(EntitiesBaseService);
    readonly #salesRequestsService = inject(SalesRequestsService);

    readonly #entityId$ = this.#salesRequestsService.getSaleRequest$().pipe(filter(Boolean), map(e => e.channel.entity.id));

    readonly saleRequestSurchargeTaxesOrigin = SaleRequestSurchargeTaxesOrigin;

    readonly $form = input.required<FormGroup>({ alias: 'form' });
    readonly $userCanWrite = input<boolean>(true, { alias: 'userCanWrite' });

    readonly $taxes = toSignal(this.#entitiesService.getEntityTaxes$().pipe(filter(Boolean)));

    readonly originCtrl = this.#fb.nonNullable.control(SaleRequestSurchargeTaxesOrigin.event, { validators: Validators.required });
    readonly taxesCtrl = this.#fb.control<number[] | null>({ value: null, disabled: true }, [Validators.required, Validators.minLength(1)]);

    readonly internalForm = this.#fb.group({
        origin: this.originCtrl,
        taxes: this.taxesCtrl
    });

    constructor() {
        this.#initListeners();
        this.#initEffects();
    }

    getRequest(channelId: number): Observable<unknown> | null {
        if (this.internalForm.pristine || this.internalForm.invalid) return null;

        const value = this.internalForm.value;
        const payload: PutSaleRequestSurchargeTaxes = {
            origin: value.origin,
            taxes: value.taxes?.map(id => ({ id }))
        };

        return this.#salesRequestsService.surchargeTaxes.update(channelId, payload);
    }

    #initListeners(): void {
        FormControlHandler.getValueChanges(this.originCtrl).subscribe(origin => this.#checkTaxesState(origin));

        this.#salesRequestsService.surchargeTaxes.get$().pipe(
            filter(Boolean),
            takeUntilDestroyed()
        ).subscribe(surchargeTaxes => {
            this.internalForm.reset({
                origin: surchargeTaxes.origin,
                taxes: surchargeTaxes.taxes?.length ? surchargeTaxes.taxes.map(t => t.id) : null
            });
        });
    }

    #initEffects(): void {
        effect(() => this.$form().addControl(SURCHARGE_TAXES_FORM_KEY, this.internalForm));
        effect(() => {
            if (this.$userCanWrite()) {
                this.internalForm.enable();
            } else {
                this.internalForm.disable();
                this.#loadTaxesOptions();
            }
        });
    }

    #checkTaxesState(origin: SaleRequestSurchargeTaxesOrigin): void {
        if (origin === SaleRequestSurchargeTaxesOrigin.saleRequest) {
            this.#loadTaxesOptions();
            this.taxesCtrl.enable();
        } else {
            this.taxesCtrl.disable();
        }
    }

    #loadTaxesOptions(): void {
        if (this.$taxes()?.length) return;
        this.#entityId$.pipe(first(Boolean)).subscribe(id => this.#entitiesService.loadEntityTaxes(id));
    }
}
