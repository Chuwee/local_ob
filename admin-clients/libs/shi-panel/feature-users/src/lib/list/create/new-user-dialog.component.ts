import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { AuthenticationService } from '@admin-clients/shi-panel/data-access-auth';
import { UserRoles } from '@admin-clients/shi-panel/utility-models';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, Subject, map } from 'rxjs';
import { PostUser } from '../../models/post-user.model';
import { UsersService } from '../../users.service';

@Component({
    imports: [CommonModule, MaterialModule, TranslatePipe, ReactiveFormsModule, FormControlErrorsComponent, FlexLayoutModule],
    selector: 'app-new-user-dialog',
    templateUrl: './new-user-dialog.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class NewUserDialogComponent implements OnDestroy {
    private readonly _fb = inject(UntypedFormBuilder);
    private readonly _usersService = inject(UsersService);
    private readonly _authService = inject(AuthenticationService);

    private _onDestroy = new Subject();

    form = this._fb.group({
        name: [null as string, Validators.required],
        email: [null as string, [Validators.required, Validators.email]],
        last_name: [null as string, [Validators.required]],
        role: [null as UserRoles, [Validators.required]]
    });

    readonly userRoles = Object.values(UserRoles)
        .map(role => ({ id: role, name: `USERS.ROLE_OPTS.${role}` }));

    isInProgress$ = this._usersService.usersListProvider.loading$();
    isOwnerUser$: Observable<boolean> = this._authService.getLoggedUser$()
        .pipe(map(loggedUser => AuthenticationService.doesUserHaveSomeRole(loggedUser, [UserRoles.owner])));

    constructor(
        private _dialogRef: MatDialogRef<NewUserDialogComponent, string>
    ) {
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
        this._dialogRef.disableClose = false;
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    createUser(): void {
        if (!this.form.value.role) {
            this.form.get('role').setValue(UserRoles.user);
        }
        if (this.form.valid) {
            const user: PostUser = {
                username: this.form.value.email,
                name: this.form.value.name,
                surname: this.form.value.last_name,
                role: this.form.value.role
            };

            this._usersService.usersListProvider.createUser(user)
                .subscribe(user => this.close(user.id));
        } else {
            this.form.markAllAsTouched();
        }
    }

    close(userId: string): void {
        this._dialogRef.close(userId);
    }
}
