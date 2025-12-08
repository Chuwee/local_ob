import { DialogSize, MessageDialogService, ObDialog, SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnInit, ViewChild } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest } from 'rxjs';
import { filter, map, take } from 'rxjs/operators';
import { NewLinkAction } from '../../actions/new-link-action';
import { VenueTplEditorViewData } from '../../models/venue-tpl-editor-view-data.model';
import { VenueTplEditorDomService } from '../../venue-tpl-editor-dom.service';
import { VenueTplEditorSelectionService } from '../../venue-tpl-editor-selection.service';
import { VenueTplEditorViewsService } from '../../venue-tpl-editor-views.service';
import { VenueTplEditorService } from '../../venue-tpl-editor.service';

@Component({
    imports: [
        CommonModule,
        MaterialModule,
        FlexLayoutModule,
        TranslatePipe,
        ReactiveFormsModule,
        SelectSearchComponent
    ],
    selector: 'app-venue-tpl-editor-link-dialog',
    templateUrl: './venue-tpl-editor-link-dialog.component.html',
    styleUrls: ['./venue-tpl-editor-link-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class VenueTplEditorLinkDialogComponent extends ObDialog<VenueTplEditorLinkDialogComponent, null, null> implements OnInit {

    private readonly _fb = inject(FormBuilder);
    private readonly _editorSrv = inject(VenueTplEditorService);
    private readonly _selectionSrv = inject(VenueTplEditorSelectionService);
    private readonly _viewsSrv = inject(VenueTplEditorViewsService);
    private readonly _domSrv = inject(VenueTplEditorDomService);
    private readonly _msgSrv = inject(MessageDialogService);

    @ViewChild('viewSelectSearch')
    readonly viewSelectSearchComponent: SelectSearchComponent<VenueTplEditorViewData>;

    readonly form = this._fb.group({
        viewData: [null as VenueTplEditorViewData, [Validators.required]]
    });

    readonly views$ = combineLatest([
        this._viewsSrv.getViewDatas$(),
        this._viewsSrv.getViewData$()
    ])
        .pipe(
            filter(Boolean),
            map(([viewDatas, viewData]) => {
                const linkViewIds = new Set(
                    viewDatas
                        .filter(vd => !vd.delete)
                        .flatMap(vd => vd.links)
                        .filter(link => !link.delete)
                        .map(vd => vd.view_id)
                );
                return viewDatas.filter(vd => !vd.view.root && vd !== viewData && !linkViewIds.has(vd.view.id));
            })
        );

    constructor() {
        super(DialogSize.MEDIUM);
    }

    ngOnInit(): void {
        this.views$
            .pipe(take(1))
            .subscribe(views => {
                if (views.length === 1) {
                    this.form.controls.viewData.setValue(views[0]);
                } else if (!views.length) {
                    this.dialogRef.close();
                    this._msgSrv.showAlert({
                        size: DialogSize.SMALL,
                        message: 'VENUE_TPL_EDITOR.UNLINKED_VIEWS_UNAVAILABLE'
                    });
                }
            });
    }

    commit(): void {
        this.dialogRef.close();
        this._editorSrv.history.enqueue(
            new NewLinkAction(this.form.value.viewData.view.id, this._viewsSrv, this._domSrv, this._selectionSrv)
        );
    }
}
