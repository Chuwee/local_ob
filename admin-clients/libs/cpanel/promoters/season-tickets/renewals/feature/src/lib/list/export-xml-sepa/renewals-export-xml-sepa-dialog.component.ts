import { seasonTicketRenewalsProviders } from '@admin-clients/cpanel/promoters/season-tickets/renewals/data-access';
import { DialogSize, ObDialog } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component } from '@angular/core';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatDialogActions, MatDialogContent, MatDialogTitle } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-renewals-export-xml-sepa-dialog',
    templateUrl: './renewals-export-xml-sepa-dialog.component.html',
    styleUrls: ['./renewals-export-xml-sepa-dialog.component.scss'],
    imports: [
        MatDialogActions, MatDialogTitle, MatDialogContent, MatIcon, MatButton, MatIconButton, TranslatePipe
    ],
    providers: [seasonTicketRenewalsProviders],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class RenewalsExportXMLSepaDialogComponent extends ObDialog<RenewalsExportXMLSepaDialogComponent, null, boolean> {

    constructor() {
        super(DialogSize.MEDIUM);
    }

    close(): void {
        this.dialogRef.close(false);
    }

    export(): void {
        this.dialogRef.close(true);
    }
}