import { ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import {
    ChannelPromotionsService, ChannelPromotionDiscountType, ChannelPromotionType, ChannelPromotion
} from '@admin-clients/cpanel-channels-promotions-data-access';
import { PromotionStatus } from '@admin-clients/cpanel-common-promotions-utility-models';
import { mapPromoVirtualStatus } from '@admin-clients/cpanel-common-promotions-utility-utils';
import {
    DialogSize, EphemeralMessageService, MessageDialogConfig, MessageDialogService,
    NavTabsMenuComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { AsyncPipe, NgClass, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { RouterOutlet } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, of } from 'rxjs';
import { delay, filter, first, map, shareReplay, switchMap, tap, withLatestFrom } from 'rxjs/operators';

const unsavedChangesDialogData: MessageDialogConfig = {
    actionLabel: 'FORMS.ACTIONS.UPDATE',
    showCancelButton: true,
    message: 'CHANNELS.PROMOTIONS.STATUS_CHANGE_WARNING.DESCRIPTION',
    title: 'CHANNELS.PROMOTIONS.STATUS_CHANGE_WARNING.TITLE',
    size: DialogSize.MEDIUM
};

@Component({
    selector: 'app-channel-promotion-details',
    templateUrl: './channel-promotion-details.component.html',
    styleUrls: ['./channel-promotion-details.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MaterialModule, TranslatePipe, ReactiveFormsModule,
        NavTabsMenuComponent, NgIf, NgClass, RouterOutlet, AsyncPipe
    ]
})
export class ChannelPromotionDetailsComponent implements OnInit {
    readonly #channelsSrv = inject(ChannelsService);
    readonly #channelPromoSrv = inject(ChannelPromotionsService);
    readonly #ephemeralMsgSrv = inject(EphemeralMessageService);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #fb = inject(FormBuilder);
    readonly #onDestroy = inject(DestroyRef);

    #childComponent: WritingComponent;
    #channelId: number;
    #promotionId: number;

    readonly loading$ = this.#channelPromoSrv.isPromotionInProgress$();
    readonly statusCtrl = this.#fb.control({ value: null, disabled: true });
    readonly promotion$ = this.#channelPromoSrv.getPromotion$()
        .pipe(
            filter(Boolean),
            withLatestFrom(this.#channelsSrv.getChannel$()),
            tap(([promotion, channel]) => {
                this.#channelId = channel.id;
                this.#promotionId = promotion.id;
            }),
            map(([promo]) => mapPromoVirtualStatus()(promo)),
            takeUntilDestroyed(this.#onDestroy),
            shareReplay(1)
        );

    ngOnInit(): void {
        // status ctrl activation logic
        combineLatest([
            this.#channelPromoSrv.getPromotion$(),
            this.#channelPromoSrv.getPromotionEvents$(),
            this.#channelPromoSrv.getPromotionSessions$(),
            this.#channelPromoSrv.getPromotionPriceTypes$(),
            this.#channelPromoSrv.getPromotionContents$(),
            this.#channelsSrv.getChannel$().pipe(
                first(Boolean), map(channel => channel.languages?.selected)
            )
        ]).pipe(
            filter(all => all.every(model => !!model)),
            takeUntilDestroyed(this.#onDestroy)
        ).subscribe(([promo, events, sessions, pricetypes, contents, langs]) => {
            const isActivePromo = promo.status !== PromotionStatus.inactive;
            this.statusCtrl.patchValue(isActivePromo);
            const submodelsToCheck: { type?: string }[] = [promo.validity_period, events, sessions, pricetypes];
            if (submodelsToCheck.some(model => !model?.type) || contents.length < langs.length || !promo.discount?.type ||
                (
                    !(promo.discount?.value) &&
                    !(promo.discount?.percentage_value) &&
                    !(promo.discount?.fixed_values?.length) &&
                    !(promo.discount?.type === ChannelPromotionDiscountType.dynamic)
                ) ||
                (promo.type === ChannelPromotionType.collective && !promo.collective?.id)) {
                this.statusCtrl.disable();
            } else {
                this.statusCtrl.enable();
            }
        });
    }

    handleStatusChange(isActive: boolean): void {
        if (this.#childComponent?.form?.dirty) {
            of(null).pipe(
                delay(100),
                tap(() => this.statusCtrl.setValue(!isActive)),
                switchMap(() => this.#msgDialogSrv.showWarn(unsavedChangesDialogData)),
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
        const promotion: ChannelPromotion = { status };

        this.#channelPromoSrv.updatePromotion(this.#channelId, this.#promotionId, promotion)
            .subscribe({
                complete: () => {
                    this.#channelPromoSrv.loadPromotion(this.#channelId, this.#promotionId);
                    this.#channelPromoSrv.loadPromotionsList(this.#channelId, { limit: 999, offset: 0, sort: 'name:asc' });
                    this.#ephemeralMsgSrv.showSaveSuccess();
                },
                error: () => this.statusCtrl.patchValue(!isActive)
            });
    }

    childComponentChange(child: WritingComponent): void {
        this.#childComponent = child;
    }
}
