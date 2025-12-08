import { AsyncPipe } from '@angular/common';
import {
    ChangeDetectionStrategy, Component, inject, ViewChild
} from '@angular/core';
import { MatButton } from '@angular/material/button';
import { MatCard, MatCardContent, MatCardFooter } from '@angular/material/card';
import { MatTooltip } from '@angular/material/tooltip';
import { Router } from '@angular/router';
import { SatPopoverAnchorDirective, SatPopoverComponent } from '@ncstate/sat-popover';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { BasicUser } from '@admin-clients/shared/data-access/models';
import { AuthenticationService } from '@admin-clients/shi-panel/data-access-auth';

@Component({
    imports: [
        MatButton, MatCard, MatCardContent, MatCardFooter, MatTooltip,
        AsyncPipe, TranslatePipe, SatPopoverComponent, SatPopoverAnchorDirective
    ],
    selector: 'app-popover-user',
    templateUrl: './popover-user.component.html',
    styleUrls: ['./popover-user.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PopoverUserComponent {
    private readonly _auth = inject(AuthenticationService);
    private readonly _router = inject(Router);
    @ViewChild('userPopover') userPopover: SatPopoverComponent;
    readonly user$: Observable<BasicUser> = this._auth.getLoggedUser$();

    logout(): void {
        this.userPopover.close();
        this._auth.logout();
        this._router.navigate(['login']);
    }
}
