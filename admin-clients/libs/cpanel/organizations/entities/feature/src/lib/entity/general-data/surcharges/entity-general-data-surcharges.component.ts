import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { AuthenticationService, isMultiCurrency$ } from '@admin-clients/cpanel/core/data-access';
import { EntitiesService } from '@admin-clients/cpanel/organizations/entities/data-access';
import { EntitySurchargeType, EntitySurcharge } from '@admin-clients/shared/common/data-access';
import {
    EphemeralMessageService, RangeCurrencyInputPipe, RangeTableComponent, TabDirective, TabsMenuComponent
} from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { LocalCurrencyPartialTranslationPipe } from '@admin-clients/shared/utility/pipes';
import { RangeElement } from '@admin-clients/shared-utility-models';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, ViewChild, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder } from '@angular/forms';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import {
    BehaviorSubject, filter, first, map, Observable, of, shareReplay,
    Subject, switchMap, takeUntil, tap, throwError
} from 'rxjs';
import { EntitySurchargesRangesComponent } from './ranges/entity-surcharges-ranges.component';

@Component({
    selector: 'app-entity-general-data-surcharges',
    templateUrl: './entity-general-data-surcharges.component.html',
    styleUrls: ['./entity-general-data-surcharges.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        TabsMenuComponent, FormContainerComponent, TranslatePipe, FlexLayoutModule,
        EntitySurchargesRangesComponent, RangeCurrencyInputPipe, AsyncPipe, RangeTableComponent,
        MatProgressSpinner, TabDirective, LocalCurrencyPartialTranslationPipe, FlexLayoutModule
    ]
})
export class EntitySurchargesComponent implements OnInit, OnDestroy {
    private readonly _entitiesSrv = inject(EntitiesService);
    private readonly _authSrv = inject(AuthenticationService);
    private readonly _fb = inject(FormBuilder);
    private readonly _ephemeralMessage = inject(EphemeralMessageService);
    private readonly _onDestroy = new Subject<void>();

    @ViewChild(TabsMenuComponent) private readonly _tabsMenuComponent: TabsMenuComponent;
    @ViewChild('secondaryMarketTabs') private readonly _tabsMenuSMComponent: TabsMenuComponent;

    private _entityId: number;

    readonly form = this._fb.nonNullable.group({
        [EntitySurchargeType.generic]: this._fb.nonNullable.group({ ranges: [null as RangeElement[]] }),
        [EntitySurchargeType.secondaryMarketPromoter]: this._fb.nonNullable.group({ ranges: [null as RangeElement[]] })
    });

    readonly isMultiCurrency$ = isMultiCurrency$().pipe(first());
    readonly hasSecondaryMarket$ = this._entitiesSrv.getEntity$()
        .pipe(first(Boolean), map(entity => entity?.settings?.allow_secondary_market));

    readonly surcharges$ = this._entitiesSrv.surcharges.get$()
        .pipe(
            filter(Boolean),
            takeUntil(this._onDestroy),
            shareReplay({ bufferSize: 1, refCount: true })
        );

    readonly surchargesData$ = this.surcharges$.pipe(
        map(surcharges => {
            const mappedSurcharges = {};
            surcharges.forEach(surcharge => mappedSurcharges[surcharge.type] = surcharge);
            return mappedSurcharges;
        })
    );

    readonly entityCurrencies$ = this._authSrv.getLoggedUser$()
        .pipe(
            first(Boolean),
            map(user => user.operator.currencies.selected)
        );

    readonly reqInProgress$ = this._entitiesSrv.surcharges.inProgress$();

    readonly currencySelectedTabBS = new BehaviorSubject('');
    readonly currencySelectedTabSMBS = new BehaviorSubject('');
    readonly entitySurchargesTypes = EntitySurchargeType;

    readonly surchargesRequestCtrl = this._fb.group({
        [EntitySurchargeType.generic]: this._fb.nonNullable.control([] as RangeElement[]),
        [EntitySurchargeType.secondaryMarketPromoter]: this._fb.nonNullable.control([] as RangeElement[])
    });

    readonly errorCtrl = this._fb.group({
        [EntitySurchargeType.generic]: this._fb.nonNullable.control(''),
        [EntitySurchargeType.secondaryMarketPromoter]: this._fb.nonNullable.control('')
    });

    readonly multiCurrencyData$ = Object.values(EntitySurchargeType).map(type => ({
        type,
        observable: isMultiCurrency$().pipe(
            first(),
            switchMap(isMultiCurrency => {
                if (!isMultiCurrency) return of(new Map());

                return this.surcharges$
                    .pipe(
                        filter(Boolean),
                        map(surcharges => {
                            const rangesMap = new Map<string, RangeElement[]>();
                            const surcharge = surcharges.find(surcharge => surcharge.type === type);
                            if (surcharge) {
                                surcharge.ranges.forEach(range => {
                                    const ranges = rangesMap.get(range.currency_code);
                                    if (ranges) {
                                        ranges.push(range);
                                    } else {
                                        rangesMap.set(range.currency_code, [range]);
                                    }
                                });
                                this.form.controls[type].controls.ranges.setValue(Object.values(rangesMap));
                            }
                            return rangesMap;
                        })
                    );
            }))
    })).reduce((acc, surcharge) => {
        acc[surcharge.type] = surcharge.observable;
        return acc;
    }, {});

    ngOnInit(): void {
        this._entitiesSrv.getEntity$()
            .pipe(first(Boolean))
            .subscribe(entity => {
                this._entityId = entity.id;
                this._entitiesSrv.surcharges.load(entity.id);
            });

        Object.values(EntitySurchargeType).map(value => this.surchargesRequestCtrl.controls[value].valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(() => {
                this.errorCtrl.controls[value].reset('', { emitEvent: false });
            }));

        Object.values(EntitySurchargeType).map(value => this.errorCtrl.controls[value].valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(error => {
                if (value === EntitySurchargeType.generic && error) this._tabsMenuComponent.goToKeyTab(error);
                if (value === EntitySurchargeType.secondaryMarketPromoter && error) this._tabsMenuSMComponent.goToKeyTab(error);
            }));

    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._entitiesSrv.surcharges.clear();
    }

    reload(): void {
        this._entitiesSrv.surcharges.load(this._entityId);
        this.form.markAsPristine();
    }

    save(): void {
        this.save$().subscribe(() => {
            this.reload();
        });
    }

    save$(): Observable<any> {
        Object.values(EntitySurchargeType).map(type => this.surchargesRequestCtrl.controls[type].setValue([]));
        const entitySurcharges: EntitySurcharge[] = [];
        const typesWithErrors = [];
        Object.values(EntitySurchargeType).map(type => {
            if (this.form.controls[type].touched) {
                if (this.form.controls[type].valid) {
                    let ranges: RangeElement[];

                    // TODO(MULTICURRENCY): delete when the multicurrency functionality is finished
                    this.isMultiCurrency$.pipe(first())
                        .subscribe(isMultiCurrency => {
                            if (!isMultiCurrency) {
                                ranges = this.form.controls[type].controls.ranges.value;
                            } else {
                                ranges = Object.values(this.surchargesRequestCtrl.controls[type].value);
                            }
                        });

                    entitySurcharges.push({ type, ranges });
                } else {
                    typesWithErrors.push(type);
                }
            }
            if (this.form.controls[type].invalid) {
                typesWithErrors.push(type);
            }
            return null;
        });
        if (typesWithErrors.length) {
            typesWithErrors.map(type => this.form.controls[type].markAllAsTouched);
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return throwError(() => 'invalid form');
        }
        return this._entitiesSrv.surcharges.update(this._entityId, entitySurcharges)
            .pipe(tap(() => this._ephemeralMessage.showSuccess({ msgKey: 'ENTITY.UPDATE_SUCCESS' })));

    }
}
