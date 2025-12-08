import { LastPathGuardListenerDirective } from '@admin-clients/cpanel/common/utils';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { getDeepPath$ } from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ActivatedRoute, Router, RouterLink, RouterOutlet } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FlexLayoutModule,
        MaterialModule,
        CommonModule,
        TranslatePipe,
        LastPathGuardListenerDirective,
        RouterOutlet,
        RouterLink
    ],
    selector: 'app-season-ticket-prices',
    templateUrl: './season-ticket-prices.component.html'
})
export class SeasonTicketPricesComponent {
    private readonly _route = inject(ActivatedRoute);
    private readonly _router = inject(Router);

    readonly deepPath$ = getDeepPath$(this._router, this._route);

}
