import { BiHistoryReport } from '@admin-clients/cpanel/bi/data-access';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { isHandset$ } from '@admin-clients/shared/utility/utils';
import { Platform } from '@angular/cdk/platform';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatIconButton } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { first } from 'rxjs';
import { BI_SUBMIT } from '../../../bi-reports.routes';

export type BiSubscriptionsRecipientsDialogInput = { biReports: BiHistoryReport[] };

@Component({
    selector: 'app-bi-reports-history-dialog',
    templateUrl: './bi-reports-history-dialog.component.html',
    styleUrls: ['./bi-reports-history-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        AsyncPipe, FlexLayoutModule, TranslatePipe, RouterModule, DateTimePipe, EllipsifyDirective, MatTableModule, MatDialogModule,
        MatIconButton, MatIcon
    ]
})
export class BiReportsHistoryDialogComponent {
    readonly #auth = inject(AuthenticationService);
    readonly #router = inject(Router);
    readonly #route = inject(ActivatedRoute);
    readonly #dialogRef = inject<MatDialogRef<BiReportsHistoryDialogComponent, void>>(MatDialogRef);
    readonly #platform = inject(Platform);
    readonly #biSubmit = inject(BI_SUBMIT);
    readonly biReports = inject<BiSubscriptionsRecipientsDialogInput>(MAT_DIALOG_DATA).biReports;

    readonly pageSize = 10;
    readonly dateTimeFormats = DateTimeFormats;
    readonly columns = ['report', 'category', 'date'];

    readonly isHandset$ = isHandset$();

    constructor() {
        this.#dialogRef.addPanelClass('handset');
    }

    close(): void {
        this.#dialogRef.close();
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
