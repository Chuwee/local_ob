import { PackItem, PacksService } from '@admin-clients/cpanel/packs/my-packs/data-access';
import {
    GetProductsDeliveryPointsRequest, productsDeliveryPointsProviders
} from '@admin-clients/cpanel/products/delivery-points/data-access';
import { productsProviders } from '@admin-clients/cpanel/products/my-products/data-access';
import {
    EmptyStateTinyComponent, EphemeralMessageService, MessageDialogService, ObMatDialogConfig
} from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { MatButton, MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatDividerModule } from '@angular/material/divider';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatRadioModule } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';
import { RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, tap } from 'rxjs';
import { CreatePackElemetsDialogComponent } from './create/create-pack-elements-dialog.component';
import { ProductDetailComponent } from './product-detail/product-detail.component';
import { SessionDetailComponent } from './session-detail/session-detail.component';
import { EditPackSubItemsDialogComponent, EditPackSubItemsDialogData } from './subitems/edit-pack-subitems-dialog.component';

export const SUBITEMS_LIMIT = 50;

@Component({
    selector: 'app-pack-elements',
    templateUrl: './pack-elements.component.html',
    styleUrls: ['./pack-elements.component.scss'],
    imports: [
        FlexLayoutModule, AsyncPipe, TranslatePipe, MatProgressSpinnerModule, FormContainerComponent,
        EmptyStateTinyComponent, MatIconModule, MatButton, DateTimePipe, MatExpansionModule, MatButtonModule, MatDividerModule,
        MatFormFieldModule, MatSelectModule, MatRadioModule, ReactiveFormsModule, SessionDetailComponent, ProductDetailComponent,
        RouterLink
    ],
    providers: [productsProviders, productsDeliveryPointsProviders],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PackElementsComponent implements OnInit {
    readonly #packsSrv = inject(PacksService);
    readonly #msgDialogService = inject(MessageDialogService);
    readonly #matDialog = inject(MatDialog);
    readonly #ephemeralMsg = inject(EphemeralMessageService);

    dateTimeFormats = DateTimeFormats;
    #currentItemsIds: number[];

    readonly productDeliveryPointsReq = new GetProductsDeliveryPointsRequest();
    readonly $pack = toSignal(this.#packsSrv.pack.get$().pipe(filter(Boolean)));
    readonly $packItems = toSignal(this.#packsSrv.packItems.get$().pipe(
        filter(Boolean),
        tap(items => {
            this.#currentItemsIds = items.map(item => item.item_id);
            const mainItem = items.find(item => item.main);
            if (mainItem) {
                this.#loadOneSubItem(mainItem);
            }
        })
    ));

    readonly $packSubItems = toSignal(this.#packsSrv.packSubItems.getData$().pipe(
        filter(Boolean)
    ));

    readonly $mainItem = computed(() => this.$packItems()?.find(item => item.main));
    readonly $items = computed(() => this.$packItems()?.filter(item => !item.main));

    readonly loading$ = booleanOrMerge([
        this.#packsSrv.pack.loading$(),
        this.#packsSrv.packItems.loading$(),
        this.#packsSrv.packSubItems.loading$()
    ]);

    ngOnInit(): void {
        this.#packsSrv.pack.load(this.$pack().id);
        this.#packsSrv.packItems.load(this.$pack().id);
    }

    delete(event: Event, item: PackItem): void {
        event.stopPropagation();
        if (this.$pack().has_sales) {
            this.#msgDialogService.showAlert({
                title: 'CHANNELS.PACKS.ELEMENTS.DELETE_HAS_SALES_ERROR_TITLE',
                message: 'CHANNELS.PACKS.ELEMENTS.DELETE_HAS_SALES_ERROR_DESCRIPTION'
            });
            return;
        }

        this.#msgDialogService.showDeleteConfirmation({
            confirmation: {
                title: 'CHANNELS.PACKS.ELEMENTS.DELETE_TITLE',
                message: 'CHANNELS.PACKS.ELEMENTS.DELETE_MESSAGE',
                messageParams: { name: item.name }
            },
            delete$: this.#packsSrv.packItems.delete(this.$pack().id, item.id).pipe(
                tap(() => this.#packsSrv.packItems.load(this.$pack().id)))
        });
    }

    openAddPackElementDialog(): void {
        if (this.$pack().has_sales) {
            this.#msgDialogService.showAlert({
                title: 'CHANNELS.PACKS.ELEMENTS.CREATE.HAS_SALES_ERROR_TITLE',
                message: 'CHANNELS.PACKS.ELEMENTS.CREATE.HAS_SALES_ERROR_DESCRIPTION'
            });
            return;
        }

        this.#matDialog.open(CreatePackElemetsDialogComponent, new ObMatDialogConfig({
            packType: this.$pack().type,
            packId: this.$pack().id,
            mainSession: this.$mainItem(),
            entityId: this.$pack().entity.id,
            currentItemsIds: this.#currentItemsIds
        })).beforeClosed()
            .subscribe((packElementId: number) => {
                if (packElementId) {
                    this.#ephemeralMsg.showSuccess({
                        msgKey: 'CHANNELS.PACKS.ELEMENTS.CREATE.CREATE_SUCCESS'
                    });
                    this.#packsSrv.packItems.load(this.$pack().id);
                }
            });
    }

    openEditSubItemsDialog(item: PackItem): void {
        const pack = this.$pack();
        this.#matDialog.open<EditPackSubItemsDialogComponent, EditPackSubItemsDialogData, number>(
            EditPackSubItemsDialogComponent, new ObMatDialogConfig<EditPackSubItemsDialogData>({
                packId: pack.id,
                eventId: item.item_id,
                itemId: item.id,
                venueTplId: this.#getVenueTplId(item)
            })
        )
            .beforeClosed()
            .subscribe(saved => {
                if (saved) {
                    this.#ephemeralMsg.showSuccess({ msgKey: 'FORMS.FEEDBACK.SAVE_SUCCESS' });
                    this.#loadOneSubItem(this.$mainItem());
                }
            });
    }

    handleEditSubItemsKeydown(event: KeyboardEvent, item: PackItem): void {
        if (event.key === 'Enter' || event.key === ' ') {
            event.preventDefault();
            this.openEditSubItemsDialog(item);
        }
    }

    #loadOneSubItem(mainItem?: PackItem): void {
        const pack = this.$pack();
        if (!mainItem || !pack || mainItem.type !== 'EVENT') {
            return this.#packsSrv.packSubItems.clear();
        }

        this.#packsSrv.packSubItems.load(pack.id, mainItem.id, { limit: 2 });
    }

    #getVenueTplId(item: PackItem): number | null {
        switch (item.type) {
            case 'SESSION':
                return item.session_data?.venue_template.id;
            case 'EVENT':
                return item.event_data?.venue_template.id;
            default:
                return null;
        }
    };
}
