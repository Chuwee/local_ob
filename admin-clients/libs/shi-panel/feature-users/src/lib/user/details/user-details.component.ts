import { GoBackComponent, NavTabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatTabsModule } from '@angular/material/tabs';
import { ActivatedRoute, RouterOutlet } from '@angular/router';
import { UsersService } from '../../users.service';

@Component({
    imports: [CommonModule, RouterOutlet, GoBackComponent, MatTabsModule, FlexLayoutModule, NavTabsMenuComponent],
    selector: 'app-user-details',
    templateUrl: './user-details.component.html',
    styleUrls: ['./user-details.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserDetailsComponent implements OnInit, OnDestroy {
    private readonly _usersService = inject(UsersService);
    private readonly _route = inject(ActivatedRoute);

    readonly user$ = this._usersService.userDetailsProvider.getUser$();
    isMyUserUrl: boolean;

    ngOnInit(): void {
        const userId = this._route.snapshot.params?.['userId'];
        this.isMyUserUrl = !!userId;
    }

    ngOnDestroy(): void {
        this._usersService.userDetailsProvider.clearUser();
    }
}
