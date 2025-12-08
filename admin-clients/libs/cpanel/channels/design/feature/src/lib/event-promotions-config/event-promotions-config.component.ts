import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import {
    ChannelPromotionsApplicationConfig, ChannelPromotionsLocations, ChannelsService, PutChannelWhitelabelSettings
} from '@admin-clients/cpanel/channels/data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { atLeastOneRequiredInFormGroup } from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, throwError, filter, tap } from 'rxjs';

@Component({
    selector: 'ob-channel-design-event-promotions-config',
    templateUrl: './event-promotions-config.component.html',
    styleUrls: ['./event-promotions-config.component.scss'],
    imports: [
        FormContainerComponent,
        ReactiveFormsModule, FlexLayoutModule, TranslatePipe,
        MaterialModule, CommonModule, FormControlErrorsComponent
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EventPromotionsConfigComponent implements OnInit {
    readonly #destroyRef = inject(DestroyRef);
    readonly #channelsSrv = inject(ChannelsService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #fb = inject(FormBuilder);

    #channelId: number;

    readonly applicationConfig = ChannelPromotionsApplicationConfig;
    readonly loading$ = this.#channelsSrv.channelWhitelabelSettings.loading$();
    readonly form = this.#fb.group({
        visualization: this.#fb.group({
            integrated: false,
            additionalStep: false
        }, { validators: [atLeastOneRequiredInFormGroup('required')] }),
        applicationConfig: null as ChannelPromotionsApplicationConfig
    });

    ngOnInit(): void {
        this.#channelsSrv.getChannel$()
            .pipe(filter(Boolean))
            .subscribe(channel => {
                this.#channelId = channel.id;
                this.#channelsSrv.channelWhitelabelSettings.load(channel.id);
            });

        this.#channelsSrv.channelWhitelabelSettings.get$()
            .pipe(takeUntilDestroyed(this.#destroyRef), filter(Boolean))
            .subscribe(({ promotions: { locations, application_config: config } }) => {
                this.form.patchValue({
                    visualization: {
                        integrated: locations.includes(ChannelPromotionsLocations.seatSelectionDialog),
                        additionalStep: locations.includes(ChannelPromotionsLocations.seatSelectionRequiredDialog)
                    },
                    applicationConfig: config || ChannelPromotionsApplicationConfig.default
                });
                this.form.markAsPristine();
            });
    }

    save(): void {
        this.save$().subscribe({
            next: () => this.#channelsSrv.channelWhitelabelSettings.load(this.#channelId)
        });
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            const formValue = this.form.value;
            const settings: PutChannelWhitelabelSettings = {
                promotions: {
                    locations: [],
                    application_config: formValue.applicationConfig
                }
            };
            if (formValue.visualization.integrated) {
                settings.promotions.locations.push(ChannelPromotionsLocations.seatSelectionDialog);
            }
            if (formValue.visualization.additionalStep) {
                settings.promotions.locations.push(ChannelPromotionsLocations.seatSelectionRequiredDialog);
            }
            return this.#channelsSrv.channelWhitelabelSettings.update(this.#channelId, settings)
                .pipe(tap(() => this.#ephemeralSrv.showSaveSuccess()));
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return throwError(() => 'invalid form');
        }
    }

    cancel(): void {
        this.#channelsSrv.channelWhitelabelSettings.load(this.#channelId);
    }
}
