import { InsurersService } from '@admin-clients/cpanel-configurations-insurers-data-access';
import { GoBackComponent, NavTabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnDestroy } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
    selector: 'app-insurer-details',
    imports: [
        AsyncPipe, RouterModule,
        GoBackComponent, NavTabsMenuComponent
    ],
    templateUrl: './insurer-details.component.html',
    styleUrls: ['./insurer-details.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class InsurerDetailsComponent implements OnDestroy {
    readonly #insurerSrv = inject(InsurersService);

    readonly insurer$ = this.#insurerSrv.insurer.get$();

    ngOnDestroy(): void {
        this.#insurerSrv.insurer.clear();
    }
}
