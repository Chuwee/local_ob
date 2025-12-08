import { BiSupersetService } from '@admin-clients/cpanel/bi/data-access';
import { ContextNotificationComponent } from '@admin-clients/shared/common/ui/components';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { MatDividerModule } from '@angular/material/divider';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, map, Observable } from 'rxjs';
import { VmBiReportCategorySearch } from '../../models/vm-reports.model';
import { BiSupersetReportListElementComponent } from '../element/bi-superset-report-list-element.component';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        AsyncPipe, TranslatePipe, BiSupersetReportListElementComponent, MatDividerModule, ContextNotificationComponent
    ],
    selector: 'app-bi-superset-reports-list-search-results',
    templateUrl: './bi-superset-reports-list-search-results.component.html'
})
export class BiSupersetReportsListSearchResultsComponent {
    readonly #biService = inject(BiSupersetService);

    readonly biReportsSearch$: Observable<VmBiReportCategorySearch[]> = this.#biService.reportsSearch.get$()
        .pipe(
            filter(Boolean),
            map(biReports =>
                biReports.reduce<VmBiReportCategorySearch[]>((acc, biReport) => {
                    const category = acc.find(category => category.name === biReport.category);
                    if (!category) {
                        acc.push({
                            name: biReport.category,
                            reports: [biReport]
                        });
                        return acc;
                    }

                    category.reports.push(biReport);
                    return acc;
                }, [])
            )
        );
}
