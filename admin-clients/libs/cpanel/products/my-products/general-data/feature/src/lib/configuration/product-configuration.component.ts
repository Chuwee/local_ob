import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { Product, ProductsService } from '@admin-clients/cpanel/products/my-products/data-access';
import {
    EmptyStateComponent, EphemeralMessageService, NavTabsMenuComponent, ObMatDialogConfig
} from '@admin-clients/shared/common/ui/components';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, inject } from '@angular/core';
import { FlexLayoutModule, FlexModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RouterOutlet } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, map, shareReplay, tap } from 'rxjs';
import { CreateProductVariantsDialogComponent } from './create-product-variants/create-product-variants-dialog.component';
import { ProductConfigSimpleComponent } from './simple/product-config-simple.component';

@Component({
    selector: 'app-product-configuration',
    imports: [
        MatFormFieldModule, MatTooltipModule, MatInputModule, MatButtonModule, MatIconModule, MatProgressSpinner,
        TranslatePipe, ReactiveFormsModule, FlexModule, FlexLayoutModule,
        EmptyStateComponent, AsyncPipe, RouterOutlet,
        NavTabsMenuComponent, ProductConfigSimpleComponent, MatDialogModule
    ],
    templateUrl: './product-configuration.component.html',
    styleUrls: ['./product-configuration.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductConfigurationComponent implements OnDestroy {
    readonly #productsSrv = inject(ProductsService);
    readonly #ephemeralMessageSrv = inject(EphemeralMessageService);
    readonly #auth = inject(AuthenticationService);
    readonly #matDialog = inject(MatDialog);

    #currentProduct: Product;

    readonly canWrite$ = this.#auth.getLoggedUser$().pipe(
        map(user => AuthenticationService.isSomeRoleInUserRoles(user, [UserRoles.OPR_MGR, UserRoles.ENT_MGR, UserRoles.EVN_MGR])),
        shareReplay({ bufferSize: 1, refCount: true })
    );

    readonly isLoadingOrSaving$ = booleanOrMerge([
        this.#productsSrv.product.variants.loading$(),
        this.#productsSrv.product.inProgress$()
    ]);

    readonly product$ = this.#productsSrv.product.get$().pipe(
        filter(Boolean),
        tap(product => {
            this.#currentProduct = product;
            this.#productsSrv.product.variants.loadIfNull(product.product_id);
        }),
        shareReplay({ bufferSize: 1, refCount: true })
    );

    readonly productHasVariants$ = this.#productsSrv.product.variants.getMetadata$().pipe(
        filter(Boolean),
        map(variantsMeta => variantsMeta.total > 0)
    );

    ngOnDestroy(): void {
        this.#productsSrv.product.variants.clear();
    }

    openCreateProductVariantsDialog(): void {
        this.#matDialog.open<CreateProductVariantsDialogComponent, null, boolean>(
            CreateProductVariantsDialogComponent, new ObMatDialogConfig()
        )
            .beforeClosed()
            .subscribe(variantsCreationSuccess => {
                if (variantsCreationSuccess) {
                    this.#ephemeralMessageSrv.showSuccess({ msgKey: 'PRODUCT.VARIANTS.FORMS.FEEDBACKS.VARIANTS_CREATION_SUCCESS' });
                    this.#productsSrv.product.variants.load(this.#currentProduct.product_id);
                }
            });
    }
}
