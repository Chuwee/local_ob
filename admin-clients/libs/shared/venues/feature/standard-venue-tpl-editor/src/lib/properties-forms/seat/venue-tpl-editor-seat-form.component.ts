import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { DialogSize, MessageDialogService, ObDialogService } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { SharedUtilityDirectivesModule } from '@admin-clients/shared/utility/directives';
import { noDuplicateValuesAsyncValidator } from '@admin-clients/shared/utility/utils';
import { VenueTemplateFieldsRestrictions } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit, ViewContainerRef } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, Subject, switchMap } from 'rxjs';
import { debounceTime, filter, map, take, takeUntil, withLatestFrom } from 'rxjs/operators';
import { DeleteItemsAction } from '../../actions/delete-items-action';
import { EditSeatAction } from '../../actions/edit-seat-action';
import { VenueTplEditorSvgEditDialogComponent } from '../../dialogs/svg-code/venue-tpl-editor-svg-edit-dialog.component';
import { CapacityEditionCapability } from '../../models/venue-tpl-editor-capacity-edition-capability';
import { EdSeat } from '../../models/venue-tpl-editor-venue-map-items.model';
import { IdGenerator } from '../../utils/editor-id-generator.utils';
import { maxWeightValue } from '../../utils/seat-weights.utils';
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
        FormControlErrorsComponent,
        SharedUtilityDirectivesModule
    ],
    selector: 'app-venue-tpl-editor-seat-form',
    templateUrl: './venue-tpl-editor-seat-form.component.html',
    styleUrls: ['../venue-tpl-editor-properties-forms-common-styles.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class VenueTplEditorSeatFormComponent implements OnInit, OnDestroy, DeletableFormItem {
    private readonly _onDestroy = new Subject<void>();
    private readonly _fb = inject(FormBuilder);
    private readonly _editorSrv = inject(VenueTplEditorService);
    private readonly _selectionSrv = inject(VenueTplEditorSelectionService);
    private readonly _venueMapSrv = inject(VenueTplEditorVenueMapService);
    private readonly _viewSrv = inject(VenueTplEditorViewsService);
    private readonly _domSrv = inject(VenueTplEditorDomService);
    private readonly _dialogSrv = inject(ObDialogService);
    private readonly _messageDialogSrv = inject(MessageDialogService);
    private readonly _viewContainerRef = inject(ViewContainerRef);

    readonly operatorMode$ = this._editorSrv.modes.getOperatorMode$();

    readonly seat$ = combineLatest([
        this._selectionSrv.getSelection$(),
        this._venueMapSrv.getVenueItems$()
    ])
        .pipe(map(([selection, venueItems]) => venueItems.seats.get(Array.from(selection.seats.values())[0])));

    readonly row$ = this.seat$.pipe(
        withLatestFrom(this._venueMapSrv.getVenueMap$()),
        map(([seat, venueMap]) => venueMap.sectors.flatMap(sector => sector.rows).find(row => row.seats.includes(seat)))
    );

    readonly sector$ = this.row$.pipe(
        withLatestFrom(this._venueMapSrv.getVenueMap$()),
        map(([row, venueMap]) => venueMap.sectors.find(sector => sector.rows.includes(row)))
    );

    readonly form = this._fb.group({
        name: ['',
            [Validators.required, Validators.maxLength(VenueTemplateFieldsRestrictions.seatNameLength)],
            [
                noDuplicateValuesAsyncValidator(this.row$.pipe(
                    withLatestFrom(this.seat$),
                    map(([row, selectedSeat]) => row
                        ?.seats
                        ?.filter(seat => !seat.delete && seat.id !== selectedSeat.id)
                        .map(seat => seat.name)
                    )
                ))
            ]
        ],
        weight: [0]
    });

    ngOnInit(): void {
        this.seat$
            .pipe(
                filter(Boolean),
                takeUntil(this._onDestroy)
            )
            .subscribe(seat => {
                this.form.patchValue({
                    name: seat.name,
                    weight: seat.weight
                }, { emitEvent: false });
                this.form.markAllAsTouched();
            });
        this.form.controls.weight.valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(weight => {
                const fixedWeight = Math.min(Math.max(weight, 0), maxWeightValue);
                if (weight !== fixedWeight) {
                    this.form.controls.weight.setValue(fixedWeight, { emitEvent: false });
                }
            });
        this.form.valueChanges
            .pipe(
                debounceTime(100),
                withLatestFrom(this.seat$),
                takeUntil(this._onDestroy)
            )
            .subscribe(([value, seat]) =>
                this._editorSrv.history.enqueue(new EditSeatAction({ id: seat.id, ...value }, this._venueMapSrv))
            );

    }

    ngOnDestroy(): void {
        this._onDestroy.next();
        this._onDestroy.complete();
    }

    openEditSvgDialog(): void {
        this.seat$.pipe(take(1))
            .subscribe(seat => this._dialogSrv.open(VenueTplEditorSvgEditDialogComponent, { target: seat }, this._viewContainerRef));
    }

    deleteFormItem(): void {
        this.seat$
            .pipe(
                withLatestFrom(this._editorSrv.getCapacityEditionCapability$()),
                take(1),
                filter(([seat, editionCapability]) => this.canDelete(seat, editionCapability)),
                switchMap(([seat]) =>
                    this._messageDialogSrv.showWarn({
                        size: DialogSize.SMALL,
                        title: 'TITLES.ALERT',
                        message: 'VENUE_TPL_EDITOR.DELETE_SEAT_WARNING',
                        messageParams: { seatName: seat.name }
                    })
                ),
                filter(Boolean),
                switchMap(() => this.seat$),
                take(1)
            )
            .subscribe(seat => {
                this._editorSrv.history.enqueue(
                    new DeleteItemsAction({ seatIds: [seat.id] }, this._venueMapSrv, this._viewSrv, this._domSrv, this._selectionSrv)
                );
            });
    }

    private canDelete(seat: EdSeat, editionCapability: CapacityEditionCapability): boolean {
        const result = editionCapability === CapacityEditionCapability.increase ? IdGenerator.isTempId(seat.id) :
            editionCapability === CapacityEditionCapability.total;
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
