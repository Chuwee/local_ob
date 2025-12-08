import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { UpperCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'ob-session-availability-info-dialog',
    imports: [
        TranslatePipe, MatIconModule, MatDialogModule, MatButtonModule, UpperCasePipe
    ],
    templateUrl: './session-availability-info-dialog.component.html',
    styleUrl: './session-availability-info-dialog.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SessionAvailabilityInfoDialogComponent {
    readonly #dialogRef = inject(MatDialogRef);

    readonly availabilities = ['high', 'medium', 'low'];

    constructor() {
        this.#dialogRef.addPanelClass(DialogSize.MEDIUM);
        this.#dialogRef.disableClose = false;
    }

    close(): void {
        this.#dialogRef.close();
    }

}
