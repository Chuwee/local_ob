import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import {
    EventSessionsService, SessionOtherSettingsRestrictions, SessionVirtualQueue
} from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { CopyTextComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, Input, OnDestroy } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, Subject } from 'rxjs';
import { filter, first, map, takeUntil, withLatestFrom } from 'rxjs/operators';

@Component({
    selector: 'app-session-virtual-queues',
    changeDetection: ChangeDetectionStrategy.OnPush,
    styleUrls: ['./session-virtual-queues.component.scss', '../session-other-settings.component.scss'],
    templateUrl: './session-virtual-queues.component.html',
    imports: [
        ReactiveFormsModule, FlexLayoutModule, TranslatePipe, MaterialModule,
        CommonModule, CopyTextComponent
    ]
})
export class SessionVirtualQueuesComponent implements OnDestroy {
    private readonly _authService = inject(AuthenticationService);
    private readonly _sessionsService = inject(EventSessionsService);
    private readonly _onDestroy = new Subject<void>();
    readonly isOperator$ = this._authService.getLoggedUser$()
        .pipe(
            first(user => !!user),
            map(user => AuthenticationService.isSomeRoleInUserRoles(user, [UserRoles.OPR_MGR]))
        );

    readonly sessionOtherSettingsRestrictions = SessionOtherSettingsRestrictions;
    readonly virtualQueueFormGroup = inject(UntypedFormBuilder)
        .group({
            enableVirtualQueue: false,
            virtualQueueAlias: [{ value: null, disabled: true }, [
                Validators.required,
                Validators.maxLength(SessionOtherSettingsRestrictions.virtualQueueMaxLength),
                Validators.pattern(SessionOtherSettingsRestrictions.virtualQueuePattern)
            ]],
            virtualQueueToken: [{ value: null, disabled: true }]
        });

    readonly markForCheck = ((): () => void => {
        const cdr = inject(ChangeDetectorRef);
        return () => cdr.markForCheck();
    })();

    constructor() {
        this._authService.getLoggedUser$()
            .pipe(
                first(user => !!user),
                filter(user => !AuthenticationService.isSomeRoleInUserRoles(user, [UserRoles.OPR_MGR])))
            .subscribe(() => this.virtualQueueFormGroup.get('enableVirtualQueue').disable());
    }

    @Input() set form(value: UntypedFormGroup) {
        if (value.contains('virtualQueues')) {
            return;
        }
        value.addControl('virtualQueues', this.virtualQueueFormGroup, { emitEvent: false });
        this.virtualQueueFormChangeHandler();
        this.updateVirtualQueueForm();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        const form = this.virtualQueueFormGroup.parent as UntypedFormGroup;
        form.removeControl('virtualQueues', { emitEvent: false });
    }

    getValue(): SessionVirtualQueue {
        return this.virtualQueueFormGroup.value.enableVirtualQueue != null ?
            {
                enable: this.virtualQueueFormGroup.value.enableVirtualQueue,
                alias: this.virtualQueueFormGroup.value.virtualQueueAlias
            } : undefined;
    }

    private virtualQueueFormChangeHandler(): void {
        (this.virtualQueueFormGroup.get('enableVirtualQueue').valueChanges as Observable<boolean>)
            .pipe(
                takeUntil(this._onDestroy),
                withLatestFrom(this.isOperator$)
            )
            .subscribe(([isEnabled, isOperator]) => {
                if (isEnabled && isOperator) {
                    this.virtualQueueFormGroup.get('virtualQueueAlias').enable({ emitEvent: false });
                } else {
                    this.virtualQueueFormGroup.get('virtualQueueAlias').disable({ emitEvent: false });
                }
            });
    }

    private updateVirtualQueueForm(): void {
        this._sessionsService.session.get$()
            .pipe(filter(session => !!session))
            .subscribe(session => {
                this.virtualQueueFormGroup.patchValue({
                    enableVirtualQueue: session.settings?.virtual_queue?.enable,
                    virtualQueueAlias: session.settings?.virtual_queue?.alias,
                    virtualQueueToken: session.settings?.virtual_queue?.skip_token
                });
                this.virtualQueueFormGroup.markAsPristine();
                this.virtualQueueFormGroup.markAsUntouched();
            });
    }
}
