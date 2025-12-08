import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import {
    ChannelStatus, channelWebTypes, Channel, PutChannel, ChannelBuild, ChannelsService,
    ChannelsPipesModule, ChannelFieldsRestrictions, IsV3$Pipe
} from '@admin-clients/cpanel/channels/data-access';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { CopyTextComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, Input, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, filter, shareReplay } from 'rxjs';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    selector: 'app-form-channel-data',
    templateUrl: './form-channel-data.component.html',
    styleUrls: ['./form-channel-data.component.scss'],
    imports: [
        CommonModule, MaterialModule, FlexLayoutModule, ReactiveFormsModule, TranslatePipe, FormControlErrorsComponent,
        ChannelsPipesModule, CopyTextComponent, IsV3$Pipe
    ]
})
export class FormChannelDataComponent implements OnInit {
    readonly #destroyRef = inject(DestroyRef);
    readonly #auth = inject(AuthenticationService);
    readonly #channelService = inject(ChannelsService);
    #channel: Channel;

    readonly channelDataForm = inject(FormBuilder).nonNullable.group({
        name: [null as string, [
            Validators.required,
            Validators.maxLength(ChannelFieldsRestrictions.channelNameLength)
        ]],
        status: [null as ChannelStatus, [Validators.required]],
        build: [{ value: null as ChannelBuild, disabled: true }],
        domain: [{ value: null as string, disabled: true }, [
            Validators.required,
            Validators.pattern(ChannelFieldsRestrictions.channelUrlPattern)
        ]]
    });

    @Input() form: FormGroup;
    @Input() putChannelCtrl: FormControl<PutChannel>;
    @Input() set channel(channel: Channel) {
        this.#channel = channel;
        this.channelDataForm.reset({
            name: channel.name,
            status: channel.status,
            build: channel.build,
            domain: channel.domain
        }, { emitEvent: false });
    }

    get channel(): Channel {
        return this.#channel;
    }

    readonly channelBuilds = ChannelBuild;
    readonly channelStatus = ChannelStatus;
    readonly channelFieldsRestrictions = ChannelFieldsRestrictions;
    readonly operatorMode$ = this.#auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.OPR_ANS])
        .pipe(shareReplay({ bufferSize: 1, refCount: true }));

    ngOnInit(): void {
        this.form.setControl('channelData', this.channelDataForm, { emitEvent: false });

        combineLatest([
            this.#channelService.getChannel$(),
            this.operatorMode$
        ])
            .pipe(
                takeUntilDestroyed(this.#destroyRef),
                filter(([channel, isOperator]) => channel !== null && isOperator !== null)
            )
            .subscribe(([channel, isOperator]) => {
                if (isOperator && channelWebTypes.includes(channel.type)) {
                    this.channelDataForm.controls.build.enable({ emitEvent: false });
                }
                if (channel.whitelabel_type === 'EXTERNAL') {
                    this.channelDataForm.controls.domain.enable({ emitEvent: false });
                }
            });

        this.putChannelCtrl.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(putChannel => {
                if (!this.form.valid) return;

                const { name, status, build, domain } = this.channelDataForm.controls;
                if (name.dirty || status.dirty || build.dirty || domain.dirty) {
                    if (name.dirty) putChannel.name = name.value;
                    if (status.dirty) putChannel.status = status.value;
                    if (build.dirty) putChannel.build = build.value;
                    if (domain.dirty) putChannel.domain = domain.value;

                    this.putChannelCtrl.setValue(putChannel, { emitEvent: false });
                }
            });
    }

}
