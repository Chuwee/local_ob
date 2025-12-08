import { ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { Subject } from 'rxjs';
import { filter, takeUntil } from 'rxjs/operators';
import { ChannelPromotionsService, ChannelPromotion } from '@admin-clients/cpanel-channels-promotions-data-access';

@Component({
    selector: 'app-channel-promotion-packs',
    templateUrl: './channel-promotion-packs.component.html',
    styleUrls: ['./channel-promotion-packs.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class ChannelPromotionPacksComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();

    @Input() form: UntypedFormGroup;

    constructor(
        private _channelPromotionsService: ChannelPromotionsService
    ) { }

    ngOnInit(): void {
        this.initFormChangesHandlers();

        this._channelPromotionsService.getPromotion$()
            .pipe(
                filter(promotion => !!promotion),
                takeUntil(this._onDestroy)
            ).subscribe(promotion => {
                this.updateFormValues(promotion);
            });
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    private updateFormValues(promotion: ChannelPromotion): void {
        this.form.reset(promotion.packs);
    }

    private initFormChangesHandlers(): void {
        this.form.get('enabled').valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(isEnabled => {
                if (isEnabled) {
                    this.form.get('events').enable();
                    this.form.get('sessions').enable();
                } else {
                    this.form.get('events').disable();
                    this.form.get('sessions').disable();
                }
            });
    }

}
