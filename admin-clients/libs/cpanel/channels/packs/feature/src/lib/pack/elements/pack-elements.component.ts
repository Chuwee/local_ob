import { ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import { PackItem, PacksService } from '@admin-clients/cpanel/channels/packs/data-access';
import { GetProductsDeliveryPointsRequest, productsDeliveryPointsProviders } from '@admin-clients/cpanel/products/delivery-points/data-access';
import { productsProviders } from '@admin-clients/cpanel/products/my-products/data-access';
import { EmptyStateTinyComponent, EphemeralMessageService, MessageDialogService, ObMatDialogConfig } from '@admin-clients/shared/common/ui/components';
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
import { TranslatePipe } from '@ngx-translate/core';
import { filter, tap } from 'rxjs';
import { CreatePackElemetsDialogComponent } from './create/create-pack-elements-dialog.component';
import { ProductDetailComponent } from './product-detail/product-detail.component';
import { SessionDetailComponent } from './session-detail/session-detail.component';

@Component({
    selector: 'app-pack-elements',
    templateUrl: './pack-elements.component.html',
    styleUrls: ['./pack-elements.component.scss'],
    imports: [
        FlexLayoutModule, AsyncPipe, TranslatePipe, MatProgressSpinnerModule, FormContainerComponent,
        EmptyStateTinyComponent, MatIconModule, MatButton, DateTimePipe, MatExpansionModule, MatButtonModule, MatDividerModule,
        MatFormFieldModule, MatSelectModule, MatRadioModule, ReactiveFormsModule, SessionDetailComponent, ProductDetailComponent
    ],
    providers: [productsProviders, productsDeliveryPointsProviders],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PackElementsComponent implements OnInit {
    readonly #packsSrv = inject(PacksService);
    readonly #channelsSrv = inject(ChannelsService);
    readonly #msgDialogService = inject(MessageDialogService);
    readonly #matDialog = inject(MatDialog);
    readonly #ephemeralMsg = inject(EphemeralMessageService);

    dateTimeFormats = DateTimeFormats;
    #currentItemsIds: number[];

    readonly productDeliveryPointsReq = new GetProductsDeliveryPointsRequest();
    readonly $channel = toSignal(this.#channelsSrv.getChannel$().pipe(filter(Boolean)));
    readonly $pack = toSignal(this.#packsSrv.pack.get$().pipe(filter(Boolean)));
    readonly $packItems = toSignal(this.#packsSrv.packItems.get$().pipe(
        filter(Boolean),
        tap(items => this.#currentItemsIds = items.map(item => item.item_id))
    ));

    readonly $mainSession = computed(() => this.$packItems()?.find(item => item.main));
    readonly $items = computed(() => this.$packItems()?.filter(item => !item.main));

    readonly loading$ = booleanOrMerge([
        this.#packsSrv.pack.loading$(),
        this.#packsSrv.packItems.loading$()
    ]);

    ngOnInit(): void {
        this.#packsSrv.pack.load(this.$channel().id, this.$pack().id);
        this.#packsSrv.packItems.load(this.$channel().id, this.$pack().id);
    }

    delete(item: PackItem): void {
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
            delete$: this.#packsSrv.packItems.delete(this.$channel().id, this.$pack().id, item.id).pipe(
                tap(() => this.#packsSrv.packItems.load(this.$channel().id, this.$pack().id)))
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
            channelId: this.$channel().id,
            packType: this.$pack().type,
            packId: this.$pack().id,
            mainSession: this.$mainSession(),
            entityId: this.$channel().entity.id,
            currentItemsIds: this.#currentItemsIds
        })).beforeClosed()
            .subscribe((packElementId: number) => {
                if (packElementId) {
                    this.#ephemeralMsg.showSuccess({
                        msgKey: 'CHANNELS.PACKS.ELEMENTS.CREATE.CREATE_SUCCESS'
                    });
                    this.#packsSrv.packItems.load(this.$channel().id, this.$pack().id);
                }
            });
    }
}
