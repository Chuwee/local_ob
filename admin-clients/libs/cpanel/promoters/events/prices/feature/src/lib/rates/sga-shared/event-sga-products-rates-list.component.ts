import { Event, PutRateGroup } from '@admin-clients/cpanel/promoters/events/data-access';
import { VmEventRate } from '@admin-clients/cpanel-promoters-events-prices-data-access';
import { ExternalInventoryProviders } from '@admin-clients/shared/common/data-access';
import { ObMatDialogConfig } from '@admin-clients/shared/common/ui/components';
import { CdkDragDrop, DragDropModule, moveItemInArray } from '@angular/cdk/drag-drop';
import { ChangeDetectionStrategy, Component, inject, input, output } from '@angular/core';
import { MatButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatList, MatListItem } from '@angular/material/list';
import { ActivatedRoute } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { TranslateRatesDialogComponent } from '../list/translate-rates-dialog/translate-rates-dialog.component';

@Component({
    selector: 'app-event-sga-products-rates-list',
    templateUrl: './event-sga-products-rates-list.component.html',
    imports: [
        MatIcon, TranslatePipe, MatButton, MatList, MatListItem, DragDropModule
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EventSgaProductsRatesListComponent {
    readonly #matDialog = inject(MatDialog);
    readonly #route = inject(ActivatedRoute);

    readonly $event = input.required<Event>({ alias: 'event' });
    readonly $sortedListData = input<VmEventRate[]>([], { alias: 'sortedListData' });
    readonly listDescription = input<string>('EVENTS.SGA_PRODUCTS_LIST_DESCRIPTION');

    readonly orderListChanged = output<PutRateGroup[]>();
    readonly translateDialogSaved = output<boolean>();

    openTranslateDialog(): void {
        const data = {
            eventId: this.$event().id,
            languages: this.$event().settings.languages.selected,
            rates: this.$sortedListData(),
            isSga: this.$event().additional_config?.inventory_provider === ExternalInventoryProviders.sga,
            isProducts: this.#route.snapshot.routeConfig.path === 'sga-products'
        };
        this.#matDialog.open(TranslateRatesDialogComponent, new ObMatDialogConfig(data))
            .beforeClosed()
            .subscribe(isSaved => {
                if (isSaved) {
                    this.translateDialogSaved.emit(true);
                }
            });
    }

    onListDrop(event: CdkDragDrop<PutRateGroup[]>): void {
        const positionHasChanged = event.currentIndex !== event.previousIndex;
        if (positionHasChanged) {
            moveItemInArray(this.$sortedListData(), event.previousIndex, event.currentIndex);
            const orderedProducts = event.container.data.map((product, index) => ({ ...product, position: index }));
            this.orderListChanged.emit(orderedProducts);
        }
    }
}
