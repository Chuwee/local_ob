import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { DialogSize, ObDialog, SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { VenueTemplateFieldsRestrictions } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnInit, ViewChild } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { map, take, withLatestFrom } from 'rxjs/operators';
import { NewNnzAction } from '../../actions/new-nnz-action';
import { EdSector } from '../../models/venue-tpl-editor-venue-map-items.model';
import { IdGenerator } from '../../utils/editor-id-generator.utils';
import { VenueTplEditorDomService } from '../../venue-tpl-editor-dom.service';
import { VenueTplEditorSelectionService } from '../../venue-tpl-editor-selection.service';
import { VenueTplEditorVenueMapService } from '../../venue-tpl-editor-venue-map.service';
import { VenueTplEditorViewsService } from '../../venue-tpl-editor-views.service';
import { VenueTplEditorService } from '../../venue-tpl-editor.service';

@Component({
    imports: [
        CommonModule,
        MaterialModule,
        FlexLayoutModule,
        TranslatePipe,
        ReactiveFormsModule,
        FormControlErrorsComponent,
        SelectSearchComponent
    ],
    selector: 'app-venue-tpl-editor-nnz-dialog',
    templateUrl: './venue-tpl-editor-nnz-dialog.component.html',
    styleUrls: ['./venue-tpl-editor-nnz-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class VenueTplEditorNnzDialogComponent extends ObDialog<VenueTplEditorNnzDialogComponent, null, null> implements OnInit {

    private readonly _fb = inject(FormBuilder);
    private readonly _editorSrv = inject(VenueTplEditorService);
    private readonly _selectionSrv = inject(VenueTplEditorSelectionService);
    private readonly _venueMapSrv = inject(VenueTplEditorVenueMapService);
    private readonly _viewsSrv = inject(VenueTplEditorViewsService);
    private readonly _domSrv = inject(VenueTplEditorDomService);

    @ViewChild('sectorsSelectSearch')
    readonly sectorSelectSearchComponent: SelectSearchComponent<EdSector>;

    readonly sectors$ = this._venueMapSrv.getVenueMap$().pipe(map(venueMap => venueMap.sectors.filter(sector => !sector.delete)));

    readonly form = this._fb.group({
        name: [null as string, [Validators.required, Validators.maxLength(VenueTemplateFieldsRestrictions.zoneNameLength)]],
        sector: [null as number, [Validators.required]],
        capacity: [50, [Validators.required, Validators.min(1), Validators.max(200000)]] // 200000 por poner algo
    });

    constructor() {
        super(DialogSize.MEDIUM);
    }

    ngOnInit(): void {
        this._viewsSrv.getViewData$()
            .pipe(
                take(1),
                withLatestFrom(this._venueMapSrv.getVenueMap$())
            )
            .subscribe(([viewData, venueMap]) => {
                this.form.controls.sector.setValue(
                    venueMap.sectors.find(sector =>
                        !!(
                            sector.rows.find(row => !!row.seats.find(seat => seat.view === viewData.view.id))
                            || sector.notNumberedZones.find(nnz => nnz.view === viewData.view.id)
                        )
                    )?.id || null
                );
            });
    }

    commit(): void {
        this.dialogRef.close();
        this._editorSrv.history.enqueue(new NewNnzAction(
            {
                id: IdGenerator.getTempId(),
                ...this.form.getRawValue()
            },
            this._venueMapSrv,
            this._viewsSrv,
            this._domSrv,
            this._selectionSrv
        ));
    }
}
