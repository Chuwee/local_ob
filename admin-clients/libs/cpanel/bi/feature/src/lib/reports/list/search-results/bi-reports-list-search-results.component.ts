import { BiService } from '@admin-clients/cpanel/bi/data-access';
import { ContextNotificationComponent } from '@admin-clients/shared/common/ui/components';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FlexModule } from '@angular/flex-layout';
import { MatDividerModule } from '@angular/material/divider';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, map, Observable } from 'rxjs';
import { VmBiReportCategorySearch } from '../../models/vm-reports.model';
import { BiReportListElementComponent } from '../element/bi-report-list-element.component';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        AsyncPipe, TranslatePipe, BiReportListElementComponent, MatDividerModule, ContextNotificationComponent, FlexModule
    ],
    selector: 'app-bi-reports-list-search-results',
    templateUrl: './bi-reports-list-search-results.component.html'
})
export class BiReportsListSearchResultsComponent {
    readonly #biService = inject(BiService);

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
