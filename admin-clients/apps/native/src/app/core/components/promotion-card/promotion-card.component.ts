import {
    EventPromotion, EventPromotionListElement, EventPromotionsService
} from '@admin-clients/cpanel/promoters/events/promotions/data-access';
import { PromotionStatus } from '@admin-clients/cpanel-common-promotions-utility-models';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, EventEmitter, Input, Output, ViewChild, inject } from '@angular/core';
import { Router } from '@angular/router';
import { IonicModule, ToastController, ToggleCustomEvent } from '@ionic/angular';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'promotion-card',
    imports: [CommonModule, IonicModule, TranslatePipe, DateTimePipe],
    templateUrl: './promotion-card.component.html',
    styleUrls: ['./promotion-card.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PromotionCardComponent {
    readonly #eventPromotionsService = inject(EventPromotionsService);
    readonly #changeDetector = inject(ChangeDetectorRef);
    readonly #toastController = inject(ToastController);
    readonly #translateService = inject(TranslateService);
    readonly #router = inject(Router);
    @ViewChild('toggleBtn') private readonly _toggleBtn: HTMLIonToggleElement;
    @Input() readonly promotion: EventPromotionListElement;
    @Input() readonly eventId: number;
    @Output() readonly emitTap = new EventEmitter<EventPromotionListElement>();

    readonly dateTimeFormats = DateTimeFormats;

    goToPromotionDetail(): void {
        this.#router.navigate(['promotion-detail'],
            {
                queryParams: {
                    event_id: this.eventId,
                    promotion_id: this.promotion.id,
                    promotion_name: this.promotion.name
                }
            });
    }

    getStatusClass(): string {
        return `promotion-card__status-item--${this.promotion.status.toLowerCase()}`;
    }

    updateStatus(e: ToggleCustomEvent): void {
        const promotionStatus = e.detail.checked ? PromotionStatus.active : PromotionStatus.inactive;
        const promotion: EventPromotion = {
            status: promotionStatus
        };

        this.#eventPromotionsService.promotion.update(this.eventId, this.promotion.id, promotion).subscribe({
            next: () => {
                const message = this.#translateService.instant('EVENT_DETAIL.INFO.TOAST.SUCCESS');
                this.showToast('success', message);
                this.promotion.status = promotionStatus;
                this.#changeDetector.detectChanges();
            },
            error: response => {
                if (response.error.code) {
                    const message = this.#translateService.instant('EVENT_DETAIL.INFO.TOAST.' + response.error.code);
                    this.showToast('error', message);
                    this._toggleBtn.checked = false;
                }
            }
        });

    }

    private async showToast(type: 'success' | 'error', message: string): Promise<void> {
        const toastExists = !!(await this.#toastController.getTop());
        if (toastExists) {
            this.#toastController.dismiss();
        }

        const toast = await this.#toastController.create({
            message,
            duration: 2500,
            position: 'top',
            icon: `./assets/media/icons/${type}_circle.svg`,
            cssClass: `ob-toast ob-toast--${type}`
        });

        await toast.present();
    }
}
