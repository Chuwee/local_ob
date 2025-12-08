import { BiReport } from '@admin-clients/cpanel/bi/data-access';
import { TabDirective, TabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { UpperCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, input, Input } from '@angular/core';
import { MatExpansionModule } from '@angular/material/expansion';
import { TranslatePipe } from '@ngx-translate/core';
import { BiSupersetReportListElementComponent } from '../element/bi-superset-report-list-element.component';

interface VmBiReportCategory {
    name: string;
    subcategories: VmBiReportSubcategory[];
}
interface VmBiReportSubcategory {
    name: string;
    reports: BiReport[];
}

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    selector: 'app-bi-superset-reports-list-tabs',
    imports: [
        MatExpansionModule, TranslatePipe, TabsMenuComponent, TabDirective,
        BiSupersetReportListElementComponent, UpperCasePipe
    ],
    templateUrl: './bi-superset-reports-list-tabs.component.html'
})
export class BiSupersetReportsListTabsComponent {
    reports = input<BiReport[]>();

    vmReports = computed(() => this.reports()?.reduce<VmBiReportCategory[]>((acc, biReport) => {
        const category = acc.find(category => category.name === biReport.category);
        if (!category) {
            acc.push({
                name: biReport.category,
                subcategories: [{
                    name: biReport.subcategory,
                    reports: [biReport]
                }]
            });
            return acc;
        }

        const subcategory = category.subcategories.find(subcategory => subcategory.name === biReport.subcategory);
        if (!subcategory) {
            category.subcategories.push({
                name: biReport.subcategory,
                reports: [biReport]
            });
            return acc;
        }

        subcategory.reports.push(biReport);
        return acc;
    }, []) ?? []
    );
}
