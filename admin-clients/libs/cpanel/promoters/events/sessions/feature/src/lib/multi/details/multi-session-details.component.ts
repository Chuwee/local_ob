import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { EventSessionsService } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { EventType } from '@admin-clients/shared/common/data-access';
import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { first, map } from 'rxjs/operators';

@Component({
    selector: 'app-multi-session-details',
    templateUrl: './multi-session-details.component.html',
    styleUrls: ['./multi-session-details.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class MultiSessionDetailsComponent implements OnInit {
    readonly #eventSrv = inject(EventsService);
    readonly #sessionsSrv = inject(EventSessionsService);

    readonly $showCapacityLink = toSignal(
        this.#eventSrv.event.get$().pipe(map(event => event.type === EventType.normal))
    );

    readonly $archived = toSignal(
        this.#sessionsSrv.getSelectedSessions$()
            .pipe(map(sessionWrappers =>
                !sessionWrappers.length
                || sessionWrappers.some(sessionWrapper => sessionWrapper?.session?.archived)
            ))
    );

    readonly $showOtherSettingsLink = toSignal(this.#eventSrv.event.get$()
        .pipe(
            first(Boolean),
            map(event => !Boolean(event.type === EventType.activity || event.type === EventType.themePark))
        )
    );

    ngOnInit(): void {
        this.#sessionsSrv.session.clear();
    }
}
