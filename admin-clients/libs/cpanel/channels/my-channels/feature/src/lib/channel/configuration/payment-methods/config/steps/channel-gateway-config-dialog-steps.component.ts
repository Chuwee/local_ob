import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { WizardBarComponent } from '@admin-clients/cpanel/shared/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output, ViewChild } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormGroup } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject } from 'rxjs';
import { ChannelGatewayMapStepsPipe } from './channel-gateway-map-steps.pipe';
import { ChannelGatewayNextTextPipe } from './channel-gateway-next-text.pipe';
import { ChannelGatewaySteps } from './channel-gateway-steps.model';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        CommonModule,
        MaterialModule,
        FlexLayoutModule,
        TranslatePipe,
        WizardBarComponent,
        ChannelGatewayNextTextPipe,
        ChannelGatewayMapStepsPipe
    ],
    selector: 'app-channel-gateway-dialog-steps',
    templateUrl: './channel-gateway-config-dialog-steps.component.html'
})
export class ChannelGatewayConfigDialogStepsComponent {
    @ViewChild(WizardBarComponent) private readonly _wizardBar: WizardBarComponent;

    @Input() form: FormGroup;
    @Input() steps: ChannelGatewaySteps;

    @Output() stepEmitter = new EventEmitter<number>();
    @Output() saveEmitter = new EventEmitter<number>();
    @Output() closeEmitter = new EventEmitter<void>();
    @Output() active$ = new BehaviorSubject<number>(0);

    async nextStep(): Promise<void> {
        const step = this._wizardBar.$active();
        this.stepEmitter.emit(step);
        if (this.form.valid) {
            if (step === this.steps.length - 1) {
                this.saveEmitter.emit(step);
            } else {
                this.setStep(step + 1);
            }
        } else {
            this.form.markAllAsTouched();
            this.form.setValue(this.form.getRawValue());
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
        }
    }

    async previousStep(): Promise<void> {
        const step = this._wizardBar.$active();
        this.stepEmitter.emit(step);
        this.setStep(step - 1);
    }

    private setStep(step: number): void {
        this._wizardBar.setActiveStep(step);
        this.active$.next(step);
    }
}
