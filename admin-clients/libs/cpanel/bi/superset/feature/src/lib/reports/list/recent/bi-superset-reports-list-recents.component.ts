import { BiHistoryReport, BiSupersetService } from '@admin-clients/cpanel/bi/data-access';
import { ObMatDialogConfig } from '@admin-clients/shared/common/ui/components';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { ChangeDetectionStrategy, Component, effect, inject, input, signal, ViewContainerRef } from '@angular/core';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { RouterModule } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { firstValueFrom } from 'rxjs';
import {
    BiSupersetReportsHistoryDialogComponent,
    BiSubscriptionsRecipientsDialogInput
} from './history-dialog/bi-superset-reports-history-dialog.component';
import { BiSupersetReportsComponent } from '../../bi-superset-reports.component';

@Component({
    selector: 'app-superset-reports-list-recents',
    imports: [
        TranslatePipe, RouterModule, EllipsifyDirective, MatDialogModule
    ],
    providers: [DateTimePipe],
    templateUrl: './bi-superset-reports-list-recents.component.html',
    styleUrls: ['./bi-superset-reports-list-recents.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class BiSupersetReportsListRecentsComponent extends BiSupersetReportsComponent {
    readonly #viewContainerRef = inject(ViewContainerRef);
    readonly #matDialog = inject(MatDialog);
    readonly #biService = inject(BiSupersetService);
    readonly recentReportsInput = input.required<BiHistoryReport[]>();
    readonly recentReports = signal<BiHistoryReport[]>([]);

    constructor() {
        super();
        effect(() => {
            this.recentReports.set(this.recentReportsInput()?.slice(0, 4) ?? []);
        });
    }


    async showMoreHistory(): Promise<void> {
        const biReports = await firstValueFrom(this.#biService.reportsHistoryList.get$());
        this.#matDialog.open<BiSupersetReportsHistoryDialogComponent, BiSubscriptionsRecipientsDialogInput, void>(
            BiSupersetReportsHistoryDialogComponent, new ObMatDialogConfig({
                biReports: biReports.slice(0, 10),
                title: 'BI_REPORTS.MOBILE_USERS.ASSIGN_LICENSE'
            }, this.#viewContainerRef)
        );
    }
}
