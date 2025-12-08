import { ExternalAtmApi, PutATMChannelConfiguration } from '@admin-clients/cpanel/external/data-access';
import { Event } from '@admin-clients/cpanel/promoters/events/data-access';
import { EventPromotionListElement, EventPromotionsService } from '@admin-clients/cpanel/promoters/events/promotions/data-access';
import { PromotionStatus, PromotionType } from '@admin-clients/cpanel-common-promotions-utility-models';
import {
    ContextNotificationComponent, EphemeralMessageService,
    SelectionListComponent
} from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { UntypedFormControl } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

/**
 * ATM ad hoc component, to set up member promotions in channel config
 */
@Component({
    selector: 'app-atm-member-promotions',
    templateUrl: 'atm-member-promotions.component.html',
    styleUrls: ['atm-member-promotions.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        DateTimePipe,
        AsyncPipe,
        MatButton, MatProgressSpinner,
        ContextNotificationComponent,
        TranslatePipe,
        FlexLayoutModule,
        SelectionListComponent
    ]
})
export class AtmMemberPromotionsComponent implements OnInit {

    @Input() event: Event;

    readonly pageSize = 50;
    readonly dateTimeFormats = DateTimeFormats;

    form: UntypedFormControl;
    promotions$: Observable<EventPromotionListElement[]>;
    loading$: Observable<boolean>;
    saving: boolean;

    constructor(
        private _eventPromoServ: EventPromotionsService,
        private _externalAtmApi: ExternalAtmApi,
        private _ephemeralMessageService: EphemeralMessageService
    ) { }

    ngOnInit(): void {
        this.loadPromotions();
        this.promotions$ = this._eventPromoServ.promotionsList.getData$()
            .pipe(map(promotion => promotion?.filter(promo => this.filterPromotion(promo))));
        this.loading$ = this._eventPromoServ.promotionsList.loading$();
        this.form = new UntypedFormControl();
    }

    loadPromotions(): void {
        this._eventPromoServ.promotionsList.load(this.event.id, {
            limit: this.pageSize,
            sort: 'name:asc'
        });
    }

    run(): void {
        const payload: PutATMChannelConfiguration = { promotions: this.selectedIds };
        if (this.selectedIds?.length && !this.saving) {
            this.saving = true;
            this._externalAtmApi.channelConfiguration(this.event.id, payload)
                .pipe(finalize(() => this.saving = false))
                .subscribe(() => {
                    this.showSuccessMessage();
                    this.form.reset();
                });
        }
    }

    get selectedIds(): number[] {
        return this.form.value?.map(promotion => promotion.id);
    }

    private filterPromotion(promo: EventPromotionListElement): boolean {
        return (promo.type === PromotionType.basic) && promo.status === PromotionStatus.active;
    }

    private showSuccessMessage(): void {
        this._ephemeralMessageService.showSaveSuccess();
    }
}
