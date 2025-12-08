/* eslint-disable @typescript-eslint/naming-convention */
import { StateProperty } from '@OneboxTM/utils-state';
import { Injectable } from '@angular/core';
import { Box } from '@svgdotjs/svg.js';
import { VenueTplEditorActionHistory, VenueTplEditorActionStatus } from '../models/actions/venue-tpl-editor-base-action';
import { BlockChange } from '../models/venue-tpl-editor-blocks.model';
import { VenueTplEditorImage } from '../models/venue-tpl-editor-image.model';
import { EditorMode, InteractionMode, VisualizationMode } from '../models/venue-tpl-editor-modes.enum';
import { venueTplEditorSeatMatrixConfInitValue } from '../models/venue-tpl-editor-seat-matrix-conf.model';
import { VenueTplEditorSelection } from '../models/venue-tpl-editor-selection.model';
import { VenueTplEditorSvgCoordinates } from '../models/venue-tpl-editor-svg-coordinates.model';
import { VenueTplSvgData } from '../models/venue-tpl-editor-svg-data.model';
import { EdSeat, EdVenueMap, EdVenueMapMaps } from '../models/venue-tpl-editor-venue-map-items.model';
import { VenueTplEditorViewData } from '../models/venue-tpl-editor-view-data.model';
import { VenueTplEditorWeightsConfiguration } from '../models/venue-tpl-editor-weights-configuration';

@Injectable()
export class VenueTplEditorState {

    // editor service
    readonly operatorMode = new StateProperty(false);
    readonly mmcIntegrationEnabled = new StateProperty(false); // only promoter templates
    readonly history = new StateProperty<VenueTplEditorActionHistory>({ undoActions: [], redoActions: [] });
    readonly actionErrors = new StateProperty<VenueTplEditorActionStatus>();
    readonly mode = new StateProperty(EditorMode.base);
    readonly visualizationModes = new StateProperty<VisualizationMode[]>([]);
    readonly interactionMode = new StateProperty(InteractionMode.all);

    // venue map service
    readonly venueMap = new StateProperty<EdVenueMap>();
    readonly venueItems = new StateProperty<EdVenueMapMaps>();

    // views service
    readonly workAreaCoordinates = new StateProperty<VenueTplEditorSvgCoordinates>();
    readonly viewDatas = new StateProperty<VenueTplEditorViewData[]>();
    readonly viewData = new StateProperty<VenueTplEditorViewData>();
    readonly SVGDatas = new StateProperty<VenueTplSvgData[]>([]);
    readonly SVGData = new StateProperty<VenueTplSvgData>();
    readonly svgSvgElement = new StateProperty<SVGSVGElement>();

    // images
    readonly images = new StateProperty<VenueTplEditorImage[]>();

    // selection service
    readonly selection = new StateProperty<VenueTplEditorSelection>({
        seats: new Set(),
        nnzs: new Set(),
        elements: []
    });

    // Seat matrix service
    readonly seatMatrixConf = new StateProperty(venueTplEditorSeatMatrixConfInitValue);

    // blocks setup service
    readonly blocksSetupViewSeats = new StateProperty<Map<number, EdSeat>>();
    readonly blocksSetupSelection = new StateProperty<BlockChange[]>();

    // weights setup service
    readonly weightsSetupViewSeats = new StateProperty<Map<number, EdSeat>>();
    readonly weightsSetupViewSeatsBoxes = new StateProperty<Map<number, Box>>();
    readonly weightsConfiguration = new StateProperty<VenueTplEditorWeightsConfiguration>();

    // save service
    readonly save = new StateProperty();

    // event templates
    readonly inUse = new StateProperty<boolean>();
    readonly capacityIncreaseEnabled = new StateProperty<boolean>();
    readonly inCapacityIncrease = new StateProperty<boolean>();
    readonly increasingCapacity = new StateProperty<boolean>();
}
