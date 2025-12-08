import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { NavigationCancel, NavigationEnd, NavigationError, NavigationStart, Router, RouterOutlet } from '@angular/router';
import { filter, map, startWith, shareReplay } from 'rxjs/operators';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { patchRequiredInputs } from '@admin-clients/shared/common/ui/components';
import { WebsocketsService, WSErrors } from '@admin-clients/shared/core/data-access';
import { TrackingService } from '@admin-clients/shared/data-access/trackers';
import { CustomResourcesService } from '@admin-clients/shared/utility/pipes';
import { RoutingState } from '@admin-clients/shared/utility/state';

@Component({
    selector: 'app-root',
    imports: [RouterOutlet, MatProgressSpinner],
    template: `
        <router-outlet />
        @if ($loading()) {
            <div class="spinner-container">
                <mat-spinner/>
            </div>
        }
    `,
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class AppComponent implements OnInit {

    readonly #router = inject(Router);
    readonly #routingState = inject(RoutingState);
    readonly #trackingService = inject(TrackingService);
    readonly #auth = inject(AuthenticationService);
    readonly #wsSrv = inject(WebsocketsService);
    readonly #customResourcesSrv = inject(CustomResourcesService);

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
        this.#trackingService.startTrackingUA();
        this.#trackingService.startTrackingGTM();
        this.#routingState.loadRouting();
        this.setManifest();
        this.#auth.getResourcesUrls$()
            .subscribe(resourcesUrl => {
                this.#customResourcesSrv.setCustomResources(resourcesUrl);
                this.setFavicon(resourcesUrl.favicon_url);
            });
    }

    ngOnInit(): void {
        patchRequiredInputs();
        // websocket unauthorized check to navigate to login
        this.#wsSrv.getParsedStompErrors$()
            .pipe(filter(error => error?.code === WSErrors.unauthorizedError))
            .subscribe(() => this.#router.navigate(['login']));
    }

    private setManifest(): void {
        const linkManifest = document.createElement('link');
        linkManifest.setAttribute('rel', 'manifest');
        linkManifest.setAttribute('href', `manifest.webmanifest`);
        document.head.appendChild(linkManifest);
    }

    private setFavicon(faviconUrl: string): void {
        const icon = faviconUrl ?? 'assets/icons/icon-120x120.png';
        const favicon = document.querySelector<HTMLLinkElement>('#favicon');
        favicon.href = icon;
    }

}
