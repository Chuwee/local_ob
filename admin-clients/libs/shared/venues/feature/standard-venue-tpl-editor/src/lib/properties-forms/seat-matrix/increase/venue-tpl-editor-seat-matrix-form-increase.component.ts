import { SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { SharedUtilityDirectivesModule } from '@admin-clients/shared/utility/directives';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { Box, Svg, SVG } from '@svgdotjs/svg.js';
import { combineLatest, distinctUntilChanged, first, Subject } from 'rxjs';
import { debounceTime, map, shareReplay, takeUntil, withLatestFrom } from 'rxjs/operators';
import { SVGDefs } from '../../../models/SVGDefs.enum';
import {
    SeatMatrixConfRowDirection, SeatMatrixConfSeatDirection, venueTplEditorSeatMatrixConfInitValue, venueTplEditorSeatMatrixLimits
} from '../../../models/venue-tpl-editor-seat-matrix-conf.model';
import { EdRow, EdSector } from '../../../models/venue-tpl-editor-venue-map-items.model';
import { getDistanceBetween } from '../../../utils/geometry.utils';
import { VenueTplEditorDomService } from '../../../venue-tpl-editor-dom.service';
import { VenueTplEditorSeatMatrixService } from '../../../venue-tpl-editor-seat-matrix.service';
import { VenueTplEditorVenueMapService } from '../../../venue-tpl-editor-venue-map.service';
import { VenueTplEditorViewsService } from '../../../venue-tpl-editor-views.service';

@Component({
    imports: [
        CommonModule,
        MaterialModule,
        FlexLayoutModule,
        TranslatePipe,
        ReactiveFormsModule,
        SharedUtilityDirectivesModule,
        SelectSearchComponent
    ],
    selector: 'app-venue-tpl-editor-seat-matrix-form-increase',
    templateUrl: './venue-tpl-editor-seat-matrix-form-increase.component.html',
    styleUrls: ['../../venue-tpl-editor-properties-forms-common-styles.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class VenueTplEditorSeatMatrixFormIncreaseComponent implements OnInit, OnDestroy {

    private readonly _onDestroy = new Subject<void>();

    private readonly _fb = inject(FormBuilder);
    private readonly _venueMapSrv = inject(VenueTplEditorVenueMapService);
    private readonly _viewsSrv = inject(VenueTplEditorViewsService);
    private readonly _domSrv = inject(VenueTplEditorDomService);
    private readonly _seatMatrixSrv = inject(VenueTplEditorSeatMatrixService);

    readonly rowDirections = Object.values(SeatMatrixConfRowDirection);
    readonly seatDirections = Object.values(SeatMatrixConfSeatDirection);

    @ViewChild('fromRowSearch')
    readonly fromRowSearchComponent: SelectSearchComponent<EdRow>;

    @ViewChild('toRowSearch')
    readonly toRowSearchComponent: SelectSearchComponent<EdRow>;

    readonly form = this._fb.group({
        sector: [0, [Validators.required]],
        seats: [5, [Validators.required, Validators.min(1), Validators.max(venueTplEditorSeatMatrixLimits.seats)]],
        fromRow: [0, Validators.required],
        toRow: [0, Validators.required],
        rowsDirection: [null as SeatMatrixConfRowDirection],
        seatsDirection: [null as SeatMatrixConfSeatDirection],
        seatsSize: [0, [Validators.required, Validators.min(1), Validators.max(venueTplEditorSeatMatrixLimits.size)]],
        seatsDistance: [0, [Validators.required, Validators.min(1), Validators.max(venueTplEditorSeatMatrixLimits.seatsDistance)]],
        rowsDistance: [0, [Validators.required, Validators.min(1), Validators.max(venueTplEditorSeatMatrixLimits.rowsDistance)]]
    });

    readonly sectors$ = combineLatest([
        this._venueMapSrv.getVenueMap$(),
        this._viewsSrv.getViewData$().pipe(map(viewData => viewData.view.id))
    ])
        .pipe(
            map(([venueMap, viewId]) =>
                venueMap.sectors.filter(sector =>
                    !sector.delete
                    && sector.rows
                        .filter(row => !row.delete)
                        .flatMap(row => row.seats)
                        .filter(seat => !seat.delete)
                        .some(seat => seat.view === viewId)
                )
            ),
            shareReplay({ refCount: true, bufferSize: 1 })
        );

    readonly rows$ = this._seatMatrixSrv.getSeatMatrixConf$()
        .pipe(
            map(conf => conf.rowContinuation.sector),
            distinctUntilChanged(),
            withLatestFrom(
                this._venueMapSrv.getVenueItems$(),
                this._viewsSrv.getViewData$().pipe(map(viewData => viewData.view.id))
            ),
            map(([sectorId, venueItems, viewId]) => this.getViewRows(venueItems.sectors.get(sectorId), viewId)),
            shareReplay({ refCount: true, bufferSize: 1 })
        );

    ngOnInit(): void {
        //form data incoming
        this._seatMatrixSrv.getSeatMatrixConf$().pipe(takeUntil(this._onDestroy))
            .subscribe(config => this.form.setValue(config.rowContinuation, { emitEvent: false }));
        // form data outgoing
        this.form.valueChanges
            .pipe(
                debounceTime(0),
                takeUntil(this._onDestroy)
            )
            .subscribe(() => {
                this._seatMatrixSrv.fixMinMaxFormValues(this.form);
                if (this.form.valid) {
                    this._seatMatrixSrv.mergeSeatMatrixConf({ rowContinuation: this.form.getRawValue() });
                }
            });
        // row auto selection on sector change
        this.form.controls.sector.valueChanges
            .pipe(
                withLatestFrom(this._venueMapSrv.getVenueItems$(), this._viewsSrv.getViewData$()),
                takeUntil(this._onDestroy)
            )
            .subscribe(([sectorId, venueItems, viewData]) => {
                const rows = this.getViewRows(venueItems.sectors.get(sectorId), viewData.view.id);
                if (rows?.length) {
                    this.form.patchValue({
                        fromRow: rows[0].id,
                        toRow: rows[rows.length - 1].id
                    });
                } else {
                    this.form.patchValue({ fromRow: null, toRow: null });
                }
            });
        // seats size and margins auto set
        combineLatest([
            this.form.controls.sector.valueChanges,
            this.form.controls.fromRow.valueChanges,
            this.form.controls.toRow.valueChanges
        ])
            .pipe(
                debounceTime(0),
                withLatestFrom(
                    this._domSrv.getSvgSvgElement$(),
                    this._venueMapSrv.getVenueItems$(),
                    this._viewsSrv.getSvgData$()
                ),
                takeUntil(this._onDestroy)
            )
            .subscribe(([[sectorId, fromRowId, toRowId], svgSvgElement, items, svgData]) => {
                const rows = this.getViewRows(items.sectors.get(sectorId), svgData.viewId);
                rows.sort(row => row.order);
                const fromRowIndex = rows.indexOf(rows.find(row => row.id === fromRowId));
                const toRowIndex = rows.indexOf(rows.find(row => row.id === toRowId));
                if (fromRowIndex >= 0 && toRowIndex >= 0) {
                    const selectedRows = rows
                        .slice(Math.min(fromRowIndex, toRowIndex), Math.max(fromRowIndex, toRowIndex) + 1)
                        .filter(row => !row.delete);
                    const svgEl = SVG(svgSvgElement);
                    this.form.patchValue({
                        seatsSize: this.getSeatSize(selectedRows[0], svgEl),
                        seatsDistance: this.getRowSeatsDistance(selectedRows[0], svgEl),
                        rowsDistance: this.getRowDistance(selectedRows, svgEl)
                    });
                } else {
                    this.form.patchValue({ seatsSize: null, seatsDistance: null, rowsDistance: null });
                }
            });
        // initial sector selection
        this.sectors$.pipe(first(sectors => !!sectors?.length))
            .subscribe(sectors => this.form.controls.sector.setValue(sectors[0].id));
    }

    ngOnDestroy(): void {
        this._onDestroy.next();
        this._onDestroy.complete();
    }

    private getViewRows(sector: EdSector, viewId: number): EdRow[] {
        return sector?.rows
            ?.filter(row => !row.delete && row.seats.some(seat => !seat.delete && seat.view === viewId))
            ?.sort((a, b) => a.order - b.order)
            ?? [];
    }

    private getSeatSize(row: EdRow, svg: Svg): number {
        const seats = row.seats.filter(seat => !seat.delete).sort((a, b) => a.order - b.order);
        return Math.round(this.getSeatCoords(seats[seats.length - 1].id, svg).width);
    }

    private getRowSeatsDistance(row: EdRow, svg: Svg): number {
        const seats = row.seats.filter(seat => !seat.delete).sort((a, b) => a.order - b.order);
        if (seats.length === 1) {
            return Math.round(this.getSeatSize(row, svg) * .6);
        } else {
            const seat1Coords = this.getSeatCoords(seats[seats.length - 1].id, svg);
            const seat2Coords = this.getSeatCoords(seats[seats.length - 2].id, svg);
            return Math.round(getDistanceBetween(seat1Coords, seat2Coords) - seat1Coords.width);
        }
    }

    private getRowDistance(rows: EdRow[], svg: Svg): number {
        let result = venueTplEditorSeatMatrixConfInitValue.rowContinuation.rowsDistance;
        if (rows.length > 1) {
            let minDistance: number = null;
            rows[0].seats
                .filter(seat => !seat.delete)
                .forEach(firstRowSeat => {
                    const seat1Coords = this.getSeatCoords(firstRowSeat.id, svg);
                    rows[1].seats
                        .filter(seat => !seat.delete)
                        .forEach(secondRowSeat => {
                            const seat2Coords = this.getSeatCoords(secondRowSeat.id, svg);
                            const rowsDistance = Math.round(getDistanceBetween(seat1Coords, seat2Coords));
                            if (minDistance === null || minDistance > rowsDistance) {
                                minDistance = rowsDistance;
                            }
                        });
                });
            if (minDistance) {
                result = Math.round(minDistance - this.getSeatSize(rows[0], svg));
            }
        }
        return result;
    }

    private getSeatCoords(seatId: number, svg: Svg): Box {
        const stringId = String(seatId);
        return Array.from(svg.children())
            .find(child => child.node.id === stringId && child.hasClass(SVGDefs.classes.interactive))
            ?.rbox(svg);
    }
}
