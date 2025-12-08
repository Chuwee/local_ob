import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import {
    ChannelAfterPromotionPipe, ChannelsService, ChannelSurcharge, ChannelType, IsWebChannelPipe, PutChannel
} from '@admin-clients/cpanel/channels/data-access';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit, viewChild, viewChildren } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatExpansionModule, MatExpansionPanel } from '@angular/material/expansion';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatTooltip } from '@angular/material/tooltip';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, forkJoin, Observable, Subject, switchMap, throwError } from 'rxjs';
import { first, tap } from 'rxjs/operators';
import { ChannelOperativeService } from '../channel-operative.service';
import { ChannelSurchargesGenericComponent } from './generic/channel-surcharges-generic.component';
import { ChannelSurchargesInvitationComponent } from './invitation/channel-surcharges-invitation.component';
import { ChannelSurchargesPromotionComponent } from './promotion/channel-surcharges-promotion.component';
import { ChannelSurchargesSettingsComponent } from './settings/channel-surcharges-settings.component';
import { ChannelSurchargesTaxesComponent } from './taxes/channel-surcharges-taxes.component';

@Component({
    selector: 'app-channel-surcharges',
    templateUrl: './channel-surcharges.component.html',
    styleUrls: ['./channel-surcharges.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        AsyncPipe, IsWebChannelPipe, ChannelAfterPromotionPipe, ReactiveFormsModule, MatExpansionModule, MatProgressSpinner,
        TranslatePipe, FormContainerComponent, ChannelSurchargesGenericComponent, ChannelSurchargesPromotionComponent,
        ChannelSurchargesInvitationComponent, ChannelSurchargesSettingsComponent, MatCheckbox, MatIcon, MatTooltip,
        ChannelSurchargesTaxesComponent
    ]
})
export class ChannelSurchargesComponent implements OnInit, OnDestroy, WritingComponent {
    readonly #channelsSrv = inject(ChannelsService);
    readonly #channelOperativeSrv = inject(ChannelOperativeService);
    readonly #entitiesBaseSrv = inject(EntitiesBaseService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #fb = inject(FormBuilder);
    readonly #router = inject(Router);
    readonly #route = inject(ActivatedRoute);
    readonly #onDestroy = new Subject<void>();

    private readonly _$matExpansionPanelQueryList = viewChildren(MatExpansionPanel);
    private readonly _$taxesComponent = viewChild(ChannelSurchargesTaxesComponent);

    readonly surchargesRequestCtrl = this.#fb.nonNullable.control<ChannelSurcharge[]>([]);
    readonly putChannelRequestCtrl = this.#fb.nonNullable.control<PutChannel>({});

    readonly form = this.#fb.nonNullable.group({});
    readonly enabledPromotionRangesCtrl = this.#fb.nonNullable.control(false);

    readonly currencySelectedTabBS = new BehaviorSubject('');
    readonly $userCanWrite = toSignal(inject(AuthenticationService).hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.CNL_MGR]));
    readonly channel$ = this.#channelsSrv.getChannel$();

    readonly isInProgress$ = booleanOrMerge([
        this.#channelOperativeSrv.isChannelSurchargesLoading$(),
        this.#channelOperativeSrv.isChannelSurchargesSaving$(),
        this.#channelOperativeSrv.surchargeTaxes.loading$(),
        this.#entitiesBaseSrv.isEntityTaxesLoading()
    ]);

    ngOnInit(): void {
        this.#channelsSrv.getChannel$()
            .pipe(first(Boolean))
            .subscribe(channel => {
                if (channel.type === ChannelType.members) {
                    this.#router.navigate(['../member-surcharges'], { relativeTo: this.#route });
                } else {
                    this.#channelOperativeSrv.loadChannelSurcharges(channel.id.toString());
                    this.#channelOperativeSrv.surchargeTaxes.load(channel.id);
                }
            });
    }

    ngOnDestroy(): void {
        this.#onDestroy.next(null);
        this.#onDestroy.complete();
        this.#channelOperativeSrv.clearChannelSurcharges();
        this.#channelOperativeSrv.surchargeTaxes.clear();
    }

    cancel(): void {
        this.#reloadModels();
    }

    save(): void {
        this.save$().subscribe(() => this.#reloadModels());
    }

    save$(): Observable<unknown> {
        this.surchargesRequestCtrl.setValue([]);
        this.putChannelRequestCtrl.setValue({});
        if (this.form.valid) {
            return this.#channelsSrv.getChannel$()
                .pipe(
                    first(),
                    switchMap(channel => {
                        const requests: Observable<unknown>[] = [];
                        if (this.surchargesRequestCtrl.value.length) {
                            requests.push(
                                this.#channelOperativeSrv.saveChannelSurcharges(
                                    channel.id.toString(),
                                    this.surchargesRequestCtrl.value
                                )
                            );
                        }
                        if (Object.keys(this.putChannelRequestCtrl.value).length) {
                            requests.push(this.#channelsSrv.saveChannel(channel.id, this.putChannelRequestCtrl.value));
                        }

                        const surchargeTaxesReq = this._$taxesComponent()?.getRequest(channel.id);
                        if (surchargeTaxesReq) { requests.push(surchargeTaxesReq); };

                        return forkJoin(requests).pipe(tap(() => this.#ephemeralSrv.showSuccess({
                            msgKey: 'CHANNELS.UPDATE_SUCCESS',
                            msgParams: {
                                channelName: channel.name
                            }
                        })));
                    })
                );
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._$matExpansionPanelQueryList());
            return throwError(() => 'invalid form');
        }
    }

    #reloadModels(): void {
        this.#channelsSrv.getChannel$()
            .pipe(first())
            .subscribe(channel => {
                this.form.markAsPristine();
                this.form.markAsUntouched();
                this.#channelOperativeSrv.loadChannelSurcharges(channel.id.toString());
                this.surchargesRequestCtrl.reset([], { emitEvent: false });
                this.#channelsSrv.loadChannel(channel.id.toString());
                this.putChannelRequestCtrl.reset({}, { emitEvent: false });
                this.#channelOperativeSrv.surchargeTaxes.load(channel.id);
            });
    }
}
