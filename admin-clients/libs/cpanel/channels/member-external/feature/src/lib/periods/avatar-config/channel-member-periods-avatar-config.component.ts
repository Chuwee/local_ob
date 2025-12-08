import { MemberPeriods } from '@admin-clients/cpanel-channels-member-external-data-access';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, input, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { TranslatePipe } from '@ngx-translate/core';

export type AvatarFormGroup = FormGroup<{
    enabled: FormControl<boolean>;
    mandatory: FormControl<boolean | null>;
}>;

@Component({
    selector: 'app-channel-member-periods-avatar-config',
    templateUrl: './channel-member-periods-avatar-config.component.html',
    styleUrls: ['./channel-member-periods-avatar-config.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [ReactiveFormsModule, MatCheckbox, TranslatePipe, MatRadioGroup, MatRadioButton]
})
export class ChannelMemberPeriodsAvatarConfigComponent implements OnInit {
    readonly #onDestroy = inject(DestroyRef);

    readonly $form = input.required<AvatarFormGroup>({ alias: 'form' });
    readonly $period = input.required<MemberPeriods>({ alias: 'period' });

    ngOnInit(): void {
        this.$form().controls.enabled.valueChanges
            .pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe(enabled => {
                if (enabled) {
                    this.$form().controls.mandatory.enable();
                } else {
                    this.$form().controls.mandatory.disable();
                }
            });
    }
}
