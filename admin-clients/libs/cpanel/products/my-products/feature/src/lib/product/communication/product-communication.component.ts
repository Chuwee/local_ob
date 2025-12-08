import { LastPathGuardListenerDirective } from '@admin-clients/cpanel/common/utils';
import { getDeepPath$ } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatDividerModule } from '@angular/material/divider';
import { MatTooltipModule } from '@angular/material/tooltip';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-product-communication',
    imports: [
        AsyncPipe,
        RouterModule, TranslatePipe, MatTooltipModule,
        MatButtonToggleModule, LastPathGuardListenerDirective, MatDividerModule
    ],
    templateUrl: './product-communication.component.html',
    styleUrls: ['./product-communication.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductCommunicationComponent {
    private readonly _route = inject(ActivatedRoute);
    private readonly _router = inject(Router);

    deepPath$ = getDeepPath$(this._router, this._route);

}
