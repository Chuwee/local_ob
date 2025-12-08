import { BiHistoryReport, BiService } from '@admin-clients/cpanel/bi/data-access';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { ObMatDialogConfig } from '@admin-clients/shared/common/ui/components';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { Platform } from '@angular/cdk/platform';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, Input, ViewContainerRef } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { first, firstValueFrom } from 'rxjs';
import { BI_SUBMIT } from '../../bi-reports.routes';
import {
    BiReportsHistoryDialogComponent,
    BiSubscriptionsRecipientsDialogInput
} from './history-dialog/bi-reports-history-dialog.component';

@Component({
    selector: 'app-bi-reports-list-recents',
    imports: [
        CommonModule,
        FlexLayoutModule,
        TranslatePipe,
        RouterModule,
        EllipsifyDirective
    ],
    providers: [DateTimePipe],
    templateUrl: './bi-reports-list-recents.component.html',
    styleUrls: ['./bi-reports-list-recents.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class BiReportsListRecentsComponent {
    readonly #viewContainerRef = inject(ViewContainerRef);
    readonly #matDialog = inject(MatDialog);
    readonly #biService = inject(BiService);
    readonly #auth = inject(AuthenticationService);
    readonly #router = inject(Router);
    readonly #route = inject(ActivatedRoute);
    readonly #platform = inject(Platform);
    readonly #biSubmit = inject(BI_SUBMIT);
    #recentReports: BiHistoryReport[];

    @Input() set recentReports(value: BiHistoryReport[]) {
        this.#recentReports = value.slice(0, 4);
    }

    get recentReports(): BiHistoryReport[] {
        return this.#recentReports;
    }

    async showMoreHistory(): Promise<void> {
        const biReports = await firstValueFrom(this.#biService.reportsHistoryList.get$());
        this.#matDialog.open<BiReportsHistoryDialogComponent, BiSubscriptionsRecipientsDialogInput, void>(
            BiReportsHistoryDialogComponent, new ObMatDialogConfig({
                biReports: biReports.slice(0, 10),
                title: 'BI_REPORTS.MOBILE_USERS.ASSIGN_LICENSE'
            }, this.#viewContainerRef)
        );
    }

    load(report: BiHistoryReport): void {
        if (report.url) {
            this.#auth.getLoggedUser$()
                .pipe(first())
                .subscribe(user => {
                    this.#biSubmit(report.url, user.reports.load, user.reports.logout, this.#platform);
                });
        } else {
            this.#router.navigate([report.id], { relativeTo: this.#route, queryParamsHandling: 'preserve' });
        }
    }
}
