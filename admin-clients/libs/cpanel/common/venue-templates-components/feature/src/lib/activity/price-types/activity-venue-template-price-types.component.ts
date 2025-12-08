import { DialogSize, EphemeralMessageService, MessageDialogService, ObMatDialogConfig } from '@admin-clients/shared/common/ui/components';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { VenueTemplatePriceType, VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, EventEmitter, inject, Input, OnInit, Output } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatList, MatListItem } from '@angular/material/list';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { filter } from 'rxjs/operators';
import { ActivityVenueTemplatePriceTypesDialogComponent } from './price-types-dialog/activity-venue-template-price-types-dialog.component';
import { PriceTypesDataMode } from './price-types-dialog/price-types-dialog-data';

@Component({
    selector: 'app-activity-venue-template-price-types',
    templateUrl: './activity-venue-template-price-types.component.html',
    styleUrls: ['./activity-venue-template-price-types.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        AsyncPipe, TranslatePipe, ReactiveFormsModule, FlexLayoutModule, MatTooltip, MatIcon, MatList,
        MatListItem, MatButton, MatIconButton
    ]
})
export class ActivityVenueTemplatePriceTypesComponent implements OnInit {
    readonly #onDestroy = inject(DestroyRef);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);
    readonly #msgDialogService = inject(MessageDialogService);
    readonly #venueTemplatesSrv = inject(VenueTemplatesService);
    readonly #venueTpl$ = this.#venueTemplatesSrv.venueTpl.get$();
    readonly #dialog = inject(MatDialog);
    #templateId: number;
    priceTypes$: Observable<VenueTemplatePriceType[]>;
    @Output() dataChanged = new EventEmitter<void>();
    @Input() isReadOnly: boolean = false;
    @Input() isSga: boolean = false;

    requestsInProgress$: Observable<boolean>;

    ngOnInit(): void {
        this.priceTypes$ = this.#venueTemplatesSrv.getVenueTemplatePriceTypes$();
        this.#venueTpl$
            .pipe(filter(Boolean), takeUntilDestroyed(this.#onDestroy))
            .subscribe(venueTemplate => {
                this.#templateId = venueTemplate.id;
                this.#venueTemplatesSrv.clearVenueTemplatePriceTypes();
                this.#venueTemplatesSrv.loadVenueTemplatePriceTypes(venueTemplate.id);
            });
        this.requestsInProgress$ = booleanOrMerge([
            this.#venueTemplatesSrv.isVenueTemplatePriceTypesLoading$(),
            this.#venueTemplatesSrv.isVenueTemplatePriceTypeSaving$()
        ]);
    }

    addNewPriceType(): void {
        this.#dialog.open<ActivityVenueTemplatePriceTypesDialogComponent>(ActivityVenueTemplatePriceTypesDialogComponent,
            new ObMatDialogConfig({
                templateId: this.#templateId,
                mode: PriceTypesDataMode.creation
            })).beforeClosed()
            .subscribe(success => {
                if (success) this.#venueTemplatesSrv.loadVenueTemplatePriceTypes(this.#templateId);
            });
    }

    editPriceType(priceType: VenueTemplatePriceType): void {
        this.#dialog.open<ActivityVenueTemplatePriceTypesDialogComponent>(
            ActivityVenueTemplatePriceTypesDialogComponent,
            new ObMatDialogConfig({
                name: priceType.name,
                code: priceType.code,
                id: priceType.id,
                templateId: this.#templateId,
                mode: PriceTypesDataMode.edition,
                isNameReadOnly: this.isReadOnly
            })
        )
            .beforeClosed()
            .subscribe(success => {
                if (success) {
                    this.#venueTemplatesSrv.loadVenueTemplatePriceTypes(this.#templateId);
                }
            });
    }

    deletePriceType(priceType: VenueTemplatePriceType): void {
        this.#msgDialogService.showWarn({
            size: DialogSize.SMALL,
            title: 'TITLES.WARNING',
            message: 'VENUE_TPLS.DELETE_TICKET_TYPE_WARNING',
            messageParams: { name: priceType.name }
        })
            .subscribe(success => {
                if (success) {
                    this.#venueTemplatesSrv.deleteVenueTemplatePriceType(this.#templateId, String(priceType.id))
                        .subscribe(() => {
                            this.#ephemeralMessageService.showSuccess({
                                msgKey: 'VENUE_TPLS.DELETE_TICKET_TYPE_SUCCESS',
                                msgParams: { name: priceType.name }
                            });
                            this.#venueTemplatesSrv.loadVenueTemplatePriceTypes(this.#templateId);
                            this.dataChanged.emit();
                        });
                }
            });
    }
}
