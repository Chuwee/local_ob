import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { channelWebTypes } from '@admin-clients/cpanel/channels/data-access';
import {
    SubscriptionListStatus, SubscriptionListsService, SubscriptionList
} from '@admin-clients/cpanel/viewers/subscriptions/data-access';
import { SaleRequestConfigurationModel, SaleRequest, SalesRequestsService } from '@admin-clients/cpanel-channels-sales-requests-data-access';
import { EntitiesBaseService, EntitiesBaseState, EntityCategory } from '@admin-clients/shared/common/data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, QueryList, ViewChildren, inject } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { forkJoin, Observable, Subject, throwError } from 'rxjs';
import { filter, first, map, shareReplay, takeUntil, tap } from 'rxjs/operators';

@Component({
    selector: 'app-sale-request-configuration',
    templateUrl: './sale-request-configuration.component.html',
    styleUrls: ['./sale-request-configuration.component.scss'],
    providers: [
        EntitiesBaseState,
        EntitiesBaseService
    ],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SaleRequestConfigurationComponent implements OnInit, OnDestroy, WritingComponent {
    private _matExpansionPanelQueryList: QueryList<MatExpansionPanel>;
    private readonly _onDestroy = new Subject<void>();
    private readonly _salesRequestsService = inject(SalesRequestsService);
    private readonly _subscriptionListsService = inject(SubscriptionListsService);
    private readonly _ephemeralMessageService = inject(EphemeralMessageService);
    private readonly _entitiesService = inject(EntitiesBaseService);
    private readonly _fb = inject(UntypedFormBuilder);

    form: UntypedFormGroup;
    saleRequest: SaleRequest;
    configuration$: Observable<SaleRequestConfigurationModel>;
    entityCategories$: Observable<EntityCategory[]>;
    entitySubscriptionLists$: Observable<SubscriptionList[]>;
    loadingOrSaving$: Observable<boolean>;
    isWebType: boolean;

    get refundForm(): UntypedFormGroup {
        return this.form.get('refundForm') as UntypedFormGroup;
    }

    get categoriesDataForm(): UntypedFormGroup {
        return this.form.get('categoriesDataForm') as UntypedFormGroup;
    }

    get customerRelationshipDataForm(): UntypedFormGroup {
        return this.form.get('customerRelationshipDataForm') as UntypedFormGroup;
    }

    @ViewChildren(MatExpansionPanel) matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    ngOnInit(): void {
        this.initForms();

        // used to show spinner and disable save & cancel button
        this.loadingOrSaving$ = booleanOrMerge([
            this._salesRequestsService.isSaleRequestLoading$(),
            this._salesRequestsService.isSaleRequestConfigurationLoading$(),
            this._salesRequestsService.isSaleRequestConfigurationSaving$()
        ]);

        // load saleRequest configuration
        this._salesRequestsService.getSaleRequest$()
            .pipe(
                filter(Boolean),
                takeUntil(this._onDestroy)
            )
            .subscribe(saleRequest => {
                this.isWebType = channelWebTypes.includes(saleRequest.channel.type);
                this._salesRequestsService.loadSaleRequestConfiguration(saleRequest.id);
                this._entitiesService.loadEntityCategories(saleRequest.channel.entity.id);
                this._subscriptionListsService.loadSubscriptionListsList({
                    entityId: saleRequest?.channel?.entity?.id,
                    status: SubscriptionListStatus.active
                });
                this.saleRequest = saleRequest;
            });
        // saleRequest configuration observer
        this.configuration$ = this._salesRequestsService.getSaleRequestConfiguration$()
            .pipe(
                filter(Boolean),
                tap(saleRequestConfiguration => this.updateForms(saleRequestConfiguration))
            );

        this.entityCategories$ = this._entitiesService.getEntityCategories$().pipe(
            first(Boolean),
            map(categories => {
                const filteredCategories = categories.reduce<EntityCategory[]>((result, currentCategory, _, categoriesArray) => {
                    if (currentCategory.parent_id) {
                        const parentCategory = categoriesArray.find(parentCategory => parentCategory.id === currentCategory.parent_id);
                        if (parentCategory) {
                            currentCategory.description = parentCategory.description + ' - ' + currentCategory.description;
                        }
                        result.push(currentCategory);
                    }
                    return result;
                }, []).sort((a, b) => (a.description > b.description) ? 1 : ((b.description > a.description) ? -1 : 0));
                if (!filteredCategories.length) {
                    this.form.get('categoriesDataForm').disable();
                }
                return filteredCategories;
            }),
            shareReplay(1)
        );

        this.entitySubscriptionLists$ = this._subscriptionListsService.getSubscriptionListsListData$().pipe(
            first(Boolean),
            takeUntil(this._onDestroy),
            tap(subscriptions => {
                if (subscriptions.length) {
                    this.customerRelationshipDataForm.get('subscriptionListId').enable();
                }
            }),
            shareReplay(1)
        );
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._salesRequestsService.clearSaleRequestConfiguration();
        this._subscriptionListsService.clearSubscriptionListsList();
    }

    cancel(): void {
        this._salesRequestsService.loadSaleRequest(this.saleRequest.id);
    }

    save(): void {
        this.save$().subscribe(() => {
            this._salesRequestsService.loadSaleRequest(this.saleRequest.id);
        });
    }

    save$(): Observable<void[]> {
        if (this.form.valid) {
            const obs$: Observable<void>[] = [];
            if (this.form.get('refundForm').touched) {
                const allowRefund = !!this.form.get('refundForm.allowedRefund').value;
                obs$.push(this._salesRequestsService.saveSaleRequestConfigurationRefundAllowed(this.saleRequest.id, allowRefund));
            }
            if (this.form.get('categoriesDataForm').touched) {
                const categoryId = this.form.get('categoriesDataForm.categoryId').value;
                obs$.push(this._salesRequestsService.saveSaleRequestConfigurationCustomCategory(this.saleRequest.id, categoryId));
            }
            if (this.form.get('customerRelationshipDataForm').touched) {
                const subscriptionListId = this.form.get('customerRelationshipDataForm.subscriptionListId').value;
                obs$.push(this._salesRequestsService.saveSaleRequestConfigurationSubscriptionList(this.saleRequest.id, subscriptionListId));
            }

            return forkJoin(obs$)
                .pipe(
                    tap(() => {
                        this._ephemeralMessageService.showSuccess({
                            msgKey: 'SALE_REQUEST.UPDATE_SUCCESS',
                            msgParams: { saleRequestName: `${this.saleRequest.event.name}-${this.saleRequest.channel.name}` }
                        });
                    })
                );
        } else {
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanelQueryList);
            return throwError(() => 'invalid form');
        }
    }

    private initForms(): void {
        const refundForm = this._fb.group({
            allowedRefund: [null]
        });
        const categoriesDataForm = this._fb.group({
            categoryId: [null]
        });
        const customerRelationshipDataForm = this._fb.group({
            subscriptionListId: { value: null, disabled: true }
        });

        this.form = this._fb.group({
            refundForm,
            categoriesDataForm,
            customerRelationshipDataForm
        });
    }

    private updateForms(configuration: SaleRequestConfigurationModel): void {
        this.form.patchValue({
            refundForm: {
                allowedRefund: configuration.allow_refund
            },
            categoriesDataForm: {
                categoryId: this.saleRequest.channel.category?.custom.id
            },
            customerRelationshipDataForm: {
                subscriptionListId: this.saleRequest.subscription_list?.id
            }
        });
        this.form.markAsPristine();
        this.form.markAsUntouched();
    }
}
