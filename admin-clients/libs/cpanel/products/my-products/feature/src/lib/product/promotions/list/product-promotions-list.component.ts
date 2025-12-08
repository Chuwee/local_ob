
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { ProductsService } from '@admin-clients/cpanel/products/my-products/data-access';
import { PromotionValidityPeriodType } from '@admin-clients/cpanel-common-promotions-utility-models';
import { DialogSize, EphemeralMessageService, MessageDialogService, ObMatDialogConfig } from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { NgClass } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, map, tap } from 'rxjs';
import { NewProductPromotionDialogComponent } from '../create/new-product-promotion-dialog.component';

@Component({
    selector: 'app-product-promotions-list',
    templateUrl: './product-promotions-list.component.html',
    styleUrls: ['./product-promotions-list.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FlexLayoutModule, MatProgressSpinner, TranslatePipe, MatButtonModule, MatTooltipModule,
        MatIconModule, MatListModule, EllipsifyDirective, NgClass
    ]
})
export class ProductPromotionsListComponent implements OnInit, OnDestroy {
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);
    readonly #router = inject(Router);
    readonly #route = inject(ActivatedRoute);
    readonly #dialog = inject(MatDialog);
    readonly #destroyRef = inject(DestroyRef);
    readonly #productsSrv = inject(ProductsService);
    readonly #authSrv = inject(AuthenticationService);

    readonly $selectedPromotionId = signal(0);
    readonly dateTimeFormats = DateTimeFormats;
    readonly validityPeriodType = PromotionValidityPeriodType;
    readonly $loadingList = toSignal(this.#productsSrv.product.promotionList.loading$());
    readonly $promotionList = toSignal(this.#productsSrv.product.promotionList.getData$().pipe(
        filter(Boolean),
        tap(promotions => {
            if (!promotions.length) return;
            if (!this.$selectedPromotionId()) this.$selectedPromotionId.set(promotions[0]?.id);
            const element = document.getElementById('promotion-list-option-' + this.$selectedPromotionId());
            element?.scrollIntoView({ behavior: 'smooth', block: 'center' });

            const path = this.#currentPath();
            this.#router.navigate([path], { relativeTo: this.#route });
        })
    ));

    readonly $totalPromotions = toSignal(this.#productsSrv.product.promotionList.getMetadata$().pipe(map(md => md?.total || 0)));
    readonly $canCreate = toSignal(this.#authSrv.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.EVN_MGR]));
    readonly $productId = toSignal(this.#productsSrv.product.get$()
        .pipe(
            filter(Boolean),
            map(product => product.product_id),
            takeUntilDestroyed(this.#destroyRef)
        )
    );

    ngOnInit(): void {
        this.#loadPromotionList();
    }

    ngOnDestroy(): void {
        this.#productsSrv.product.promotionList.clear();
    }

    openNewPromotionDialog(): void {
        this.#dialog.open(NewProductPromotionDialogComponent, new ObMatDialogConfig({ productId: this.$productId() }))
            .beforeClosed().pipe(filter(Boolean))
            .subscribe((promotionId: number) => {
                if (!promotionId) return;
                this.#ephemeralMessageService.showSuccess({ msgKey: 'PRODUCT.PROMOTIONS.CREATE_SUCCESS' });
                this.#loadPromotionList();
                this.selectionChangeHandler(promotionId);
            });
    }

    openDeletePromotionDialog(): void {
        this.#msgDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'TITLES.DELETE_PROMOTION',
            message: 'PRODUCT.PROMOTIONS.DELETE_PROMOTION_WARNING',
            messageParams: {},
            actionLabel: 'FORMS.ACTIONS.DELETE',
            showCancelButton: true
        })
            .subscribe(isConfirmed => {
                if (!isConfirmed) return;
                this.#productsSrv.product.promotion.delete(this.$productId(), this.$selectedPromotionId()).subscribe({
                    next: () => {
                        this.#ephemeralMessageService.showDeleteSuccess();
                        this.#loadPromotionList();
                        this.selectionChangeHandler(this.$promotionList()?.at(0)?.id);
                    }
                });
            });
    }

    selectionChangeHandler(promotionId: number): void {
        if (!!promotionId && this.$selectedPromotionId() !== promotionId) {
            this.$selectedPromotionId.set(promotionId);
            const path = this.#currentPath();
            this.#router.navigate([path], { relativeTo: this.#route });
        }
    }

    #loadPromotionList(): void {
        this.#productsSrv.product.promotionList.load(this.$productId(), { limit: 999, offset: 0 });
    }

    #currentPath(): string {
        return this.#innerPath ?
            this.$selectedPromotionId().toString() + '/' + this.#innerPath :
            this.$selectedPromotionId().toString();
    }

    get #innerPath(): string {
        return this.#route.snapshot.children[0]?.children[0]?.routeConfig.path;
    }
}
