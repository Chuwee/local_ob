
import { ProductsService } from '@admin-clients/cpanel/products/my-products/data-access';
import { PromotionStatus } from '@admin-clients/cpanel-common-promotions-utility-models';
import { DialogSize, EphemeralMessageService, MessageDialogConfig, MessageDialogService, NavTabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { NgClass } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnDestroy } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RouterOutlet } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { of, delay, tap, switchMap, filter, map } from 'rxjs';

const unsavedChangesDialogData: MessageDialogConfig = {
    actionLabel: 'FORMS.ACTIONS.UPDATE',
    showCancelButton: true,
    message: 'PRODUCT.PROMOTIONS.STATUS_CHANGE_WARNING.DESCRIPTION',
    title: 'PRODUCT.PROMOTIONS.STATUS_CHANGE_WARNING.TITLE',
    size: DialogSize.MEDIUM
};

@Component({
    selector: 'app-product-promotion-details',
    templateUrl: './product-promotion-details.component.html',
    styleUrls: ['./product-promotion-details.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        NavTabsMenuComponent, MatTooltipModule, MatSlideToggleModule, ReactiveFormsModule, TranslatePipe,
        NgClass, RouterOutlet
    ]
})
export class ProductPromotionDetailsComponent implements OnDestroy {
    readonly #fb = inject(FormBuilder);
    readonly #destroyRef = inject(DestroyRef);
    readonly #productsSrv = inject(ProductsService);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #ephemeralMsgSrv = inject(EphemeralMessageService);
    readonly statusCtrl = this.#fb.control({ value: null, disabled: true });
    readonly $promotion = toSignal(this.#productsSrv.product.promotion.get$().pipe(
        filter(Boolean),
        tap(promo => {
            const isActivePromo = promo.status !== PromotionStatus.inactive;
            this.statusCtrl.patchValue(isActivePromo);

            if (!promo?.name || !promo?.discount || !promo.activator) {
                this.statusCtrl.disable();
            } else {
                this.statusCtrl.enable();
            }
        }),
        takeUntilDestroyed(this.#destroyRef))
    );

    readonly $productId = toSignal(this.#productsSrv.product.get$()
        .pipe(
            filter(Boolean),
            map(product => product.product_id),
            takeUntilDestroyed(this.#destroyRef)
        )
    );

    #childComponent: WritingComponent;

    handleStatusChange(isActive: boolean): void {
        if (this.#childComponent?.form?.dirty) {
            of(null).pipe(
                delay(100),
                tap(() => this.statusCtrl.setValue(!isActive)),
                switchMap(() =>
                    this.#msgDialogSrv.showWarn(unsavedChangesDialogData)
                ),
                switchMap(saveAccepted =>
                    saveAccepted ? this.#childComponent.save$() : of(false)
                )
            ).subscribe();
        } else {
            this.savePromotionStatus(isActive);
        }
    }

    savePromotionStatus(isActive: boolean): void {
        const status = isActive ? PromotionStatus.active : PromotionStatus.inactive;
        const promotion = { status };

        this.#productsSrv.product.promotion.update(this.$productId(), this.$promotion().id, promotion)
            .subscribe({
                next: () => {
                    this.#loadPromotion();
                    this.#loadPromotionList();
                    this.#ephemeralMsgSrv.showSaveSuccess();
                },
                error: () => this.statusCtrl.patchValue(!isActive)
            });
    }

    childComponentChange(child: WritingComponent): void {
        this.#childComponent = child;
    }

    ngOnDestroy(): void {
        this.#productsSrv.product.promotion.clear();
    }

    #loadPromotion(): void {
        this.#productsSrv.product.promotion.load(this.$productId(), this.$promotion().id);
    }

    #loadPromotionList(): void {
        this.#productsSrv.product.promotionList.load(this.$productId(), { limit: 999, offset: 0 });
    }
}
