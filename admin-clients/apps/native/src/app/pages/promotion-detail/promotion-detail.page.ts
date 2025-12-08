import {
    EventPromotion, EventPromotionsService, eventPromotionsProviders
} from '@admin-clients/cpanel/promoters/events/promotions/data-access';
import {
    CommunicationContentTextType
} from '@admin-clients/cpanel/shared/data-access';
import { PromotionDiscountType, PromotionStatus } from '@admin-clients/cpanel-common-promotions-utility-models';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { ChangeDetectionStrategy, Component, ViewChild, effect, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute } from '@angular/router';
import { ToastController, ToggleCustomEvent } from '@ionic/angular';
import { TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'promotion-detail',
    templateUrl: './promotion-detail.page.html',
    styleUrls: ['./promotion-detail.page.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [eventPromotionsProviders],
    standalone: false
})

export class PromotionDetailPage {
    readonly #eventPromotionsService = inject(EventPromotionsService);
    readonly #activatedRoute = inject(ActivatedRoute);
    readonly #toastController = inject(ToastController);
    readonly #translateService = inject(TranslateService);

    @ViewChild('toggleBtn') private readonly _toggleBtn: HTMLIonToggleElement;

    #eventId: number;
    #promotionId: number;

    isError = false;
    isUpdatingStatus = false;
    promotionName: string;
    promotionLang: string;
    promotionDescription: string;
    readonly dateTimeFormats = DateTimeFormats;

    readonly promotionDiscountTypes = PromotionDiscountType;

    readonly $isLoading = toSignal(this.#eventPromotionsService.promotion.loading$());
    readonly $promotion = toSignal(this.#eventPromotionsService.promotion.get$());
    readonly $promotionChannels = toSignal(this.#eventPromotionsService.promotionChannels.get$());
    readonly $promotionSessions = toSignal(this.#eventPromotionsService.promotionSessions.get$());
    readonly $promotionPriceTypes = toSignal(this.#eventPromotionsService.promotionPriceTypes.get$());
    readonly $promotionRates = toSignal(this.#eventPromotionsService.promotionRates.get$());
    readonly $promotionChannelTextContent = toSignal(this.#eventPromotionsService.promotionChannelTextContents.get$());
    readonly $textContent = effect(() => {
        if (this.$promotionChannelTextContent()) {
            this.$promotionChannelTextContent().forEach(textContent => {
                if (textContent.type === CommunicationContentTextType.name) {
                    this.promotionLang = textContent.language;
                }

                if (textContent.type === CommunicationContentTextType.description) {
                    this.promotionDescription = textContent.value;
                }
            });
        }
    });

    readonly loadData$ = this.#activatedRoute.queryParams.subscribe({
        next: params => {
            this.#eventId = params['event_id'];
            this.#promotionId = params['promotion_id'];
            this.promotionName = params['promotion_name'];

            this.loadAllServices(params['event_id'], params['promotion_id']);
        }
    });

    updateStatus(e: ToggleCustomEvent): void {
        this.isUpdatingStatus = true;
        const promotionStatus = e.detail.checked ? PromotionStatus.active : PromotionStatus.inactive;
        const promotion: EventPromotion = {
            status: promotionStatus
        };

        this.#eventPromotionsService.promotion.update(this.#eventId, this.#promotionId, promotion).subscribe({
            next: () => {
                const message = this.#translateService.instant(
                    'EVENT_DETAIL.INFO.TOAST.SUCCESS');
                this.showToast('success', message);
                this.loadAllServices(this.#eventId, this.#promotionId);
                this.isUpdatingStatus = false;
            },
            error: response => {
                if (response.error.code) {
                    const message = this.#translateService.instant(
                        'EVENT_DETAIL.INFO.TOAST.' + response.error.code);
                    this.showToast('error', message);
                    this._toggleBtn.checked = false;
                }
            }
        });
    }

    reTry(): void {
        this.loadAllServices(this.#eventId, this.#promotionId);
    }

    toggleActivatedClass(ev: Event): void {
        const element = ev.currentTarget as HTMLElement;
        const allAccordionTitles = document.querySelectorAll('.promotion-detail__accordion-title');

        allAccordionTitles.forEach(accordion =>
            accordion.id === element.id ? element.classList.toggle('activated') : accordion.classList.remove('activated')
        );
    }

    getItemList(list: { name: string }[]): string {
        return list.map(session => session.name).toString().replace(/,/g, ', ');
    }

    private loadAllServices(eventId: number, promotionId: number): void {
        this.#eventPromotionsService.promotion.load(eventId, promotionId);
        this.#eventPromotionsService.promotionChannelTextContents.load(eventId, promotionId);
        this.#eventPromotionsService.promotionChannels.load(eventId, promotionId);
        this.#eventPromotionsService.promotionSessions.load(eventId, promotionId);
        this.#eventPromotionsService.promotionPriceTypes.load(eventId, promotionId);
        this.#eventPromotionsService.promotionRates.load(eventId, promotionId);
        this.#eventPromotionsService.promotionPacks.load(eventId, promotionId);
    }

    private async showToast(type: 'success' | 'error', message: string): Promise<void> {
        const activeToast = await this.#toastController.getTop();

        if (activeToast) {
            await this.#toastController.dismiss();
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
