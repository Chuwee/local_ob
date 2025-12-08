import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import {
    Channel,
    PutChannel,
    ChannelFieldsRestrictions,
    DestinationChannelType
} from '@admin-clients/cpanel/channels/data-access';
import { FeverService } from '@admin-clients/cpanel-fever-data-access';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { AsyncPipe, KeyValuePipe } from '@angular/common';
import { Component, Input, ChangeDetectionStrategy, inject, OnInit, OnDestroy, computed } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { Subject, takeUntil, map, startWith, tap } from 'rxjs';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MaterialModule,
        ReactiveFormsModule,
        TranslatePipe,
        FormControlErrorsComponent,
        AsyncPipe,
        KeyValuePipe
    ],
    selector: 'app-destination-channel-form',
    styleUrl: './destination-channel.component.scss',
    templateUrl: './destination-channel.component.html'
})
export class FormDestinationChannelComponent implements OnInit, OnDestroy {
    readonly #onDestroy = new Subject<void>();
    #channel: Channel;
    readonly #feverService = inject(FeverService);

    readonly destinationChannelForm = inject(FormBuilder).nonNullable.group({
        isDestinationChannel: [false],
        destinationChannelType: [null as string],
        destinationChannelName: [null as string]
    });

    readonly destinationChannelTypes = DestinationChannelType;

    destinationChannelNames$ = this.#feverService.destinationChannels.get$().pipe(
        map(channels => channels?.map(x => x.id) ?? [])
    );

    destinations = toSignal(this.#feverService.destinationChannels.get$());
    loadingChannels = toSignal(this.#feverService.destinationChannels.loading$());
    errorChannels = toSignal(this.#feverService.destinationChannels.error$());

    destinationChannelNames = computed(() => this.destinations()?.map(x => x.id) ?? []);

    destinationChannelCurrentName: string;

    @Input() form: FormGroup;
    @Input() putChannelCtrl: FormControl<PutChannel>;
    @Input() set channel(channel: Channel) {
        this.#channel = channel;
        const destinationChannelDictionary = this.#channel.settings?.destination_channel;
        this.destinationChannelCurrentName = destinationChannelDictionary?.destination_channel_id;
        this.destinationChannelForm.reset({
            isDestinationChannel: !!destinationChannelDictionary &&
                !!destinationChannelDictionary.destination_channel_type &&
                !!destinationChannelDictionary.destination_channel_id,
            destinationChannelType: destinationChannelDictionary?.destination_channel_type.toUpperCase(),
            destinationChannelName: destinationChannelDictionary?.destination_channel_id
        }, { emitEvent: false });
    }

    get channel(): Channel {

        return this.#channel;
    }

    channelFieldsRestrictions = ChannelFieldsRestrictions;

    ngOnInit(): void {
        this.form.setControl('destinationChannel', this.destinationChannelForm, { emitEvent: false });

        this.destinationChannelForm.controls.destinationChannelType.valueChanges.pipe(
            startWith(this.destinationChannelForm.controls.destinationChannelType.value),
            takeUntil(this.#onDestroy),
            tap(category => {
                if (category && this.#channel?.entity?.id) {
                    this.#feverService.destinationChannels.load(this.#channel.entity.id,
                        this.destinationChannelForm.controls.destinationChannelType.value.toLowerCase());
                } else {
                    this.#feverService.destinationChannels.clear();
                }
            })
        ).subscribe();

        this.putChannelCtrl.valueChanges
            .pipe(takeUntil(this.#onDestroy))
            .subscribe(putChannel => {
                if (!this.form.valid) return;

                const { isDestinationChannel, destinationChannelName, destinationChannelType } =
                    this.destinationChannelForm.controls;

                const hasChanges = destinationChannelName.dirty || destinationChannelType.dirty || isDestinationChannel.dirty;

                if (hasChanges) {
                    putChannel.settings = putChannel.settings ?? {};
                    putChannel.settings.destination_channel = putChannel.settings.destination_channel ?? {};

                    if (destinationChannelName.dirty) {
                        putChannel.settings.destination_channel.destination_channel_id = destinationChannelName.value;
                    }

                    if (destinationChannelType.dirty) {
                        putChannel.settings.destination_channel.destination_channel_type = destinationChannelType.value?.toLowerCase();
                    }

                    if (isDestinationChannel.dirty && !isDestinationChannel.value) {
                        putChannel.settings.destination_channel.destination_channel_id = '';
                        putChannel.settings.destination_channel.destination_channel_type = '';
                    }

                    this.putChannelCtrl.setValue(putChannel, { emitEvent: false });
                }
            });
    }

    ngOnDestroy(): void {
        this.#onDestroy.next(null);
        this.#onDestroy.complete();
        this.#feverService.destinationChannels.clear();
    }
}
