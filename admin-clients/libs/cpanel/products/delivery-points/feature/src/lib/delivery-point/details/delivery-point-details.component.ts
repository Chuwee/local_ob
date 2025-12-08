import {
    DeliveryPoint, DeliveryPointStatus, ProductsDeliveryPointsService, PutProductDeliveryPoint
} from '@admin-clients/cpanel/products/delivery-points/data-access';
import { EphemeralMessageService, GoBackComponent, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnDestroy, QueryList, ViewChildren } from '@angular/core';
import { FlexLayoutModule, FlexModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { RouterModule } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { tap } from 'rxjs';

@Component({
    selector: 'app-delivery-point-details',
    imports: [CommonModule, GoBackComponent, FlexLayoutModule, FlexModule, TranslatePipe, RouterModule,
        MaterialModule, ReactiveFormsModule],
    templateUrl: './delivery-point-details.component.html',
    styleUrls: ['./delivery-point-details.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class DeliveryPointDetailsComponent implements OnDestroy {

    @ViewChildren(MatExpansionPanel) private readonly _matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    private readonly _deliveryPointSrv = inject(ProductsDeliveryPointsService);
    private readonly _msgDialogSrv = inject(MessageDialogService);
    private readonly _fb = inject(FormBuilder);
    private readonly _ephemeralMessageSrv = inject(EphemeralMessageService);

    private _deliveryPointId: number;
    private _currentChildRouteComponent;

    readonly form = this._fb.group({
        status: [true, [Validators.required]]
    });

    readonly deliveryPoint$ = this._deliveryPointSrv.deliveryPoint.get$().pipe(tap(deliveryPoint => this.patchFormValues(deliveryPoint)));
    readonly isLoadingOrSaving$ = this._deliveryPointSrv.deliveryPoint.inProgress$();

    ngOnDestroy(): void {
        this._deliveryPointSrv.deliveryPoint.clear();
    }

    updateStatus(): void {
        if (this._currentChildRouteComponent.form.dirty) {
            this._msgDialogSrv.showWarn({
                title: 'DELIVERY_POINT.STATUS_CHANGE_WARN.TITLE',
                message: 'DELIVERY_POINT.STATUS_CHANGE_WARN.DESCRIPTION',
                actionLabel: 'DELIVERY_POINT.STATUS_CHANGE_WARN.CONFIRM'
            }).subscribe(accepted => {
                if (accepted) {
                    this.save();
                } else {
                    this.form.controls.status.setValue(!this.form.value.status);
                }
            });
        } else {
            this.save();
        }
    }

    save(): void {
        this._deliveryPointSrv.deliveryPoint.upload(this._deliveryPointId, this.getPutBody())
            .pipe(tap(() => this._ephemeralMessageSrv.showSaveSuccess()))
            .subscribe(() => {
                this._deliveryPointSrv.deliveryPoint.load(this._deliveryPointId),
                    this._currentChildRouteComponent.form.markAsPristine();
                this._currentChildRouteComponent.form.markAsUntouched();
            });
    }

    handleChildRouteComponentInstanceChange(component): void {
        this._currentChildRouteComponent = component;
    }

    private patchFormValues(deliveryPoint: DeliveryPoint): void {
        this._deliveryPointId = deliveryPoint.id;
        this.form.patchValue({
            status: deliveryPoint.status === DeliveryPointStatus.active ? true : false
        });
    }

    private getPutBody(): PutProductDeliveryPoint {
        return {
            status: this.form.value.status ? DeliveryPointStatus.active : DeliveryPointStatus.inactive
        };
    }

}
