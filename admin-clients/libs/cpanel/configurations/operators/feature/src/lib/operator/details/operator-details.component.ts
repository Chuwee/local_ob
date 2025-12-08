import { OperatorsService } from '@admin-clients/cpanel-configurations-operators-data-access';
import { GoBackComponent, NavTabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { AsyncPipe, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { RouterOutlet } from '@angular/router';

@Component({
    selector: 'app-operator-details',
    templateUrl: './operator-details.component.html',
    styleUrls: ['./operator-details.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FlexLayoutModule,
        RouterOutlet,
        GoBackComponent,
        NgIf, AsyncPipe,
        NavTabsMenuComponent
    ]
})
export class OperatorDetailsComponent implements OnDestroy {
    private readonly _operatorsSrv = inject(OperatorsService);

    readonly operator$ = this._operatorsSrv.operator.get$();

    ngOnDestroy(): void {
        this._operatorsSrv.operator.clear();
    }
}
