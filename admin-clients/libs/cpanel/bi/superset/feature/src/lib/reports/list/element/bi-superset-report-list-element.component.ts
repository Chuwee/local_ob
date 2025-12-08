import { BiReport } from '@admin-clients/cpanel/bi/data-access';
import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';
import { BiSupersetReportsComponent } from '../../bi-superset-reports.component';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    selector: 'app-superset-report-list-element',
    styleUrls: ['./bi-superset-report-list-element.component.scss'],
    imports: [
        RouterModule
    ],
    templateUrl: './bi-superset-report-list-element.component.html'
})
export class BiSupersetReportListElementComponent extends BiSupersetReportsComponent {
    readonly biReport = input<BiReport>();
}
