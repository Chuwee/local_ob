import { StateProperty } from '@OneboxTM/utils-state';
import { ObFile } from '@admin-clients/shared/data-access/models';
import { BaseStateProp } from '@admin-clients/shared/utility/state';
import { VmItemsMap, VmItemsSet } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { VenueTemplateLabel, VenueTemplateLabelGroup } from '../models/label-group/venue-template-label-group-list.model';
import { RelocationStatus } from '../models/rellocation-status.model';
import { VenueTemplateEditorState } from '../models/venue-template-editor-state.enum';
import { VenueTemplateEditorView } from '../models/venue-template-editor-view.enum';
import { VenueTemplateOriginOrderItem } from '../models/venue-template-origin-order-item';
import { VenueTemplateSelectionChange } from '../models/venue-template-selection-change.model';

@Injectable()
export class StandardVenueTemplateState {
    // STATE
    private readonly _currentState = new BaseStateProp<VenueTemplateEditorState>(VenueTemplateEditorState.main);
    readonly getCurrentState$ = this._currentState.getValueFunction();
    readonly setCurrentState = this._currentState.setValueFunction();
    // CURRENT VIEW
    private readonly _currentView = new BaseStateProp<VenueTemplateEditorView>(VenueTemplateEditorView.graphic);
    readonly getCurrentView$ = this._currentView.getValueFunction();
    readonly setCurrentView = this._currentView.setValueFunction();
    // LABEL GROUPS
    private readonly _labelGroups = new BaseStateProp<VenueTemplateLabelGroup[]>();
    readonly getLabelGroups$ = this._labelGroups.getValueFunction();
    readonly setLabelGroups = this._labelGroups.setValueFunction();
    private readonly _selectedLabelGroup = new BaseStateProp<VenueTemplateLabelGroup>();
    readonly getSelectedLabelGroup$ = this._selectedLabelGroup.getValueFunction();
    readonly setSelectedLabelGroup = this._selectedLabelGroup.setValueFunction();
    // FILTERED LABELS
    private readonly _filteredLabels = new BaseStateProp<VenueTemplateLabel[]>();
    readonly getFilteredLabels$ = this._filteredLabels.getValueFunction();
    readonly setFilteredLabels = this._filteredLabels.setValueFunction();
    // VENUE ITEMS, SEATS AND NOT NUMBERED ZONES
    private readonly _venueItems = new BaseStateProp<VmItemsMap>();
    readonly getVenueItems$ = this._venueItems.getValueFunction();
    readonly setVenueItems = this._venueItems.setValueFunction();
    // FILTERED VENUE ITEMS (excluded from view)
    private readonly _filteredVenueItems = new BaseStateProp<VmItemsSet>();
    readonly getFilteredVenueItems$ = this._filteredVenueItems.getValueFunction();
    readonly setFilteredVenueItems = this._filteredVenueItems.setValueFunction();
    // SELECTION, this one never gets a new value
    private readonly _selectedVenueItems = new BaseStateProp<VmItemsSet>({ seats: new Set<number>(), nnzs: new Set<number>() });
    readonly getSelectedVenueItems$ = this._selectedVenueItems.getValueFunction();
    private readonly _selectionQueue = new Subject<VenueTemplateSelectionChange>();
    private readonly _selectionQueue$ = this._selectionQueue.asObservable();
    // MODIFICATIONS
    private readonly _modifiedItems = new BaseStateProp<VmItemsSet>({
        seats: new Set<number>(),
        nnzs: new Set<number>(),
        sectors: new Set<number>()
    });

    private readonly _setModifiedItems = this._modifiedItems.setValueFunction();
    readonly getModifiedItems$ = this._modifiedItems.getValueFunction();
    // TEMPLATE IMAGE
    private readonly _templateImage = new BaseStateProp<ObFile | string>();
    readonly getTemplateImage$ = this._templateImage.getValueFunction();
    readonly setTemplateImage = this._templateImage.setValueFunction();

    // RELOCATION
    readonly relocationStatus = new StateProperty<RelocationStatus>();
    readonly selectedOriginSeatIds = new StateProperty<number[]>();
    readonly selectedOriginOrderItems = new StateProperty<VenueTemplateOriginOrderItem[]>();
    readonly selectedDestinationSeats = new StateProperty<{ [originId: number]: number }>();

    getSelectionQueue$(): Observable<VenueTemplateSelectionChange> {
        return this._selectionQueue$;
    }

    enqueueSelection(selectionChange: VenueTemplateSelectionChange): void {
        this._selectionQueue.next(selectionChange);
    }

    setModifiedItems(
        modifiedItems: { seats?: Set<number>; nnzs?: Set<number>; sectors?: Set<number> } = null
    ): void {
        this._setModifiedItems({
            seats: modifiedItems?.seats ?? new Set<number>(),
            nnzs: modifiedItems?.nnzs ?? new Set<number>(),
            sectors: modifiedItems?.sectors ?? new Set<number>()
        });
    }
}
