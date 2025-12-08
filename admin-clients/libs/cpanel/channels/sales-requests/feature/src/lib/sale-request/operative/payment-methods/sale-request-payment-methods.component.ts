/* eslint-disable @typescript-eslint/dot-notation */
import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ChannelGateway } from '@admin-clients/cpanel/channels/data-access';
import {
    PutSaleRequestGateways, SaleRequestGateway, SalesRequestsService
} from '@admin-clients/cpanel-channels-sales-requests-data-access';
import { DialogSize, EphemeralMessageService, openDialog } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { booleanOrMerge, FormControlHandler } from '@admin-clients/shared/utility/utils';
import { OptionsTableColumnOption } from '@admin-clients/shared-common-ui-options-table';
import {
    ChangeDetectionStrategy, Component, OnDestroy, inject, ViewContainerRef, computed
} from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import {
    AbstractControl, FormBuilder, FormControl, FormGroup, ValidationErrors, ValidatorFn
} from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Observable, throwError } from 'rxjs';
import { filter, map, tap } from 'rxjs/operators';
import { SaleRequestPaymentMethodsBenefitsComponent } from '../benefits/sale-request-payment-methods-benefits.component';

type ChannelGatewayCtrlElem = FormGroup<{
    gatewayId: FormControl<string>;
    configId: FormControl<string>;
    name: FormControl<string>;
    active: FormControl<boolean>;
    default: FormControl<boolean>;
}>;

@Component({
    selector: 'app-sale-request-payment-methods',
    templateUrl: './sale-request-payment-methods.component.html',
    styleUrls: ['./sale-request-payment-methods.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SaleRequestPaymentMethodsComponent implements OnDestroy, WritingComponent {
    readonly #fb = inject(FormBuilder);
    readonly #salesRequestsService = inject(SalesRequestsService);
    readonly #ephemeralMessage = inject(EphemeralMessageService);
    readonly #dialogSrv = inject(MatDialog);
    readonly #viewContainerRef = inject(ViewContainerRef);
    #salesRequestId: number;

    readonly form = this.#fb.group({
        customGateways: false,
        gateways: this.#fb.array<ChannelGatewayCtrlElem>([], this.#activeAndDefaultRequired())
    });

    readonly gateways$ = this.#salesRequestsService.getSaleRequestPaymentMethods$()
        .pipe(filter(Boolean), map(saleRequestGateways => saleRequestGateways?.channel_gateways));

    readonly reqInProgress$ = booleanOrMerge([
        this.#salesRequestsService.isSaleRequestPaymentMethodsLoading$(),
        this.#salesRequestsService.isSaleRequestPaymentMethodsSaving$()
    ]);

    readonly $hasBenefits = toSignal(this.#salesRequestsService.getSaleRequestPaymentMethods$()
        .pipe(map(gateways => !!gateways?.benefits)), { initialValue: false });

    readonly $columns = computed(() => {
        let columns = [OptionsTableColumnOption.active, OptionsTableColumnOption.default, 'id', 'name'];
        if (this.$hasBenefits()) {
            columns = columns.concat('benefits', 'actions');
        }
        return columns;
    });

    constructor() {
        this.#salesRequestsService.getSaleRequest$()
            .pipe(filter(Boolean), takeUntilDestroyed())
            .subscribe(saleRequest => {
                this.#salesRequestId = saleRequest.id;
                this.#salesRequestsService.loadSaleRequestPaymentMethods(this.#salesRequestId);
            });

        this.#salesRequestsService.getSaleRequestPaymentMethods$()
            .pipe(filter(Boolean), takeUntilDestroyed())
            .subscribe(saleRequestGateways => {
                this.#saleRequestPaymentMethodSetData(saleRequestGateways);
                this.form.markAsPristine();
            });

        this.form.controls.customGateways.valueChanges
            .pipe(takeUntilDestroyed())
            .subscribe(customGateways => {
                if (customGateways) {
                    this.form.controls.gateways.enable();
                } else {
                    this.form.controls.gateways.disable();
                }
            });
    }

    ngOnDestroy(): void {
        this.#salesRequestsService.clearSaleRequestPaymentMethods();
    }

    save(): void {
        this.save$().subscribe(() => this.#salesRequestsService.loadSaleRequestPaymentMethods(this.#salesRequestId));
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            const paymentMethodsSaveData = {
                custom: this.form.controls.customGateways.value
            } as PutSaleRequestGateways;

            if (paymentMethodsSaveData.custom) {
                paymentMethodsSaveData.channel_gateways = this.form.controls.gateways.value.map(elem => ({
                    gateway_sid: elem['gatewayId'],
                    configuration_sid: elem['configId'],
                    active: elem['active'],
                    default: elem['default']
                }));
            }

            return this.#salesRequestsService.saveSaleRequestPaymentMethods(this.#salesRequestId, paymentMethodsSaveData).pipe(
                tap(() => {
                    this.#ephemeralMessage.showSaveSuccess();
                }));
        } else {
            FormControlHandler.markAllControlsAsTouched(this.form);
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            this.form.controls.customGateways.setValue(true);
            return throwError(() => 'invalid form');
        }
    }

    cancel(): void {
        this.#salesRequestsService.loadSaleRequestPaymentMethods(this.#salesRequestId);
    }

    editElementsInfo(channelGateway: ChannelGateway): void {
        openDialog(this.#dialogSrv, SaleRequestPaymentMethodsBenefitsComponent,
            {
                saleRequestId: this.#salesRequestId,
                gatewaySid: channelGateway.gateway_sid,
                configurationSid: channelGateway.configuration_sid,
                gatewayName: channelGateway.name
            }, this.#viewContainerRef, DialogSize.LATERAL);
    }

    #saleRequestPaymentMethodSetData(saleRequestGateway: SaleRequestGateway): void {
        this.form.controls.gateways.clear();
        this.form.controls.customGateways.setValue(saleRequestGateway.custom);
        saleRequestGateway.channel_gateways.forEach(pm => {
            this.form.controls.gateways.push(this.#fb.group({
                gatewayId: pm.gateway_sid,
                configId: pm.configuration_sid,
                name: pm.name,
                active: pm.active,
                default: pm.default
            }));
        });
        if (saleRequestGateway.custom) {
            this.form.controls.gateways.enable();
        } else {
            this.form.controls.gateways.disable();
        }
        this.form.markAsPristine();
    }

    #activeAndDefaultRequired(): ValidatorFn {
        return (gatewaysList: AbstractControl): ValidationErrors | null => {
            const activeAndDefault = !!gatewaysList.value.find((value: { [key: string]: unknown }) =>
                value?.['default'] && value?.['active']
            );
            return !activeAndDefault ? { activeAndDefaultRequired: true } : null;
        };
    }
}
