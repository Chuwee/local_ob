import { NotNumberedZone, Seat, SeatLinkable, SeatStatus, VenueTemplateItemType } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { DOCUMENT, formatNumber } from '@angular/common';
import { Directive, Inject, OnDestroy, OnInit, Renderer2 } from '@angular/core';
import { MatTooltip, TOOLTIP_PANEL_CLASS } from '@angular/material/tooltip';
import { TranslateService } from '@ngx-translate/core';
import { debounceTime, Subject } from 'rxjs';
import { takeUntil, tap } from 'rxjs/operators';
import { VenueTemplateLabelGroup } from '../../models/label-group/venue-template-label-group-list.model';
import { VenueTemplateLabelGroupType } from '../../models/label-group/venue-template-label-group-type.enum';
import { StandardVenueTemplateBaseService } from '../../services/standard-venue-template-base.service';
import { VenueTemplateSVGComponent } from '../svg/venue-template-svg.component';
import { VenueTemplateViewService } from '../venue-template-view.service';

// Only for venue template view
@Directive({
    standalone: true,
    exportAs: 'appVenueTplViewTooltip',
    selector: '[appVenueTplViewTooltip]'
})
export class VenueTemplateViewTooltipDirective implements OnInit, OnDestroy {
    private readonly _onDestroy: Subject<void> = new Subject<void>();
    private readonly SET_TOOLTIP_DELAY = 500;
    private readonly SEAT_LABEL = this._translateSrv.instant('VENUE_TPLS.SEAT');
    private readonly ROW_LABEL = this._translateSrv.instant('VENUE_TPLS.ROW');
    private readonly SECTOR_LABEL = this._translateSrv.instant('VENUE_TPLS.SECTOR');
    private _labelGroups: VenueTemplateLabelGroup[];
    private _tooltipActive = false;

    constructor(
        @Inject(DOCUMENT) private _document: Document,
        private _renderer: Renderer2,
        private _matTooltip: MatTooltip,
        private _translateSrv: TranslateService,
        private _venueTemplateSVGComponent: VenueTemplateSVGComponent,
        private _venueTemplateViewServiceSrv: VenueTemplateViewService,
        private _standardVenueTplSrv: StandardVenueTemplateBaseService
    ) {
    }

    ngOnInit(): void {
        if (this._matTooltip) {
            // LABEL GROUPS
            this._standardVenueTplSrv.getLabelGroups$().pipe(takeUntil(this._onDestroy))
                .subscribe(labelGroups => this._labelGroups = labelGroups);
            //ELEMENT IN
            this._venueTemplateSVGComponent.elementMouseIn
                .pipe(
                    tap(() => this._tooltipActive = true),
                    debounceTime(this.SET_TOOLTIP_DELAY),
                    takeUntil(this._onDestroy)
                )
                .subscribe(svgEvent => this.mouseInHandler(svgEvent.element));
            //ELEMENT OUT
            this._venueTemplateSVGComponent.elementMouseOut.pipe(takeUntil(this._onDestroy))
                .subscribe(() => {
                    this._tooltipActive = false;
                    this._matTooltip.disabled = true;
                    this._matTooltip.message = '';
                });

        }
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    private mouseInHandler(element: SVGElement): void {
        if (this._tooltipActive) {
            this._venueTemplateViewServiceSrv.getTemplateElementItem(element)
                .subscribe(item => {
                    if (item) {
                        if (item.itemType === VenueTemplateItemType.seat) {
                            this.processTooltip(element, () => this.setSeatTooltip(item));
                        } else if (item.itemType === VenueTemplateItemType.notNumberedZone) {
                            this.processTooltip(element, () => this.setNNZTooltip(item));
                        }
                    }
                });
        }
    }

    private processTooltip(element: SVGElement, tooltipUpdater: () => void): void {
        this._matTooltip.disabled = false;
        tooltipUpdater();
        this._matTooltip.show();
        setTimeout(() => {
            const matTooltipElement = this._document.querySelector('.' + TOOLTIP_PANEL_CLASS);
            const left = (element: Element): number => element.getBoundingClientRect().left;
            const top = (element: Element): number => element.getBoundingClientRect().top;
            const style = (element: Element, style: string, value: string): void => this._renderer.setStyle(element, style, value);
            if (matTooltipElement) {
                style(matTooltipElement, 'position', 'fixed');
                style(matTooltipElement, 'left', left(element) + 'px');
                style(matTooltipElement, 'top', top(element) + 'px');
            }
        });
    }

    private setSeatTooltip(seat: Seat): void {
        this._matTooltip.message =
            this.ROW_LABEL + ': ' + seat.rowName +
            '\n' + this.SEAT_LABEL + ': ' + seat.name +
            '\n' + this.SECTOR_LABEL + ': ' + seat.sectorName +
            (seat.notAssignableReason ?
                '\n' + this._translateSrv.instant('SEASON_TICKET.NOT_LINKED_REASON.' + seat.notAssignableReason) : '');
    }

    private setNNZTooltip(nnz: NotNumberedZone): void {
        const result: string[] = [
            nnz.name + ' (' + this.formatNum(nnz.capacity) + ')',
            this.SECTOR_LABEL + ': ' + nnz.sectorName
        ];
        result.push(this.getNNZCounterLine(VenueTemplateLabelGroupType.seasonTicketLinkable, SeatLinkable.linkable, nnz.linkableSeats));
        nnz.statusCounters
            .filter(sc => sc.count && sc.status !== SeatStatus.promotorLocked)
            .forEach(sc => result.push(this.getNNZCounterLine(VenueTemplateLabelGroupType.state, sc.status, sc.count)));
        nnz.blockingReasonCounters
            ?.filter(bc => bc.count)
            .forEach(bc =>
                result.push(this.getNNZCounterLine(VenueTemplateLabelGroupType.blockingReason, String(bc.blocking_reason), bc.count)));
        result.push(this.getNNZAspectLine(VenueTemplateLabelGroupType.priceType, nnz.priceType));
        if (nnz.quotaCounters?.length > 1) {
            result.push(this.getNNZCounterLinesWithLabelGroupLiteral(
                VenueTemplateLabelGroupType.quota,
                nnz.quotaCounters
                    ?.filter(qc => qc.count)
                    .map(qc => [qc.quota, qc.count])
            ));
        }
        if (nnz.quotaCounters?.length === 1) {
            result.push(this.getNNZAspectLine(VenueTemplateLabelGroupType.quota, nnz.quotaCounters[0].quota));
        }
        result.push(this.getNNZAspectLine(VenueTemplateLabelGroupType.visibility, nnz.visibility));
        result.push(this.getNNZAspectLine(VenueTemplateLabelGroupType.accessibility, nnz.accessibility));
        result.push(this.getNNZAspectLine(VenueTemplateLabelGroupType.gate, nnz.gate));
        if (nnz.firstCustomTag) {
            result.push(this.getNNZAspectLine(VenueTemplateLabelGroupType.firstCustomLabelGroup, nnz.firstCustomTag));
        }
        if (nnz.secondCustomTag) {
            result.push(this.getNNZAspectLine(VenueTemplateLabelGroupType.secondCustomLabelGroup, nnz.secondCustomTag));
        }
        this._matTooltip.message = result.filter(value => !!value).join('\n');
    }

    private getNNZCounterLine(
        labelGroupType: VenueTemplateLabelGroupType, labelId: number | string, count: number
    ): string {
        const labelGroup = this._labelGroups
            .find(lg => lg.id === labelGroupType);
        if (!labelGroup) {
            return null;
        } else {
            return (labelGroup.labels.find(label => label.id === String(labelId))
                ?.literal ?? (labelGroupType + ' not found: ' + labelId))
                + ': '
                + this.formatNum(count);
        }
    }

    private getNNZCounterLinesWithLabelGroupLiteral(
        labelGroupType: VenueTemplateLabelGroupType,
        counters: [number | string, number][]
    ): string {
        const labelGroup = this._labelGroups.find(lg => lg.id === labelGroupType);
        if (!labelGroup) {
            return null;
        }
        const getNNZCounterLine = (labelId: number | string, count: number): string =>
            (labelGroup.labels.find(label => label.id === String(labelId))
                ?.literal ?? (labelGroupType + ' not found: ' + labelId))
            + ' ('
            + this.formatNum(count) + ')';

        let result = this._translateSrv.instant(labelGroup.literalKey) + ': ';
        const [[firstLabelId, firstCounter], ...restCounters] = counters;
        result += getNNZCounterLine(firstLabelId, firstCounter);
        restCounters.forEach(([labelId, count]) => {
            result += ', ' + getNNZCounterLine(labelId, count);
        });
        return result;
    }

    private getNNZAspectLine(
        labelGroupType: VenueTemplateLabelGroupType, labelId: number | string
    ): string {
        const labelGroup = this._labelGroups.find(lg => lg.id === labelGroupType);
        if (labelGroup) {
            return (labelGroup.custom ? labelGroup.customName : this._translateSrv.instant(labelGroup.literalKey)) + ': '
                + (labelGroup.labels.find(label => label.id === String(labelId))?.literal ?? 'not found');
        } else {
            return '';
        }
    }

    private formatNum(value: number): string {
        return formatNumber(value, this._translateSrv.getCurrentLang(), '.0-2');

    }
}
