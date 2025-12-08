import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import {
    ChannelsService,
    PutChannelDeliverySettings
} from '@admin-clients/cpanel/channels/data-access';
import { EphemeralMessageService }
    from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit, inject, DestroyRef, OnDestroy } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import {
    FormBuilder,
    ReactiveFormsModule
} from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, throwError } from 'rxjs';
import {
    filter, first, map, shareReplay,
    switchMap, tap
} from 'rxjs/operators';
import { ChannelOperativeService } from '../channel-operative.service';

@Component({
    selector: 'app-channel-members-delivery-methods',
    templateUrl: './channel-members-delivery-methods.component.html',
    styleUrls: ['./channel-members-delivery-methods.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        AsyncPipe, TranslatePipe, FormContainerComponent, MaterialModule, ReactiveFormsModule, FlexLayoutModule
    ]
})
export class ChannelMembersDeliveryMethodsComponent implements OnInit, OnDestroy, WritingComponent {
    readonly #destroyRef = inject(DestroyRef);
    readonly #channelsSrv = inject(ChannelsService);
    readonly #channelOperativeSrv = inject(ChannelOperativeService);
    readonly #ephemeralMessageSrv = inject(EphemeralMessageService);
    readonly #fb = inject(FormBuilder);

    readonly form = this.#fb.group({
        use_nfc: false,
        passbook: false
    });

    readonly deliverySettings$ = this.#channelOperativeSrv.getChannelDeliveryMethods$()
        .pipe(
            filter(Boolean),
            map(deliverySettings => ({
                use_nfc: deliverySettings.use_nfc,
                passbook: deliverySettings.receipt_ticket_display?.passbook
            })),
            shareReplay({ refCount: true, bufferSize: 1 })
        );

    readonly inProgress$ = booleanOrMerge([
        this.#channelOperativeSrv.isChannelDeliveryMethodsInProgress$(),
        this.#channelsSrv.isChannelsListLoading$()
    ]);

    ngOnInit(): void {
        this.#channelsSrv.getChannel$()
            .pipe(first(Boolean))
            .subscribe(channel => {
                this.#channelOperativeSrv.loadChannelDeliveryMethods(channel.id.toString());
            });

        this.deliverySettings$
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(deliverySettings => {
                this.form.patchValue(deliverySettings);
                this.form.markAsPristine();
            });
    }

    ngOnDestroy(): void {
        this.#channelOperativeSrv.clearChannelDeliveryMethods();
        this.#channelsSrv.channelsList.clear();
    }

    save$(): Observable<void> {
        if (this.form.valid && this.form.dirty) {
            return this.#channelsSrv.getChannel$()
                .pipe(
                    first(),
                    switchMap(channel =>
                        this.#channelOperativeSrv.updateChannelDeliveryMethods({
                            channelId: channel.id.toString(),
                            deliveryMethods: this.#getDeliverySettings()
                        }).pipe(
                            tap(() => this.#ephemeralMessageSrv.showSuccess({
                                msgKey: 'CHANNELS.UPDATE_SUCCESS',
                                msgParams: { channelName: channel.name }
                            }))
                        ))

                );
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return throwError(() => 'invalid form');
        }
    }

    save(): void {
        this.save$().subscribe(() => this.#reloadModels());
    }

    cancel(): void {
        this.#reloadModels();
    }

    #getDeliverySettings(): PutChannelDeliverySettings {
        const { use_nfc: useNfc, passbook } = this.form.getRawValue();
        return {
            receipt_ticket_display: {
                passbook
            },
            use_nfc: useNfc
        };
    }

    #reloadModels(): void {
        this.#channelsSrv.getChannel$()
            .pipe(first())
            .subscribe(channel => {
                this.#channelOperativeSrv.loadChannelDeliveryMethods(channel.id.toString());
                this.#channelsSrv.loadChannel(channel.id.toString());
                this.form.markAsPristine();
            });
    }

}
