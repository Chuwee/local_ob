import { EventSessionsService, SessionTicketLimit } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, Input, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, UntypedFormGroup, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { Subject } from 'rxjs';
import { filter, takeUntil } from 'rxjs/operators';

@Component({
    selector: 'app-session-ticket-limit',
    changeDetection: ChangeDetectionStrategy.OnPush,
    styleUrls: ['./session-ticket-limit.component.scss', '../session-other-settings.component.scss'],
    templateUrl: './session-ticket-limit.component.html',
    imports: [
        ReactiveFormsModule, MaterialModule, TranslatePipe
    ]
})
export class SessionTicketLimitComponent implements OnInit, OnDestroy {
    private readonly _sessionsService = inject(EventSessionsService);
    readonly #fb = inject(FormBuilder);
    private readonly _onDestroy = new Subject<void>();

    readonly ticketLimitFormGroup = this.#fb.group({
        enableTicketLimit: false,
        ticketLimit: [{ value: null, disabled: true }, [Validators.required, Validators.min(1)]]
    });

    @Input() set form(value: UntypedFormGroup) {
        if (value.contains('ticketLimit')) {
            return;
        }
        value.addControl('ticketLimit', this.ticketLimitFormGroup, { emitEvent: false });
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
        const form = this.ticketLimitFormGroup.parent as UntypedFormGroup;
        form.removeControl('ticketLimit', { emitEvent: false });
    }

    getValue(): SessionTicketLimit {
        const ticketLimitGroupData = this.ticketLimitFormGroup.getRawValue();
        return {
            tickets: {
                enable: ticketLimitGroupData.enableTicketLimit,
                max: ticketLimitGroupData.ticketLimit
            }
        };
    }

    private ticketLimitFormChangeHandler(): void {
        this.ticketLimitFormGroup.get('enableTicketLimit').valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe((isEnabled: boolean) => {
                if (isEnabled) {
                    this.ticketLimitFormGroup.get('ticketLimit').enable({ emitEvent: false });
                } else {
                    this.ticketLimitFormGroup.get('ticketLimit').disable({ emitEvent: false });
                }
            });
    }

    private updateTicketLimitForm(): void {
        this._sessionsService.session.get$()
            .pipe(
                filter(Boolean)
            )
            .subscribe(session => {
                this.ticketLimitFormGroup.patchValue({
                    enableTicketLimit: !!session.settings?.limits?.tickets?.enable,
                    ticketLimit: session.settings?.limits?.tickets?.max
                }, { onlySelf: true });
                this.ticketLimitFormGroup.markAsPristine();
                this.ticketLimitFormGroup.markAsUntouched();
            });
    }
}
