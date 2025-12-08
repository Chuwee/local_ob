import { BiService } from '@admin-clients/cpanel/bi/data-access';
import { GoBackComponent } from '@admin-clients/shared/common/ui/components';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FlexModule } from '@angular/flex-layout';
import { MatTooltip } from '@angular/material/tooltip';
import { RouterOutlet } from '@angular/router';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FlexModule, GoBackComponent, RouterOutlet, EllipsifyDirective, AsyncPipe, MatTooltip
    ],
    selector: 'app-bi-reports-details',
    templateUrl: './bi-report-details.component.html'
})
export class BiReportDetailsComponent {
    private readonly _biReportsSrv = inject(BiService);
    readonly report$ = this._biReportsSrv.report.get$();
}
