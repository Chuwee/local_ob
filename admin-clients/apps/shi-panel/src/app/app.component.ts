import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { NavigationCancel, NavigationEnd, NavigationError, NavigationStart, Router, RouterOutlet } from '@angular/router';
import { filter, map, startWith, shareReplay } from 'rxjs';
import { patchRequiredInputs } from '@admin-clients/shared/common/ui/components';
import { TrackingService } from '@admin-clients/shared/data-access/trackers';
import { RoutingState } from '@admin-clients/shared/utility/state';

@Component({
    selector: 'app-shi-root',
    template: `
        <router-outlet />
        @if($loading()) {
            <div class="spinner-container">
                <mat-spinner />
            </div>
        }
    `,
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: true,
    imports: [RouterOutlet, MatProgressSpinner]
})
export class AppComponent implements OnInit {
    readonly #routingState = inject(RoutingState);
    readonly #trackingService = inject(TrackingService);
    readonly #router = inject(Router);

    readonly $loading = toSignal(
        this.#router.events.pipe(
            filter(e =>
                e instanceof NavigationStart ||
                e instanceof NavigationEnd ||
                e instanceof NavigationCancel ||
                e instanceof NavigationError
            ),
            map(e => e instanceof NavigationStart),
            startWith(false),
            shareReplay(1)
        )
    );

    constructor() {
        this.#trackingService.startTrackingGTM();
        this.#routingState.loadRouting();
        this.setManifest();
    }

    ngOnInit(): void {
        patchRequiredInputs();
    }

    private setManifest(): void {
        const linkManifest = document.createElement('link');
        linkManifest.setAttribute('rel', 'manifest');
        linkManifest.setAttribute('href', `manifest.webmanifest`);
        document.head.appendChild(linkManifest);
    }
}

