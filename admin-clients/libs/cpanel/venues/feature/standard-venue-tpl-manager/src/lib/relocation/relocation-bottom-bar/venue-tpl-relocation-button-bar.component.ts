import { DialogSize, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { NotNumberedZone, Seat } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { ChangeDetectionStrategy, Component, inject, input, output } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, of, tap } from 'rxjs';
import { first, map, switchMap, filter } from 'rxjs/operators';
import { VenueTemplateStartRelocation, VenueTemplateActionType } from '../../models/venue-template-action.model';
import { StandardVenueTemplateBaseService } from '../../services/standard-venue-template-base.service';

@Component({
    selector: 'app-venue-template-relocation-button-bar',
    imports: [TranslatePipe, MaterialModule],
    templateUrl: './venue-tpl-relocation-button-bar.component.html',
    styleUrl: './venue-tpl-relocation-button-bar.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class VenueTemplateRelocationButtonBarComponent {
    readonly #standardVenueTemplateSrv = inject(StandardVenueTemplateBaseService);
    readonly #msgDialogService = inject(MessageDialogService);

    readonly $selectedDestinationSeats = input<{ [originId: number]: number }>(null, { alias: 'selectedDestinationSeats' });
    readonly $stopRelocation = output({ alias: 'stopRelocation' });
    readonly $relocationStatus = toSignal(this.#standardVenueTemplateSrv.relocation.getStatus$());

    cancelRelocation(): void {
        this.#msgDialogService.showWarn({
            size: DialogSize.MEDIUM,
            title: 'VENUE_TPLS.TITLES.DIALOG_CANCEL_RELOCATION',
            message: 'VENUE_TPLS.FORMS.INFOS.DIALOG_CANCEL_RELOCATION',
            actionLabel: 'FORMS.ACTIONS.CANCEL_RELOCATION',
            cancelLabel: 'FORMS.ACTIONS.GO_BACK'
        })
            .pipe(filter(Boolean))
            .subscribe(() => this.$stopRelocation.emit());
    }

    setActiveStep(activeStep: number): void {
        this.#standardVenueTemplateSrv.relocation.updateStatus({ activeStep });
    }

    startRelocation(): void {
        combineLatest([
            of(this.$selectedDestinationSeats()).pipe(filter(Boolean)),
            this.#standardVenueTemplateSrv.getVenueItems$()
        ])
            .pipe(
                first(),
                // If the price type between origin and destination seats is different, show a warning
                tap(([destinationSeats, venueItems]) => this.#validateDifferentPriceTypes(destinationSeats, venueItems)),
                map(([destinationSeats, venueItems]) =>
                    Object.entries(destinationSeats).map(([sourceId, destinationId]) =>
                    ({
                        source_id: venueItems.seats?.get(Number(sourceId))?.ticketId,
                        destination_id: venueItems.seats?.get(destinationId)?.ticketId
                    }))),
                switchMap(relocationSeats => combineLatest([
                    of(relocationSeats),
                    this.#msgDialogService.showWarn({
                        size: DialogSize.MEDIUM,
                        title: 'VENUE_TPLS.TITLES.DIALOG_START_RELOCATION',
                        message: 'VENUE_TPLS.FORMS.INFOS.DIALOG_START_RELOCATION',
                        actionLabel: 'FORMS.ACTIONS.RELOCATION',
                        messageParams: { seatCount: relocationSeats.length }
                    })
                ])),
                filter(vals => vals.every(Boolean))
            ).subscribe(([relocationSeats]) => {
                this.#standardVenueTemplateSrv.emitAction({
                    type: VenueTemplateActionType.startRelocation,
                    data: relocationSeats
                } as VenueTemplateStartRelocation);
                this.$stopRelocation.emit();
            });
    }

    #validateDifferentPriceTypes(destinationSeats: { [originId: number]: number },
        venueItems: { seats: Map<number, Seat>; nnzs: Map<number, NotNumberedZone> }): void {
        const sameRelocationPriceTypes = Object.entries(destinationSeats)
            .every(([sourceId, destinationId]) =>
                venueItems.seats.get(Number(sourceId)).priceType === venueItems.seats.get(Number(destinationId)).priceType);

        if (!sameRelocationPriceTypes) {
            this.#msgDialogService.showWarn({
                size: DialogSize.MEDIUM,
                title: 'VENUE_TPLS.TITLES.DIFFERENT_PRICE_TYPES',
                message: 'VENUE_TPLS.FORMS.INFOS.DIFFERENT_PRICE_TYPES',
                actionLabel: 'FORMS.ACTIONS.AGREED',
                showCancelButton: false
            });
        }
    }
}
