import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ChannelsService, ChannelCommission } from '@admin-clients/cpanel/channels/data-access';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit, QueryList, ViewChildren } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatExpansionPanel, MatExpansionModule } from '@angular/material/expansion';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, Observable, switchMap, throwError } from 'rxjs';
import { filter, first, tap } from 'rxjs/operators';
import { ChannelOperativeService } from '../channel-operative.service';
import { ChannelCommissionsGenericComponent } from './generic/channel-commissions-generic.component';
import { ChannelCommissionsPromotionComponent } from './promotion/channel-commissions-promotion.component';

@Component({
    selector: 'app-channel-commissions',
    templateUrl: './channel-commissions.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        AsyncPipe, ReactiveFormsModule, MatExpansionModule, TranslatePipe, FormContainerComponent,
        ChannelCommissionsGenericComponent, ChannelCommissionsPromotionComponent, MatProgressSpinner, MatCheckbox
    ]
})
export class ChannelCommissionsComponent implements OnInit, OnDestroy, WritingComponent {
    readonly #channelsSrv = inject(ChannelsService);
    readonly #channelOperativeSrv = inject(ChannelOperativeService);
    readonly #ephemeralMessageSrv = inject(EphemeralMessageService);
    readonly #fb = inject(FormBuilder);

    @ViewChildren(MatExpansionPanel) private readonly _matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    readonly commissionsRequestCtrl = this.#fb.nonNullable.control([] as ChannelCommission[]);
    readonly form = this.#fb.nonNullable.group({});
    readonly enabledPromotionRangesCtrl = this.#fb.nonNullable.control(false);
    readonly currencySelectedTabBS = new BehaviorSubject('');

    readonly isInProgress$ = booleanOrMerge([
        this.#channelOperativeSrv.isChannelCommissionsLoading$(),
        this.#channelOperativeSrv.isChannelCommissionsSaving$()
    ]);

    readonly $userCanWrite = toSignal(inject(AuthenticationService).hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.CNL_MGR]));

    ngOnInit(): void {
        this.#channelsSrv.getChannel$()
            .pipe(filter(value => value !== null), first())
            .subscribe(channel => {
                this.#channelOperativeSrv.loadChannelCommissions(channel.id.toString());
            });
    }

    ngOnDestroy(): void {
        this.#channelOperativeSrv.clearChannelCommissions();
    }

    cancel(): void {
        this.#reloadModels();
    }

    save(): void {
        this.save$().subscribe(() => this.#reloadModels());
    }

    save$(): Observable<unknown> {
        this.commissionsRequestCtrl.setValue([]);
        if (this.form.valid) {
            return this.#channelsSrv.getChannel$()
                .pipe(
                    first(),
                    switchMap(channel =>
                        this.#channelOperativeSrv.saveChannelCommissions(channel.id.toString(), this.commissionsRequestCtrl.value)
                            .pipe(tap(() => this.#ephemeralMessageSrv.showSuccess(
                                {
                                    msgKey: 'CHANNELS.UPDATE_SUCCESS',
                                    msgParams: { channelName: channel.name }
                                })
                            ))
                    )
                );
        } else {
            this.form.markAllAsTouched();
            this.form.setValue(this.form.getRawValue());
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanelQueryList);
            return throwError(() => 'invalid form');
        }
    }

    #reloadModels(): void {
        this.#channelsSrv.getChannel$()
            .pipe(first())
            .subscribe(channel => {
                this.form.markAsPristine();
                this.form.markAsUntouched();
                this.commissionsRequestCtrl.reset([], { emitEvent: false });
                this.#channelOperativeSrv.loadChannelCommissions(channel.id.toString());
            });
    }
}
