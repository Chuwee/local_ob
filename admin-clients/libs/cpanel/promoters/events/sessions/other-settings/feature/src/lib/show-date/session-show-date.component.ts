import { EventSessionsService, SessionChannelShowDate } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, Input, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, UntypedFormGroup } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { filter } from 'rxjs/operators';

@Component({
    selector: 'app-session-show-date',
    changeDetection: ChangeDetectionStrategy.OnPush,
    templateUrl: './session-show-date.component.html',
    styleUrl: './session-show-date.component.css',
    imports: [MaterialModule, ReactiveFormsModule, TranslatePipe]
})
export class SessionShowDateComponent implements OnInit, OnDestroy {
    readonly #sessionsService = inject(EventSessionsService);
    readonly #fb = inject(FormBuilder);
    readonly showDateFormGroup = this.#fb.group({
        showDate: false,
        showTime: false,
        showProvisionalDate: false
    });

    @Input() set form(value: UntypedFormGroup) {
        if (value.contains('showDate')) {
            return;
        }
        value.addControl('showDate', this.showDateFormGroup, { emitEvent: false });
    }

    readonly markForCheck = ((): () => void => {
        const cdr = inject(ChangeDetectorRef);
        return () => cdr.markForCheck();
    })();

    ngOnInit(): void {
        this.updateShowDateForm();

        this.showDateFormGroup.controls.showDate.valueChanges.subscribe(() => {
            this.showDateFormGroup.controls.showTime.setValue(false);
            this.showDateFormGroup.controls.showProvisionalDate.setValue(false);
        });
    }

    ngOnDestroy(): void {
        const form = this.showDateFormGroup.parent as UntypedFormGroup;
        form.removeControl('showDate', { emitEvent: false });
    }

    getValue(): SessionChannelShowDate {
        this.showDateFormGroup.value.showTime;
        return {
            show_date: this.showDateFormGroup.value.showDate,
            show_time: this.showDateFormGroup.value.showTime,
            show_unconfirmed_date: this.showDateFormGroup.value.showProvisionalDate
        };
    }

    private updateShowDateForm(): void {
        this.#sessionsService.session.get$()
            .pipe(filter(Boolean))
            .subscribe(session => {
                this.showDateFormGroup.patchValue({
                    showDate: !!session.settings?.channels?.show_date,
                    showTime: !!session.settings?.channels?.show_time,
                    showProvisionalDate: !!session.settings?.channels?.show_unconfirmed_date
                }, { onlySelf: true });
                this.showDateFormGroup.markAsPristine();
                this.showDateFormGroup.markAsUntouched();
            });
    }
}
