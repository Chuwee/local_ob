import { BiReport } from '@admin-clients/cpanel/bi/data-access';
import { TabDirective, TabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { UpperCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { FlexModule } from '@angular/flex-layout';
import { MatExpansionModule } from '@angular/material/expansion';
import { TranslatePipe } from '@ngx-translate/core';
import { BiReportListElementComponent } from '../element/bi-report-list-element.component';

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
    selector: 'app-bi-reports-list-tabs',
    imports: [
        MatExpansionModule, TranslatePipe, FlexModule, TabsMenuComponent, TabDirective,
        BiReportListElementComponent, UpperCasePipe
    ],
    templateUrl: './bi-reports-list-tabs.component.html'
})
export class BiReportsListTabsComponent {
    vmReports: VmBiReportCategory[];

    @Input() set reports(value: BiReport[]) {
        this.vmReports = value.reduce<VmBiReportCategory[]>((acc, biReport) => {
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
        }, []);
    }
}
