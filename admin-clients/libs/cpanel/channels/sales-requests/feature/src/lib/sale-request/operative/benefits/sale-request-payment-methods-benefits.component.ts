import {
    SaleRequestGatewayBenefit, SalesRequestsService
} from '@admin-clients/cpanel-channels-sales-requests-data-access';
import {
    DialogSize, EmptyStateTinyComponent, EphemeralMessageService, MessageDialogService, ObDialog, openDialog
} from '@admin-clients/shared/common/ui/components';
import {
    ChangeDetectionStrategy, Component, computed, inject, OnDestroy, signal, ViewContainerRef
} from '@angular/core';
import { takeUntilDestroyed, toObservable, toSignal } from '@angular/core/rxjs-interop';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatDialog, MatDialogContent, MatDialogTitle } from '@angular/material/dialog';
import {
    MatAccordion, MatExpansionPanel, MatExpansionPanelContent, MatExpansionPanelHeader, MatExpansionPanelTitle
} from '@angular/material/expansion';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import {
    SaleRequestPaymentMethodsBenefitsDialogComponent
} from './dialog/sale-request-payment-methods-benefits-dialog.component';
import {
    SaleRequestPaymentMethodsBenefitsInstallmentsComponent
} from './installments/sale-request-payment-methods-benefits-installments.component';
import {
    SaleRequestPaymentMethodsBenefitsPresalesComponent
} from './presales/sale-request-payment-methods-benefits-presales.component';
import { VmSaleRequestGatewayBenefit } from './vm-sale-request-gateway-benefit.model';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MatDialogTitle, MatDialogContent, MatIcon, MatIconButton, TranslatePipe, MatButton, MatAccordion, MatExpansionPanel,
        MatExpansionPanelTitle, MatExpansionPanelHeader, MatExpansionPanelContent, SaleRequestPaymentMethodsBenefitsPresalesComponent,
        SaleRequestPaymentMethodsBenefitsInstallmentsComponent, MatProgressSpinner, EmptyStateTinyComponent
    ],
    selector: 'app-sale-request-payment-methods-benefits',
    templateUrl: './sale-request-payment-methods-benefits.component.html'
})
export class SaleRequestPaymentMethodsBenefitsComponent extends ObDialog<SaleRequestPaymentMethodsBenefitsComponent,
    { saleRequestId: number; gatewaySid: string; configurationSid: string; gatewayName: string }, unknown> implements OnDestroy {
    readonly #dialogSrv = inject(MatDialog);
    readonly #ephemeralMessageSrv = inject(EphemeralMessageService);
    readonly #viewContainerRef = inject(ViewContainerRef);
    readonly #salesRequestsSrv = inject(SalesRequestsService);
    readonly #msgDialogService = inject(MessageDialogService);
    readonly #$saleRequestBenefits = toSignal(this.#salesRequestsSrv.gatewayBenefits.get$());
    // custom_valid_period only for POST, it is not persisted. So we have to enhance the model from the backend
    // for the potential save (not ideal...)
    readonly #$saleRequestBenefitsEnhancedWithCustomValidPeriod = computed(() => {
        const saleRequestBenefits = this.#$saleRequestBenefits();
        const enhancedBenefitsWithCustomValidPeriod = saleRequestBenefits?.benefits.map(benefit => {
            const enhancedBinGroupsWithCustomValidPeriod = benefit.bin_groups.map(binGroup => ({
                ...binGroup,
                custom_valid_period: !!binGroup.validity_period?.start_date
            }));
            return {
                ...benefit,
                bin_groups: enhancedBinGroupsWithCustomValidPeriod
            };
        });
        return {
            benefits: enhancedBenefitsWithCustomValidPeriod
        };
    });

    readonly $vmSaleRequestsBenefits = signal<VmSaleRequestGatewayBenefit[]>([]);
    readonly $someBeingModified = computed(() => this.$vmSaleRequestsBenefits().some(benefit => benefit.beingModified));
    readonly $inProgress = toSignal(this.#salesRequestsSrv.gatewayBenefits.inProgress$());

    constructor() {
        super(DialogSize.LATERAL, true);
        this.dialogRef.addPanelClass('no-action-bar');

        this.#salesRequestsSrv.gatewayBenefits.load(this.data.saleRequestId, this.data.gatewaySid, this.data.configurationSid);
        toObservable(this.#$saleRequestBenefitsEnhancedWithCustomValidPeriod)
            .pipe(takeUntilDestroyed())
            .subscribe(saleRequestBenefits => {
                const result = ['INSTALLMENTS', 'PRESALE']
                    .map(type => {
                        const vmBenefit = this.$vmSaleRequestsBenefits()?.find(benefit => benefit.type === type);
                        const benefit = saleRequestBenefits?.benefits?.find(benefit => benefit.type === type);
                        if (vmBenefit?.beingModified && vmBenefit.saved && benefit) {
                            return benefit;
                        } else if (vmBenefit?.beingModified && vmBenefit.deleted && !benefit) {
                            return null;
                        } else if (vmBenefit?.beingModified && !vmBenefit.saved && !vmBenefit.deleted) {
                            return vmBenefit;
                        } else {
                            return benefit;
                        }
                    }).filter(Boolean);
                this.$vmSaleRequestsBenefits.set(result);
            });
    }

    ngOnDestroy(): void {
        this.#salesRequestsSrv.gatewayBenefits.clear();
    }

    create(): void {
        openDialog(this.#dialogSrv, SaleRequestPaymentMethodsBenefitsDialogComponent, { benefits: this.$vmSaleRequestsBenefits() },
            this.#viewContainerRef).beforeClosed()
            .subscribe(benefitType => {
                if (!benefitType) return;

                const benefitsCreated = ['INSTALLMENTS', 'PRESALE']
                    .map(type => {
                        if (type === benefitType) {
                            return { type: benefitType, beingModified: { create: true } };
                        } else {
                            return this.$vmSaleRequestsBenefits().find(benefit => benefit.type === type);
                        }
                    }).filter(Boolean);

                this.$vmSaleRequestsBenefits.set(benefitsCreated);
            });
    }

    edit(benefitType: VmSaleRequestGatewayBenefit['type']): void {
        this.$vmSaleRequestsBenefits.update(benefits => benefits.map(benefit => {
            if (benefit.type === benefitType) {
                return {
                    ...benefit,
                    beingModified: {
                        edit: true
                    }
                };
            } else {
                return benefit;
            }
        }));
    }

    cancel(benefitType: VmSaleRequestGatewayBenefit['type']): void {
        this.$vmSaleRequestsBenefits.update(benefits => benefits.map(benefit => {
            if (benefit.type === benefitType) {
                const { beingModified, ...restBenefit } = benefit;
                return restBenefit;
            } else {
                return benefit;
            }
        }));
    }

    delete(benefitType: VmSaleRequestGatewayBenefit['type']): void {
        const benefit = this.$vmSaleRequestsBenefits().find(benefit => benefit.type === benefitType);

        this.#msgDialogService.showWarn({
            size: DialogSize.SMALL,
            title: 'TITLES.CONFIRM_DELETE_BENEFIT',
            message: 'SALE_REQUESTS.DELETE_BENEFIT_WARNING',
            actionLabel: 'FORMS.ACTIONS.DELETE',
            showCancelButton: true
        }).subscribe(accepted => {
            if (!accepted) return;

            if (benefit?.beingModified?.create) {
                this.$vmSaleRequestsBenefits.update(benefits => benefits.filter(benefit => benefit.type !== benefitType));
            } else {
                const requestBenefits = ['INSTALLMENTS', 'PRESALE']
                    .map(type => {
                        if (benefitType === type) {
                            return null;
                        } else {
                            return this.#$saleRequestBenefitsEnhancedWithCustomValidPeriod()?.benefits
                                .find(benefit => benefit.type === type);
                        }
                    }).filter(Boolean);

                this.#salesRequestsSrv.gatewayBenefits.update(
                    this.data.saleRequestId, this.data.gatewaySid, this.data.configurationSid, { benefits: requestBenefits }
                ).subscribe(() => {
                    this.#ephemeralMessageSrv.showSuccess({ msgKey: 'SALE_REQUEST.PAYMENT_METHODS.BENEFITS.FORMS.FEEDBACK.DELETED' });
                    this.$vmSaleRequestsBenefits.update(benefits =>
                        benefits.map(benefitMap =>
                            benefitType === benefitMap.type ? { ...benefitMap, deleted: true } : benefitMap));
                    this.#salesRequestsSrv.loadSaleRequestPaymentMethods(this.data.saleRequestId);
                });
            }
        });
    }

    save(postBenefit: SaleRequestGatewayBenefit): void {
        let requestBenefits: SaleRequestGatewayBenefit[];
        if (this.#$saleRequestBenefitsEnhancedWithCustomValidPeriod()?.benefits.length) {
            requestBenefits = ['INSTALLMENTS', 'PRESALE']
                .map(type => {
                    if (postBenefit.type === type) {
                        return postBenefit;
                    } else {
                        return this.#$saleRequestBenefitsEnhancedWithCustomValidPeriod()?.benefits.find(benefit => benefit.type === type);
                    }
                }).filter(Boolean);
        } else {
            requestBenefits = [postBenefit];
        }

        this.#salesRequestsSrv.gatewayBenefits.update(
            this.data.saleRequestId, this.data.gatewaySid, this.data.configurationSid, { benefits: requestBenefits }
        ).subscribe(() => {
            this.#ephemeralMessageSrv.showSuccess({ msgKey: 'SALE_REQUEST.PAYMENT_METHODS.BENEFITS.FORMS.FEEDBACK.SAVED' });
            this.$vmSaleRequestsBenefits.update(benefits =>
                benefits.map(benefitMap =>
                    postBenefit.type === benefitMap.type ? { ...benefitMap, saved: true } : benefitMap));
            this.#salesRequestsSrv.loadSaleRequestPaymentMethods(this.data.saleRequestId);
        });
    }
}
