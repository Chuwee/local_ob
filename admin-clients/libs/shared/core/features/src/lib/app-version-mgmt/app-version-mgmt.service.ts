import { ENVIRONMENT_TOKEN } from '@OneboxTM/utils-environment';
import { EphemeralMessageService, MessageType } from '@admin-clients/shared/common/ui/components';
import { ApplicationRef, Injectable, inject } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { SwUpdate, VersionReadyEvent } from '@angular/service-worker';
import { BehaviorSubject, delay, filter, from, interval, switchMap, tap } from 'rxjs';
import { first, take, takeUntil } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class AppVersionMgmtService {

    private readonly _pollingInterval = 30000; // 30 * 1000, 30 seconds
    private readonly _showNewVersionDelay = 300000; // 5 * 60 * 1000, 5 minutes

    private readonly _env = inject(ENVIRONMENT_TOKEN);
    private readonly _appRef = inject(ApplicationRef);
    private readonly _swUpdate = inject(SwUpdate);
    private readonly _router = inject(Router);
    private readonly _ephemeralMsg = inject(EphemeralMessageService);

    private readonly _inited = new BehaviorSubject(false);

    readonly inited$ = this._inited.asObservable();

    constructor() {
        if (this._swUpdate.isEnabled && this._env.branch === 'default') {
            // if any update download has produced an inconsistent state, reloads
            this._swUpdate.unrecoverable.subscribe(event => {
                console.error(`An error occurred that we cannot recover from:\n ${event.reason} \n\nPlease reload the page.`);
                window.location.reload();
            });
            // emits when new version is ready
            const newVersionAvailable$ = this._swUpdate.versionUpdates.pipe(
                filter((evt): evt is VersionReadyEvent => evt.type === 'VERSION_READY'),
                take(1)
            );
            // Allow the app to stabilize first, before starting polling for updates with `interval()`.
            this._appRef.isStable
                .pipe(
                    first(Boolean),
                    tap(() => this._inited.next(true)),
                    switchMap(() => interval(this._pollingInterval)),
                    takeUntil(newVersionAvailable$)
                )
                .subscribe(() => this._swUpdate.checkForUpdate());
            // when new version is ready, waits for any navigation to reload
            newVersionAvailable$
                .pipe(
                    tap(() => this.logUpdateAvailable()),
                    switchMap(() => this._router.events),
                    filter(event => event instanceof NavigationEnd),
                    switchMap(() => from(this._swUpdate.activateUpdate())),
                    filter(Boolean)
                )
                .subscribe(() => document.location.reload());
            // delays the "new version available" notification, any navigation will reload the app, and the notification will not be shown
            newVersionAvailable$
                .pipe(delay(this._showNewVersionDelay))
                .subscribe(() => this.showNewVersionMessage());
        } else {
            this._inited.next(true);
        }
    }

    private logUpdateAvailable(): void {
        const infoIcon = String.fromCodePoint(0x2139); // info emoji
        console.log(
            `%c${infoIcon}%c New version available`,
            'color:white; background-color:DodgerBlue; padding:1px 6px; border-radius:50%',
            'color:DodgerBlue'
        );
    }

    private showNewVersionMessage(): void {
        this._ephemeralMsg.show({
            type: MessageType.info,
            msgKey: 'ACTIONS.NEW_APP_VERSION.DESCRIPTION',
            duration: 0
        });
    }
}
