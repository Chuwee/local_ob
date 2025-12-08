
import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { EntityUsersService } from '@admin-clients/cpanel/organizations/entity-users/data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormControl, FormRecord, ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, filter, first, tap, throwError } from 'rxjs';

@Component({
    selector: 'ob-api-management',
    templateUrl: './api-management.component.html',
    styleUrls: ['./api-management.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [CommonModule, MaterialModule, TranslatePipe, FlexLayoutModule, FormContainerComponent, ReactiveFormsModule]
})
export class ApiManagementComponent implements OnInit {
    private readonly _destroyRef = inject(DestroyRef);
    private readonly _fb = inject(FormBuilder);
    private readonly _usersSrv = inject(EntityUsersService);
    private readonly _ephemeralMsgSrv = inject(EphemeralMessageService);

    private _userId: number;

    readonly servers$ = this._usersSrv.userResourceServers.get$().pipe(filter(Boolean));
    readonly loading$ = this._usersSrv.userResourceServers.inProgress$();
    form: FormRecord<FormControl<boolean>>;

    ngOnInit(): void {
        this._usersSrv.getEntityUser$()
            .pipe(first(Boolean))
            .subscribe(user => {
                this._userId = user.id;
                this._usersSrv.userResourceServers.load(user.id);
            });

        this._usersSrv.userResourceServers.get$()
            .pipe(
                filter(Boolean),
                takeUntilDestroyed(this._destroyRef)
            )
            .subscribe(servers => {
                this.form = this._fb.record({});
                servers.forEach(server => this.form.addControl(server.name, this._fb.control(server.enabled)));
                this.form.markAsPristine();
            });
    }

    save(): void {
        this.save$().subscribe(() => this._usersSrv.userResourceServers.load(this._userId));
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            const resources = Object.keys(this.form.value).filter(serverCtrl => this.form.controls[serverCtrl].value);
            return this._usersSrv.userResourceServers.update(this._userId, resources)
                .pipe(tap(() => this._ephemeralMsgSrv.showSaveSuccess()));
        } else {
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            this.form.markAllAsTouched();
            return throwError(() => 'invalid form');
        }
    }

    cancel(): void {
        this._usersSrv.userResourceServers.load(this._userId);
    }
}
