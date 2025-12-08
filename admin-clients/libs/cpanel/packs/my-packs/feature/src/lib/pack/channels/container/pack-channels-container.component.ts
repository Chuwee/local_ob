import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { PacksService } from '@admin-clients/cpanel/packs/my-packs/data-access';
import { EmptyStateComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { AsyncPipe, NgClass } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, inject, viewChild, OnInit } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule, FlexModule } from '@angular/flex-layout';
import { RouterModule } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, map } from 'rxjs';
import { PackChannelsListComponent } from '../list/pack-channels-list.component';

@Component({
    selector: 'app-pack-channels-container',
    imports: [
        NgClass, MaterialModule, TranslatePipe, FlexModule, FlexLayoutModule, RouterModule,
        PackChannelsListComponent, EmptyStateComponent, AsyncPipe
    ],
    templateUrl: './pack-channels-container.component.html',
    styleUrls: ['./pack-channels-container.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PackChannelsContainerComponent implements OnDestroy, OnInit {
    readonly #breakpointObserver = inject(BreakpointObserver);
    readonly #packsSrv = inject(PacksService);
    readonly #authSrv = inject(AuthenticationService);

    readonly packChannels$ = this.#packsSrv.pack.channels.getData$();

    readonly isLoading$ = this.#packsSrv.pack.channels.loading$();

    readonly sidebarWidth$: Observable<string> = this.#breakpointObserver
        .observe([Breakpoints.XSmall, Breakpoints.Small, Breakpoints.Medium])
        .pipe(
            map(result => result.matches ? '240px' : '280px')
        );

    readonly canWrite$ = this.#authSrv.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.ENT_MGR, UserRoles.EVN_MGR]);

    readonly $pack = toSignal(this.#packsSrv.pack.get$());

    listComponent = viewChild(PackChannelsListComponent);

    ngOnInit(): void {
        this.#packsSrv.pack.channels.load(this.$pack()?.id);
    }

    ngOnDestroy(): void {
        this.#packsSrv.pack.channels.clear();
    }

    addChannels(): void {
        this.listComponent().openAddChannelsDialog();
    }

}
