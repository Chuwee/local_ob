import { ProductsDeliveryPointsService } from '@admin-clients/cpanel/products/delivery-points/data-access';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { DateTimeFormats, IdName } from '@admin-clients/shared/data-access/models';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { atLeastOneRequiredInArray } from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit, inject } from '@angular/core';
import { FlexLayoutModule, FlexModule } from '@angular/flex-layout';
import { FormBuilder } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { ProductDeliveryPointsSelectorComponent } from '../delivery-points-selector/product-delivery-points-selector.component';

@Component({
    selector: 'app-set-sessions-delivery-points-dialog',
    imports: [CommonModule, TranslatePipe, MaterialModule, ProductDeliveryPointsSelectorComponent,
        FlexLayoutModule, FlexModule, DateTimePipe],
    templateUrl: './set-sessions-delivery-points-dialog.component.html',
    styleUrls: ['./set-sessions-delivery-points-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SetSessionsDeliveryPointsDialogComponent implements OnInit {

    private readonly _deliveryPointSrv = inject(ProductsDeliveryPointsService);
    private readonly _fb = inject(FormBuilder);

    private readonly _dialogRef = inject(MatDialogRef<SetSessionsDeliveryPointsDialogComponent>);

    readonly data = inject(MAT_DIALOG_DATA);
    readonly form = this._fb.group({
        deliveryPoints: this._fb.control<IdName[]>([], atLeastOneRequiredInArray())
    });

    readonly dateTimeFormats = DateTimeFormats;
    isInProgress$ = this._deliveryPointSrv.productsDeliveryPointsList.loading$();

    ngOnInit(): void {
        this.form.controls.deliveryPoints.patchValue(this.data.session?.delivery_points || []);
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
    }

    close(selectedDeliveryPoints?: IdName[]): void {
        this._dialogRef.close(selectedDeliveryPoints);
    }

    setSessionsDeliveryPoints(): void {
        this._dialogRef.close(this.form.value.deliveryPoints);
    }

}
