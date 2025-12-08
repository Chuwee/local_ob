import { EventTiers, EventTiersService } from '@admin-clients/cpanel-promoters-events-prices-data-access';
import { ObMatDialogConfig } from '@admin-clients/shared/common/ui/components';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { TiersCommunicationDialogComponent } from './tiers-communication-dialog/tiers-communication-dialog.component';

@Component({
    selector: 'app-tiers-communication-table',
    templateUrl: './tiers-communication-table.component.html',
    styleUrls: ['./tiers-communication-table.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class TiersCommunicationTableComponent implements OnInit {

    isInProgress$: Observable<boolean>;
    eventTiers$: Observable<EventTiers[]>;
    tableHead = ['price_zone', 'tiers_name', 'actions'];

    @Input() eventId: string;

    constructor(
        private _eventTiersService: EventTiersService,
        private _matDialog: MatDialog
    ) {
    }

    ngOnInit(): void {
        this.isInProgress$ = booleanOrMerge([
            this._eventTiersService.isEventTiersListInProgress$(),
            this._eventTiersService.isEventTiersListSaveInProgress$()
        ]);
        this.eventTiers$ = this._eventTiersService.getEventTiersListData$();
    }

    openTranslationsDialog(eventTier: EventTiers): void {
        const data = {
            tierId: eventTier.id.toString(),
            eventId: this.eventId,
            priceTypeTitle: eventTier.price_type.name,
            tierName: eventTier.name
        };
        this._matDialog.open(TiersCommunicationDialogComponent, new ObMatDialogConfig(data))
            .beforeClosed().subscribe();
    }

}
