import { ChannelType } from '@admin-clients/cpanel/channels/data-access';
import { entitiesProviders, EntitiesService } from '@admin-clients/cpanel/organizations/entities/data-access';
import { Rate, RATE_RESTRICTIONS_CHANNELS_SERVICE, RATE_RESTRICTIONS_SERVICE } from '@admin-clients/cpanel/promoters/shared/data-access';
import {
    DialogSize, EmptyStateComponent, EphemeralMessageService, MessageDialogService, ObMatDialogConfig, UnsavedChangesDialogResult
} from '@admin-clients/shared/common/ui/components';
import { PrefixPipe } from '@admin-clients/shared/utility/pipes';
import { VenueTemplate, VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import {
    ChangeDetectionStrategy, Component, computed, inject, input, ViewContainerRef, OnDestroy, OnInit, viewChildren, booleanAttribute,
    DestroyRef
} from '@angular/core';
import { takeUntilDestroyed, toObservable, toSignal } from '@angular/core/rxjs-interop';
import { MatButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIcon } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';
import { catchError, combineLatest, filter, finalize, map, Observable, of, switchMap } from 'rxjs';
import { NewRateRestrictionsDialogComponent } from '../new-rate-restrictions-dialog/new-rate-restrictions-dialog.component';
import { RateRestrictionsDetailComponent } from '../rate-restrictions-detail/rate-restrictions-detail.component';

@Component({
    selector: 'app-restricted-rates-list',
    imports: [
        TranslatePipe, MatButton, MatIcon, MatExpansionModule, RateRestrictionsDetailComponent,
        EmptyStateComponent, PrefixPipe
    ],
    providers: [
        ...entitiesProviders
    ],
    templateUrl: './restricted-rates-list.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class RestrictedRatesListComponent implements OnDestroy, OnInit {
    readonly #onDestroy = inject(DestroyRef);
    readonly #rateRestrictionsSrv = inject(RATE_RESTRICTIONS_SERVICE);
    readonly #rateChannelsSrv = inject(RATE_RESTRICTIONS_CHANNELS_SERVICE);
    readonly #entitiesSrv = inject(EntitiesService);
    readonly #venueTemplatesSrv = inject(VenueTemplatesService);
    readonly #matDialog = inject(MatDialog);
    readonly #viewContainerRef = inject(ViewContainerRef);
    readonly #ephemeralMsgSrv = inject(EphemeralMessageService);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #prefix = inject(PrefixPipe.PREFIX);

    private _$restrictionsForms = viewChildren(RateRestrictionsDetailComponent);

    readonly $contextId = input.required<number>({ alias: 'contextId' });
    readonly $loadCondition = input<number>(null, { alias: 'loadCondition' });
    readonly loadCondition$ = toObservable(this.$loadCondition).pipe(takeUntilDestroyed(this.#onDestroy));

    readonly $rates = input.required<Rate[]>({ alias: 'rates' });
    readonly $venueTemplates = input.required<VenueTemplate[]>({ alias: 'venueTemplates' });
    readonly $entityId = input.required<number>({ alias: 'entityId' });
    readonly $showPeriodRestrictions = input(false, { alias: 'showPeriodRestrictions', transform: booleanAttribute });

    readonly $groupedVenueTemplatePriceTypes = toSignal(this.#venueTemplatesSrv.getGroupedVenueTemplatePriceTypes$());
    readonly $rateRestrictions = toSignal(this.#rateRestrictionsSrv.ratesRestrictions.get$());
    readonly $channelsList = toSignal(this.#rateChannelsSrv.get$().pipe(filter(Boolean),
        map(channels => channels?.filter(channel => [ChannelType.web, ChannelType.webB2B].includes(channel.channel.type)))));

    readonly $customerTypes = toSignal(this.#entitiesSrv.entityCustomerTypes.get$());
    readonly $restrictionsCreationAllowed = computed(() => this.$rateRestrictions()?.length < this.$rates()?.length);
    openedId: number = null;

    ngOnInit(): void {
        this.loadCondition$.subscribe(() => this.#rateRestrictionsSrv.ratesRestrictions.load(this.$contextId()));
        this.#entitiesSrv.entityCustomerTypes.load(this.$entityId());
        this.#rateChannelsSrv.load(this.$contextId());
        if (this.$venueTemplates()?.length) {
            this.#venueTemplatesSrv.loadMultipleVenueTemplatePriceTypes(this.$venueTemplates());
        }
    }

    ngOnDestroy(): void {
        this.#rateRestrictionsSrv.ratesRestrictions.clear();
        this.#entitiesSrv.entityCustomerTypes.clear();
        this.#venueTemplatesSrv.clearGroupedVenueTemplatePriceTypes();
        this.#rateChannelsSrv.clear();
    }

    openNewRateRestrictionsDialog(): void {
        this.#matDialog.open(NewRateRestrictionsDialogComponent, new ObMatDialogConfig({
            ratesRestrictions: this.$rateRestrictions()?.map(rate => rate.rate.id) || [],
            rates: this.$rates(),
            contextId: this.$contextId()
        }, this.#viewContainerRef))
            .beforeClosed()
            .subscribe(createdRateId => {
                if (createdRateId) {
                    this.#ephemeralMsgSrv.showCreateSuccess();
                    this.#rateRestrictionsSrv.ratesRestrictions.load(this.$contextId());
                    this.openedId = createdRateId;
                }
            });
    }

    deleteRateRestrictions(event: Event, rate: { name: string; id: number }): void {
        event.stopPropagation();
        this.#msgDialogSrv
            .showWarn({
                size: DialogSize.SMALL,
                title: this.#prefix + 'RATES_RESTRICTIONS.DELETE.TITLE',
                message: this.#prefix + 'RATES_RESTRICTIONS.DELETE.DESCRIPTION',
                messageParams: { rateName: rate.name },
                actionLabel: 'FORMS.ACTIONS.DELETE',
                showCancelButton: true
            })
            .subscribe(success => {
                if (success) {
                    this.#rateRestrictionsSrv.ratesRestrictions.delete(this.$contextId(), rate.id)
                        .pipe(finalize(() => this.#rateRestrictionsSrv.ratesRestrictions.load(this.$contextId())))
                        .subscribe(() => this.#showSuccess(this.#prefix + 'RATES_RESTRICTIONS.DELETE.SUCCESS', { rateName: rate.name }));
                }
            });
    }

    canDeactivate(): Observable<boolean> {
        if (this._$restrictionsForms()?.some(restriction => restriction.form.dirty)) {
            return this.#msgDialogSrv.openRichUnsavedChangesWarn()
                .pipe(
                    switchMap(result => {
                        if (result === UnsavedChangesDialogResult.continue) {
                            return of(true);
                        } else if (result === UnsavedChangesDialogResult.save) {
                            const restrictionsToSave = this._$restrictionsForms()
                                ?.filter(restriction => restriction.form.dirty)
                                .map(restriction => restriction.save$());

                            return combineLatest([
                                ...restrictionsToSave
                            ]).pipe(
                                switchMap(() => of(true)),
                                catchError(() => of(false))
                            );
                        }
                        return of(false);
                    })
                );
        }
        return of(true);
    }

    #showSuccess(msgKey: string, msgParams?: { [key: string]: string }): void {
        this.#ephemeralMsgSrv.showSuccess({
            msgKey,
            msgParams
        });
    }

}
