import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ChannelCommission } from '@admin-clients/cpanel/channels/data-access';
import { SalesRequestsService } from '@admin-clients/cpanel-channels-sales-requests-data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, inject } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable, Subject, switchMap, throwError } from 'rxjs';
import { first, map, tap } from 'rxjs/operators';

@Component({
    selector: 'app-sale-request-commissions',
    templateUrl: './sale-request-commissions.component.html',
    styleUrls: ['./sale-request-commissions.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SaleRequestCommissionsComponent implements OnInit, OnDestroy, WritingComponent {
    private readonly _salesRequestsSrv = inject(SalesRequestsService);
    private readonly _ephemeralMessageSrv = inject(EphemeralMessageService);

    private readonly _onDestroy = new Subject<void>();

    readonly commissionsRequestCtrl = inject(FormBuilder).nonNullable.control([] as ChannelCommission[]);
    readonly form = inject(FormBuilder).nonNullable.group({});
    readonly currency$ = this._salesRequestsSrv.getSaleRequest$()
        .pipe(
            first(),
            map(saleRequest => saleRequest.event.currency_code)
        );

    readonly isInProgress$ = booleanOrMerge([
        this._salesRequestsSrv.isSaleRequestCommissionsLoading$(),
        this._salesRequestsSrv.isSaleRequestCommissionsSaving$()
    ]);

    ngOnInit(): void {
        this._salesRequestsSrv.getSaleRequest$()
            .pipe(first())
            .subscribe(saleRequest => this._salesRequestsSrv.loadSaleRequestCommissions(saleRequest.id));
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._salesRequestsSrv.clearSaleRequestCommissions();
    }

    cancel(): void {
        this.reloadModels();
    }

    save(): void {
        this.save$().subscribe(() => this.reloadModels());
    }

    save$(): Observable<unknown> {
        if (this.form.valid) {
            this.commissionsRequestCtrl.setValue([]);
            return this._salesRequestsSrv.getSaleRequest$()
                .pipe(
                    first(),
                    switchMap(saleRequest =>
                        this._salesRequestsSrv.saveSaleRequestCommissions(saleRequest.id, this.commissionsRequestCtrl.value)
                            .pipe(tap(() => this._ephemeralMessageSrv.showSuccess(
                                {
                                    msgKey: 'SALE_REQUEST.UPDATE_SUCCESS',
                                    msgParams: { saleRequestName: `${saleRequest.event.name}-${saleRequest.channel.name}` }
                                })
                            ))
                    )
                );
        } else {
            this.form.markAllAsTouched();
            this.form.setValue(this.form.getRawValue());
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return throwError(() => 'invalid form');
        }
    }

    private reloadModels(): void {
        this._salesRequestsSrv.getSaleRequest$()
            .pipe(first())
            .subscribe(saleRequest => {
                this.form.markAsPristine();
                this.form.markAsUntouched();
                this.commissionsRequestCtrl.reset([], { emitEvent: false });
                this._salesRequestsSrv.loadSaleRequestCommissions(saleRequest.id);
            });
    }
}
