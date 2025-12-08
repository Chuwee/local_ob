import { BiService, BiUser } from '@admin-clients/cpanel/bi/data-access';
import { ChangeDetectionStrategy, Component, inject, viewChild } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogActions, MatDialogContent, MatDialogRef, MatDialogTitle } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';
import { throwError } from 'rxjs';
import { Observable } from 'rxjs/internal/Observable';
import { BiUserCreateFormComponent } from '../form/bi-user-create-form.component';

export interface BiUserCreateDialogData {
    entityId: number;
}

@Component({
    selector: 'app-bi-user-create-dialog',
    templateUrl: './bi-user-create-dialog.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        TranslatePipe, BiUserCreateFormComponent, MatIconButton,
        MatDialogTitle, MatDialogContent, MatDialogActions, MatButton, MatIcon
    ]
})
export class BiUserCreateDialogComponent {
    readonly #dialogRef = inject(MatDialogRef<BiUserCreateDialogComponent, boolean>);
    readonly #biSrv = inject(BiService);
    readonly #data = inject<BiUserCreateDialogData>(MAT_DIALOG_DATA);

    private readonly _formComponent = viewChild(BiUserCreateFormComponent);

    readonly #entityId = this.#data.entityId;
    readonly $loading = toSignal(this.#biSrv.usersList.loading$());

    save(): void {
        this.save$().subscribe(() => this.#dialogRef.close(true));
    }

    save$(): Observable<unknown> {
        const formValue = this._formComponent()?.getFormValue();

        if (formValue) {
            return this.#biSrv.usersList.create({ ...formValue, entity_id: this.#entityId } as BiUser);
        }

        return throwError(() => 'invalid form');
    }

    close(): void {
        this.#dialogRef.close(false);
    }
}
