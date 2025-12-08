import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { AuthenticationService, isMultiCurrency$, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { CategoriesService } from '@admin-clients/cpanel/organizations/data-access';
import { EventCommunicationService } from '@admin-clients/cpanel/promoters/events/communication/data-access';
import { EventsService, EventStatus, PutEvent } from '@admin-clients/cpanel/promoters/events/data-access';
import { ArchivedEventMgrComponent } from '@admin-clients/cpanel/promoters/events/feature';
import { EventSessionsService } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { ProducersService } from '@admin-clients/cpanel/promoters/producers/data-access';
import { CustomerTypesAssignationComponent } from '@admin-clients/cpanel/promoters/shared/feature/customer-types';
import { ToursService } from '@admin-clients/cpanel/promoters/tours/data-access';
import { CategoriesSelectionComponent } from '@admin-clients/cpanel/shared/ui/components';
import { SubscriptionListsService } from '@admin-clients/cpanel/viewers/subscriptions/data-access';
import { FeverZonePlanLinkComponent } from '@admin-clients/cpanel-fever-feature';
import { InvoiceInfoComponent } from '@admin-clients/cpanel-promoters-producers-feature';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { DialogSize, EphemeralMessageService, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { PrefixPipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import {
    ChangeDetectionStrategy, Component, QueryList, ViewChildren, ViewChild, inject, OnInit, DestroyRef, OnDestroy, computed, signal
} from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { TranslatePipe } from '@ngx-translate/core';
import { forkJoin, Observable, of, skip, switchMap, throwError } from 'rxjs';
import { filter, first, map, shareReplay } from 'rxjs/operators';
import { EventPrincipalInfoContactComponent } from './contact/event-principal-info-contact.component';
import { EventPrincipalInfoCurrencyComponent } from './currency/event-principal-info-currency.component';
import {
    EventPrincipalInfoCustomerRelationshipComponent
} from './customer-relationship/event-principal-info-customer-relationship.component';
import { EventPrincipalInfoDataComponent } from './data/event-principal-info-data.component';
import { EventPrincipalInfoLanguageComponent } from './language/event-principal-info-language.component';
import { EventPrincipalInfoSaleGoalsComponent } from './sale-goals/event-principal-info-sale-goals.component';
import { EventPrincipalInfoSidebarComponent } from './sidebar/event-principal-info-sidebar.component';

@Component({
    imports: [
        AsyncPipe, FlexLayoutModule, ReactiveFormsModule, MaterialModule, TranslatePipe, FormContainerComponent,
        EventPrincipalInfoCurrencyComponent, EventPrincipalInfoDataComponent, EventPrincipalInfoContactComponent,
        CategoriesSelectionComponent, EventPrincipalInfoCustomerRelationshipComponent, EventPrincipalInfoSaleGoalsComponent,
        EventPrincipalInfoSidebarComponent, EventPrincipalInfoLanguageComponent, ArchivedEventMgrComponent,
        CustomerTypesAssignationComponent, InvoiceInfoComponent, FeverZonePlanLinkComponent
    ],
    selector: 'app-principal-info',
    templateUrl: './event-principal-info.component.html',
    providers: [
        PrefixPipe.provider('EVENTS.')
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PrincipalInfoComponent
    implements WritingComponent, OnInit, OnDestroy {
    readonly #fb = inject(FormBuilder);
    readonly #entitiesSrv = inject(EntitiesBaseService);
    readonly #eventsSrv = inject(EventsService);
    readonly #eventCommunicationSrv = inject(EventCommunicationService);
    readonly #sessionsSrv = inject(EventSessionsService);
    readonly #producersSrv = inject(ProducersService);
    readonly #categoriesSrv = inject(CategoriesService);
    readonly #subscriptionListsSrv = inject(SubscriptionListsService);
    readonly #toursSrv = inject(ToursService);
    readonly #ephemeralMessageSrv = inject(EphemeralMessageService);
    readonly #messageDialogSrv = inject(MessageDialogService);
    readonly #destroyRef = inject(DestroyRef);
    readonly #authSrv = inject(AuthenticationService);

    @ViewChildren(MatExpansionPanel)
    private readonly _matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    @ViewChild(InvoiceInfoComponent)
    private readonly _invoiceInfoComponent: InvoiceInfoComponent;

    readonly form = this.#fb.nonNullable.group({
        putEventCtrl: null as PutEvent,
        eventStatusCtrl: [null as EventStatus, Validators.required]
    });

    readonly event$ = this.#eventsSrv.event.get$()
        .pipe(filter(Boolean), shareReplay({ refCount: true, bufferSize: 1 }));

    readonly $programmingStatus = signal(EventStatus.inProgramming);
    readonly $eventCustomerTypesAssigned = toSignal(this.#eventsSrv.customerTypesAssignation.get$());
    readonly $entityCustomerTypes = toSignal(this.#entitiesSrv.entityCustomerTypes.get$());
    readonly $hasAutomaticPurchaseCustomerTypes = computed(() => this.$entityCustomerTypes()
        ?.some(customerType => !!customerType.triggers?.find(trigger => trigger.trigger === 'PURCHASE')?.selected));

    readonly $isFeverZoneEnabled = toSignal(
        this.#authSrv.getLoggedUser$()
            .pipe(map(user => user && AuthenticationService.isSomeRoleInUserRoles(user, [UserRoles.FV_REPORTING])))
    );

    readonly literalKeysFather = {
        invoiceDescription: 'EVENTS.INVOICE_DESCRIPTION',
        organizatorData: 'EVENTS.ORGANIZATOR_DATA',
        producerData: 'EVENTS.PRODUCER_DATA',
        nif: 'EVENTS.NIF',
        socialReason: 'EVENTS.SOCIAL_REASON',
        address: 'EVENTS.ADDRESS',
        invoiceSequenceDescription: 'EVENTS.INVOICE_SEQUENCE_DESCRIPTION',
        invoiceSequenceLabel: 'EVENTS.INVOICE_SEQUENCE'
    } as const;

    readonly isLoadingOrSaving$ = booleanOrMerge([
        this.#eventsSrv.event.inProgress$(),
        this.#entitiesSrv.entityTypes.loading$(),
        this.#entitiesSrv.isEntityCategoriesLoading$(),
        this.#producersSrv.producer.loading$(),
        this.#producersSrv.invoicePrefixes.loading$(),
        this.#categoriesSrv.isCategoriesLoading$(),
        this.#toursSrv.isToursListLoading$(),
        this.#subscriptionListsSrv.isSubscriptionListsListLoading$(),
        this.#sessionsSrv.isAllSessionsLoading$(),
        this.#eventCommunicationSrv.isEventChannelContentImagesLoading$(),
        this.#eventsSrv.customerTypesAssignation.inProgress$()
    ]);

    //TODO(MULTICURRENCY): delete when the multicurrency functionality is finished
    readonly isMultiCurrency$ = isMultiCurrency$();

    ngOnInit(): void {
        this.#loadEntityCustomerTypes();
        this.#eventsSrv.event.get$()
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(event => {
                this.#eventsSrv.customerTypesAssignation.load(event.id);
                this.form.controls.eventStatusCtrl.reset(event.status, { emitEvent: false });
                if (event.archived) {
                    this.form.controls.eventStatusCtrl.disable({ emitEvent: false });
                } else {
                    this.form.controls.eventStatusCtrl.enable({ emitEvent: false });
                }
            });
    }

    ngOnDestroy(): void {
        this.#entitiesSrv.entityCustomerTypes.clear();
    }

    cancel(): void {
        this.#eventsSrv.event.get$()
            .pipe(first())
            .subscribe(event => {
                this.form.markAsPristine();
                this.form.markAsUntouched();
                this.form.controls.putEventCtrl.reset(null, { emitEvent: false });
                this.#eventsSrv.event.load(event.id.toString());

                if (this._invoiceInfoComponent) {
                    this._invoiceInfoComponent.resetInternalState();
                }
            });
    }

    save(): void {
        this.save$().subscribe();
    }

    save$(): Observable<unknown> {
        if (this.form.valid) {
            // Makes that all child component set the value of putEventCtrl
            this.form.controls.putEventCtrl.setValue({});
            return this.#eventsSrv.event.get$().pipe(
                first(),
                switchMap(event => {
                    const { currency_code: currencyCode } = this.form.controls.putEventCtrl.value;
                    let canContinue$: Observable<boolean>;
                    if (currencyCode) {
                        canContinue$ = this.#messageDialogSrv.showWarn({
                            size: DialogSize.SMALL,
                            title: 'FORMS.INFOS.CHANGE_CURRENCY_WARN',
                            message: 'EVENTS.FORMS.INFOS.CHANGE_CURRENCY_WARN_DETAILS',
                            showCancelButton: true
                        });
                    } else {
                        canContinue$ = of(true);
                    }

                    return canContinue$.pipe(
                        switchMap(canContinue => {
                            if (!canContinue) return of(null);

                            const obs$: Observable<void>[] = [];

                            if (Object.keys(this.form.controls.putEventCtrl.value).length) {
                                obs$.push(this.#eventsSrv.event.update(event.id, this.form.controls.putEventCtrl.value));
                            }

                            if (this.form.get('customerTypesAssignation')?.dirty) {
                                const customerTypesAssignation: { customerTypesToAdd: number[]; customerTypesToRemove: number[] } =
                                    this.form.get('customerTypesAssignation').value;
                                const customerTypesAssignationBody = [];
                                customerTypesAssignation.customerTypesToAdd?.forEach(customerType => {
                                    customerTypesAssignationBody.push({
                                        customer_type_id: customerType,
                                        mode: 'ADD'
                                    });
                                });
                                customerTypesAssignation.customerTypesToRemove?.forEach(customerType => {
                                    customerTypesAssignationBody.push({
                                        customer_type_id: customerType,
                                        mode: 'REMOVE'
                                    });
                                });
                                obs$.push(this.#eventsSrv.customerTypesAssignation.update(event.id, customerTypesAssignationBody));
                            }

                            return forkJoin(obs$)
                                .pipe(
                                    switchMap(() => {
                                        this.#ephemeralMessageSrv.showSaveSuccess();
                                        this.form.markAsPristine();
                                        this.form.markAsUntouched();
                                        this.form.controls.putEventCtrl.reset(null, { emitEvent: false });
                                        this.#eventsSrv.event.load(event.id.toString());
                                        return this.#eventsSrv.event.get$()
                                            .pipe(skip(1), first());
                                    })
                                );
                        })
                    );

                })
            );
        } else {
            this.form.markAllAsTouched();
            //SetValue in order to rerender child components with form fields in order to show input errors.
            this.form.setValue(this.form.getRawValue());
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanelQueryList);
            this.form.controls.putEventCtrl.reset(null, { emitEvent: false });
            return throwError(() => 'invalid form');
        }
    }

    #loadEntityCustomerTypes(): void {
        this.#entitiesSrv.getEntity$().pipe(
            filter(Boolean),
            takeUntilDestroyed(this.#destroyRef)
        ).subscribe(entity => {
            this.#entitiesSrv.entityCustomerTypes.load(Number(entity.id));
        });
    }
}
