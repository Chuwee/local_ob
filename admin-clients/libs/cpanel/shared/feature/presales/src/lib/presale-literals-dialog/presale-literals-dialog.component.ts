import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { PrefixPipe } from '@admin-clients/shared/utility/pipes';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatDialogActions, MatDialogContent, MatDialogRef, MatDialogTitle } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-presale-literals-dialog',
    templateUrl: './presale-literals-dialog.component.html',
    styleUrls: ['./presale-literals-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        TranslatePipe, MatIcon, MatDialogTitle, MatDialogContent,
        MatDialogActions, MatButton, MatIconButton, PrefixPipe
    ]
})

export class PresaleLiteralsDialogComponent {

    readonly #dialogRef = inject(MatDialogRef<PresaleLiteralsDialogComponent>);

    constructor() {
        this.#dialogRef.addPanelClass(DialogSize.MEDIUM);
        this.#dialogRef.disableClose = false;
    }

    close(): void {
        this.#dialogRef.close();
    }
}
