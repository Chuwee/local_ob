import { DialogSize, TabDirective, TabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';
import { BuyerLinkComponent } from './buyer-link/change-seat-buyer-link.component';
import { PromoterLinkComponent } from './promoter-link/change-seat-promoter-link.component';

@Component({
    selector: 'app-change-seat-link',
    imports: [
        TabsMenuComponent, TabDirective, PromoterLinkComponent, BuyerLinkComponent,
        TranslatePipe, MatIcon, MatIconButton, MatButton, MatDialogModule
    ],
    templateUrl: './change-seat-link.component.html',
    styleUrls: ['./change-seat-link.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ChangeSeatDialogComponent {
    readonly #dialogRef = inject(MatDialogRef<ChangeSeatDialogComponent>);

    constructor() {
        this.#dialogRef.addPanelClass(DialogSize.MEDIUM);
    }

    close(): void {
        this.#dialogRef.close();
    }
}
