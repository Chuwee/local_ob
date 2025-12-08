import { SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import { getDeepPath$ } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { distinctUntilChanged } from 'rxjs/operators';

@Component({
    selector: 'app-season-ticket-communication',
    templateUrl: './season-ticket-communication.component.html',
    styleUrls: ['./season-ticket-communication.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SeasonTicketCommunicationComponent {
    private readonly _route = inject(ActivatedRoute);
    private readonly _router = inject(Router);
    readonly deepPath$ = getDeepPath$(this._router, this._route);
    readonly isGenerationStatusError$ = inject(SeasonTicketsService).seasonTicketStatus.isGenerationStatusError$()
        .pipe(distinctUntilChanged());
}
