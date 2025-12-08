import { DialogSize, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { VenueTemplateItemType } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { coerceBooleanProperty } from '@angular/cdk/coercion';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, ElementRef, inject, Input, viewChild } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { G, SVG } from '@svgdotjs/svg.js';
import { combineLatest } from 'rxjs';
import { debounceTime, map, shareReplay, take, withLatestFrom } from 'rxjs/operators';
import { NewRowsAction } from '../../../actions/new-rows-action';
import { NewSeatsAction } from '../../../actions/new-seats-action';
import { SVGDefs } from '../../../models/SVGDefs.enum';
import { EditorMode } from '../../../models/venue-tpl-editor-modes.enum';
import {
    SeatMatrixConfNumerationType, SeatMatrixConfRangeType, SeatMatrixConfRowDirection, SeatMatrixConfRowLabelPosition,
    SeatMatrixConfSeatDirection, VenueTplEditorSeatMatrixConf
} from '../../../models/venue-tpl-editor-seat-matrix-conf.model';
import { EdRow, EdSeat, EdVenueMap } from '../../../models/venue-tpl-editor-venue-map-items.model';
import { IdGenerator } from '../../../utils/editor-id-generator.utils';
import { maxWeightValue } from '../../../utils/seat-weights.utils';
import { VenueTplEditorDomService } from '../../../venue-tpl-editor-dom.service';
import { VenueTplEditorSeatMatrixService } from '../../../venue-tpl-editor-seat-matrix.service';
import { VenueTplEditorSelectionService } from '../../../venue-tpl-editor-selection.service';
import { VenueTplEditorVenueMapService } from '../../../venue-tpl-editor-venue-map.service';
import { VenueTplEditorViewsService } from '../../../venue-tpl-editor-views.service';
import { VenueTplEditorService } from '../../../venue-tpl-editor.service';
import { RotateControlComponent } from '../controls/rotate/rotate-control.component';
import { ShapeControlDataManager } from '../shape-control-data-manager';
import { VenueTplEditorSvgModifier } from '../venue-tpl-editor-svg-modifier';

interface SeatMatrixViewConfiguration {
    rows: EdRow[];
    padding: number;
    rect: { width: number; height: number; offsetX: number; offsetY: number };
    labelFontSize: number;
    leftLabelsWidth: number;
    interactiveRowLabels: boolean;
    rightLabelsWidth: number;
    labelDistance: number;
    seatsSize: number;
    seatsDistance: number;
    rowsDistance: number;
    pos: { x: number; y: number };
    angle: number;
    invScale: number;
}

@Component({
    imports: [
        CommonModule,
        MaterialModule,
        FlexLayoutModule,
        RotateControlComponent
    ],
    selector: 'app-venue-tpl-editor-seat-matrix',
    templateUrl: './venue-tpl-editor-seat-matrix.component.html',
    styleUrls: ['./venue-tpl-editor-seat-matrix.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class VenueTplEditorSeatMatrixComponent extends VenueTplEditorSvgModifier {

    readonly #editorSrv = inject(VenueTplEditorService);
    readonly #viewsSrv = inject(VenueTplEditorViewsService);
    readonly #domSrv = inject(VenueTplEditorDomService);
    readonly #venueMapSrv = inject(VenueTplEditorVenueMapService);
    readonly #selectionSrv = inject(VenueTplEditorSelectionService);
    readonly #seatMatrixSrv = inject(VenueTplEditorSeatMatrixService);
    readonly #msgDialogSrv = inject(MessageDialogService);

    private readonly _svg = viewChild<ElementRef<SVGSVGElement>>('svg');
    private readonly _placerGroup = viewChild<ElementRef<SVGSVGElement>>('placerGroup');
    private readonly _contentGroup = viewChild<ElementRef<SVGSVGElement>>('contentGroup');
    private readonly _seatsGroup = viewChild<ElementRef<SVGSVGElement>>('seatsGroup');

    #continueRowsMode = false;

    readonly shapeDrag = new ShapeControlDataManager({ x: 150, y: 100 });

    readonly interactiveClass = SVGDefs.classes.interactive;

    readonly viewBox$ = this.#domSrv.getSvgSvgElementViewBox$();

    readonly viewConf$ = combineLatest([
        this.#seatMatrixSrv.getSeatMatrixConf$()
            .pipe(
                debounceTime(1),
                withLatestFrom(this.#venueMapSrv.getVenueMap$(), this.#viewsSrv.getViewData$()),
                map(([conf, venueMap, viewData]) => {
                    const rows = this.#getSeatMatrixTempRows(conf, venueMap, viewData.view.id);
                    if (rows?.length) {
                        if (!this.#continueRowsMode) {
                            return this.#generateMatrixAppearanceConfiguration(
                                rows,
                                conf.matrix.seats, conf.matrix.seatsSize,
                                conf.matrix.seatsDistance, conf.matrix.rowsDistance,
                                conf.rows.show, conf.rows.position
                            );
                        } else {
                            return this.#generateMatrixAppearanceConfiguration(
                                rows,
                                conf.rowContinuation.seats, conf.rowContinuation.seatsSize,
                                conf.rowContinuation.seatsDistance, conf.rowContinuation.rowsDistance,
                                true, SeatMatrixConfRowLabelPosition.left
                            );
                        }
                    } else {
                        return null;
                    }
                })
            ),
        this.shapeDrag.controlData$
    ])
        .pipe(
            map(([viewConf, controlData]) =>
                this.#placeMatrixAppearanceConfiguration(viewConf, controlData.pos, controlData.angle, controlData.scale)
            ),
            shareReplay({ refCount: true, bufferSize: 1 })
        );

    @Input()
    set continueRowsMode(value: boolean) {
        this.#continueRowsMode = coerceBooleanProperty(value);
    }

    getSeatLabelOffset(seatNameLength: number): number {
        switch (seatNameLength) {
            case 1: return 0.28;
            case 2: return 0.08;
            default: return -0.05;
        }
    }

    // ITEM GENERATION

    #getSeatMatrixTempRows(config: VenueTplEditorSeatMatrixConf, venueMap: EdVenueMap, viewId: number): EdRow[] {
        if (config) {
            if (!this.#continueRowsMode && !config.continueRows) {
                return this.#getTempRows(config, venueMap, viewId);
            } else if (this.#continueRowsMode && config.continueRows) {
                return this.#getTempRowContinuations(config, venueMap, viewId);
            }
        }
        return [];
    }

    #getTempRows(config: VenueTplEditorSeatMatrixConf, venueMap: EdVenueMap, viewId: number): EdRow[] {
        const rows: EdRow[] = [];
        if (config && !config.continueRows) {
            const sector = venueMap.sectors.find(sector => sector.id === config.matrix.sector);
            const lastRowOrder = Math.max(...(sector?.rows?.map(row => row.order) ?? []), 0) + 1;
            const rowNames = this.#generateItemNamesCreation(
                config.matrix.rows,
                config.rows.range === SeatMatrixConfRangeType.numeric,
                config.rows.numeration,
                config.rows.numericStartsWith,
                config.rows.alphabeticStartsWith
            );
            config.seats.track1.seats = config.seats.numTracks === 1 ? config.matrix.seats : config.seats.track1.seats;
            const track1SeatNames = this.#generateItemNamesCreation(
                config.seats.track1.seats,
                config.seats.seatsRange === SeatMatrixConfRangeType.numeric,
                config.seats.track1.numeration,
                config.seats.track1.numericStartsWith,
                config.seats.track1.alphabeticStartsWith
            );
            const track2SeatNames = config.seats.numTracks === 1 ? [] : this.#generateItemNamesCreation(
                config.seats.track2.seats,
                config.seats.seatsRange === SeatMatrixConfRangeType.numeric,
                config.seats.track2.numeration,
                config.seats.track2.numericStartsWith,
                config.seats.track2.alphabeticStartsWith
            );
            const weightIncr = maxWeightValue / config.matrix.rows;
            let weight = maxWeightValue;
            for (let r = 0; r < config.matrix.rows; r++) {
                const row: EdRow = {
                    itemType: VenueTemplateItemType.row,
                    id: undefined,
                    name: (config.rows.prefix ?? '') + rowNames[r],
                    sector: config.matrix.sector,
                    order: r + lastRowOrder,
                    seats: [],
                    create: true
                };
                row.seats.push(...this.#generateRowSeats(
                    config.seats.track1.seats, 0, config.seats.track1.direction, track1SeatNames, 0, r, weight, viewId
                ));
                if (config.seats.numTracks === 2) {
                    row.seats.push(...this.#generateRowSeats(
                        config.seats.track2.seats, config.seats.track1.seats,
                        config.seats.track2.direction, track2SeatNames, 0, r, weight, viewId
                    ));
                }
                weight -= weightIncr;
                rows.push(row);
            }
            if (config.commitConfiguration && this.#filterExistentRows(rowNames, venueMap, viewId, config.matrix.sector)) {
                this.#commitConfiguration(rows);
            }
        }
        return config.rows.direction === SeatMatrixConfRowDirection.down ? rows : rows.reverse();
    }

    #getTempRowContinuations(config: VenueTplEditorSeatMatrixConf, venueMap: EdVenueMap, viewId: number): EdRow[] {
        if (config?.continueRows && config.rowContinuation.sector) {
            const sector = venueMap.sectors.find(sector => sector.id === config.rowContinuation.sector);
            const sectorRows = sector.rows.filter(row => !row.delete).sort((a, b) => a.order - b.order);
            // gets the index from the first and the last row to continue, and sorts it.
            const fromToRowsToContinueIndexes = [
                sectorRows.indexOf(sectorRows.find(r => r.id === config.rowContinuation.fromRow)),
                sectorRows.indexOf(sectorRows.find(r => r.id === config.rowContinuation.toRow))
            ].sort();
            // Gets the rows to continue
            const rowsToContinue = sectorRows.slice(fromToRowsToContinueIndexes[0], fromToRowsToContinueIndexes[1] + 1);
            // Creates the instances of the rows to continue, they are copies of
            // the original ones, with the same ids, and the new seats to create.
            const resultRows = rowsToContinue.map(row => this.#getTempRowContinuation(row, config, viewId)).filter(Boolean);
            // When commitConfiguration flag is true, it adds the new seats to the template, in the model and in the svg
            if (config.commitConfiguration) {
                this.#commitConfiguration(resultRows);
            }
            // for correct rendering, rows must be sorted reverse when up direction, it does not affect to model or final svg
            if (config.rowContinuation.rowsDirection === SeatMatrixConfRowDirection.down) {
                return resultRows;
            } else {
                return resultRows.reverse();
            }
        } else {
            return [];
        }
    }

    #getTempRowContinuation(row: EdRow, config: VenueTplEditorSeatMatrixConf, viewId: number): EdRow {
        const rowSeats = row.seats.filter(seat => !seat.delete).sort((a, b) => a.order - b.order);
        if (rowSeats?.length) {
            const lastSeat = rowSeats[rowSeats.length - 1];
            return {
                ...row,
                seats: this.#generateRowSeats(
                    config.rowContinuation.seats,
                    lastSeat.order,
                    config.rowContinuation.seatsDirection,
                    this.#generateItemNamesRowContinuation(lastSeat, rowSeats[rowSeats.length - 2], config.rowContinuation.seats),
                    lastSeat.posX + 1,
                    lastSeat.posY,
                    lastSeat.weight,
                    viewId,
                    lastSeat.rowBlock
                )
            };
        } else {
            return null;
        }
    }

    #generateItemNamesCreation(
        numItems: number,
        numericRange: boolean,
        numerationType: SeatMatrixConfNumerationType,
        numericStartsWith: number,
        alphabeticStartsWith: string
    ): string[] {
        if (numericRange) {
            const step = numerationType === SeatMatrixConfNumerationType.correlative ? 1 : 2;
            if (
                (numerationType === SeatMatrixConfNumerationType.even && numericStartsWith % 2 === 1)
                || (numerationType === SeatMatrixConfNumerationType.odd && numericStartsWith % 2 === 0)
            ) {
                numericStartsWith++;
            }
            return this.#generateNumericItemNames(numItems, numericStartsWith, step);
        } else {
            return this.#generateAlphabeticItemNames(numItems, alphabeticStartsWith);
        }
    }

    #generateItemNamesRowContinuation(lastSeat: EdSeat, prevLastSeat: EdSeat, numItems: number): string[] {
        if (lastSeat.name === String(Number(lastSeat.name))) {
            const step = prevLastSeat ? Number(lastSeat.name) - Number(prevLastSeat.name) : 1;
            const firstValue = Number(lastSeat.name) + step;
            return this.#generateNumericItemNames(numItems, firstValue, step);
        } else {
            return this.#generateAlphabeticItemNames(
                numItems,
                this.#seatMatrixSrv.getAlphabeticName(1, lastSeat.name.toUpperCase().charAt(0))
            );
        }
    }

    #generateNumericItemNames(numItems: number, firstValue: number, step: number): string[] {
        return [...Array(numItems).keys()].map(index => String(firstValue + index * step));
    }

    #generateAlphabeticItemNames(numItems: number, firstValue: string): string[] {
        return [...Array(numItems).keys()].map(index => this.#seatMatrixSrv.getAlphabeticName(index, firstValue));
    }

    #generateRowSeats(
        numSeats: number, initOrder: number, direction: SeatMatrixConfSeatDirection,
        seatNames: string[], posXOffset: number, posY: number, weight: number, view: number, rowBlock = '1'
    ): EdSeat[] {
        const seats: EdSeat[] = [...Array(numSeats).keys()]
            .map(index => ({
                itemType: VenueTemplateItemType.seat,
                id: undefined,
                name: seatNames[index],
                external: undefined,
                order: initOrder + index + 1,
                posX: posXOffset + index,
                posY,
                rowBlock,
                view,
                weight,
                create: true
            }));
        return direction === SeatMatrixConfSeatDirection.right ? seats : seats.reverse();
    }

    // VIEW CONFIGURATION

    #generateMatrixAppearanceConfiguration(
        rows: EdRow[], numSeats: number,
        seatsSize: number, seatsDistance: number, rowsDistance: number,
        showRows: boolean, rowsPosition: SeatMatrixConfRowLabelPosition
    ): SeatMatrixViewConfiguration {
        const viewConfiguration: SeatMatrixViewConfiguration = {
            rows,
            padding: seatsSize,
            rect: { width: 0, height: 0, offsetX: 0, offsetY: 0 },
            labelFontSize: seatsSize,
            leftLabelsWidth: 0,
            interactiveRowLabels: !this.#continueRowsMode,
            rightLabelsWidth: 0,
            labelDistance: 0,
            seatsSize,
            seatsDistance,
            rowsDistance,
            pos: null,
            angle: null,
            invScale: null
        };
        if (showRows) {
            const characterWidth = viewConfiguration.labelFontSize * 0.625;
            viewConfiguration.labelDistance = characterWidth / 2;
            const labelWidth = (Math.max(...rows.map(r => r.name.length)) * characterWidth) + viewConfiguration.labelDistance;
            if (rowsPosition !== SeatMatrixConfRowLabelPosition.right) {
                viewConfiguration.leftLabelsWidth = labelWidth;
            }
            if (rowsPosition !== SeatMatrixConfRowLabelPosition.left) {
                viewConfiguration.rightLabelsWidth = labelWidth;
            }
        }
        const rectWidth = viewConfiguration.padding * 2 // padding
            + viewConfiguration.leftLabelsWidth + viewConfiguration.rightLabelsWidth // labels width
            + (numSeats * seatsSize) + // seats size
            ((numSeats - 1) * seatsDistance); // seats distance
        const rectHeight = viewConfiguration.padding * 2 // padding
            + (rows.length * seatsSize) // seats size
            + ((rows.length - 1) * rowsDistance); // seats distance
        viewConfiguration.rect = { width: rectWidth, height: rectHeight, offsetX: -rectWidth / 2, offsetY: -rectHeight / 2 };
        return viewConfiguration;
    }

    #placeMatrixAppearanceConfiguration(
        viewConf: SeatMatrixViewConfiguration, pos: { x: number; y: number }, angle: number, scale: number
    ): SeatMatrixViewConfiguration {
        if (viewConf) {
            viewConf.pos = pos;
            viewConf.angle = angle;
            viewConf.invScale = 1 / scale;
        }
        return viewConf;
    }

    // COMMIT

    #commitConfiguration(rows: EdRow[]): void {
        this.#seatMatrixSrv.setTempIds(rows, () => IdGenerator.getTempId());
        this.#domSrv.getSvgSvgElement$()
            .pipe(debounceTime(0), take(1))
            .subscribe(mainSVG => {
                this.#selectionSrv.unselectAll();
                // this ungrouping can be optimized the same way in svg-transforms.component
                const svg = SVG(this._svg().nativeElement);
                const placerGroup = new G(this._placerGroup().nativeElement);
                const contentGroup = new G(this._contentGroup().nativeElement);
                const seatsGroup = new G(this._seatsGroup().nativeElement);
                placerGroup.ungroup(svg);
                contentGroup.ungroup(svg);
                seatsGroup.ungroup(svg);
                const newElements = svg.children()
                    .filter(child => child.classes().includes(SVGDefs.classes.interactive))
                    .map(child => {
                        this.#domSrv.optimizeSeatTransform(child, svg);
                        this.#domSrv.optimizeTransform(child.node);
                        const clone = child.clone(true, false).node;
                        this.#cleanAngularData(clone);
                        return clone;
                    });
                mainSVG.append(...newElements);
                this.#editorSrv.modes.setEditorMode(EditorMode.base);
                this.#seatMatrixSrv.endCommit();
                if (!this.#continueRowsMode) {
                    this.#editorSrv.history.enqueue(
                        new NewRowsAction(rows, this.#venueMapSrv, this.#viewsSrv, this.#domSrv, this.#selectionSrv, newElements)
                    );
                } else {
                    this.#editorSrv.history.enqueue(new NewSeatsAction(
                        rows.flatMap(row => row.seats), this.#venueMapSrv, this.#viewsSrv, this.#domSrv, this.#selectionSrv, newElements
                    ));
                }
            });
    }

    #cleanAngularData(element: Element): void {
        // Removes any attribute set by angular to manage de template, or by svgjs, to get a clean svg node.
        element.getAttributeNames().forEach(attributeName => {
            if (attributeName.at(0) === '_' || attributeName.includes('svgjs')) {
                element.removeAttribute(attributeName);
            }
        });
        // Removes all style classes and resets the interactive class if required
        if (element.classList.contains(SVGDefs.classes.interactive)) {
            element.removeAttribute(SVGDefs.attributes.class);
            element.classList.add(SVGDefs.classes.interactive);
        } else {
            element.removeAttribute(SVGDefs.attributes.class);
        }
        // recursive call on node children
        Array.from(element.children).forEach(child => this.#cleanAngularData(child));
    }

    #filterExistentRows(rowNames: string[], venueMap: EdVenueMap, viewId: number, sectorId: number): boolean {
        const valid = venueMap.sectors.find(sector => sector.id === sectorId)?.rows
            .filter(row => row.seats.at(0)?.view === viewId && !row.delete)
            .every(row => !rowNames.includes(row.name));

        if (!valid) {
            this.#seatMatrixSrv.endCommit();
            this.#msgDialogSrv.showAlert({
                size: DialogSize.SMALL,
                title: 'TITLES.ERROR_DIALOG',
                message: 'VENUE_TPL_EDITOR.FORMS.INFOS.ALERT_CREATE_SAME_ROWS'
            });
        }
        return valid;
    }
}
