import {
    ChannelGatewayConfig, ChannelsService, ChannelsExtendedService, ChannelGatewayConfigRequest
} from '@admin-clients/cpanel/channels/data-access';
import { EntityGateway, GatewaysService } from '@admin-clients/shared/common/data-access';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder } from '@angular/forms';
import { MatIconButton } from '@angular/material/button';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { switchMap } from 'rxjs';
import { first } from 'rxjs/operators';
import { ChannelGatewayConfigDescriptionComponent } from './description/channel-gateway-config-description.component';
import { ChannelGatewayConfigSelectionComponent } from './selection/channel-gateway-config-selection.component';
import { ChannelGatewayConfigDialogStepsComponent } from './steps/channel-gateway-config-dialog-steps.component';
import { ChannelGatewayConfigSurchargesComponent } from './surcharges/channel-gateway-config-surcharges.component';

export type ChannelGatewayConfigDialogInput = ChannelGatewayConfig;
export type ChannelGatewayConfigDialogOutput = boolean;
export type ChannelGatewayCtrlType = { entityGateway?: EntityGateway; request: ChannelGatewayConfigRequest };

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        TranslatePipe, ChannelGatewayConfigSelectionComponent, ChannelGatewayConfigDescriptionComponent,
        ChannelGatewayConfigDialogStepsComponent, ChannelGatewayConfigSurchargesComponent, MatProgressSpinner, MatIcon, AsyncPipe,
        MatDialogModule, MatIconButton
    ],
    selector: 'app-channel-gateway-config-dialog',
    templateUrl: './channel-gateway-config-dialog.component.html'
})
export class ChannelGatewayConfigDialogComponent implements OnInit, OnDestroy {
    readonly #gatewaysSrv = inject(GatewaysService);
    readonly #channelsSrv = inject(ChannelsService);
    readonly #channelsExtSrv = inject(ChannelsExtendedService);
    readonly #translate = inject(TranslateService);
    readonly #fb = inject(FormBuilder);
    readonly #dialogRef = inject<
        MatDialogRef<ChannelGatewayConfigDialogComponent,
            ChannelGatewayConfigDialogOutput>>(MatDialogRef);

    readonly $isInProgress = toSignal(this.#channelsExtSrv.gatewayConfiguration.loading$());
    readonly channelGatewayConfig = inject<ChannelGatewayConfigDialogInput>(MAT_DIALOG_DATA);
    readonly form = this.#fb.nonNullable.group({});
    readonly steps = [
        {
            title: 'CHANNELS.PAYMENT_METHODS.SELECTION_STEP',
            nextText: this.#translate.instant('FORMS.ACTIONS.NEXT'),
            requestCtrl: this.#fb.nonNullable.control({ request: {} } as ChannelGatewayCtrlType)
        },
        {
            title: 'CHANNELS.PAYMENT_METHODS.SURCHARGES_STEP',
            nextText: this.#translate.instant('FORMS.ACTIONS.NEXT'),
            requestCtrl: this.#fb.nonNullable.control({ request: {} } as ChannelGatewayCtrlType)
        },
        {
            title: 'CHANNELS.PAYMENT_METHODS.DESCRIPTION_STEP',
            nextText: this.channelGatewayConfig ?
                this.#translate.instant('FORMS.ACTIONS.UPDATE') :
                this.#translate.instant('FORMS.ACTIONS.CREATE'),
            requestCtrl: this.#fb.nonNullable.control({ request: {} } as ChannelGatewayCtrlType)
        }
    ];

    async ngOnInit(): Promise<void> {
        this.#dialogRef.addPanelClass(DialogSize.MEDIUM);

        if (this.channelGatewayConfig) {
            this.#gatewaysSrv.gateway.clear();
            this.#gatewaysSrv.gateway.load(this.channelGatewayConfig.gateway_sid);
        }
    }

    ngOnDestroy(): void {
        this.#gatewaysSrv.gateway.clear();
    }

    stepHandler(step: number): void {
        this.steps[step].requestCtrl.setValue({ request: {} as ChannelGatewayConfigRequest });
    }

    close(): void {
        this.#dialogRef.close();
    }

    save(): void {
        const {
            entityGateway,
            request: firstStepRequest
        } = this.steps[0].requestCtrl.value;
        const { request: secondStepRequest } = this.steps[1].requestCtrl.value;
        // TODO: Delete '?' check  and '|| {}' when payment method surcharges are implemented
        const { request: thirdStepRequest } = this.steps[2]?.requestCtrl.value || {};

        if (secondStepRequest.surcharges?.[0]?.type === 'NONE') {
            secondStepRequest.surcharges = [];
        }

        if (this.channelGatewayConfig) {
            this.#channelsSrv.getChannel$()
                .pipe(
                    first(),
                    switchMap(channel => this.#channelsExtSrv.gatewayConfiguration.save(
                        channel.id.toString(),
                        entityGateway.gateway_sid,
                        this.channelGatewayConfig.configuration_sid,
                        { ...firstStepRequest, ...secondStepRequest, ...thirdStepRequest }
                    ))
                )
                .subscribe(() => {
                    this.#dialogRef.close(true);
                });
        } else {
            this.#channelsSrv.getChannel$()
                .pipe(
                    first(),
                    switchMap(channel => this.#channelsExtSrv.gatewayConfiguration.create(
                        channel.id.toString(),
                        entityGateway.gateway_sid,
                        { ...firstStepRequest, ...secondStepRequest, ...thirdStepRequest }
                    ))
                )
                .subscribe(() => {
                    this.#dialogRef.close(true);
                });
        }
    }
}
