import { MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { coerceBooleanProperty } from '@angular/cdk/coercion';
import { ChangeDetectionStrategy, Component, computed, inject, input, model, output } from '@angular/core';

@Component({
    template: '',
    styles: [],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export abstract class BaseFormContainerComponent {
    readonly #msgSrv = inject(MessageDialogService);

    // To use when the granularity of disabled is not enough, and the need to differentiate between disabling save is different
    // from disabling cancel
    readonly $saveDisabled = model(true, { alias: 'saveDisabled' });

    // To use when the granularity of disabled is not enough, and the need to differentiate between disabling cancel is different
    // from disabling save
    readonly $cancelDisabled = model(true, { alias: 'cancelDisabled' });

    // To use when disabled is enough granular to disable the buttons save and cancel at the same time
    readonly $disabled = input<boolean, boolean>(true, {
        alias: 'disabled', transform: disabled => {
            this.$cancelDisabled.set(disabled);
            this.$saveDisabled.set(disabled);
            return disabled;
        }
    });

    readonly $getDisabled = computed(() => this.$saveDisabled() && this.$cancelDisabled());

    // To hide the bottom action bar
    readonly $hideActionBar = input(false, { alias: 'hideActionBar', transform: val => coerceBooleanProperty(val) });

    readonly cancel = output<void>();
    readonly save = output<void>();

    protected constructor() { }

    discard(): void {
        this.#msgSrv.defaultDiscardChangesWarn().subscribe(() => this.cancel.emit());
    }

}
