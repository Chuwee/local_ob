import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { VenueTemplateItemType } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, HostListener, inject, ViewChild } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { combineLatest } from 'rxjs';
import { map } from 'rxjs/operators';
import { SVGDefs } from '../models/SVGDefs.enum';
import { VenueTplEditorForms } from '../models/venue-tpl-editor-forms.enum';
import { EditorMode } from '../models/venue-tpl-editor-modes.enum';
import { EdNotNumberedZone, EdSeat } from '../models/venue-tpl-editor-venue-map-items.model';
import { VenueTplEditorSelectionService } from '../venue-tpl-editor-selection.service';
import { VenueTplEditorVenueMapService } from '../venue-tpl-editor-venue-map.service';
import { VenueTplEditorService } from '../venue-tpl-editor.service';
import { VenueTplEditorBlocksSetupFormComponent } from './blocks-setup/venue-tpl-editor-blocks-setup-form.component';
import { DeletableFormItem } from './deletable-form-item';
import { VenueTplEditorLinkFormComponent } from './link/venue-tpl-editor-link-form.component';
import { VenueTplEditorMultipleFormComponent } from './multiple/venue-tpl-editor-multiple-form.component';
import { VenueTplEditorNnzFormComponent } from './nnz/venue-tpl-editor-nnz-form.component';
import { VenueTplEditorSeatFormComponent } from './seat/venue-tpl-editor-seat-form.component';
import { VenueTplEditorSeatMatrixFormComponent } from './seat-matrix/venue-tpl-editor-seat-matrix-form.component';
import { VenueTplEditorShapeFormComponent } from './shape/venue-tpl-editor-shape-form.component';
import { VenueTplEditorViewFormComponent } from './view/venue-tpl-editor-view-form.component';
import { VenueTplEditorWeightsSetupFormComponent } from './weights-setup/venue-tpl-editor-weights-setup-form.component';

@Component({
    imports: [
        CommonModule,
        MaterialModule,
        FlexLayoutModule,
        VenueTplEditorViewFormComponent,
        VenueTplEditorNnzFormComponent,
        VenueTplEditorSeatFormComponent,
        VenueTplEditorLinkFormComponent,
        VenueTplEditorShapeFormComponent,
        VenueTplEditorMultipleFormComponent,
        VenueTplEditorSeatMatrixFormComponent,
        VenueTplEditorBlocksSetupFormComponent,
        VenueTplEditorWeightsSetupFormComponent
    ],
    selector: 'app-venue-tpl-editor-properties-forms',
    templateUrl: './venue-tpl-editor-properties-forms.component.html',
    styleUrls: ['./venue-tpl-editor-properties-forms.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class VenueTplEditorPropertiesFormsComponent {

    private readonly _editorSrv = inject(VenueTplEditorService);
    private readonly _selectionSrv = inject(VenueTplEditorSelectionService);
    private readonly _venueMapSrv = inject(VenueTplEditorVenueMapService);

    private readonly _selectedItems$ = combineLatest([
        this._venueMapSrv.getVenueItems$(),
        this._selectionSrv.getSelection$()
    ])
        .pipe(
            map(([venueItems, selection]) => {
                const result: (EdNotNumberedZone | EdSeat | SVGElement)[] = [];
                result.push(...selection.elements);
                result.push(...Array.from(selection.seats.keys()).map(seatId => venueItems.seats.get(seatId)));
                result.push(...Array.from(selection.nnzs.keys()).map(nnzId => venueItems.nnzs.get(nnzId)));
                return result;
            })
        );

    @ViewChild('currentForm')
    readonly currentForm: DeletableFormItem;

    readonly editorForms = VenueTplEditorForms;

    readonly selectedForm$ = combineLatest([
        this._selectedItems$,
        this._editorSrv.modes.getEditorMode$()
    ]).pipe(map(([selectedItems, mode]) => {
        if (mode === EditorMode.base) {
            if (!(selectedItems?.length > 0)) {
                return VenueTplEditorForms.view;
            } else {
                if (selectedItems.length === 1) {
                    const item = selectedItems[0];
                    if (item) {
                        if (item instanceof SVGElement) {
                            if (item instanceof SVGGElement && item.id && item.classList.contains(SVGDefs.classes.interactive)) {
                                return VenueTplEditorForms.link;
                            } else {
                                return VenueTplEditorForms.shape;
                            }
                        } else if (item.itemType === VenueTemplateItemType.seat) {
                            return VenueTplEditorForms.seat;
                        } else if (item.itemType === VenueTemplateItemType.notNumberedZone) {
                            return VenueTplEditorForms.nnz;
                        }
                    }
                } else {
                    if (selectedItems.every(
                        item => item instanceof SVGElement && !item.id && !item.classList.contains(SVGDefs.classes.interactive)
                    )) {
                        return VenueTplEditorForms.shape;
                    } else {
                        return VenueTplEditorForms.multiple;
                    }
                }
                return null;
            }
        } else if (mode === EditorMode.blocksSetup) {
            return VenueTplEditorForms.blocksSetup;
        } else if (mode === EditorMode.seatMatrixCreate) {
            return VenueTplEditorForms.newSeatMatrix;
        } else if (mode === EditorMode.seatMatrixIncrease) {
            return VenueTplEditorForms.newSeatMatrix;
        } else if (mode === EditorMode.weightsSetup) {
            return VenueTplEditorForms.weightsSetup;
        }
    }));

    @HostListener('window:keydown.delete', ['$event'])
    @HostListener('window:keydown.Backspace', ['$event'])
    private deleteKeyDown(event: KeyboardEvent): void {
        if (
            event.target instanceof HTMLBodyElement // skips key press on input elements
            && this.currentForm && this.currentForm.deleteFormItem instanceof Function // checks current form has "delete functionality"
        ) {
            this.currentForm.deleteFormItem();
        }
    }
}
