import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ChannelSurcharge } from '@admin-clients/cpanel/channels/data-access';
import { SalesRequestsService } from '@admin-clients/cpanel-channels-sales-requests-data-access';
import { TaxesMode } from '@admin-clients/cpanel-promoters-events-prices-data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit, viewChild, viewChildren } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { forkJoin, Observable, Subject, switchMap, throwError } from 'rxjs';
import { first, map, tap } from 'rxjs/operators';
import { SaleRequestChannelSurchargesTaxesComponent } from './taxes/sale-request-surcharges-taxes.component';

@Component({
    selector: 'app-sale-request-surcharges',
    templateUrl: './sale-request-surcharges.component.html',
    styleUrls: ['./sale-request-surcharges.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SaleRequestSurchargesComponent implements OnInit, OnDestroy, WritingComponent {
    readonly #salesRequestsSrv = inject(SalesRequestsService);
    readonly #ephemeralMessageSrv = inject(EphemeralMessageService);
    readonly #entitiesBaseSrv = inject(EntitiesBaseService);
    readonly #fb = inject(FormBuilder);
    readonly #onDestroy = new Subject<void>();

    private readonly _$matExpansionPanelQueryList = viewChildren(MatExpansionPanel);
    private readonly _$taxesComponent = viewChild(SaleRequestChannelSurchargesTaxesComponent);

    readonly surchargesRequestCtrl = this.#fb.nonNullable.control<ChannelSurcharge[]>([]);
    readonly form = this.#fb.nonNullable.group({});
    readonly $currency = toSignal(this.#salesRequestsSrv.getSaleRequest$()
        .pipe(
            first(),
            map(saleRequest => saleRequest.event.currency_code)
        ));

    readonly $isInProgress = toSignal(booleanOrMerge([
        this.#salesRequestsSrv.isSaleRequestSurchargesLoading$(),
        this.#salesRequestsSrv.isSaleRequestSurchargesSaving$(),
        this.#salesRequestsSrv.surchargeTaxes.loading$(),
        this.#entitiesBaseSrv.isEntityTaxesLoading()
    ]));

    readonly $saleRequest = toSignal(this.#salesRequestsSrv.getSaleRequest$().pipe(first()));
    readonly taxesMode = TaxesMode;
    simulationExpanded = false;
    // TODO: Remove isTestTaxes when  taxes end
    readonly isTestTaxes = false;
    ngOnInit(): void {
        this.#salesRequestsSrv.getSaleRequest$()
            .pipe(first())
            .subscribe(saleRequest => {
                this.#salesRequestsSrv.loadSaleRequestSurcharges(saleRequest.id);
                this.#salesRequestsSrv.surchargeTaxes.load(saleRequest.id);
            });
    }

    ngOnDestroy(): void {
        this.#onDestroy.next(null);
        this.#onDestroy.complete();
        this.#salesRequestsSrv.clearSaleRequestSurcharges();
        this.#salesRequestsSrv.surchargeTaxes.clear();
    }

    cancel(): void {
        this.reloadModels();
    }

    save(): void {
        this.save$().subscribe(() => this.reloadModels());
    }

    save$(): Observable<unknown> {
        if (this.form.valid) {
            this.surchargesRequestCtrl.setValue([]);
            return this.#salesRequestsSrv.getSaleRequest$()
                .pipe(
                    first(),
                    switchMap(saleRequest => {
                        const requests: Observable<unknown>[] =
                            [this.#salesRequestsSrv.saveSaleRequestSurcharges(saleRequest.id, this.surchargesRequestCtrl.value)];

                        const surchargeTaxesReq = this._$taxesComponent()?.getRequest(saleRequest.id);
                        if (surchargeTaxesReq) { requests.push(surchargeTaxesReq); };

                        return forkJoin(requests).pipe(tap(() =>
                            this.#ephemeralMessageSrv.showSuccess(
                                {
                                    msgKey: 'SALE_REQUEST.UPDATE_SUCCESS',
                                    msgParams: { saleRequestName: `${saleRequest.event.name}-${saleRequest.channel.name}` }
                                })
                        ));
                    })
                );
        } else {
            this.form.markAllAsTouched();
            this.form.setValue(this.form.getRawValue());
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._$matExpansionPanelQueryList());
            return throwError(() => 'invalid form');
        }
    }

    private reloadModels(): void {
        this.#salesRequestsSrv.getSaleRequest$()
            .pipe(first())
            .subscribe(saleRequest => {
                this.form.markAsPristine();
                this.form.markAsUntouched();
                this.surchargesRequestCtrl.reset([], { emitEvent: false });
                this.#salesRequestsSrv.loadSaleRequestSurcharges(saleRequest.id);
                this.#salesRequestsSrv.surchargeTaxes.load(saleRequest.id);
            });
    }
}
