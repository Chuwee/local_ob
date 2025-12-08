import { LastPathGuardListenerDirective } from '@admin-clients/cpanel/common/utils';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { getDeepPath$ } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatButtonToggle, MatButtonToggleGroup } from '@angular/material/button-toggle';
import { ActivatedRoute, Router, RouterLink, RouterOutlet } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { map } from 'rxjs';

@Component({
    selector: 'ob-event-design',
    imports: [MatButtonToggleGroup, MatButtonToggle, TranslatePipe, RouterOutlet, RouterLink, LastPathGuardListenerDirective],
    templateUrl: './events-design.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EventsDesignComponent {
    readonly #router = inject(Router);
    readonly #route = inject(ActivatedRoute);
    readonly #entitiesSrv = inject(EntitiesBaseService);

    readonly $enabledPostBookingQuestions = toSignal(
        this.#entitiesSrv.getEntity$().pipe(map(entity => entity?.settings.post_booking_questions?.enabled)),
        { initialValue: false }
    );

    readonly $deepPath = toSignal(getDeepPath$(this.#router, this.#route));
}
