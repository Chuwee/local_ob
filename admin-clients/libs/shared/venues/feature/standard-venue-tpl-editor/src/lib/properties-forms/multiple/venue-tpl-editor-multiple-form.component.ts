import { DialogSize, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { SharedUtilityDirectivesModule } from '@admin-clients/shared/utility/directives';
import { LocalNumberPipe } from '@admin-clients/shared/utility/pipes';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, map, switchMap, take, withLatestFrom } from 'rxjs/operators';
import { DeleteItemsAction } from '../../actions/delete-items-action';
import { SVGDefs } from '../../models/SVGDefs.enum';
import { CapacityEditionCapability } from '../../models/venue-tpl-editor-capacity-edition-capability';
import { EdNotNumberedZone, EdSeat } from '../../models/venue-tpl-editor-venue-map-items.model';
import { IdGenerator } from '../../utils/editor-id-generator.utils';
import { VenueTplEditorDomService } from '../../venue-tpl-editor-dom.service';
import { VenueTplEditorSelectionService } from '../../venue-tpl-editor-selection.service';
import { VenueTplEditorVenueMapService } from '../../venue-tpl-editor-venue-map.service';
import { VenueTplEditorViewsService } from '../../venue-tpl-editor-views.service';
import { VenueTplEditorService } from '../../venue-tpl-editor.service';
import { DeletableFormItem } from '../deletable-form-item';

@Component({
    imports: [
        CommonModule,
        MaterialModule,
        FlexLayoutModule,
        TranslatePipe,
        ReactiveFormsModule,
        SharedUtilityDirectivesModule,
        LocalNumberPipe
    ],
    selector: 'app-venue-tpl-editor-multiple-form',
    templateUrl: './venue-tpl-editor-multiple-form.component.html',
    styleUrls: ['../venue-tpl-editor-properties-forms-common-styles.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class VenueTplEditorMultipleFormComponent implements DeletableFormItem {
    private readonly _messageDialogSrv = inject(MessageDialogService);
    private readonly _editorSrv = inject(VenueTplEditorService);
    private readonly _selectionSrv = inject(VenueTplEditorSelectionService);
    private readonly _venueMapSrv = inject(VenueTplEditorVenueMapService);
    private readonly _viewsSrv = inject(VenueTplEditorViewsService);
    private readonly _domSrv = inject(VenueTplEditorDomService);

    readonly shapeItems$ = this._selectionSrv.getSelection$()
        .pipe(
            map(selection => selection.elements
                .filter(el => !el.classList.contains(SVGDefs.classes.interactive) || el.tagName === SVGDefs.nodeTypes.rowLabel)
            ),
            map(result => result?.length && result || null)
        );

    readonly links$ = this._selectionSrv.getSelection$()
        .pipe(
            map(selection => selection.elements
                .filter(el => el.tagName === SVGDefs.nodeTypes.link && el.classList.contains(SVGDefs.classes.interactive))
            ),
            map(result => result?.length && result || null)
        );

    readonly seats$ = this._selectionSrv.getSelection$()
        .pipe(
            map(selection => Array.from(selection.seats)),
            map(result => result?.length && result || null)
        );

    readonly notNumberedZones$ = this._selectionSrv.getSelection$()
        .pipe(
            map(selection => Array.from(selection.nnzs)),
            map(result => result?.length && result || null)
        );

    deleteFormItem(): void {
        this._selectionSrv.getSelection$()
            .pipe(
                take(1),
                switchMap(selection =>
                    this._editorSrv.getCapacityEditionCapability$()
                        .pipe(
                            withLatestFrom(this._venueMapSrv.getVenueItems$()),
                            map(([editionCapability, venueItems]) => selection)
                        )
                ),
                withLatestFrom(this._venueMapSrv.getVenueItems$(), this._editorSrv.getCapacityEditionCapability$()),
                filter(([selection, venueItems, editionCapability]) =>
                    this.canDelete(
                        Array.from(selection.seats.values()).map(seatId => venueItems.seats.get(seatId)),
                        Array.from(selection.nnzs.values()).map(nnzId => venueItems.nnzs.get(nnzId)),
                        editionCapability
                    )
                ),
                switchMap(() =>
                    this._messageDialogSrv.showWarn({
                        size: DialogSize.SMALL,
                        title: 'VENUE_TPL_EDITOR.DELETE_ITEMS_WARNING_TITLE',
                        message: 'VENUE_TPL_EDITOR.DELETE_ITEMS_WARNING',
                        actionLabel: 'FORMS.ACTIONS.DELETE'
                    })
                ),
                filter(Boolean),
                switchMap(() => this._selectionSrv.getSelection$()),
                take(1)
            )
            .subscribe(selection => {
                const elements = selection.elements.concat();
                const seatIds = Array.from(selection.seats);
                const nnzIds = Array.from(selection.nnzs);
                this._editorSrv.history.enqueue(
                    new DeleteItemsAction(
                        { seatIds, nnzIds, elements }, this._venueMapSrv, this._viewsSrv, this._domSrv, this._selectionSrv
                    )
                );
            });
    }

    private canDelete(seats: EdSeat[], nnzList: EdNotNumberedZone[], editionCapability: CapacityEditionCapability): boolean {
        let result: boolean;
        if (editionCapability === CapacityEditionCapability.increase) {
            result = seats.every(seat => IdGenerator.isTempId(seat.id))
                && nnzList.every(nnz => IdGenerator.isTempId(nnz.id));
        } else {
            result = editionCapability === CapacityEditionCapability.total;
        }
        if (!result) {
            this._messageDialogSrv
                .showAlert({
                    size: DialogSize.SMALL,
                    title: 'VENUE_TPL_EDITOR.CAPACITY_DECREMENT_NOT_AVAILABLE_BY_SESSIONS_ERROR_TITLE',
                    message: 'VENUE_TPL_EDITOR.CAPACITY_DECREMENT_NOT_AVAILABLE_BY_SESSIONS_ERROR'
                });
        }
        return result;
    }
}
