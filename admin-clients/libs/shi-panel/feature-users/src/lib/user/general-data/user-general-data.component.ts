import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { AuthenticationService } from '@admin-clients/shi-panel/data-access-auth';
import { User, UserPermissions, UserRoles, UserStatus } from '@admin-clients/shi-panel/utility-models';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, OnInit, QueryList, ViewChildren, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, Validators } from '@angular/forms';
import { MatOptionModule } from '@angular/material/core';
import { MatDividerModule } from '@angular/material/divider';
import { MatExpansionModule, MatExpansionPanel } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, combineLatest, filter, first, skip, switchMap, tap, throwError } from 'rxjs';
import { PutUser } from '../../models/put-user.model';
import { UsersService } from '../../users.service';

@Component({
    imports: [
        CommonModule, TranslatePipe, ReactiveFormsModule, FormControlErrorsComponent, FormContainerComponent,
        FlexLayoutModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatOptionModule, MatDividerModule,
        MatExpansionModule, MatProgressSpinnerModule
    ],
    selector: 'app-user-general-data',
    templateUrl: './user-general-data.component.html',
    styleUrls: ['./user-general-data.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserGeneralDataComponent implements OnInit {

    readonly #destroyRef = inject(DestroyRef);
    readonly #fb = inject(UntypedFormBuilder);
    readonly #usersService = inject(UsersService);
    readonly #auth = inject(AuthenticationService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    #userId: string;

    @ViewChildren(MatExpansionPanel)
    private _matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    readonly form = this.#fb.group({
        userStatus: [null as { id: UserStatus; name: string }, Validators.required],
        generalData: this.#fb.group({
            name: [null as string, [Validators.required]],
            last_name: [null as string, [Validators.required]],
            email: [null as string, [Validators.required, Validators.email]],
            role: [null as UserRoles, Validators.required]
        })
    });

    readonly userRoles = Object.values(UserRoles)
        .map(role => ({ id: role, name: `USERS.ROLE_OPTS.${role}` }));

    readonly userStatus = UserStatus;
    readonly userStatusList = Object.values(UserStatus);

    readonly isLoading$: Observable<boolean> = this.#usersService.userDetailsProvider.loading$();

    readonly user$ = this.#usersService.userDetailsProvider.getUser$()
        .pipe(
            filter(Boolean),
            tap(user => {
                this.updateForm(user);
                this.$canChangeStatus.set(this.canChangeUserStatus(user));
            })
        );

    isOwner: boolean;
    isAdmin: boolean;
    canEditUser: boolean;
    $canChangeStatus = signal(false);

    ngOnInit(): void {
        this.#auth.hasLoggedUserSomeRoles$([UserRoles.owner]).subscribe(owner => this.isOwner = owner);
        this.#auth.hasLoggedUserSomeRoles$([UserRoles.admin]).subscribe(admin => this.isAdmin = admin);

        combineLatest([
            this.#auth.getLoggedUser$(),
            this.#usersService.userDetailsProvider.getUser$()
        ])
            .pipe(
                first(sources => sources.every(s => !!s)),
                takeUntilDestroyed(this.#destroyRef)
            )
            .subscribe(([authUser, user]) => {
                let isSelf = false;
                if (authUser.id === user.id) {
                    isSelf = true;
                    this.#userId = String(authUser.id);
                } else {
                    this.#userId = String(user.id);
                }
                const hasEditPermissions = authUser.permissions.some(permission => permission === UserPermissions.userWrite);
                this.canEditUser =
                    isSelf || hasEditPermissions && (this.isOwner || (user.role === UserRoles.user && authUser.role === UserRoles.admin));
            });
    }

    cancel(): void {
        this.#usersService.userDetailsProvider.loadUser(this.#userId);
    }

    save(): void {
        this.save$().subscribe(() => this.#usersService.userDetailsProvider.loadUser(this.#userId));
    }

    save$(): Observable<User> {
        if (this.form.valid && this.form.dirty) {
            const fv = this.form.value;
            const userChanges: PutUser = {
                status: fv.userStatus,
                name: fv.generalData.name,
                surname: fv.generalData.last_name,
                username: fv.generalData.email,
                role: fv.generalData.role
            };

            return this.#usersService.userDetailsProvider.updateUser(this.#userId, userChanges).pipe(
                switchMap(() => {
                    this.#ephemeralSrv.showSuccess({ msgKey: 'USER.UPDATE_SUCCESS', msgParams: { userEmail: userChanges.username } });
                    this.#usersService.userDetailsProvider.loadUser(this.#userId);
                    return this.#usersService.userDetailsProvider.getUser$().pipe(skip(1), first(Boolean));
                })
            );
        } else {
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanelQueryList);
            return throwError(() => 'invalid form');
        }
    }

    canChangeUserStatus(user: User): boolean {
        if (this.isOwner || (this.isAdmin && (user.role !== UserRoles.owner && user.role !== UserRoles.admin))) {
            return true;
        }
        return false;
    }

    private updateForm(user: User): void {
        this.form.patchValue({
            userStatus: user.status,
            generalData: {
                name: user.name,
                last_name: user.surname,
                email: user.username,
                role: user.role
            }
        });

        this.form.markAsPristine();
        this.form.markAllAsTouched();
    }
}
