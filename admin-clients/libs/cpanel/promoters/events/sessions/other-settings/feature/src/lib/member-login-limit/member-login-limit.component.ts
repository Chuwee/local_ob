import { EventSessionsService, SessionTicketLimit } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, Input, OnDestroy, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { Subject } from 'rxjs';
import { filter, takeUntil } from 'rxjs/operators';

@Component({
    selector: 'app-member-login-limit',
    changeDetection: ChangeDetectionStrategy.OnPush,
    styleUrls: ['./member-login-limit.component.scss'],
    templateUrl: './member-login-limit.component.html',
    imports: [
        MaterialModule, ReactiveFormsModule, CommonModule, FlexLayoutModule,
        TranslatePipe
    ]
})
export class MemberLoginLimitComponent implements OnInit, OnDestroy {
    private readonly _sessionsService = inject(EventSessionsService);
    private readonly _onDestroy = new Subject<void>();

    readonly memberLoginLimitFormGroup = inject(UntypedFormBuilder)
        .group({
            enable: null as boolean,
            max: [{ value: null as number, disabled: true }]
        });

    @Input() set form(value: UntypedFormGroup) {
        if (value.contains('max')) {
            return;
        }
        value.addControl('max', this.memberLoginLimitFormGroup, { emitEvent: false });
    }

    readonly markForCheck = ((): () => void => {
        const cdr = inject(ChangeDetectorRef);
        return () => cdr.markForCheck();
    })();

    ngOnInit(): void {
        this.ticketLimitFormChangeHandler();
        this.updateTicketLimitForm();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        const form = this.memberLoginLimitFormGroup.parent as UntypedFormGroup;
        form.removeControl('max', { emitEvent: false });
    }

    getValue(): SessionTicketLimit {
        return {
            members_logins: {
                enable: this.memberLoginLimitFormGroup.value.enable,
                max: this.memberLoginLimitFormGroup.value.max
            }
        };
    }

    private ticketLimitFormChangeHandler(): void {
        this.memberLoginLimitFormGroup.get('enable').valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe((isEnabled: boolean) => {
                if (isEnabled) {
                    this.memberLoginLimitFormGroup.get('max').enable({ emitEvent: false });
                    this.memberLoginLimitFormGroup.controls['max'].setValidators([Validators.required, Validators.min(1)]);

                } else {
                    this.memberLoginLimitFormGroup.get('max').disable({ emitEvent: false });
                    this.memberLoginLimitFormGroup.controls['max'].setValidators([]);

                }
                this.memberLoginLimitFormGroup.controls['max'].updateValueAndValidity();
            });
    }

    private updateTicketLimitForm(): void {
        this._sessionsService.session.get$().pipe(filter(Boolean)).subscribe(session => {
            this.memberLoginLimitFormGroup.patchValue({
                enable: session.settings.limits.members_logins.enable,
                max: session.settings.limits.members_logins.max
            }, { onlySelf: true });
            this.memberLoginLimitFormGroup.markAsPristine();
            this.memberLoginLimitFormGroup.markAsUntouched();
        });
    }
}
