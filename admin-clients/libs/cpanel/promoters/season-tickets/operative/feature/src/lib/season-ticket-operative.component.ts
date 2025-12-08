import { LastPathGuardListenerDirective } from '@admin-clients/cpanel/common/utils';
import { SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import { ContextNotificationComponent } from '@admin-clients/shared/common/ui/components';
import { getDeepPath$ } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { ActivatedRoute, Router, RouterLink, RouterOutlet } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-season-ticket-operative',
    templateUrl: './season-ticket-operative.component.html',
    styles: `:host { position: relative; }`,
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [MatButtonToggleModule, TranslatePipe, RouterOutlet, RouterLink,
        LastPathGuardListenerDirective, ContextNotificationComponent, MatProgressBarModule]
})
export class SeasonTicketOperativeComponent {

    readonly #router = inject(Router);
    readonly #route = inject(ActivatedRoute);
    readonly #seasonTicketSrv = inject(SeasonTicketsService);

    readonly $isGenerationStatusInProgress = toSignal(this.#seasonTicketSrv.seasonTicketStatus.isGenerationStatusInProgress$());

    readonly $deepPath = toSignal(getDeepPath$(this.#router, this.#route));
}