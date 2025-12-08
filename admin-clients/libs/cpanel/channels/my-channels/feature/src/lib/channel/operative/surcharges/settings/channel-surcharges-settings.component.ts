import { Channel, PutChannel, SurchargeMode } from '@admin-clients/cpanel/channels/data-access';
import { ChangeDetectionStrategy, Component, DestroyRef, effect, inject, input, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, FormControl, FormGroupDirective, ReactiveFormsModule } from '@angular/forms';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        TranslatePipe, ReactiveFormsModule, MatCheckbox, MatIcon, MatTooltip
    ],
    selector: 'app-channel-surcharges-settings',
    templateUrl: './channel-surcharges-settings.component.html'
})
export class ChannelSurchargesSettingsComponent implements OnInit {
    readonly #formGroup = inject(FormGroupDirective);
    readonly #destroyRef = inject(DestroyRef);
    readonly afterChannelPromotionCtrl = inject(FormBuilder)
        .nonNullable.control({ value: false, disabled: true });

    readonly $channel = input.required<Channel>({ alias: 'channel' });
    readonly $userCanWrite = input.required<boolean>({ alias: 'userCanWrite' });
    readonly $putChannelRequestCtrl = input.required<FormControl<PutChannel>>({ alias: 'putChannelRequestCtrl' });

    constructor() {
        effect(() => {
            if (this.$userCanWrite()) {
                this.afterChannelPromotionCtrl.enable({ emitEvent: false });
            } else {
                this.afterChannelPromotionCtrl.disable({ emitEvent: false });
            }
        });

        effect(() => {
            this.afterChannelPromotionCtrl.reset(
                this.$channel().settings?.surcharges?.calculation === SurchargeMode.afterChannelPromotion,
                { emitEvent: false }
            );
        });
    }

    ngOnInit(): void {
        this.#formGroup.control.addControl('afterChannelPromotion', this.afterChannelPromotionCtrl);
        this.$putChannelRequestCtrl().valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(putChannelRequest => {
                if (this.#formGroup.control.invalid) return;

                if (this.afterChannelPromotionCtrl.dirty) {
                    putChannelRequest.settings = putChannelRequest.settings ?? { surcharges: {} };
                    putChannelRequest.settings.surcharges = putChannelRequest.settings.surcharges ?? {};
                    putChannelRequest.settings.surcharges.calculation = this.afterChannelPromotionCtrl.value ?
                        SurchargeMode.afterChannelPromotion :
                        SurchargeMode.beforeChannelPromotion;
                }
            });
    }
}
