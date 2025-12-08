import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import { ChannelMemberExternalService } from '@admin-clients/cpanel-channels-member-external-data-access';
import { EphemeralMessageService, HelpButtonComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { SharedUtilityDirectivesModule } from '@admin-clients/shared/utility/directives';
import { urlValidator } from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, OnDestroy, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { first, switchMap, filter, Observable, throwError, tap } from 'rxjs';

@Component({
    selector: 'app-availability-query',
    imports: [
        CommonModule,
        TranslatePipe,
        FlexLayoutModule,
        MaterialModule,
        FormContainerComponent,
        ReactiveFormsModule,
        HelpButtonComponent,
        SharedUtilityDirectivesModule
    ],
    templateUrl: './availability-query.component.html',
    styleUrls: ['./availability-query.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class AvailabilityQueryComponent implements OnInit, OnDestroy {

    readonly #fb = inject(FormBuilder);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #memberExtSrv = inject(ChannelMemberExternalService);
    readonly #channelsService = inject(ChannelsService);
    readonly #onDestroy = inject(DestroyRef);

    readonly form = this.#fb.group({
        public_availability_enabled: [false],
        internal_link: [true],
        landing_button_url: ['', [Validators.required, urlValidator()]]
    });

    readonly inProgress$ = this.#memberExtSrv.channelOptions.loading$();

    #channelId: number;

    ngOnInit(): void {
        this.#channelsService.getChannel$()
            .pipe(
                first(Boolean),
                switchMap(channel => {
                    this.#channelId = channel.id;
                    this.load();
                    return this.#memberExtSrv.channelOptions.get$();
                }),
                filter(Boolean),
                takeUntilDestroyed(this.#onDestroy)
            )
            .subscribe(settings => {
                this.form.patchValue({
                    ...settings,
                    internal_link: !settings.landing_button_url
                });
                this.form.markAsPristine();
            });

        this.form.controls.internal_link.valueChanges.pipe(takeUntilDestroyed(this.#onDestroy)).subscribe(val => {
            if (val) {
                this.form.controls.landing_button_url.disable();
            } else {
                this.form.controls.landing_button_url.enable();
            }
        });
    }

    ngOnDestroy(): void {
        this.#memberExtSrv.channelOptions.clear();
    }

    cancel(): void {
        this.load();
    }

    save(): void {
        this.save$().subscribe({
            next: () => this.load()
        });
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            const reqBody = {
                ...this.form.value,
                landing_button_url: this.form.value.internal_link ? '' : this.form.value.landing_button_url
            };
            return this.#memberExtSrv.channelOptions.save(this.#channelId, reqBody).pipe(
                tap(() => this.#ephemeralSrv.showSaveSuccess())
            );
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return throwError(() => 'invalid form');
        }
    }

    private load(): void {
        this.#memberExtSrv.channelOptions.load(this.#channelId);
    }

}
