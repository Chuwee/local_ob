import { ProductsDeliveryPointsService } from '@admin-clients/cpanel/products/delivery-points/data-access';
import { EmptyStateTinyComponent, SearchablePaginatedSelectionModule } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { IdName } from '@admin-clients/shared/data-access/models';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input, inject } from '@angular/core';
import { FlexLayoutModule, FlexModule } from '@angular/flex-layout';
import { FormControl } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, map, shareReplay, take } from 'rxjs';

@Component({
    selector: 'app-product-delivery-points-selector',
    templateUrl: './product-delivery-points-selector.component.html',
    styleUrls: ['./product-delivery-points-selector.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        CommonModule, SearchablePaginatedSelectionModule, MaterialModule, TranslatePipe, FlexModule, FlexLayoutModule,
        EmptyStateTinyComponent, RouterModule
    ]
})
export class ProductDeliveryPointsSelectorComponent {
    readonly #deliveryPointsSrv = inject(ProductsDeliveryPointsService);

    @Input() form: FormControl<IdName[]>;

    readonly allDeliveryPoints$ = this.#deliveryPointsSrv.productsDeliveryPointsList.getData$().pipe(
        filter(Boolean),
        map(deliveryPoints => deliveryPoints.map(deliveryPoint => ({ id: deliveryPoint.id, name: deliveryPoint.name }))),
        shareReplay({ refCount: true, bufferSize: 1 })
    );

    selectAllDeliveryPointsInGroup(deliveryPointsNotSelected: IdName[]): void {
        this.form.patchValue([
            ...this.form.value,
            ...deliveryPointsNotSelected
        ]);
    }

    diselectAllDeliveryPointsInGroup(deliveryPointsNotSelected: IdName[]): void {
        const deliveryPoints = this.form.value.filter(deliveryPoint => deliveryPointsNotSelected
            .map(deliveryPoint => deliveryPoint.id).includes(deliveryPoint.id));
        this.form.patchValue(deliveryPoints);
    }

    handleDeliveryPointsSelectionGroupChange(checked: boolean): void {
        this.allDeliveryPoints$.pipe(take(1)).subscribe(deliveryPoints => {
            const currentFormDeliveryPointIds = this.form.value.map(deliveryPoint => deliveryPoint.id);
            const deliveryPointsGroupNotSelected = deliveryPoints?.filter(deliveryPoint =>
                !currentFormDeliveryPointIds.includes(deliveryPoint.id)) || [];
            if (checked) this.selectAllDeliveryPointsInGroup(deliveryPointsGroupNotSelected);
            else this.diselectAllDeliveryPointsInGroup(deliveryPointsGroupNotSelected);
            this.form.markAsTouched();
            this.form.markAsDirty();
        });
    }
}
