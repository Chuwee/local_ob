import {
    customerTypeRestrictions, EntitiesService, EntityCustomerTypeRestriction
} from '@admin-clients/cpanel/organizations/entities/data-access';
import { EntityCustomerType } from '@admin-clients/shared/common/data-access';
import {
    DialogSize, EmptyStateComponent, EphemeralMessageService, MessageDialogService, ObMatDialogConfig
} from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, computed, DestroyRef, inject, OnDestroy, ViewContainerRef } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatSpinner } from '@angular/material/progress-spinner';
import { MatSortModule } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, first, map, switchMap } from 'rxjs';
import {
    CreateUpdateEntityCustomerTypeRestrictionsDialogComponent
} from './create-update-entity-customer-type-restrictions-dialog/create-update-entity-customer-type-restrictions-dialog.component';
import {
    CreateUpdateEntityCustomerTypeDialogComponent
} from './create-update-entity-customer-types-dialog/create-update-entity-customer-type-dialog.component';

@Component({
    selector: 'app-entity-register-users-customer-types',
    imports: [TranslatePipe, MatTableModule, MatSortModule, MatIconModule, MatButtonModule, MatSpinner,
        EmptyStateComponent, FormContainerComponent
    ],
    templateUrl: './entity-register-users-customer-types.component.html',
    styleUrl: './entity-register-users-customer-types.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EntityCustomerTypesComponent implements OnDestroy {

    readonly #entitiesSrv = inject(EntitiesService);
    readonly #matDialog = inject(MatDialog);
    readonly #ephemeralMsg = inject(EphemeralMessageService);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #destroyRef = inject(DestroyRef);
    readonly #entity$ = this.#entitiesSrv.getEntity$();
    readonly #viewContainerRef = inject(ViewContainerRef);

    readonly $customerTypes = toSignal(this.#entitiesSrv.entityCustomerTypes.get$().pipe(filter(Boolean)));
    readonly $restrictions = toSignal(this.#entitiesSrv.entityCustomerTypesRestrictions.get$().pipe(
        filter(Boolean),
        map(restrictions => restrictions?.restrictions || [])
    ));

    readonly $restrictionTypesAllowedToCreate = computed(() => customerTypeRestrictions
        .filter(ctRestriction => !this.$restrictions()?.map(restr => restr.key).includes(ctRestriction)));

    readonly $restrictionsWithCustomerTypesNamesMap = computed(() => {
        const map = new Map();
        this.$restrictions()?.forEach(restr => {
            const customerTypesNames = restr.restricted_customer_types
                ?.map(ctId => this.$customerTypes()?.find(ct => ct.id === ctId)?.name)?.join(', ') || [];
            map.set(restr.key, customerTypesNames);
        });
        return map;
    });

    readonly $isLoading = toSignal(booleanOrMerge([
        this.#entitiesSrv.entityCustomerTypes.inProgress$(),
        this.#entitiesSrv.entityCustomerTypesRestrictions.inProgress$()
    ]));

    readonly customerTypesColumnsToIterate = ['name', 'code'];
    readonly customerTypesColumns = ['name', 'code', 'assignation_type', 'actions'];
    readonly restrictionsColumns = ['key', 'restricted_custom_types', 'actions'];

    constructor() {
        this.#loadCustomerTypes();
        this.#loadRestrictions();
    }

    ngOnDestroy(): void {
        this.#entitiesSrv.entityCustomerTypes.clear();
        this.#entitiesSrv.entityCustomerTypesRestrictions.clear();
    }

    openCreateOrUpdateCustomerTypeDialog(customerType: EntityCustomerType = null): void {
        this.#matDialog.open(CreateUpdateEntityCustomerTypeDialogComponent, new ObMatDialogConfig({
            customerType,
            allCustomerTypes: this.$customerTypes()
        }, this.#viewContainerRef))
            .afterClosed()
            .subscribe(success => {
                if (success) {
                    this.#loadCustomerTypes();
                }
            });
    }

    openCreateOrUpdateCustomerTypeRestrictionDialog(restriction: EntityCustomerTypeRestriction = null): void {
        this.#matDialog.open(CreateUpdateEntityCustomerTypeRestrictionsDialogComponent, new ObMatDialogConfig({
            restriction,
            allCustomerTypes: this.$customerTypes(),
            allRestrictions: this.$restrictions()
        }, this.#viewContainerRef))
            .afterClosed()
            .subscribe(success => {
                if (success) {
                    this.#loadRestrictions();
                }
            });
    }

    openDeleteRestrictionDialog(restrictionKey: string): void {
        this.#msgDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'TITLES.DELETE_CUSTOM_TYPE_RESTRICTION',
            message: 'ENTITY.CUSTOM_TYPE.RESTRICTIONS.DELETE_WARNING',
            actionLabel: 'FORMS.ACTIONS.DELETE',
            showCancelButton: true
        }).pipe(
            filter(Boolean),
            switchMap(() => this.#entity$),
            switchMap(entity => {
                const body = {
                    entity_id: entity.id,
                    restrictions: this.$restrictions()?.filter(restr => restr.key !== restrictionKey)
                };
                return this.#entitiesSrv.entityCustomerTypesRestrictions.save(entity.id, body);
            })
        ).subscribe(() => {
            this.#ephemeralMsg.showSuccess({
                msgKey: 'ENTITY.CUSTOM_TYPE.RESTRICTIONS.DELETE_SUCCESS'
            });
            this.#loadRestrictions();
        });
    }

    #loadCustomerTypes(): void {
        this.#entity$.pipe(
            first(Boolean),
            takeUntilDestroyed(this.#destroyRef)
        ).subscribe(entity => this.#entitiesSrv.entityCustomerTypes.load(entity.id));
    }

    #loadRestrictions(): void {
        this.#entity$.pipe(
            first(Boolean),
            takeUntilDestroyed(this.#destroyRef)
        ).subscribe(entity => this.#entitiesSrv.entityCustomerTypesRestrictions.load(entity.id));
    }

}
