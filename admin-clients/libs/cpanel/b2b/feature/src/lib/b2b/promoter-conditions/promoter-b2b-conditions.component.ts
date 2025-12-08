import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { Metadata } from '@OneboxTM/utils-state';
import {
    B2B_CONDITIONS_SERVICE,
    B2bConditionsClient, B2bService, ConditionsToVmConditionsMap, ConditionType, PaymentMethodType,
    PutB2bConditionsClients, VmB2bConditionsClient, VmEditPromoterConditionsData
} from '@admin-clients/cpanel/b2b/data-access';
import { EntitiesBaseService, Entity } from '@admin-clients/shared/common/data-access';
import {
    DialogSize, EphemeralMessageService, ExpirationDaysSelectorComponent, MessageDialogService, ObMatDialogConfig,
    PercentageInputComponent, SearchablePaginatedSelectionLoadEvent, SearchablePaginatedSelectionModule
} from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { LocalNumberPipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge, differenceWith, unionWith } from '@admin-clients/shared/utility/utils';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, EventEmitter, inject, OnDestroy, OnInit, ViewContainerRef } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormControl, UntypedFormGroup } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatCheckbox, MatCheckboxChange } from '@angular/material/checkbox';
import { MatOption } from '@angular/material/core';
import { MatDialog } from '@angular/material/dialog';
import { MatDivider } from '@angular/material/divider';
import { MatFormField } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatSelect } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import {
    filter, first, map, Observable, shareReplay, startWith, switchMap, take, tap, throwError, withLatestFrom
} from 'rxjs';
import { EditPromoterB2bConditionsDialogComponent } from './edit/edit-promoter-b2b-conditions-dialog.component';

const PAGE_SIZE = 20;

@Component({
    selector: 'app-promoter-b2b-conditions',
    templateUrl: './promoter-b2b-conditions.component.html',
    styleUrls: ['./promoter-b2b-conditions.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        CommonModule, TranslatePipe, FormContainerComponent, MatIconButton, MatInput,
        MatIcon, MatDivider, MatTooltip, MatButton, MatCheckbox, MatTableModule, MatFormField, MatOption, MatSelect,
        SearchablePaginatedSelectionModule, ReactiveFormsModule, FlexLayoutModule,
        ExpirationDaysSelectorComponent, PercentageInputComponent, LocalNumberPipe, EllipsifyDirective
    ]
})
export class PromoterB2bConditionsComponent implements WritingComponent, OnInit, OnDestroy {
    readonly #b2bConditionsService = inject(B2B_CONDITIONS_SERVICE);
    readonly #b2bSrv = inject(B2bService);
    readonly #fb = inject(UntypedFormBuilder);
    readonly #breakpointObserver = inject(BreakpointObserver);
    readonly #ephemeralMsgSrv = inject(EphemeralMessageService);
    readonly #msgDialogService = inject(MessageDialogService);
    readonly #matDialog = inject(MatDialog);
    readonly #entitiesSrv = inject(EntitiesBaseService);
    readonly #viewContainerRef = inject(ViewContainerRef);
    readonly #destroyRef = inject(DestroyRef);

    #modifiedRows: VmB2bConditionsClient[] = [];
    #filters: PageableFilter = { limit: PAGE_SIZE, sort: 'name:asc' };
    #contextCurrency: string;
    #contextId: number;
    #entity: Entity;

    readonly context = this.#b2bConditionsService.context;
    readonly pageSize = PAGE_SIZE;
    readonly showSelectedOnlyClick = new EventEmitter<boolean>();
    readonly paymentMethodTypes = Object.values(PaymentMethodType);
    readonly columns = [
        'active', 'b2bClient', 'canBuy', 'canBook', 'canPublish', 'canInvite', 'maxSeats', 'bookingExpirationDays',
        'clientDiscount' /* + clientDiscountPercentage*/,
        'clientComission', 'paymentMethods', 'condHierarchicalLevel', 'actions'
    ];

    readonly entity$ = this.#entitiesSrv.getEntity$();
    readonly showCanPublish$ = this.entity$.pipe(map(entity => entity?.settings?.allow_B2B_publishing));
    readonly showCanInvite$ = this.entity$.pipe(map(entity => entity?.settings?.allow_invitations));

    readonly reqInProgress$ = booleanOrMerge([
        this.#b2bSrv.isConditionsClientsInProgress$(),
        this.#b2bSrv.isAllB2bConditionsClientsLoading$()
    ]);

    readonly translationKeys = {
        default: 'PROFESSIONAL_SELLING.CONDITIONS.BOOKING_EXPIRATION_DAYS_FROM_EVENT',
        custom: 'PROFESSIONAL_SELLING.CONDITIONS.BOOKING_EXPIRATION_DAYS_CUSTOM_DAYS'
    };

    readonly showSelectedOnly$ = this.showSelectedOnlyClick.pipe(
        startWith(false),
        shareReplay(1)
    );

    conditionsClientList$: Observable<VmB2bConditionsClient[]>;
    metadata$: Observable<Metadata>;

    form: UntypedFormGroup;
    selectedRowsCtrl = new UntypedFormControl([]);
    totalClients$: Observable<number>;
    isHandsetOrTablet$: Observable<boolean> = this.#breakpointObserver
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(map(result => result.matches));

    totalColumns$ = this.entity$.pipe(
        tap(entity => this.#entity = entity),
        map(entity => {
            let columns = [...this.columns];
            const settings = entity?.settings;
            if (!settings?.allow_B2B_publishing) {
                columns = columns.filter(elem => elem !== 'canPublish');
            }
            if (!settings?.allow_invitations) {
                columns = columns.filter(elem => elem !== 'canInvite');
            }
            return columns;
        })
    );

    get numSelectedClients(): number {
        return this.selectedRowsCtrl?.value?.length || 0;
    }

    get clientsFormGroup(): UntypedFormGroup {
        return this.form?.get('clients') as UntypedFormGroup;
    }

    set clientsFormGroup(val: UntypedFormGroup) {
        this.form?.setControl('clients', val);
    }

    ngOnInit(): void {
        this.form = this.#fb.group({
            clients: this.#fb.group({})
        });

        this.#b2bConditionsService.getContextIdAndCurrency()
            .pipe(first(Boolean))
            .subscribe(({ id, currency }) => {
                this.#contextId = id;
                this.#contextCurrency = currency;
            });

        const selectedClients$: Observable<VmB2bConditionsClient[]> =
            this.selectedRowsCtrl.valueChanges
                .pipe(
                    map((selected: VmB2bConditionsClient[]) => {
                        if (!selected?.length) {
                            this.showSelectedOnlyClick.next(false);
                            return [];
                        }
                        return selected?.sort((a, b) => a.name.localeCompare(b.name));
                    }),
                    takeUntilDestroyed(this.#destroyRef),
                    shareReplay(1)
                );

        selectedClients$.subscribe();

        // all selectable clients
        const allClients$ = this.#b2bSrv.getConditionsClientsData$()
            .pipe(
                map(condClients => this.#mapConditionsClientsToVm(condClients, true)),
                shareReplay(1)
            );

        this.conditionsClientList$ = this.showSelectedOnly$.pipe(
            switchMap(isActive => isActive ? selectedClients$ : allClients$),
            shareReplay(1)
        );

        this.totalClients$ = this.#b2bSrv.getConditionsClientsMetadata$()
            .pipe(map(metadata => metadata?.total || 0));

        this.metadata$ = this.showSelectedOnly$.pipe(
            switchMap(isActive => isActive ?
                selectedClients$.pipe(map(list => new Metadata({ total: list?.length, limit: 999, offset: 0 }))) :
                this.#b2bSrv.getConditionsClientsMetadata$()
            ),
            shareReplay(1)
        );
    }

    ngOnDestroy(): void {
        this.#b2bSrv.clearConditionsClients();
    }

    filterChangeHandler({ limit, q, offset }: SearchablePaginatedSelectionLoadEvent): void {
        this.#filters = { ...this.#filters, limit, offset, q: q?.length ? q : null };
        this.#loadEventConditionsClients();
    }

    cancel(): void {
        this.#resetComponentState();
    }

    save$(): Observable<void> {
        const filterPublish = (cond: ConditionType): boolean => (cond !== 'CAN_PUBLISH' || this.#entity?.settings.allow_B2B_publishing);
        const filterInvitation = (cond: ConditionType): boolean => (cond !== 'CAN_INVITE' || this.#entity?.settings.allow_invitations);

        if (this.form.valid) {
            const formValues = this.clientsFormGroup.value;
            const conditions = Object.keys(ConditionsToVmConditionsMap) as ConditionType[];
            const modifClients: PutB2bConditionsClients = {
                id: this.#contextId,
                clients: this.#modifiedRows
                    .map(client => {
                        const clientDiscount: string = formValues[client.id].clientDiscount;
                        const isPercentage = String(clientDiscount).includes('%');

                        return {
                            id: client.id,
                            conditions: conditions
                                .filter(cond => filterPublish(cond) && filterInvitation(cond) &&
                                    (isPercentage ? cond !== 'CLIENT_DISCOUNT' : cond !== 'CLIENT_DISCOUNT_PERCENTAGE')
                                )
                                .map(cond => {
                                    let formValue = formValues[client.id][ConditionsToVmConditionsMap[cond]];
                                    if (cond === 'CLIENT_DISCOUNT' || cond === 'CLIENT_DISCOUNT_PERCENTAGE') {
                                        formValue = Number(String(formValues[client.id].clientDiscount).replace('%', ''));
                                    }
                                    return {
                                        condition_type: cond,
                                        value: formValue ?? client[ConditionsToVmConditionsMap[cond]],
                                        ...(cond === 'CLIENT_DISCOUNT' &&
                                            { currencies: [{ currency_code: this.#contextCurrency, value: formValue }] })
                                    };
                                })
                        };
                    })
            };

            return this.#b2bSrv.saveConditionsClients('EVENT', modifClients)
                .pipe(tap(() => this.#ephemeralMsgSrv.showSaveSuccess()));
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return throwError(() => 'invalid form');
        }
    }

    saveConditions(): void {
        this.save$().subscribe(() => this.#resetComponentState());
    }

    markRowAsModified(row: VmB2bConditionsClient): void {
        const rowFormGroup = this.clientsFormGroup.get(row.id.toString()) as UntypedFormGroup;
        const rowFormGroupValues = rowFormGroup.value;
        row.modified = Object.keys(rowFormGroupValues).reduce<boolean>((modified, ctrlName) => {
            if (Array.isArray(rowFormGroupValues[ctrlName]) && rowFormGroupValues[ctrlName].sort().join() !== row[ctrlName].sort().join()) {
                return true;
            } else if (rowFormGroupValues[ctrlName] !== row[ctrlName]) {
                return true;
            }
            return modified;
        }, false);
        const modifRowIndex = this.#modifiedRows.findIndex(modifRow => modifRow.id === row.id);
        if (row.modified && modifRowIndex < 0) {
            this.#modifiedRows.push(row);
        } else if (!row.modified && modifRowIndex >= 0) {
            this.#modifiedRows.splice(modifRowIndex, 1);
            rowFormGroup.markAsPristine();
        }
    }

    clickShowSelected(): void {
        this.showSelectedOnly$.pipe(take(1)).subscribe((isSelected => this.showSelectedOnlyClick.emit(!isSelected)));
    }

    restoreClientConditions(client: VmB2bConditionsClient): void {
        this.#msgDialogService.showWarn({
            size: DialogSize.SMALL,
            title: 'TITLES.ALERT',
            message: 'PROFESSIONAL_SELLING.DELETE_B2B_CLIENTS_CONDITIONS_CONFIRM_MSG',
            actionLabel: 'FORMS.ACTIONS.OK',
            showCancelButton: true
        })
            .pipe(
                filter(accepted => !!accepted),
                switchMap(() => this.#b2bSrv.deleteConditionsClients('EVENT', {
                    ...(this.context === 'SEASON_TICKET' && { season_ticket_id: this.#contextId }),
                    ...(this.context === 'EVENT' && { event_id: this.#contextId }),
                    clients_ids: [client.id]
                }))
            )
            .subscribe(() => {
                this.#resetComponentState();
                this.#ephemeralMsgSrv.showSaveSuccess();
            });
    }

    editB2bConditions(selectedClients?: VmB2bConditionsClient[]): void {
        this.#matDialog.open<EditPromoterB2bConditionsDialogComponent, VmEditPromoterConditionsData, boolean>(
            EditPromoterB2bConditionsDialogComponent, new ObMatDialogConfig({
                contextId: this.#contextId,
                contextCurrency: this.#contextCurrency,
                context: this.context,
                selectedClients
            }, this.#viewContainerRef)
        )
            .beforeClosed()
            .subscribe(actionPerformed => {
                if (actionPerformed) {
                    this.#resetComponentState();
                    this.#ephemeralMsgSrv.showSaveSuccess();
                }
            });
    }

    editMultipleClientConditions(): void {
        const selectedClients: VmB2bConditionsClient[] = this.selectedRowsCtrl.value;
        this.editB2bConditions(selectedClients);
    }

    /**
     * selects all filtered clients
     */
    selectAll(change?: MatCheckboxChange): void {
        this.#b2bSrv.loadAllConditionsClients('EVENT', {
            ...this.#filters,
            ...(this.context === 'SEASON_TICKET' && { season_ticket_id: this.#contextId }),
            ...(this.context === 'EVENT' && { event_id: this.#contextId })
        });
        this.#b2bSrv.getAllConditionsClients$()
            .pipe(
                first(condClients => !!condClients),
                map(condClients => this.#mapConditionsClientsToVm(condClients))
            )
            .subscribe(condClients => {
                if (change?.checked) {
                    this.selectedRowsCtrl.patchValue(unionWith(this.selectedRowsCtrl.value, condClients));
                } else {
                    this.selectedRowsCtrl.patchValue(differenceWith(this.selectedRowsCtrl.value, condClients));
                }
            });
    }

    #mapConditionsClientsToVm(condClients: B2bConditionsClient[], updateControls = false): VmB2bConditionsClient[] {
        if (!condClients) {
            return [];
        }

        return condClients.map(condClient => {
            const result = condClient.conditions.reduce<Partial<VmB2bConditionsClient>>((accum, cond) => {
                const vmProperty = ConditionsToVmConditionsMap[cond.condition_type];
                return {
                    ...accum,
                    ...(vmProperty ? {
                        [
                            // en esta sección ambas propiedades se setean en un mismo formCtrl:
                            cond.condition_type === 'CLIENT_DISCOUNT' || cond.condition_type === 'CLIENT_DISCOUNT_PERCENTAGE' ?
                                'clientDiscount' : vmProperty
                        ]: cond.condition_type === 'CLIENT_DISCOUNT_PERCENTAGE'
                                ? cond.value + '%'
                                : cond.condition_type === 'CLIENT_DISCOUNT'
                                    ? cond?.currencies?.[0]?.value || cond.value
                                    : cond.value
                    } : {})
                };
            }, {
                id: condClient.client.id,
                name: condClient.client.name,
                modified: this.#modifiedRows.some(client => client.id === condClient.client.id),
                condHierarchicalLevel: condClient.condition_group_type
            }) as VmB2bConditionsClient;

            if (updateControls && !result.modified) {
                this.clientsFormGroup.setControl(condClient.client.id.toString(), this.#fb.group({
                    canBuy: [result.canBuy],
                    canBook: [result.canBook],
                    canPublish: [result.canPublish],
                    canInvite: [result.canInvite],
                    maxSeats: [result.maxSeats],
                    bookingExpirationDays: [result.bookingExpirationDays],
                    clientDiscount: [result.clientDiscount],
                    clientComission: [result.clientComission],
                    paymentMethods: [result.paymentMethods, []]
                }));
            }

            return result;
        });
    }

    #resetComponentState(): void {
        this.#modifiedRows = [];
        this.selectedRowsCtrl.setValue([]);
        this.form.markAsPristine();
        this.#loadEventConditionsClients();
    }

    #loadEventConditionsClients(): void {
        this.#b2bConditionsService.getContextIdAndCurrency()
            .pipe(
                first(({ id }) => !!id),
                switchMap(({ id }) => {
                    this.#b2bSrv.loadConditionsClients('EVENT', {
                        ...this.#filters,
                        ...(this.context === 'SEASON_TICKET' && { season_ticket_id: id }),
                        ...(this.context === 'EVENT' && { event_id: id })
                    });
                    // change to non selected only view if a search is made
                    return this.#b2bSrv.getConditionsClientsData$().pipe(withLatestFrom(this.showSelectedOnly$));
                }),
                take(1)
            )
            .subscribe(([, isSelectedOnlyMode]) => this.showSelectedOnlyClick.emit(isSelectedOnlyMode));
    }

}

export default PromoterB2bConditionsComponent;
