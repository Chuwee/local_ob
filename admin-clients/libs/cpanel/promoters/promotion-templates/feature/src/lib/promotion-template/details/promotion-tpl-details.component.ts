import { PromotionTpl, PromotionTplsService } from '@admin-clients/cpanel/promoters/promotion-templates/data-access';
import { GoBackComponent, NavTabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable } from 'rxjs';

@Component({
    selector: 'app-promotion-tpl-details',
    templateUrl: './promotion-tpl-details.component.html',
    styleUrls: ['./promotion-tpl-details.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        GoBackComponent, NavTabsMenuComponent, RouterOutlet, TranslatePipe, AsyncPipe
    ]
})
export class PromotionTplDetailsComponent implements OnInit, OnDestroy {
    promotionTpl$: Observable<PromotionTpl>;

    constructor(private _promotionTplsSrv: PromotionTplsService) { }

    ngOnInit(): void {
        this.promotionTpl$ = this._promotionTplsSrv.getPromotionTemplate$();
    }

    ngOnDestroy(): void {
        this._promotionTplsSrv.clearPromotionTemplate();
    }
}
