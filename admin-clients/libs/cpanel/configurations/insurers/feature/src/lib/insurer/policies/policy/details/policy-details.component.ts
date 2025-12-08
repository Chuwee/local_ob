import { InsurersService } from '@admin-clients/cpanel-configurations-insurers-data-access';
import { DialogSize, EphemeralMessageService, MessageDialogConfig, MessageDialogService, NavTabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { NgClass } from '@angular/common';
import { ChangeDetectionStrategy, Component, effect, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatSlideToggle } from '@angular/material/slide-toggle';
import { MatTooltip } from '@angular/material/tooltip';
import { RouterOutlet } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { delay, filter, of, switchMap, tap } from 'rxjs';

const unsavedChangesDialogData: MessageDialogConfig = {
    actionLabel: 'FORMS.ACTIONS.UPDATE',
    showCancelButton: true,
    message: 'CHANNELS.PACKS.STATUS_CHANGE_WARNING.DESCRIPTION',
    title: 'CHANNELS.PACKS.STATUS_CHANGE_WARNING.TITLE',
    size: DialogSize.MEDIUM
};

@Component({
    selector: 'app-policy-details',
    imports: [
        RouterOutlet, NavTabsMenuComponent, MatTooltip, MatSlideToggle, TranslatePipe, ReactiveFormsModule, NgClass
    ],
    templateUrl: './policy-details.component.html',
    styleUrls: ['./policy-details.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PolicyDetailsComponent {
    readonly #insurerSrv = inject(InsurersService);
    readonly #fb = inject(FormBuilder);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #ephemeralMsgSrv = inject(EphemeralMessageService);

    #childComponent = signal<WritingComponent | null>(null);

    readonly statusCtrl = this.#fb.control({ value: null, disabled: true });

    readonly $policy = toSignal(this.#insurerSrv.policy.get$().pipe(
        filter(Boolean),
        tap(policy => {
            this.statusCtrl.patchValue(policy.active);
        })
    ));

    constructor() {
        effect(() => {
            const child = this.#childComponent();

            if (!child) return;

            //StatusCtrl disabled until backend terms-conditions is ready
            /*if (child.form.valid) {
                this.statusCtrl.enable();
            } else {
                this.statusCtrl.disable();
            }*/
        });
    }

    handleStatusChange(isActive: boolean): void {
        if (this.#childComponent()?.form?.dirty) {
            of(null).pipe(
                delay(100),
                tap(() => this.statusCtrl.setValue(!isActive)),
                switchMap(() => this.#msgDialogSrv.showWarn(unsavedChangesDialogData)),
                switchMap(saveAccepted =>
                    saveAccepted ? this.#childComponent().save$() : of(false)
                )
            ).subscribe();
        } else {
            const policy = { active: isActive };
            this.#insurerSrv.policy.update(this.$policy().insurer_id, this.$policy().id, policy).subscribe({
                complete: () => {
                    this.#insurerSrv.policy.load(this.$policy().insurer_id, this.$policy().id);
                    this.#insurerSrv.policiesList.load(this.$policy().insurer_id);
                    const message = isActive ? 'INSURERS.POLICIES.ENABLE_POLICY_SUCCESS' : 'INSURERS.POLICIES.DISABLE_POLICY_SUCCESS';
                    this.#ephemeralMsgSrv.showSuccess({
                        msgKey: message,
                        msgParams: { policyName: this.$policy().name }
                    });
                },

                error: () => this.statusCtrl.patchValue(!isActive)
            });

        }
    }

    childComponentChange(component: WritingComponent): void {
        this.#childComponent.set(component);
    }
}
