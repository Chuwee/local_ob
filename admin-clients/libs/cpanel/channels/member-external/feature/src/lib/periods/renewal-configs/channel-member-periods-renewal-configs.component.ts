import {
    scrollIntoFirstInvalidFieldOrErrorMsg
} from '@OneboxTM/feature-form-control-errors';
import { ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import {
    ChannelMemberExternalService,
    MemberDatesFilter,
    MemberPeriods
} from '@admin-clients/cpanel-channels-member-external-data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, UntypedFormGroup } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { forkJoin, Observable, of, throwError } from 'rxjs';
import { filter, first, tap } from 'rxjs/operators';
import {
    ChannelMemberPeriodsDatesFilterComponent
} from '../dates-filter/channel-member-periods-dates-filter.component';

@Component({
    selector: 'app-channel-member-periods-renewal-configs',
    templateUrl: './channel-member-periods-renewal-configs.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormContainerComponent,
        TranslatePipe,
        ReactiveFormsModule,
        AsyncPipe,
        ChannelMemberPeriodsDatesFilterComponent
    ]
})
export class ChannelMemberExternalPeriodsRenewalConfigsComponent implements OnInit, WritingComponent {
    readonly #onDestroy = inject(DestroyRef);
    readonly #channelsService = inject(ChannelsService);
    readonly #memberExtSrv = inject(ChannelMemberExternalService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #fb = inject(FormBuilder);

    readonly form = this.#fb.group({
        dates_filter: this.#fb.group({
            enabled: false,
            access: this.#fb.group([]),
            default_access: null
        })
    });

    readonly loading$ = this.#memberExtSrv.datesFilter.loading$();

    readonly memberPeriods = MemberPeriods;

    #channelId: number;

    ngOnInit(): void {
        this.#channelsService.getChannel$()
            .pipe(
                first(Boolean),
                tap(channel => {
                    this.#channelId = channel.id;
                    this.load();
                }),
                filter(Boolean),
                takeUntilDestroyed(this.#onDestroy)
            )
            .subscribe();
    }

    save(): void {
        this.save$().subscribe(() => this.load());
    }

    save$(): Observable<void[]> {
        if (this.form.valid && this.form.dirty) {
            const obs$: Observable<void>[] = [of(null)];
            const datesFilter = this.form.value.dates_filter;
            if (datesFilter) {
                const datesFilterRequest = {
                    enabled: datesFilter.enabled,
                    access: Object.values(datesFilter.access),
                    default_access: datesFilter.default_access
                } as MemberDatesFilter;
                obs$.push(this.#memberExtSrv.datesFilter.update(this.#channelId, MemberPeriods.renewal, datesFilterRequest));
            }

            return forkJoin(obs$).pipe(
                tap(() => this.#ephemeralSrv.showSaveSuccess())
            );
        } else {
            this.form.markAllAsTouched();
            if (this.form.value.dates_filter.enabled) {
                this.form.controls.dates_filter.controls.default_access.markAllAsTouched();
            }
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return throwError(() => 'Invalid form');
        }
    }

    cancel(): void {
        this.form.reset();
        const form = this.form.controls.dates_filter.controls.access as UntypedFormGroup;
        Object.keys(this.form.controls.dates_filter.controls.access.controls)
            .forEach(controlKey => form.removeControl(controlKey));
        this.load();
    }

    private load(): void {
        this.#memberExtSrv.datesFilter.load(this.#channelId, MemberPeriods.renewal);
    }
}
