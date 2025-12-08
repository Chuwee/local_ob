import { BiHistoryReport } from '@admin-clients/cpanel/bi/data-access';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { isHandset$ } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { MatIconButton } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { RouterModule } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { BiSupersetReportsComponent } from '../../../bi-superset-reports.component';

export type BiSubscriptionsRecipientsDialogInput = { biReports: BiHistoryReport[] };

@Component({
    selector: 'app-bi-reports-history-dialog',
    templateUrl: './bi-superset-reports-history-dialog.component.html',
    styleUrls: ['./bi-superset-reports-history-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        AsyncPipe, TranslatePipe, RouterModule, DateTimePipe, EllipsifyDirective,
        MatTableModule, MatDialogModule, MatIconButton, MatIcon
    ]
})
export class BiSupersetReportsHistoryDialogComponent extends BiSupersetReportsComponent {
    readonly #dialogRef = inject<MatDialogRef<BiSupersetReportsHistoryDialogComponent, void>>(MatDialogRef);
    readonly biReports = inject<BiSubscriptionsRecipientsDialogInput>(MAT_DIALOG_DATA).biReports;

    readonly pageSize = 10;
    readonly dateTimeFormats = DateTimeFormats;
    readonly columns = ['report', 'category', 'date'];

    readonly isHandset$ = isHandset$();

    constructor() {
        super();
        this.#dialogRef.addPanelClass('handset');
    }

    close(): void {
        this.#dialogRef.close();
    }

}
