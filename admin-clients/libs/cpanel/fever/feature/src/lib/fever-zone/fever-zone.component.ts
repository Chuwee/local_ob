import { FeverService } from '@admin-clients/cpanel-fever-data-access';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { ObMatDialogConfig } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, effect, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatDialog } from '@angular/material/dialog';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { Router } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { FeverZoneEntityDialogComponent } from './entity-dialog/fever-zone-entity-dialog.component';

@Component({
    selector: 'app-fever-zone',
    template: `
        <div class="spinner-container flex flex-col gap-8">
            <h2>{{'FEVER.FEVER_ZONE.LOADING' | translate}}</h2>
            <mat-spinner />
        </div>
    `,
    imports: [MatProgressSpinner, TranslatePipe],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class FeverZoneComponent {
    readonly #auth = inject(AuthenticationService);
    readonly #dialog = inject(MatDialog);
    readonly #router = inject(Router);
    readonly #feverService = inject(FeverService);

    readonly #$user = toSignal(this.#auth.getLoggedUser$());
    readonly #$isOperator = toSignal(this.#auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.OPR_ANS]));
    readonly #$token = toSignal(this.#auth.getToken$());

    constructor() {
        effect(() => {
            const user = this.#$user();
            const token = this.#$token();
            const isOperator = this.#$isOperator();
            if (user && token) {
                if (isOperator) {
                    this.openEntityDialog();
                } else {
                    this.#redirectToFeverZone(this.#$token(), this.#$user().entity.id);
                }
            }
        })
    }

    openEntityDialog(): void {
        this.#dialog.open(FeverZoneEntityDialogComponent, new ObMatDialogConfig())
            .beforeClosed()
            .subscribe(result => {
                if (result) {
                    this.#redirectToFeverZone(this.#$token(), result);
                } else {
                    this.#goToRoot();
                }
            });
    }

    #goToRoot(): void {
        this.#router.navigate(['/']);
    }

    #redirectToFeverZone(token: string, entityId: number): void {
        const feverZoneUrl = this.#feverService.loginUrl(entityId, token);

        window.location.href = feverZoneUrl;
    }
}
