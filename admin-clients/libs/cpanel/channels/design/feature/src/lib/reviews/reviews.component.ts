import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ChannelsService, ReviewConfig, ReviewCriteria, ReviewTimeUnit } from '@admin-clients/cpanel/channels/data-access';
import { EphemeralMessageService, TabDirective, TabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { ChangeDetectionStrategy, Component, effect, inject, OnDestroy } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDivider } from '@angular/material/divider';
import { MatIcon } from '@angular/material/icon';
import { MatSlideToggle } from '@angular/material/slide-toggle';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { first, map, Observable, throwError } from 'rxjs';
import { ChannelReviewConfigComponent } from './review-config/review-config.component';
import { ChannelReviewListComponent } from './review-list/review-list.component';

@Component({
    selector: 'ob-channel-reviews',
    templateUrl: './reviews.component.html',
    styleUrl: './reviews.component.scss',
    imports: [
        MatSlideToggle, MatIcon, MatDivider, FormContainerComponent, TabsMenuComponent, ChannelReviewListComponent,
        TranslatePipe, TabDirective, MatTooltip, ChannelReviewConfigComponent, ReactiveFormsModule
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ChannelReviewsComponent implements OnDestroy {
    readonly #fb = inject(FormBuilder);
    readonly #channelSrv = inject(ChannelsService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);

    readonly #$reviewData = toSignal(this.#channelSrv.reviewConfig.get$().pipe(first(Boolean)));
    readonly $loading = toSignal(this.#channelSrv.reviewConfig.loading$());
    readonly $channelId = toSignal(this.#channelSrv.getChannel$().pipe(first(Boolean), map(channel => channel.id)));

    readonly reviewForm = this.#fb.group({
        enable: false,
        config: this.#fb.group({
            criteria: this.#fb.control<ReviewCriteria>(null),
            time: this.#fb.group({
                unit: this.#fb.control<ReviewTimeUnit>('DAYS'),
                value: this.#fb.control(1, [Validators.required, Validators.min(1)])
            })
        })
    });

    readonly #$enabledReview = toSignal(this.reviewForm.get('enable').valueChanges, { initialValue: false });

    constructor() {
        effect(() => {
            const channelId = this.$channelId();
            if (channelId) {
                this.#channelSrv.reviewConfig.load(channelId);
            }
        });

        effect(() => {
            const data = this.#$reviewData();
            if (data) {
                this.reviewForm.patchValue({
                    enable: data?.enable || false,
                    config: {
                        criteria: data?.send_criteria || 'ALWAYS',
                        time: {
                            unit: data?.send_time_unit || 'DAYS',
                            value: data?.send_time_value || 1
                        }
                    }
                });
            }
        });

        effect(() => {
            if (this.#$enabledReview()) this.reviewForm.get('config').enable();
            else this.reviewForm.get('config').disable();
        });
    }

    ngOnDestroy(): void {
        this.#channelSrv.reviewConfig.clear();
    }

    cancel(): void {
        this.#channelSrv.reviewConfig.load(this.$channelId());
    }

    save$(): Observable<void> {
        this.reviewForm.markAllAsTouched();
        if (this.reviewForm.valid) {
            const { enable, config: { criteria, time } } = this.reviewForm.getRawValue();
            const config: ReviewConfig = { enable };
            if (enable) {
                config.send_criteria = criteria;
                config.send_time_unit = time.unit;
                config.send_time_value = time.value;
            }

            return this.#channelSrv.reviewConfig.update(this.$channelId(), config);
        } else {
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return throwError(() => 'invalid form');
        }
    }

    save(): void {
        this.save$().subscribe(() => {
            this.#ephemeralSrv.showSuccess({ msgKey: 'FORMS.FEEDBACK.SAVE_SUCCESS' });
            this.reviewForm.markAsPristine();
        });
    }
}
