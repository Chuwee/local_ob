import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { isMultiCurrency$ } from '@admin-clients/cpanel/core/data-access';
import { CategoriesService } from '@admin-clients/cpanel/organizations/data-access';
import { ProducersService } from '@admin-clients/cpanel/promoters/producers/data-access';
import {
    PutSeasonTicket, PutSeasonTicketStatus, SeasonTicketsService, SeasonTicketStatus
} from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import { CustomerTypesAssignationComponent } from '@admin-clients/cpanel/promoters/shared/feature/customer-types';
import { ToursService } from '@admin-clients/cpanel/promoters/tours/data-access';
import { CategoriesSelectionComponent } from '@admin-clients/cpanel/shared/ui/components';
import { InvoiceInfoComponent } from '@admin-clients/cpanel-promoters-producers-feature';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import {
    LanguageSelectorComponent,
    EphemeralMessageService,
    DialogSize, MessageDialogService
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import {
    ChangeDetectionStrategy, Component, QueryList, ViewChild, ViewChildren, inject, OnDestroy, computed, signal
} from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, Validators } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, first, from, Observable, of, skip, throwError } from 'rxjs';
import { concatAll, filter, last, shareReplay, switchMap } from 'rxjs/operators';
import { SeasonTicketPrincipalInfoContactComponent } from './contact/season-ticket-principal-info-contact.component';
import { SeasonTicketPrincipalInfoCurrencyComponent } from './currency/season-ticket-principal-info-currency.component';
import { SeasonTicketCustomerRelationshipComponent } from './customer-relationship/season-ticket-customer-relationship.component';
import { SeasonTicketPrincipalInfoStDataComponent } from './data/season-ticket-principal-info-st-data.component';
import { SeasonTicketPrincipalInfoLanguageComponent } from './language/season-ticket-principal-info-language.component';
import { SeasonTicketSalesGoalsComponent } from './sales-goals/season-ticket-sales-goals.component';

@Component({
    selector: 'app-season-ticket-principal-info',
    templateUrl: './season-ticket-principal-info.component.html',
    styleUrls: ['./season-ticket-principal-info.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        CommonModule, MaterialModule, ReactiveFormsModule, TranslatePipe, FlexLayoutModule, FormContainerComponent,
        SeasonTicketSalesGoalsComponent, CategoriesSelectionComponent, SeasonTicketCustomerRelationshipComponent,
        SeasonTicketPrincipalInfoCurrencyComponent, SeasonTicketPrincipalInfoContactComponent, SeasonTicketPrincipalInfoStDataComponent,
        SeasonTicketPrincipalInfoLanguageComponent, CustomerTypesAssignationComponent, InvoiceInfoComponent
    ]
})
export class SeasonTicketPrincipalInfoComponent implements WritingComponent, OnDestroy {
    readonly #fb = inject(UntypedFormBuilder);
    readonly #entitiesService = inject(EntitiesBaseService);
    readonly #producersService = inject(ProducersService);
    readonly #seasonTicketSrv = inject(SeasonTicketsService);
    readonly #toursService = inject(ToursService);
    readonly #categoriesService = inject(CategoriesService);
    readonly #ephemeralMessageSrv = inject(EphemeralMessageService);
    readonly #messageDialogSrv = inject(MessageDialogService);

    @ViewChildren(MatExpansionPanel)
    private _matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    @ViewChild(InvoiceInfoComponent)
    private readonly _invoiceInfoComponent: InvoiceInfoComponent;

    @ViewChild('languageSelector') languageSelector: LanguageSelectorComponent;

    readonly form = this.#fb.nonNullable.group({
        putSeasonTicketCtrl: null as PutSeasonTicket,
        putSeasonTicketStatusCtrl: null as PutSeasonTicketStatus,
        seasonTicketStatusCtrl: [null as SeasonTicketStatus, Validators.required]
    });

    readonly seasonTicketStatus = toSignal(this.#seasonTicketSrv.seasonTicketStatus.get$());
    readonly seasonTicket$ = this.#seasonTicketSrv.seasonTicket.get$()
        .pipe(shareReplay({ refCount: true, bufferSize: 1 }));

    readonly literalKeysFather = {
        invoiceDescription: 'SEASON_TICKETS.INVOICE_DESCRIPTION',
        organizatorData: 'SEASON_TICKETS.ORGANIZATOR_DATA',
        producerData: 'SEASON_TICKETS.PRODUCER_DATA',
        nif: 'SEASON_TICKETS.NIF',
        socialReason: 'SEASON_TICKETS.SOCIAL_REASON',
        address: 'SEASON_TICKETS.ADDRESS',
        invoiceSequenceDescription: 'SEASON_TICKETS.INVOICE_SEQUENCE_DESCRIPTION',
        invoiceSequenceLabel: 'SEASON_TICKETS.INVOICE_SEQUENCE'
    } as const;

    readonly $seasonTicket = toSignal(this.seasonTicket$);
    readonly isLoadingOrSaving$ = booleanOrMerge([
        this.#entitiesService.isEntityLoading$(),
        this.#entitiesService.isEntityTypesLoading$(),
        this.#entitiesService.isEntityCategoriesLoading$(),
        this.#seasonTicketSrv.seasonTicket.inProgress$(),
        this.#seasonTicketSrv.seasonTicketStatus.inProgress$(),
        this.#producersService.invoicePrefixes.loading$(),
        this.#producersService.isProducerLoading$(),
        this.#toursService.isToursListLoading$(),
        this.#categoriesService.isCategoriesLoading$(),
        this.#seasonTicketSrv.customerTypesAssignation.inProgress$()
    ]);

    readonly $programmingStatus = signal(SeasonTicketStatus.setUp);
    readonly $stCustomerTypesAssigned = toSignal(this.#seasonTicketSrv.customerTypesAssignation.get$());
    readonly $entityCustomerTypes = toSignal(this.#entitiesService.entityCustomerTypes.get$());
    readonly $hasAutomaticPurchaseCustomerTypes = computed(() => this.$entityCustomerTypes()
        ?.some(customerType => !!customerType.triggers?.find(trigger => trigger.trigger === 'PURCHASE')?.selected));

    readonly isMultiCurrency$ = isMultiCurrency$();

    constructor() {
        this.#loadEntityCustomerTypes();
        this.#producersService.producer.load(this.$seasonTicket()?.producer?.id);
        this.seasonTicket$.pipe(
            takeUntilDestroyed()
        ).subscribe(st => {
            this.#seasonTicketSrv.customerTypesAssignation.load(st.id);
        });
    }

    ngOnDestroy(): void {
        this.#entitiesService.entityCustomerTypes.clear();
    }

    cancel(): void {
        this.#seasonTicketSrv.seasonTicket.get$()
            .pipe(first())
            .subscribe(seasonTicket => {
                this.form.markAsPristine();
                this.form.markAsUntouched();
                this.form.controls.putSeasonTicketCtrl.reset(null, { emitEvent: false });
                this.form.controls.putSeasonTicketStatusCtrl.reset(null, { emitEvent: false });
                this.#seasonTicketSrv.seasonTicket.load(seasonTicket.id.toString());
                this.#seasonTicketSrv.seasonTicketStatus.load(seasonTicket.id.toString());

                if (this._invoiceInfoComponent) {
                    this._invoiceInfoComponent.resetInternalState();
                }
            });
    }

    save(): void {
        this.save$().subscribe();
    }

    save$(): Observable<unknown> {
        return this.#seasonTicketSrv.seasonTicket.get$()
            .pipe(
                first(),
                switchMap(seasonTicket => {
                    this.form.controls.putSeasonTicketCtrl.setValue({});
                    this.form.controls.putSeasonTicketStatusCtrl.setValue({});
                    if (this.form.valid) {
                        const obs$: Observable<void>[] = [];
                        const { status } = this.form.controls.putSeasonTicketStatusCtrl.value;
                        if (status) {
                            obs$.push(this.#seasonTicketSrv.seasonTicketStatus.save(seasonTicket.id, { status }));
                        }
                        if (Object.keys(this.form.controls.putSeasonTicketCtrl.value).length) {
                            obs$.push(
                                this.#seasonTicketSrv.seasonTicket.save(
                                    seasonTicket.id.toString(),
                                    this.form.controls.putSeasonTicketCtrl.value
                                )
                            );
                        }

                        if (this.form.get('customerTypesAssignation')?.dirty) {
                            const customerTypesAssignation: { customerTypesToAdd: number[]; customerTypesToRemove: number[] }
                                = this.form.get('customerTypesAssignation').value;
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
                            obs$.push(this.#seasonTicketSrv.customerTypesAssignation.update(seasonTicket.id, customerTypesAssignationBody));
                        }

                        const { currency_code: currencyCode } = this.form.controls.putSeasonTicketCtrl.value;
                        let canContinue$: Observable<boolean>;
                        if (currencyCode) {
                            canContinue$ = this.#messageDialogSrv.showWarn({
                                size: DialogSize.SMALL,
                                title: 'FORMS.INFOS.CHANGE_CURRENCY_WARN',
                                message: 'SEASON_TICKETS.FORMS.INFOS.CHANGE_CURRENCY_WARN_DETAILS',
                                showCancelButton: true
                            });
                        } else {
                            canContinue$ = of(true);
                        }

                        return canContinue$.pipe(
                            switchMap(canContinue => {
                                if (!canContinue) return of(null);

                                return from(obs$).pipe(
                                    concatAll(),
                                    last(),
                                    switchMap(() => {
                                        this.#ephemeralMessageSrv.showSaveSuccess();
                                        this.form.markAsPristine();
                                        this.form.markAsUntouched();
                                        this.form.controls.putSeasonTicketCtrl.reset(null, { emitEvent: false });
                                        this.form.controls.putSeasonTicketStatusCtrl.reset(null, { emitEvent: false });
                                        this.#seasonTicketSrv.seasonTicket.load(seasonTicket.id.toString());
                                        this.#seasonTicketSrv.seasonTicketStatus.load(seasonTicket.id.toString());
                                        return combineLatest([
                                            this.#seasonTicketSrv.seasonTicket.get$().pipe(skip(1), first()),
                                            this.#seasonTicketSrv.seasonTicketStatus.get$().pipe(skip(1), first())
                                        ]);
                                    })
                                );
                            })
                        );
                    } else {
                        this.form.markAllAsTouched();
                        //SetValue in order to rerender child components with form fields in order to show input errors.
                        this.form.setValue(this.form.getRawValue());
                        scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanelQueryList);
                        this.form.controls.putSeasonTicketCtrl.reset(null, { emitEvent: false });
                        this.form.controls.putSeasonTicketStatusCtrl.reset(null, { emitEvent: false });
                        return throwError(() => 'invalid form');
                    }
                })
            );
    }

    #loadEntityCustomerTypes(): void {
        this.#entitiesService.getEntity$().pipe(
            filter(Boolean),
            takeUntilDestroyed()
        ).subscribe(entity => {
            this.#entitiesService.entityCustomerTypes.load(Number(entity.id));
        });
    }
}
