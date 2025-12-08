import {
    ChangeDetectionStrategy,
    Component,
    inject,
    viewChild
} from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatButton } from '@angular/material/button';
import { MatCard, MatCardContent, MatCardFooter } from '@angular/material/card';
import { MatTooltip } from '@angular/material/tooltip';
import { Router } from '@angular/router';
import { SatPopoverComponent, SatPopoverModule } from '@ncstate/sat-popover';
import { TranslatePipe } from '@ngx-translate/core';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';

@Component({
    imports: [
        MatCard, MatCardFooter, MatCardContent, MatButton, MatTooltip, TranslatePipe, SatPopoverModule
    ],
    selector: 'app-popover-user',
    templateUrl: './popover-user.component.html',
    styleUrls: ['./popover-user.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PopoverUserComponent {

    readonly #auth = inject(AuthenticationService);
    readonly #router = inject(Router);

    readonly $user = toSignal(this.#auth.getLoggedUser$());
    readonly $popover = viewChild(SatPopoverComponent);

    logout(): void {
        this.$popover().close();
        this.#auth.logout();
        this.#router.navigate(['login']);
    }
}
